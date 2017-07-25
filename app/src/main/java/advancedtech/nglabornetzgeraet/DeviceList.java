package advancedtech.nglabornetzgeraet;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class DeviceList extends AppCompatActivity{

    private ArrayList<String> data = new ArrayList<>();
    private HighLevelCommunicationInterface communicationInterface;
    public BeaconEvaluator beaconEvaluator = new BeaconEvaluator();
    private android.os.Handler deviceListUpdateHandler = new android.os.Handler();
    private ArrayList<Beacon> availablePowerSupplies = new ArrayList<>();
    private ListView deviceList;

    private Runnable deviceListUpdater = new Runnable() {
        @Override
        public void run() {
            try {
                updateDeviceList();
            } finally {
                int refreshDeviceListPeriod = 1000;
                deviceListUpdateHandler.postDelayed(deviceListUpdater, refreshDeviceListPeriod);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        communicationInterface = HighLevelCommunicationInterface.getInstance();
        setContentView(R.layout.activity_devicesist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceList = (ListView) findViewById(R.id.listview);
        deviceList.setAdapter(new powerSupplyListAdapter(this, R.layout.power_supply_list_item, data));

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deviceListUpdateHandler.removeMessages(0);
                Intent intent = new Intent(DeviceList.this, PowerSupply.class);
                intent.putExtra("beaconInfo", availablePowerSupplies.get(position).toJsonString()); // makes the selected Power Supply Info available for the next Activity
                startActivity(intent);
            }
        });
        communicationInterface.setContext(getApplicationContext());
        deviceListUpdateHandler.post(deviceListUpdater); // updates the deviceList
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        deviceListUpdateHandler.removeMessages(0);
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        deviceListUpdateHandler.post(deviceListUpdater);
    }



    public void onConnect(View view) {
        if(communicationInterface.isBluetoothAdapterAvailable())
            Toast.makeText(this, "Bluetooth Adapter Available", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Bluetooth Adapter Not Available", Toast.LENGTH_LONG).show();
        if(communicationInterface.isBluetoothEnabled())
            Toast.makeText(this, "Bluetooth Connectivity Available", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Bluetooth Connectivity Not Available", Toast.LENGTH_LONG).show();
        if (communicationInterface.isWifiAdapterAvailable())
            Toast.makeText(this, "Wifi Adapter Available", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Wifi Adapter not available", Toast.LENGTH_LONG).show();
        if (communicationInterface.isWifiEnabled())
            Toast.makeText(this, "Wifi Enabled", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Wifi Disabled", Toast.LENGTH_LONG).show();

    }

    public void updateDeviceList(){
        Log.v("performanceDebugging", "startUpdate");
        availablePowerSupplies = new ArrayList<>();
        Beacon bluetoothFake = new Beacon("{\"name\":\"Adrians Netzgeraet\", \"version\":\"1\", \"bluetooth_address\":\"DFDFJK-DFJKDJFL\", \"port\":\"2223\", \"id\": 175640}", "Bluetooth");
        Beacon serverFake = new Beacon("{\"name\":\"Adrians Netzgeraet\", \"version\":\"1\", \"server_ip\":\"192.168.175.1\", \"port\":\"2223\", \"id\": 175640}", "Server");
        availablePowerSupplies.add(bluetoothFake);
        availablePowerSupplies.add(serverFake);
        data.clear();

        for (Beacon powerSupply: beaconEvaluator.getAvailablePowerSupplies().values()){
            availablePowerSupplies.add(powerSupply);
        }
        for(Beacon beacon : availablePowerSupplies){
                data.add("Netzgerät (ID: " + beacon.ID + ")");
        }

        deviceList.setAdapter(new powerSupplyListAdapter(this, R.layout.power_supply_list_item, data));

        Log.v("performanceDebugging", "stopUpdate");
    }

    private class powerSupplyListAdapter extends ArrayAdapter<String> {
        private int layout;
        private powerSupplyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.powerSupplyInfo = (TextView) convertView.findViewById(R.id.powerSupplyInfo);

                if(availablePowerSupplies.get(position).connectionType.equals("WIFI")){
                    ImageView wifiSymbol = (ImageView) convertView.findViewById(R.id.connectionTypeSymbol);
                    Drawable wifiDrawable = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_wifi_connection_signal_symbol);
                    wifiSymbol.setImageDrawable(wifiDrawable);
                    viewHolder.connectionTypeSymbol = wifiSymbol;
                }
                else if(availablePowerSupplies.get(position).connectionType.equals("Bluetooth")){
                    ImageView bluetoothSymbol = (ImageView) convertView.findViewById(R.id.connectionTypeSymbol);
                    Drawable bluetoothDrawable = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_bluetooth);
                    bluetoothSymbol.setImageDrawable(bluetoothDrawable);
                    viewHolder.connectionTypeSymbol = bluetoothSymbol;
                }
                else if(availablePowerSupplies.get(position).connectionType.equals("Server")){
                    ImageView bluetoothSymbol = (ImageView) convertView.findViewById(R.id.connectionTypeSymbol);
                    Drawable serverDrawable = AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.ic_server_connection_type);
                    bluetoothSymbol.setImageDrawable(serverDrawable);
                    viewHolder.connectionTypeSymbol = bluetoothSymbol;
                }
                else{
                    viewHolder.connectionTypeSymbol = (ImageView) convertView.findViewById(R.id.connectionTypeSymbol);
                }

                viewHolder.powerSupplyInfo.setText(getItem(position));
                convertView.setTag(viewHolder);
            }

            else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                String info = getItem(position);
                mainViewHolder.powerSupplyInfo.setText(info);
            }

            return convertView;
        }
    }

    private class ViewHolder{
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
