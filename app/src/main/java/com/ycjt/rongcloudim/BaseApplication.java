package com.ycjt.rongcloudim;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

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

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this, "82hegw5u8xzrx", true);

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

//下面三个为好友列表四个账号中的三个账号信息，分别为:用户userid，token，登录邮箱，密码，昵称


/**
 * 56146
 * rjlNearK6Qzu3MxMbMrQPLI6ZiT8q7s0UEaMPWY0lMwZFCr6nvpDtW4C3cjQ9tv4cIQMvwbkJb6xzmBRkFjv+Q==
 * 112@rongcloud.com
 * 123456
 * rongcloud112
 * <p>
 * 56147
 * ke0BCyqQnwWiagWoS1ckzrI6ZiT8q7s0UEaMPWY0lMwZFCr6nvpDtX2/KvQEWEDo6r3YdoOyCDqUgN53uY4SEA==
 * 113@rongcloud.com
 * 123456
 * rongcloud113
 * <p>
 * 56148
 * mYLERi6S6fkqdGjEIJrEubI6ZiT8q7s0UEaMPWY0lMwZFCr6nvpDtQIt9svl5Wir1X0Zf3Hy5T1QRLmZJrAbgQ==
 * 114@rongcloud.com
 * 123456
 * rongcloud114
 */

/**
 * 56147
 * ke0BCyqQnwWiagWoS1ckzrI6ZiT8q7s0UEaMPWY0lMwZFCr6nvpDtX2/KvQEWEDo6r3YdoOyCDqUgN53uY4SEA==
 * 113@rongcloud.com
 * 123456
 * rongcloud113
 */

/**
 * 56148
 * mYLERi6S6fkqdGjEIJrEubI6ZiT8q7s0UEaMPWY0lMwZFCr6nvpDtQIt9svl5Wir1X0Zf3Hy5T1QRLmZJrAbgQ==
 * 114@rongcloud.com
 * 123456
 * rongcloud114
 */
