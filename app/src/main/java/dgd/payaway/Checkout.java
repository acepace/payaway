package dgd.payaway;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import utils.QRgenerator;

public class Checkout extends AppCompatActivity {

    private String mCartID;
    private Bitmap QRcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        
        Intent i = getIntent();
        mCartID = i.getStringExtra("cartID");

        QRcode = QRgenerator.generateQRtoken(mCartID);

        ImageView v = (ImageView)findViewById(R.id.qr_image);
        v.setImageBitmap(QRcode);
    }

}
