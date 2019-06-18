package com.hiscene.drone.droneplugin;

/**
 * Created by hujun on 2019/4/20.
 */

public class DroneConstants {
    //    public static final double DEFAULT_FOV = 83.0;
//    public static final double DEFAULT_FOV = 59.83;
    public static final double DEFAULT_FOV = 78.158;

    //大疆mavic2 相机离无人机旋转中心的距离，单位m
    public static final double MAVIC_Camera_DISTANCE = 0.0;

    public static final double DEFAULT_CAMERA_RATIO = 0.5625;

    static {
        double t = Math.tan(Math.toRadians(DEFAULT_FOV / 2.0));
        double rr = Math.sqrt(1 + DEFAULT_CAMERA_RATIO * DEFAULT_CAMERA_RATIO);
        double rr1 = Math.sqrt(1 + 1.0 / (DEFAULT_CAMERA_RATIO * DEFAULT_CAMERA_RATIO));

//        FOV_X = Math.toDegrees(2 * Math.atan(t / rr)) + 7;
//        FOV_Y = Math.toDegrees(2 * Math.atan(t / rr1)) - 3;
        FOV_X = Math.toDegrees(2 * Math.atan(t / rr));
        FOV_Y = Math.toDegrees(2 * Math.atan(t / rr1));
    }

    public static double FOV_X;

    public static double FOV_Y;


    //装备室二楼会议室坐标
    //117.1098417163581,36.51071393111032,251.4062822090097


}
