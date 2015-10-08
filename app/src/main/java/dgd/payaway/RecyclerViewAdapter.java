package dgd.payaway;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import utils.Product;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RowViewHolder> {
    Context context;
    ArrayList<Product> itemsList;

    public RecyclerViewAdapter(Context context, ArrayList<Product> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    @Override
    public int getItemCount() {
        if (itemsList == null) {
            return 0;
        } else {
            return itemsList.size();
        }
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.single_row, null);
        RowViewHolder viewHolder = new RowViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RowViewHolder rowViewHolder, int position) {
        Product item = itemsList.get(position);
        rowViewHolder.name.setText(item.Name);
        Picasso
                .with(context)
                .load(item.ImageUrl)
                .placeholder(R.drawable.pa_icon)
                .into(rowViewHolder.productImage);
    }
}
