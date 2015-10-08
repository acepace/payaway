package dgd.payaway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import utils.CartManager;
import utils.Store;

public class CartActivity extends ActionBarActivity
{
    RecyclerView recyclerView;

    Store mPickedStore;
    CartManager mCart;

    ArrayList<CartItem> itemsList = new ArrayList<>();
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        Intent i = getIntent();
        mPickedStore = i.getParcelableExtra("pickedStore");

        mCart = new CartManager("1337",mPickedStore.ChainId,mPickedStore.StoreId);
        mCart.initCart(this);

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

        ArrayList<CartItem> it = new ArrayList<CartItem>(); //set something emptpy up front
        adapter = new RecyclerViewAdapter(CartActivity.this, it);
        recyclerView.setAdapter(adapter);

    }

    ///If cart information exists, lets load it
    public ArrayList<CartItem> getCartData()
    {
        ArrayList<CartItem> it = new ArrayList<CartItem>();
        CartItem items1 = new CartItem();
        items1.setTitle("Banana");
        items1.setIcon(R.drawable.pa_icon);
        items1.setPricePerUnit(3);
        items1.setTotalPrice(9);
        it.add(items1);
        CartItem items2 = new CartItem();
        items2.setTitle("Banana");
        items2.setIcon(R.drawable.pa_icon);
        items2.setPricePerUnit(3);
        items2.setTotalPrice(9);
        it.add(items2);
        CartItem items3 = new CartItem();
        items3.setTitle("Banana");
        items3.setIcon(R.drawable.pa_icon);
        items3.setPricePerUnit(3);
        items3.setTotalPrice(9);
        it.add(items3);

        return it;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.checkoutBtn:
                //openSearch();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
