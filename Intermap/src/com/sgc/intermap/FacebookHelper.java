package com.sgc.intermap;

import android.content.Context;
import android.widget.Button;

public class FacebookHelper {
	
	public static void setFacebookButtonStyle(Context context, Button button) {
		button.setBackgroundResource(
	    		R.drawable.com_facebook_button_blue);
		button.setCompoundDrawablesWithIntrinsicBounds(
	    		R.drawable.com_facebook_inverse_icon, 0, 0, 0);
		button.setCompoundDrawablePadding(
				context.getResources().getDimensionPixelSize(
                R.dimen.com_facebook_loginview_compound_drawable_padding));
		button.setPadding(
				context.getResources().getDimensionPixelSize(
	    		R.dimen.com_facebook_loginview_padding_left),
	    		context.getResources().getDimensionPixelSize(
                R.dimen.com_facebook_loginview_padding_top),
                context.getResources().getDimensionPixelSize(
                R.dimen.com_facebook_loginview_padding_right),
                context.getResources().getDimensionPixelSize(
                R.dimen.com_facebook_loginview_padding_bottom));
	    
	}

}
