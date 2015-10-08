package dgd.payaway;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RowViewHolder extends RecyclerView.ViewHolder {

    ImageView productImage;
    TextView name;
    TextView quantity;
    TextView price;

    public RowViewHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.title);
        this.productImage = (ImageView) view.findViewById(R.id.image);
        this.quantity = (TextView) view.findViewById(R.id.quantity);
        this.price = (TextView) view.findViewById(R.id.price);
    }
}
