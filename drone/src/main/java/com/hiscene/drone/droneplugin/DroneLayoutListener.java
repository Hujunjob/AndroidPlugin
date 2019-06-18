package com.hiscene.drone.droneplugin;


import com.hiscene.drone.gps.GPSTarget;

/**
 * Created by hujun on 2019/4/23.
 */

public interface DroneLayoutListener {
    void onUpdateTarget(GPSTarget gpsLocation);
    void sendTarget(GPSTarget gpsTarget);
}
