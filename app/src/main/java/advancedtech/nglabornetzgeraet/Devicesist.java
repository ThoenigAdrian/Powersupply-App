package advancedtech.nglabornetzgeraet;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Devicesist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicesist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_devicesist, menu);
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

    public void settingsButtonOnClick(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public boolean wifiEnabled(){
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifi != null)
            return wifi.isWifiEnabled();
        else
            return false;
    }

    public boolean wifiAdapterAvailable(){
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifi == null)
            return false;
        else
            return true;
    }

    public boolean bluetoothAdapterAvailable(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        else{
            return true;
        }
    }

    public boolean bluetoothConnectivityAvailable(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

        } else {
            if (bluetoothAdapter.isEnabled()) {
                return true;
            }
        }

        return false;
    }

    public void onConnectingScreenButtonClick(View view) {
        if(bluetoothAdapterAvailable())
            Toast.makeText(this, "Bluetooth Adapter Available", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Bluetooth Adapter Not Available", Toast.LENGTH_LONG).show();
        if(bluetoothConnectivityAvailable())
            Toast.makeText(this, "Bluetooth Connectivity Available", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Bluetooth Connectivity Not Available", Toast.LENGTH_LONG).show();
        if (wifiAdapterAvailable())
            Toast.makeText(this, "Wifi Adapter Available", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Wifi Adapter not available", Toast.LENGTH_LONG).show();
        if (wifiEnabled())
            Toast.makeText(this, "Wifi Enabled", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Wifi Disabled", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, PowerSupplyConnecting.class);
        startActivity(intent);
    }

    // Reading Settings
    /*
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String setting = prefs.getString("key_aus_xml", "Default Value , maybe if nothing is there");
    Toast.makeText(this, setting, Toast.LENGTH_LONG).show();
    */
}
