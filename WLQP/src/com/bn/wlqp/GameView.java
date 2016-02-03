package com.bn.wlqp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.bn.wlqp.R;

import android.R.color;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import static com.bn.wlqp.Constant.*;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
	WLQPActivity activity;
	Paint paint;
	GameViewDrawThread viewdraw;
	
	static Bitmap iback; //背景图片
	static Bitmap[] iscore=new Bitmap[10];  //0-9的贴图
	static Bitmap[] wjSmall=new Bitmap[3];  //哪个玩家的贴图
	static Bitmap[] wjHead=new Bitmap[3];  //哪个玩家对应的头像的贴图
	static Bitmap card2; //反面扑克图
	static Bitmap down1;//左下角图
	static Bitmap out;   //有人退出的界面
	static Bitmap fcard; //出牌贴图
	static Bitmap giveup;  //放弃图片
	static Bitmap people1;//左边图
	static Bitmap people2;//右边图
	static Bitmap cards[][];  //得到图片的贴图 
	static Bitmap own;//自己出牌提示图
	static Bitmap other;//别人出牌提示
	
	ArrayList<CardForControl> alcfc=new ArrayList<CardForControl>();
	
	public GameView(WLQPActivity activity) {		
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint(); 
		paint.setAntiAlias(true);	  
	} 
	public static void initBitmap(Resources r) //加载图片方法  
	{		
		iback=BitmapFactory.decodeResource(r, R.drawable.backg);  //背景图
		iscore[0]=BitmapFactory.decodeResource(r, R.drawable.zero);
		iscore[1]=BitmapFactory.decodeResource(r, R.drawable.one);
		iscore[2]=BitmapFactory.decodeResource(r, R.drawable.two);
		iscore[3]=BitmapFactory.decodeResource(r, R.drawable.three);
		iscore[4]=BitmapFactory.decodeResource(r, R.drawable.four);
		iscore[5]=BitmapFactory.decodeResource(r, R.drawable.five);
		iscore[6]=BitmapFactory.decodeResource(r, R.drawable.six);
		iscore[7]=BitmapFactory.decodeResource(r, R.drawable.seven);
		iscore[8]=BitmapFactory.decodeResource(r, R.drawable.eight);
		iscore[9]=BitmapFactory.decodeResource(r, R.drawable.nine);
		//右上角对应的玩家的头像的图
		wjHead[0]=BitmapFactory.decodeResource(r, R.drawable.head1);
		wjHead[1]=BitmapFactory.decodeResource(r, R.drawable.head2);
		wjHead[2]=BitmapFactory.decodeResource(r, R.drawable.head3);
		//右上角图
		wjSmall[0]=BitmapFactory.decodeResource(r, R.drawable.personc);
		wjSmall[1]=BitmapFactory.decodeResource(r, R.drawable.personb);
		wjSmall[2]=BitmapFactory.decodeResource(r, R.drawable.persona); 
		card2=BitmapFactory.decodeResource(r, R.drawable.card2); //上面的扑克图
		down1=BitmapFactory.decodeResource(r, R.drawable.down1);//左下角图
		out=BitmapFactory.decodeResource(r, R.drawable.ret);
		fcard=BitmapFactory.decodeResource(r, R.drawable.fc);
		giveup=BitmapFactory.decodeResource(r, R.drawable.giveup);
		people1=BitmapFactory.decodeResource(r,R.drawable.people1);//左边图
		people2=BitmapFactory.decodeResource(r, R.drawable.people2);//右边图
		own=BitmapFactory.decodeResource(r, R.drawable.own);//自己出牌提示图
		other=BitmapFactory.decodeResource(r, R.drawable.other);// 别人出牌提示图
	}
	public static void initCards(Resources r)
	{//得到扑克牌
		Bitmap srcPic=PicLoadUtil.LoadBitmap(r,R.drawable.cards);
		cards=PicLoadUtil.splitPic(6, 9, srcPic, CARD_WIDTH, CARD_HEIGHT);
	}
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		//触摸点的坐标
		int x=(int)(e.getX());
		int y=(int)(e.getY());
		switch(e.getAction())
		{
		    case MotionEvent.ACTION_DOWN:
		    	 //点击在自己扑克牌的范围内
			     if(x>CARD_SMALL_XOFFSET&&x<CARD_BIG_XOFFSET
			        &&y>DOWN_Y-MOVE_YOFFSET&&y<CARD_LEFT_YOFFSET&&activity.ca.perFlag)
			     {
			    	 int size=alcfc.size();
			    	 for(int i=size-1;i>=0;i--)
			    	 {
			    		 CardForControl cfcTemp=alcfc.get(i);//得到在点击范围内的牌的引用 
			    		 if(cfcTemp.isIn(x, y)) 
			    		 {//判断是哪张牌并且让牌向上移动一定的距离  并且跳出该if语句
			    			 break;
			    		 } 
			    	 }  
			     }	 
			     
			     //点击返回按钮
			     if(x>LEFT_RETURN_XOFFSET&&x<LEFT_RETURN_XOFFSET+BUTTON_RETURN_WIDTH
			        &&y>LEFT_RETURN_YOFFSET&&y<LEFT_RETURN_YOFFSET+BUTTON_RETURN_HEIGHT)
			     {
			    	try 
			    	{//通过输出流输出<#EXIT#>信息
						activity.ca.dout.writeUTF("<#EXIT#>");
					} catch (IOException e1) 
					{
						e1.printStackTrace();
					}
			     }
			     //点击出牌按钮
			     if(x>RIGHT_FCARD_XOFFSET&&x<RIGHT_FCARD_XOFFSET+BUTTON_FCARD_WIDTH
					&&y>RIGHT_FCARD_YOFFSET&&y<RIGHT_FCARD_YOFFSET+BUTTON_RETURN_HEIGHT)
				 {
					 if(activity.ca.perFlag)
					 {
						String lastCards=activity.ca.lastCards;//上一个玩家出的牌
						String currCards="";
						
						
						ArrayList<CardForControl> currSelected=new ArrayList<CardForControl>();
						
						for(CardForControl cfc:alcfc)
						{//遍历alcfc并且判定该牌的flag标志位   并将其存入currSelected中   currSelected要存放点击到的牌   
							if(cfc.flag)
							{
								currSelected.add(cfc);
							}
						}
						
					    for(CardForControl cfc:currSelected)
					    {//遍历手中的点击到的牌并且将牌号存入String中
					    	currCards=currCards+","+cfc.num;
					    }
					    
					    //若有出牌，去掉前导逗号
					    if(currCards.length()>0)
					    {
					    	currCards=currCards.substring(1);
					    }
					    
					    if(activity.ca.selfNum==activity.ca.lastNum)
					    {//若别人不要又轮到自己出牌
					    	if(RuleUtil.ruleSelf(currCards)!=RuleUtil.N_A)
					    	{//判断牌是否合法
					    		try 
						    	{//在手中的牌可以出的情况下发送消息并且设定该玩家的牌权的标志位为false
									activity.ca.dout.writeUTF("<#PLAY#>"+currCards);
									activity.ca.perFlag=false;
									//播放声音
									activity.playSound(1, 0);
									
									for(CardForControl cfc:currSelected)
								    {//将发的牌从存牌的ArrayList中移除
										alcfc.remove(cfc);
								    }
									
									for(int i=0;i<alcfc.size();i++)
									{//玩家手中还有的牌的X位移量
										alcfc.get(i).xOffset=DOWN_X+MOVE_SIZE*i;
									}
									//客户端向服务器发送消息<#COUNT#>+手中剩余牌的数量+当前玩家的编号
									activity.ca.dout.writeUTF("<#COUNT#>"+alcfc.size()+","+activity.ca.selfNum);
									
									if(alcfc.size()==0)
									{//当手中的牌为0时发送<#I_WIN#>消息
										Constant.SCORE=Constant.SCORE+15;
										activity.ca.dout.writeUTF("<#I_WIN#>");
									}
									
								} catch (IOException e1) 
								{
									e1.printStackTrace();
								}
					    	}
					    	else
					    	{//否则弹出Toast对话框--->不合规则，不允许出牌！
					    		Toast.makeText(activity,"不合规则，不允许出牌！",Toast.LENGTH_SHORT).show();
					    	}
					    }
					    else
					    {//若不是自己则按照规则出牌
					    	if(RuleUtil.rule(lastCards, currCards))
					    	{//判断手中的牌是否比上一家的要大
					    		try 
						    	{//并发送<#PLAY#>+currCards消息      并设定标志位为false
									activity.ca.dout.writeUTF("<#PLAY#>"+currCards);									
									activity.ca.perFlag=false;
									//播放声音
									activity.playSound(1, 0);
									
									for(CardForControl cfc:currSelected)
								    {//将发的牌从存牌的ArrayList中移除
										alcfc.remove(cfc);
								    }
									for(int i=0;i<alcfc.size();i++)
									{//玩家手中还有的牌的X位移量
										alcfc.get(i).xOffset=DOWN_X+MOVE_SIZE*i;
									}
									//客户端向服务器发送消息<#COUNT#>+手中剩余牌的数量+当前玩家的编号
									activity.ca.dout.writeUTF("<#COUNT#>"+alcfc.size()+","+activity.ca.selfNum);
									if(alcfc.size()==0)
									{//当手中的牌为0时发送<#I_WIN#>消息
										activity.ca.dout.writeUTF("<#I_WIN#>");
									}
								} catch (IOException e1) 
								{
									e1.printStackTrace();
								}
					    	}
					    	else
					    	{//否则弹出Toast对话框--->不合规则，不允许出牌！
					    		Toast.makeText(activity,"不合规则，不允许出牌！",Toast.LENGTH_SHORT).show();
					    	}
					    }
					 }
				 }
			     //点击放弃按钮
			     if(x>RIGHT_GIVEUP_XOFFSET&&x<RIGHT_GIVEUP_XOFFSET+BUTTON_GIVEUP_WIDTH
					&&y>RIGHT_GIVEUP_YOFFSET&&y<RIGHT_GIVEUP_YOFFSET+BUTTON_GIVEUP_HEIGHT)
				 {
			    	if(activity.ca.perFlag)
			    	{//activity.ca.lastNum==activity.ca.selfNum自己出了牌后别人都没有要 自己不能放弃 自己是第一个的时候不能放弃
			    		if(activity.ca.lastNum==activity.ca.selfNum||activity.ca.lastNum==-1)
			    		{
			    			Toast.makeText(activity,"不合规则，不允许放弃！",Toast.LENGTH_SHORT).show();
			    			return true;
			    		}
			    		
			    		for(CardForControl cfc:alcfc)
						{//遍历玩家手中的牌 设定标志位
							cfc.flag=false;
						}
						try 
						{//发送以<#NO_PLAY#>为开头的信息  即点击的是放弃按钮所要发送的信息 并让出牌权
							activity.ca.dout.writeUTF("<#NO_PLAY#>");
							activity.ca.perFlag=false; 
							
						} catch (IOException e1) 
						{
							e1.printStackTrace();
						}

			    	}
				}
			break;
		}
		return true;
	}
	 
	public void initCardsForControl(String cardListStr)
	{//得到扑克牌的整数标志位并且将其存在CardForControl的alcfc对象中
		alcfc.clear();
		System.out.println(cardListStr);
		String[] cardNums=cardListStr.split("\\,");
		int c=cardNums.length;
		
		int numsTemp[]=new int[17];
		for(int i=0;i<c;i++)
		{
			numsTemp[i]=Integer.parseInt(cardNums[i]);			
		}		
		Arrays.sort(numsTemp);
		
		for(int i=0;i<c;i++)
		{
			int num=numsTemp[i];
			int[] ab=Constant.fromNumToAB(num);
			CardForControl cc=new CardForControl(cards[ab[0]][ab[1]],DOWN_X+MOVE_SIZE*i,num);
			alcfc.add(cc);
		}
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		//设置背景图片
		canvas.drawBitmap(iback, BACK_XOFFSET, BACK_YOFFSET, paint);
    	for(int i=0;i<3;i++) //上面的扑克
        {
        	 canvas.drawBitmap(card2, UP_X,UP_Y,paint);  
        	 UP_X=UP_X+45;
        }
        	UP_X=150;
        //右侧上角名称对应的头像
        canvas.drawBitmap(wjHead[0], RIGHT_UP_HEAD1_XOFFSET, RIGHT_UP_HEAD1_YOFFSET, paint);
        canvas.drawBitmap(wjHead[1], RIGHT_UP_HEAD2_XOFFSET, RIGHT_UP_HEAD2_YOFFSET, paint);
        canvas.drawBitmap(wjHead[2], RIGHT_UP_HEAD3_XOFFSET, RIGHT_UP_HEAD3_YOFFSET, paint);
        //右上角名称
    	canvas.drawBitmap(wjSmall[0], RIGHT_UP_PE1_X, RIGHT_UP_PE1_Y,paint); 
    	canvas.drawBitmap(wjSmall[1], RIGHT_UP_PE1_X, RIGHT_UP_PE2_Y,paint);
    	canvas.drawBitmap(wjSmall[2], RIGHT_UP_PE1_X, RIGHT_UP_PE3_Y,paint);
    	//分数    	
    	for(int i=0;i<activity.ca.scores.length;i++)
    	{
    		int sct=activity.ca.scores[i];
    		String ts=sct+"";
    		for(int j=0;j<ts.length();j++)
    		{
    			canvas.drawBitmap
    			(
    				iscore[ts.charAt(j)-'0'], 
    				RIGHT_UP_PEJ_X+j*RIGHT_UP_PEJ_X_SPAN, 
    				RIGHT_UP_PEJ_Y+i*RIGHT_UP_PEJ_Y_SPAN,
    				paint
    			);
    		}
    	}
    	 	
        
        canvas.drawBitmap(out, LEFT_RETURN_XOFFSET, LEFT_RETURN_YOFFSET,paint);//右上角的按钮
        //动态的适应为玩家分配头像
        
        //右下角的两个按钮
        canvas.drawBitmap(fcard, RIGHT_FCARD_XOFFSET, RIGHT_FCARD_YOFFSET,paint);
        canvas.drawBitmap(giveup, RIGHT_GIVEUP_XOFFSET, RIGHT_GIVEUP_YOFFSET,paint);
        
        for(int i=0;i<activity.ca.shangjiaCount;i++)//上家剩余的扑克的数量绘制的扑克图
        {
        	canvas.drawBitmap(card2, LEFT_CARD_XOFFSET, LEFT_CARD_YOFFSET,paint);
        	LEFT_CARD_YOFFSET=LEFT_CARD_YOFFSET+5;
        }
        LEFT_CARD_YOFFSET=100;
        
        
        for(int i=0;i<activity.ca.xiajiaCount;i++)//下家剩余的扑克的数量绘制的扑克图
        {
        	canvas.drawBitmap(card2, RIGHT_CARD_XOFFSET, RIGHT_CARD_YOFFSET,paint);
        	RIGHT_CARD_YOFFSET=RIGHT_CARD_YOFFSET+5;
        }
        RIGHT_CARD_YOFFSET=100;
        
        
        //循环手中的排得控制量并且绘制自己手中的牌
    	for(CardForControl cc:alcfc)
    	{
    		cc.drawSelf(canvas);
    	}
    	//绘制玩家
    	if((activity.ca.selfNum-1)<=0)
        {
        	canvas.drawBitmap(down1, LEFT_DOWN_X, LEFT_DOWN_Y,paint);//当前玩家
        	canvas.drawBitmap(people1, LEFT_X, LEFT_Y,paint);//上家玩家
        	canvas.drawBitmap(people2, RIGHT_PERSON_XOFFSET, RIGHT_PERSON_YOFFSET,paint);//下家玩家 
        }
        else
        if((activity.ca.selfNum+1)>3)
        {
        	canvas.drawBitmap(people1, LEFT_DOWN_X, LEFT_DOWN_Y,paint);//当前玩家
        	canvas.drawBitmap(people2, LEFT_X, LEFT_Y,paint);//上家玩家
        	canvas.drawBitmap(down1, RIGHT_PERSON_XOFFSET, RIGHT_PERSON_YOFFSET,paint);//下家玩家
        }
        else 
        {
        	canvas.drawBitmap(people2, LEFT_DOWN_X, LEFT_DOWN_Y,paint);//当前玩家
        	canvas.drawBitmap(down1, LEFT_X, LEFT_Y,paint);//上家玩家
        	canvas.drawBitmap(people1, RIGHT_PERSON_XOFFSET, RIGHT_PERSON_YOFFSET,paint);//下家玩家
        }
    	//绘制自己还是别人出牌的提示
    	if(activity.ca.perFlag)
    	{
    		canvas.drawBitmap(own, TIP_OWN_XOFFSET, TIP_OWN_YOFFSET, paint);
    	}
    	else
    	{ 
    		canvas.drawBitmap(other, TIP_OWN_XOFFSET, TIP_OTHER_YOFFSET, paint);
    	}
    	
    	//
    	String lastTemp=activity.ca.lastCards;
    	if(lastTemp!=null)
    	{
    		String[] saTemp=lastTemp.split("\\,");
    		for(int i=0;i<saTemp.length;i++)
    		{
    			int nTemp=Integer.parseInt(saTemp[i]);
    			int[] abTemp=Constant.fromNumToAB(nTemp);    			
    			canvas.drawBitmap
    			(
    				cards[abTemp[0]][abTemp[1]], 
    				MIDDLE_CARD1_XOFFSET+i*MIDDLE_CARD_SPAN, 
    				MIDDLE_CARD1_YOFFSET,
    				paint
    			);    			
    		}
    	}
    	
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{//创建SurfaceView时要初始化initCardsForControl()方法，同事要启动线程viewdraw   (后台不断刷帧的线程)
		initCardsForControl(WLQPActivity.cardListStr);	
		if(viewdraw==null)
		{
			viewdraw=new GameViewDrawThread(this);
			viewdraw.flag=true;
			viewdraw.start();
		}
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{//SurfaceView销毁时
		boolean reatry=true;
		viewdraw.flag=false;
		while(reatry){
			try{
				viewdraw.join();
				reatry=false;
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	public void repaint()
	{
		SurfaceHolder surfaceholder=this.getHolder();
		Canvas canvas=surfaceholder.lockCanvas();
		try
		{
			synchronized(surfaceholder)
			{
				onDraw(canvas);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(canvas!=null)
			{
				surfaceholder.unlockCanvasAndPost(canvas);
			}
		}
	}
}