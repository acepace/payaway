package dgd.payaway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
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
    private String TAG = "CartActivity";

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
                i.putExtra("cartID",mCart.getCartID());
                startActivityForResult(i,0x100);
            }
        });

        ArrayList<Product> it = new ArrayList<Product>();

        adapter = new RecyclerViewAdapter(CartActivity.this, it);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // callback for drag-n-drop, false to skip this feature
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                // callback for swipe to dismiss, removing item from data and adapter
                itemsList.remove(viewHolder.getAdapterPosition());
                //TODO: Update the server about deleting
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView);

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
                Intent intent = new Intent(this, BraintreePaymentActivity.class);
                intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, mCart.cartToken);

                Customization customization = new Customization.CustomizationBuilder()
                        .primaryDescription("Shopping cart")
                        .secondaryDescription(mCart.getTotalItems() + " Items")
                        .amount(mCart.getTotalPrice().toString()+"₪")
                        .submitButtonText("Purchase")
                        .build();
                intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION, customization);


                // REQUEST_CODE is arbitrary and is only used within this activity.
                startActivityForResult(intent, 100);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) { //return from braintree
            switch (resultCode) {
                case BraintreePaymentActivity.RESULT_OK:
                    String paymentMethodNonce = data
                            .getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                    RunTransaction(paymentMethodNonce);
                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR:
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR:
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_UNAVAILABLE:
                    // handle errors here, a throwable may be available in
                    // data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE)
                    break;
                default:
                    break;
            }
        } else if (requestCode == 0x100) //Scanner
        {
            switch (resultCode) {
                case 0:
                    //Failed
                    Log.i(TAG,"User did not want to add an item");
                    break;
                case 1:
                    Product newProd = data.getParcelableExtra("Product");
                    mCart.cartProducts.add(newProd);
                    OnCartItemsLoaded();
                    break;

                default:
                    break;
            }
        }
    }

    public void RunTransaction(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        String cartID = mCart.getCartID();
        String url = String.format("http://payaway.me/api/cart/%s?nonce=%s",cartID,nonce);
        client.delete(url,new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Failed to send payment nonce");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                // Successfully got a response
                Log.i(TAG, "Suceeded to send payment nonce");
                if (responseString.contains("true")) {
                    Toast.makeText(CartActivity.this,"Paid!",Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onStart() {
                // Initiated the request
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
            }
        });
    }

    @Override
    public void OnCartItemsLoaded(){

        itemsList = mCart.cartProducts;
        adapter.itemsList = itemsList;
        adapter.notifyDataSetChanged();
    }
}
