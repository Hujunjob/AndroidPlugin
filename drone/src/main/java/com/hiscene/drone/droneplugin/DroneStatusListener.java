package com.hiscene.drone.droneplugin;

/**
 * Created by hujun on 2019/4/15.
 */

public interface DroneStatusListener {
    /**
     * 更新camera的角度
     * @param pitch
     * @param roll
     * @param yaw
     */
    void onCameraAttitude(float pitch, float roll, float yaw);

    /**
     * 更新电池剩余
     * @param percent
     */
    void onBatteryPercent(float percent);

    /**
     * 更新GPS和位置信息
     * @param altitude
     * @param longitude
     * @param latitude
     */
    void onGPSLocation(DroneStatus.GPSLEVEL gpsLevel, float altitude, double longitude, double latitude);

    void onVelocity(float velocityX, float velocityY, float velocityZ);
}
