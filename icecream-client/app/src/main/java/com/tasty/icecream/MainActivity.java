package com.tasty.icecream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    TextView tvStep1, tvStep2, tvStep3;
    int[] flavours = {R.id.btnDarkChocMint, R.id.btnMatchaGreen, R.id.btnPumpkinPie, R.id.btnStrawberryBasil};
    String selectedFlavour;
    String selectedStyle;
    String totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedFlavour="";
        selectedStyle="";
        totalAmount="";
        setTextViewFont();

        showPopupDialog(MainActivity.this);
    }

    private void showPopupDialog(final Context context) {
        String serverIP = System.getProperty("SERVER_IP");
        String serverPort = System.getProperty("SERVER_PORT");
        Log.d("SYSTEM_PROP_CALL", "> IP: " + serverIP + ", PORT: " + serverPort);
        if(serverIP == null || serverPort == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LayoutInflater li = LayoutInflater.from(context);
                    View vModalDialog = li.inflate(R.layout.modal_dialog, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(vModalDialog);

                    final EditText userServerIP = (EditText) vModalDialog.findViewById(R.id.etServerIP);
                    final EditText userServerPort = (EditText) vModalDialog.findViewById(R.id.etServerPort);

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String ipInput = userServerIP.getText().toString();
                                            String portInput = userServerPort.getText().toString();
                                            if(!ipInput.isEmpty() && !portInput.isEmpty()) {
                                                System.setProperty("SERVER_IP", ipInput);
                                                System.setProperty("SERVER_PORT", portInput);
                                                Log.d("SYSTEM_PROP", "> IP: " + ipInput + ", PORT: " + portInput);
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }, 1000);
        }
    }

    private void setTextViewFont() {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Schalk.ttf");
        tvStep1 = (TextView) findViewById(R.id.tvStep1);
        tvStep3 = (TextView) findViewById(R.id.tvStep3);
        tvStep2 = (TextView) findViewById(R.id.tvStep2);
        tvStep1.setTypeface(custom_font);
        tvStep2.setTypeface(custom_font);
        tvStep3.setTypeface(custom_font);
    }

    // Handles the onClick functionality for the Style icons
    public void selectFlavour(View v) {
        selectedFlavour = v.isActivated() ? "" : (String) v.getContentDescription();
        v.setActivated(!v.isActivated()); // Toggle on/off

        for (int flavour : flavours) {  // Deactivate the other imageButtons if activated
            if (v.getId() != flavour) {
                View altView = findViewById(flavour);
                altView.setActivated(false);
            }
        }
    }

    // Handles the onClick functionality for the Style icons
    public void selectStyle(View v) {
        switch (v.getId()) {
            case R.id.btnWaffleCone:
                v.setActivated(!v.isActivated()); // Toggle on/off
                selectedStyle = (String) v.getContentDescription();
                totalAmount = "5.00";
                findViewById(R.id.btnBowl).setActivated(false);
                break;
            case R.id.btnBowl:
                v.setActivated(!v.isActivated());
                selectedStyle = (String) v.getContentDescription();
                totalAmount = "4.50";
                findViewById(R.id.btnWaffleCone).setActivated(false);
                break;
        }
    }

    public void selectCheckout(View v) {
        if(validateSelections()) {
            Intent i = new Intent(getApplicationContext(), CheckoutActivity.class);
            i.putExtra("selected-flavour", selectedFlavour);
            i.putExtra("selected-style", selectedStyle);
            i.putExtra("total-amount", totalAmount);
            startActivity(i);
            finish();
        } else
            Toast.makeText(getBaseContext(), "Choose flavor/style", Toast.LENGTH_SHORT).show();
    }

    private boolean validateSelections() {
        return (!(selectedFlavour.isEmpty() || selectedStyle.isEmpty()));
    }
}