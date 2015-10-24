package com.tasty;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {

    ImageView ivResult;
    TextView tvResult;
    ImageButton btnExit, btnSaveReceipt;
    String httpResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            httpResult = extras.getString("http-result");
        }

        tvResult = (TextView) findViewById(R.id.tvResult);
        ivResult = (ImageView) findViewById(R.id.ivResult);
        btnExit = (ImageButton) findViewById(R.id.btnExit);
        btnSaveReceipt = (ImageButton) findViewById(R.id.btnSaveReceipt);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Schalk.ttf");
        tvResult.setTypeface(custom_font);

        if (httpResult.equalsIgnoreCase(getString(R.string.trxApproved))) {
            ivResult.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.ice_cream_success));
            tvResult.setText(R.string.tvApprovedResult);
            btnExit.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.exit));
            btnSaveReceipt.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.save_receipt));
        }
        else if(httpResult.equalsIgnoreCase(getString(R.string.trxDeclined))) {
            ivResult.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.ice_cream_fail));
            tvResult.setText(R.string.tvDeclinedResult);
            btnExit.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.exit));
        }
        else {
            tvResult.setText(R.string.tvGoneWrongResult);
            btnExit.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.exit));
        }
    }

    public void selectExit(View v) {
        finish();
//        System.exit(0);
    }

    public void selectSaveReceipt(View v) {
        Toast.makeText(getBaseContext(), "Downloading to device!", Toast.LENGTH_LONG).show();
    }
}
