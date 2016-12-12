package cn.edu.xjtu.pinauth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.edu.xjtu.pinauth.R;
import cn.edu.xjtu.pinauth.model.User;
import cn.edu.xjtu.pinauth.util.SensorMonitor;

public class MainActivity extends Activity {
	
    private static final String TAG = "MainActivity";
    private Button mTraningButton;
    private Button mAuthButton;
    private Button mSettingButton;
    private Button mLearningButton;
    private Button mResultButton;
//    private Button mReversedButton;
    
    private TextView mUsernameTextView;
    private TextView mTimesTextView;
    private User user = User.shareInstance();

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "启动应用");
        new SensorMonitor(getApplicationContext());
        // 初始化监听器

        mTraningButton = (Button) findViewById(R.id.training_button);
        mLearningButton = (Button)findViewById(R.id.plot_button);
        mResultButton = (Button) findViewById(R.id.result_button);
        
        mSettingButton = (Button) findViewById(R.id.setting_button);
        mAuthButton = (Button) findViewById(R.id.auth_button);
//        mReversedButton = (Button) findViewById(R.id.reverse_button);
        
        mUsernameTextView = (TextView)findViewById(R.id.username_textview);
        mTimesTextView = (TextView)findViewById(R.id.training_time_textView);

        mTraningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TrainingActivity.class);
                startActivity(i);
            }
        });

        mAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(i);
            }
        });
        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
            }
        });

        mLearningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PlotActivity.class);
                startActivity(i);
            	
            }
        });
        
        mResultButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, ResultActivity.class);
				startActivity(i);
			}
		});

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (User.shareInstance().getUsername().isEmpty()) {
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
        } else {
            mUsernameTextView.setText(user.getUsername());
            mTimesTextView.setText(user.trainingTimes()+"次");
        }
        if (user.getPassword().isEmpty()) {
            mTraningButton.setEnabled(false);
            mLearningButton.setEnabled(false);
            mAuthButton.setEnabled(false);
        } 
        else 
        {
            mTraningButton.setEnabled(true);
            if (user.trainingTimes() < 20) {
            	mLearningButton.setEnabled(false);
                mAuthButton.setEnabled(false);
            }
            else
            {
            	mLearningButton.setEnabled(true);
            	if (user.isThresholdLearning()) {
                	mAuthButton.setEnabled(true);
    			}
                else {
                	mAuthButton.setEnabled(false);
                }
            }
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			finish();
	        return true;
		}
	    return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
            finish();
        }
    }
    
}
