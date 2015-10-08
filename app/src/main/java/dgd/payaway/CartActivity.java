package dgd.payaway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class CartActivity extends ActionBarActivity
{
    RecyclerView recyclerView;
    ArrayList<RowItems> itemsList = new ArrayList<>();
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageButton productChooserButton = (ImageButton) findViewById(R.id.imageButton);

        productChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(getApplicationContext(), TestBarcode.class);
                startActivity(i);
            }
        });

        adapter = new RecyclerViewAdapter(CartActivity.this, getData());
        recyclerView.setAdapter(adapter);

    }

    public ArrayList<RowItems> getData()
    {
        ArrayList<RowItems> it = new ArrayList<RowItems>();
        RowItems items1 = new RowItems();
        items1.setTitle("Banana");
        items1.setIcon(R.drawable.pa_icon);
        items1.setPricePerUnit(3);
        items1.setTotalPrice(9);
        it.add(items1);
        RowItems items2 = new RowItems();
        items2.setTitle("Banana");
        items2.setIcon(R.drawable.pa_icon);
        items2.setPricePerUnit(3);
        items2.setTotalPrice(9);
        it.add(items2);
        RowItems items3 = new RowItems();
        items3.setTitle("Banana");
        items3.setIcon(R.drawable.pa_icon);
        items3.setPricePerUnit(3);
        items3.setTotalPrice(9);
        it.add(items3);

        return it;
    }
}
