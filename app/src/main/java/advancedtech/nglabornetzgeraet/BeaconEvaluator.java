package advancedtech.nglabornetzgeraet;

import java.util.Map;

/**
 * Created by Adrian on 16.07.2017.
 */

public class BeaconEvaluator {
    BeaconList validBeaconList = new BeaconList();
    HighLevelCommunicationInterface hlc = HighLevelCommunicationInterface.getInstance();
    private Integer validTimeDifferenceMs = 30000;

    public BeaconEvaluator(){

    }

    private boolean beaconAgeOK(Beacon b){
        return Math.abs((System.currentTimeMillis() - b.timestamp)) < validTimeDifferenceMs;
    }

    private void updateBeacons(){
        for(Map.Entry<Integer, Beacon>  cleanupCandidate : validBeaconList.entrySet()) {
            if (!beaconAgeOK(cleanupCandidate.getValue()))
                validBeaconList.remove(cleanupCandidate.getKey());
        }
        for(Map.Entry<Integer, Beacon>  potentialEntry:hlc.getBeacons().entrySet()){
            if(validBeaconList.containsKey(potentialEntry.getKey())){
                Beacon currentEntry = validBeaconList.get(potentialEntry.getKey());
                if(currentEntry.timestamp > validBeaconList.get(potentialEntry.getKey()).timestamp && beaconAgeOK(potentialEntry.getValue())) { // should always be the case, just in case layer below doesn't order the beacons chronologically
                    validBeaconList.put(potentialEntry.getKey(), potentialEntry.getValue());
                }
            }
            else if(beaconAgeOK(potentialEntry.getValue())){
                validBeaconList.put(potentialEntry.getKey(), potentialEntry.getValue());
            }
        }
    }

    public BeaconList getAvailablePowerSupplies(){
        updateBeacons();
        return validBeaconList;
    }
}
