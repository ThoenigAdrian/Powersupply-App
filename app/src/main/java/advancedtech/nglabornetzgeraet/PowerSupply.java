package advancedtech.nglabornetzgeraet;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PowerSupply extends AppCompatActivity {

    static final Boolean powerSupplyConnectionReady = true;
    static final Boolean currentlyUpdating = false;
    private Handler updateChannelHandler = new Handler();
    Long allowedMsBetweenClicksBeforeUpdating = 600L;
    Channel previousChannel1 = new Channel();
    Channel previousChannel2 = new Channel();
    ArrayList<Button> voltageCurrentButtons = new ArrayList<>();
    HighLevelCommunicationInterface hlc = HighLevelCommunicationInterface.getInstance();
    Channel channel1 = new Channel();
    Channel channel2 = new Channel();
    Beacon passedPowerSuppyLnfo;
    Long latestChange = -1L;

    private Runnable channelUpdater = new Runnable() {
        @Override
        public void run() {
            int numberOfThingsChanged = 0;
            boolean voltageChannel1 = false;
            boolean currentChannel1 = false;
            boolean voltageChannel2 = false;
            boolean currentChannel2 = false;
            asyncSendToPowerSupply a = new asyncSendToPowerSupply();
            if(!channel1.getVoltage().equals(previousChannel1.getVoltage())) {
                voltageChannel1 = true;
                numberOfThingsChanged++;
            }
            if(!channel1.getCurrent().equals(previousChannel1.getCurrent())) {
                currentChannel1 = true;
                numberOfThingsChanged++;
            }
            if(!channel2.getVoltage().equals(previousChannel2.getVoltage())) {
                voltageChannel2 = true;
                numberOfThingsChanged++;
            }
            if(!channel2.getCurrent().equals(previousChannel2.getCurrent())) {
                currentChannel2 = true;
                numberOfThingsChanged++;
            }

            if(numberOfThingsChanged == 0)
                return;
            else if(numberOfThingsChanged == 1) {
                if (voltageChannel1)
                    setVoltage(1, channel1.voltage);
                else if (voltageChannel2)
                    setVoltage(2, channel2.voltage);
                else if (currentChannel1)
                    setCurrent(1, channel1.current);
                else if (currentChannel2)
                    setCurrent(2, channel2.current);
            }
            else if(numberOfThingsChanged > 1){
                    setAll();
            }
            previousChannel1.voltage = channel1.voltage;
            previousChannel2.voltage = channel2.voltage;
            previousChannel1.current = channel1.current;
            previousChannel2.current = channel2.current;
        }
    };



    private class asyncSendToPowerSupply extends AsyncTask<String, Void, String>
    {
        String powerSupplyRequest;
        @Override
        protected String doInBackground(String... params) {
            powerSupplyRequest = params[0];
            synchronized (powerSupplyConnectionReady) {
                return hlc.sendMessageToPowerSupply(powerSupplyRequest);
            }
        }
        @Override
        protected void onPostExecute(String powerSupplyResponse) {
            onMessageSent(powerSupplyRequest, powerSupplyResponse);
        }
    }

    private class connectToPowerSupply extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void[] params) {
            return hlc.connectToPowerSupply(passedPowerSuppyLnfo);
        }
        @Override
        protected void onPostExecute(String result) {
            if(hlc.isPowerSupplyConnected())
                onConnectionSuccessfull(result);
            else
                onConnectionFailed(result);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_supply);
        Button increase_current_channel_1 = (Button) findViewById(R.id.increase_current_channel_1);
        Button increase_voltage_channel_1 = (Button) findViewById(R.id.increase_voltage_channel_1);
        Button increase_voltage_channel_2 = (Button) findViewById(R.id.increase_voltage_channel_2);
        Button increase_current_channel_2 = (Button) findViewById(R.id.increase_current_channel_2);
        Button decrease_current_channel_1 = (Button) findViewById(R.id.decrease_current_channel_1);
        Button decrease_current_channel_2 = (Button) findViewById(R.id.decrease_current_channel_2);
        Button decrease_voltage_channel_1 = (Button) findViewById(R.id.decrease_voltage_channel_1);
        Button decrease_voltage_channel_2 = (Button) findViewById(R.id.decrease_voltage_channel_2);
        voltageCurrentButtons.add(increase_current_channel_1);
        voltageCurrentButtons.add(increase_voltage_channel_1);
        voltageCurrentButtons.add(increase_voltage_channel_2);
        voltageCurrentButtons.add(increase_current_channel_2);
        voltageCurrentButtons.add(decrease_current_channel_1);
        voltageCurrentButtons.add(decrease_current_channel_2);
        voltageCurrentButtons.add(decrease_voltage_channel_1);
        voltageCurrentButtons.add(decrease_voltage_channel_2);

        passedPowerSuppyLnfo = new Beacon((getIntent().getExtras().getString("beaconInfo")));
        connectToPowerSupply asyncConnectTask = new connectToPowerSupply();
        asyncConnectTask.execute();

    }

    public void onMessageSent(String powerSupplyRequest, String powerSupplyResponse){
        JSONObject request;
        String command = "";
        try{
            request = new JSONObject(powerSupplyRequest);
            command = request.getString("command");
        } catch(JSONException e){}

        if(command.equals("set_voltage") || command.equals("set_current") || command.equals("set_all") || command.equals("get_all_data"))
            setAllData(powerSupplyResponse);



    }

    public void onConnectionFailed(String reason){
        Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG).show();
        finish();
    }

    public void onConnectionSuccessfull(String connectionAcceptResponse){

        for(Button controlButton : voltageCurrentButtons){
            controlButton.setClickable(true);
        }

        Button connectionStatus = (Button) findViewById(R.id.connectionStatus);
        connectionStatus.setText(R.string.connected_string);
        connectionStatus.setBackgroundColor(Color.parseColor("#0ece1b"));
        EditText liveDataChannel1 = (EditText) findViewById(R.id.live_data_content_channel1);
        EditText liveDataChannel2 = (EditText) findViewById(R.id.live_data_content_channel2);
        liveDataChannel1.setVisibility(View.VISIBLE);
        liveDataChannel2.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.voltage_channel_1)).setTextColor(Color.BLACK);
        ((TextView) findViewById(R.id.voltage_channel_2)).setTextColor(Color.BLACK);
        ((TextView) findViewById(R.id.current_channel_1)).setTextColor(Color.BLACK);
        ((TextView) findViewById(R.id.current_channel_2)).setTextColor(Color.BLACK);
        setAllData(connectionAcceptResponse);

    }

    public void onClickInreacseVoltageChannel1(View v){
        updateChannelHandler.removeMessages(0);
        channel1.voltage += 0.1f;
        ((TextView) findViewById(R.id.voltage_channel_1)).setText(channel1.getVoltage());
        latestChange = System.currentTimeMillis();
        updateChannelHandler.postDelayed(channelUpdater, allowedMsBetweenClicksBeforeUpdating);
    }

    public void onClickInreacseCurrentChannel1(View v){
        updateChannelHandler.removeMessages(0);
        channel1.current += 0.1f;
        ((TextView) findViewById(R.id.current_channel_1)).setText(channel1.getCurrent());
        latestChange = System.currentTimeMillis();
        updateChannelHandler.postDelayed(channelUpdater, allowedMsBetweenClicksBeforeUpdating);
    }

    public void onClickInreacseVoltageChannel2(View v){
        updateChannelHandler.removeMessages(0);
        channel2.voltage += 0.1f;
        ((TextView) findViewById(R.id.voltage_channel_2)).setText(channel2.getVoltage());
        latestChange = System.currentTimeMillis();
        updateChannelHandler.postDelayed(channelUpdater, allowedMsBetweenClicksBeforeUpdating);
    }

    public void onClickInreacseCurrentChannel2(View v){
        updateChannelHandler.removeMessages(0);
        channel2.current += 0.1f;
        ((TextView) findViewById(R.id.current_channel_2)).setText(channel2.getCurrent());
        latestChange = System.currentTimeMillis();
        updateChannelHandler.postDelayed(channelUpdater, allowedMsBetweenClicksBeforeUpdating);
    }


    private void setAll(){
        asyncSendToPowerSupply a = new asyncSendToPowerSupply();
        String command = "set_all";
        JSONObject message = new JSONObject();
        try{
            message.put("command", command);

            JSONObject channel1_data = new JSONObject();
            channel1_data.put("voltage", channel1.getVoltage());
            channel1_data.put("voltage", channel1.getCurrent());

            JSONObject channel2_data = new JSONObject();
            channel2_data.put("voltage", channel2.getVoltage());
            channel2_data.put("voltage", channel2.getCurrent());

            message.put("channel1", channel1_data);
            message.put("channel2", channel2_data);
        } catch (JSONException e){

        }
        a.execute(message.toString());

    }

    private void setVoltage(Integer channelNr, Float voltage){
        asyncSendToPowerSupply a = new asyncSendToPowerSupply();
        String command = "set_voltage";
        JSONObject message = new JSONObject();
        try{
            message.put("command", command);
            message.put("channel", channelNr.toString());
            message.put("value", String.format(java.util.Locale.US, "%.1f", voltage));
        } catch (JSONException e) {

        }
        a.execute(message.toString());
    }

    private void setCurrent(Integer channelNr, Float current){
        asyncSendToPowerSupply a = new asyncSendToPowerSupply();
        String command = "set_current";
        JSONObject message = new JSONObject();
        try{
            message.put("command", command);
            message.put("channel", channelNr.toString());
            message.put("value", String.format(java.util.Locale.US, "%.1f", current));
        } catch (JSONException e) {

        }
        a.execute(message.toString());
    }

    private class Channel{
        public Float voltage;
        public Float current;

        public Channel(Float voltage, Float current){
            this.voltage = voltage;
            this.current = current;
        }

        public Channel(){

        }

        public String getVoltage() {
            return String.format(java.util.Locale.US, "%.1f", voltage);
        }

        public String getCurrent() {
            return String.format(java.util.Locale.US, "%.1f", current);
        }
    }

    public  void setAllData(String resp)
    {
        try {
            JSONObject reader = new JSONObject(resp);
            JSONObject data = reader.getJSONObject("data");
            EditText liveDataChannel1 = (EditText) findViewById(R.id.live_data_content_channel1);
            EditText liveDataChannel2 = (EditText) findViewById(R.id.live_data_content_channel2);


            String current = data.getString("live_current_channel_1");
            String voltage = data.getString("live_voltage_channel_1");

            channel1.current = Float.parseFloat(current);
            channel1.voltage = Float.parseFloat(voltage);
            previousChannel1.voltage = Float.parseFloat(current);
            previousChannel1.current = Float.parseFloat(voltage);


            String power = ((Float) (Float.parseFloat(voltage) * Float.parseFloat(current))).toString();
            liveDataChannel1.setText("Leistung: " + power + "W\n" + "Spannung: " + voltage + "V\n" +
                    "Strom: " + current + "A\n");

            current = data.getString("live_current_channel_2");
            voltage = data.getString("live_voltage_channel_2");
            channel2.current = Float.parseFloat(current);
            channel2.voltage = Float.parseFloat(voltage);
            previousChannel2.voltage = Float.parseFloat(current);
            previousChannel2.current = Float.parseFloat(voltage);

            power = ((Float) (Float.parseFloat(voltage) * Float.parseFloat(current))).toString();
            liveDataChannel2.setText("Leistung: " + power + "W\n" + "Spannung: " + voltage + "V\n" +
                    "Strom: " + current + "A\n");
        } catch (JSONException e) {
            Toast.makeText(this, "Error during setAllData" + e.toString(), Toast.LENGTH_LONG).show();
        } catch (NullPointerException e){
            Toast.makeText(this, "Error during setAllData" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
