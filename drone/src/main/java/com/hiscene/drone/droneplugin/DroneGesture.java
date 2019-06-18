package com.hiscene.drone.droneplugin;

/**
 * Created by hujun on 2019/4/19.
 */

public class DroneGesture {
    //float pitch, float roll, float yaw
    private float pitch;
    private float roll;
    private float yaw;

    public DroneGesture(float pitch, float roll, float yaw) {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public String toString() {
        return "pitch="+pitch+",roll="+roll+",yaw="+yaw;
    }
}
