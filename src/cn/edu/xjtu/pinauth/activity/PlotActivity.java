package cn.edu.xjtu.pinauth.activity;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.model.PointValue;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.edu.xjtu.pinauth.R;
import cn.edu.xjtu.pinauth.model.AuthEvent;
import cn.edu.xjtu.pinauth.model.User;
import cn.edu.xjtu.pinauth.util.FileHelper;


public class PlotActivity extends Activity {
	
//	private TextView mTextView;
	private LineChartView mLineChartView;
//	private ProgressDialog progressDialog;
	private ProgressBar mProgressBar;
	
	public static int para = 3;
	private double frr;
	private int MAX_ITER = 20;
	
	private ArrayList<double[]> dataList;
	private MyHandler mHandler;
	
	private ArrayList<AuthEvent> authEvents;
	private User user;
	
	private Thread learningThread;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
//        mTextView = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.bar_learning);
        mLineChartView = (LineChartView) findViewById(R.id.learning_curve);
        setLineChart();
        
        user = User.shareInstance();
        dataList = new ArrayList<>();
        
//        new Thread(new MyRunnable()).start();
//        mLineChartView.setLineChartData(listToLineData(dataList));
//        updateDataHandler.post(updateThread);
//        mLineChartView.getLineChartData();
//        updateDataHandler.post(updateThread);
//        mLineChartView.refreshDrawableState();
        
//        progressDialog = ProgressDialog.show(this, "阈值学习中", "wait . . .", true);
//        new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				learning();
//		    	progressDialog.dismiss();
//			}
//		}).start();
	}
    
    @Override
    	protected void onStart() {
    		// TODO Auto-generated method stub
    		super.onStart();
    		authEvents = FileHelper.loadRawData(user.getUsername(), user.getPassword(), FileHelper.TRAINING_DATA_Dir);
            mHandler = new MyHandler();
            learningThread = new Thread(new MyThread());
            learningThread.start();
            System.out.println("plot start");
    	}
    
    class MyHandler extends Handler {
    	
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		super.handleMessage(msg);
    		if (msg.arg1 == 0) {
    			mProgressBar.setProgress((int) ((MAX_ITER - msg.arg1)/(double) MAX_ITER * mProgressBar.getMax()));
				Toast.makeText(PlotActivity.this, "learning over", Toast.LENGTH_LONG).show();
			}
    		else {
    			mProgressBar.setProgress((int) ((MAX_ITER - msg.arg1)/(double) MAX_ITER * mProgressBar.getMax()));
    			dataList.add((double[]) msg.obj);
        		mLineChartView.setLineChartData(listToLineData(dataList));
//        		updateDataHandler.post(updateThread);
			}
    	}
    }
    
 /*   Handler updateDataHandler = new Handler() {
    	
    	@Override
    	public void handleMessage(android.os.Message msg) {
//    		dataList.add
//    		mLineChartView.getLineChartData().getLines().remove(0);
//    		updateDataHandler.post(updateThread);
    		if (msg.arg1 == 1) {
				Toast.makeText(PlotActivity.this, "learning over", Toast.LENGTH_LONG).show();
			}
    		else {
    			dataList.add((double[]) msg.obj);
        		mLineChartView.setLineChartData(listToLineData(dataList));
//        		updateDataHandler.post(updateThread);
			}
    	};
    };
 */   
//    Runnable updateThread = new Runnable() {
//    	
//    	int iterNum = 30;
//		@Override
//		public void run() {
//			iterNum --;
//			Message msg = updateDataHandler.obtainMessage();
//			msg.obj = learning();
//			if(iterNum <= 0 || Math.abs(frr - user.FIXED_FRR) < 0.01){
//				
//				user.isThresholdLearning = true;
//		    	user.frr = frr;
//		    	msg.arg1 = 1;
//		    	updateDataHandler.sendMessage(msg);
//				updateDataHandler.removeCallbacks(updateThread);
//			} else {
//				updateDataHandler.sendMessage(msg);
//			}
//		}
//    };
    
    class MyThread implements Runnable {
    	
    	int iterNum = MAX_ITER;
    	@Override
    	public void run() {
    		user.setThreshold(1.41);
    		while(iterNum > 0 && Math.abs(frr - user.FIXED_FRR) > 0.005) {
    			iterNum --;
    			Message msg = new Message();
    			msg.arg1 = iterNum;
    			msg.obj = learning();
				PlotActivity.this.mHandler.sendMessage(msg);
    		}
    		Message msg = new Message();
			user.setThresholdLearning(true);
	    	user.frr = frr;
	    	if (user.getThreshold() > 5) {
				user.setThreshold(5/(user.getThreshold() - 4));
			}
	    	msg.arg1 = 0;
	    	PlotActivity.this.mHandler.sendMessage(msg);
    	};
    };
    
    private double[] learning() {
    	
    	double[] ret = new double[2];
    	frr = user.reTrain(authEvents, 10, user.getThreshold());	// train_num = 10
    	ret[1] = user.getThreshold();
    	ret[0] = frr;
    	
    	user.setThreshold(user.getThreshold() * (1 + 3* (frr - user.FIXED_FRR)));
    	
    	return ret;
//    	StringBuffer message = new StringBuffer();
//    	double frr = 0;
//    	int iterNum = 30;
//    	User user = User.shareInstance();
//    	ArrayList<AuthEvent> authEvents = FileHelper.loadRawData(user.getUsername(), user.getPassword(), FileHelper.TRAINING_DATA_Dir);
//    	do
//    	{
    		
//    		message.append("\n> " + DataHandler.format(user.getThreshold()) + ":" + DataHandler.format(frr/para));
    		
//    		iterNum --;
//    	} while(Math.abs(frr - user.FIXED_FRR) > 0.01 && iterNum > 0);
    	
//    	message.append("\n> end");
//    	return message.toString();
	}
    
    private LineChartData listToLineData(ArrayList<double[]> dataList) {
    	
    	List<PointValue> mPointValues = new ArrayList<>();
    	List<AxisValue> mAxisValues = new ArrayList<>();
        for (int i = 0; i < dataList.size() ; i++) {
			mPointValues.add(new PointValue(i, (float) dataList.get(i)[1]));
			mAxisValues.add(new AxisValue(i).setLabel(i + ""));	//为每个对应的i设置相应的label(显示在X轴)
		}
		Line line = new Line(mPointValues).setColor(Color.rgb(149, 217, 247)).setCubic(true).setStrokeWidth(0);
		List<Line> lines = new ArrayList<Line>();
		lines.add(line);
		LineChartData data = new LineChartData();
		data.setLines(lines);
		
		//坐标轴
		Axis axisX = new Axis();
		axisX.setValues(mAxisValues);
		axisX.setName("阈值学习曲线");
		Axis axisY = new Axis();
		data.setAxisXBottom(axisX);
		data.setAxisYLeft(axisY);
		
		return data;
    }
    
    private void setLineChart() {

		//设置行为属性，支持缩放、滑动以及平移
		mLineChartView.setInteractive(true);
		mLineChartView.setZoomEnabled(false);
		mLineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
		mLineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
		mLineChartView.setVisibility(View.VISIBLE);
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			System.out.println("back ket down");
			if (learningThread.isAlive()) {
				learningThread.destroy();
			}
		}
	    return super.onKeyDown(keyCode, event);
	}
    
}
