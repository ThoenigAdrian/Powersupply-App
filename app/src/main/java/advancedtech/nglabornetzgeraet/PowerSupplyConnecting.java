package advancedtech.nglabornetzgeraet;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class PowerSupplyConnecting extends AppCompatActivity {


    ArrayList<Button> voltageCurrentButtons = new ArrayList<>();

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

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               onConnectionSuccessfull();
            }
        }, 2000);

    }

    public void onConnectionSuccessfull(){

        for(Button controlButton : voltageCurrentButtons){
            controlButton.setClickable(true);
        }

        Button connectionStatus = (Button) findViewById(R.id.connectionStatus);
        connectionStatus.setText(R.string.connected_string);
        connectionStatus.setBackgroundColor(Color.parseColor("#0ece1b"));
    }
}
