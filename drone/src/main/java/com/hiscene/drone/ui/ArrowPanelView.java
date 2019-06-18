package com.hiscene.drone.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupMenu;

import com.hiscene.drone.Log4aUtil;
import com.hiscene.drone.R;
import com.hiscene.drone.gps.ArrowTarget;
import com.hiscene.drone.gps.MathUtil;
import com.hiscene.drone.gps.UserRole;

/**
 * Created by hujun on 2019/5/18.
 * 在视频上直接拉取指挥路径
 */

public class ArrowPanelView extends UserPanelView {
    private static final String TAG = "ArrowPanelViewTAG";
    //图标的高度
    private int height = 0;

    private float endX = 0, endY = 0;

    //x方向移动一点，放在用户手指位置
    public static final int DELTA_X = 50;

    private PopupMenu popupMenu;
    private ArrowTarget arrowTarget;

    private Context mContext;

    public ArrowPanelView(Context context) {
        super(context, UserRole.TARGET);
        mContext = context;
        initView();
    }

    private void initView() {
        rlSmallBoard.setVisibility(GONE);
        imgUser.setImageResource(R.drawable.route_arrow);
        imgUser.setClickable(true);
        imgUser.setOnClickListener(v -> initPopMenu());

        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        //只需要获取一次高度，获取后移除监听器
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        //这里高度应该定义为成员变量，定义为局部为展示代码方便
                        viewCreated();
                    }

                });
    }


    /**
     * 生成点击AR标注弹框
     */
    private void initPopMenu() {
        Log4aUtil.d(TAG, "initPopMenu");
        if (popupMenu == null) {
            popupMenu = new PopupMenu(mContext, imgUser);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu2, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_del:
                        if (onEventListener != null) {
                            onEventListener.onDelete(arrowTarget);
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

    public void setArrowTarget(ArrowTarget arrowTarget) {
        this.arrowTarget = arrowTarget;
        setStart((float) arrowTarget.getX(), (float) arrowTarget.getY());
    }

    public ArrowTarget getArrowTarget() {
        return arrowTarget;
    }

    //更新尾部
    @Override
    public void update(double x, double y) {
        if (height == 0) {
            endX = (float) x;
            endY = (float) y;
            return;
        }
        float startX = getX();
        float startY = getY();
        x = x - 50;
        y = (float) (y - height * 0.5);

        setPivotX(0);
        setPivotY((float) (height * 0.5));
        double angle = MathUtil.calATan(x - startX, y - startY);
        angle = Math.toDegrees(angle);
        setRotation((float) angle);
        double len = MathUtil.getDistance(x, y, startX, startY);
        ViewGroup.LayoutParams params = imgUser.getLayoutParams();
        params.width = (int) len;
        imgUser.setLayoutParams(params);
    }

    private void viewCreated() {
        height = getHeight();
        Log4aUtil.d(TAG, "viewCreated h=" + height);
        setY((float) (getY() - height * 0.5));
        if (endY != 0 || endX != 0) {
            update(endX, endY);
        }
    }

    //更新头部
    public void setStart(float x, float y) {
        if (height == 0) {
            setY(y);
        } else {
            setY((float) (y - height * 0.5));
        }
        setX(x - DELTA_X);

    }

    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public interface OnEventListener {
        void onDelete(ArrowTarget target);
    }
}
