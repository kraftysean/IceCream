package com.tasty.icecream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tasty.icecream.util.FourDigitCardFormatWatcher;
import com.tasty.icecream.util.NetworkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends Activity {

    private NetworkReceiver nReceiver;
    TextView netConnection, tvOrder, tvOrderDetail, tvTotal, tvTotalDetail, tvNumber, tvMonth, tvYear, tvCVV2;
    EditText etNumber, etMonth, etYear, etCVV2;
    ImageView ivOrder;
    ImageButton btnPayNow;
    String selectedFlavour;
    String selectedStyle;
    String totalAmount;
    String httpResult;
    String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        String serverIP = System.getProperty("SERVER_IP");
        String serverPort = System.getProperty("SERVER_PORT");
        serverUrl = String.format("http://%s:%s/icecream-server/Sales", serverIP, serverPort);

        netConnection = (TextView) findViewById(R.id.netConnection);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedStyle = extras.getString("selected-style").toUpperCase();
            selectedFlavour = extras.getString("selected-flavour").toUpperCase();
            totalAmount = extras.getString("total-amount");
        }

        ivOrder = (ImageView) findViewById(R.id.ivOrder);
            if(selectedStyle.startsWith("B"))
                ivOrder.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.cup_icecream));
            else
                ivOrder.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.cone_icecream));

        etNumber = (EditText) findViewById(R.id.etNumber);
        etMonth = (EditText) findViewById(R.id.etMonth);
        etYear = (EditText) findViewById(R.id.etYear);
        etCVV2 = (EditText) findViewById(R.id.etCVV2);
        tvOrder = (TextView) findViewById(R.id.tvOrder);
        tvOrderDetail = (TextView) findViewById(R.id.tvOrderDetail);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvTotalDetail= (TextView) findViewById(R.id.tvTotalDetail);
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        tvMonth = (TextView) findViewById(R.id.tvMonth);
        tvYear = (TextView) findViewById(R.id.tvYear);
        tvCVV2 = (TextView) findViewById(R.id.tvCVV2);
        btnPayNow = (ImageButton) findViewById(R.id.btnPayNow);

        tvOrderDetail.setText(String.format("%s\n%s", selectedStyle, selectedFlavour));
        tvTotalDetail.setText(totalAmount);
        etNumber.addTextChangedListener(new FourDigitCardFormatWatcher());

        setTypeFaceForTextView();

        nReceiver = new NetworkReceiver();
        registerReceiver(nReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void setTypeFaceForTextView() {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Schalk.ttf");

        // set textView typeface
        tvOrder.setTypeface(custom_font);
        tvTotal.setTypeface(custom_font);
        tvNumber.setTypeface(custom_font);
        tvMonth.setTypeface(custom_font);
        tvYear.setTypeface(custom_font);
        tvCVV2.setTypeface(custom_font);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(nReceiver);
        super.onDestroy();
    }

    public void selectPayNow(View v) {
        if (!validateFields())
            Toast.makeText(getBaseContext(), "Enter all card details!", Toast.LENGTH_SHORT).show();
        else {
            btnPayNow.setEnabled(false);

            new Thread(new Runnable() {
                public void run() {

                    try {
                        // TODO: URL not secure - need to verify Server Certificate in order to use https
                        // If we want to tokenize the credit card details, we'll need to run in WebView to enable javascript
                        // https://developer.android.com/guide/webapps/webview.html

                        URL url = new URL(serverUrl);
                        URLConnection connection = url.openConnection();

                        String cNumber = etNumber.getText().toString();
                        cNumber = cNumber.replaceAll("\\s", "");
                        String cMonth = etMonth.getText().toString();
                        String cYear = etMonth.getText().toString();
                        String cCVV2 = etCVV2.getText().toString();
                        String amount = tvTotalDetail.getText().toString();
                        String style = selectedStyle;
                        String flavour = selectedFlavour;

                        String inputString = String.format(
                                "card_number=%s&card_expiry_month=%s&card_expiry_year=%s&cvv2=%s&amount=%s&-style=%s&flavour=%s",
                                cNumber, cMonth, cYear, cCVV2, amount, style, flavour);

                        Log.d("inputString", inputString);

                        connection.setDoOutput(true);
                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        out.write(inputString);
                        out.close();

                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        String returnString = "";
                        List<String> httpResponse = new ArrayList<>();

                        while ((returnString = in.readLine()) != null) {
                            httpResponse.add(returnString);
                        }
                        in.close();

                        if(httpResponse != null) {
                            Log.d("Response: ", "> " + httpResponse.get(0));
                            try {
                                // Parse the json to get the response from the server
                                JSONObject jObj = new JSONObject(httpResponse.get(0));
                                httpResult = jObj.getString("result");
                                Log.d("JSON-RESULT: ", "> " +httpResult);

                            } catch (JSONException jex) {
                                Log.d("JSONException: ", jex.toString());
                            }
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Intent i = new Intent(getApplicationContext(), ResultActivity.class);
                                i.putExtra("http-result", httpResult);
                                startActivity(i);
                                finish();
                            }
                        });
                    } catch (Exception e) {
                        Log.d("Exception", e.toString());
                    }
                }
            }).start();
        }
    }

    private boolean validateFields() {
        return (!(etNumber.getText().toString().isEmpty() || etMonth.getText().toString().isEmpty()
                || etYear.getText().toString().isEmpty() || etCVV2.getText().toString().isEmpty()));
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    // TODO: Link to payment button so that it can be toggled on/off
    // Monitors the network connection to ensure we can send our HttpRequest
    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            netConnection.setText(NetworkStatus.getConnectivityStatusString(context));
            if (isConnected()) {
                btnPayNow.setEnabled(true);
                netConnection.setBackgroundColor(getResources().getColor(R.color.green));
            }
            else {
                btnPayNow.setEnabled(false);
                netConnection.setBackgroundColor(getResources().getColor(R.color.red));
            }
        }
    }
}