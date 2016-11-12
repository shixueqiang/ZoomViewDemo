package com.example.ZoomViewDemo.pulltozoomview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("DrawAllocation")
public class AspScrollFade extends ScrollView implements GestureDetector.OnGestureListener {

	/**
	 * 下拉头的View
	 */
	private View inner;

	private static final int size = 3;
	private float y;
	private Rect normal = new Rect();;
	private GestureDetector mGestureDetector;
	private boolean isFadeScrolled = false;
	private boolean isLeftOrRight = false;
	private boolean isScrollUp = false;
	private ContactScrollView parentScrollView;
	
	public AspScrollFade(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public AspScrollFade(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AspScrollFade(Context context) {
		super(context);
		init(context);
	}

	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			inner = getChildAt(0);
			inner.addOnLayoutChangeListener(mLayoutChangeListener);
		}
	}
	
	private OnLayoutChangeListener mLayoutChangeListener = new OnLayoutChangeListener() {
		
		@Override
		public void onLayoutChange(View v, int left, int top, int right,
				int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) 
		{
			
		}
	};
	
	private float distanceY=0;
	
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		if(parentScrollView != null) {
			parentScrollView.requestDisallowInterceptTouchEvent(true);
		}
		mGestureDetector.onTouchEvent(ev);
        if (inner == null) {  
            return super.dispatchTouchEvent(ev);  
        } else {  
        	switch(ev.getAction())
        	{
        	case MotionEvent.ACTION_UP:
        	{
        		Log.e("rcs", "Touch Up");
        		if (isNeedAnimation()) {
    				animation();
    			}
    			isFadeScrolled = false;
        	}
        		break;
        	}
        	if(!isLeftOrRight){
        		commOnTouchEvent(ev);
           }  
        }
		return super.dispatchTouchEvent(ev);
	}

	public void commOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			y = ev.getY();
			break;
//		case MotionEvent.ACTION_UP:
//			if (isNeedAnimation()&&isFadeScrolled) {
//				animation();
//			}
//			isFadeScrolled = false;
//			break;
		case MotionEvent.ACTION_MOVE:
			
			
			final float preY = y;
			float nowY = ev.getY();
			if(nowY - y < 0) {
				if (parentScrollView.getScrollY() >= ContactScrollView.MAX_MOVE_DIFF) {
					parentScrollView.requestDisallowInterceptTouchEvent(true);
				}else {
					parentScrollView.requestDisallowInterceptTouchEvent(false);
				}
			}else if(nowY - y > 0) {
				if(parentScrollView.getScrollY() <= 0) {
					parentScrollView.requestDisallowInterceptTouchEvent(true);
				}else {
					parentScrollView.requestDisallowInterceptTouchEvent(false);
				}
			}
			/**
			 * size=4 表示 拖动的距离为屏幕的高度的1/4
			 */
			int deltaY = (int) (preY - nowY) / size;
			// 滚动
			// scrollBy(0, deltaY);
			y = nowY;
			
			
			int max =inner.getMeasuredHeight();
			int sum = ((getScrollY()+getHeight()));
			// 当滚动到最上或者最下时就不会再滚动，这时移动布局
			if (isNeedMove()) {
				if (normal.isEmpty()) {
//					// 保存正常的布局位置
					normal.set(inner.getLeft(), inner.getTop(),inner.getRight(), inner.getBottom());
					return;
				}else{
					if(isFirst&&normal.bottom!=inner.getBottom())
					{
						isFirst = false;
						normal.set(inner.getLeft(), inner.getTop(),inner.getRight(), inner.getBottom());
					}
				}
				int yy = inner.getTop() - deltaY;

				// 移动布局
				inner.layout(inner.getLeft(), yy, inner.getRight(),inner.getBottom() - deltaY);
//				MarginLayoutParams lp = (MarginLayoutParams)inner.getLayoutParams();
//				int distance = ((-(int)this.distanceY)/2+lp.topMargin);
//				if(Math.abs(distanceY)>1&&Math.abs(distance)<this.getHeight()/4){
//					lp.setMargins(lp.leftMargin, distance,lp.rightMargin,lp.bottomMargin);
//					inner.setLayoutParams(lp);
//				}else{
//					animation();
//				}
				
			}else{
//				MarginLayoutParams lp = (MarginLayoutParams)inner.getLayoutParams();
//				lp.setMargins(lp.leftMargin, lp.topMargin,lp.rightMargin,lp.bottomMargin);
//				inner.setLayoutParams(lp);
			}
//			Log.e("rcs","distanceY ACTION_MOVE"+y);
			break;
		default:
			break;
		}
	}

	public void animation() {
		
		if(Math.abs(inner.getTop())<4)return;
		MarginLayoutParams lp = (MarginLayoutParams)inner.getLayoutParams();
//		if(isScrollBottom() ||isScrollTop()){
		lp.setMargins(0, 0, 0, lp.bottomMargin);
		inner.setLayoutParams(lp);
		// 开启移动动画
		TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),normal.top);
		ta.setDuration(200);
		ta.setAnimationListener(myAnimation);
		inner.startAnimation(ta);
		// 设置回到正常的布局位置
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);
		
//		MarginLayoutParams lp = (MarginLayoutParams)inner.getLayoutParams();
//		lp.setMargins(0, 0, 0, lp.bottomMargin);
//		inner.setLayoutParams(lp);
		
		normal.setEmpty();
//		}
	}
	
	public void requestLayouts()
	{
//		requestLayout();
//		inner.requestLayout();
	}
	
	private AnimationListener myAnimation= new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
//			MarginLayoutParams lp = (MarginLayoutParams)inner.getLayoutParams();
//			lp.setMargins(0, 0, 0, lp.bottomMargin);
//			inner.setLayoutParams(lp);
		}
	};

	// 是否需要开启动画
	public boolean isNeedAnimation() {
		
		Log.e("rcs", "isNeedAnimation inner:"+inner.getTop()+" normal isEmpty: bottom:"+normal.bottom+" top:"+normal.top);
		return (isScrollTop()||isScrollBottom());
	}
	
	private boolean isFirst = true;
	// 是否需要移动布局
	public boolean isNeedMove() {
		int offset = inner.getMeasuredHeight() - getMeasuredHeight();
		int scrollY = getScrollY();
		int IH = inner.getMeasuredHeight();
		int SH = (scrollY+getHeight()+50);
		if (scrollY == 0 || scrollY== (offset+this.getPaddingBottom()+this.getPaddingTop())
				/*||(SH>=IH&&this.distanceY>0)*/) {
			Log.e("rcs", scrollY+" -"+(offset+this.getPaddingBottom()+this.getPaddingTop()));
			return true;
		}
		return false;
	}

	private OnScrollChangeListener listener;

	private Context mContext;
	private void init(Context context) {
		if (context instanceof OnScrollChangeListener) {
			listener = (OnScrollChangeListener) context;
		}
		this.mContext = context;
		mGestureDetector= new GestureDetector(context,this);
	}

	public interface OnScrollChangeListener {
		public void onScrollChanged(int l, int t, int ol, int ot);
	}

	@Override
	protected void onScrollChanged(int l, int t, int ol, int ot) {
		if (listener != null) {
			listener.onScrollChanged(l, t, ol, ot);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		isFadeScrolled = true;
		distanceY = 0;
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		isFadeScrolled = true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		this.distanceY = distanceY;
		if(Math.abs(distanceX)>Math.abs(distanceY))
		{
			isLeftOrRight = true;
			isScrollUp = false;
		}else{
			isLeftOrRight = false;
			if(distanceY>0)
			{
				isScrollUp = true;
				Log.e("rcs", "向上拖222");
			}else{
				Log.e("rcs", "向下拉111");
				isScrollUp = false;
			}
		}
		
		
		
		return false;
	}

	
	private boolean isScrollBottom()
	{
//		return getScrollY()==Math.abs(this.getMeasuredHeight()-inner.getMeasuredHeight()-this.getPaddingBottom()-this.getPaddingTop());
		return !normal.isEmpty();
	}
	
	private boolean isScrollTop()
	{
		return inner.getTop()>0;
	}
	
	
	
	
	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public void setParentScrollView(ContactScrollView parentScrollView) {
		this.parentScrollView = parentScrollView;
	}
}
