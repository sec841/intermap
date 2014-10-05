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

public class RegisterFragment extends Fragment {

	private static final String TAG = "RegisterFragment";
	
	private Button _fbLinkButton;

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
		_fbLinkButton = (Button)view.findViewById(R.id.btnLinkToFacebook);
		FacebookHelper.setFacebookButtonStyle(this.getActivity(), _fbLinkButton);
		
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

	//-------------------------------------------------------------------------
	//
	public Button getLinkToFacebookButton() {
		return _fbLinkButton;
	}


}
