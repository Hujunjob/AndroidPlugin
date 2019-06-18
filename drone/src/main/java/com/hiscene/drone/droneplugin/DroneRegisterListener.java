package com.hiscene.drone.droneplugin;

import dji.common.useraccount.UserAccountState;
import dji.sdk.base.BaseProduct;

/**
 * Created by hujun on 2019/4/15.
 */

public interface DroneRegisterListener {
    /**
     * 应用注册失败
     */
    void onError(String error);

    /**
     * 无人机断开连接
     */
    void onProductDisconnect();

    /**
     * 无人机连接上
     * @param baseProduct
     */
    void onProductConnect(BaseProduct baseProduct);

    /**
     * 用户登录成功
     * @param userAccountState
     */
    void onLoginSuccess(UserAccountState userAccountState);

    /**
     * 用户登录失败
     */
    void onLoginFailure(String error);

    /**
     * 用户登出
     */
    void onLogout();

    /**
     * sdk初始化成功
     */
    void onRegisterSuccess();

}
