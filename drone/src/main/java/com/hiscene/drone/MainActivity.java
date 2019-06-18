package com.hiscene.drone;

import android.app.Activity;
import android.os.Bundle;

import com.hiscene.drone.droneplugin.DroneManager;
import com.hiscene.drone.droneplugin.DroneRegisterListener;

import dji.common.useraccount.UserAccountState;
import dji.sdk.base.BaseProduct;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivityTAG";
    private DroneManager droneManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        droneManager = DroneManager.getInstance();

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            droneManager.init(this, droneRegisterListener);
//            droneManager.login();
        });
    }

    private DroneRegisterListener droneRegisterListener = new DroneRegisterListener() {
        @Override
        public void onError(String error) {
            Log4aUtil.d(TAG, "onError");
        }

        @Override
        public void onProductDisconnect() {
            Log4aUtil.d(TAG, "onProductDisconnect");
        }

        @Override
        public void onProductConnect(BaseProduct baseProduct) {
            Log4aUtil.d(TAG, "onProductConnect");
        }

        @Override
        public void onLoginSuccess(UserAccountState userAccountState) {
            Log4aUtil.d(TAG, "onLoginSuccess");
        }

        @Override
        public void onLoginFailure(String error) {
            Log4aUtil.d(TAG, "onLoginFailure");
        }

        @Override
        public void onLogout() {
            Log4aUtil.d(TAG, "onLogout");
        }

        @Override
        public void onRegisterSuccess() {
            Log4aUtil.d(TAG, "onRegisterSuccess");
        }
    };
}
