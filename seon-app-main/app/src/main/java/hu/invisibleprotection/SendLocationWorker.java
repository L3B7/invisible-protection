package hu.invisibleprotection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import hu.invisibleprotection.ui.DataStruct;
import hu.invisibleprotection.ui.ResponseObject;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendLocationWorker extends Worker {
    private static final String TAG = "SendLocationWorker";

    private String baseURL = "https://seon-ipmapper.herokuapp.com/ping";//"https://requestbin.io/slylorsl"; //TODO change...

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private DataStruct dats2send =  new DataStruct();

    private SharedPreferences preferences;

    public SendLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d(TAG, "Starting work...");
        Date date = new Date();
        String timenowstring = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss").format(date);
        preferences.edit().putString("laststart", timenowstring).commit();

        getNetworkInformation(getApplicationContext());
        if (preferences.getString("overrideaddress","").length() > 0){
            baseURL = preferences.getString("overrideaddress","");
        }
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(100).setFastestInterval(100).setSmallestDisplacement(0.001f).setNumUpdates(1);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    String latitude = String.valueOf(location.getLatitude());
                    String longitude = String.valueOf(location.getLongitude());
                    dats2send.latitude = location.getLatitude();
                    dats2send.longitude = location.getLongitude();
                    dats2send.accuracy = location.getAccuracy();
                    dats2send.isLocationValid = true;
                    Log.d(TAG, dats2send.toString());

                    Log.d(TAG, "LAT:"+latitude+" LON:"+longitude);
                    sendData();
                }
            }
        };
        if (dats2send.isVPN && preferences.getBoolean("privacy_no_loc_if_vpn", false)){
            Log.d(TAG, "Privacy mode... Not sending location");
            sendData();
            return Result.success();
        }

        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "Location permission denied :(");
            sendData();
            return Result.failure();
        }
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this.getApplicationContext());
        client.requestLocationUpdates(locationRequest, locationCallback, getApplicationContext().getMainLooper());

        return Result.success();
    }

    private void getNetworkInformation(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        dats2send.isVPN = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        dats2send.isWiFi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        dats2send.isCellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            dats2send.isUsb = caps.hasTransport(NetworkCapabilities.TRANSPORT_USB);
        } else {
            dats2send.isUsb = false;
        }
        dats2send.isBluetooth = caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH);
    }

    private void sendData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Sending data...");
                Gson gson = new Gson();

                dats2send.appToken = "PleaseDontSpoofThisThisIsOnlyADemo";

                String gsonString = gson.toJson(dats2send);
                Log.d(TAG, gsonString);

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                        .build();

                Request request = new Request.Builder()
                        .url(baseURL) //TODO: change to /ping
                        .post(RequestBody.create(gsonString, JSON))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String rsp = response.body().string();
                    if (response.isSuccessful()){
                        Log.d(TAG, "Sending Success");
                        int count = preferences.getInt("totalsent", 0);
                        Date date = new Date();
                        String timenowstring = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss").format(date);

                        preferences.edit().putString("lastsent", timenowstring).commit();

                        preferences.edit().putInt("totalsent", count+1).commit();
                        rsp = rsp.substring(rsp.indexOf("{"));
                        JSONObject ob = new JSONObject(rsp);
                        Log.d(TAG, "Total IPLOC: "+ (count+1));
                        Log.d(TAG, "Response:" + rsp);

                        String ip = ob.getString("ip");
                        preferences.edit().putString("lastip", ip).commit();

                    }else{
                        Log.e(TAG, "Send failed");
                    }
                } catch (IOException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


}
