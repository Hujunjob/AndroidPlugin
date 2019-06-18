package com.hiscene.drone.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiscene.drone.Log4aUtil;
import com.hiscene.drone.R;
import com.hiscene.drone.gps.HiGPS;
import com.hiscene.drone.gps.UserRole;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hujun on 2019/4/28.
 */

public class UserPanelView extends LinearLayout {
    private static final String TAG = "UserPanelViewTAG";
    @BindView(R.id.img_user)
    ImageView imgUser;
    @BindView(R.id.icon_user)
    ImageView iconUser;
    @BindView(R.id.txt_user_name)
    TextView txtUserName;
    @BindView(R.id.rl_small_board)
    RelativeLayout rlSmallBoard;
    @BindView(R.id.txt_longitude)
    TextView labelLongitude;
    @BindView(R.id.txt_latitude)
    TextView labelLatitude;

    private final int userType;
    @BindView(R.id.txt_department)
    TextView txtDepartment;
    @BindView(R.id.txt_target_name)
    TextView txtTargetName;
    private long userId;
    protected String name = "";

    private boolean lastOnLine = true;

    public UserPanelView(Context context, int userType) {
        super(context);
        this.userType = userType;

        View view = LayoutInflater.from(context).inflate(R.layout.item_user_panel, this);
        ButterKnife.bind(this, view);

        initView(context);

    }

    public int getUserType() {
        return userType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    private void initView(Context context) {
        switch (userType) {
            case UserRole.DRONE:
                imgUser.setImageResource(R.drawable.drone_dot);
                iconUser.setImageResource(R.drawable.drone_icon);
                break;
            case UserRole.ADMIN:
                imgUser.setImageResource(R.drawable.leader_dot);
                iconUser.setImageResource(R.drawable.leader_icon);
                break;
            case UserRole.POLICE:
                imgUser.setImageResource(R.drawable.police_dot);
                iconUser.setImageResource(R.drawable.police_icon);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log4aUtil.d(TAG, "id=%s,onMeasure w=%d,h=%d",userId, widthMeasureSpec, heightMeasureSpec);
    }

    public void update(double x, double y) {
        if (x == 0 && y == 0) {
            return;
        }

        float nx = (float) (x - (double) this.getWidth() / 2.0);
        float ny = (float) (y - (double) this.getHeight() / 2.0);
        this.setX(nx);
        this.setY(ny);
//        Log4aUtil.d(TAG, "update  x=%s,y=%s,nx=%s,ny=%s,width=%s,height=%s", x,y,nx,ny);
    }

    public void setInfo(HiGPS hiGPS) {
        if (hiGPS != null) {
            labelLatitude.setText("纬度：" + hiGPS.getLatitude());
            labelLongitude.setText("经度：" + hiGPS.getLongitude());
            if (userType == UserRole.DRONE) {
                txtUserName.setText("无人机");
            } else {
                if (hiGPS.getName() != null && hiGPS.getName().length() > 1) {
                    if (hiGPS.isEnable() && !lastOnLine) {
                        imgUser.setImageResource(R.drawable.police_dot);
                    }else if (!hiGPS.isEnable() && lastOnLine){
                        imgUser.setImageResource(R.drawable.police_offline);
                    }
                    lastOnLine = hiGPS.isEnable();
                    txtUserName.setText(hiGPS.getName());
                    txtTargetName.setText(hiGPS.getName());
                }
            }
        }
    }


    @OnClick(R.id.rl_small_board)
    public void onRlSmallBoardClicked() {
        if (labelLongitude.getVisibility() == VISIBLE) {
            labelLongitude.setVisibility(GONE);
            labelLatitude.setVisibility(GONE);
        } else {
            labelLongitude.setVisibility(VISIBLE);
            labelLatitude.setVisibility(VISIBLE);
        }
    }

    public void updateName(String name) {
        if (name != null && name.length() > 1 && !this.name.equals(name)) {
            txtUserName.setText(name);
            this.name = name;
            Log4aUtil.d(TAG, "updateName %s", name);
        }
    }
}
