package utils;


import android.content.Context;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by ace1_ on 10/8/2015.
 */
public class CartManager {


    private String userID;
    private String chainID;
    private String storeID;
    private String cartID;

    public CartManager(String userID,String chainID,String storeID)
    {
        this.userID = userID;
        this.chainID = chainID;
        this.storeID = storeID;
    }

    public void initCart(Context context)
    {
        AsyncHttpClient client = new AsyncHttpClient();

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("UserId", userID);
            jsonParams.put("ChainId", chainID);
            jsonParams.put("StoreId", storeID);
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context,"http://payaway.me/api/cart",entity, "application/json", new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    String test = null;
                    try {
                        test = response.getString("CartId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(test);
                }

                @Override
                public void onFailure(int statusCode,
                                      cz.msebera.android.httpclient.Header[] headers,
                                      java.lang.Throwable throwable,
                                      org.json.JSONObject errorResponse)
                {
                    System.out.println("wtf");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    // Pull out the first event on the public timeline
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }


    }
}
