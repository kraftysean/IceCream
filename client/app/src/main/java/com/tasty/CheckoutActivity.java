package com.tasty;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;
import com.tasty.util.FourDigitCardFormatWatcher;
import com.tasty.util.NetworkStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckoutActivity extends Activity {
    private static final String TAG = CheckoutActivity.class.getSimpleName();

    public static final String PUBLISHABLE_KEY = "pk_test_5aP62wpdLE9i8aeUyhpewZb5";
    public static final String API_HOST = "http://ews-kraftys.rhcloud.com/icecream/Sales";  // remote server
//    public static final String API_HOST = "http://10.0.2.2:8080/icecream/Sales";  // Use 10.0.2.2:8080 for emulator
    private NetworkReceiver nReceiver;
    TextView netConnection, tvOrder, tvOrderDetail, tvTotal, tvTotalDetail, tvNumber, tvMonth, tvYear, tvCVV2;
    EditText etNumber, etMonth, etYear, etCVV2;
    ImageView ivOrder;
    ImageButton btnPayNow;
    String selectedFlavour;
    String selectedStyle;
    String totalAmount;
    ProgressDialog pDialog;

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
        if (selectedStyle.equalsIgnoreCase("Bowl"))
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
        tvTotalDetail = (TextView) findViewById(R.id.tvTotalDetail);
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
//        if (pDialog != null)
//            pDialog.dismiss();
        unregisterReceiver(nReceiver);
        super.onDestroy();
    }

    private void startStripeWebServiceTask(JSONObject stripeParams) {
        StripeWebServiceTask stripeWebServiceTask = new StripeWebServiceTask();
        stripeWebServiceTask.execute(this, API_HOST, stripeParams);
    }

    public void selectPayNow(View v) throws AuthenticationException {
        pDialog = ProgressDialog.show(CheckoutActivity.this, "", "Authorizing payment.  Please wait...");

        String number = etNumber.getText().toString();
        String month = etMonth.getText().toString();
        String year = etYear.getText().toString();
        Integer expMonth = 0, expYear = 0;
        if (!month.equals("") && !year.equals("")) {
            expMonth = Integer.parseInt(month);
            expYear = Integer.parseInt(year);
        }
        String cvc = etCVV2.getText().toString();
        final int amount = (int) (Double.parseDouble(tvTotalDetail.getText().toString()) *100);
        final String name = "MY NAME";
        final String style = selectedStyle;
        final String flavour = selectedFlavour;

        // Stripe card object
        Card card = new Card(number, expMonth, expYear, cvc);

        // Validate card details
        if (!card.validateCard()) {
            pDialog.dismiss();
            Log.d(TAG, "Invalid card details entered");
            Toast.makeText(this, "Card details invalid !!", Toast.LENGTH_LONG).show();
        } else {
            Stripe stripe = new Stripe(PUBLISHABLE_KEY);

            stripe.createToken(card, new TokenCallback() {
                @Override
                public void onError(Exception error) {
                    Log.e(TAG, "Unable to create token: " + error.toString());
                    Toast.makeText(getBaseContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(Token token) {
                    Log.d(TAG, "New token created...");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("stripe-token", token.getId());
                        // TODO: Add name textfield
                        jsonObject.put("name", name);
                        jsonObject.put("amount", amount);
                        jsonObject.put("flavour", flavour);
                        jsonObject.put("style", style);
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                    // Connect to remote server to authorize Stripe payment
                    startStripeWebServiceTask(jsonObject);
                }
            });
            pDialog.dismiss();
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // TODO: Link to payment button so that it can be toggled on/off
    // Monitors the network connection to ensure we can send our HttpRequest
    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            netConnection.setText(NetworkStatus.getConnectivityStatusString(context));
            if (isConnected()) {
                btnPayNow.setVisibility(View.VISIBLE);
                netConnection.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                btnPayNow.setVisibility(View.INVISIBLE);
                netConnection.setBackgroundColor(getResources().getColor(R.color.red));
            }
        }
    }
}
