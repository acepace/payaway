package dgd.payaway;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.CartManager;
import utils.ChainFinder;
import utils.Store;

/**
 * Created by galpeer on 10/8/15.
 */
public class LandingActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    protected static final String TAG = "LandingActivity";
    private List<Store> mNearbyStores;
    private Store pickedStore;

    protected GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    ObjectMapper mMapper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(i);
            }
        });
        buildGoogleApiClient();
        mMapper = new ObjectMapper();


    }


    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String chainJSON = extras.getString("jsonChainList");

        try {
            List<Store> list = mMapper.readValue(chainJSON,
                    new TypeReference<ArrayList<Store>>() {});

            setNearbyStoreList(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            ChainFinder.GetNearbyStores(this,mLastLocation);
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    public void setNearbyStoreList(List<Store> list) {
        mNearbyStores = list;
        //Cast to strings, and display as items

        final List<String> storeNames = new ArrayList<String>();
        for (Store item : mNearbyStores) {
            storeNames.add(item.toString());
        }
        String[] strarray = new String[storeNames.size()];
        storeNames.toArray(strarray );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.nearbyStores))
                .setItems(strarray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText storePicker = (EditText) findViewById(R.id.input_store);
                        storePicker.setText(storeNames.get(which));
                        pickedStore = mNearbyStores.get(which);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void pickedStoreClick(View v){
        EditText storePicker = (EditText) findViewById(R.id.input_store);


        Intent i = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(i);
    }
}
