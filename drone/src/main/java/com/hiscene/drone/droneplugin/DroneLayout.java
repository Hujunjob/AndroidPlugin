package com.hiscene.drone.droneplugin;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hiscene.drone.Log4aUtil;
import com.hiscene.drone.R;
import com.hiscene.drone.gps.ArrowTarget;
import com.hiscene.drone.gps.GPSLocation;
import com.hiscene.drone.gps.GPSTarget;
import com.hiscene.drone.gps.GPSUtil;
import com.hiscene.drone.gps.HiGPS;
import com.hiscene.drone.gps.UserRole;
import com.hiscene.drone.ui.ArrowPanelView;
import com.hiscene.drone.ui.MyPanelView;
import com.hiscene.drone.ui.TargetPanelView;
import com.hiscene.drone.ui.UserPanelView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by hujun on 2019/4/19.
 */

public class DroneLayout extends RelativeLayout {
    private static final String TAG = "DroneLayoutTAG";
    @BindView(R.id.txt_altitude)
    TextView txtAltitude;
    @BindView(R.id.txt_drone_gps)
    TextView txtDroneGps;
    @BindView(R.id.txt_p1_gps)
    TextView txtP1Gps;
    @BindView(R.id.txt_drone_gesture)
    TextView txtDroneGesture;
    @BindView(R.id.txt_drone_position)
    TextView txtDronePosition;
    @BindView(R.id.txt_p1_position)
    TextView txtP1Position;
    @BindView(R.id.ll_drone_info)
    LinearLayout llDroneInfo;
    @BindView(R.id.img_compass)
    ImageView imgCompass;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.img_pin)
    ImageView imgPin;
    @BindView(R.id.img_pin2)
    ImageView imgPin2;
    @BindView(R.id.img_pin3)
    ImageView imgPin3;
    @BindView(R.id.ll_icons)
    LinearLayout llIcons;
    @BindView(R.id.img_tag)
    ImageView imgTag;
    @BindView(R.id.edit_ratiox)
    EditText editRatiox;
    @BindView(R.id.edit_ratioy)
    EditText editRatioy;
    @BindView(R.id.img_route)
    ImageView imgRoute;
    @BindView(R.id.btn_up_x)
    Button btnUpX;
    @BindView(R.id.btn_down_x)
    Button btnDownX;
    @BindView(R.id.btn_up_y)
    Button btnUpY;
    @BindView(R.id.btn_down_y)
    Button btnDownY;
    @BindView(R.id.cl_edit_ratio)
    ConstraintLayout clEditRatio;
    @BindView(R.id.btn_switch)
    Button btnSwitch;

    private long droneId;
    private long myId = 0;

    private Paint mPaint;
    private List<HiGPS> hiGPSList = new ArrayList<>();

    private HiGPS droneGPS;
    private DroneGesture droneGesture;

    private GPSUtil gpsUtil;

    private int screenWidth = 0, screenHeight = 0;

    private DroneLayoutListener droneLayoutListener;

    private Context mContext;

    private int choosenTarget = Enums.TargetType.Type0_VALUE;

    //无人机此时的转角yaw
    private float droneYaw;
    private LongSparseArray<UserPanelView> userPanels = new LongSparseArray<>();


    private boolean drawEnable = false;

    private boolean debugMode = false;

    private double ratioX = 0;
    private double ratioY = 0;

    private boolean testMode = false;

    //无人机飞手位置
//    private GPSLocation controllerGPS = new GPSLocation();

    //保存暂时的AR标注，通话后删除
    private SparseArray<GPSTarget> tempTarget = new SparseArray<>();

    //绘制当前正在画的arrow
    private ArrowPanelView curArrowPanelView;

    public DroneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_drone, this);
        ButterKnife.bind(this, view);
        this.setWillNotDraw(false);
        initPaint();
        gpsUtil = new GPSUtil();
        if (testMode) {
            droneGesture = new DroneGesture(-90, 0, 0);
            droneGPS = new HiGPS();
            droneGPS.setRole(UserRole.DRONE);
            droneGPS.setGPS(new GPSLocation(117.10954284667969, 36.482479095458984, 200.0));
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(10);
    }

    private void putTemp(int id, GPSTarget target) {
//        Log4aUtil.d(TAG, "putTemp %s", target.toString());
        tempTarget.put(id, target);
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        if (debugMode) {
            llDroneInfo.setVisibility(VISIBLE);
            clEditRatio.setVisibility(VISIBLE);
        } else {
            llDroneInfo.setVisibility(GONE);
            clEditRatio.setVisibility(GONE);
        }
    }


    private boolean gpsutil1 = false;

    private void updateCanvas() {
        if (droneId == 0 || droneGesture == null || droneGPS == null) {
            drawUserLocations();
            return;
        }

        PointF droneLocation = gpsUtil.calDroneLocation(droneGesture, droneGPS);

//        droneLocation = gpsUtil.calDroneLocation(droneGesture.getPitch());

        float droneX = droneLocation.x;
        float droneY = droneLocation.y;
        drawDrone(droneX, droneY, droneGPS);

        for (HiGPS gps : hiGPSList) {
            if (gps.getUserId() != droneId) {
                PointF pointF = gpsUtil.calOtherLocation(droneGesture, droneGPS, gps);
                if (gpsutil1) {
                    pointF = gpsUtil.calOtherLocation2(droneGesture, droneGPS, gps);
                }
//                PointF pointF2 = gpsUtil.calOtherLocation2(droneGesture, droneGPS, gps);
//                pointF = gpsUtil.calOtherLocationOnScreen(droneGesture, droneGPS, gps);

//                Log4aUtil.d(TAG, "calOtherLocation drone :%s, police : %s, point:%s", droneGPS, gps, pointF);
//                Log4aUtil.d(TAG, "updateCanvas p1=%s,p2=%s", pointF, pointF2);

                double pitch = droneGesture.getPitch() - GPSUtil.DEFAULT_PITCH;
//                pointF = gpsUtil.calOtherLocationWithOffset(pointF, pitch, ratioX, ratioY);

                float pX = pointF.x;
                float pY = pointF.y;

                PointF distance = gpsUtil.calDistance(gps, droneGPS);
                gps.setPosX(((double) pX / (double) screenWidth));
                gps.setPosY(((double) pY / (double) screenHeight));


//                Log4aUtil.d(TAG, "calOtherLocation with offset drone :%s, police : %s, point:%s", droneGPS, gps, pointF);

                switch (gps.getRole()) {
                    case UserRole.POLICE:
                        drawPolice(pX, pY, gps);
                        break;
                    case UserRole.TARGET:
                        drawTarget(pX, pY, (GPSTarget) gps);
                        break;
                    case UserRole.ADMIN:
                        drawLeader(pX, pY, gps);
                        break;
                    default:
                        break;
                }

                if (debugMode) {
                    txtP1Position.setText("警员位置：x=" + pX + ",y=" + pY);
                    txtP1Gps.setText("警员GPS：" + gps.getLongitude() + "," + gps.getLatitude() + "," + gps.getAltitude());
                }
            } else {
                gps.setPosX(((double) droneX / (double) screenWidth));
                gps.setPosY(((double) droneY / (double) screenHeight));
            }
        }

        for (int i = 0; i < tempTarget.size(); i++) {
            int id = tempTarget.keyAt(i);
            GPSTarget target = tempTarget.get(id);
            if (target instanceof ArrowTarget) {
                ArrowTarget arrowTarget = (ArrowTarget) target;
                PointF pointStart = gpsUtil.calOtherLocation(droneGesture, droneGPS, arrowTarget);
                PointF pointEnd = gpsUtil.calOtherLocation(droneGesture, droneGPS, arrowTarget.getEndTarget());
                arrowTarget.setX((double) pointStart.x / (double) screenWidth);
                arrowTarget.setY((double) pointStart.y / (double) screenHeight);
                arrowTarget.getEndTarget().setX((double) pointEnd.x / (double) screenWidth);
                arrowTarget.getEndTarget().setY((double) pointEnd.y / (double) screenHeight);
                drawArrowTarget(arrowTarget);
            } else {
                PointF pointF = gpsUtil.calOtherLocation(droneGesture, droneGPS, target);
                float pX = pointF.x;
                float pY = pointF.y;
                target.setPosX(((double) pX / (double) screenWidth));
                target.setPosY(((double) pY / (double) screenHeight));
                drawTarget(pX, pY, target);
            }
        }

        if (debugMode) {
            txtDronePosition.setText("无人机位置：x=" + droneX + ",y=" + droneY);
        }
    }

    /**
     * 从服务器更新所有警员GPS信息
     */
    public void updateGPS(List<HiGPS> hiGPSs) {
        hiGPSList = hiGPSs;
        for (HiGPS gps : hiGPSList) {
//            Log4aUtil.d(TAG, "updateGPS : %s", gps.toString());
            //修改其他端的相对飞机起飞高度
//            gps.setAltitude(gps.getAltitude() - controllerGPS.getAltitude());
            gps.setAltitude(0);
        }

        if (droneGPS != null) {
            //添加无人机
            hiGPSList.add(droneGPS);
        }
//        refreshUI();
    }

    public void updateDroneGPS(GPSLocation gpsLocation) {
//        Log4aUtil.d(TAG, "updateDroneGPS : %s", gpsLocation);
        if (droneGPS == null) {
            droneGPS = new HiGPS(droneId, UserRole.DRONE, gpsLocation.getLongitude(), gpsLocation.getLatitude(), gpsLocation.getAltitude());
        } else {
            droneGPS.setAltitude(gpsLocation.getAltitude());
            droneGPS.setLongitude(gpsLocation.getLongitude());
            droneGPS.setLatitude(gpsLocation.getLatitude());
        }
//        refreshUI();
    }

    /**
     * 更新无人机姿态
     */
    public void updateGesture(float pitch, float roll, float yaw) {
//        Log4aUtil.d(TAG, "updateGesture pitch:%s,roll:%s,yaw:%s", pitch, roll, yaw);
        droneGesture = new DroneGesture(pitch, roll, yaw);
        if (droneGPS != null) {
            droneGPS.setOrientation(yaw);
        }
        updateCompass(yaw);
        refreshUI();
    }

    public void setDroneId(long droneId) {
        this.droneId = droneId;
        Log4aUtil.d(TAG, "setDroneId : %s", droneId);
    }

    public List<HiGPS> getUserLocations() {
//        Log4aUtil.e(TAG, "getUserLocations start");
        List<HiGPS> array = new ArrayList<>();
        for (HiGPS hiGPS : hiGPSList) {
            if (hiGPS.getPosX() != 0) {
                array.add(hiGPS);
            }
//            Log4aUtil.d(TAG, "getUserLocations %s", hiGPS.toString());
        }
//        Log4aUtil.e(TAG, "getUserLocations center %s", array.size());
        for (int i = 0; i < tempTarget.size(); i++) {
            int id = tempTarget.keyAt(i);
            GPSTarget target = tempTarget.get(id);
//            Log4aUtil.d(TAG, "getUserLocations temp : %s", target.toString());
            if (!Double.isNaN(target.getPosX()) && target.getTargetType() != Enums.TargetType.TypeDraw_VALUE && target.getRole() == UserRole.TARGET) {
//                Log4aUtil.d(TAG, "getUserLocations add target %s" , target.toString());
                array.add(target);
            }
        }
        for (HiGPS gps : array) {
//            Log4aUtil.d(TAG, "getUserLocations : %s", gps.toString());
        }
//        Log4aUtil.e(TAG, "getUserLocations finish:%s", array.size());
        return array;
    }

    public List<ArrowTarget> getArrowTargets() {
        List<ArrowTarget> arrowTargets = new ArrayList<>();
        for (int i = 0; i < tempTarget.size(); i++) {
            int id = tempTarget.keyAt(i);
            GPSTarget target = tempTarget.get(id);
            if (target.getTargetType() == Enums.TargetType.TypeDraw_VALUE) {
                arrowTargets.add((ArrowTarget) target);
            }
        }
//        Log4aUtil.d(TAG, "getArrowTargets : %s", arrowTargets.size());
        return arrowTargets;
    }

    /**
     * 从无人机更新所有人的GPS
     *
     * @param userLocations
     */
    public void updateUserLocationFromDrone(List<HiGPS> userLocations) {
        this.hiGPSList = userLocations;
        for (HiGPS gps : hiGPSList) {
//            Log4aUtil.d(TAG, "updateUserLocationFromDrone : %s", gps.toString());
        }
        refreshUI();
    }


    /**
     * 非飞手端的各种元素绘制
     */
    private void drawUserLocations() {
        //需要判断是不是远端删除了某些元素
        for (HiGPS user : hiGPSList) {
//            Log4aUtil.d(TAG, "drawUserLocations : %s", user);
            double x = user.getPosX() * screenWidth;
            double y = user.getPosY() * screenHeight;
            switch (user.getRole()) {
                case UserRole.DRONE:
                    droneYaw = user.getOrientation();
                    updateCompass(droneYaw);
                    drawDrone(x, y, user);
                    break;
                case UserRole.POLICE:
                    drawPolice(x, y, user);
                    break;
                case UserRole.TARGET:
                    if (user instanceof ArrowTarget) {
                        drawArrowTarget((ArrowTarget) user);
                    } else {
                        drawTarget(x, y, (GPSTarget) user);
                    }
                    break;
                case UserRole.ADMIN:
                    drawLeader(x, y, user);
                    break;
                default:
                    break;
            }
        }
    }

    public void setMyInfo(long myId) {
        this.myId = myId;
    }

    /**
     * 更新无人机图标位置
     *
     * @param x
     * @param y
     */
    private void drawDrone(double x, double y, HiGPS hiGPS) {
        long userId = hiGPS.getUserId();
        droneId = userId;
        UserPanelView userPanelView = userPanels.get(userId);
        if (userPanelView == null) {
            Log4aUtil.d(TAG, "drawDrone create:" + hiGPS);
            userPanelView = new UserPanelView(mContext, UserRole.DRONE);
            userPanelView.setUserId(userId);
            flContainer.addView(userPanelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            userPanels.append(userId, userPanelView);
        }
        userPanelView.setInfo(hiGPS);
        userPanelView.update(x, y);
    }

    /**
     * 更新警员图标位置
     *
     * @param x 在屏幕上的坐标
     * @param y 在屏幕上的坐标
     */
    private void drawPolice(double x, double y, HiGPS hiGPS) {
        long userId = hiGPS.getUserId();
//        Log4aUtil.d(TAG, "drawPolice x=%s,y=%s,id=%s,caller=%s", x, y, userId, Log4aUtil.getCallerMsg());
        if (userId == myId) {
            drawMine(x, y, hiGPS);
        } else {
            UserPanelView panelView = userPanels.get(userId);
            if (panelView == null) {
                Log4aUtil.d(TAG, "drawPolice create:" + userId);
                panelView = new UserPanelView(mContext, UserRole.POLICE);
                panelView.setUserId(userId);

                flContainer.addView(panelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                userPanels.append(userId, panelView);
            }
            panelView.setInfo(hiGPS);
            panelView.update(x, y);
        }
    }

    /**
     * 更新指挥中心图标位置
     *
     * @param x 在屏幕上的坐标
     * @param y 在屏幕上的坐标
     */
    private void drawLeader(double x, double y, HiGPS hiGPS) {
        long userId = hiGPS.getUserId();
        UserPanelView panelView = userPanels.get(userId);
        if (panelView == null) {
            Log4aUtil.d(TAG, "drawLeader create:" + userId);
            panelView = new UserPanelView(mContext, UserRole.ADMIN);
            panelView.setUserId(userId);
            flContainer.addView(panelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            userPanels.append(userId, panelView);
        }
        panelView.setInfo(hiGPS);
        panelView.update(x, y);
    }

    private void drawMine(double x, double y, HiGPS hiGPS) {
        UserPanelView panelView = userPanels.get(myId);
        if (panelView == null) {
            Log4aUtil.d(TAG, "drawMine create : " + hiGPS);
            panelView = new MyPanelView(mContext, UserRole.POLICE);
            panelView.setUserId(myId);
            userPanels.append(myId, panelView);
            flContainer.addView(panelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        panelView.setInfo(hiGPS);
        panelView.update(x, y);
    }

    public void updateOrientation(float angle) {
        float yaw = droneYaw;
//        Log4aUtil.d(TAG, "updateOrientation id=%s, angle=%s,yaw=%s", myId, angle, yaw);
        if (droneId == myId) {
            return;
        }
        UserPanelView panelView = userPanels.get(myId);
        if (panelView != null && panelView instanceof MyPanelView) {
            ((MyPanelView) panelView).updateOrientation(yaw, angle + getScreenRotation());
        }
    }

    private void updateCompass(float yaw) {
        int rotation = getScreenRotation();
        imgCompass.setRotation(-yaw);
    }


    private int getScreenRotation() {
//        if (inCallFragment != null) {
//            return inCallFragment.getScreenRotation();
//        }
        return 90;
    }

    /**
     * 通过userId获取详细GPS信息
     *
     * @param userId
     * @return
     */
    private HiGPS getHiGPSByUserId(long userId) {
        for (HiGPS gps : hiGPSList) {
            if (gps.getUserId() == userId) {
                return gps;
            }
        }
        return null;
    }


    /**
     * 更新指挥员添加的虚拟目标点图标位置
     *
     * @param x
     * @param y
     */
    private void drawTarget(double x, double y, GPSTarget hiGPS) {
        long userId = hiGPS.getUserId();
        if (hiGPS.isEnable()) {
//            Log4aUtil.d(TAG, "drawTarget x=%s,y=%s,gps=%s,caller=%s", x, y, hiGPS, Log4aUtil.getCallerMsg());
            TargetPanelView panelView = null;
            if (userPanels.get(userId) != null && userPanels.get(userId) instanceof TargetPanelView) {
                panelView = (TargetPanelView) userPanels.get(userId);
            }
            if (panelView == null) {
                Log4aUtil.d(TAG, "drawTarget create:" + hiGPS);
                panelView = new TargetPanelView(mContext);
                hiGPS.setDroneId(droneId);
                panelView.setGpsTarget(hiGPS);
                panelView.setOnEventListener(targetEventListener);
                panelView.setTargetType((hiGPS).getTargetType());
                panelView.setUserId(userId);
                flContainer.addView(panelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                userPanels.append(userId, panelView);
            }
            panelView.setInfo(hiGPS);
            panelView.update(x, y);
            if (hiGPS.getName() != null) {
                panelView.updateName(hiGPS.getName());
            }
        } else {
            UserPanelView panelView = userPanels.get(userId);
            if (panelView != null) {
                Log4aUtil.d(TAG, "drawTarget delete : %s", userId);
                userPanels.delete(userId);
                flContainer.removeView(panelView);
            }
        }
    }

    private boolean checkSameArrowTarget(ArrowTarget target, ArrowTarget target2) {
        double x = target.getX() - target2.getX();
        double y = target.getY() - target2.getY();
        if (Math.abs(x) < 0.0001 && Math.abs(y) < 0.0001) {
            return true;
        }
        return false;
    }

    private void drawArrowTarget(ArrowTarget arrowTarget) {
//        Log4aUtil.d(TAG, "drawArrowTarget : %s", arrowTarget.toString());
        long userId = arrowTarget.getUserId();
        if (arrowTarget.isEnable()) {
            ArrowPanelView panelView = null;
            //删除temp arrow
            if (curArrowPanelView != null) {
                ArrowTarget arrowTarget1 = curArrowPanelView.getArrowTarget();
                if (checkSameArrowTarget(arrowTarget, arrowTarget1)) {
                    Log4aUtil.e(TAG, "drawArrowTarget delete temp");
                    flContainer.removeView(curArrowPanelView);
                    curArrowPanelView = null;
                }
            }
            if (userPanels.get(userId) != null) {
                panelView = (ArrowPanelView) userPanels.get(userId);
            }
            if (panelView == null) {
                Log4aUtil.d(TAG, "drawArrowTarget create:" + userId);
                panelView = new ArrowPanelView(mContext);
                panelView.setArrowTarget(arrowTarget);
                arrowTarget.setDroneId(droneId);
                panelView.setUserId(userId);
                panelView.setOnEventListener(arrow -> {
                    Log4aUtil.e(TAG, "drawArrowTarget delete");
                    arrow.setEnable(false);
                    arrow.setDroneId(droneId);
                    if (droneGesture == null) {
                        droneLayoutListener.sendTarget(arrow);
                    } else {
                        deleteTempTarget(arrow.getTargetId());
                    }
                });
                flContainer.addView(panelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                userPanels.append(userId, panelView);
            }

            float startX = (float) (arrowTarget.getX() * screenWidth);
            float startY = (float) (arrowTarget.getY() * screenHeight);
            float endX = (float) (arrowTarget.getEndTarget().getX() * screenWidth);
            float endY = (float) (arrowTarget.getEndTarget().getY() * screenHeight);
            panelView.setStart(startX, startY);
            panelView.update(endX, endY);
//            Log4aUtil.d(TAG, "drawArrowTarget : start:%s,end:%s", startX, endX);
        } else {
            UserPanelView panelView = userPanels.get(userId);
            if (panelView != null) {
                Log4aUtil.d(TAG, "drawArrowTarget delete : %s", userId);
                userPanels.delete(userId);
                flContainer.removeView(panelView);
            }
        }
    }

    private TargetPanelView.OnEventListener targetEventListener = new TargetPanelView.OnEventListener() {
        @Override
        public void onDelete(GPSTarget gpsTarget, int id) {
            Log4aUtil.d(TAG, "drawTarget onDelete");
            gpsTarget.setEnable(false);
            gpsTarget.setDroneId(droneId);

            if (droneGesture == null) {
                droneLayoutListener.sendTarget(gpsTarget);
            } else {
                deleteTempTarget(id);
                if (gpsTarget.getTargetType() == Enums.TargetType.TypeTag_VALUE) {
                    droneLayoutListener.onUpdateTarget(gpsTarget);
                }
            }
        }

        @Override
        public void onChangeInfo(GPSTarget gpsTarget) {
            if (gpsTarget.getTargetType() == Enums.TargetType.TypeTag_VALUE && droneGesture != null) {
                Toast.makeText(mContext, "正在上传云端...", Toast.LENGTH_LONG).show();
                droneLayoutListener.onUpdateTarget(gpsTarget);
            }
            Log4aUtil.d(TAG, "onChangeInfo : %s", gpsTarget.toString());
            if (tempTarget.get(gpsTarget.getTargetId()) != null) {
                putTemp(gpsTarget.getTargetId(), gpsTarget);
            }
            if (droneGesture == null) {
                gpsTarget.setDroneId(droneId);
                droneLayoutListener.sendTarget(gpsTarget);
            }
        }
    };

    private void deleteTempTarget(int userId) {
        Log4aUtil.d(TAG, "deleteTempTarget : %s", userId);
        GPSTarget target = tempTarget.get(userId);
        if (target != null) {
            target.setEnable(false);
        }
        UserPanelView panelView = userPanels.get(userId);
        if (panelView != null) {
            Log4aUtil.d(TAG, "deleteTempTarget delete : %s", userId);
            userPanels.delete(userId);
            flContainer.removeView(panelView);
        }
    }

    /**
     * 远端发送目标点坐标过来，无人机进行计算
     */
    public void addTargetFromOther(GPSTarget gpsTarget) {
        double x = gpsTarget.getX();
        double y = gpsTarget.getY();
        Log4aUtil.d(TAG, "addTargetFromOther  ： %s", gpsTarget.toString());
        if (droneGesture != null && droneGPS != null) {
            //删除
            if (!gpsTarget.isEnable()) {
                Log4aUtil.d(TAG, "remove Target : %s", gpsTarget.getTargetId());
                if (tempTarget.get(gpsTarget.getTargetId()) != null) {
                    Log4aUtil.d(TAG, "addTargetFromOther delete success");
                    deleteTempTarget(gpsTarget.getTargetId());
                    if (gpsTarget.getTargetType() == Enums.TargetType.TypeTag_VALUE) {
                        droneLayoutListener.onUpdateTarget(gpsTarget);
                    }
                }
                refreshUI();
                return;
            }

            GPSTarget target = tempTarget.get(gpsTarget.getTargetId());
            if (target == null || gpsTarget.getTargetId() == 0) {
                if (gpsTarget.getTargetType() == Enums.TargetType.TypeDraw_VALUE) {
                    ArrowTarget arrowTarget = (ArrowTarget) gpsTarget;
                    arrowPoint2GPS(arrowTarget);
                    int id = generateTargetIdNow();
                    arrowTarget.setTargetId(id);
                    putTemp(id, arrowTarget);
                } else {
                    PointF pointF = new PointF((float) x * screenWidth, (float) y * screenHeight);
                    GPSLocation gpsLocation = gpsUtil.calGPSOnScreen(pointF, droneGesture, droneGPS);
                    if (gpsLocation == null) {
                        toast("该目标点过远");
                        return;
                    }
                    int id = generateTargetIdNow();
                    gpsTarget.setTargetId(id);
                    gpsTarget.setGPS(gpsLocation);
                    putTemp(gpsTarget.getTargetId(), gpsTarget);
                }
                Log4aUtil.d(TAG, "addTargetFromOther create target:%s", gpsTarget);
            } else {
                target.setName(gpsTarget.getName());
                putTemp(target.getTargetId(), target);
                Log4aUtil.d(TAG, "addTargetFromOther update");
            }
            if (gpsTarget.getTargetType() == Enums.TargetType.TypeTag_VALUE) {
                droneLayoutListener.onUpdateTarget(gpsTarget);
            }
        } else {
            Log4aUtil.e(TAG, "addTargetFromOther error");
        }
        refreshUI();
    }

    private String printTempTarget() {
        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log4aUtil.d(TAG, "onTouchEvent:" + event.toString());
        if (!drawEnable) {
            return false;
        }
        //如果是实时绘制路径
        if (choosenTarget == Enums.TargetType.TypeDraw_VALUE) {
            drawArrow(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            Log4aUtil.d(TAG, "onTouchEvent : targettype=%s,x=%s,y=%s", choosenTarget, x, y);
            if (droneGesture != null && droneGPS != null) {
                double pitch = droneGesture.getPitch();
                PointF pointF = new PointF(x, y);
                //反校准后的点
//                PointF p = gpsUtil.calOtherPointWithOffset(pointF, pitch - GPSUtil.DEFAULT_PITCH, ratioX, ratioY);
                GPSLocation gpsLocation = gpsUtil.calGPSOnScreen(pointF, droneGesture, droneGPS);
                if (gpsLocation == null) {
                    toast("该目标点过远");
                    return super.onTouchEvent(event);
                }
                int id = generateTargetIdNow();
                GPSTarget gpsTarget = new GPSTarget(x, y);
                gpsTarget.setTargetType(choosenTarget);
                gpsTarget.setTargetId(id);
                gpsTarget.setGPS(gpsLocation);
                putTemp(id, gpsTarget);
                if (choosenTarget == Enums.TargetType.TypeTag_VALUE) {
                    droneLayoutListener.onUpdateTarget(gpsTarget);
                }
                Log4aUtil.d(TAG, "onTouchEvent x=%s,y=%s,gps=%s", x, y, gpsLocation.toString());
            } else {
                GPSTarget target = new GPSTarget((double) x / (double) screenWidth, (double) y / (double) screenHeight);
                target.setDroneId(droneId);
                target.setTargetType(choosenTarget);
                Log4aUtil.d(TAG, "onTouchEvent send target:" + target.toString());
                droneLayoutListener.sendTarget(target);
            }
        }
        refreshUI();
        return true;
    }

    private void drawArrow(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(event);
                break;
            default:
                break;
        }
    }

    private void touchStart(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log4aUtil.d(TAG, "touchStart x=%s,y=%s", x, y);
        ArrowTarget currentArrow = new ArrowTarget(x, y);
        currentArrow.setTargetType(Enums.TargetType.TypeDraw_VALUE);
        currentArrow.setDroneId(droneId);
        curArrowPanelView = new ArrowPanelView(mContext);
        curArrowPanelView.setArrowTarget(currentArrow);
        flContainer.addView(curArrowPanelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void touchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        curArrowPanelView.update(x, y);
    }

    private void touchUp(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        GPSTarget endTarget = new GPSTarget(x, y);
        ArrowTarget arrowTarget = curArrowPanelView.getArrowTarget();
        arrowTarget.setEndTarget(endTarget);
        curArrowPanelView.update(x, y);

        arrowTarget.setX(arrowTarget.getX() / (double) screenWidth);
        arrowTarget.setY(arrowTarget.getY() / (double) screenHeight);

        GPSTarget endT = arrowTarget.getEndTarget();
        endT.setX(endT.getX() / (double) screenWidth);
        endT.setY(endT.getY() / (double) screenHeight);

        //如果是无人机飞手端，则将arrow放入tempTarget，之后发送出去
        if (droneGesture != null) {
            int id = generateTargetIdNow();
            arrowTarget.setTargetId(id);
            arrowPoint2GPS(arrowTarget);
            putTemp(id, arrowTarget);
        }
        //如果是非飞手端，则将该arrow发送出去给飞手端
        else {
            Log4aUtil.d(TAG, "touchUp:" + arrowTarget.toString());
            droneLayoutListener.sendTarget(arrowTarget);
        }
    }

    private void arrowPoint2GPS(ArrowTarget arrowTarget) {
        PointF start = new PointF((float) arrowTarget.getX() * screenWidth, (float) arrowTarget.getY() * screenHeight);
        GPSLocation startGPS = gpsUtil.calGPSOnScreen(start, droneGesture, droneGPS);
        arrowTarget.setGPS(startGPS);

        GPSTarget endT = arrowTarget.getEndTarget();
        PointF end = new PointF((float) endT.getX() * screenWidth, (float) endT.getY() * screenHeight);
        GPSLocation endGPS = gpsUtil.calGPSOnScreen(end, droneGesture, droneGPS);
        arrowTarget.getEndTarget().setGPS(endGPS);
    }

    private int generateTargetIdNow() {
        int len = tempTarget.size();
        int max = tempTarget.keyAt(0);
        for (int i = 0; i < len; i++) {
            int id = tempTarget.keyAt(i);
            GPSTarget target = tempTarget.get(id);
            if (target != null) {
                if (!target.isEnable()) {
                    max = id - 1;
                    break;
                }
            }
            if (id > max) {
                max = id;
            }
        }
        return max + 1;
    }

    public static final int MESSAGE_REFRESH = 299;

    private void refreshUI() {
//        Log4aUtil.d(TAG, "refreshUI : %s", Log4aUtil.getCallerMsg());
        handler.sendEmptyMessage(MESSAGE_REFRESH);
    }

    private void doRefreshUI() {
        if (screenHeight == 0) {
            screenHeight = getHeight();
            screenWidth = getWidth();
            gpsUtil.setScreen(screenWidth, screenHeight);
        }
        updateCanvas();
        if (droneGPS != null && debugMode) {
            txtAltitude.setText("高度：" + droneGPS.getAltitude());
            txtDroneGps.setText("无人机GPS：" + droneGPS.getLongitude() + "," + droneGPS.getLatitude());
        }

        if (droneGesture != null && debugMode) {
            txtDroneGesture.setText("角度：" + droneGesture.toString());
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_REFRESH:
                    doRefreshUI();
                    break;
                default:
                    break;
            }
        }
    };

    public void setDroneLayoutListener(DroneLayoutListener droneLayoutListener) {
        this.droneLayoutListener = droneLayoutListener;
    }


    public void setDrawEnable(boolean enable) {
        drawEnable = enable;
        if (enable) {
            llIcons.setVisibility(VISIBLE);
        } else {
            llIcons.setVisibility(GONE);
        }
    }

    public void addFixServerGPS(List<HiGPS> hiGPSs) {
        Log4aUtil.d(TAG, "addFixServerGPS");
        for (HiGPS gps : hiGPSs) {
//            Log4aUtil.d(TAG, "addFixServerGPS %s", gps);
            if (gps instanceof GPSTarget) {
                GPSTarget target = (GPSTarget) gps;
                if (target.isEnable()) {
                    int id = target.getTargetId();
                    Log4aUtil.d(TAG, "addFixServerGPS : %s", target);
                    putTemp(id, target);
                }
            }
        }
    }

    public boolean isDrawEnable() {
        return drawEnable;
    }

    @OnClick({R.id.img_pin, R.id.img_pin2, R.id.img_pin3, R.id.img_tag, R.id.img_route, R.id.btn_switch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_switch:
                gpsutil1 = !gpsutil1;
                break;
            case R.id.img_route:
                choosenTarget = Enums.TargetType.TypeDraw_VALUE;
                break;
            case R.id.img_pin:
                choosenTarget = Enums.TargetType.Type0_VALUE;
                break;
            case R.id.img_pin2:
                choosenTarget = Enums.TargetType.Type1_VALUE;
                break;
            case R.id.img_pin3:
                choosenTarget = Enums.TargetType.Type2_VALUE;
                break;
            case R.id.img_tag:
                choosenTarget = Enums.TargetType.TypeTag_VALUE;
                break;
            default:
                break;
        }
        chooseTarget();
    }

    private void chooseTarget() {
        imgPin.setImageResource(R.drawable.pin_icon);
//        imgPin2.setImageResource(0);
//        imgPin3.setImageResource(0);
        imgTag.setImageResource(R.drawable.pin_text);
        imgRoute.setImageResource(R.drawable.route);
        switch (choosenTarget) {
            case Enums.TargetType.Type0_VALUE:
                imgPin.setImageResource(R.drawable.pin_actived);
                break;
            case Enums.TargetType.Type1_VALUE:
                imgPin2.setBackgroundColor(Color.BLUE);
                break;
            case Enums.TargetType.TypeDraw_VALUE:
                imgRoute.setImageResource(R.drawable.route_actived);
                break;
            case Enums.TargetType.Type2_VALUE:
                imgPin3.setBackgroundColor(Color.BLUE);
                break;
            case Enums.TargetType.TypeTag_VALUE:
                imgTag.setImageResource(R.drawable.pin_text_actived);
                break;
            default:
                break;
        }
    }

    /**
     * 更新无人机飞手GPS，主要用于获取飞手高度，计算警员有高度时的相对位置
     *
     * @param gps
     */
    public void setControllerGPS(GPSLocation gps) {
        Log4aUtil.d(TAG, "setControllerGPS : " + gps);
//        this.controllerGPS = gps;
    }

    private void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }


    public static final double GAP = 1;

    @OnClick({R.id.btn_up_x, R.id.btn_down_x, R.id.btn_up_y, R.id.btn_down_y})
    public void onViewClicked2(View view) {
        String x = editRatiox.getText().toString();
        String y = editRatioy.getText().toString();
        if (x.length() == 0) {
            ratioX = 0;
        } else {
            try {
                ratioX = Double.parseDouble(x);
            } catch (Exception e) {
                e.printStackTrace();
                Log4aUtil.e(TAG, "editRatiox error %s", e.getMessage());
            }
        }
        if (y.length() == 0) {
            ratioY = 0;
        } else {
            try {
                ratioY = Double.parseDouble(y);
            } catch (Exception e) {
                e.printStackTrace();
                Log4aUtil.e(TAG, "editRatioY  error %s", e.getMessage());
            }
        }
//        Log4aUtil.d(TAG, "onEditClicked x=%s,y=%s", ratioX, ratioY);
        switch (view.getId()) {
            case R.id.btn_up_x:
                ratioX += GAP;
                break;
            case R.id.btn_down_x:
                ratioX -= GAP;
                break;
            case R.id.btn_up_y:
                ratioY += GAP;
                break;
            case R.id.btn_down_y:
                ratioY -= GAP;
                break;
            default:
                break;
        }
        editRatiox.setText(String.format("%.2f", ratioX));
        editRatioy.setText(String.format("%.2f", ratioY));
        gpsUtil.updateCameraFov(DroneConstants.DEFAULT_FOV + ratioX);
        Log4aUtil.d(TAG, "onEditClicked x=%.2f,y=%.2f", ratioX, ratioY);
    }


    /******************************* 绘制实时路径  *******************************/
}
