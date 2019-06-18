package com.hiscene.drone.droneplugin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hiscene.drone.Log4aUtil;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.GPSSignalLevel;
import dji.common.gimbal.Attitude;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.airlink.AirLink;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.BluetoothProductConnector;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

/**
 * Created by hujun on 2019/4/15.
 */

public class DroneManager {
    private static final String TAG = "DroneManagerTAG";
    private static DroneManager droneManager;
    private DroneRegisterListener registerListener;
    private DroneStatusListener statusListener;
    private Context mContext;
    private boolean register = false;

    //更新GPS和转角信息频率
    private long updateGap = 300;

    private long lastGpsTime = 0;
    private long lastGestureTime = 0;

    private DroneManager() {

    }

    public static DroneManager getInstance() {
        if (droneManager == null) {
            droneManager = new DroneManager();
        }
        return droneManager;
    }

    public boolean isRegister() {
        return register;
    }


    /**
     * 初始化大疆SDK
     *
     * @param context
     * @param listener
     */
    public void init(Context context, DroneRegisterListener listener) {
        this.registerListener = listener;
        if (listener == null) {
            return;
        }
        Log4aUtil.d(TAG, "init: ");
        mContext = context;

        AsyncTask.execute(() -> {
            Log4aUtil.d(TAG, "开始注册DJI");
            DJISDKManager.getInstance().registerApp(mContext, new DJISDKManager.SDKManagerCallback() {
                @Override
                public void onRegister(DJIError djiError) {
                    if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                        register = true;
                        registerListener.onRegisterSuccess();
                        DJILog.e("App registration", DJISDKError.REGISTRATION_SUCCESS.getDescription());
                        DJISDKManager.getInstance().startConnectionToProduct();
                        checkLoginStatus();
                        Log4aUtil.d(TAG, "onRegister: 注册成功");
                    } else {
                        registerListener.onError(djiError.getDescription());
                        Log4aUtil.d(TAG, "onRegister: 注册失败");
                    }
                    Log.v(TAG, "onRegister getDescription:" + djiError.getDescription());
                }

                @Override
                public void onProductDisconnect() {
                    Log4aUtil.d(TAG, "onProductDisconnect");
                    registerListener.onProductDisconnect();
                }

                @Override
                public void onProductConnect(BaseProduct baseProduct) {
                    Log4aUtil.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
                    registerListener.onProductConnect(baseProduct);
                    productConnect();
                }

                @Override
                public void onComponentChange(BaseProduct.ComponentKey componentKey,
                                              BaseComponent oldComponent,
                                              BaseComponent newComponent) {
                    if (newComponent != null) {
                        newComponent.setComponentListener(b ->
                                Log4aUtil.d(TAG, "onConnectivityChange %s,connect=%s",
                                        newComponent.toString(), b));
                    }
                    if (newComponent != null && newComponent.isConnected() && newComponent instanceof AirLink) {
                        productConnect();
                    }
                    Log4aUtil.d(TAG,
                            String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                    componentKey,
                                    oldComponent,
                                    newComponent));
                }

            });
        });
    }

    public void connect() {
        Log4aUtil.d(TAG, "connect");
        DJISDKManager.getInstance().startConnectionToProduct();
    }

    /**
     * 登录大疆账号
     */
    public void login() {
        if (registerListener == null) {
            return;
        }
        Log4aUtil.d(TAG, "login: ");
        UserAccountManager.getInstance().logIntoDJIUserAccount(mContext, new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
            @Override
            public void onSuccess(UserAccountState userAccountState) {
                Log4aUtil.d(TAG, "onSuccess: 登录成功");
                registerListener.onLoginSuccess(userAccountState);
            }

            @Override
            public void onFailure(DJIError djiError) {
                Log.e(TAG, "onFailure: 登录失败:" + djiError.getDescription());
                registerListener.onLoginFailure("登录失败:" + djiError.getDescription());
            }
        });
    }

    /**
     * 登出账号
     */
    public void logout() {
        if (registerListener == null) {
            return;
        }
        Log4aUtil.d(TAG, "logout: ");
        UserAccountManager.getInstance().logoutOfDJIUserAccount(djiError -> {
            Log4aUtil.d(TAG, "登出");
            registerListener.onLogout();
        });
    }

    public void setStatusListener(DroneStatusListener statusListener) {
        this.statusListener = statusListener;
    }


    /**
     * Gets instance of the specific product connected after the
     * API KEY is successfully validated. Please make sure the
     * API_KEY has been added in the Manifest
     */
    public synchronized BaseProduct getProductInstance() {
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        return product;
    }

    public synchronized BluetoothProductConnector getBluetoothProductConnector() {
        BluetoothProductConnector bluetoothConnector = DJISDKManager.getInstance().getBluetoothProductConnector();
        return bluetoothConnector;
    }

    public boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public boolean isHandHeldConnected() {
        return getProductInstance() != null && getProductInstance() instanceof HandHeld;
    }

    public synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) {
            return null;
        }
        return (Aircraft) getProductInstance();
    }

    public synchronized HandHeld getHandHeldInstance() {
        if (!isHandHeldConnected()) {
            return null;
        }
        return (HandHeld) getProductInstance();
    }


    public synchronized Camera getCameraInstance() {
        if (getProductInstance() == null) {
            return null;
        }

        Camera camera = null;

        if (getProductInstance() instanceof Aircraft) {
            camera = ((Aircraft) getProductInstance()).getCamera();

        } else if (getProductInstance() instanceof HandHeld) {
            camera = ((HandHeld) getProductInstance()).getCamera();
        }

        return camera;
    }


    private void productConnect() {
        Log4aUtil.d(TAG, "productConnect");
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        if (product != null) {
            Log4aUtil.d(TAG, "productConnect success");
            registerGimbal(product);
            registerBattery(product);
            registerFlightState(product);
        } else {
            Log4aUtil.e(TAG, "productConnect fail");
            registerListener.onError("连接失败");
        }
    }

    private void registerGimbal(BaseProduct product) {
        Gimbal gimbal = product.getGimbal();
        if (gimbal == null) {
            registerListener.onError("陀螺损坏");
        } else {
            gimbal.setStateCallback(gimbalState -> {
                if (statusListener != null) {
                    Attitude attitude = gimbalState.getAttitudeInDegrees();
//                    Log4aUtil.d(TAG, "registerGimbal pitch=%s,roll=%s,yaw=%s",
//                            attitude.getPitch(), attitude.getRoll(), attitude.getYaw());
                    if (lastGestureTime == 0 || System.currentTimeMillis() - lastGestureTime > updateGap) {
                        statusListener.onCameraAttitude(attitude.getPitch(), attitude.getRoll(), attitude.getYaw());
                        lastGestureTime = System.currentTimeMillis();
                    }
                }
            });

        }
    }

    private void registerBattery(BaseProduct product) {
        Battery battery = product.getBattery();
        if (battery == null) {
            registerListener.onError("电池损坏");
        } else {
            battery.setStateCallback(batteryState -> {
                if (statusListener != null) {
                    statusListener.onBatteryPercent(batteryState.getChargeRemainingInPercent());
                }
            });
        }
    }

    private void registerFlightState(BaseProduct product) {
        final FlightController flightController = ((Aircraft) product).getFlightController();
        if (flightController == null) {
            registerListener.onError("GPS损坏");
        } else {
            flightController.setStateCallback(flightControllerState -> {
                GPSSignalLevel signalLevel = flightControllerState.getGPSSignalLevel();
                double latitude = flightControllerState.getAircraftLocation().getLatitude();
                double longitude = flightControllerState.getAircraftLocation().getLongitude();
                float altitude = flightControllerState.getAircraftLocation().getAltitude();
                DroneStatus.GPSLEVEL gpslevel = DroneStatus.GPSLEVEL.NONE;
                switch (signalLevel) {
                    case NONE:
                        gpslevel = DroneStatus.GPSLEVEL.NONE;
                        break;
                    case LEVEL_0:
                        gpslevel = DroneStatus.GPSLEVEL.NONE;
                        break;
                    case LEVEL_1:
                        gpslevel = DroneStatus.GPSLEVEL.LOW;
                        break;
                    case LEVEL_2:
                        gpslevel = DroneStatus.GPSLEVEL.NORMAL;
                        break;
                    case LEVEL_3:
                    case LEVEL_4:
                        gpslevel = DroneStatus.GPSLEVEL.GOOD;
                        break;
                    case LEVEL_5:
                        gpslevel = DroneStatus.GPSLEVEL.VERY_GOOD;
                        break;
                    default:
                        break;
                }
                if (statusListener != null) {
                    if (lastGpsTime == 0 || System.currentTimeMillis() - lastGpsTime > updateGap) {
                        statusListener.onGPSLocation(gpslevel, altitude, longitude, latitude);
                        statusListener.onVelocity(flightControllerState.getVelocityX(), flightControllerState.getVelocityY(), flightControllerState.getVelocityZ());
                        lastGpsTime = System.currentTimeMillis();
                    }
                }
            });
        }
    }

    private void checkLoginStatus() {
        UserAccountState userAccountState = UserAccountManager.getInstance().getUserAccountState();
        Log4aUtil.d(TAG, "checkLoginStatus " + userAccountState);
        switch (userAccountState) {
            case UNKNOWN:
//                registerListener.onLoginFailure("登陆状态有问题");
                break;
            case AUTHORIZED:
                registerListener.onLoginSuccess(userAccountState);
                break;
            case NOT_AUTHORIZED:
                registerListener.onLoginSuccess(userAccountState);
                break;
            case TOKEN_OUT_OF_DATE:
                registerListener.onLoginFailure("登陆过期，请重新登陆");
                break;
            case NOT_LOGGED_IN:
                registerListener.onLoginFailure("没有登陆，请重新登录");
                break;
            default:
                Log4aUtil.d(TAG, "checkLoginStatus: 未登录");
                break;
        }
    }

}
