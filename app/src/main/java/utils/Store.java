package utils;

import android.content.res.Resources;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import dgd.payaway.R;


/**
 * Created by ace1_ on 10/8/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Store {
    @JsonProperty("ChainName")
    public String ChainName;
    @JsonProperty("StoreName")
    public String StoreName;
    @JsonProperty("SubChainName")
    public String SubChainName;
    @JsonProperty("Address")
    public String StoreAddress;

    @JsonProperty("ChainId")
    public String ChainId;

    @JsonProperty("StoreId")
    public String StoreId;

    public Store()
    {

    }

    public Store (String chain,String name,String address) {
        ChainName = chain;
        StoreName = name;
        StoreAddress = address;
    }

    @Override
    public String toString() {
        return ChainName +" " + "כתובת" + " "+ StoreAddress;
    }
}
