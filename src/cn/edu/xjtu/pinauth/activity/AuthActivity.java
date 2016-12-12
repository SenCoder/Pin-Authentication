package cn.edu.xjtu.pinauth.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import cn.edu.xjtu.pinauth.model.User;
import cn.edu.xjtu.pinauth.util.KeyboardHandler;
import cn.edu.xjtu.pinauth.util.SensorMonitor;
import cn.edu.xjtu.pinauth.view.MyRecyclerView;
import cn.edu.xjtu.pinauth.R;

/**
 * Created by Yunpeng on 15/10/18.
 */
public class AuthActivity extends Activity {
	
    private static final String TAG = "AuthActivity";
    private User user = User.shareInstance();
    private TextView mPasswordView;
    private TextView mCounterView;
    private MyRecyclerView mRecyclerView;
    private TextView mFeatureView;

    /**
     * 启动
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        SensorMonitor.shareInstance().start();
        setTitle("当前用户:" + user.getUsername());
        user.reloadModel();
        mRecyclerView = (MyRecyclerView) findViewById(R.id.id_recyclerview);
        mPasswordView = (TextView) findViewById(R.id.current_password_text_view);
        mCounterView = (TextView)findViewById(R.id.auth_time_textView);
        mFeatureView = (TextView)findViewById(R.id.feature_textView);
        mFeatureView.setText(User.shareInstance().getFeatureIndexSet().toString());
        KeyboardHandler handler = new KeyboardHandler(this,
                mPasswordView,
                mCounterView,
                KeyboardHandler.AuthPattern.kAuthPatternPredict
        );
        mRecyclerView.setAdapter(handler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setBackgroundColor(getResources().getColor(R.color.black));
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "取消Sensor监听");
            SensorMonitor.shareInstance().stop();
            finish();
        }
        return true;

    }
}
