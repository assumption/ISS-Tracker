package edu.calpoly.isstracker.IssData;

import com.badlogic.gdx.math.Vector3;

public class ISSMath {

    // Radius of Earth in 10^4m
    public static final float EARTH_R = 637.1f;
    // Flattening ratio of Earth
    public static final float EARTH_F = 0.00335281f;

    // 0 lat 0 lon = <-EARTH_R, 0, 0>
    public static void convertToXyz(Vector3 issPosition) {
        float latitude = issPosition.x;
        float longitude = issPosition.y;
        float altitude = issPosition.z / 10;

        float cosLat = (float) Math.cos(latitude * Math.PI / 180.0);
        float cosLong = (float) Math.cos(longitude * Math.PI / 180.0);
        float sinLat = (float) Math.sin(latitude * Math.PI / 180.0);
        float sinLong = (float) Math.sin(longitude * Math.PI / 180.0);

        float earth_c = (float) (1.0 / Math.sqrt(Math.pow(cosLat, 2) +
                Math.pow(1.0 - EARTH_F, 2) * Math.pow(sinLat, 2)));
        float earth_s = (float) (Math.pow(1.0 - EARTH_F, 2) * earth_c);

        issPosition.x = -1 * cosLat * cosLong * (altitude + EARTH_R * earth_c);
        issPosition.y = sinLat * (altitude + EARTH_R * earth_s);
        issPosition.z = cosLat * sinLong * (altitude + EARTH_R * earth_c);
    }
}
