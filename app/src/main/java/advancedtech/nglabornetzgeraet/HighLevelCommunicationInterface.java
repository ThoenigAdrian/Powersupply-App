package advancedtech.nglabornetzgeraet;

import android.content.Context;

/**
 * Created by Adrian on 16.07.2017
 */

public class HighLevelCommunicationInterfacee {
    private static final HighLevelCommunicationInterfacee ourInstance = new HighLevelCommunicationInterfacee();
    private LowLevelCommunication communicationInterface;
    private boolean powerSupplyConnected = false;
    Context ctx;

    public static HighLevelCommunicationInterfacee getInstance() {
        return ourInstance;
    }

    private HighLevelCommunicationInterfacee() {

    }

    public BeaconList getBeacons(){
        return communicationInterface.getBeacons();
    }

    public String connectToPowerSupply(Beacon beacon){
        return communicationInterface.connectToPowerSupply(beacon);
    }

    public void setContext(Context context){
        ctx = context;
        communicationInterface = new LowLevelCommunication((ctx));
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
