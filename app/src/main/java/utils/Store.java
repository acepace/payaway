package utils;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import dgd.payaway.R;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Store implements Parcelable{
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

    @Override
    public String toString() {
        return ChainName +" " + "כתובת" + " "+ StoreAddress;
    }


    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(ChainName);
        out.writeString(SubChainName);
        out.writeString(StoreAddress);
        out.writeString(ChainId);
        out.writeString(StoreId);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Store> CREATOR = new Parcelable.Creator<Store>() {
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Store(Parcel in) {
        ChainName = in.readString();
        SubChainName = in.readString();
        StoreAddress = in.readString();
        ChainId = in.readString();
        StoreId = in.readString();
    }
}
