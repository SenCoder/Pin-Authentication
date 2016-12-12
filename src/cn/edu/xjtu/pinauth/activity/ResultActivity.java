package cn.edu.xjtu.pinauth.activity;

import static cn.edu.xjtu.pinauth.util.FileHelper.APP_PATH;
import static cn.edu.xjtu.pinauth.util.FileHelper.PREDICT_FALSE_DATA_Dir;
import static cn.edu.xjtu.pinauth.util.FileHelper.PREDICT_TRUE_DATA_Dir;

import java.io.File;
import java.io.FilenameFilter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import cn.edu.xjtu.pinauth.R;
import cn.edu.xjtu.pinauth.model.User;
import cn.edu.xjtu.pinauth.util.DataHandler;

public class ResultActivity extends Activity {

	private TextView mTextView;
	private int attackNum = 0;
	private  double FAR = 0.0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		mTextView = (TextView) findViewById(R.id.tv_result);
		FAR = getFARResult();
		String message = "¹¥»÷´ÎÊý£º " + attackNum + "\nFRR = " + DataHandler.format(User.shareInstance().frr/PlotActivity.para)
				+ "\nFAR = " + FAR;
		mTextView.setText(message);
		
	}
	
	private double getFARResult() {
		
		final User user = User.shareInstance();
    	String[] pFiles = new File(APP_PATH + user.getUsername() + "/" + PREDICT_TRUE_DATA_Dir).list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				if (filename.contains(user.getPassword())) {
					return true;
				}
				return false;
			}
		});
    	String[] nFiles = new File(APP_PATH + user.getUsername() + "/" + PREDICT_FALSE_DATA_Dir).list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				if (filename.contains(user.getPassword())) {
					return true;
				}
				return false;
			}
		});
    	int positiveNum = 0;
    	int negativeNum = 0;
    	if (pFiles == null) {
			positiveNum = 0;
		}
    	else {
    		positiveNum = pFiles.length;
		}
    	if (nFiles == null) {
    		negativeNum = 0;
		}
    	else {
    		negativeNum = nFiles.length;
		}
    	if (positiveNum + negativeNum == 0) {
			return 0;
		}
    	attackNum = positiveNum + negativeNum;
		return DataHandler.format(positiveNum/(double)attackNum);
    }
}
