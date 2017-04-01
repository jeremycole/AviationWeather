package us.jcole.aviationweather;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class MetarActivity extends AppCompatActivity {
    private TextView mAirportName;
    private TextView mFlightCategory;
    private TextView mMetarAge;
    private TextView mMetarText;

    static int getColorForFlightCategory(String flightCategory) {
        switch (flightCategory) {
            case "VFR":
                return Color.parseColor("#00ff00");
            case "MVFR":
                return Color.parseColor("#ff00ff");
            case "IFR":
                return Color.parseColor("#ff0000");
        }
        return Color.parseColor("#000000");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metar);

        mAirportName = (TextView) findViewById(R.id.airport_name);
        mMetarText = (TextView) findViewById(R.id.metar_text);
        mFlightCategory = (TextView) findViewById(R.id.flight_category);
        mMetarAge = (TextView) findViewById(R.id.metar_age);

        AvWx avwx = new AvWx(getApplicationContext());
        avwx.fetchMetarCollection("KRNO", new AvWx.MetarResponse() {
            @Override
            public void onResponse(Metar metar) {
                mAirportName.setText(metar.getName());
                mMetarText.setText(metar.getMetarText());
                mFlightCategory.setText(metar.getFlightCategory());
                mFlightCategory.setTextColor(getColorForFlightCategory(metar.getFlightCategory()));
                if (metar.getObservationTime() != null) {
                    Date now = new Date();
                    long ageInMillis = now.getTime() - metar.getObservationTime().getTime();
                    mMetarAge.setText(String.format("%d minutes ago", ageInMillis / (1000 * 60)));
                } else {
                    mMetarAge.setText("unknown age");
                }
            }
        });
    }

}
