package com.tasty.icecream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity implements View.OnClickListener { // add Action Bar by extendingn AppCompatActivity

    TextView netConnection, tvStep1, tvStep2, tvStep3;
    EditText etName;
    ImageButton btnDarkChocMint, btnMatchaGreen, btnPumpkinPie, btnStrwaberryBasis, btnWaffleCone, btnBowl, btnCheckout;

    CreditCard cCard;
    private NetworkMonitor nMonitor;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        nMonitor = new NetworkMonitor();
        registerReceiver(nMonitor, filter);

        // get reference to the views
        netConnection = (TextView) findViewById(R.id.netConnection);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Schalk.ttf");
        tvStep1 = (TextView) findViewById(R.id.tvStep1);
        tvStep1.setTypeface(custom_font);
        tvStep2 = (TextView) findViewById(R.id.tvStep2);
        tvStep2.setTypeface(custom_font);
        tvStep3 = (TextView) findViewById(R.id.tvStep3);
        tvStep3.setTypeface(custom_font);
//        etName = (EditText) findViewById(R.id.etName);
        btnCheckout = (ImageButton) findViewById(R.id.btnCheckout);

        // add click listener to Button "POST"
        btnCheckout.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnCheckout) {
            Toast.makeText(getBaseContext(), "Checkout", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), CheckoutActivity.class);
            i.putExtra("new_variable_name","value");
            startActivity(i);
//        switch(v.getId()){
//            case R.id.btnCheckout:
//                if(!validate())
//                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
//                else {
//                    // call AsynTask to perform network operation on separate thread
//                    new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservlet");
//                    break;
//                }
        }
    }

    public static String httpPOST(String url, CreditCard cCard){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", cCard.getName());
            jsonObject.accumulate("country", cCard.getCountry());
            jsonObject.accumulate("twitter", cCard.getTwitter());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            cCard = new CreditCard();
            cCard.setName(etName.getText().toString());

            return httpPOST(urls[0], cCard);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(){
        if(etName.getText().toString().trim().equals(""))
            return false;
//        else if(etCountry.getText().toString().trim().equals(""))
//            return false;
//        else if(etTwitter.getText().toString().trim().equals(""))
//            return false;
        else
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class NetworkMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            netConnection.setText(NetworkStatus.getConnectivityStatusString(context));
            isConnected = NetworkStatus.isConnected(context);
            if(isConnected())
                netConnection.setBackgroundColor(getResources().getColor(R.color.green));
            else
                netConnection.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }
}


