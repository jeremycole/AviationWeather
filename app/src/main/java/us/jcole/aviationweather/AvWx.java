package us.jcole.aviationweather;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

class AvWx {
    private static String sApiUriPrefix = "http://api.av-wx.com/";
    private RequestQueue mRequestQueue;
    // TODO: The 'X' format doesn't exist on at least API 22. When was it introduced?
    private SimpleDateFormat mDateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX",
            Locale.US);

    interface MetarListener {
        void onMetar(Metar metar);
    }

    AvWx(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    private static String getDelimitedString(String delimiter, List<String> stringList) {
        String delimitedString = "";

        int i = 0;
        for (String str : stringList) {
            if (i > 0)
                delimitedString += delimiter;
            delimitedString += str;
            i++;
        }

        return delimitedString;
    }

    private static String getMetarUri(List<String> identifiers) {
        return sApiUriPrefix + "metar/" + getDelimitedString(",", identifiers);
    }

    private static String getTafUri(List<String> identifiers) {
        return sApiUriPrefix + "taf/" + getDelimitedString(",", identifiers);
    }

    private void parseAvWxJsonMetars(MetarListener metarListener, String jsonResponse) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonResponse);
            JSONArray reports = jsonObject.getJSONArray("reports");
            Log.i("Metar", reports.toString());
            for (int i=0; i < reports.length(); i++) {
                JSONObject report = reports.getJSONObject(i);
                Metar metar = new Metar(
                        report.getString("station_id"),
                        report.getString("raw_text"));
                try {
                    metar.setObservationTime(mDateParser.parse(report.getString("observation_time")));
                } catch (ParseException e) {
                    Log.i("Metar", e.toString());
                    metar.setObservationTime(null);
                }

                metar.setName(report.getString("name"));
                metar.setFlightCategory(report.getString("flight_category"));
                metarListener.onMetar(metar);
            }
        } catch(JSONException e) {
        }

    }

    void fetchMetars(final List<String> identifiers, final MetarListener metarListener) {
        String metarUri = getMetarUri(identifiers);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, metarUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseAvWxJsonMetars(metarListener, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        mRequestQueue.add(stringRequest);
    }
}
