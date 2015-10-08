package utils;


import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product implements Parcelable {

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



    public Product()
    {

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

}