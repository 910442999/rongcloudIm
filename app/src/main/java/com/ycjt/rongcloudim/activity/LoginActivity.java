package com.ycjt.rongcloudim.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ycjt.rongcloudim.BaseApplication;
import com.ycjt.rongcloudim.DemoContext;
import com.ycjt.rongcloudim.R;
import com.ycjt.rongcloudim.fakeserver.FakeServer;
import com.ycjt.rongcloudim.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;


/**
 * Created by Bob on 15/8/19.
 * 登录页面
 * 1，token 从自己 server 获取的演示
 * 2，拿到 token 后，做 connect 操作
 */
public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    /**
     * token 的主要作用是身份授权和安全，因此不能通过客户端直接访问融云服务器获取 token，
     * 您必须通过 Server API 从融云服务器 获取 token 返回给您的 App，并在之后连接时使用
     */
    private String token; //通过融云Server API接口，获取的token
    private static String mSenderIdTest; //发送信息者ID
    private static String mSenderNameTest = "Oliver"; //发送信息者的昵称
    private static String mPortraitUriTest = "http://static.yingyonghui.com/screenshots/1657/1657011_5.jpg"; //获取发送信息者头像的url

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final EditText et1 = findViewById(R.id.et1);
        final EditText et2 = findViewById(R.id.et2);
        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSenderIdTest = et1.getText().toString();
                mSenderNameTest = et2.getText().toString();
                login();
            }
        });
        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                token = "QnLlEU4IKnoIv1f6riHSndOZWjk4M23xC4EfGJHOqdnRiIRg2+nFKG8cDkKFbz+7PGYU2j5R7+VkHmcmkIk1xw==";
                connect(token);
                SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                edit.putString("DEMO_TOKEN", token);
                edit.apply();
                SharedPreferences.Editor DEMO_USERID = DemoContext.getInstance().getSharedPreferences().edit();
                DEMO_USERID.putString("DEMO_USERID", "1234567890");
                DEMO_USERID.apply();


            }
        });
        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                token = "/4LPidjLzNRSARPkayG7fgBl+4auiKt760DXm22Jj4VjcLtZ2JJ10GidebXdw/xri8GJ2y15rjtLqgG48aBQCtQT6VGHbb/u";
                connect(token);
                SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                edit.putString("DEMO_TOKEN", token);
                edit.apply();

                SharedPreferences.Editor DEMO_USERID = DemoContext.getInstance().getSharedPreferences().edit();
                DEMO_USERID.putString("DEMO_USERID", "123456789");
                DEMO_USERID.apply();

            }
        });

    }

    /**
     * 用户登录，用户登录成功，获得 cookie，将cookie 保存
     */
    private void login() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                //                mSenderIdTest = "17600328876";

                return mSenderIdTest;
            }

            @Override
            protected void onPostExecute(String result) {

                getToken();
            }
        }.execute();
    }

    /**
     * 通过服务器端请求获取token，客户端不提供获取token的接口
     */
    private void getToken() {
        //1.直接用融云提供的后台sdk调用即可(导入jar包冲突暂时用工具类)
        //        RongCloud rongCloud = RongCloud.getInstance("82hegw5u8xzrx", "q8lWfk95Ib2fdu");
        //        User user = rongCloud.user;
        //        /**
        //         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#register
        //         *
        //         * 注册用户，生成用户在融云的唯一身份标识 Token
        //         */
        //        try {
        //            UserModel userModel = new UserModel()
        //                    .setId(mSenderIdTest)
        //                    .setName(mSenderNameTest)
        //                    .setPortrait(mPortraitUriTest);
        //            TokenResult result = user.register(userModel);
        //            token = result.getToken();
        //            connect(token);
        //            SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
        //            edit.putString("DEMO_TOKEN", token);
        //            edit.apply();
        //            Log.i(TAG, "获取的结果为:\n" + result.toString() + '\n');
        //            Log.i(TAG, "获取的 token 值为:\n" + token + '\n');
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //            Log.i(TAG, "获取 token 失败" + '\n');
        //        }

        //2.封装的工具类

        FakeServer.getToken(mSenderIdTest, mSenderNameTest, mPortraitUriTest, new HttpUtil.OnResponse() {
            @Override
            public void onResponse(int code, String body) {
                if (code == 200) {
                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        String userId = jsonObj.optString("userId");
                        token = jsonObj.optString("token");
                        connect(token);
                        SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                        edit.putString("DEMO_TOKEN", token);
                        edit.apply();
                        Log.i(TAG, "获取的 userId 值为:\n" + userId + '\n');
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Log.i(TAG, "获取的 token 值为:\n" + token + '\n');
                } else {
                    Log.i(TAG, "获取 token 失败" + '\n');
                }
            }
        });
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(BaseApplication.getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {

                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {

                    Log.d("LoginActivity", "--onSuccess" + userid);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 *                  http://www.rongcloud.cn/docs/android.html#常见错误码
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }

    //下面三个为好友列表四个账号中的三个账号信息，分别为:用户userid，token，登录邮箱，密码，昵称

    //    {"code":200,"userId":"123456789","token":"QnLlEU4IKnoIv1f6riHSndOZWjk4M23xC4EfGJHOqdnRiIRg2+nFKG8cDkKFbz+7PGYU2j5R7+VkHmcmkIk1xw=="}
    //    {"code":200,"userId":"1234567890","token":"/4LPidjLzNRSARPkayG7fgBl+4auiKt760DXm22Jj4VjcLtZ2JJ10GidebXdw/xri8GJ2y15rjtLqgG48aBQCtQT6VGHbb/u"}

}
