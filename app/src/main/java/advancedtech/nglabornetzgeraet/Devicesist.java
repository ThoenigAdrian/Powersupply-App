package advancedtech.nglabornetzgeraet;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Devicesist extends AppCompatActivity{

    private ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicesist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lv = (ListView) findViewById(R.id.listview);
        generateListContent();
        lv.setAdapter(new MyListAdapter(this, R.layout.power_supply_list_item, data));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(Devicesist.this, PowerSupplyConnecting.class);
                startActivity(intent);
            }
        });

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(Devicesist.this, Settings.class);
        startActivity(intent);
    }

    private void generateListContent(){
        for(int i = 0; i < 10; i++){
            data.add("Netzgerät ID:  " + new Random().nextInt(999999));
        }
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

    public void onConnect(View view) {
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

    public void updateDeviceList(){

    }

    private class MyListAdapter extends ArrayAdapter<String> {
        private int layout;
        public MyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.powerSupplyIcon = (ImageView) convertView.findViewById(R.id.powerSupplyIcon);
                viewHolder.powerSupplyInfo = (TextView) convertView.findViewById(R.id.powerSupplyInfo);
                viewHolder.connectionTypeSymbol = (ImageView) convertView.findViewById(R.id.connectionTypeSymbol);

                convertView.setTag(viewHolder);
            }

            else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.powerSupplyInfo.setText(getItem(position));
            }

            return convertView;
        }
    }

    public class ViewHolder{
        ImageView powerSupplyIcon;
        TextView powerSupplyInfo;
        ImageView connectionTypeSymbol;
    }

    // Reading Settings
    /*
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String setting = prefs.getString("key_aus_xml", "Default Value , maybe if nothing is there");
    Toast.makeText(this, setting, Toast.LENGTH_LONG).show();
    */
}
