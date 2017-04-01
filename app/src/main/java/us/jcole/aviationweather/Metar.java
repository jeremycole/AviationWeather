package us.jcole.aviationweather;

import java.util.Date;

public class Metar {
    private Date mObservationTime;
    private String mIdentifier;
    private String mMetarText;
    private String mFlightCategory;
    private String mName;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Metar(String identifier, String metarText) {
        setIdentifier(identifier);
        setMetarText(metarText);
    }

    public Date getObservationTime() {
        return mObservationTime;
    }

    public void setObservationTime(Date observationTime) {
        mObservationTime = observationTime;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    public String getMetarText() {
        return mMetarText;
    }

    public void setMetarText(String metarText) {
        mMetarText = metarText;
    }

    public String getFlightCategory() {
        return mFlightCategory;
    }

    public void setFlightCategory(String flightCategory) {
        mFlightCategory = flightCategory;
    }
}
