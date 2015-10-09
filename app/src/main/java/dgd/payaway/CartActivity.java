package dgd.payaway;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import cz.msebera.android.httpclient.Header;
import utils.CartManager;
import utils.Product;
import utils.Store;
import utils.UserEmailFetcher;

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

        mCart = new CartManager(UserEmailFetcher.getEmail(this),mPickedStore.ChainId,mPickedStore.StoreId,this);
        mCart.initCart(this,new CartManager.OnCartInitCallback(){
            @Override
            public void OnCartInit(boolean success, String CartID){
                ImageButton productChooserButton = (ImageButton) findViewById(R.id.imageButton);
                productChooserButton.setEnabled(true);
                mCart.loadCartData();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton productChooserButton = (ImageButton) findViewById(R.id.imageButton);
        ImageButton fakeChooserButton = (ImageButton) findViewById(R.id.fake_image_button);
        fakeChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                navigateToTestBarcode();
                findViewById(R.id.top_layout).setVisibility(View.INVISIBLE);
            }
        });
        productChooserButton.setEnabled(false); //untill we have a cart
        productChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigateToTestBarcode();
            }


        });

        ArrayList<Product> it = new ArrayList<Product>();

        adapter = new RecyclerViewAdapter(CartActivity.this, it);
        recyclerView.setAdapter(adapter);

        initSwipeToDismissTouchHelper();

    }

    private void navigateToTestBarcode() {
        Intent i = new Intent(getApplicationContext(), TestBarcode.class);
        i.putExtra("cartID",mCart.getCartID());
        startActivityForResult(i, 0x100);
    }

    private void initSwipeToDismissTouchHelper() {
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
                Product removedProduct = itemsList.get(viewHolder.getAdapterPosition());
                removedProduct.Amount = (-removedProduct.Amount);
                Product.updateProduct(CartActivity.this,mCart.getCartID(),removedProduct);
                itemsList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                updateTotal();
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
                        .amount(mCart.getTotalPriceString())
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
                    updateProduct(newProd);
                    OnCartItemsLoaded();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateProduct(Product newProd) {
        //if it exists
        int prodIndex = mCart.cartProducts.indexOf(newProd);

        if (-1 == prodIndex) {
            mCart.cartProducts.add(newProd);
        } else {
            mCart.cartProducts.get(prodIndex).Amount = newProd.Amount;
        }
    }

    public void RunTransaction(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        String cartID = mCart.getCartID();
        String url = String.format("http://payaway.me/api/cart/%s?nonce=%s",cartID,nonce);
        Log.i(TAG,url);
        client.delete(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Failed to send payment nonce");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                // Successfully got a response
                Log.i(TAG, "Suceeded to send payment nonce");
                if (responseString.equals("true")) {
                    Intent intent = new Intent(getApplicationContext(), Checkout.class);
                    intent.putExtra("cartID",mCart.getCartID());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    //
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

        updateTotal();


    }

    private void updateTotal() {
        setTitle(mCart.getTotalPriceString());
    }
}
