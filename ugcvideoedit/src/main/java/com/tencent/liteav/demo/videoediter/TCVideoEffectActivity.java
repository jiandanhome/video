package com.tencent.liteav.demo.videoediter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.UGCKitVideoEffect;
import com.tencent.qcloud.ugckit.module.effect.IVideoEffectKit;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;


public class TCVideoEffectActivity extends FragmentActivity {
    private static final String TAG = "TCVideoEffectActivity";

    private int mFragmentType;
    private UGCKitVideoEffect mUGCKitVideoEffect;

    private IVideoEffectKit.OnVideoEffectListener mOnVideoEffectListener = new IVideoEffectKit.OnVideoEffectListener() {
        @Override
        public void onEffectApply() {
            finish();
        }

        @Override
        public void onEffectCancel() {
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoEditerSDK.getInstance().setVideoPath(getIntent().getStringExtra(UGCKitConstants.VIDEO_PATH));
        setContentView(R.layout.ugcedit_activity_video_effect);

        mFragmentType = getIntent().getIntExtra(UGCKitConstants.KEY_FRAGMENT, 0);

        mUGCKitVideoEffect = (UGCKitVideoEffect) findViewById(R.id.video_effect_layout);
        mUGCKitVideoEffect.setEffectType(mFragmentType);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mUGCKitVideoEffect.setOnVideoEffectListener(mOnVideoEffectListener);
        mUGCKitVideoEffect.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUGCKitVideoEffect.stop();
        mUGCKitVideoEffect.setOnVideoEffectListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUGCKitVideoEffect.release();
    }


    @Override
    public void onBackPressed() {
        mUGCKitVideoEffect.backPressed();
    }

}
