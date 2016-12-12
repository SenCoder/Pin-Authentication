package cn.edu.xjtu.pinauth.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

/**
*
* 本类用来监听传感器的数据
* 单例
* Created by Yunpeng on 15/10/17.
*/
public class SensorMonitor implements SensorEventListener {
   private static final String TAG = "SensorMonitor";
   private static SensorMonitor monitor = null;
   private static Context context;
   private static int state;
   private SensorManager mSensorManager;

   /**
    * 标记是否记录
    */
   private boolean isRecorded = false;

   /**
    * 保存加速度传感器的数据序列
    */
   private  ArrayList<double[]> accelerometerDataList = new ArrayList<>();

   /**
    * 保存陀螺仪传感器的数据序列
    */
   private  ArrayList<double[]> gyroscopeDataList = new ArrayList<>();


   public SensorMonitor(Context context){
       Log.d(TAG, "构造函数");
       SensorMonitor.context = context;
       monitor = this;
   }

   public static SensorMonitor shareInstance() {
       if (monitor == null) {
           Log.e(TAG,"monitor空指针");
       }
       return monitor;
   }

   /**
    * 开始记录数据
    */
   public void startRecord() {
       setIsRecorded(true);
   }


   /**
    * 停止记录数据
    */
   public void stopRecord() {
       setIsRecorded(false);
   }


   public ArrayList<double[]> getAccelerometerDataList() {
       return accelerometerDataList;
   }

   public ArrayList<double[]> getGyroscopeDataList() {
       return gyroscopeDataList;
   }

   /**
    * 清空当前记录
    */
   public void removeLastData() {
       if (!accelerometerDataList.isEmpty()) {
           accelerometerDataList.remove(accelerometerDataList.size()-1);
       }
       if (!gyroscopeDataList.isEmpty()) {
           gyroscopeDataList.remove(gyroscopeDataList.size()-1);
       }
   }

   /**
    * 开始监听传感器
    */
   public void start() {
       Log.d(TAG,"开始监听传感器:"+System.currentTimeMillis());
       mSensorManager=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
       state = SensorManager.SENSOR_DELAY_FASTEST;
       loadSensorManager();

   }

   /**
    * 停止监听传感器
    */
   public void stop() {
       Log.d(TAG, "停止监听传感器:" + System.currentTimeMillis());
       mSensorManager.unregisterListener(this);
   }

   /**
    * 初始化SensorManager
    */
   private void loadSensorManager() {
       Log.d(TAG,"loadSensorManager()");

       mSensorManager.registerListener(this,
               mSensorManager.getDefaultSensor(
                       Sensor.TYPE_GYROSCOPE),
               state);

       mSensorManager.registerListener(this,
               mSensorManager.getDefaultSensor(
                       Sensor.TYPE_ACCELEROMETER),
               state);
   }


   /**
    * 设置是否需要记录
    * @param isRecorded
    */
   public void setIsRecorded(boolean isRecorded) {
       if(isRecorded) {
           accelerometerDataList.clear();
           gyroscopeDataList.clear();
       }
       this.isRecorded = isRecorded;
   }


   /**
    * 监听传感器数据的变化
    * @param event SensorEvent
    */
   @Override
   public void onSensorChanged(SensorEvent event) {

       if (isRecorded == false) return;
//       Log.d(TAG,"传感器:"+event.sensor.getStringType()+" 数据变化:"+System.currentTimeMillis());
       float x = event.values[0];
       float y = event.values[1];
       float z = event.values[2];
       String str = event.timestamp+":("+x+","+y+","+z+")";
       Log.d(TAG,str);
       switch(event.sensor.getType()) {
           case Sensor.TYPE_ACCELEROMETER:
               accelerometerDataList.add(new double[]{x,y,z});
               break;
           case Sensor.TYPE_GYROSCOPE:
               gyroscopeDataList.add(new double[]{x,y,z});
               break;
           default:
               return;

       }
   }


   @Override
   public void onAccuracyChanged(Sensor sensor, int accuracy) {

   }

}
