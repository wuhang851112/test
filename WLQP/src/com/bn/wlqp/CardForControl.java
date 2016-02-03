package com.bn.wlqp;
import static com.bn.wlqp.Constant.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CardForControl 
{//������
	Bitmap bitmapTmp;//�Ƶ�Bitmap
	int xOffset;     //�Ƶ�X��ƫ����
	boolean flag=false;  //�Ƿ���Ƶı�־
	int num;//0-53   �ƺ�

	public CardForControl(Bitmap bitmapTmp,int xOffset,	int num)
	{//������
		this.bitmapTmp=bitmapTmp;
		this.xOffset=xOffset;
		this.num=num;		
	}
	
	
	public void drawSelf(Canvas canvas)
	{   /*����һ��ͼƬ��flagΪfalseʱ����ƽ���Ĳ����������¼��ģ�
		���������¼�֮��flag��Ϊtrue�����ڻ��Ƶ�ʱ��ʹ�������ƶ�MOVE_YOFFSE����*/
		if(!flag)
		{
			canvas.drawBitmap(bitmapTmp, xOffset, DOWN_Y, null);
		}
		else
		{
			canvas.drawBitmap(bitmapTmp, xOffset, DOWN_Y-MOVE_YOFFSET, null);
		}
	}
	
	public boolean isIn(int x,int y)
	{//�ж�Ҫ������������
		boolean result=false;
		int yUp=(flag)?DOWN_Y-MOVE_YOFFSET:DOWN_Y;

		
		if(x>xOffset&&x<xOffset+CARD_WIDTH
		   && y>yUp&&y<yUp+CARD_HEIGHT)
		{//�жϵ�����������������Ƶķ�Χ�� �����趨��־λ �����趨flag��booleanֵ
			flag=!flag;
			result=true;
		}		
		return result;
	}
}