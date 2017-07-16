package advancedtech.nglabornetzgeraet;

import android.net.wifi.WifiManager;
import android.provider.ContactsContract;

import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Created by Adrian on 15.07.2017.
 */

public class HighLevelCommunicationInterface {
    private LowLevelCommunication communicationInterface;

    public HighLevelCommunicationInterface(Beacon powerSupplyBeacon){
        communicationInterface = new LowLevelCommunication(powerSupplyBeacon);
    }

    public String sendMessageToPowerSupply(String message){
        communicationInterface.sendMessageToPowerSupply(message);
    }
}
