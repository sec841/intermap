package com.sgc.intermap.webapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 Login helper.  
 Equivalent to Facebook's UiLifecycleHelper but works with all 
 other supported login schemes.
 
 We should have one of these per activity.
*/
public class WebApiLoginManager {
	
	private OnSessionStateChangeListener _sessionStateChangeListener;
	
	
	private UiLifecycleHelper _fbUiHelper;
	
	// The current webapi active session.
	private WebApiSession _session; // TODO: session map?
	public Activity _activity;
	
	//-------------------------------------------------------------------------
	//
	// TODO: FacebookLoginManager subclass?
	// TODO: No longer use facebook stuff in the arguments of onSessionStateChange.
    public interface OnSessionStateChangeListener {
        public void onSessionStateChange(WebApiSession session);
    }
    
	//-------------------------------------------------------------------------
	//
    public WebApiSession getActiveSession() {
    	return _session;
    }

	//-------------------------------------------------------------------------
	//
    // TODO: only create this if facebook login
	private Session.StatusCallback _fbSessionStatusCb = 
		    new Session.StatusCallback() {
		    @Override
		    public void call(Session fbSession, 
		            SessionState fbSessionState, 
		            Exception exception) {
		    	// TODO: Check exception value.
		    	// TODO: Transform Facebook session object into our own session object 
		    	//WebApiSession session = new WebApiSession(fbSession);	    	
		    	//WebApiSession sessionState = new WebApiSessionState(fbSessionState);
		    	_session.setFacebookSession(fbSession);
		    	//_session.setFacebookSessionState(fbSessionState);
		    	
		    	_sessionStateChangeListener.onSessionStateChange(
		    			_session );
		    }
	};

	//-------------------------------------------------------------------------
	//
	// TODO: Transform this class into an invisible fragment?
    public WebApiLoginManager(
    		OnSessionStateChangeListener listener, 
    		Activity activity) {
    	_sessionStateChangeListener = listener;
    	_session = new WebApiSession();
    	_activity = activity;
    	_fbUiHelper = null;
    }
	
	//-------------------------------------------------------------------------
	//
    public void loginFacebook() {
    	// start Facebook login workflow
    	_fbUiHelper = new UiLifecycleHelper(_activity, _fbSessionStatusCb);
    	Session.openActiveSession(_activity, true, _fbSessionStatusCb);
    }
    
	//-------------------------------------------------------------------------
	//
    // Log out from all active sessions
    public void closeSessions() {
    	if(_session != null && _session.isOpened()) {
    		_session.close();
    		// TODO: _fbUiHelper = null; ?  _session = null ?
    	}
    }
	
	//-------------------------------------------------------------------------
	//
	public void onCreate(Bundle savedInstanceState) {
		if(_fbUiHelper != null)
			_fbUiHelper.onCreate(savedInstanceState);
	}
	
	//-------------------------------------------------------------------------
	//
	public void onResume() {
		if(_fbUiHelper != null)
			_fbUiHelper.onResume();
	}
	
	//-------------------------------------------------------------------------
	//
	public void onPause() {
		if(_fbUiHelper != null)
			_fbUiHelper.onPause();
	}
	
	//-------------------------------------------------------------------------
	//
	public void onStop() {
		if(_fbUiHelper != null)
			_fbUiHelper.onStop();
	}
	
	//-------------------------------------------------------------------------
	//
	public void onActivityResult(
			int requestCode, int resultCode, Intent data) {
		//_session.onActivityResult(_activity, requestCode, resultCode, data);
		if(_fbUiHelper != null)
			_fbUiHelper.onActivityResult(requestCode, resultCode, data);
	}

	//-------------------------------------------------------------------------
	//
	public void onDestroy() {
		if(_fbUiHelper != null)
			_fbUiHelper.onDestroy();
	}

	//-------------------------------------------------------------------------
	//
	public void onSaveInstanceState(Bundle outState) {
		if(_fbUiHelper != null)
			_fbUiHelper.onSaveInstanceState(outState);
	}
	
}
