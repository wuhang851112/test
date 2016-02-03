package com.bn.wlqp;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class PicLoadUtil 
{

   //����Դ�м���һ��ͼƬ
   public static Bitmap LoadBitmap(Resources res,int picId)
   {
	   Bitmap result=BitmapFactory.decodeResource(res, picId);
	   return result;
   }
   
   //������תͼƬ�ķ���
   public static Bitmap scaleToFit(Bitmap bm,int dstWidth,int dstHeight)//����ͼƬ�ķ���
   {
   	float width = bm.getWidth(); //ͼƬ���
   	float height = bm.getHeight();//ͼƬ�߶�
   	float wRatio=dstWidth/height;
   	float hRatio=dstHeight/width;
   	
   	Matrix m1 = new Matrix(); 
   	m1.postScale(wRatio, hRatio);
   	Matrix m2= new Matrix();
   	m2.setRotate(90, dstWidth/2, dstHeight/2);
   	Matrix mz=new Matrix();
	mz.setConcat(m1, m2);
   	
   	Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, (int)width, (int)height, mz, true);//����λͼ        	
   	return bmResult;
   }
   
   public static Bitmap[][] splitPic
   (
		   int cols,//�и������ 
		   int rows,//�и������    
		   Bitmap srcPic,//���и��ͼƬ  
		   int dstWitdh,//�и�������Ŀ����
		   int dstHeight//�и�������Ŀ��߶�  
   ) 
   {   
	   final float width=srcPic.getWidth();
	   final float height=srcPic.getHeight();
	   
	   final int tempWidth=(int)(width/cols);
	   final int tempHeight=(int)(height/rows);
	   
	   Bitmap[][] result=new Bitmap[rows][cols];
	   
	   for(int i=0;i<rows;i++)
	   {
		   for(int j=0;j<cols;j++)
		   {
			   Bitmap tempBm=Bitmap.createBitmap(srcPic, j*tempWidth, i*tempHeight,tempWidth, tempHeight);		
			   result[i][j]=scaleToFit(tempBm,dstWitdh,dstHeight);
		   }
	   }
	   
	   return result;
   }
}