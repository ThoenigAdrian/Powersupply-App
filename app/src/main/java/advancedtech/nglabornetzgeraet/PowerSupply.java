package advancedtech.nglabornetzgeraet;

import android.graphics.Color;
import android.os.AsyncTask;
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

public class PowerSupplyConnecting extends AppCompatActivity {


    ArrayList<Button> voltageCurrentButtons = new ArrayList<>();
    HighLevelCommunicationInterface hlc = HighLevelCommunicationInterface.getInstance();
    Beacon passedPowerSuppyLnfo;
    String response;

    private class asyncSendToPowerSupply extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            return hlc.sendMessageToPowerSupply(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            onMessageSent(s);
        }
    }

    private class connectToPowerSupply extends AsyncTask<Void,Void,Void> {

        private String error_string="";

        @Override
        protected Void doInBackground(Void[] params) {
            error_string = hlc.connectToPowerSupply(passedPowerSuppyLnfo);
            return null;
        }

        @Override
        protected void onPostExecute(Void a) {
            if(hlc.isPowerSupplyConnected())
                onConnectionSuccessfull();
            else
                onConnectionFailed(error_string);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_supply_connecting);
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

    public void onMessageSent(String response){

    }

    public void onConnectionFailed(String reason){
        Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG).show();
        finish();
    }

    public void onConnectionSuccessfull(){

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
        setAllData(response);

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
            String power = ((Float) (Float.parseFloat(voltage) * Float.parseFloat(current))).toString();
            liveDataChannel1.setText("Leistung: " + power + "W\n" + "Spannung: " + voltage + "V\n" +
                    "Strom: " + current + "A\n");

            current = data.getString("live_current_channel_2");
            voltage = data.getString("live_voltage_channel_2");
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
