package com.sgc.intermap.mainactivity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.Session;
import com.sgc.intermap.FacebookHelper;
import com.sgc.intermap.R;

public class SignInFragment extends Fragment {

	private static final String TAG = "SignInFragment";

	private Button _fbLoginButton;
	//private Button _basicLoginButton;
	private EditText _txtEmail;
	private EditText _txtPassword;

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
	    _fbLoginButton = (Button)view.findViewById(R.id.btnFacebookSignIn);
	    //_basicLoginButton = (Button)view.findViewById(R.id.btnBasicSignIn);
	    
	    _txtEmail = (EditText)view.findViewById(R.id.txtEmail);
	    _txtPassword = (EditText)view.findViewById(R.id.txtPassword);
	 
	    
	    //authBuOtton.setFragment(this);
	    // TODO:
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

	//-------------------------------------------------------------------------
	//
	protected String getEmail() {
		return _txtEmail.getText().toString();
	}

	//-------------------------------------------------------------------------
	//
	protected String getPassword() {
		return _txtPassword.getText().toString();
	}

	
}
