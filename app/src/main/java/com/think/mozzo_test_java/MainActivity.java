package com.think.mozzo_test_java;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;
import com.think.mozzo_test_java.data.Constants;

import java.util.ArrayList;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    public static final String AUTHORITY = "com.think.mozzo_test_java";
    public static final String ACCOUNT_TYPE = "com.think.mozzo_test.datasyncservice";
    public static final String ACCOUNT = "dummyaccount";
    Account mAccount;

    protected ArrayList<Geofence> mGeofenceList;
    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;
    private ProximityManager proximityManager;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<String> myDataset;

    private String TAG="Java Sample";
    private String initialData="Searching for URL's";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this,LoaderActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
//        mAccount = CreateSyncAccount(this);
//        KontaktSDK.initialize(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGeofenceList = new ArrayList<Geofence>();
        populateGeofenceList();

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Found message: " + messageAsString);
                if(myDataset.get(0).equals(initialData)){
                    myDataset.remove(0);
                }
                myDataset.add(messageAsString);
                mAdapter.notifyDataSetChanged();
                insertIntoContentProvider(messageAsString);
            }

            @Override
            public void onLost(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost sight of message: " + messageAsString);
                for (int i=0;i<myDataset.size();i++){
                    if (myDataset.get(i).equals(messageAsString)){
                        myDataset.remove(i);
                    }
                }
                if (myDataset.size()==0){
                    myDataset.add(initialData);
                }
                mAdapter.notifyDataSetChanged();
            }
        };


//        proximityManager = new ProximityManager(this);
//        proximityManager.setIBeaconListener(createIBeaconListener());
//        proximityManager.setEddystoneListener(createEddystoneListener());

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataset=new ArrayList<>();
        myDataset.add(initialData);
        mAdapter = new MaterialAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

        System.out.println("Done");

    }



    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            return newAccount;
        }

        else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Log.d("TAG", "Error Sync Adapter Not Configured");
            return null;

        }
    }

    private void insertIntoContentProvider(String url) {
        ContentValues values = new ContentValues();
        values.put(UrlHistoryProvider._ID,
                (url));

        values.put(UrlHistoryProvider.TIME,
                (System.currentTimeMillis()));

        Uri uri = getContentResolver().insert(
                UrlHistoryProvider.CONTENT_URI, values);

        Toast.makeText(getBaseContext(),
                "Data Updated", Toast.LENGTH_SHORT).show();
    }


//    private IBeaconListener createIBeaconListener() {
//        return new SimpleIBeaconListener() {
//            @Override
//            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
//                Log.i(TAG, "IBeacon discovered: " + ibeacon.toString());
//            }
//        };
//    }
//
//    private EddystoneListener createEddystoneListener() {
//        return new SimpleEddystoneListener() {
//            @Override
//            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
//                Log.i(TAG, "Eddystone discovered: " + eddystone.toString());
//            }
//        };
//    }

//    private void startScanning() {
//        proximityManager.connect(new OnServiceReadyListener() {
//            @Override
//            public void onServiceReady() {
//                proximityManager.startScanning();
//            }
//        });
//    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build();
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        subscribe();
        registerGeoFence();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

//        Log.i()
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 1001);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("NO resolution");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                Log.e(TAG, "GoogleApiClient connection failed. Unable to resolve.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        startScanning();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

//        proximityManager.disconnect();
//        proximityManager = null;
        super.onStop();
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    public void registerGeoFence() {
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added Successfully",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            System.out.println("GeoFence Not Added");
        }

    }
}
