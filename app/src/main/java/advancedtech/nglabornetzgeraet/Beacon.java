package advancedtech.nglabornetzgeraet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adrian on 12.07.2017.
 */

public class Beacon {
    String name;
    String version;
    String ip;
    Integer port;
    Integer ID;
    Long timestamp;
    String serverIP;
    String bluetoothAddress;
    String connectionType;

    Beacon(String name, String version, String ip, Integer port, Integer ID, Long timestamp, String serverIP, String bluetoothAddress, String connectionType){
        this.name = name;
        this.version = version;
        this.ip = ip;
        this.port = port;
        this.ID = ID;
        this.timestamp = timestamp;
        this.serverIP = serverIP;
        this.bluetoothAddress = bluetoothAddress;
        this.connectionType = connectionType;
    }

    Beacon(String jsonBeacon, String connectionType){
        try {
            JSONObject receivedBeacon = new JSONObject(jsonBeacon);

            name = receivedBeacon.getString("name");
            version = receivedBeacon.getString("version");
            ID = Integer.parseInt(receivedBeacon.getString("id"));
            timestamp = System.currentTimeMillis();
            this.connectionType = connectionType;

            if(connectionType.equals("WIFI")){
                ip = receivedBeacon.getString("ip");
                port = Integer.parseInt(receivedBeacon.getString("port"));
            }

            else if(connectionType.equals("Bluetooth"))
                bluetoothAddress = receivedBeacon.getString("bluetooth_address");

            else if(connectionType.equals("Server"))
                serverIP = receivedBeacon.getString("server_ip");

        } catch(JSONException e){
            throw new IllegalArgumentException("unexpected parsing error", e); // java won't allow to rethrow JSONException ... seriously !?!
        }
    }

    Beacon(String jsonString){
        try {
            JSONObject receivedBeacon = new JSONObject(jsonString);

            name = receivedBeacon.getString("name");
            version = receivedBeacon.getString("version");
            ip = receivedBeacon.getString("ip");
            port = Integer.parseInt(receivedBeacon.getString("port"));
            ID = Integer.parseInt(receivedBeacon.getString("ID"));
            timestamp = Long.parseLong(receivedBeacon.getString("timestamp"));
            serverIP = receivedBeacon.getString("serverIP");
            bluetoothAddress = receivedBeacon.getString("bluetoothAddress");
            connectionType = receivedBeacon.getString("connectionType");
        } catch(JSONException e){
            throw new IllegalArgumentException("unexpected parsing error", e); // java won't allow to rethrow JSONException ... seriously !?!
        }
    }

    public String toJsonString(){
        String jsonString = "{";
        jsonString += jsonEntry("name", name, true);
        jsonString += jsonEntry("version", version);
        jsonString += jsonEntry("ip", ip);
        jsonString += jsonEntry("port", port.toString());
        jsonString += jsonEntry("ID", ID.toString());
        jsonString += jsonEntry("timestamp", timestamp.toString());
        jsonString += jsonEntry("serverIP", serverIP);
        jsonString += jsonEntry("bluetoothAddress", bluetoothAddress);
        jsonString += jsonEntry("connectionType", connectionType);
        return jsonString + "}";
    }

    private String jsonEntry(String name, String value){
        return ", \"" + name + "\": " + "\"" + value + "\" ";
    }

    private String jsonEntry(String name, String value, boolean firstEntry){
        if(firstEntry)
            return "\"" + name + "\": " + "\"" + value + "\" ";
        else
            return ", \"" + name + "\": " + "\"" + value + "\" ";
    }

}
