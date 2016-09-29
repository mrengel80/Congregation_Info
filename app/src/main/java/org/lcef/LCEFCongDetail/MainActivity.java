package org.lcef.LCEFCongDetail;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Map<Integer, Integer> Segments = new HashMap<Integer, Integer>();
    Map<Integer, Integer> VisitCodes = new HashMap<Integer, Integer>();
    Map<Integer, String> LastVisitDates = new HashMap<Integer, String>();
    Map<Integer, String> SearchNames = new HashMap<Integer, String>();
    Map<Integer, Float> Longitudes = new HashMap<Integer, Float>();
    Map<Integer, Float> Latitudes = new HashMap<Integer, Float>();
    Map<Integer, Integer> IndexIDs = new HashMap<Integer, Integer>();
    Map<Integer, String> CityStates = new HashMap<Integer, String>();
    Map<Integer, String> CustomerNames = new HashMap<Integer, String>();
    Map<Integer, Integer> CIFs = new HashMap<Integer, Integer>();
    ArrayList<Integer> ProspectIDs = new ArrayList<Integer>();

    SQLiteDatabase congDB;
    ArrayList<String> titles = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    Button btnCIF;
    Button btnClear;

    EditText CIFText;

    String result;
    String DistrictID;

    String strProspectIDVal;
    String strCIFVal;
    String strCustomerNameVal;
    String strCityStateVal;
    String strIndexIDVal;
    String strLatitudeVal;
    String strLongitudeVal;
    String strSearchNameVal;
    String strLastVisitDateVal;
    String strVisitCodeVal;
    String strSegmentVal;

    Spinner District_CB;

    JSONArray jsonArray;

    TextView strCIF;
    TextView strCustomerName;
    TextView strCityState;
    TextView strLatitude;
    TextView strLongitude;
    TextView strLastVisitDate;

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    private boolean userIsInteracting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CIFText = (EditText) findViewById(R.id.editTxtCIF);

        strCIF = (TextView) findViewById(R.id.txtViewCIF);
        strCustomerName = (TextView) findViewById(R.id.txtViewCustomerName);
        strCityState = (TextView) findViewById(R.id.txtViewCityState);
        strLatitude = (TextView) findViewById(R.id.txtViewLatitude);
        strLongitude = (TextView) findViewById(R.id.txtViewLongitude);
        strLastVisitDate = (TextView) findViewById(R.id.txtViewLastVisitDate);

        btnCIF = (Button)findViewById(R.id.btnCIFEnter);
        btnClear = (Button)findViewById(R.id.btnCIFClear);

        District_CB = (Spinner)findViewById(R.id.District_CB);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.districts, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        District_CB.setAdapter(adapter);

        final DownloadTask task = new DownloadTask();

        District_CB.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String strDistrict = District_CB.getSelectedItem().toString();

                String DistrictID = strDistrict.substring(0,2);

                if (userIsInteracting) {

                try{
                    result = task.execute("https://mobile.lcef.org/mobile/GetOrgsInDistrict?DistrictID=" + DistrictID).get();

                    jsonArray = new JSONArray(result);

                    congDB.execSQL("DELETE FROM tblCongregations");

                    for (int i = 0; i < jsonArray.length(); i++){
                        String CongString = jsonArray.getString(i);

                        DownloadTask getArticle = new DownloadTask();

                        JSONObject jsonObject = new JSONObject(CongString);
                        strProspectIDVal = jsonObject.getString("ProspectID");
                        strCIFVal = jsonObject.getString("CIF");
                        strCustomerNameVal = jsonObject.getString("CustomerName");
                        strCityStateVal = jsonObject.getString("CityState");
                        strIndexIDVal = jsonObject.getString("Index");
                        strLatitudeVal = jsonObject.getString("Latitude");
                        strLongitudeVal = jsonObject.getString("Longitude");
                        strSearchNameVal = jsonObject.getString("SearchName");
                        strLastVisitDateVal = jsonObject.getString("LastVisitDate");
                        strVisitCodeVal = jsonObject.getString("VisitCode");
                        strSegmentVal = jsonObject.getString("Segment");

                        Integer prospect = Integer.valueOf(strProspectIDVal);

                        ProspectIDs.add(prospect);
                        CIFs.put(prospect, Integer.valueOf(strCIFVal));
                        CustomerNames.put(prospect, strCustomerNameVal);
                        CityStates.put(prospect, strCityStateVal);
                        IndexIDs.put(prospect, Integer.valueOf(strIndexIDVal));
                        Latitudes.put(prospect, Float.valueOf(strLatitudeVal));
                        Longitudes.put(prospect, Float.valueOf(strLongitudeVal));
                        SearchNames.put(prospect, strSearchNameVal);
                        LastVisitDates.put(prospect, strLastVisitDateVal);
                        VisitCodes.put(prospect, Integer.valueOf(strVisitCodeVal));
                        Segments.put(prospect, Integer.valueOf(strSegmentVal));

                        String sql = "INSERT INTO tblCongregations (Prospect, CIF, CustomerName, CityState, IndexID, Latitude, Longitude, SearchName, LastVisitDate, VisitCode, Segment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        SQLiteStatement statement = congDB.compileStatement(sql);

                        statement.bindString(1, strProspectIDVal);
                        statement.bindString(2, strCIFVal);
                        statement.bindString(3, strCustomerNameVal);
                        statement.bindString(4, strCityStateVal);
                        statement.bindString(5, strIndexIDVal);
                        statement.bindString(6, strLatitudeVal);
                        statement.bindString(7, strLongitudeVal);
                        statement.bindString(8, strSearchNameVal);
                        statement.bindString(9, strLastVisitDateVal);
                        statement.bindString(10, strVisitCodeVal);
                        statement.bindString(11, strSegmentVal);

                        statement.execute();
                    }

                    String query = "SELECT '' as _id, * FROM tblCongregations ORDER BY CIF desc";

                    Cursor cursor = congDB.rawQuery(query, null);

                    Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));

                    cursor.moveToFirst();

//                   ClientCursorAdapter dbAdapter = new ClientCursorAdapter(getApplicationContext(), R.layout.activity_listview, cursor, 0 );
                    ClientCursorAdapter dbAdapter = new ClientCursorAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, cursor, 0 );

                    ListView myList= (ListView) findViewById(R.id.lstViewCustomers);

                    myList.setAdapter(dbAdapter);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


                    Toast.makeText(MainActivity.this,
                            strDistrict, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        btnCIF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String CIF = CIFText.getText().toString();

                try {

                    Cursor c;

                    if (CIF == null && CIF.isEmpty()) {
                        c = congDB.rawQuery("SELECT * FROM tblCongregations ORDER BY Prospect DESC", null);
                        //Log.i("SQL query string", "Select all");
                    } else {
                        c = congDB.rawQuery("SELECT * FROM tblCongregations WHERE CIF = " + CIF + " ORDER BY Prospect DESC", null);
                        //Log.i("SQL query string", "Select one CIF = " + CIF);
                    }

                    //Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(c));

                    if (c.moveToFirst()) {
                        c.moveToFirst();
                        strCIFVal = c.getString(c.getColumnIndex("CIF"));
                        strCustomerNameVal = c.getString(c.getColumnIndex("CustomerName"));
                        strCityStateVal = c.getString(c.getColumnIndex("CityState"));
                        strLatitudeVal = c.getString(c.getColumnIndex("Latitude"));
                        strLongitudeVal = c.getString(c.getColumnIndex("Longitude"));
                        strLastVisitDateVal = c.getString(c.getColumnIndex("LastVisitDate"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(getApplicationContext(), CustomerDetailActivity.class);
                i.putExtra("txtCIF", strCIFVal);
                i.putExtra("txtCustName", strCustomerNameVal);
                i.putExtra("txtCityState", strCityStateVal);
                i.putExtra("txtLatitude", strLatitudeVal);
                i.putExtra("txtLongitude", strLongitudeVal);
                i.putExtra("txtLastVisitDate", strLastVisitDateVal);
                startActivity(i);
            }
        });

        /*
        String[]  myStringArray={"A","B","C"};


        ArrayAdapter<String> myAdapter=new
                ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                myStringArray);

        ListView myList=(ListView)
                findViewById(R.id.lstViewCustomers);

        myList.setAdapter(myAdapter);
        */

        congDB = this.openOrCreateDatabase("tblCongregations", MODE_PRIVATE, null);

        congDB.execSQL("CREATE TABLE IF NOT EXISTS tblCongregations (Prospect INTEGER, CIF INTEGER, CustomerName VARCHAR, CityState VARCHAR, IndexID INTEGER, Latitude FLOAT, Longitude FLOAT, SearchName VARCHAR, LastVisitDate VARCHAR, VisitCode INTEGER, Segment INTEGER)");
    }

    public class ClientCursorAdapter extends ResourceCursorAdapter {

        public ClientCursorAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView CIF = (TextView) view.findViewById(R.id.CIF);
            CIF.setText(cursor.getString(cursor.getColumnIndex("CIF")));

            TextView CustomerName = (TextView) view.findViewById(R.id.CustomerName);
            CustomerName.setText(cursor.getString(cursor.getColumnIndex("CustomerName")));

            TextView CityState = (TextView) view.findViewById(R.id.CityState);
            CityState.setText(cursor.getString(cursor.getColumnIndex("CityState")));
        }
    }

    public void searchCIF(View view){
        String CIF = CIFText.getText().toString();

        try {

            Cursor c;

            if (CIF == null && CIF.isEmpty()){
                c = congDB.rawQuery("SELECT * FROM tblCongregations ORDER BY Prospect DESC", null);
                Log.i("SQL query string", "Select all");
            }
            else {
                c = congDB.rawQuery("SELECT * FROM tblCongregations WHERE CIF = " + CIF + " ORDER BY Prospect DESC", null);
                Log.i("SQL query string", "Select one CIF = " + CIF);
            }

            int ProspectIDIndex = c.getColumnIndex("Prospect");
            int CIFIndex = c.getColumnIndex("CIF");
            int CustomerNameIndex = c.getColumnIndex("CustomerName");
            int CityStateIndex = c.getColumnIndex("CityState");
            int IndexIDIndex = c.getColumnIndex("IndexID");
            int LatitudeIndex = c.getColumnIndex("Latitude");
            int LongitudeIndex = c.getColumnIndex("Longitude");
            int SearchNameIndex = c.getColumnIndex("SearchName");
            int LastVisitDateIndex = c.getColumnIndex("LastVisitDate");
            int VisitCodeIndex = c.getColumnIndex("VisitCode");
            int SegmentIndex = c.getColumnIndex("Segment");


            if (c.moveToFirst()) {
                strCIF.setText(c.getString(CIFIndex));
                strCustomerName.setText(c.getString(CustomerNameIndex));
                strCityState.setText(c.getString(CityStateIndex));
                strLatitude.setText(c.getString(LatitudeIndex));
                strLongitude.setText(c.getString(LongitudeIndex));
                strLastVisitDate.setText(c.getString(LastVisitDateIndex));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCIF(View view){
        CIFText.setText("");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return result;
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
