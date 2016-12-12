package cn.edu.xjtu.pinauth.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.xjtu.pinauth.R;
import cn.edu.xjtu.pinauth.model.User;

public class SettingActivity extends Activity {
	
    private static final String TAG = "LoginActivity";

    public static final String EXTRA_USERNAME = "cn.edu.xjtu.pinauth.username";
    private TextView mUsernameTextView;
    private TextView mPasswordTextView;
    private Button mResetButton;
    private SeekBar mSizeSeekBar;
    private Switch mAutoLearningSwitch;

    private EditText mThresholdEditText;
    private TextView mSizeTextView;
    private Button mLogoutButton;

    private SeekBar mFeatureSizeSeekBar;
    private TextView mFeatureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mUsernameTextView = (TextView) findViewById(R.id.username_textview);
        mPasswordTextView = (TextView) findViewById(R.id.password_textview);
        mResetButton = (Button) findViewById(R.id.reset_button);
        mSizeSeekBar = (SeekBar) findViewById(R.id.size_seekbar);
        mSizeTextView = (TextView) findViewById(R.id.size_textview);
        mLogoutButton = (Button) findViewById(R.id.logout_button);
        mAutoLearningSwitch = (Switch) findViewById(R.id.isAutoLearning_switch);
        mThresholdEditText = (EditText)findViewById(R.id.threshold_textview);
        mFeatureSizeSeekBar = (SeekBar) findViewById(R.id.feature_size_seekbar);
        mFeatureTextView = (TextView)findViewById(R.id.feature_size_textview);

        User user = User.shareInstance();
        mUsernameTextView.setText(user.getUsername());
        mSizeTextView.setText("" + user.getModelSize());
        mSizeSeekBar.setProgress(user.getModelSize());
        mFeatureSizeSeekBar.setProgress(user.getFeatureSize());
        mFeatureTextView.setText("" + user.getFeatureSize());
        mAutoLearningSwitch.setChecked(user.isAutoLearning());
        mThresholdEditText.setText(String.format("%.2f", user.getThreshold()));
        if (user.isAutoLearning()) {
            mAutoLearningSwitch.setText("已开启");
        } else {
            mAutoLearningSwitch.setText("已关闭");
        }



        if (user.getPassword().isEmpty()) {
            mPasswordTextView.setText("未设置密码");
        } else {
            mPasswordTextView.setText(user.getPassword());
        }
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText inputEditText = new EditText(SettingActivity.this);
                inputEditText.setFocusable(true);

                inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("请输入密码").setView(inputEditText)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = inputEditText.getText().toString();
                        if (password.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "密码不可为空,请重新设置", Toast.LENGTH_SHORT).show();
                        } else {
                            User.shareInstance().updatePassword(password);
                            mPasswordTextView.setText(password);
                            Toast.makeText(getApplicationContext(), "密码更新完成:" + password, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setCancelable(false);
                builder.show();
                inputEditText.requestFocus();

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                                   public void run() {
                                       InputMethodManager inputManager =
                                               (InputMethodManager) inputEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                       inputManager.showSoftInput(inputEditText, 0);
                                   }
                               },
                        100);
            }
        });


        mSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSizeTextView.setText("" + progress);
                User.shareInstance().setModelSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mFeatureSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFeatureTextView.setText("" + progress);
                User.shareInstance().setFeatureSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mAutoLearningSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                User.shareInstance().setIsAutoLearning(isChecked);
                if (isChecked) {
                    mAutoLearningSwitch.setText("已开启");
                } else {
                    mAutoLearningSwitch.setText("已关闭");
                }
            }
        });

        mThresholdEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                try {
                    double t = Double.valueOf(mThresholdEditText.getText().toString());
                    User.shareInstance().setThreshold(t);
                } catch (Exception e) {
                    Log.d(TAG,"输入异常,此次设定阈值无效");
                }
                return false;
            }
        });


        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.shareInstance().logout();
                Intent i = new Intent(SettingActivity.this,LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
            finish();
        }
    }
}