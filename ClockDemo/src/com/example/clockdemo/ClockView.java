package com.example.clockdemo;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class ClockView extends View {
	private int mRound;
	private int screenWidth;
	private int screenHeight;
    private Thread repeatThread;
    private boolean isShowing = false;
	public ClockView(Context context) {
		super(context);
	}

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		repeatThread = new Thread() {
			@Override
			public void run() {
				
				while(isShowing){
					postInvalidate();
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		};
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 定义画笔
		Paint dialPaint = new Paint();
		dialPaint.setColor(Color.BLACK);
		dialPaint.setStrokeWidth(3);
		dialPaint.setStyle(Paint.Style.STROKE);
		dialPaint.setAntiAlias(true);
		// 获取屏幕宽度和高度
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		// 将画布的绘制起点移动到屏幕正中央，以宽度一半去100像素为半径画圆
		canvas.translate(screenWidth / 2, screenHeight / 2);
		canvas.drawCircle(0, 0, screenWidth / 2 - 100, dialPaint);
		canvas.save();

		Paint tmpPaint = new Paint(dialPaint);
		tmpPaint.setStrokeWidth(2);
		tmpPaint.setTextSize(50);
		float y = 100;
		int count = 60;// 总刻度数
		String[] tmpCount = new String[] { "12", "1", "2", "3", "4", "5", "6",
				"7", "8", "9", "10", "11" };
		for (int i = 0; i < count; i++) {
			if (i % 5 == 0) {
				canvas.drawLine(0, -screenWidth / 2 + 100, 0, -385, dialPaint);
				if (Integer.parseInt(tmpCount[i / 5]) > 9)
					canvas.drawText(tmpCount[i / 5], -20,
							-screenWidth / 2 + 95, tmpPaint);
				else
					canvas.drawText(tmpCount[i / 5], -12,
							-screenWidth / 2 + 95, tmpPaint);

			} else {
				canvas.drawLine(0, -screenWidth / 2 + 100, 0,
						-screenWidth / 2 + 110, tmpPaint);
			}
			canvas.rotate(360 / count, 0, 0);// 旋转画纸
		}

		canvas.restore();
		tmpPaint.setStyle(Paint.Style.FILL);
		tmpPaint.setColor(Color.BLACK);
		canvas.drawCircle(0, 0, 10, tmpPaint);
		// 根据当前时间得到指针对应的坐标位置
		int[] coordinate = GetValues();
		//绘制时针
		canvas.drawLine(0, 0, coordinate[0], coordinate[1], dialPaint);
		//绘制分针
		canvas.drawLine(0, 0, coordinate[2], coordinate[3], dialPaint);
		//绘制秒针
		canvas.drawLine(0, 0, coordinate[4], coordinate[5], dialPaint);

	}

	// 根据指针偏移的角度，得到纵横坐标
	private int[] GetValues() {
		int values[] = new int[6];
		// 获取系统时间的时、分、秒
		Calendar calendar = Calendar.getInstance();
		double hour = calendar.get(Calendar.HOUR);
		double minute = calendar.get(Calendar.MINUTE);
		double second = calendar.get(Calendar.SECOND);
		double degreeSecond = second * 360 / 60;
		double degreeMinute = minute * 360 / 60+(double)second/60;
        double degreeHour=hour*360/12+30*minute/60;
		int xSecondCount = (int) ((screenWidth / 2 - 100) * Math
				.sin(degreeSecond * Math.PI / 180));
		int ySecondCount = (int) ((screenWidth / 2 - 100) * Math
				.cos(degreeSecond * Math.PI / 180));
		int xMinuteCount = (int) ((screenWidth / 2 - 100) * Math
				.sin(degreeMinute * Math.PI / 180));
		int yMinuteCount = (int) ((screenWidth / 2 - 100) * Math
				.cos(degreeMinute * Math.PI / 180));
		//获取时针的坐标
		values[0]=(int) ((screenWidth / 2 - 100) * Math
				.sin(degreeHour * Math.PI / 180));
		values[1]=-(int) ((screenWidth / 2 - 100) * Math
				.cos(degreeHour * Math.PI / 180));
		//获取分针的坐标
		values[2]=(int) ((screenWidth / 2 - 100) * Math
				.sin(degreeMinute * Math.PI / 180));
		values[3]=-(int) ((screenWidth / 2 - 100) * Math
				.cos(degreeMinute* Math.PI / 180));
		//获取秒针的坐标
		values[4]=(int) ((screenWidth / 2 - 100) * Math
				.sin(degreeSecond * Math.PI / 180));
		values[5]=-(int) ((screenWidth / 2 - 100) * Math
				.cos(degreeSecond * Math.PI / 180));
		return values;
	}
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		isShowing = true;
		repeatThread.start();
		Log.d("TAG", "onAttachedToWindow");
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isShowing = false;
		Log.d("TAG", "onDetachedFromWindow");
	}
}
