package net.randby.simon.positionrecorder;

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

        TableLayout logTable = (TableLayout) findViewById(R.id.space_time_rows);


        LayoutInflater layoutInflater = getLayoutInflater();

        View theInflatedRow;

        for (int i = 0; i < 3; i++){
            theInflatedRow = layoutInflater.inflate(R.layout.table_log_entry, logTable, false);

            TextView idContent = (TextView)theInflatedRow.findViewById(R.id.id_column_content);
            idContent.setText("ID" + i);
//            logTable.addView(idContent);

            TextView xContent = (TextView)theInflatedRow.findViewById(R.id.x_coordinate_column_content);
            xContent.setText("X Pos: " + i);
//            logTable.addView(xContent);

            TextView yContent = (TextView)theInflatedRow.findViewById(R.id.y_coordinate_column_content);
            yContent.setText("Y Pos: " + i);
//            logTable.addView(yContent);

            TextView timeContent = (TextView)theInflatedRow.findViewById(R.id.time_column_content);
            timeContent.setText("T Pos: " + i);
//            logTable.addView(timeContent);

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
