package com.bn.wlqp;
import static com.bn.wlqp.Constant.*;
public class GameViewDrawThread extends Thread
{//���ƽ���ʱ��ˢ֡�߳�
	GameView gameview;
	boolean flag=true;
	public GameViewDrawThread(GameView gameview)
	{
		this.gameview=gameview;
	}
	@Override
	public void run()
	{
		while(flag){
			gameview.repaint();//��ʱˢ֡  ������Ҫд��һ��������
			try{
				Thread.sleep(sleeptime);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}