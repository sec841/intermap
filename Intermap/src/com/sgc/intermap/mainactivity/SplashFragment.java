package com.sgc.intermap.mainactivity;

import com.sgc.intermap.R;
import com.sgc.intermap.R.dimen;
import com.sgc.intermap.R.drawable;
import com.sgc.intermap.R.id;
import com.sgc.intermap.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SplashFragment extends Fragment {

	private static final String TAG = "SplashFragment";
	

	//-------------------------------------------------------------------------
	//
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_splash, 
	            container, false);
	    

	    
	    return view;
	}
	
	//-------------------------------------------------------------------------
	//
	@Override
	public void onResume() {
		super.onResume();
	}

}
