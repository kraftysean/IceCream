package com.tasty;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class StripeWebServiceTask extends AsyncTask<Object, Void, String> {

    CheckoutActivity callerActivity;

    private static final String TAG = StripeWebServiceTask.class.getSimpleName();

    @Override
    protected String doInBackground(Object... params) {
        callerActivity = (CheckoutActivity) params[0];
        String hostUrl = (String) params[1];
        JSONObject stripeParams = (JSONObject) params[2];
        Log.d(TAG, stripeParams.toString());
        try {
            return authorizeStripePayment(hostUrl, stripeParams);
        } catch (JSONException | IOException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent i = new Intent(callerActivity, ResultActivity.class);
        i.putExtra("http-result", result);
        callerActivity.startActivity(i);
        callerActivity.finish();
    }

    private String authorizeStripePayment(String hostUrl, final JSONObject stripeParams) throws IOException, JSONException {
        // TODO: Update to send request using Volley
        // http://developer.android.com/training/volley/simple.html
        // Use JsonObjectRequest - http://stackoverflow.com/questions/23220695/send-post-request-with-json-data-using-volley

        InputStream is = null;
        StringBuilder httpResponse = new StringBuilder();
        try {
            URL url = new URL(hostUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            // Start the query
            conn.connect();
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.write(stripeParams.toString().getBytes("UTF-8"));
            dos.flush();
            dos.close();


            int httpStatus = conn.getResponseCode();
            Log.d(TAG, "HTTP Status: " + httpStatus);

            BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String strLine;
            while ((strLine = input.readLine()) != null) {
                httpResponse.append(strLine);
            }
            input.close();
            Log.d(TAG, "HTTP Response: " + httpResponse.toString());
        } finally {
            if (is != null)
                is.close();
        }
        return httpResponse.toString();
    }
}
