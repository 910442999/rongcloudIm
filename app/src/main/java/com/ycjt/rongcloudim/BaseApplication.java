package com.ycjt.rongcloudim;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.ycjt.rongcloudim.common.ErrorCode;
import com.ycjt.rongcloudim.im.IMManager;

import io.rong.imkit.RongIM;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.message.FileMessage;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         *
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            ErrorCode.init(this);

//            /**
//             * IMKit SDK调用第一步 初始化
//             */
//            RongIM.init(this, "82hegw5u8xzrx", true);
            /*
             * 以下部分仅在主进程中进行执行
             */
            // 初始化融云IM SDK，初始化 SDK 仅需要在主进程中初始化一次
            IMManager.getInstance().init(this);

            if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

                DemoContext.init(this);
            }
        }

//        /**
//         * 用于自定义消息的注册, 注册后方能正确识别自定义消息, 建议在init后及时注册，保证自定义消息到达时能正确解析。
//         */
//        try {
//            RongIMClient.registerMessageType(FileMessage.class);
//            RongIMClient.registerMessageType(CustomizeMessage.class);
//        } catch (AnnotationNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        AppContext.getInstance().init(getApplicationContext());
//        AppContext.getInstance().registerReceiveMessageListener();

    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }
}




