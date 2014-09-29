package com.sgc.intermap.mainactivity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sgc.intermap.FacebookHelper;
import com.sgc.intermap.R;

public class SignInFragment extends Fragment {

	private static final String TAG = "SignInFragment";

	private Button _fbLoginButton;

	//-------------------------------------------------------------------------
	//
	@Override
	public View onCreateView(
			LayoutInflater inflater, 
			ViewGroup container, 
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_signin, 
				container, false);	   
	    // TODO: Maybe this should be a singleton instead!
	    //_loginManager = ((MainActivity)getActivity()).getLoginManager();
		
		setHasOptionsMenu(true);
	    _fbLoginButton = (Button)view.findViewById(R.id.btnFacebookLogin);
	    //authBuOtton.setFragment(this);
	    
	    //Log.i(TAG, "Asking for Facebook permissions...");
	    
	    // Ask for permissions.
	    //_fbAuthButton.setReadPermissions(
	    //		Arrays.asList("user_interests"));
	    
	    FacebookHelper.setFacebookButtonStyle(this.getActivity(), _fbLoginButton);

	    
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
	    inflater.inflate(R.menu.menu_fragment_signin, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}

}
