package edu.hanover.odometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ServiceConfigurationError;

public class MainActivity extends AppCompatActivity {

    private OdometerService odometer;
    private boolean bound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        //Need to define ServiceConnection
        public void onServiceConnected(ComponentName componentname, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder =
                    (OdometerService.OdometerBinder) binder;
            //Get a reference to the OdometerService when the service is connected
            odometer = odometerBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Call the watchMileage() function when the activity's created
        watchMileage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Bind the service when the activity starts
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    //Unbind the service when the activity stops
    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    //Update the mileage that's displayed
    private void watchMileage() {
        final TextView distanceView = (TextView)findViewById(R.id.distance);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (odometer != null) {
                    distance = odometer.getMiles();
                }
                String distanceStr = String.format("%1$,.2f miles", distance);
                distanceView.setText(distanceStr);
                //Update distance every second
                handler.postDelayed(this, 1000);
            }
        });
    }
}
