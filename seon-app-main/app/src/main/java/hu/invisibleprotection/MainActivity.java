package hu.invisibleprotection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import hu.invisibleprotection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";


    public static void schedulePeriodicWork(Context ctx, String timeVal){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());

        int time = Integer.parseInt(preferences.getString("timing", "0"));
        if (timeVal.length() > 0){
            time = Integer.parseInt(timeVal);
        }
        if (time <= 0){
            WorkManager.getInstance(ctx).cancelUniqueWork("IPLOC");
            Log.d(TAG, "Turned off work scheduling");
        }else{
            Log.d(TAG, "MinInterval:" + PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS);
            if (time*60_000 < PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS){
                time = (int) (PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS/60_000);
            }
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest iplocscheduled =
                    new PeriodicWorkRequest.Builder(SendLocationWorker.class, time, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .addTag("IPLOC-tag")
                            .build();

            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                    "IPLOC",
                    ExistingPeriodicWorkPolicy.KEEP,
                    iplocscheduled);
            Log.d(TAG, "Scheduled work for " + time + "minutes");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schedulePeriodicWork(this, "");


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }

}