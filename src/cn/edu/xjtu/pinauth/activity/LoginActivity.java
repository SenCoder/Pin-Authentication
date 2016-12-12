package cn.edu.xjtu.pinauth.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.edu.xjtu.pinauth.R;
import cn.edu.xjtu.pinauth.model.User;
import cn.edu.xjtu.pinauth.util.FileHelper;

/**
 * Created by Yunpeng on 15/10/17.
 */
public class LoginActivity extends Activity {
	
    private static final String TAG = "LoginActivity";
    public static final String EXTRA_USERNAME = "cn.edu.xjtu.pinauth.username";
    private EditText mEditText;
    private Button mLoginButton;

    /**
     * 
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditText = (EditText)findViewById(R.id.name_editText);
        mLoginButton = (Button)findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEditText.getText().toString();
                Log.d(TAG,username);
                if (username.length()<2) {
                    Toast.makeText(getApplicationContext(), "用户名太短", Toast.LENGTH_SHORT).show();
                } else {
                    User.shareInstance().login(username);
                    Toast.makeText(getApplicationContext(), username + " 登录成功", Toast.LENGTH_SHORT).show();
                    FileHelper.checkUserDir(username);
                    finish();
                }
            }
        });
    }

    /**
     * 检测退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("退出");
            builder.setNegativeButton("取消", null);
            builder.setMessage("确认退出?");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意
                    startActivity(intent);
                    finish();

                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        return true;
    }

}
