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
import java.util.List;

import utils.CartManager;
import utils.Product;
import utils.Store;

public class CartActivity extends ActionBarActivity implements CartManager.OnCartItemsCallback
{
    RecyclerView recyclerView;

    Store mPickedStore;
    CartManager mCart;

    ArrayList<Product> itemsList = new ArrayList<>();
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent i = getIntent();
        mPickedStore = i.getParcelableExtra("pickedStore");

        mCart = new CartManager("1337",mPickedStore.ChainId,mPickedStore.StoreId,this);
        mCart.initCart(this,new CartManager.OnCartInitCallback(){
            @Override
            public void OnCartInit(boolean success, String CartID){
                mCart.loadCartData();
            }
        });

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

        ArrayList<Product> it = new ArrayList<Product>();

        adapter = new RecyclerViewAdapter(CartActivity.this, it);
        recyclerView.setAdapter(adapter);

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

    @Override
    public void OnCartItemsLoaded() {

        itemsList = mCart.cartProducts;
        adapter.itemsList = itemsList;
        adapter.notifyDataSetChanged();

    }
}
