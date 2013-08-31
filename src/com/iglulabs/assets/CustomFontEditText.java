package com.iglulabs.assets;

import com.iglulabs.parsedemo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;


public class CustomFontEditText extends EditText {

	public CustomFontEditText(Context context, AttributeSet attrs) {
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
}

