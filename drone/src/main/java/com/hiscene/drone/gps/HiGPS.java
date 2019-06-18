package com.hiscene.drone.gps;

/**
 * Created by hujun on 2019/4/16.
 * 记录一个虚拟点的所有信息，包括点类型role（警员、AR标注、无人机等）、该点的地球坐标、该点的屏幕坐标
 */

public class HiGPS extends GPSLocation {
    private long userId;
    private int role;
    private float orientation;
    private String name;
    private double posX;
    private double posY;

    //该目标是否还活跃，比如警员离开、AR标注停用等
    private boolean enable = true;

    public HiGPS(long userId, int role, double longitude, double latitude, double attitude) {
        super(longitude, latitude, attitude);
        this.userId = userId;
        this.role = role;
    }

    public HiGPS() {
    }

    public HiGPS(HiGPS gps){
        clone(gps);
    }

    public void clone(HiGPS hiGPS) {
        this.userId = hiGPS.getUserId();
        this.role = hiGPS.role;
        this.setLongitude(hiGPS.getLongitude());
        this.setLatitude(hiGPS.getLatitude());
        this.setAltitude(hiGPS.getAltitude());
        this.posX = hiGPS.posX;
        this.posY = hiGPS.posY;
        name = hiGPS.getName();
        orientation = hiGPS.getOrientation();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }


    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public void setGPS(GPSLocation gps) {
        if (gps == null) {
            return;
        }
        setLatitude(gps.getLatitude());
        setLongitude(gps.getLongitude());
        setAltitude(gps.getAltitude());
    }

    @Override
    public String toString() {
        return "userid:" + userId + ",role=" + role + ",orientatino=" + orientation + ",name="+name+",enable="+enable+",x="+posX+",y="+posY+","+ super.toString();
    }
}
