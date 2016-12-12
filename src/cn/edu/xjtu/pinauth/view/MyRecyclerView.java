package cn.edu.xjtu.pinauth.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyRecyclerView extends RecyclerView {

//	private int mPosition;
	
	public MyRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		super.dispatchTouchEvent(ev);
//		return false;
//	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
	   if(ev.getAction() == MotionEvent.ACTION_MOVE)
	      return true;
	   return super.dispatchTouchEvent(ev);
	}
}
