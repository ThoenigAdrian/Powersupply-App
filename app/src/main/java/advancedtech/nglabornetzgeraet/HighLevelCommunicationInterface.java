package advancedtech.nglabornetzgeraet;

import android.content.Context;

/**
 * Created by Adrian on 16.07.2017
 */

public class HighLevelCommunicationInterface {
    private static final HighLevelCommunicationInterface ourInstance = new HighLevelCommunicationInterface();
    private LowLevelCommunication communicationInterface;
    private boolean powerSupplyConnected = false;

    public static HighLevelCommunicationInterface getInstance() {
        return ourInstance;
    }

    private HighLevelCommunicationInterface() {

    }

    public BeaconList getBeacons(){
        return communicationInterface.getBeacons();
    }

    public String connectToPowerSupply(Beacon beacon){
        String result = communicationInterface.connectToPowerSupply(beacon);
        if(result.startsWith("couldn't connect to power Supply") || result.startsWith("Invalid")){
            powerSupplyConnected = false;
        }
        else{
            powerSupplyConnected = true;
        }
        return result;
    }

    public void setContext(Context context){
        if(communicationInterface==null)
            communicationInterface = new LowLevelCommunication((context));
    }

    public boolean isPowerSupplyConnected(){
        return powerSupplyConnected;
    }

    public boolean isWifiAdapterAvailable(){
        return communicationInterface.isWifiAdapterAvailable();
    }

    public boolean isWifiEnabled(){
        return communicationInterface.isWifiEnabled();
    }

    public boolean isBluetoothEnabled(){
        return communicationInterface.isBluetoothEnabled();
    }

    public boolean isBluetoothAdapterAvailable(){
        return communicationInterface.isBluetoothAdapterAvailable();
    }

    public String sendMessageToPowerSupply(String message){
        return communicationInterface.sendMessageToPowerSupply(message);
    }
}
