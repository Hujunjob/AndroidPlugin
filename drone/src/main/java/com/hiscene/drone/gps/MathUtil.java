package com.hiscene.drone.gps;

import android.graphics.PointF;

/**
 *
 * @author hujun
 * @date 18/7/4
 */
public class MathUtil {
    public static double getDistance(double x0,double y0,double x1,double y1){
        double dis = 0;

        double sum = (x0-x1)*(x0-x1) + (y0-y1)*(y0-y1);

        dis =  Math.sqrt(sum);

        return dis;
    }

    public static boolean isCloseRatio(float width, float height, float ratio){
        if(width == 0) {
            return false;
        }
        if(height == 0) {
            return false;
        }
        float newRatio = width/height;
        return ((newRatio- ratio>0.01)||(ratio-newRatio<0.01));
    }

    /**
     * 计算直角三角形斜边长度
     * @param a 直角边a
     * @param b 直角边b
     * @return
     */
    public static double triangleEdge(double a,double b){
        return Math.sqrt(a*a + b*b);
    }


    public static double calATan(double x, double y) {
        PointF pointF = new PointF((float) x, (float) y);
        double angle = Math.atan((double) pointF.y / (double) pointF.x);
        if (pointF.x == 0) {
            if (pointF.y < 0) {
                angle = Math.toRadians(-90);
            } else if (pointF.y > 0) {
                angle = Math.toRadians(90);
            } else {
                angle = 0;
            }
        } else if (pointF.y == 0) {
            if (pointF.x > 0) {
                angle = 0;
            } else if (pointF.x < 0) {
                angle = Math.toRadians(-180);
            } else {
                angle = 0;
            }
        } else {
            if (pointF.y > 0 && pointF.x > 0) {
                angle = Math.atan((double) pointF.y / (double) pointF.x);
            } else if (pointF.y > 0 && pointF.x < 0) {
                angle = Math.PI - Math.atan(-(double) pointF.y / (double) pointF.x);
            } else if (pointF.y < 0 && pointF.x > 0) {
                angle = -Math.atan(-(double) pointF.y / (double) pointF.x);
            } else if (pointF.y < 0 && pointF.x < 0) {
                angle = Math.atan((double) pointF.y / (double) pointF.x) + Math.PI;
            }
        }

        return angle;
    }

}
