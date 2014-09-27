package com.sgc.intermap;

import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class SelectionFragment extends Fragment {

	private static final String TAG = "SelectionFragment";



	@Override
	public View onCreateView(
			LayoutInflater inflater, 
			ViewGroup container, 
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_selection, 
				container, false);	   
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		/*

		Session session = Session.getActiveSession();
		if(session.isOpened())
		{
			// make the API call 
			new Request(
					session,
					"/me/interests",
					null,
					HttpMethod.GET,
					new Request.Callback() {
						public void onCompleted(Response response) {
							// handle the result 
							GraphObject obj = response.getGraphObject();
							
							Map<String, Object> map = obj.asMap();
							
							for(String key : map.keySet())
							{
								Object value = map.get(key);
								if(value instanceof String)
								{
									Log.i(TAG, "key: "+key + " value: "+(String)value);
								}
								else
								{
									Log.i(TAG, "key: "+key);
								}
							}
							
							// TODO not sure
							GraphObjectList<GraphObject> list = 
									response.getGraphObjectList();

							if( list != null )
							{
								for(GraphObject graphObject : list)
								{
									String about = (String)graphObject.getProperty("about");
									String category = (String)graphObject.getProperty("category");
									//CoverPhoto photo = (CoverPhoto)graphObject.getProperty("coverO");
								}
							}
						}
					}
					).executeAndWait();

		}
		
		*/
	}


}
