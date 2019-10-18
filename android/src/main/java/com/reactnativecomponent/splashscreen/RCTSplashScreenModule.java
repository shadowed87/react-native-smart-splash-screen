package com.reactnativecomponent.splashscreen;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;


public class RCTSplashScreenModule extends ReactContextBaseJavaModule {
    public static String ImgPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + "mg_open_image.jpg";
    public static int CODE = 11000;
    public static String Url = "";

    public RCTSplashScreenModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "SplashScreen";
    }

    public static void requestPermissionsCallback() {
        try {
            Bitmap mBitmap = BitmapFactory.decodeStream(getImageStream(Url));
            saveBitmapToSDCard(mBitmap);
        } catch (Exception e) {

        }

    }

    /**
     * 图片下载方法
     */
    @ReactMethod
    public void loadLaunchScreenImage(String url) {
        try {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            int permission_result = PermissionChecker.checkPermission(getCurrentActivity(), perms[0], android.os.Process.myPid(), android.os.Process.myUid(), getCurrentActivity().getPackageName());
            if (permission_result != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getCurrentActivity(), perms, RCTSplashScreenModule.CODE);
                return;
            }
            Url = url;
            Bitmap mBitmap = BitmapFactory.decodeStream(getImageStream(Url));
            saveBitmapToSDCard(mBitmap);
        } catch (Exception e) {

        }
    }

    /**
     * 保存到本地
     *
     * @param bitmap
     */
    public static void saveBitmapToSDCard(Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(ImgPath);//picPath为保存SD卡路径
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }


    @ReactMethod
    public void close(ReadableMap options) {

        int animationType = RCTSplashScreen.UIAnimationNone;
        int duration = 0;
        int delay = 0;

        if (options != null) {
            if (options.hasKey("animationType")) {
                animationType = options.getInt("animationType");
            }
            if (options.hasKey("duration")) {
                duration = options.getInt("duration");
            }
            if (options.hasKey("delay")) {
                delay = options.getInt("delay");
            }
        }

        if (animationType == RCTSplashScreen.UIAnimationNone) {
            delay = 0;
        }

        final int final_animationType = animationType;
        final int final_duration = duration;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                RCTSplashScreen.removeSplashScreen(getCurrentActivity(), final_animationType, final_duration);
            }
        }, delay);
    }


    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        return Collections.unmodifiableMap(new HashMap<String, Object>() {
            {
                put("animationType", getAnimationTypes());
            }

            private Map<String, Object> getAnimationTypes() {
                return Collections.unmodifiableMap(new HashMap<String, Object>() {
                    {
                        put("none", RCTSplashScreen.UIAnimationNone);
                        put("fade", RCTSplashScreen.UIAnimationFade);
                        put("scale", RCTSplashScreen.UIAnimationScale);
                    }
                });
            }
        });
    }

}
