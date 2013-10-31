package com.majestyk.buzr;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

public class FontManager {
	private static boolean USE_FONT = true;
	private static Typeface font;
	
	public static void loadFont(AssetManager assets) {
		font = Typeface.createFromAsset(assets, "fonts/Lato-Reg.ttf");
	}
	
	private static Typeface getFont() {
		return font;
	}
	
	public static void setTypeFace(TextView textView) {
		if(USE_FONT) {
			textView.setTypeface(getFont());
		}
	}

	public static void setTextColor(TextView textView) {
		TextView newTextView = textView;
	    Shader textShader=new LinearGradient(0, 0, 0, 20,
	            new int[] {Color.WHITE, Color.GRAY},
	            new float[] {0, 1}, TileMode.CLAMP);
	    newTextView.getPaint().setShader(textShader);
	}
	
	public static void setTypeFace(Button button) {
		if(USE_FONT) {
			button.setTypeface(getFont());
		}
	}
	
	public static void setTextColor(Button button) {
		Button newButton = button;
	    Shader textShader=new LinearGradient(0, 0, 0, 20,
	            new int[] {Color.WHITE, Color.GRAY},
	            new float[] {0, 1}, TileMode.CLAMP);
	    newButton.getPaint().setShader(textShader);
	}
	
	public static void setTypeFace(CompoundButton cb) {
		if(USE_FONT) {
			cb.setTypeface(getFont());
		}
	}

}