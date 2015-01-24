package net.randby.simon.positionrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TableLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void addTableEntry() {
        AwesomeLocationModule awesomeLocationModule = new AwesomeLocationModule().invoke();
        double latitude = awesomeLocationModule.getLatitude();
        double longitude = awesomeLocationModule.getLongitude();


//        Table inflation:
        TableLayout logTable = (TableLayout) findViewById(R.id.space_time_rows);
        LayoutInflater layoutInflater = getLayoutInflater();
        View theInflatedRow;


        theInflatedRow = layoutInflater.inflate(R.layout.table_log_entry, logTable, false);

        TextView idContent = (TextView)theInflatedRow.findViewById(R.id.id_column_content);
        idContent.setText("" + 1);

        TextView xContent = (TextView)theInflatedRow.findViewById(R.id.x_coordinate_column_content);
        xContent.setText("" + latitude);

        TextView yContent = (TextView)theInflatedRow.findViewById(R.id.y_coordinate_column_content);
        yContent.setText("" + longitude);

        TextView timeContent = (TextView)theInflatedRow.findViewById(R.id.time_column_content);
        timeContent.setText("" + System.currentTimeMillis());

        logTable.addView(theInflatedRow);

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

    /**Remove all current entries (use with care!)*/
    public void flushSpaceTimeCoordinates(View view){

        new AlertDialog.Builder(this)
                .setTitle("Flush Log")
                .setMessage("Are you sure you want to delete all the entries in the current log?")
                .setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TableLayout spaceTimeRows = (TableLayout) findViewById(R.id.space_time_rows);
                        spaceTimeRows.removeAllViews();
                    }
                })
                .setNegativeButton("No, abort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just nothing?
                    }
                })
                .show();


    }

    /**Add new entry when the trigger has been clicked*/
    public void triggerSpaceTimeRecording(View view){
        addTableEntry();
    }

    private class AwesomeLocationModule {
        private double latitude;
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public AwesomeLocationModule invoke() {
            //        Location:
            latitude = -1;
            longitude = -1;
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setBearingRequired(true);

            String bestProvider = locationManager.getBestProvider(criteria, true);
            LocationListener location_Listener = new LocationListener() {
                public void onLocationChanged(Location loc) {}
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(bestProvider, 0, 0, location_Listener);
            Location location = locationManager.getLastKnownLocation(bestProvider);
//            locationManager.
            try {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }catch (NullPointerException e){
                latitude = -1;
                longitude = -1;
            }
            return this;
        }
    }
}
