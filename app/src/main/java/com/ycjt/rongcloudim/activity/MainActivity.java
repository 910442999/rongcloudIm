package com.ycjt.rongcloudim.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ycjt.rongcloudim.DemoContext;
import com.ycjt.rongcloudim.R;

import io.rong.callkit.RongCallKit;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mBut2, mBut3, mBut4, mBut5, mBut6;
    private ImageView mBackImg;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBut2 = (Button) findViewById(R.id.bt2);
        mBut3 = (Button) findViewById(R.id.bt3);
        mBut4 = (Button) findViewById(R.id.bt4);
        mBut5 = (Button) findViewById(R.id.bt5);
        mBut6 = (Button) findViewById(R.id.bt6);
        Button bt7 = (Button) findViewById(R.id.bt7);
        mBackImg = (ImageView) findViewById(R.id.img1);
        mTitle = (TextView) findViewById(R.id.txt1);

        mBackImg.setVisibility(View.GONE);
        mTitle.setText("主页面");
        mBut6.setVisibility(View.GONE);

        mBut2.setOnClickListener(this);
        mBut3.setOnClickListener(this);
        mBut4.setOnClickListener(this);
        mBut5.setOnClickListener(this);
        mBut6.setOnClickListener(this);
        bt7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String targetId = DemoContext.getInstance().getSharedPreferences().getString("DEMO_USERID", "default");
        switch (v.getId()) {
            case R.id.bt2:
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startPrivateChat(MainActivity.this, targetId, "title");
                //                    RongIM.getInstance().startConversation(MainActivity.this ,Conversation.ConversationType.PRIVATE , userid, "title");
                break;
            case R.id.bt3:
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startConversationList(MainActivity.this);
                break;
            case R.id.bt4:
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startSubConversationList(MainActivity.this, Conversation.ConversationType.GROUP);
                break;
            case R.id.bt5:
                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                break;
            case R.id.bt6:
                RongCallKit.startSingleCall(MainActivity.this, targetId, RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
                break;
            case R.id.bt7:
                RongCallKit.startSingleCall(MainActivity.this, targetId, RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO);
                break;


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {


            final AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
            alterDialog.setMessage("确定退出应用？");
            alterDialog.setCancelable(true);

            alterDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (RongIM.getInstance() != null)
                        RongIM.getInstance().disconnect(true);

                    Process.killProcess(Process.myPid());
                }
            });
            alterDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alterDialog.show();
        }

        return false;
    }

}
