package com.iglulabs.parsedemo;

import android.graphics.Canvas;
import android.graphics.Paint;

public class NeighbouringPoint extends Point {

	private final Point neighbour;

	public NeighbouringPoint(final float x, final float y, final int col,
			final Point neighbour, final int width) {
		super(x, y, col, width);
		this.neighbour = neighbour;
	}

	@Override
	public void draw(final Canvas canvas, final Paint paint) {
		paint.setColor(col);
		paint.setStrokeWidth(width);
		canvas.drawLine(x, y, neighbour.x, neighbour.y, paint);
		canvas.drawCircle(x, y, width / 2, paint);
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + col + "; N[" + neighbour.toString() + "]";
	}
}
