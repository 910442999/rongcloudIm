package com.ycjt.rongcloudim.fakeserver;

import com.ycjt.rongcloudim.utils.HttpUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * 模拟服务器
 */

public class FakeServer {
    /**
     * 由"开发者平台"提供的 App Key 和 App Secret，
     */
    private static final String APP_KEY = "82hegw5u8xzrx";
    private static final String APP_SECRET = "q8lWfk95Ib2fdu";

    /**
     * 获取融云Token, 通过调用融云ServerApi获得 也可集成sdk获取
     */
    public static void getToken(String userId, String userName, String userPortrait, HttpUtil.OnResponse callback) {
        try {
            String register_data = "userId=" + URLEncoder.encode(userId, "UTF-8")
                    + "&name=" + URLEncoder.encode(userName, "UTF-8")
                    + "&portraitUri=" + URLEncoder.encode(userPortrait, "UTF-8");
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setOnResponse(callback);
            httpUtil.post(APP_KEY, APP_SECRET, register_data, callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
