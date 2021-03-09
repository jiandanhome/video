package com.tencent.qcloud.ugckit.module.record;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tencent.qcloud.ugckit.custom.OnSelectVideoListener;
import com.tencent.qcloud.ugckit.utils.UIAttributeUtil;
import com.tencent.qcloud.ugckit.R;
import com.tencent.ugc.TXUGCPartsManager;
import com.tencent.ugc.TXUGCRecord;

import java.util.Locale;

public class RecordBottomLayout extends RelativeLayout implements View.OnClickListener {
    private Activity            mActivity;
    private TextView            mImageCameraSwitch;         // 切换摄像头
    private TextView            mTextProgressTime;
    private TextView            mImageDeleteLastPart;       // 删除上一段
    private TextView            tvFromGallery;              // 从相册选择视频
    private TextView            mImageTorch;                // 闪光灯
    private RecordProgressView  mRecordProgressView;        // 录制进度条
    private RecordSpeedLayout   mRecordSpeedLayout;         // 速度面板
    private RecordButton        mButtonRecord;              // 录制按钮
    private RecordModeView      mRecordModeView;            // 录制模式[单击/长按]
    private View                mRecordModeDot;
    private TextView            mTvHint;

    private boolean mFrontCameraFlag = false;                //是否前置摄像头UI判断
    private boolean mIsTorchOpenFlag;                       // 是否打开闪光灯UI判断
    private boolean isSelectDeleteLastPartFlag;             // 是否点击一次过"删除最有一段分段视频"按钮
    private boolean mDisableTakePhoto;
    private boolean mDisableLongPressRecord;

    private OnDeleteLastPartListener mOnDeleteLastPartListener;

    private OnSelectVideoListener onSelectVideoListener;

    public RecordBottomLayout(Context context) {
        super(context);
        initViews();
    }

    public RecordBottomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public RecordBottomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mActivity = (Activity) getContext();
        inflate(mActivity, R.layout.ugckit_record_bottom_layout, this);
        mTextProgressTime = (TextView) findViewById(R.id.record_progress_time);
        showRecordedTime(0);

        mImageDeleteLastPart = (TextView) findViewById(R.id.iv_delete_last_part);
        mImageDeleteLastPart.setOnClickListener(this);

        tvFromGallery = (TextView) findViewById(R.id.tvFromGallery);
        tvFromGallery.setOnClickListener(this);

        mImageTorch = (TextView) findViewById(R.id.iv_torch);
        mImageTorch.setOnClickListener(this);

        mImageCameraSwitch = (TextView) findViewById(R.id.iv_switch_camera);
        mImageCameraSwitch.setOnClickListener(this);

        mRecordProgressView = (RecordProgressView) findViewById(R.id.record_progress_view);

        mRecordSpeedLayout = (RecordSpeedLayout) findViewById(R.id.record_speed_layout);
        mRecordSpeedLayout.setOnRecordSpeedListener(new RecordSpeedLayout.OnRecordSpeedListener() {
            @Override
            public void onSpeedSelect(int speed) {
                UGCKitRecordConfig.getInstance().mRecordSpeed = speed;
                VideoRecordSDK.getInstance().setRecordSpeed(speed);
            }
        });

        mButtonRecord = (RecordButton) findViewById(R.id.record_button);
        mRecordModeView = (RecordModeView) findViewById(R.id.record_mode_view);
        mRecordModeDot = findViewById(R.id.record_mode_dot);

        // 根据不同的拍摄模式，更新拍摄按钮
        mRecordModeView.setOnRecordModeListener(new RecordModeView.OnRecordModeListener() {
            @Override
            public void onRecordModeSelect(int currentMode) {
                mButtonRecord.setCurrentRecordMode(currentMode);
            }
        });


        if (mFrontCameraFlag) {
            mImageTorch.setVisibility(View.GONE);
        } else {
            mImageTorch.setVisibility(View.VISIBLE);
        }

        mTvHint=findViewById(R.id.tvHint);


    }


    public RecordBottomLayout setOnSelectVideoListener(OnSelectVideoListener onSelectVideoListener) {
        this.onSelectVideoListener = onSelectVideoListener;
        return this;
    }

    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();
        if (id == R.id.iv_delete_last_part) {
            deleteLastPart();
        } else if (id == R.id.iv_torch) {
            toggleTorch();
        } else if (id == R.id.iv_switch_camera) {
            switchCamera();
        } else if(id==R.id.tvFromGallery){
            if(onSelectVideoListener!=null){
                onSelectVideoListener.toSelect();
            }
        }
    }


    /**
     * 切换前后摄像头
     */
    private void switchCamera() {
        mFrontCameraFlag = !mFrontCameraFlag;
        mIsTorchOpenFlag = false;
        if (mFrontCameraFlag) {
            mImageTorch.setVisibility(View.GONE);
        } else {
            mImageTorch.setVisibility(View.VISIBLE);
        }
        VideoRecordSDK.getInstance().switchCamera(mFrontCameraFlag);
    }

    /**
     * 切换闪光灯开/关
     */
    private void toggleTorch() {
        mIsTorchOpenFlag = !mIsTorchOpenFlag;
        if (mIsTorchOpenFlag) {

            TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
            if (record != null) {
                record.toggleTorch(true);
            }
        } else {
            TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
            if (record != null) {
                record.toggleTorch(false);
            }
        }
    }

    /**
     * 设置闪光灯的状态为关闭
     */
    public void closeTorch() {
        if (mIsTorchOpenFlag) {
            mIsTorchOpenFlag = false;
            if (mFrontCameraFlag) {
                mImageTorch.setVisibility(View.GONE);
            } else {
                mImageTorch.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 删除上一段
     */
    private void deleteLastPart() {
        int size = VideoRecordSDK.getInstance().getPartManager().getPartsPathList().size();
        if (size == 0) {
            // 没有任何分段视频，返回
            return;
        }
        if (!isSelectDeleteLastPartFlag) {
            isSelectDeleteLastPartFlag = true;
            // 选中最后一段视频，更新进度条颜色
            mRecordProgressView.selectLast();
        } else {
            isSelectDeleteLastPartFlag = false;
            // 删除最后一段视频，更新进度条颜色
            mRecordProgressView.deleteLast();

            VideoRecordSDK.getInstance().deleteLastPart();

            long duration = VideoRecordSDK.getInstance().getPartManager().getDuration();
            float timeSecond = duration / 1000;
            showRecordedTime(duration);

            mOnDeleteLastPartListener.onUpdateTitle(timeSecond >= UGCKitRecordConfig.getInstance().mMinDuration / 1000);

            // 删除分段后再次判断size
            size = VideoRecordSDK.getInstance().getPartManager().getPartsPathList().size();
            if (size == 0) { // 重新开始录
                mOnDeleteLastPartListener.onReRecord();
            }
            setRightActionViewVisibility();
        }
    }

    public void setOnRecordButtonListener(RecordButton.OnRecordButtonListener listener) {
        mButtonRecord.setOnRecordButtonListener(listener);
    }

    public void setOnDeleteLastPartListener(OnDeleteLastPartListener lister) {
        mOnDeleteLastPartListener = lister;
    }

    public void disableRecordSpeed() {
        mRecordSpeedLayout.setVisibility(View.GONE);
    }

    public void disableTakePhoto() {
        mRecordModeView.disableTakePhoto();
        mDisableTakePhoto = true;
        showRecordMode();
    }

    public void disableLongPressRecord() {
        mRecordModeView.disableLongPressRecord();
        mDisableLongPressRecord = true;
        showRecordMode();
    }

    //如果禁用拍照和长按拍摄，则仅剩下单击拍摄
    private void showRecordMode() {
        mRecordModeDot.setVisibility(View.INVISIBLE);
        mRecordModeView.selectOneRecordMode();
    }

    public interface OnDeleteLastPartListener {
        void onUpdateTitle(boolean enable);

        void onReRecord();
    }

    public void startRecord() {
        mImageDeleteLastPart.setVisibility(View.INVISIBLE);
        tvFromGallery.setVisibility(View.INVISIBLE);

        mImageCameraSwitch.setVisibility(View.INVISIBLE);
        mImageTorch.setVisibility(View.INVISIBLE);
        mRecordModeView.setVisibility(View.INVISIBLE);
        mRecordSpeedLayout.setVisibility(View.INVISIBLE);
        mTvHint.setVisibility(View.INVISIBLE);
    }

    public void pauseRecord() {
//        mImageDeleteLastPart.setVisibility(View.VISIBLE);
//        tvFromGallery.setVisibility(View.GONE);

        tvFromGallery.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRightActionViewVisibility();
            }
        },50);


        mImageCameraSwitch.setVisibility(View.VISIBLE);
        mImageTorch.setVisibility(View.VISIBLE);
        mRecordModeView.setVisibility(View.VISIBLE);
        mRecordSpeedLayout.setVisibility(View.VISIBLE);
        mTvHint.setVisibility(View.VISIBLE);
    }

    private void setRightActionViewVisibility(){
        try {
            boolean hasVideoPart=VideoRecordSDK.getInstance().getPartManager().getPartsPathList().size()>0;
            mImageDeleteLastPart.setVisibility(hasVideoPart?View.VISIBLE:View.GONE);
            tvFromGallery.setVisibility(hasVideoPart?View.GONE:View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化最大/最小视频录制时长
     */
    public void initDuration() {
        mRecordProgressView.setMaxDuration(UGCKitRecordConfig.getInstance().mMaxDuration);
        mRecordProgressView.setMinDuration(UGCKitRecordConfig.getInstance().mMinDuration);
    }

    /**
     * 更新录制进度Progress
     *
     * @param milliSecond
     */
    public void updateProgress(long milliSecond) {
        mRecordProgressView.setProgress((int) milliSecond);
        showRecordedTime(milliSecond);
    }

    private void showRecordedTime(long milliSecond ){
        long second = milliSecond / 1000;
        long minute = second / 60;
        second = second - minute*60;
        String minuteStr=minute+"";
        String secondStr=second+"";
        String s0 = minuteStr.length() == 1 ? "0" + minuteStr : minuteStr;
        String s1 = secondStr.length() == 1 ? "0" + secondStr : secondStr;
        mTextProgressTime.setText(String.format("%s:%s", s0,s1));
    }

    public RecordButton getRecordButton() {
        return mButtonRecord;
    }

    public RecordModeView getRecordModeView() {
        return mRecordModeView;
    }

    public RecordProgressView getRecordProgressView() {
        return mRecordProgressView;
    }

    public RecordSpeedLayout getRecordSpeedLayout() {
        return mRecordSpeedLayout;
    }
}
