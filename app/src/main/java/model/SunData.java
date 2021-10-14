package model;

import java.util.Objects;

public class SunData {
    Double altitude;
    Double azimuth;
    Double distance;

    public SunData(Double altitude, Double azimuth, Double distance) {
        this.altitude = altitude;
        this.azimuth = azimuth;
        this.distance = distance;
    }

    public Double getAltitude() {
        return altitude;
    }

    public Double getAzimuth() {
        return azimuth;
    }

    public Double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SunData sunData = (SunData) o;
        return Objects.equals(altitude, sunData.altitude) && Objects.equals(azimuth, sunData.azimuth) && Objects.equals(distance, sunData.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(altitude, azimuth, distance);
    }

    @Override
    public String toString() {
        return "SunData{" +
                "altitude=" + altitude +
                ", azimuth=" + azimuth +
                ", distance=" + distance +
                '}';
    }
}
