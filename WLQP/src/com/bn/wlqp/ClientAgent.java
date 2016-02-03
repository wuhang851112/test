package com.bn.wlqp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientAgent extends Thread
{
	WLQPActivity father;
	Socket sc;
	DataInputStream din;
	DataOutputStream dout;
	boolean flag=true;
	int selfNum=0;//�Լ�����ұ��
	String lastCards;//��һ�δ����
	boolean perFlag;//����Ȩ��־
	int lastNum=-1;//��һ�γ��Ƶ���ұ��
	int scores[]=new int[3];
	
	int shangjiaCount=17;//�ϼ�����
	int xiajiaCount=17;//�¼�����
	
	public ClientAgent(WLQPActivity father,Socket sc,DataInputStream din,DataOutputStream dout)
	{
		this.father=father;
		this.sc=sc;
		this.din=din;
		this.dout=dout;
	}
	@Override
	public void run()
	{
		while(flag)
		{
			try
			{
				final String msg=din.readUTF();
				System.out.println("msg:"+msg);
				if(msg.startsWith("<#ACCEPT#>"))
				{//�յ����Լ�����Ϣ
					String numStr=msg.substring(10);
					selfNum=Integer.parseInt(numStr);
					father.hd.sendEmptyMessage(0);
					//��������˷�����һ�ֵĵ÷����
					dout.writeUTF("<#SCORE#>"+selfNum+"|"+Constant.SCORE);
				}
				else if(msg.startsWith("<#START#>"))
				{//�յ���ʼ��Ϸ������Ϣ				
				   	new Thread()
				   	{
				   		public void run()
				   		{
				   			GameView.initBitmap(father.getResources()); 
					    	GameView.initCards(father.getResources());					
							WLQPActivity.cardListStr=msg.substring(9);
							father.hd.sendEmptyMessage(1);
				   		} 
				   	}.start();
				}
				else if(msg.startsWith("<#YOU#>"))
				{//�����Ȩ				
					perFlag=true;
				}
				else if(msg.startsWith("<#CURR#>"))
				{//֪������һ�����			<#CURR#>+��ұ��	
					lastNum=Integer.parseInt(msg.substring(8));					
				}
				else if(msg.startsWith("<#CARDS#>"))
				{//�õ���һ����ҳ����Ƶ���Ϣ
					lastCards=msg.substring(9);
				}
				else if(msg.startsWith("<#COUNT#>"))
				{//�õ���<#COUNT#>Ϊ��ͷ����Ϣ  
					String temps=msg.substring(9);
					String[] ta=temps.split("\\,");
					int tempNum=Integer.parseInt(ta[1]);
					int tempCount=Integer.parseInt(ta[0]);
					
					if(tempNum!=selfNum)
					{
						int ifShang=((tempNum+1)>3)?1:(tempNum+1);
						if(ifShang==selfNum)
						{
							shangjiaCount=tempCount;
						}
						
						int ifXia=((tempNum-1)==0)?3:(tempNum-1);
						if(ifXia==selfNum)
						{
							xiajiaCount=tempCount;
						}
					}					
				}
				else if(msg.startsWith("<#FINISH#>"))
				{//�õ���Ϸ��������Ϣ
					int tempNum=Integer.parseInt(msg.substring(10));
					if(tempNum==selfNum)
					{
						father.hd.sendEmptyMessage(2);
					}
					else
					{
						father.hd.sendEmptyMessage(3);
					}
					
					this.father.gameview.viewdraw.flag=false;
					
					this.flag=false;
					this.din.close();
					this.dout.close();
					this.sc.close();
				}
				else if(msg.startsWith("<#EXIT#>"))
				{//�õ�������˳���Ϸ����
					father.hd.sendEmptyMessage(4);
					this.father.gameview.viewdraw.flag=false;					
					this.flag=false;
					this.din.close();
					this.dout.close();
					this.sc.close();
				}
				else if(msg.startsWith("<#FULL#>"))
				{//�������
					father.hd.sendEmptyMessage(5);
					this.father.gameview.viewdraw.flag=false;					
					this.flag=false;
					this.din.close();
					this.dout.close();
					this.sc.close();
				}
				else if(msg.startsWith("<#SCORE#>"))
				{//�÷�������
					String ts=msg.substring(9);
					String[] sat=ts.split("\\|");
					scores[Integer.parseInt(sat[0])-1]=Integer.parseInt(sat[1]);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}