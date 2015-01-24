package net.randby.simon.positionrecorder;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.*;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Location:
        double latitude = -1;
        double longitude = -1;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        LocationListener location_Listener = new LocationListener() {
            public void onLocationChanged(Location loc) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(bestProvider, 0, 0, location_Listener);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }catch (NullPointerException e){
            latitude = -1;
            longitude = -1;
        }

//        Table inflation:
        TableLayout logTable = (TableLayout) findViewById(R.id.space_time_rows);
        LayoutInflater layoutInflater = getLayoutInflater();
        View theInflatedRow;

        for (int i = 0; i < 30; i++){
            theInflatedRow = layoutInflater.inflate(R.layout.table_log_entry, logTable, false);

            TextView idContent = (TextView)theInflatedRow.findViewById(R.id.id_column_content);
            idContent.setText("ID" + i);

            TextView xContent = (TextView)theInflatedRow.findViewById(R.id.x_coordinate_column_content);
            xContent.setText("X Pos: " + latitude);

            TextView yContent = (TextView)theInflatedRow.findViewById(R.id.y_coordinate_column_content);
            yContent.setText("Y Pos: " + longitude);

            TextView timeContent = (TextView)theInflatedRow.findViewById(R.id.time_column_content);
            timeContent.setText("T Pos: " + System.currentTimeMillis());

            logTable.addView(theInflatedRow);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
