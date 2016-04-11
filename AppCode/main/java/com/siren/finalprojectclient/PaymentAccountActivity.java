/**
 * account payment activity, use to make payment
 */
package com.siren.finalprojectclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.ObjectInputStream;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import ParkingMeterSource.AllHandler.HandlerWrite;

public class PaymentAccountActivity extends AppCompatActivity {
    EditText rfid;
    EditText psw;
    int meterID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // go back to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        meterID = intent.getIntExtra("id",100);

        rfid = (EditText) findViewById(R.id.payment_rfid);
        psw = (EditText) findViewById(R.id.payment_psw);
    }

    /**
     * click the submit button
     * @param view
     */
    public void submit (View view) {
        byte[] encryptBytes;
        String password = "";
        char meterid = (char)meterID;

        String send = "cp" + meterid + "@" + rfid.getText() + "@";

        //encrypt
        encryptBytes = encryptPassword(psw.getText().toString());
        password = parseByteToHexStr(encryptBytes);

        send = send + password + "@#";

        new HandlerWrite().send(send);
    }

    /**
     * use RSA to encrypt the password
     * @param password
     * @return
     */
    public byte[] encryptPassword (String password){
        Log.d("thread", "do encrypt");
        try {
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");

            ObjectInputStream inputStream = null;
            inputStream = new ObjectInputStream(getResources().getAssets().open("publickey"));
            RSAPublicKey publickey = (RSAPublicKey) inputStream.readObject();

            inputStream.close();

            cipher.init(Cipher.ENCRYPT_MODE, publickey);
            byte[] resultBytes = cipher.doFinal(password.getBytes());


            return resultBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // parse byte to hex string
    public String parseByteToHexStr (byte buf[]) {
        StringBuffer sb = new StringBuffer();

        for (int i=0; i<buf.length;i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * action bar menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.payment_account_menu, menu);
        return true;
    }

    /**
     * action bar menu click
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.payment_credit) {
            Intent intent = new Intent(getApplicationContext(), PaymentCreditActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
