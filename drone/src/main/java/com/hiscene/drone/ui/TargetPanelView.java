package com.hiscene.drone.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.hiscene.drone.Log4aUtil;
import com.hiscene.drone.R;
import com.hiscene.drone.droneplugin.Enums;
import com.hiscene.drone.gps.GPSTarget;
import com.hiscene.drone.gps.TargetType;
import com.hiscene.drone.gps.UserRole;

/**
 * Created by hujun on 2019/5/5.
 */

public class TargetPanelView extends UserPanelView {
    private static final String TAG = "TargetPanelViewTAG";
    private int targetType;
    private Context mContext;
    private PopupMenu popupMenu;

    private GPSTarget gpsTarget;

    public TargetPanelView(Context context) {
        super(context, UserRole.TARGET);
        iconUser.setVisibility(GONE);
        mContext = context;
        imgUser.setClickable(true);
        imgUser.setOnClickListener(v -> initPopMenu());
        rlSmallBoard.setVisibility(GONE);
        txtUserName.setText("目标点");
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
        switch (targetType) {
            case TargetType
                    .Type0:
                imgUser.setImageResource(R.drawable.pin);
                break;
            case TargetType
                    .Type1:
                imgUser.setImageResource(R.drawable.policeicon1);
                break;
            case TargetType
                    .Type2:
                imgUser.setImageResource(R.drawable.policeicon2);
                break;
            case TargetType
                    .TypeTag:
                imgUser.setImageResource(R.drawable.video_pin_text);
                txtTargetName.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void updateName(String name) {
        if (name != null && name.length() > 1 && !super.name.equals(name)) {
            if (targetType == TargetType.TypeTag) {
                txtTargetName.setText(name);
                if (name.length() <= 7) {
                    imgUser.setImageResource(R.drawable.video_pin_text_1);
                } else {
                    imgUser.setImageResource(R.drawable.video_pin_text_2);
                }
                super.name = name;
            } else {
                super.updateName(name);
            }
        }
    }

    @Override
    public void update(double x, double y) {
        if (x == 0 && y == 0) {
            return;
        }
        //如果是标签，则需要以左下角的小圆点为中心
        if (targetType == TargetType.TypeTag) {
            float nx = (float) (x - 20);
            float ny = (float) (y - (double) this.getHeight() + 10);
            this.setX(nx);
            this.setY(ny);
//            Log4aUtil.d(TAG, "update x=%s,y=%s,nx=%s,ny=%s", x, y, nx, ny);
        } else {
            super.update(x, y);
        }
    }

    /**
     * 生成点击AR标注弹框
     */
    private void initPopMenu() {
        Log4aUtil.d(TAG, "initPopMenu");
        if (popupMenu == null) {
            popupMenu = new PopupMenu(mContext, imgUser, Gravity.CENTER);
            popupMenu.getMenuInflater().inflate(R.menu.popup_medu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_del:
                        if (onEventListener != null) {
                            onEventListener.onDelete(gpsTarget, (int) getUserId());
                        }
                        break;
                    case R.id.action_edit:
                        if (targetType == Enums.TargetType.TypeTag_VALUE) {
                            showEditAlert();
                        } else {
                            Toast.makeText(mContext, "该标签不能编辑", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            });
        }
        popupMenu.show();
    }

    private void showEditAlert() {
        LayoutInflater factory = LayoutInflater.from(mContext);//提示框
        final View view = factory.inflate(R.layout.item_edit_dialog, null);//这里必须是final的
        final EditText edit =  view.findViewById(R.id.editText);//获得输入框对象

        new AlertDialog.Builder(mContext)
                .setTitle("编辑云端标签")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        (dialog, which) -> {
                            //事件
                            String tag = edit.getText().toString();
                            if (tag.length() < 2) {
                                Toast.makeText(mContext, "输入内容太短", Toast.LENGTH_LONG).show();
                            } else if (tag.length() > 14) {
                                Toast.makeText(mContext, "输入内容太长", Toast.LENGTH_LONG).show();
                            } else {
                                if (onEventListener != null) {
                                    Log4aUtil.d(TAG, "showEditAlert : %s", tag);
                                    gpsTarget.setInfo(tag);
                                    gpsTarget.setName(tag);
                                    updateName(tag);
                                    onEventListener.onChangeInfo(gpsTarget);
                                }
                            }
                        })
                .setNegativeButton("取消", null).create().show();
    }

    public GPSTarget getGpsTarget() {
        return gpsTarget;
    }

    public void setGpsTarget(GPSTarget gpsTarget) {
        this.gpsTarget = gpsTarget;
    }

    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public interface OnEventListener {
        void onDelete(GPSTarget target, int id);

        void onChangeInfo(GPSTarget target);
    }
}
