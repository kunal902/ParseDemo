package com.iglulabs.parsedemo;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class PaintView extends ImageView implements OnTouchListener {
	private List<Point> points = new ArrayList<Point>();
	private Paint paint = new Paint();
	private int col_mode;
	private int wid_mode;

	public PaintView(Context context) {
		this(context, null);
	}

	public PaintView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			Drawable drawable = getDrawable();
			if (drawable == null) {
				setMeasuredDimension(0, 0);
			} else {
				float imageSideRatio = (float) drawable.getIntrinsicWidth()
						/ (float) drawable.getIntrinsicHeight();
				float viewSideRatio = (float) MeasureSpec
						.getSize(widthMeasureSpec)
						/ (float) MeasureSpec.getSize(heightMeasureSpec);
				if (imageSideRatio >= viewSideRatio) {
					int width = MeasureSpec.getSize(widthMeasureSpec);
					int height = (int) (width / imageSideRatio);
					setMeasuredDimension(width, height);
				} else {
					int height = MeasureSpec.getSize(heightMeasureSpec);
					int width = (int) (height * imageSideRatio);
					setMeasuredDimension(width, height);
				}
			}
		} catch (Exception e) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	protected void init(AttributeSet attrs) {
		col_mode = 0;
		wid_mode = 10;
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		paint.setAntiAlias(true);
	}

	public void clearPoints() {
		points.clear();
		invalidate();
	}

	public void changeColour(int col_in) {
		col_mode = col_in;
	}

	public void changeWidth(int wid_in) {
		wid_mode = wid_in;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (Point point : points) {
			point.draw(canvas, paint);
		}
	}

	public boolean onTouch(View arg0, MotionEvent event) {
		int new_col = 0;
		if (col_mode >= 0) {
			switch (col_mode) {
			case 0: {
				new_col = Color.WHITE;
				break;
			}
			case 1: {
				new_col = Color.BLUE;
				break;
			}
			case 2: {
				new_col = Color.GREEN;
				break;
			}
			case 3: {
				new_col = Color.RED;
				break;
			}
			case 4: {
				new_col = Color.BLACK;
				break;
			}
			}
		}
		Point point;
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			point = new NeighbouringPoint(event.getX(), event.getY(), new_col,
					points.get(points.size() - 1), wid_mode);
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			point = new Point(event.getX(), event.getY(), new_col, wid_mode);
		} else {
			return false;
		}
		points.add(point);
		invalidate();
		return true;
	}

}
