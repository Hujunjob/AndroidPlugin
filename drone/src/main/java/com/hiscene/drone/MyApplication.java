package com.hiscene.drone;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * Created by hujun on 2019-06-18.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplicationTAG";
    private static BaseProduct mProduct;

    public static synchronized BaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getProduct();
        }
        return mProduct;
    }

    public static synchronized void updateProduct(BaseProduct product) {
        mProduct = product;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "attachBaseContext: ");
        com.secneo.sdk.Helper.install(this);
    }
}
