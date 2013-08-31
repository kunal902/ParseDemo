package com.iglulabs.assets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.iglulabs.parsedemo.R;

public class CustomFontTextView extends TextView {

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			return;
		}
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
		String fontName = styledAttrs.getString(R.styleable.TypefacedTextView_typeface);
		styledAttrs.recycle();
		if (fontName != null) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
			setTypeface(typeface);
		}
	}

	public CustomFontTextView(Context context, String fontName) {
		super(context, null);
		if (isInEditMode()) {
			return;
		}
		if (fontName != null) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
			setTypeface(typeface);
		}
	}
}
