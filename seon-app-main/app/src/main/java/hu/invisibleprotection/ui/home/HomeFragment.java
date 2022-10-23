package hu.invisibleprotection.ui.home;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import hu.invisibleprotection.MainActivity;
import hu.invisibleprotection.SendLocationWorker;
import hu.invisibleprotection.databinding.FragmentHomeBinding;
import hu.invisibleprotection.ui.MotivationObject;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";


    private FragmentHomeBinding binding;

    SharedPreferences.OnSharedPreferenceChangeListener listener;

    private static final String permissions[] = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    void requestPermissions() {
        List<String> toRequest = Arrays.stream(permissions).filter(perm ->
                ActivityCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED
        ).collect(Collectors.toList());
        if (toRequest.size() > 0) {
            String[] filtered = new String[toRequest.size()];
            filtered = toRequest.toArray(filtered);
            ActivityCompat.requestPermissions(getActivity(),
                    filtered,
                    43);
        }
    }

    void getMotivationalStats() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                        .build();

                String baseURL = "https://seon-ipmapper.herokuapp.com/feedback";

                Request request = new Request.Builder()
                        .url(baseURL)
                        .get()
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String rsp = response.body().string();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Received Motivation data");
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
                        SharedPreferences.Editor ed = preferences.edit();
                        Log.d(TAG, "Data: " + rsp);
                        Gson gson = new Gson();
                        MotivationObject data = gson.fromJson(rsp, MotivationObject.class);
                        ed.putString("motivation1", data.text1);
                        ed.putString("motivation2", data.text2);
                        ed.putString("motivation3", data.text3);
                        ed.commit();
                        ed.putLong("lastmotivupdate", System.currentTimeMillis());
                        ed.commit();
                    } else {
                        Log.e(TAG, "Send failed");
                    }
                } catch (IOException | ClassCastException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runIntroAnimation();
                        }
                    });
                }
            }
        }).start();
    }

    private void runIntroAnimation() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        binding.text1.setText(preferences.getString("motivation1", "Fight criminals"));
        binding.text2.setText(preferences.getString("motivation2", "Prevent fraud"));
        binding.text3.setText(preferences.getString("motivation3", "Keep accounts safe"));


        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(600);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);


        binding.card1.setAlpha(0);
        binding.card2.setAlpha(0);
        binding.card3.setAlpha(0);

        binding.youhelpedCard.setAlpha(1);
        binding.youhelpedCard.startAnimation(fadeIn);



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(binding == null || binding.card1 == null) return;
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(800);
                binding.card1.setAlpha(1);
                binding.card1.startAnimation(fadeIn);
            }
        }, 500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(binding == null || binding.card2 == null) return;
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(800);
                binding.card2.setAlpha(1);
                binding.card2.startAnimation(fadeIn);
            }
        }, 800);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(binding == null || binding.card3 == null) return;
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(800);
                binding.card3.setAlpha(1);
                binding.card3.startAnimation(fadeIn);
            }
        }, 1100);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.youhelpedCard.setAlpha(0);

        getMotivationalStats();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());


        int count = Integer.parseInt(preferences.getString("timing", "0"));
        String lastSent = preferences.getString("lastsent", "");
        String lastip = preferences.getString("lastip", "");
        String msg = "Periodic IPLOC sending is turned off\nYou can turn it on in the Settings tab";
        if (count > 0){
            msg = "Sending IPLOC every ";
            if (count%60 == 0){
                msg += (int)count/60 + " hours";
            }else{
                msg += count + " minutes";
            }
        }
        if (count > 0 && lastSent.length() > 0){
            binding.scheduleinfoextra.setText("Last sent: " + lastSent + "\nYour latest IP: " + lastip);
            binding.scheduleinfoextra.setVisibility(View.VISIBLE);
        }else{
            binding.scheduleinfoextra.setVisibility(View.INVISIBLE);
        }
        binding.scheduletext.setText(msg);
        Animation fadeIn2 = new AlphaAnimation(0, 1);
        fadeIn2.setInterpolator(new DecelerateInterpolator());
        fadeIn2.setDuration(600);
        binding.schedulecard.startAnimation(fadeIn2);

        Animation a = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        a.setInterpolator(new AccelerateInterpolator());
        a.setDuration(250);
        binding.totalsendcard.startAnimation(a);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        count = preferences.getInt("totalsent", 0);
        binding.totalsendtext.setText("By sending " + count + " IPLOC infos");
        if (listener != null)
            PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).unregisterOnSharedPreferenceChangeListener(listener);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if (s.equals("totalsent")) {
                    Animation a = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    a.setInterpolator(new AccelerateInterpolator());
                    a.setDuration(250);
                    int count = sharedPreferences.getInt("totalsent", 0);
                    binding.totalsendtext.setText("By sending " + count + " IPLOC infos");
                    binding.totalsendcard.startAnimation(a);
                } else if (s.equals("lastmotivupdate")) {
                    runIntroAnimation();
                } else if(s.equals("lastsent")){
                    String lastSent = sharedPreferences.getString("lastsent", "");
                    String lastip = sharedPreferences.getString("lastip", "");
                    binding.scheduleinfoextra.setText("Last sent: " + lastSent + "\nYour latest IP: " + lastip);
                    binding.scheduleinfoextra.setVisibility(View.VISIBLE);
                } else if(s.equals("lastip")){
                    String lastSent = sharedPreferences.getString("lastsent", "");
                    String lastip = sharedPreferences.getString("lastip", "");
                    binding.scheduleinfoextra.setText("Last sent: " + lastSent + "\nYour latest IP: " + lastip);
                    binding.scheduleinfoextra.setVisibility(View.VISIBLE);
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).registerOnSharedPreferenceChangeListener(listener);


        final Button manualStart = binding.iplocSend;
        manualStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkRequest uploadWorkRequest =
                        new OneTimeWorkRequest.Builder(SendLocationWorker.class)
                                .build();
                WorkManager
                        .getInstance(getContext())
                        .enqueue(uploadWorkRequest);
            }
        });

        requestPermissions();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        int count = preferences.getInt("totalsent", 0);
        binding.totalsendtext.setText("By sending " + count + " IPLOC infos");


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (listener != null)
            PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).unregisterOnSharedPreferenceChangeListener(listener);

    }
}