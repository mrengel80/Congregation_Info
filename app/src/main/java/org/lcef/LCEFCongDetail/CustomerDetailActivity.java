package org.lcef.LCEFCongDetail;

        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import org.w3c.dom.Text;

public class CustomerDetailActivity extends AppCompatActivity {

    String strCIFVal;
    String strCustomerNameVal;
    String strCityStateVal;
    String strLatitudeVal;
    String strLongitudeVal;
    String strLastVisitDateVal;

    public void changeActivity(View view) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button fab = (Button) findViewById(R.id.btnReturnToMain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        TextView CIF = (TextView) findViewById(R.id.txtViewCIF);
        TextView CustName = (TextView) findViewById(R.id.txtViewCustomerName);
        TextView CityState = (TextView) findViewById(R.id.txtViewCityState);
        TextView Latitude = (TextView) findViewById(R.id.txtViewLatitude);
        TextView Longitude = (TextView) findViewById(R.id.txtViewLongitude);
        TextView LastViewDate = (TextView) findViewById(R.id.txtViewLastVisitDate);

        Intent i = getIntent();

        strCIFVal = i.getStringExtra("txtCIF");
        strCustomerNameVal = i.getStringExtra("txtCustName");
        strCityStateVal = i.getStringExtra("txtCityState");
        strLatitudeVal = i.getStringExtra("txtLatitude");
        strLongitudeVal = i.getStringExtra("txtLongitude");
        strLastVisitDateVal = i.getStringExtra("txtLastVisitDate");


        CIF.setText(strCIFVal);
        CustName.setText(strCustomerNameVal);
        CityState.setText(strCityStateVal);
        Latitude.setText(strLatitudeVal);
        Longitude.setText(strLongitudeVal);
        LastViewDate.setText(strLastVisitDateVal);

    }

    public void openMap(View view) {
        Uri gmmIntentUri = Uri.parse("geo:" + strLatitudeVal + "," + strLongitudeVal + "?q=" + strLatitudeVal + "," + strLongitudeVal + "(" + strCustomerNameVal + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

}