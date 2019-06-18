package com.hiscene.drone.ui;

import android.content.Context;

import com.hiscene.drone.R;
import com.hiscene.drone.gps.UserRole;


/**
 * Created by hujun on 2019/4/28.
 */

public class MyPanelView extends UserPanelView {
    private static final String TAG = "MyPanelViewTAG";

    public MyPanelView(Context context, int userType) {
        super(context, userType);
        if (userType == UserRole.DRONE) {
            imgUser.setImageResource(R.drawable.drone_mine);
        } else if (userType == UserRole.POLICE) {
            imgUser.setImageResource(R.drawable.police_mine);
        } else if (userType == UserRole.ADMIN) {
            imgUser.setImageResource(R.drawable.leader_mine);
        }
    }

    /**
     * @param yaw   正北的偏移角度
     * @param angle 手机方向朝正北的偏移角度
     */
    public void updateOrientation(float yaw, float angle) {
        imgUser.setRotation(-yaw + angle);
    }
}
