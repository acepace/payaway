package utils;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import cz.msebera.android.httpclient.entity.StringEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product implements Parcelable {

    private static String TAG = "ProductTag";
    @JsonProperty("ProductId")
    public String ProductId;

    @JsonProperty("Name")
    public String Name;
    @JsonProperty("ImageUrl")
    public String ImageUrl;

    @JsonProperty("Amount")
    public int Amount;
    @JsonProperty("Price")
    public BigDecimal Price;
    @JsonProperty("TotalPrice")
    public BigDecimal TotalPrice;

    private static ObjectMapper sMapper = new ObjectMapper();


    public Product()
    {

    }

    public BigDecimal getTotalPrice() {
        return Price.multiply(new BigDecimal(Amount));
    }

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(ProductId);
        out.writeString(Name);
        out.writeString(ImageUrl);
        out.writeInt(Amount);
        out.writeString(Price.toString());
        out.writeString(TotalPrice.toString());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Product(Parcel in) {
        ProductId = in.readString();
        Name = in.readString();
        ImageUrl = in.readString();
        Amount = in.readInt();
        Price = new BigDecimal(in.readString());
        TotalPrice = new BigDecimal(in.readString());
    }

    public static void getProduct(Context context,String CartID,String ProductID,final onProductLoadedCallback callback)
    {

        AsyncHttpClient client = new AsyncHttpClient();

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("CartID", CartID);
            jsonParams.put("ProductId", ProductID);
            jsonParams.put("Amount", 0);
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.put(context, "http://payaway.me/api/cart", entity, "application/json", new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.i(TAG,"Received object");
                    try {
                        Product newProduct = sMapper.readValue(response.toString(),
                                new TypeReference<Product>() {
                                });
                        callback.ProductLoaded(newProduct);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode,
                                      cz.msebera.android.httpclient.Header[] headers,
                                      java.lang.Throwable throwable,
                                      org.json.JSONObject errorResponse) {
                    System.out.println(TAG + "wtf");
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

    public static void updateProduct(Context context,String CartID,Product newProd) {
        AsyncHttpClient client = new AsyncHttpClient();
        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("CartID", CartID);
            jsonParams.put("ProductId", newProd.ProductId);
            jsonParams.put("Amount", newProd.Amount);
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.put(context, "http://payaway.me/api/cart", entity, "application/json", new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.i(TAG,"Updated cart");


                }

                @Override
                public void onFailure(int statusCode,
                                      cz.msebera.android.httpclient.Header[] headers,
                                      java.lang.Throwable throwable,
                                      org.json.JSONObject errorResponse) {
                    Log.i(TAG, "Failed to update cart");

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    // Pull out the first event on the public timeline
                }


                @Override
                public void onRetry(int retryNo) {
                    Log.i(TAG, "Retrying to update cart");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
    }
    public interface onProductLoadedCallback {
        public void ProductLoaded(Product p);
    }
}