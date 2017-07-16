package advancedtech.nglabornetzgeraet;

import org.json.JSONObject;

/**
 * Created by Adrian on 12.07.2017.
 */

public class Beacon {
    String name;
    String version;
    String ip;
    int port;
    int ID;
    int timestamp;
    String connectionType;

    Beacon(JSONObject receivedBeacon){

    }
}
