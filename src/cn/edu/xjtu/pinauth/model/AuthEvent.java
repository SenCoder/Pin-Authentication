package cn.edu.xjtu.pinauth.model;

import android.util.Log;

import java.util.ArrayList;

import cn.edu.xjtu.pinauth.util.FileHelper;

/**
 * Created by Yunpeng on 15/10/18.
 */
public class AuthEvent {
    private static String TAG = "AuthEvent";

    /**
     * 保存TouchEvent列表
     */
    private ArrayList<TouchEvent> touchEvents = new ArrayList<>();

    public AuthEvent(ArrayList<TouchEvent> touchEvents) {
        this.touchEvents = touchEvents;
    }

    public ArrayList<TouchEvent> getTouchEvents() {
        return touchEvents;
    }

    /**
     * 获取时间向量(基于整体的)
     * @return
     */
    public double[] getTimeVector() {
//        Log.d(TAG,"获取时间向量");
        int size = touchEvents.size();
//        Log.d(TAG,"向量大小"+size);
        double vector[] = new double[2*size - 1];
        TouchEvent firstTouchEvent = touchEvents.get(0);
        vector[0] = firstTouchEvent.getEndTime() - firstTouchEvent.getStartTime();
        TouchEvent lastTouchEvent = firstTouchEvent;
        for (int i=1;i<2*size-1;i+=2) {
            int index = (i+1)/2;
            TouchEvent touchEvent = touchEvents.get(index);
            vector[i] = touchEvent.getStartTime() - lastTouchEvent.getEndTime();
            vector[i+1] = touchEvent.getEndTime() - touchEvent.getStartTime();
            lastTouchEvent = touchEvent;
        }
        return vector;
    }


    /**
     * 训练当前AuthEvent
     * @return
     */
    public boolean train() {
        return User.shareInstance().trainByAuthEvent(this);
    }

    /**
     * 预测当前AuthEvent
     * @return
     */
    public String predict() {
        User user =User.shareInstance();
        String msg = "";
        double r = user.predictByAuthEvent(this);
        if (r > 0.5) {
            Log.d(TAG,"真");
            FileHelper.saveRawData(this, FileHelper.PREDICT_TRUE_DATA_Dir);
            msg = "本次认证为真\n"+r;
            if(user.isAutoLearning() &&  FileHelper.getFileCount(user.getUsername(),user.getPassword(),FileHelper.PREDICT_TRUE_DATA_Dir) >= 5){
                autoLearning();
            }
        } else {
            Log.d(TAG,"假");
            msg = "本次认证为假\n"+r;
            FileHelper.saveRawData(this, FileHelper.PREDICT_FALSE_DATA_Dir);
        }
        return msg;
    }

    private void autoLearning() {
        User user =User.shareInstance();
        FileHelper.move(user.getUsername(),user.getPassword(),FileHelper.PREDICT_TRUE_DATA_Dir,FileHelper.TRAINING_DATA_Dir);
        user.reloadModel();
        FileHelper.clearFolder(user.getUsername(),user.getPassword(),FileHelper.PREDICT_TRUE_DATA_Dir);
    }
}
