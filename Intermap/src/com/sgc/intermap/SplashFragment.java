package com.sgc.intermap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SplashFragment extends Fragment {

	private static final String TAG = "SplashFragment";
	
	private Button _fbLoginButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_splash, 
	            container, false);
	    
	    // TODO: Maybe this should be a singleton instead!
	    //_loginManager = ((MainActivity)getActivity()).getLoginManager();
	    
	    _fbLoginButton = (Button)view.findViewById(R.id.btnFacebookLogin);
	    //authBuOtton.setFragment(this);
	    
	    //Log.i(TAG, "Asking for Facebook permissions...");
	    
	    // Ask for permissions.
	    //_fbAuthButton.setReadPermissions(
	    //		Arrays.asList("user_interests"));
	    
	    _fbLoginButton.setBackgroundResource(
	    		R.drawable.com_facebook_button_blue);
	    _fbLoginButton.setCompoundDrawablesWithIntrinsicBounds(
	    		R.drawable.com_facebook_inverse_icon, 0, 0, 0);
	    _fbLoginButton.setCompoundDrawablePadding(
                getResources().getDimensionPixelSize(
                		R.dimen.com_facebook_loginview_compound_drawable_padding));
	    _fbLoginButton.setPadding(getResources().getDimensionPixelSize(
	    		R.dimen.com_facebook_loginview_padding_left),
                getResources().getDimensionPixelSize(
                		R.dimen.com_facebook_loginview_padding_top),
                getResources().getDimensionPixelSize(
                		R.dimen.com_facebook_loginview_padding_right),
                getResources().getDimensionPixelSize(
                		R.dimen.com_facebook_loginview_padding_bottom));
	    
	    return view;
	}
		
}
