package com.tasty.icecream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends Activity {

    private NetworkReceiver nReceiver;
    private static final String MERCHANT_ID = "a3e5588422";
    private static final String API_KEY = "d7c566842e19aa2103ce15ffd06657ea9701bcf5";
    private static final String ENCRYPTION_KEY = "2d2d2d2d2d424547494e205055424c4943204b45592d2d2d2d2d4d494942496a414e42676b71686b6947397730424151454641414f43415138414d49494243674b434151454177454f6e2f7643387732586e4354395467757559434e6d4772682b626d48546972563732354a773979597771574950647a6978623353367665652b6853365962776d475171707457446d35667a56736c754367516a794d41315041352b6b3063772f774a677262756b51373853394b6f4a5a4d34527a7a6571736d5039614f5063635846454a6961644a73526a3659333837306838396f3255796b52747230474568326f6d7159585844535a413174534c786b556b765461516b4d2b456266574e4971554b35466e515734675555563643754d4479352b6b554849386c4b7862385a2f556e6a6272734c485338696f6a5162414b74437a79754267707852704f385a527261422b5962722f65367073504461435074496e364b514f4555362b6f7537686b6433505a756a502b3130786152695a7361365637763647644c4f5a645a336f786b2b5359694150302f374358444a332b6e774944415141422d2d2d2d2d454e44205055424c4943204b45592d2d2d2d2d";
    private static final boolean TEST_MODE = true;
    private static final String PAYFIRMA_URL = "https://ecom.payfirma.com/sale/";
    private static final String PAYFIRMA_URL_FULL = "https://ecom.payfirma.com/sale/?merchant_id=a3e5588422&key=d7c566842e19aa2103ce15ffd06657ea9701bcf5&test_mode=true&amount=11.34&card_number=4111111111111111&card_expiry_month=12&card_expiry_year=34&cvv2=123";
    TextView netConnection, tvOrder, tvOrderDetail, tvTotal, tvTotalDetail, tvNumber, tvMonth, tvYear, tvCVV2;
    EditText etNumber, etMonth, etYear, etCVV2;
    ImageView ivOrder;
    ImageButton btnPayNow;
    CreditCard cCard;
    String selectedFlavour;
    String selectedStyle;
    String totalAmount;
    String httpResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

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
                        // TODO: URL not secure - could use something like WebView
                        URL url = new URL("http://192.168.1.86:8080/icecream-server/Sales");
                        URLConnection connection = url.openConnection();

                        String cNumber = etNumber.getText().toString();
                        cNumber = cNumber.replaceAll("\\s", "");
                        String cMonth = etMonth.getText().toString();
                        String cYear = etMonth.getText().toString();
                        String cCVV2 = etCVV2.getText().toString();
                        String amount = tvTotalDetail.getText().toString();
                        String inputString = String.format(
                                "card_number=%s&card_expiry_month=%s&card_expiry_year=%s&cvv2=%s&amount=%s",
                                cNumber, cMonth, cYear, cCVV2, amount);

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