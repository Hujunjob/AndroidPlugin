package com.hiscene.drone.gps;


import android.graphics.PointF;

import com.hiscene.drone.Log4aUtil;
import com.hiscene.drone.droneplugin.DroneConstants;
import com.hiscene.drone.droneplugin.DroneGesture;

/**
 * Created by hujun on 2019/4/16.
 * 利用GPS和高度来计算相对位置
 */


public class GPSUtil {
    private static final String TAG = "GPSUtilTAG";
    /**
     * 地球平均半径，单位米。考虑到极半径（6356.9088km）和赤道半径（6377.830km）差异不明显。和平均半径差异在1‰
     * 考虑到海拔，山东、上海海拔都在1km以内，影响可以忽略不计
     * 海拔5km，影响也只在1‰以内
     */
    public static final double EARTH_RADIUS = 6371393.0;

    /**
     * 屏幕大小，用于绘制在同一个平面分布的警员
     */
    private int screenWidth, screenHeight;

    public final static double DEFAULT_PITCH = -90.0;

    private CameraMatrix cameraMatrix;


    public GPSUtil() {
    }


    /*
     * 参考：
     * 上海市 经度:121.47 纬度:31.23
     * */


    public PointF calDistance(GPSLocation otherGPSLocation, GPSLocation droneGPSLocation) {
        double y = Math.toRadians(otherGPSLocation.getLatitude() - droneGPSLocation.getLatitude()) * EARTH_RADIUS;
        double x = Math.toRadians(otherGPSLocation.getLongitude() - droneGPSLocation.getLongitude()) * EARTH_RADIUS;
        return new PointF((float) x, (float) y);
    }


    public PointF calDroneLocation(DroneGesture gesture, GPSLocation droneGPS) {
        double[][] intrinsic = cameraMatrix.getIntrinsicMatrix();
        double x = DroneConstants.MAVIC_Camera_DISTANCE;
        double y = 0;
        double z = droneGPS.getAltitude();

        double pitch = gesture.getPitch() + 90;
        double matrix[][] = TranslationUtil.projectionMatrix(intrinsic, Math.toRadians(pitch), Math.toRadians(gesture.getRoll()), Math.toRadians(gesture.getYaw()), x, y, z);

        //相对位置，地球坐标系
        double yw = 0;
        double xw = 0;
        double zw = 0;

        double[] image = TranslationUtil.translateWorldToImage(matrix, xw, yw, zw);

        float sx = (float) (image[0] / image[2]);
        float sy = (float) (image[1] / image[2]);

        return new PointF(sx, sy);
//        return calDroneLocation(gesture.getPitch());
    }

    public void updateCameraFov(double fov){
        Log4aUtil.d(TAG, "updateCameraFov %s", fov);
        cameraMatrix.updateFov(fov);
    }

    public PointF calOtherLocation(DroneGesture gesture, GPSLocation droneGPS, GPSLocation otherGPS) {
        double[][] intrinsic = cameraMatrix.getIntrinsicMatrix();
        double x = DroneConstants.MAVIC_Camera_DISTANCE;
        double y = 0;
        double z = droneGPS.getAltitude();

        double pitch = gesture.getPitch() + 90;
        double matrix[][] = TranslationUtil.projectionMatrix(intrinsic, Math.toRadians(pitch), Math.toRadians(gesture.getRoll()), Math.toRadians(gesture.getYaw()), x, y, z);

        //地球坐标系下警员相对无人机的相对位置
        double pY = Math.toRadians(otherGPS.getLatitude() - droneGPS.getLatitude()) * EARTH_RADIUS;

        double R1 = EARTH_RADIUS * Math.cos(Math.toRadians(droneGPS.getLatitude()));

        double pX = Math.toRadians(otherGPS.getLongitude() - droneGPS.getLongitude()) * R1;
//        double pX = Math.toRadians(otherGPS.getLongitude() - droneGPS.getLongitude()) * EARTH_RADIUS;

        //相对位置，地球坐标系
        double yw = pY;
        double xw = -pX;
        double zw = 0;

        double[] image = TranslationUtil.translateWorldToImage(matrix, xw, yw, zw);

        float sx = (float) (image[0] / image[2]);
        float sy = (float) (image[1] / image[2]);

        if (image[2] > 0) {
            sx = -100;
            sy = -100;
        }

        PointF p = new PointF();
        p.x = sx;
        p.y = sy;

        return p;
//        return calOtherLocationOnScreen(gesture, droneGPS, otherGPS);
    }


    public PointF calOtherLocation2(DroneGesture gesture, GPSLocation droneGPS, GPSLocation otherGPS) {
        double[][] intrinsic = cameraMatrix.getIntrinsicMatrix();
        double x = DroneConstants.MAVIC_Camera_DISTANCE;
        double y = 0;
        double z = droneGPS.getAltitude();

        double pitch = gesture.getPitch() + 90;
        double matrix[][] = TranslationUtil.projectionMatrix(intrinsic, Math.toRadians(pitch), Math.toRadians(gesture.getRoll()), Math.toRadians(gesture.getYaw()), x, y, z);

        //地球坐标系下警员相对无人机的相对位置
        double pY = Math.toRadians(otherGPS.getLatitude() - droneGPS.getLatitude()) * EARTH_RADIUS;
        double pX = Math.toRadians(otherGPS.getLongitude() - droneGPS.getLongitude()) * EARTH_RADIUS;

        //相对位置，地球坐标系
        double yw = pY;
        double xw = -pX;
        double zw = 0;

        double[] image = TranslationUtil.translateWorldToImage(matrix, xw, yw, zw);

        float sx = (float) (image[0] / image[2]);
        float sy = (float) (image[1] / image[2]);

        if (image[2] > 0) {
            sx = -100;
            sy = -100;
        }

        PointF p = new PointF();
        p.x = sx;
        p.y = sy;

        return p;
//        return calOtherLocationOnScreen(gesture, droneGPS, otherGPS);
    }

    /**
     * 图像坐标系 -> 世界坐标系
     *
     * @param pointF   图像坐标系
     * @param gesture
     * @param droneGPS
     * @return
     */
    public GPSLocation calGPSOnScreen(PointF pointF, DroneGesture gesture, GPSLocation droneGPS) {
        //过滤过于远的点
        double[][] intrinsic = cameraMatrix.getIntrinsicMatrix();
        double x = 0;
        double y = 0;
        double z = droneGPS.getAltitude();

        //获取相机内参的逆矩阵
        double[][] reCamera = TranslationUtil.reverseMatrix(intrinsic);
        //根据相机内参逆矩阵，图像坐标系->世界坐标系
        //构建图像坐标系的3*1齐次矩阵
        double[] image = {
                pointF.x,
                pointF.y,
                1};
        double[] worldWithoutRotate = TranslationUtil.multiply3331(reCamera, image);

        double pitch = gesture.getPitch() + 90;

        double matrix[][] = TranslationUtil.reRotateAndTranslation(Math.toRadians(pitch), Math.toRadians(gesture.getRoll()), Math.toRadians(-gesture.getYaw()), x, y, z);

        double xw = -worldWithoutRotate[0];
        double yw = worldWithoutRotate[1];
        double zw = worldWithoutRotate[2];

        //根据最后计算出来的高度必须为0，可以解算出添加到齐次矩阵最后一位的附加值（不能像平时一样随意添加一个1，必须计算出来该值）
        double a = -(matrix[2][0] * xw + matrix[2][1] * yw + matrix[2][2] * zw) / matrix[2][3];
        if (a > 0) {
            return null;
        }
        double[] world = {xw, yw, zw, a};

        double[] w = TranslationUtil.matrixMultiply4441(matrix, world);

        double gpsx = w[0] / w[3];
        double gpsy = w[1] / w[3];
        double alt = w[2];

        double lon = Math.toDegrees(gpsx / EARTH_RADIUS) + droneGPS.getLongitude();
        double lat = Math.toDegrees(gpsy / EARTH_RADIUS) + droneGPS.getLatitude();

        GPSLocation gpsLocation = new GPSLocation(lon, lat, 0);
        Log4aUtil.d(TAG, "calGPSOnScreen %s,%s,%s,%s", pointF, gesture.toString(), droneGPS.toString(), gpsLocation);

        return gpsLocation;
//        return calGPSOnScreen(gesture, droneGPS, pointF);
    }

//    private double k1 = -0.1039 * 4;
//    private double k2 = -0.1188 * 4;
//
//    public PointF calOtherLocationDistortion(DroneGesture gesture, GPSLocation droneGPS, GPSLocation otherGPS) {
//        double[][] intrinsic = cameraMatrix.getIntrinsicMatrix();
//        double x = 0;
//        double y = 0;
//        double z = droneGPS.getAltitude();
//
//        double pitch = gesture.getPitch() + 90;
//        double roll = gesture.getRoll();
//        double yaw = gesture.getYaw();
//
//        //地球坐标系下警员相对无人机的相对位置
//        double pY = Math.toRadians(otherGPS.getLatitude() - droneGPS.getLatitude()) * EARTH_RADIUS;
//        double pX = Math.toRadians(otherGPS.getLongitude() - droneGPS.getLongitude()) * EARTH_RADIUS;
//
//        //相对位置，地球坐标系
//        double yw = pY;
//        double xw = -pX;
//        double zw = 0;
//
//        double[][] rotateAndTransMatrix = TranslationUtil.rotateAndTransMatrix(Math.toRadians(pitch), Math.toRadians(roll), Math.toRadians(yaw), x, y, z);
//        double t[] = {xw, yw, zw, 1};
//
//        double world1[] = TranslationUtil.multiply3441(rotateAndTransMatrix, t);
//
//        double x1 = world1[0] / world1[2];
//        double y1 = world1[1] / world1[2];
//        double r = x1 * x1 + y1 * y1;
//
////        System.out.println("world:"+world1[2]);
//
//        if (world1[2] > 0) {
//            return new PointF(-100, -100);
//        }
//
////        double x2 = x1 / (1 + k1 * r + k2 * r * r);
////        double y2 = y1 / (1 + k1 * r + k2 * r * r);
//
////        System.out.println("x1:"+x1+",x2="+x2+",y1="+y1+",y2="+y2);
//
//        float sx = (float) (cameraMatrix.fx * x1 + cameraMatrix.cx);
//        float sy = (float) (cameraMatrix.fy * y1 + cameraMatrix.cy);
//
//        return new PointF((float) sx, (float) sy);
//    }


    /**
     * 校准屏幕上的位置
     *
     * @param pointF
     * @param pitch
     * @param ratioX
     * @param ratioY
     * @return
     */
    @Tested
    public PointF calOtherLocationWithOffset(PointF pointF, double pitch, double ratioX, double ratioY) {
        PointF p = new PointF();
        pitch = Math.toRadians(pitch);
        p.y = (float) (pointF.y * (1 + ratioY * pitch));
        p.x = (float) (pointF.x * (1 + ratioX) - screenWidth * 0.5 * ratioX);

//        Log4aUtil.d(TAG, "calOtherLocationWithOffset %s,pitch=%s,rx=%s,ry=%s,p=%s",
//                pointF, pitch, ratioX, ratioY, p);
        return p;
    }


    /**
     * 校准屏幕位置，计算得到校准前的实际屏幕位置
     *
     * @param pointF
     * @param pitch
     * @param ratioX
     * @param ratioY
     * @return
     */
    @Tested
    public PointF calOtherPointWithOffset(PointF pointF, double pitch, double ratioX, double ratioY) {
        PointF p = new PointF();
        pitch = Math.toRadians(pitch);

        p.y = (float) (pointF.y / (1 + ratioY * pitch));
        p.x = (float) ((pointF.x + screenWidth * 0.5 * ratioX) / (1 + ratioX));

        return p;
    }

    public void setScreen(int width, int height) {
        Log4aUtil.d(TAG, "setScreen w=%s,h=%s", width, height);
        screenHeight = height;
        screenWidth = width;
        cameraMatrix = new CameraMatrix(DroneConstants.DEFAULT_FOV, width, height);
    }

    /**
     * 计算无人机在屏幕上的位置
     *
     * @param pitch 角度°
     * @return
     */
    public PointF calDroneLocation(double pitch) {
        PointF pointF = new PointF();
        pointF.x = (float) ((double) screenWidth / 2.0);

        //摄像头相对垂直地面的倾角
        double alpha = pitch - DEFAULT_PITCH;

        double fovY = DroneConstants.FOV_Y / 2.0;

        //无人机点在y方向占据屏幕比例
        //虚拟焦距
        double f = 1.0;
        //虚拟成像面长度
        double microLen = 2 * f * Math.tan(Math.toRadians(fovY));
        //虚拟成像面中心到无人机在地面投影到成像面点的距离
        double od = f * Math.tan(Math.toRadians(alpha));

        double r = od / microLen;

        pointF.y = (float) (screenHeight * (0.5 + r));

//        Log4aUtil.d(TAG, "calDroneLocation pitch=%s,pointf=%s", pitch, pointF);

        return pointF;
    }


    /**
     * 根据无人机位姿、GPS和警员的GPS计算警员在屏幕上的位置
     *
     * @param gesture
     * @param droneGPS
     * @param otherGPS
     * @return
     */
    public PointF calOtherLocationOnScreen(DroneGesture gesture, GPSLocation droneGPS, GPSLocation otherGPS) {
        PointF pointF = new PointF();
        //地球坐标系下警员相对无人机的相对位置
        double pY = Math.toRadians(otherGPS.getLatitude() - droneGPS.getLatitude()) * EARTH_RADIUS;
        double pX = Math.toRadians(otherGPS.getLongitude() - droneGPS.getLongitude()) * EARTH_RADIUS;

        //将点P旋转角度yaw到正北
        double yaw = gesture.getYaw();

        double angle1 = MathUtil.calATan(pX, pY);

        double newAngle = angle1 + Math.toRadians(yaw);

        double len = Math.sqrt(pX * pX + pY * pY);

        //旋转到正北后，警员的地球位置
        pY = (float) (len * Math.sin(newAngle));
        pX = (float) (len * Math.cos(newAngle));

        double pitch = gesture.getPitch() - DEFAULT_PITCH;

        //虚拟成像面的虚拟焦距
        double f = 1.0;

        //X方向虚拟成像面长度
        double microLenX = 2 * f * Math.tan(Math.toRadians(DroneConstants.FOV_X * 0.5));
        double microLenY = 2 * f * Math.tan(Math.toRadians(DroneConstants.FOV_Y * 0.5));


        //点P成像点在X方向，离成像面中心点在X方向的距离
        double rX = Math.tan(pointAngleX(droneGPS.getAltitude(), pX, pY)) * f / microLenX;

        //根据pitch修正x轴
        double ratioX = pitch * 0.03 / 50.0;
//        rX = rX * (1 + ratioX);

        pointF.x = (float) ((0.5 + rX * 0.8) * screenWidth);

        double rY = Math.tan(pointAngleY(pitch, droneGPS.getAltitude(), pY)) * f / microLenY;

        //根据pitch修正y轴
        double ratioY = pitch * 0.08 / 50.0;
//        rY = rY * (1 + ratioY);
        pointF.y = (float) ((0.5 + rY * 1.025 ) * screenHeight);

        return pointF;
    }


    /**
     * 根据点击屏幕坐标折算GPS坐标
     *
     * @param gesture
     * @param droneGPS
     * @param pointF
     * @return
     */
    @Tested
    public GPSLocation calGPSOnScreen(DroneGesture gesture, GPSLocation droneGPS, PointF pointF) {
        double rY = pointF.y / (double) screenHeight - 0.5;
        double rX = pointF.x / (double) screenWidth - 0.5;

        //X方向虚拟成像面长度
        double microLenX = 2 * Math.tan(Math.toRadians(DroneConstants.FOV_X * 0.5));
        double microLenY = 2 * Math.tan(Math.toRadians(DroneConstants.FOV_Y * 0.5));

        double pitch = gesture.getPitch() - DEFAULT_PITCH;

        double pointAngleY = Math.atan(rY * microLenY);
        double pointAngleX = Math.atan(rX * microLenX);

        double pY = angleY(pitch, droneGPS.getAltitude(), pointAngleY);
        double pX = angelX(droneGPS.getAltitude(), pY, pointAngleX);

        //将点P旋转角度yaw到正北
        double yaw = gesture.getYaw();

        double angle1 = MathUtil.calATan(pX, pY);

        double newAngle = angle1 - Math.toRadians(yaw);

        double len = Math.sqrt(pX * pX + pY * pY);

        //旋转到正北后，警员的地球位置
        pY = (float) (len * Math.sin(newAngle));
        pX = (float) (len * Math.cos(newAngle));

        //地球坐标系下警员相对无人机的相对位置
//        double pY = Math.toRadians(otherGPS.getLatitude() - droneGPS.getLatitude()) * EARTH_RADIUS;
//        double pX = Math.toRadians(otherGPS.getLongitude() - droneGPS.getLongitude()) * EARTH_RADIUS;

        double longitude = Math.toDegrees(pX / EARTH_RADIUS) + droneGPS.getLongitude();
        double latitude = Math.toDegrees(pY / EARTH_RADIUS) + droneGPS.getLatitude();

        GPSLocation otherGPS = new GPSLocation(longitude, latitude, 0);
        return otherGPS;
    }

    /**
     * 计算点P在X方向在屏幕上的投影角度
     *
     * @param altitude 无人机高度
     * @param x        点P相对于无人机底部地球坐标X距离
     * @param y        点P相对于无人机底部地球坐标Y距离
     * @return 角度，单位弧度
     */
    private double pointAngleX(double altitude, double x, double y) {
        double DPd = MathUtil.triangleEdge(altitude, y);
        double angle = Math.atan(x / DPd);
        return angle;
    }

    /**
     * 计算点P在Y方向在屏幕上的投影角度
     *
     * @param pitch
     * @param altitude
     * @param y
     * @return
     */
    private double pointAngleY(double pitch, double altitude, double y) {
        double angle = 0;
        double gama = Math.atan(y / altitude);
        angle = Math.toRadians(pitch) - gama;
        return angle;
    }


    private double angelX(double altitude, double y, double angle) {
        double DPd = MathUtil.triangleEdge(altitude, y);
        double xDPd = Math.tan(angle);
        double x = DPd * xDPd;
        return x;
    }


    private double angleY(double pitch, double altitude, double angle) {
        double gama = Math.toRadians(pitch) - angle;
        double y = Math.tan(gama) * altitude;
        return y;
    }

}
