package net.randby.simon.positionrecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {


    private ArrayList logRows;
    private int nextRowId;
    private boolean csvExportMode;
    private boolean txtExportMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nextRowId = 0;
        logRows = new ArrayList();
        csvExportMode = false;
        txtExportMode = false;

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
        idContent.setText("" + nextRowId);

        TextView xContent = (TextView)theInflatedRow.findViewById(R.id.x_coordinate_column_content);
        xContent.setText("" + latitude);

        TextView yContent = (TextView)theInflatedRow.findViewById(R.id.y_coordinate_column_content);
        yContent.setText("" + longitude);

        TextView timeContent = (TextView)theInflatedRow.findViewById(R.id.time_column_content);
        timeContent.setText("" + System.currentTimeMillis());

        theInflatedRow.setId(nextRowId);
        if(nextRowId % 2 != 0){
//            theInflatedRow.setBackgroundColor(Color.GRAY);
            theInflatedRow.setBackgroundColor(0xffe3e3e3);
//            general color format is 0xffHEX
        }
        nextRowId++;

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

    /**Write to log file when "log" button is pressed*/
    public void createLogFile(View view){
        new AlertDialog.Builder(this)
                .setTitle("Export Mode")
                .setMessage("What format do you want to export the log with?")
                .setPositiveButton("CSV", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        csvExportMode = true;
                        txtExportMode = false;
                        buildLogTextAndSave();
                    }
                })
                .setNegativeButton("TXT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtExportMode = true;
                        csvExportMode = false;
                        buildLogTextAndSave();
                    }
                })
                .show();


    }
    public void buildLogTextAndSave(){
        String inlineSeparator = "";
        String rowSeparator = "";

        if(csvExportMode){
            inlineSeparator = ",";
            rowSeparator = ",";
        }else if(txtExportMode){
            inlineSeparator = " ";
            rowSeparator = "\n";
        }else{
            inlineSeparator = "-";
            rowSeparator = "\n\n";
        }

        String stringToWrite = "";
        TableLayout spaceTimeRows = (TableLayout) findViewById(R.id.space_time_rows);
        TableRow currentRow;
        TextView currentText;
        for(int i = 0; i < spaceTimeRows.getChildCount(); i++){
            currentRow = (TableRow) spaceTimeRows.getChildAt(i);
            for (int j = 0; j <  currentRow.getChildCount();j++){
                currentText = (TextView) currentRow.getChildAt(j);
                stringToWrite += currentText.getText();
                //Don't write separator at end of line
                if(j != currentRow.getChildCount() - 1) stringToWrite += inlineSeparator;
            }
            //Don't write line separator after last line
            if(i != spaceTimeRows.getChildCount() - 1) stringToWrite += rowSeparator;
        }
        stringToWrite += "\n";
        System.out.println(stringToWrite);
        writeNewLogFile("",stringToWrite);
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
                        nextRowId = 0;
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

        ScrollView spaceTimeContainer = (ScrollView) findViewById(R.id.table_container);
        spaceTimeContainer.fullScroll(ScrollView.FOCUS_DOWN);
    }

    /**Make row selected when clicked*/
    public void deleteLastEntry(View view){
        TableLayout spaceTimeRows = (TableLayout) findViewById(R.id.space_time_rows);
        if(spaceTimeRows.getChildCount() <= 0) return;
        spaceTimeRows.removeViewAt(spaceTimeRows.getChildCount() - 1);
        nextRowId--;
        ScrollView spaceTimeContainer = (ScrollView) findViewById(R.id.table_container);
        spaceTimeContainer.fullScroll(ScrollView.FOCUS_DOWN);
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

//            Initialize lat and lon, and get the location service
            latitude = -1;
            longitude = -1;
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

//            Find what is the best provider according to a set of criteria
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);

            String bestProvider = locationManager.getBestProvider(criteria, true);
            LocationListener location_Listener = new LocationListener() {
                public void onLocationChanged(Location loc) {}
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };


            locationManager.requestLocationUpdates(bestProvider, 0, 0, location_Listener);



//            Find and use provider that gives the best accuracy:
            String bestFoundProvider;
            double currentAccuracy = 999999;

            bestFoundProvider = bestProvider;
            Location location = locationManager.getLastKnownLocation(bestProvider);
            double bestProviderAccurasy = location.getAccuracy();
            if (bestProviderAccurasy < currentAccuracy){
                bestFoundProvider = bestProvider;
                currentAccuracy = bestProviderAccurasy;
            }

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double networkProviderAccuracy = location.getAccuracy();
            if(networkProviderAccuracy < currentAccuracy){
                bestFoundProvider = LocationManager.NETWORK_PROVIDER;
                currentAccuracy = networkProviderAccuracy;
            }

            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            double passiveProviderAccuracy = location.getAccuracy();
            if(passiveProviderAccuracy < currentAccuracy){
                bestFoundProvider = LocationManager.PASSIVE_PROVIDER;
                currentAccuracy = passiveProviderAccuracy;
            }

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double gpsProviderAccuracy = location.getAccuracy();
            if(gpsProviderAccuracy < currentAccuracy){
                bestFoundProvider = LocationManager.GPS_PROVIDER;
                currentAccuracy = gpsProviderAccuracy;
            }

            System.out.println("The best provider was: " + bestFoundProvider + ", with an accuracy of: " + currentAccuracy);
            location = locationManager.getLastKnownLocation(bestFoundProvider);


//            Set lat and log
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

    public void writeNewLogFile(String givenFileName, String textToWrite){
        String targetDirectory = "SpaceTimeLogger";
        String fileExtension;
        String moreSpecificPartOfName = "Space-Time Log";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(csvExportMode) fileExtension = ".csv";
        else if (txtExportMode) fileExtension = ".txt";
        else fileExtension = "";
        String fileName = (givenFileName == "") ? "" + dateFormat.format(new Date()) + " " + moreSpecificPartOfName + fileExtension : givenFileName + fileExtension;
        FileOutputStream outputStream;

        try {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+targetDirectory);
            directory.mkdirs();

            File outputLogFile = new File(directory, fileName);
            outputStream = new FileOutputStream(outputLogFile);
            outputStream.write(textToWrite.getBytes());
            outputStream.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
