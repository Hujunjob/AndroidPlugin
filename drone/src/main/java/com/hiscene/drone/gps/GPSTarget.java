package com.hiscene.drone.gps;

/**
 * Created by hujun on 2019/4/26.
 * 目标点位置，视频坐标系
 */

public class GPSTarget extends HiGPS {
    private long droneUserId;
    private int targetType;
    private String info = "目标点";

    public GPSTarget(double x,double y) {
        this.setRole(UserRole.TARGET);
        this.setName("目标点");
        setPosY(y);
        setPosX(x);
    }

    public GPSTarget(){}

    public GPSTarget(GPSTarget target){
        clone(target);
        droneUserId = target.getDroneId();
        targetType = target.getTargetType();
        info = target.getInfo();
    }

    public void setHiGPS(HiGPS hiGPS){
        setGPS(hiGPS);
        setRole(hiGPS.getRole());
        setUserId(hiGPS.getUserId());
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
        super.setName(info);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        setInfo(name);
    }

    public void setX(double x) {
        setPosX(x);
    }


    public void setY(double y) {
        setPosY(y);
    }

    public int getTargetId() {
        return (int) super.getUserId();
    }

    public void setTargetId(int targetId) {
        super.setUserId(targetId);
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public long getDroneId() {
        return droneUserId;
    }

    public void setDroneId(long userId) {
        this.droneUserId = userId;
    }

    public double getX() {
        return getPosX();
    }

    public double getY() {
        return getPosY();
    }

    @Override
    public String toString() {
        return "x=" + getPosX() + ",y=" + getPosY()+",type="+targetType+",enable="+isEnable()+",droneid="+droneUserId+",gps:"+super.toString();
    }

    @Override
    public String getGPS() {
        return super.getGPS();
    }

    /**
     * 获得发送到服务器的AR标注信息
     *
     * @return
     */
    public String getSInfo() {
        return getTargetId()+","+targetType + "," + isEnable() + "," + info;
    }

    /**
     * 解析服务器获取的AR标注信息
     *
     * @param sInfo
     */
    public void decodeSInfo(String sInfo) {
        if (sInfo == null) {
            return;
        }
        String arr[] = sInfo.split(",");
        if (arr.length == 3) {
            targetType = Integer.parseInt(arr[1]);
            setEnable(true);
            info = arr[2];
        } else if (arr.length == 4) {
            targetType = Integer.parseInt(arr[1]);
            setEnable(Boolean.parseBoolean(arr[2]));
            info = arr[3];
        }
    }
}
