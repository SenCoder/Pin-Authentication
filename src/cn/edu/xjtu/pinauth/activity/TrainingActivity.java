package cn.edu.xjtu.pinauth.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.xjtu.pinauth.R;
import cn.edu.xjtu.pinauth.model.User;
import cn.edu.xjtu.pinauth.util.SensorMonitor;
import cn.edu.xjtu.pinauth.util.KeyboardHandler;
import cn.edu.xjtu.pinauth.view.MyRecyclerView;

/**
 * Created by Yunpeng on 15/10/17.
 */
public class TrainingActivity extends Activity {
	
    private static final String TAG = "TraningActivity";
    private User user = User.shareInstance();
    private TextView mPasswordView;
    private TextView mCounterView;
    private MyRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        SensorMonitor.shareInstance().start();
        setTitle("��ǰ�û�:" + user.getUsername());
        mRecyclerView = (MyRecyclerView) findViewById(R.id.id_recyclerview);
        mPasswordView = (TextView) findViewById(R.id.current_password_text_view);
        mCounterView = (TextView)findViewById(R.id.training_time_textView);

        KeyboardHandler handler = new KeyboardHandler(this,
                mPasswordView,
                mCounterView,
                KeyboardHandler.AuthPattern.kAuthPatternTraning
        );

        mRecyclerView.setAdapter(handler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setBackgroundColor(getResources().getColor(R.color.black));

    }

    @Override
    protected void onStart() {
        super.onStart();
        user.setThresholdLearning(false);
        if (user.getPassword().isEmpty()) {
        	Toast.makeText(getApplicationContext(), "������������", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
        	Toast.makeText(getApplicationContext(), "���ݲɼ��������뾡�������ȶ�", Toast.LENGTH_SHORT).show();
		}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
        	Log.d(TAG,"ȡ��Sensor����");
            SensorMonitor.shareInstance().stop();
            finish();
        }
        return true;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("����¼�����,���ȷ�Ͻ���¼������");
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}
