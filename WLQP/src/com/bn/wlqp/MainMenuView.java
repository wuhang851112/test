package com.bn.wlqp;

import static com.bn.wlqp.Constant.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainMenuView extends SurfaceView implements SurfaceHolder.Callback
{
	WLQPActivity activity;
	Paint paint;
	Bitmap bitmapStart; 
	Bitmap bitmapHelp;  
	Bitmap bitmapAbout;
	Bitmap bitmapBack;
	Bitmap bitmapOut;
	
	public MainMenuView(WLQPActivity activity) {
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint();
		paint.setAntiAlias(true);
		initBitmap();
	}
	public void initBitmap()
	{
		//���ؿ�ʼ��ť��ͼƬ
		bitmapStart=BitmapFactory.decodeResource(getResources(), R.drawable.start);
		//���ذ�����ť��ͼƬ
		bitmapHelp=BitmapFactory.decodeResource(getResources(), R.drawable.help);
		//���ع��ڰ�ť��ͼƬ
		bitmapAbout=BitmapFactory.decodeResource(getResources(), R.drawable.about);
		//�����˳���ť��ͼƬ
		bitmapOut=BitmapFactory.decodeResource(getResources(), R.drawable.out);
		//���ر�����ͼƬ
		bitmapBack=BitmapFactory.decodeResource(getResources(), R.drawable.back);
	}
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawBitmap(bitmapBack, BACK_XOFFSET,BACK_YOFFSET, paint);
    	//start��ť
    	canvas.drawBitmap(bitmapStart, BUTTON_START_XOFFSET, BUTTON_START_YOFFSET, null);
    	//start��ť
    	canvas.drawBitmap(bitmapHelp, BUTTON_HELP_XOFFSET, BUTTON_HELP_YOFFSET, null);
    	//start��ť
    	canvas.drawBitmap(bitmapAbout, BUTTON_ABOUT_XOFFSET, BUTTON_ABOUT_YOFFSET, null);
    	//out��ť
    	canvas.drawBitmap(bitmapOut, BUTTON_OUT_XOFFSET, BUTTON_OUT_YOFFSET, null);
    	
	}
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{//���������¼�
		int x=(int) (e.getX());
		int y=(int) (e.getY());
		
		switch(e.getAction())
		{
		    case MotionEvent.ACTION_DOWN:
		    	if(x>BUTTON_START_XOFFSET&&x<BUTTON_START_WIDTH+BUTTON_START_XOFFSET
		    	   &&y>BUTTON_START_YOFFSET&&y<BUTTON_START_YOFFSET+BUTTON_START_HEIGHT)
				{//�Կ�ʼ��ť�ļ���  ������ǰ�ť����IpView
		    		activity.gotoIpView();
				}
		        if(x>BUTTON_HELP_XOFFSET&&x<BUTTON_HELP_XOFFSET+BUTTON_HELP_WIDTH
		           &&y>BUTTON_HELP_YOFFSET&&y<BUTTON_HELP_YOFFSET+BUTTON_HELP_HEIGHT)
		        {//�԰�����ť�ļ���
					activity.hd.sendEmptyMessage(6);
				}
				if(x>BUTTON_ABOUT_XOFFSET&&x<BUTTON_ABOUT_XOFFSET+BUTTON_ABOUT_WIDTH
				   &&y>BUTTON_ABOUT_YOFFSET&&y<BUTTON_ABOUT_YOFFSET+BUTTON_ABOUT_HEIGHT)
				{//�Թ��ڰ�ť�ļ���
					activity.hd.sendEmptyMessage(7);
				}
				if(x>BUTTON_OUT_XOFFSET&&x<BUTTON_OUT_XOFFSET+BUTTON_OUT_WIDTH
						   &&y>BUTTON_OUT_YOFFSET&&y<BUTTON_OUT_YOFFSET+BUTTON_OUT_HEIGHT)
				{//�Թ��ڰ�ť�ļ���
							System.exit(0);
				}
				
		    break;
		}
		return true;
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) 
	{	
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		this.repaint();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		
	}
	public void repaint()
	{
		SurfaceHolder holder=this.getHolder();
		Canvas canvas=holder.lockCanvas();
		try{
			synchronized(holder){
				onDraw(canvas);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(canvas!=null){
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
}