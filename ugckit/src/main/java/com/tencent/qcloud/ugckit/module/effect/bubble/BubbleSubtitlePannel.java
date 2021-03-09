package com.tencent.qcloud.ugckit.module.effect.bubble;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tencent.qcloud.ugckit.R;
import com.tencent.qcloud.ugckit.component.circlebmp.TCCircleView;
import com.tencent.qcloud.ugckit.component.seekbar.TCColorView;


import java.util.List;

/**
 * 配置气泡字幕样式、以及字体颜色的控件
 */
public class BubbleSubtitlePannel extends FrameLayout implements IBubbleSubtitlePannel, BubbleAdapter.OnItemClickListener, View.OnClickListener, TCColorView.OnSelectColorListener {
    private View                mContentView;
    private RecyclerView        mRecycleBubbles;
    private BubbleAdapter       mBubbleAdapter;
    private List<TCBubbleInfo>  mBubbles;
    private ImageView           mImageClose;

    @Nullable
    private TCSubtitleInfo mSubtitleInfo;
    private OnBubbleSubtitleCallback mCallback;

    public BubbleSubtitlePannel(@NonNull Context context) {
        super(context);
        init();
    }

    public BubbleSubtitlePannel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleSubtitlePannel(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mSubtitleInfo = new TCSubtitleInfo();

        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.ugckit_layout_bubble_window, this, true);
        initViews(mContentView);
    }

    private void initViews(@NonNull View contentView) {
        mImageClose = (ImageView)contentView.findViewById(R.id.iv_close);
        mImageClose.setOnClickListener(this);
        mRecycleBubbles = (RecyclerView) contentView.findViewById(R.id.bubble_rv_style);
        mRecycleBubbles.setVisibility(View.VISIBLE);
    }

    private void enterAnimator() {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mContentView, "translationY", mContentView.getHeight(), 0);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(400);
        set.play(translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                BubbleSubtitlePannel.this.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    private void resetInfo() {
        mSubtitleInfo.setBubblePos(0);
        // 创建一个默认的
        TCBubbleInfo info = new TCBubbleInfo();
        info.setHeight(0);
        info.setWidth(0);
        info.setDefaultSize(40);
        info.setBubblePath(null);
        info.setIconPath(null);
        info.setRect(0, 0, 0, 0);
        mSubtitleInfo.setBubbleInfo(info);
    }

    @Override
    public void loadAllBubble(List<TCBubbleInfo> list) {
        mBubbles = list;
        mRecycleBubbles.setVisibility(View.VISIBLE);
        mBubbleAdapter = new BubbleAdapter(list);
        mBubbleAdapter.setOnItemClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(mContentView.getContext(),LinearLayoutManager.HORIZONTAL,false);
        mRecycleBubbles.setLayoutManager(manager);
        mRecycleBubbles.setAdapter(mBubbleAdapter);
    }

    @Override
    public void show(@Nullable TCSubtitleInfo info) {
        if (info == null) {
            resetInfo();
        } else {
            mSubtitleInfo = info;
        }
        mContentView.post(new Runnable() {
            @Override
            public void run() {
                enterAnimator();
            }
        });
    }

    @Override
    public void dismiss() {
        exitAnimator();
    }

    private void exitAnimator() {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mContentView, "translationY", 0,
                mContentView.getHeight());
        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        set.play(translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                BubbleSubtitlePannel.super.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();
    }

    @Override
    public void onItemClick(View view, int position) {
        mSubtitleInfo.setBubblePos(position);
        mSubtitleInfo.setBubbleInfo(mBubbles.get(position));
        callback();
    }

    @Override
    public void onClick(@NonNull View v) {
        int i = v.getId();
         if (i == R.id.iv_close) {
            dismiss();
        }
    }

    @Override
    public void onFinishColor(@ColorInt int color) {
        mSubtitleInfo.setTextColor(color);
        callback();
    }

    @Override
    public void onProgressColor(@ColorInt int color) {
    }

    private void callback() {
        if (mCallback != null) {
            TCSubtitleInfo info = new TCSubtitleInfo();
            info.setBubblePos(mSubtitleInfo.getBubblePos());
            info.setBubbleInfo(mSubtitleInfo.getBubbleInfo());
            info.setTextColor(mSubtitleInfo.getTextColor());
            mCallback.onBubbleSubtitleCallback(info);
        }
    }

    @Override
    public void setOnBubbleSubtitleCallback(OnBubbleSubtitleCallback callback) {
        mCallback = callback;
    }



}
