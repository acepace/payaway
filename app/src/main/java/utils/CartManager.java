package utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CartManager {
    private String TAG = "CartManager";

    private String userID;
    private String chainID;
    private String storeID;
    private String cartID;

    public String getCartID() {return cartID;}
    public String cartToken;

    public ArrayList<Product> cartProducts;
    private static ObjectMapper sMapper = new ObjectMapper();

    private OnCartItemsCallback callback;



    public CartManager(String userID,String chainID,String storeID,OnCartItemsCallback callback)
    {
        this.userID = userID;
        this.chainID = chainID;
        this.storeID = storeID;
        cartID = "BADFOOD";
        this.callback = callback;


    }

    public interface OnCartInitCallback {
        void OnCartInit(boolean success, String cartID);
    }

    public interface OnCartItemsCallback {
        void OnCartItemsLoaded();

    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = new BigDecimal(0);
        for (Product item: cartProducts) {
            totalPrice = totalPrice.add(item.getTotalPrice());
        }
        return totalPrice;
    }

    public String getTotalPriceString() {
        BigDecimal bd = getTotalPrice();
        return bd.toString()+" ₪";
    }

    public int getTotalItems()
    {
        int total = 0;
        for (Product item: cartProducts) {
            total += item.Amount;
        }
        return total;
    }


    public void initCart(Context context, final OnCartInitCallback callback)
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
                    try {
                        cartID = response.getString("CartId");
                        cartToken = response.getString("Token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG,"Cart ID is"+ cartID);
                    callback.OnCartInit(true, cartID);
                }

                @Override
                public void onFailure(int statusCode,
                                      cz.msebera.android.httpclient.Header[] headers,
                                      java.lang.Throwable throwable,
                                      org.json.JSONObject errorResponse)
                {
                    System.out.println(TAG+"wtf");
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
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Tries to load cart data
    public void loadCartData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get( "http://payaway.me/api/cart/"+cartID, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.e(TAG, "Received cartObject");

                //get the cart items
                try {
                    JSONArray productsJSON = response.getJSONArray("Products");
                    cartProducts = sMapper.readValue(productsJSON.toString(),
                            new TypeReference<ArrayList<Product>>() {
                            });

                    Log.i(TAG,"Finished parsing carting");
                    callback.OnCartItemsLoaded();
                    //WT
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  cz.msebera.android.httpclient.Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
                Log.e(TAG,"Failed to find cart members");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                System.out.println(TAG+"yayArray");
                Log.e(TAG, "Received array instead of object");
            }
        });
    }


}


