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

public class AvWx {
    private static String sApiUriPrefix = "http://api.av-wx.com/";
    private RequestQueue mRequestQueue;
    private SimpleDateFormat mDateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    public interface MetarResponse {
        void onResponse(Metar metar);
    }

    public AvWx(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    static String getMetarUri(String identifier) {
        return sApiUriPrefix + "metar/" + identifier;
    }

    static String getTafUri(String identifier) {
        return sApiUriPrefix + "taf/" + identifier;
    }

    public void parseAvWxJsonMetars(MetarResponse metarResponse, String jsonResponse) {
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
                metarResponse.onResponse(metar);
            }
        } catch(JSONException e) {
            return;
        }

    }

    public void fetchMetarCollection(final String identifier, final MetarResponse metarResponse) {
        String metarUri = getMetarUri(identifier);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, metarUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseAvWxJsonMetars(metarResponse, response);
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
