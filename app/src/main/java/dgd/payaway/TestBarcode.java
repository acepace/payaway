package dgd.payaway;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class TestBarcode extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private EditText quantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_barcode);
        this.quantity = (EditText) findViewById(R.id.quantity);
        quantity.setText("0");
        mScannerView = new ZBarScannerView(this);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.zk_layout);
        linearLayout.addView(mScannerView);
        findViewById(R.id.btn_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence charSequence = quantity.getText().toString();
                int numOfItems = Integer.valueOf(charSequence.toString());
                numOfItems++;
                quantity.setText(String.valueOf(numOfItems));

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

            }
        });

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
    }

}
