package com.example.refreshlistviewdemo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.smartbj.R;
import com.example.smartbj.utils.PrintLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RefreshListView extends ListView {

	private LinearLayout mHeadRootView;
	private LinearLayout mRefreshView;
	private LinearLayout mFootView;
	private int mRefreshViewHeight;
	private int mFootViewHeight;
	private View vp_image;

	// 下拉条的三个状态
	private final static int PULL_STATE = 1;
	private final static int RELASE_STATE = 2;
	private final static int LOADING_STATE = 3;

	private int refresh_state = PULL_STATE;
	private float downY = -1;
	private ImageView iv_arr;
	private ProgressBar pd_ring;
	private TextView tv_desc;
	private TextView tv_update;
	private RotateAnimation ra_up;
	private RotateAnimation ra_down;
	private boolean isLoadMore = false;

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 初始化头部刷新
		initHead();
		// 初始化尾部刷新
		initFoot();
		// 初始化动画
		initAnimation();
		initEvent();
	}

	private void initEvent() {
		this.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {// 停止状态

					PrintLog.print(getLastVisiblePosition() + "<>" + getAdapter().getCount());

					if (getLastVisiblePosition() == getAdapter().getCount() - 1 && !isLoadMore) {// 防止刷新更多没有结束，又刷新
						// 状态改变
						isLoadMore = true;
						PrintLog.print("记载更多");
						// 加载更多条现实
						mFootView.setPadding(0, 0, 0, 0);
						// 事件监听
						if (mOnRefreshDataListener != null) {
							mOnRefreshDataListener.loadingMore();
						}
						setSelection(getAdapter().getCount());

					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// 灵敏度高

			}
		});

	}

	private OnRefreshDataListener mOnRefreshDataListener;

	public void setOnRefreshDataListener(OnRefreshDataListener listener) {
		this.mOnRefreshDataListener = listener;
	}

	public interface OnRefreshDataListener {
		void refreshData();

		void loadingMore();
	}

	// 添加子组件
	public void addChildView(View v_carousel) {
		vp_image = v_carousel;
		mHeadRootView.addView(v_carousel);
	}

	public void updateState() {
		System.out.println("isLoadMore"+isLoadMore);
		if (isLoadMore) {
			// 隐藏加载更多菜单
			mFootView.setPadding(0, 0, 0, -mFootViewHeight);
			isLoadMore = false;
		} else {
			updateRefreshState();
		}
	}

	// 下来刷新界面的设置
	public void updateRefreshState() {
		// 改变状态 下拉刷新
		refresh_state = PULL_STATE;
		// 显示箭头
		iv_arr.setVisibility(View.VISIBLE);
		// 隐藏进度条
		pd_ring.setVisibility(View.GONE);
		// 改变文字
		tv_desc.setText("下来刷新");
		// 改变时间
		tv_update.setText(getCurrentTime());
		// 隐藏下拉刷新条
		mRefreshView.setPadding(0, -mRefreshViewHeight, 0, 0);

	}

	@SuppressLint("SimpleDateFormat")
	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	private boolean isCarouselCompleteShow() {
		int[] outLocation = new int[2];
		this.getLocationInWindow(outLocation);
		// 获取lv在屏幕上的y坐标
		int lv_y = outLocation[1];

		vp_image.getLocationInWindow(outLocation);
		// 获取vp在屏幕上的y坐标
		int vp_y = outLocation[1];

		if (lv_y <= vp_y) {
			return true;
		} else {
			return false;
		}

	}

	private void initHead() {
		mHeadRootView = (LinearLayout) View.inflate(getContext(), R.layout.loading_listview_head, null);

		mRefreshView = (LinearLayout) mHeadRootView.findViewById(R.id.rl_listview_refreshview);
		iv_arr = (ImageView) mHeadRootView.findViewById(R.id.iv_listview_head_arr);
		pd_ring = (ProgressBar) mHeadRootView.findViewById(R.id.pb_listview_head_ring);
		tv_desc = (TextView) mHeadRootView.findViewById(R.id.tv_listview_head_desc);
		tv_update = (TextView) mHeadRootView.findViewById(R.id.tv_listview_head_update);

		mRefreshView.measure(0, 0);// 随意测量
		mRefreshViewHeight = mRefreshView.getMeasuredHeight();
		// 隐藏 RelativeLayout设置padding有问题
		mRefreshView.setPadding(0, -mRefreshViewHeight, 0, 0);

		addHeaderView(mHeadRootView);

	}

	// 刷新状态处理
	private void processState() {
		switch (refresh_state) {
		case PULL_STATE:// 下拉刷新的状态
			// 箭头线下动画
			iv_arr.startAnimation(ra_down);
			// 改变文字
			tv_desc.setText("下拉刷新");
			break;
		case RELASE_STATE:// 松开的状态
			// 箭头向上动画
			iv_arr.startAnimation(ra_up);
			// 改变文字
			tv_desc.setText("松开刷新");
			break;
		case LOADING_STATE:// 正在刷新的状态
			// 清除动画
			iv_arr.clearAnimation();
			// 显示进度条
			pd_ring.setVisibility(View.VISIBLE);
			// 隐藏箭头
			iv_arr.setVisibility(View.GONE);
			// 改变文字
			tv_desc.setText("正在刷新");
			break;

		default:
			break;
		}
	}

	/**
	 * 箭头动画
	 */
	private void initAnimation() {
		ra_up = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra_up.setDuration(500);
		ra_up.setFillAfter(true);

		ra_down = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra_down.setDuration(500);
		ra_down.setFillAfter(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();

			break;
		case MotionEvent.ACTION_MOVE:

			if (!isCarouselCompleteShow()) {
				break;
			}

			if (refresh_state == LOADING_STATE) {
				return true;
			}
			if (downY == -1) {
				// 如果down取值失效
				downY = ev.getY();
			}

			float moveY = ev.getY();

			float dy = moveY - downY;
			// 如果是lv的第一个位置，且vp完全显示
			int firstVisiblePosition = getFirstVisiblePosition();
			if (firstVisiblePosition == 0 && dy > 0) {

				float hiddenHeight = -mRefreshViewHeight + dy;

				if (hiddenHeight < 0 && refresh_state != PULL_STATE) {// 只执行一次
					refresh_state = PULL_STATE;
					processState();
				} else if (hiddenHeight >= 0 && refresh_state != RELASE_STATE) {
					refresh_state = RELASE_STATE;
					processState();
				}

				mRefreshView.setPadding(0, (int) hiddenHeight, 0, 0);
				return true;
			}

			break;

		case MotionEvent.ACTION_UP:

			// 松开的时候
			if (refresh_state == PULL_STATE) {
				// 继续隐藏
				mRefreshView.setPadding(0, -mRefreshViewHeight, 0, 0);
			} else if (refresh_state == RELASE_STATE) {
				// 更新到正在刷新的状态
				refresh_state = LOADING_STATE;
				processState();
				// 显示刷新条
				mRefreshView.setPadding(0, 0, 0, 0);
				if (mOnRefreshDataListener != null) {
					mOnRefreshDataListener.refreshData();
				}
			}

			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void initFoot() {
		mFootView = (LinearLayout) View.inflate(getContext(), R.layout.loading_listview_foot, null);

		mFootView.measure(0, 0);// 随意测量
		mFootViewHeight = mFootView.getMeasuredHeight();
		// 隐藏 RelativeLayout设置padding有问题
		mFootView.setPadding(0, 0, 0, -mFootViewHeight);
		addFooterView(mFootView);

	}

	public RefreshListView(Context context) {
		this(context, null);
	}

}
