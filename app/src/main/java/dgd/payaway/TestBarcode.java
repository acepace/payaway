package dgd.payaway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import utils.Product;

public class TestBarcode extends AppCompatActivity implements ZBarScannerView.ResultHandler,Product.onProductLoadedCallback {

    private ZBarScannerView mScannerView;
    private EditText quantity;
    private String mCartID;

    private Product mProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_barcode);

        Intent i = getIntent();
        mCartID = i.getStringExtra("cartID");

        this.quantity = (EditText) findViewById(R.id.quantity);
        quantity.setText("0");
        mScannerView = new ZBarScannerView(this);
        mScannerView.setAutoFocus(true);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.zk_layout);
        linearLayout.addView(mScannerView);
        findViewById(R.id.btn_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence charSequence = quantity.getText().toString();
                int numOfItems = Integer.valueOf(charSequence.toString());
                numOfItems++;
                quantity.setText(String.valueOf(numOfItems));
                UpdatePrice(numOfItems);
            }
        });

        findViewById(R.id.btn_minus).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CharSequence charSequence = quantity.getText().toString();
                int numOfItems = Integer.valueOf(charSequence.toString());
                if (numOfItems == 0)
                {
                    return;
                }

                numOfItems --;
                quantity.setText(String.valueOf(numOfItems));
                UpdatePrice(numOfItems);
            }
        });

    }

    private void UpdatePrice(int numOfItems) {
        mProd.Amount = numOfItems;
        TextView pl = (TextView) findViewById(R.id.ProdPriceLbl);
        String price = new DecimalFormat("#0.##₪").format(mProd.getTotalPrice());
        pl.setText(price);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("blah", rawResult.getContents()); // Prints scan results
        Log.v("blah", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
        Product.getProduct(this,mCartID,rawResult.getContents(),this);

    }

    @Override
    public void ProductLoaded(Product p) {


        if (p == null) {
            mScannerView.stopCamera();
            mScannerView.startCamera();
            Toast.makeText(this,"Failed to find product.", Toast.LENGTH_LONG).show();
            return;
        }
        mProd = p;
        findViewById(R.id.btn_plus).setEnabled(true);
        findViewById(R.id.btn_minus).setEnabled(true);
        TextView tv = (TextView) findViewById(R.id.ProdNameLbl);
        tv.setText(mProd.Name);
        String price = "0.00₪";
        if (mProd.Amount == 0) {
            mProd.Amount = 1;
        }


        price = new DecimalFormat("#0.##₪").format(mProd.getTotalPrice());
        EditText numItemsEt = (EditText) findViewById(R.id.quantity);
        numItemsEt.setText(String.valueOf(mProd.Amount));
        TextView pl = (TextView) findViewById(R.id.ProdPriceLbl);
        pl.setText(price);

    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(0, data);
        //---close the activity---
        finish();
    }

    public void onConfirmClick(View v)
    {
        Product.updateProduct(this,mCartID,mProd);
        Intent data = new Intent();
        data.putExtra("Product",mProd);
        setResult(1, data);
        //---close the activity---
        finish();
    }
}
