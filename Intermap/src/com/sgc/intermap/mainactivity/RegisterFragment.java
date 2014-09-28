package com.sgc.intermap.mainactivity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sgc.intermap.R;

public class RegisterFragment extends Fragment {

	private static final String TAG = "RegisterFragment";
	
	private Button _fbRegisterButton;

	//-------------------------------------------------------------------------
	//
	@Override
	public View onCreateView(
			LayoutInflater inflater, 
			ViewGroup container, 
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_register, 
				container, false);	   

		setHasOptionsMenu(true);
		//btnFacebookRegister
		
		_fbRegisterButton = (Button)view.findViewById(R.id.btnFacebookRegister);
		
	    // TODO: Put this in FacebookHelper
		_fbRegisterButton.setBackgroundResource(
	    		R.drawable.com_facebook_button_blue);
		_fbRegisterButton.setCompoundDrawablesWithIntrinsicBounds(
	    		R.drawable.com_facebook_inverse_icon, 0, 0, 0);
		_fbRegisterButton.setCompoundDrawablePadding(
                getResources().getDimensionPixelSize(
                		R.dimen.com_facebook_loginview_compound_drawable_padding));
		_fbRegisterButton.setPadding(getResources().getDimensionPixelSize(
	    		R.dimen.com_facebook_loginview_padding_left),
                getResources().getDimensionPixelSize(
                		R.dimen.com_facebook_loginview_padding_top),
                getResources().getDimensionPixelSize(
                		R.dimen.com_facebook_loginview_padding_right),
                getResources().getDimensionPixelSize(
                		R.dimen.com_facebook_loginview_padding_bottom));
		
		return view;
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public void onPause() {
		super.onPause();
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public void onResume() {
		super.onResume();
		
	}
	
	//-------------------------------------------------------------------------
	//
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.menu_fragment_register, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}


}
