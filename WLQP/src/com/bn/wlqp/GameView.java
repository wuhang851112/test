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
	
	static Bitmap iback; //����ͼƬ
	static Bitmap[] iscore=new Bitmap[10];  //0-9����ͼ
	static Bitmap[] wjSmall=new Bitmap[3];  //�ĸ���ҵ���ͼ
	static Bitmap[] wjHead=new Bitmap[3];  //�ĸ���Ҷ�Ӧ��ͷ�����ͼ
	static Bitmap card2; //�����˿�ͼ
	static Bitmap down1;//���½�ͼ
	static Bitmap out;   //�����˳��Ľ���
	static Bitmap fcard; //������ͼ
	static Bitmap giveup;  //����ͼƬ
	static Bitmap people1;//���ͼ
	static Bitmap people2;//�ұ�ͼ
	static Bitmap cards[][];  //�õ�ͼƬ����ͼ 
	static Bitmap own;//�Լ�������ʾͼ
	static Bitmap other;//���˳�����ʾ
	
	ArrayList<CardForControl> alcfc=new ArrayList<CardForControl>();
	
	public GameView(WLQPActivity activity) {		
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint(); 
		paint.setAntiAlias(true);	  
	} 
	public static void initBitmap(Resources r) //����ͼƬ����  
	{		
		iback=BitmapFactory.decodeResource(r, R.drawable.backg);  //����ͼ
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
		//���ϽǶ�Ӧ����ҵ�ͷ���ͼ
		wjHead[0]=BitmapFactory.decodeResource(r, R.drawable.head1);
		wjHead[1]=BitmapFactory.decodeResource(r, R.drawable.head2);
		wjHead[2]=BitmapFactory.decodeResource(r, R.drawable.head3);
		//���Ͻ�ͼ
		wjSmall[0]=BitmapFactory.decodeResource(r, R.drawable.personc);
		wjSmall[1]=BitmapFactory.decodeResource(r, R.drawable.personb);
		wjSmall[2]=BitmapFactory.decodeResource(r, R.drawable.persona); 
		card2=BitmapFactory.decodeResource(r, R.drawable.card2); //������˿�ͼ
		down1=BitmapFactory.decodeResource(r, R.drawable.down1);//���½�ͼ
		out=BitmapFactory.decodeResource(r, R.drawable.ret);
		fcard=BitmapFactory.decodeResource(r, R.drawable.fc);
		giveup=BitmapFactory.decodeResource(r, R.drawable.giveup);
		people1=BitmapFactory.decodeResource(r,R.drawable.people1);//���ͼ
		people2=BitmapFactory.decodeResource(r, R.drawable.people2);//�ұ�ͼ
		own=BitmapFactory.decodeResource(r, R.drawable.own);//�Լ�������ʾͼ
		other=BitmapFactory.decodeResource(r, R.drawable.other);// ���˳�����ʾͼ
	}
	public static void initCards(Resources r)
	{//�õ��˿���
		Bitmap srcPic=PicLoadUtil.LoadBitmap(r,R.drawable.cards);
		cards=PicLoadUtil.splitPic(6, 9, srcPic, CARD_WIDTH, CARD_HEIGHT);
	}
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		//�����������
		int x=(int)(e.getX());
		int y=(int)(e.getY());
		switch(e.getAction())
		{
		    case MotionEvent.ACTION_DOWN:
		    	 //������Լ��˿��Ƶķ�Χ��
			     if(x>CARD_SMALL_XOFFSET&&x<CARD_BIG_XOFFSET
			        &&y>DOWN_Y-MOVE_YOFFSET&&y<CARD_LEFT_YOFFSET&&activity.ca.perFlag)
			     {
			    	 int size=alcfc.size();
			    	 for(int i=size-1;i>=0;i--)
			    	 {
			    		 CardForControl cfcTemp=alcfc.get(i);//�õ��ڵ����Χ�ڵ��Ƶ����� 
			    		 if(cfcTemp.isIn(x, y)) 
			    		 {//�ж��������Ʋ������������ƶ�һ���ľ���  ����������if���
			    			 break;
			    		 } 
			    	 }  
			     }	 
			     
			     //������ذ�ť
			     if(x>LEFT_RETURN_XOFFSET&&x<LEFT_RETURN_XOFFSET+BUTTON_RETURN_WIDTH
			        &&y>LEFT_RETURN_YOFFSET&&y<LEFT_RETURN_YOFFSET+BUTTON_RETURN_HEIGHT)
			     {
			    	try 
			    	{//ͨ����������<#EXIT#>��Ϣ
						activity.ca.dout.writeUTF("<#EXIT#>");
					} catch (IOException e1) 
					{
						e1.printStackTrace();
					}
			     }
			     //������ư�ť
			     if(x>RIGHT_FCARD_XOFFSET&&x<RIGHT_FCARD_XOFFSET+BUTTON_FCARD_WIDTH
					&&y>RIGHT_FCARD_YOFFSET&&y<RIGHT_FCARD_YOFFSET+BUTTON_RETURN_HEIGHT)
				 {
					 if(activity.ca.perFlag)
					 {
						String lastCards=activity.ca.lastCards;//��һ����ҳ�����
						String currCards="";
						
						
						ArrayList<CardForControl> currSelected=new ArrayList<CardForControl>();
						
						for(CardForControl cfc:alcfc)
						{//����alcfc�����ж����Ƶ�flag��־λ   ���������currSelected��   currSelectedҪ��ŵ��������   
							if(cfc.flag)
							{
								currSelected.add(cfc);
							}
						}
						
					    for(CardForControl cfc:currSelected)
					    {//�������еĵ�������Ʋ��ҽ��ƺŴ���String��
					    	currCards=currCards+","+cfc.num;
					    }
					    
					    //���г��ƣ�ȥ��ǰ������
					    if(currCards.length()>0)
					    {
					    	currCards=currCards.substring(1);
					    }
					    
					    if(activity.ca.selfNum==activity.ca.lastNum)
					    {//�����˲�Ҫ���ֵ��Լ�����
					    	if(RuleUtil.ruleSelf(currCards)!=RuleUtil.N_A)
					    	{//�ж����Ƿ�Ϸ�
					    		try 
						    	{//�����е��ƿ��Գ�������·�����Ϣ�����趨����ҵ���Ȩ�ı�־λΪfalse
									activity.ca.dout.writeUTF("<#PLAY#>"+currCards);
									activity.ca.perFlag=false;
									//��������
									activity.playSound(1, 0);
									
									for(CardForControl cfc:currSelected)
								    {//�������ƴӴ��Ƶ�ArrayList���Ƴ�
										alcfc.remove(cfc);
								    }
									
									for(int i=0;i<alcfc.size();i++)
									{//������л��е��Ƶ�Xλ����
										alcfc.get(i).xOffset=DOWN_X+MOVE_SIZE*i;
									}
									//�ͻ����������������Ϣ<#COUNT#>+����ʣ���Ƶ�����+��ǰ��ҵı��
									activity.ca.dout.writeUTF("<#COUNT#>"+alcfc.size()+","+activity.ca.selfNum);
									
									if(alcfc.size()==0)
									{//�����е���Ϊ0ʱ����<#I_WIN#>��Ϣ
										Constant.SCORE=Constant.SCORE+15;
										activity.ca.dout.writeUTF("<#I_WIN#>");
									}
									
								} catch (IOException e1) 
								{
									e1.printStackTrace();
								}
					    	}
					    	else
					    	{//���򵯳�Toast�Ի���--->���Ϲ��򣬲�������ƣ�
					    		Toast.makeText(activity,"���Ϲ��򣬲�������ƣ�",Toast.LENGTH_SHORT).show();
					    	}
					    }
					    else
					    {//�������Լ����չ������
					    	if(RuleUtil.rule(lastCards, currCards))
					    	{//�ж����е����Ƿ����һ�ҵ�Ҫ��
					    		try 
						    	{//������<#PLAY#>+currCards��Ϣ      ���趨��־λΪfalse
									activity.ca.dout.writeUTF("<#PLAY#>"+currCards);									
									activity.ca.perFlag=false;
									//��������
									activity.playSound(1, 0);
									
									for(CardForControl cfc:currSelected)
								    {//�������ƴӴ��Ƶ�ArrayList���Ƴ�
										alcfc.remove(cfc);
								    }
									for(int i=0;i<alcfc.size();i++)
									{//������л��е��Ƶ�Xλ����
										alcfc.get(i).xOffset=DOWN_X+MOVE_SIZE*i;
									}
									//�ͻ����������������Ϣ<#COUNT#>+����ʣ���Ƶ�����+��ǰ��ҵı��
									activity.ca.dout.writeUTF("<#COUNT#>"+alcfc.size()+","+activity.ca.selfNum);
									if(alcfc.size()==0)
									{//�����е���Ϊ0ʱ����<#I_WIN#>��Ϣ
										activity.ca.dout.writeUTF("<#I_WIN#>");
									}
								} catch (IOException e1) 
								{
									e1.printStackTrace();
								}
					    	}
					    	else
					    	{//���򵯳�Toast�Ի���--->���Ϲ��򣬲�������ƣ�
					    		Toast.makeText(activity,"���Ϲ��򣬲�������ƣ�",Toast.LENGTH_SHORT).show();
					    	}
					    }
					 }
				 }
			     //���������ť
			     if(x>RIGHT_GIVEUP_XOFFSET&&x<RIGHT_GIVEUP_XOFFSET+BUTTON_GIVEUP_WIDTH
					&&y>RIGHT_GIVEUP_YOFFSET&&y<RIGHT_GIVEUP_YOFFSET+BUTTON_GIVEUP_HEIGHT)
				 {
			    	if(activity.ca.perFlag)
			    	{//activity.ca.lastNum==activity.ca.selfNum�Լ������ƺ���˶�û��Ҫ �Լ����ܷ��� �Լ��ǵ�һ����ʱ���ܷ���
			    		if(activity.ca.lastNum==activity.ca.selfNum||activity.ca.lastNum==-1)
			    		{
			    			Toast.makeText(activity,"���Ϲ��򣬲����������",Toast.LENGTH_SHORT).show();
			    			return true;
			    		}
			    		
			    		for(CardForControl cfc:alcfc)
						{//����������е��� �趨��־λ
							cfc.flag=false;
						}
						try 
						{//������<#NO_PLAY#>Ϊ��ͷ����Ϣ  ��������Ƿ�����ť��Ҫ���͵���Ϣ ���ó���Ȩ
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
	{//�õ��˿��Ƶ�������־λ���ҽ������CardForControl��alcfc������
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
		//���ñ���ͼƬ
		canvas.drawBitmap(iback, BACK_XOFFSET, BACK_YOFFSET, paint);
    	for(int i=0;i<3;i++) //������˿�
        {
        	 canvas.drawBitmap(card2, UP_X,UP_Y,paint);  
        	 UP_X=UP_X+45;
        }
        	UP_X=150;
        //�Ҳ��Ͻ����ƶ�Ӧ��ͷ��
        canvas.drawBitmap(wjHead[0], RIGHT_UP_HEAD1_XOFFSET, RIGHT_UP_HEAD1_YOFFSET, paint);
        canvas.drawBitmap(wjHead[1], RIGHT_UP_HEAD2_XOFFSET, RIGHT_UP_HEAD2_YOFFSET, paint);
        canvas.drawBitmap(wjHead[2], RIGHT_UP_HEAD3_XOFFSET, RIGHT_UP_HEAD3_YOFFSET, paint);
        //���Ͻ�����
    	canvas.drawBitmap(wjSmall[0], RIGHT_UP_PE1_X, RIGHT_UP_PE1_Y,paint); 
    	canvas.drawBitmap(wjSmall[1], RIGHT_UP_PE1_X, RIGHT_UP_PE2_Y,paint);
    	canvas.drawBitmap(wjSmall[2], RIGHT_UP_PE1_X, RIGHT_UP_PE3_Y,paint);
    	//����    	
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
    	 	
        
        canvas.drawBitmap(out, LEFT_RETURN_XOFFSET, LEFT_RETURN_YOFFSET,paint);//���Ͻǵİ�ť
        //��̬����ӦΪ��ҷ���ͷ��
        
        //���½ǵ�������ť
        canvas.drawBitmap(fcard, RIGHT_FCARD_XOFFSET, RIGHT_FCARD_YOFFSET,paint);
        canvas.drawBitmap(giveup, RIGHT_GIVEUP_XOFFSET, RIGHT_GIVEUP_YOFFSET,paint);
        
        for(int i=0;i<activity.ca.shangjiaCount;i++)//�ϼ�ʣ����˿˵��������Ƶ��˿�ͼ
        {
        	canvas.drawBitmap(card2, LEFT_CARD_XOFFSET, LEFT_CARD_YOFFSET,paint);
        	LEFT_CARD_YOFFSET=LEFT_CARD_YOFFSET+5;
        }
        LEFT_CARD_YOFFSET=100;
        
        
        for(int i=0;i<activity.ca.xiajiaCount;i++)//�¼�ʣ����˿˵��������Ƶ��˿�ͼ
        {
        	canvas.drawBitmap(card2, RIGHT_CARD_XOFFSET, RIGHT_CARD_YOFFSET,paint);
        	RIGHT_CARD_YOFFSET=RIGHT_CARD_YOFFSET+5;
        }
        RIGHT_CARD_YOFFSET=100;
        
        
        //ѭ�����е��ŵÿ��������һ����Լ����е���
    	for(CardForControl cc:alcfc)
    	{
    		cc.drawSelf(canvas);
    	}
    	//�������
    	if((activity.ca.selfNum-1)<=0)
        {
        	canvas.drawBitmap(down1, LEFT_DOWN_X, LEFT_DOWN_Y,paint);//��ǰ���
        	canvas.drawBitmap(people1, LEFT_X, LEFT_Y,paint);//�ϼ����
        	canvas.drawBitmap(people2, RIGHT_PERSON_XOFFSET, RIGHT_PERSON_YOFFSET,paint);//�¼���� 
        }
        else
        if((activity.ca.selfNum+1)>3)
        {
        	canvas.drawBitmap(people1, LEFT_DOWN_X, LEFT_DOWN_Y,paint);//��ǰ���
        	canvas.drawBitmap(people2, LEFT_X, LEFT_Y,paint);//�ϼ����
        	canvas.drawBitmap(down1, RIGHT_PERSON_XOFFSET, RIGHT_PERSON_YOFFSET,paint);//�¼����
        }
        else 
        {
        	canvas.drawBitmap(people2, LEFT_DOWN_X, LEFT_DOWN_Y,paint);//��ǰ���
        	canvas.drawBitmap(down1, LEFT_X, LEFT_Y,paint);//�ϼ����
        	canvas.drawBitmap(people1, RIGHT_PERSON_XOFFSET, RIGHT_PERSON_YOFFSET,paint);//�¼����
        }
    	//�����Լ����Ǳ��˳��Ƶ���ʾ
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
	{//����SurfaceViewʱҪ��ʼ��initCardsForControl()������ͬ��Ҫ�����߳�viewdraw   (��̨����ˢ֡���߳�)
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
	{//SurfaceView����ʱ
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