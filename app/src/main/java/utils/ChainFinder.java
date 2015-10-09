package utils;



import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import dgd.payaway.LandingActivity;


/**
 * Created by ace1_ on 10/8/2015.
 */
public class ChainFinder {

    private static String TAG = "ChainFinder";

    public static void GetNearbyStores(final Context context,Location searchLocation, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("latitude", searchLocation.getLatitude());
        params.put("longitude", searchLocation.getLongitude());

        client.get(context, "http://payaway.me/api/store", null, params,handler);
    }
}
