package cn.edu.xjtu.pinauth.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.edu.xjtu.pinauth.R;
import cn.edu.xjtu.pinauth.activity.TrainingActivity;
import cn.edu.xjtu.pinauth.model.AuthEvent;
import cn.edu.xjtu.pinauth.model.TouchEvent;
import cn.edu.xjtu.pinauth.model.User;

/**
 * 这个类主要用于处理键盘的输入,以及传感器的记录
 * Created by Yunpeng on 15/10/22.
 */
@SuppressWarnings("rawtypes")
public class KeyboardHandler extends RecyclerView.Adapter {
    private static final String TAG = "KeyboardHandler";

    Context mContext;
    TextView mPasswordTextView;
    TextView mCounterTextView; 
    LayoutInflater mLayoutInflater;
    int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
    String[] s = {"", "abc", "def", "ghi", "jkl", "mno", "oprs", "tux", "wxyz"};


    public KeyboardHandler(Context context, TextView mPasswordTextView, TextView mCounterTextView, AuthPattern authPattern) {
        this.mContext = context;
        this.mPasswordTextView = mPasswordTextView;
        this.mCounterTextView = mCounterTextView;
        this.authPattern = authPattern;
        mCounterTextView.setText("当前密码训练次数:" + user.trainingTimes());
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.key_item, parent, false);
        return new UnlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position == 9) {
            UnlockViewHolder viewHolder = (UnlockViewHolder) holder;
            viewHolder.num.setText("紧急呼叫");
            viewHolder.num.setTextSize(14);
            viewHolder.num.setPadding(0,45,0,0);
            viewHolder.v.setText("");
        } else if (position == 11) {
            UnlockViewHolder viewHolder = (UnlockViewHolder) holder;
            viewHolder.num.setText("删除");
            viewHolder.num.setTextSize(14);
            viewHolder.num.setPadding(0,45,0,0);
            viewHolder.v.setText("");
        } else if (position == 10) {
            UnlockViewHolder viewHolder = (UnlockViewHolder) holder;
            viewHolder.num.setText("0");
            viewHolder.v.setText(" ");
        } else {
            UnlockViewHolder viewHolder = (UnlockViewHolder) holder;
            viewHolder.num.setText(nums[position] + "");
            viewHolder.v.setText(s[position]);
        }
    }

    @Override
    public int getItemCount() {
        return nums.length + 2;
    }


    /**
     * 定义输入类型（训练/认证）
     */
    public enum AuthPattern {
        kAuthPatternTraning,
        kAuthPatternPredict,
    }

    private User user = User.shareInstance();

    /**
     * 输入类型（训练/认证）
     */
    private AuthPattern authPattern;

    /**
     * 当前处理的按键
     */
    private String mKey;

    /**
     * 当前事件开始时间
     */
    private long mStartTime;

    /**
     * 当前事件结束时间
     */
    private long mEndTime;

    /**
     * 当前事件接触面积序列
     */
    private ArrayList<Double> mSizeList = new ArrayList<>();

    /**
     * 当前事件接触压力序列
     */
    private ArrayList<Double> mPressureList = new ArrayList<>();
    private SensorMonitor mSensorMonitor = SensorMonitor.shareInstance();

    /**
     * 记录当前已输入的密码
     */
    private StringBuilder mPassword = new StringBuilder();

    /**
     * 内部类,用来处理
     */
    class UnlockViewHolder extends RecyclerView.ViewHolder {
        TextView num;
        TextView v;
        public UnlockViewHolder(final View itemView) {
            super(itemView);
            num = (TextView) itemView.findViewById(R.id.num);
            v = (TextView) itemView.findViewById(R.id.v);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mSizeList.add((double) event.getSize());
                    mPressureList.add((double) event.getPressure());
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_CANCEL:
                            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.black));
                            itemView.setAlpha(1.0f);
                            break;
                        case MotionEvent.ACTION_DOWN:
                            Log.d(TAG, "按下了" + num.getText());
                            mKey = num.getText().toString();
                            mSensorMonitor.startRecord();
                            mStartTime = System.currentTimeMillis();
                            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                            itemView.setAlpha(0.4f);
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "放开了" + num.getText());
                            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.black));
                            itemView.setAlpha(1.0f);
                            mEndTime = System.currentTimeMillis();
                            mSensorMonitor.stopRecord();
                            String input = num.getText().toString();
                            if (input.equals("紧急呼叫")) {
                                Log.d(TAG, "无效操作,请继续输入密码");
                                resetData();
                                mSensorMonitor.removeLastData();
                                Toast.makeText(mContext, "无效操作,请继续输入密码", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            if (input.equals("删除")) {
                                resetData();
                                mSensorMonitor.removeLastData();
                                if (mPassword.length() > 0) {
                                    mPassword.deleteCharAt(mPassword.length() - 1);
                                }
                                Log.d(TAG, "当前密码" + mPassword);
                                mPasswordTextView.setText(mPassword);
                                return true;
                            }

                            saveTouchEvent();
                            resetData();
                            mPassword.append(num.getText().toString());
                            mPasswordTextView.setText(mPassword);
                            Log.d(TAG, "当前密码:" + mPassword);
                            Log.d(TAG, "原密码:" + user.getPassword());
                            if (mPassword.toString().equals(user.getPassword())) {
                                Log.d(TAG, "密码匹配成功");
                                Log.d(TAG, "预测数据");
                                AuthEvent authEvent = new AuthEvent(mTouchEventList);

                                switch (authPattern) {
                                    case kAuthPatternTraning:
                                        if (authEvent.train()) {
                                            FileHelper.saveRawData(authEvent, FileHelper.TRAINING_DATA_Dir);
                                            showDialog("密码录入成功");
                                            if (user.trainingTimes() >= user.getModelSize()) {
                                                ((TrainingActivity)mContext).showDialog();
                                            }
                                        } else {
                                            showDialog("数据异常,本次录入无效");
                                        }
                                        mCounterTextView.setText("当前密码训练次数:" + user.trainingTimes());
                                        break;
                                    case kAuthPatternPredict:
                                        showDialog(authEvent.predict());
                                        break;
                                }
                                mTouchEventList.clear();
                                mPassword = new StringBuilder();
                                mPasswordTextView.setText(mPassword);

                            } else if (mPassword.length() >= user.getPassword().length()) {
                                Log.d(TAG, "密码错误");
                                mTouchEventList.clear();
                                mPassword = new StringBuilder();
                                mPasswordTextView.setText(mPassword);
                                showDialog("密码录入失败: 密码错误");
//                                Toast.makeText(mContext, "密码录入失败: 密码错误", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "继续输入");
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
    }
    private ArrayList<TouchEvent> mTouchEventList = new ArrayList<>();
    private void saveTouchEvent() {
        TouchEvent touchEvent = new TouchEvent(
                mKey,
                mStartTime,
                mEndTime,
                mSizeList,
                mPressureList,
                mSensorMonitor.getAccelerometerDataList(),
                mSensorMonitor.getGyroscopeDataList());
        mTouchEventList.add(touchEvent);
    }

    private void resetData() {
        mStartTime = 0;
        mEndTime = 0;
        mSizeList.clear();
        mPressureList.clear();
    }


    private void showDialog(final String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(msg);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


//    class OtherViewHolder extends RecyclerView.ViewHolder {
//        public OtherViewHolder(View itemView) {
//            super(itemView);
//
//        }
//    }
}
