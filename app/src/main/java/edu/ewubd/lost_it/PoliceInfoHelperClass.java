package edu.ewubd.lost_it;

public class PoliceInfoHelperClass {

    private String StationName, StationLocation, StationNumber;

    public PoliceInfoHelperClass() {

    }

    public PoliceInfoHelperClass(String stationName, String stationLocation, String stationNumber) {
        StationName = stationName;
        StationLocation = stationLocation;
        StationNumber = stationNumber;

    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getStationLocation() {
        return StationLocation;
    }

    public void setStationLocation(String stationLocation) {
        StationLocation = stationLocation;
    }

    public String getStationNumber() {
        return StationNumber;
    }

    public void setStationNumber(String stationNumber) {
        StationNumber = stationNumber;
    }

}
