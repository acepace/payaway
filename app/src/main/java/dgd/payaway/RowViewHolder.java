package dgd.payaway;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RowViewHolder extends RecyclerView.ViewHolder {

    ImageView productImage;
    TextView name;
    TextView pricePerUnit;
    TextView totalPrice;

    public RowViewHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.product_name);
        this.productImage = (ImageView) view.findViewById(R.id.product_image);
        this.pricePerUnit = (TextView) view.findViewById(R.id.price_per_unit);
        this.totalPrice = (TextView) view.findViewById(R.id.total_price);
    }
}
