package advancedtech.nglabornetzgeraet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by Adrian on 16.07.2017.
 */

public class LowLevelCommunication {

    private BeaconList beacons = new BeaconList();
    private ResultList results;
    private Socket powerSupplyConnection;
    private String powerSupplyIPAddress;
    private String powerSupplybluetoothAddress;

    // This is a bluetooth UUID which can freely be choosen to identify applications
    // In this case all Power Supplies will use the same UUID so that the app can distinguish them
    // from other Bluetooth Devices
    private String powerSupplyUUID = "57891357-1150-1039-2722-1580520B34FB";

    private DatagramSocket udpBeaconListener;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> foundBluetoothDevices = new ArrayList<>();;
    private int powerSupplyPort;
    private int connectTimeoutMs = 10*1000;
    private int sendTimeoutSec = 5;
    private boolean error_occured = false;
    private String error_string = "";
    Thread beaconCollectorThread;
    private Context ApplicationContext;
    private boolean wifiAdapterAvailable = false;
    private boolean wifiEnabled = false;
    private boolean bluetoothAdapterAvailable = false;
    private boolean bluetoothEnabled = false;
    DataOutputStream powerSupplyOutputStream;
    BufferedReader powerSupplyInputStream;
    private String connectionType;

    private WifiManager.WifiLock wifiLock;
    private WifiManager.MulticastLock wifiMulticastLock;

    /*
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
                        Parcelable a = p; // Just a debug line
                        if (p.toString() == powerSupplyUUID){
                            Beacon beacon = new Beacon(deviceExtra.getName().split("Y")[0], "1", null, null, Integer.parseInt(deviceExtra.getName().split("Y")[1]), System.currentTimeMillis(), "", deviceExtra.getAddress(), "Bluetooth");
                            beacons.add(beacon);
                        }
                    }
                }
            }
        }
    };*/

    public LowLevelCommunication(Context ctx){
        ApplicationContext = ctx;
        updateConnectionPossibilities();
        initializeBluetooth();
        initializeWIFI();
        startCollectingBeacons();

    }

    public String connectToPowerSupply(Beacon powerSupplyBeacon){
        String result = "Invalid connection type";
        connectionType = powerSupplyBeacon.connectionType;
        if(connectionType.equals("WIFI")){
            powerSupplyPort = powerSupplyBeacon.port;
            powerSupplyIPAddress = powerSupplyBeacon.ip;
            result = connectToPowerSupplyViaWIFI();
        } else if (connectionType.equals("Bluetooth")){
            powerSupplybluetoothAddress = powerSupplyBeacon.bluetoothAddress;
            result = connectToPowerSupplyViaBluetooth();
        } else if(connectionType.equals("Server")){
            powerSupplyPort = powerSupplyBeacon.port;
            powerSupplyIPAddress = powerSupplyBeacon.ip;
            result = connectToPowerSupplyViaServer();
        }
        return result;
    }

    private void initializeBluetooth(){
        if(bluetoothAdapterAvailable){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.startDiscovery();
        }
    }

    private void updateConnectionPossibilities(){
        updateBluetoothAdapterAvailable();
        updateWifiAdapterAvailable();
        updateBluetoothEnabled();
        updateWifiEnabled();
    }

    private String connectToPowerSupplyViaServer() {
        return "NotImplementedYet";
    }

    private String connectToPowerSupplyViaBluetooth() {
        return "NotImplementedYet";

    }

    private String connectToPowerSupplyViaWIFI() {
        String result = "couldn't connect to power Supply";
        String response;

        try {
            powerSupplyConnection = new Socket();
            powerSupplyConnection.connect(new InetSocketAddress(powerSupplyIPAddress, powerSupplyPort) , connectTimeoutMs);
            powerSupplyOutputStream = new DataOutputStream(powerSupplyConnection.getOutputStream());
            powerSupplyInputStream = new BufferedReader(new InputStreamReader(powerSupplyConnection.getInputStream()));
        } catch (IOException e) {
            return result + e.toString();

        } catch (Exception e) {
            return result + e.toString();
        }

        try {
            response = powerSupplyInputStream.readLine();
            if (response == null) {
                return result + "couldn't read from tcp stream";
            }
        } catch (Exception e) {
            return result + e.toString();
        }
        return response;
    }


    public boolean isWifiAdapterAvailable(){
        return wifiAdapterAvailable;
    }

    public boolean isBluetoothAdapterAvailable(){
        return bluetoothAdapterAvailable;
    }

    public boolean isWifiEnabled(){
        return wifiEnabled;
    }

    public boolean isBluetoothEnabled(){
        return bluetoothEnabled;
    }


    private void updateWifiEnabled() {
        WifiManager wifi = (WifiManager) ApplicationContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiEnabled = wifi != null && wifi.isWifiEnabled();
    }

    private void updateWifiAdapterAvailable(){
        WifiManager wifi = (WifiManager) ApplicationContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiAdapterAvailable = wifi != null;
    }

    private void updateBluetoothAdapterAvailable(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapterAvailable = bluetoothAdapter != null;
    }

    private void updateBluetoothEnabled(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

        } else {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothEnabled = true;
            }
        }
        bluetoothEnabled =  false;
    }

    private Runnable beaconCollector = new Runnable() {
        @Override
        public void run() {
            try {
                 collectBeacons();
            } finally {
            }
        }
    };

    private void initializeWIFI()
    {
        WifiManager wifiManager = (WifiManager) ApplicationContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager !=null){
            wifiLock = wifiManager.createWifiLock("nglabornetzgeraetlock");
            wifiLock.acquire();
            wifiMulticastLock = wifiManager.createMulticastLock("nglabornetzgeraetlock2");
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
        beaconCollectorThread = new Thread(beaconCollector);
        beaconCollectorThread.start();
    }

    void stopCollectingBeacons() {
        beaconCollectorThread.interrupt();

    }


    private void collectBeacons()
    {
        byte[] receivedata = new byte[1024];
        DatagramPacket recv_packet = new DatagramPacket(receivedata, receivedata.length);

        while(true) {
            try {
                udpBeaconListener.receive(recv_packet);
                String recv_string = new String(recv_packet.getData());
                try {
                    Beacon b = new Beacon(recv_string, "WIFI");
                    beacons.put(b.ID, b);
                } catch (IllegalArgumentException e) {
                    Log.v("debug", "Error while parsing beacon: " + e.toString());
                }

            } catch (SocketTimeoutException e) {
                break;
            } catch (IOException e) {
                Log.v("Debug", "Can't receive from UDP Socket because" + e.toString());
            }
        }
    }


    public BeaconList getBeacons() {
        return beacons;
    }

    public ResultList getResults() {
        return results;
    }

    public String sendMessageToPowerSupply(String message){
        String response = "Invalid connection type";
        message += "\n";
        if(connectionType.equals("WIFI"))
            return sendMessageToPowerSupplyViaWIFI(message);
        else if (connectionType.equals("Bluetooth"))
            return sendMessageToPowerSupplyViaBluetooth(message);
        else if (connectionType.equals("Server"))
            return sendMessageToPowerSupplyViaServer(message);
        return response;
    }

    private String sendMessageToPowerSupplyViaWIFI(String message){
        String response;
        String result = "Couldn't send message";
        try {
            powerSupplyOutputStream.write(message.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(powerSupplyConnection.getInputStream()));
            response = reader.readLine();
            if (response == null) {
                return result + "couldn't read from tcp stream";
            }
        } catch (Exception e) {
            return result + e.toString();
        }
        return response;
    }

    private String sendMessageToPowerSupplyViaBluetooth(String message){
        return "NotImplementedYet";

    }

    private String sendMessageToPowerSupplyViaServer(String message){
        return "NotImplementedYet";
    }

}
