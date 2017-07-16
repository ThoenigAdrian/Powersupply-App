package advancedtech.nglabornetzgeraet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by Adrian on 16.07.2017.
 */

public class LowLevelCommunication {

    private BeaconList beacons;
    private ResultList results;
    private Socket PowerSupplyConnection;
    private String powerSupplyIPAddress;

    // This is a bluetooth UUID which can freely be choosen to identify applications
    // In this case all Power Supplies will use the same UUID so that the app can distinguish them
    // from other Bluetooth Devices
    private String powerSupplyUUID = "57891357-1150-1039-2722-1580520B34FB";

    private DatagramSocket udpBeaconListener;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> foundBluetoothDevices;
    private int powerSupplyPort;
    private Handler beaconHandler;
    private int sendTimeout = 5;
    private boolean error_occured = false;
    private String error_string = "";
    private Context ApplicationContext;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundBluetoothDevices.add(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if (!foundBluetoothDevices.isEmpty()){
                    BluetoothDevice device = foundBluetoothDevices.remove(0);
                    device.fetchUuidsWithSdp();
                }
            } else if (BluetoothDevice.ACTION_UUID.equals(action)){
                BluetoothDevice deviceExtra = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);
                if(uuidExtra != null){
                    for (Parcelable p : uuidExtra){
                        Parcelable a = p;
                        if (p.toString() == powerSupplyUUID){
                            Beacon beacon = new Beacon();
                            beacons.add(beacon);
                        }
                    }
                }
            }
        }
    };

    WifiManager.WifiLock wifiLock;
    WifiManager.MulticastLock wifiMulticastLock;

    public LowLevelCommunication(Beacon powerSupplyBeacon, Context ctx){
        ApplicationContext = ctx;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
        startCollectingBeacons();
        initializeWlan();
    }

    private Runnable beaconCollector = new Runnable() {
        @Override
        public void run() {
            try {
                collectBeacons(); //this function can change value of refreshDeviceListPeriod.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                int refreshDeviceListPeriod = 2000;
                beaconHandler.postDelayed(beaconCollector, refreshDeviceListPeriod);
            }
        }
    };

    private void initializeWlan()
    {
        WifiManager wlan_manager = (WifiManager) ApplicationContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wlan_manager !=null){
            wifiLock = wlan_manager.createWifiLock("nglabornetzgeraetlock");
            wifiLock.acquire();
            wifiMulticastLock = wlan_manager.createMulticastLock("nglabornetzgeraetlock2");
            wifiMulticastLock.setReferenceCounted(false);
            wifiMulticastLock.acquire();
        }
        try
        {
            udpBeaconListener = new DatagramSocket(5455);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
    }

    void startCollectingBeacons() {
        beaconCollector.run();
    }

    void stopCollectingBeacons() {
        beaconHandler.removeCallbacks(beaconCollector);
    }

    //stopCollectingBeacons();

    private void collectBeacons()
    {
        byte[] receivedata = new byte[1024];
        DatagramPacket recv_packet = new DatagramPacket(receivedata, receivedata.length);
        try{
            udpBeaconListener.setSoTimeout(100);
        }
        catch (SocketException e)
        {
            //Toast.makeText(this, "Can't set udp timeout for whatever reason : " + e.toString(), Toast.LENGTH_LONG).show();
        }
        while(true) {
            try {
                udpBeaconListener.receive(recv_packet);
                String recv_string = new String(recv_packet.getData());
                JSONObject reader;
                try {
                    reader = new JSONObject(recv_string);
                    beacons.add(new Beacon(reader));
                } catch (JSONException e) {
                    //Toast.makeText(this, "Can't set udp timeout for whatever reason : " + e.toString(), Toast.LENGTH_LONG).show();
                }


            } catch (SocketTimeoutException e) {
                break;
            } catch (IOException e) {
                //Toast.makeText(this, "Can't receive from UDP Socket becuase" + e.toString(), Toast.LENGTH_LONG).show();
                break;
            }
        }
    }



    public BeaconList getBeacons() {
        return beacons;
    }

    public ResultList getResults() {
        return results;
    }

    public void sendMessageToPowerSupply(String message, callbackInterface cI){
        // do stuff
        cI.onReceivedResponseFromPowerSupply();

    }
    public void sendMessageToPowerSupply(String message){
        // do stuff

    }

    public int connect_to_power_supply()
    {

    }

    private void bluetoothSend(String message){

    }

    private void wifiSend(String message){

    }
}
