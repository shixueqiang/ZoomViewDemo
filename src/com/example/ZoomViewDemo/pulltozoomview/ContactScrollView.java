package com.example.ZoomViewDemo.pulltozoomview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.ZoomViewDemo.R;

/**
 * Created by shixq on 2016/11/11.
 */
public class ContactScrollView extends LinearLayout {
    private String TAG = "ContactScrollView";
    private Context mContext;
    private float mInitialMotionX, mLastMotionX;
    private float mInitialMotionY, mLastMotionY;
    private int mTouchSlop;
    private ImageView header;
    private TextView userName;
    private int headerWidth, headerHeight;
    private AspScrollFade scrollFade;
    public static final float MAX_MOVE_DIFF = 240.0f;

    public ContactScrollView(Context context) {
        this(context, null);
    }

    public ContactScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public ContactScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init(mContext);
    }

    private void inflateView(Context context) {
        mContext = context;
        mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void init(Context context) {
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        header = (ImageView) findViewById(R.id.iv_user_head);
        userName = (TextView) findViewById(R.id.tv_user_name);
        ViewGroup.LayoutParams params = header.getLayoutParams();
        headerWidth = params.width;
        headerHeight = params.height;
        Log.e(TAG, "headerWidth:" + headerWidth);
        scrollFade = (AspScrollFade) findViewById(R.id.asp_scrollfade);
        scrollFade.setParentScrollView(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (canPullDown() || canPullUp())
                return true;
            else
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mLastMotionX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                float diff = y - mLastMotionY;
                float absDiff = Math.abs(diff);
                float oppositeDiff = x - mLastMotionX;
                if (absDiff > mTouchSlop && absDiff > Math.abs(oppositeDiff)) {
                    if (diff < -1f && canPullUp()) {
                        Log.e(TAG, "getScrollY1:" + getScrollY() + "diff:" + ((int) -diff));
                        //上推
//                        scrollTo(0, (int) -diff);
                        layout((int) (getLeft()), (int) (getTop() + diff), (int) (getRight()), (int) (getBottom() + diff));
                        invalidate();
                        pullEvent((int) diff);
                    } else if (diff > 1f && canPullDown()) {
                        Log.e(TAG, "getScrollY2:" + getScrollY() + "diff:" + ((int) -diff));
                        //下拉
//                        scrollTo(0, (int) (-diff + MAX_MOVE_DIFF));
                        layout((int) (getLeft()), (int) (getTop() + diff), (int) (getRight()), (int) (getBottom() + diff));
                        invalidate();
                        pullEvent((int) diff);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastMotionY = ev.getY();
                mLastMotionX = ev.getX();
                break;
        }
        return true;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        if (t >= MAX_MOVE_DIFF) {
//            scrollTo(0, (int) MAX_MOVE_DIFF);
//            userName.setTextColor(Color.argb(0, 255, 255, 255));
//        }
    }

    public boolean canPullDown() {
        return getTop() > 0;
    }

    public boolean canPullUp() {
        return getTop() < MAX_MOVE_DIFF;
    }

    private void pullEvent(int diff) {
        //缩放头像 渐隐名字
        if (diff < -1) {
            ViewGroup.LayoutParams params = header.getLayoutParams();
            int newValue = headerWidth + diff / 2;
            if (newValue < 0)
                newValue = 0;
            params.width = params.height = newValue;
            header.setLayoutParams(params);
            int alpha = (int) (255 * (MAX_MOVE_DIFF - Math.abs(diff) / MAX_MOVE_DIFF));
            userName.setTextColor(Color.argb(alpha, 255, 255, 255));
        } else if (diff > 1f) {
            ViewGroup.LayoutParams params = header.getLayoutParams();
            int newValue = diff;
            if (newValue > headerWidth)
                newValue = headerWidth;
            params.width = params.height = newValue;
            header.setLayoutParams(params);
            int alpha = (int) (255 * (Math.abs(diff) / MAX_MOVE_DIFF));
            if (alpha > 255)
                alpha = 255;
            userName.setTextColor(Color.argb(alpha, 255, 255, 255));
        }
    }
}
