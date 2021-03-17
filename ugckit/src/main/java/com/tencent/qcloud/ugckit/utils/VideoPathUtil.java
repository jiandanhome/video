package com.tencent.qcloud.ugckit.utils;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.custom.EjuVideoConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 视频路径生成器
 */
public class VideoPathUtil {
    private static final String TAG = "VideoPathUtil";


    public static String generateVideoPath() {
        return generateVideoPath(null);
    }

    /**
     * 生成编辑后输出视频路径
     *
     * @return
     */
    public static String generateVideoPath(String videoName) {
        videoName=TextUtils.isEmpty(videoName)?generateVideoName()+"":videoName;
        if(!TextUtils.isEmpty(EjuVideoConfig.INSTANCE.getVideoOutputDirPath())){
            return new File(EjuVideoConfig.INSTANCE.getVideoOutputDirPath(),videoName+".mp4").getAbsolutePath();
        }
        File sdcardDir = UGCKit.getAppContext().getExternalFilesDir(null);
        if (sdcardDir == null) {
            Log.e(TAG, "generateVideoPath sdcardDir is null");
            return "";
        }
        String outputPath = sdcardDir + File.separator + UGCKitConstants.DEFAULT_MEDIA_PACK_FOLDER;
        File outputFolder = new File(outputPath);

        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        return outputFolder + File.separator + videoName+".mp4";
    }

    private static String generateVideoName(){
        String current = String.valueOf(System.currentTimeMillis() / 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String time = sdf.format(new Date(Long.valueOf(current + "000")));
        String saveFileName = String.format("video_%s", time);
        return saveFileName;
    }

    public static String getCustomVideoOutputPath() {
        return getCustomVideoOutputPath(null);
    }

    public static String getCustomVideoOutputPath(String fileNamePrefix) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String time = sdf.format(new Date(currentTime));

        File sdcardDir = UGCKit.getAppContext().getExternalFilesDir(null);
        if (sdcardDir == null) {
            Log.e(TAG, "sdcardDir is null");
            return null;
        }

        String outputDir = sdcardDir + File.separator + UGCKitConstants.OUTPUT_DIR_NAME;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }
        String tempOutputPath;
        if (TextUtils.isEmpty(fileNamePrefix)) {
            tempOutputPath = outputDir + File.separator + "TXUGC_" + time + ".mp4";
        } else {
            tempOutputPath = outputDir + File.separator + "TXUGC_" + fileNamePrefix + time + ".mp4";
        }
        return tempOutputPath;
    }
}
