package com.sgc.intermap.mainactivity;


import com.sgc.intermap.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class LoadingFragment extends Fragment {

	private static final String TAG = "LoadingFragment";

	private ProgressBar _progressSpinner;
	
	//-------------------------------------------------------------------------
	//
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_loading, 
	            container, false);
	    
	    _progressSpinner = (ProgressBar)view.findViewById(R.id.progressBarSpinner);
	    
	    //spinner.setVisibility(View.GONE);
	    _progressSpinner.setVisibility(View.VISIBLE);
	    
	    return view;
	}
	
}
