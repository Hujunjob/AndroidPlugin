package com.hiscene.drone.gps;

/**
 * Created by hujun on 2019/5/18.
 * 实时绘制的路径
 */

public class ArrowTarget extends GPSTarget{
    //该路径的终点；该类本身是路径的起点
    private GPSTarget endTarget;

    public static final String START  =  "start";
    public static final String END  = "end";

    public ArrowTarget(double x, double y) {
        super(x, y);
        setInfo(START);
        endTarget = new GPSTarget(x,y);
        endTarget.setInfo(END);
        setRole(UserRole.TARGET);
    }

    public ArrowTarget() {
        endTarget = new GPSTarget();
        setRole(UserRole.TARGET);
    }

    public void setStart(GPSTarget gpsTarget){

    }

    @Override
    public void setGPS(GPSLocation gps) {
        super.setGPS(gps);
        endTarget.setGPS(gps);
    }

    public GPSTarget getEndTarget() {
        return endTarget;
    }

    public void setEndTarget(GPSTarget endTarget) {
        this.endTarget = endTarget;
    }

    @Override
    public String toString() {
        return super.toString()+",endtarget:"+endTarget.toString();
    }
}
