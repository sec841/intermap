package com.sgc.intermap.webapi;


import com.facebook.Session;

public class WebApiSession {
	
	// TODO: Do we really need other class for state?
	private WebApiSessionState _state;
	
	// Facebook session (if any).
	private Session _fbSession;
	
	//-------------------------------------------------------------------------
	//
	public WebApiSession() 
	{
		_fbSession = Session.getActiveSession();
		_state = new WebApiSessionState();
		
		if(_fbSession != null) {
			_state.setFacebookSessionState(_fbSession.getState());
		}
	}
	
	//-------------------------------------------------------------------------
	//
	public boolean isOpened()
	{
		return _state.isOpened();
	}
	
	//-------------------------------------------------------------------------
	//
	public boolean isClosed()
	{
		return _state.isClosed();
	}
	
	//-------------------------------------------------------------------------
	//
	public WebApiSessionState getState()
	{
		return _state;
	}
	
	//-------------------------------------------------------------------------
	//
	public void setFacebookSession(Session fbSession)
	{
		_fbSession = fbSession;
		_state.setFacebookSessionState( fbSession.getState() );
	}
	
	//-------------------------------------------------------------------------
	//
	public String getAccessToken() {
		if( _fbSession != null ) 
			return _fbSession.getAccessToken();
		return null;
	}

	/*
    //-------------------------------------------------------------------------
	//
	public void onActivityResult(
			Activity currentActivity, 
			int requestCode, 
			int resultCode, 
			Intent data) {
		if(_fbSession != null) {
			_fbSession.onActivityResult(
					currentActivity, requestCode, resultCode, data);
		}
	}
	*/
	
	//-------------------------------------------------------------------------
	//
	public void close() {
		_fbSession.closeAndClearTokenInformation();
		_fbSession = null;
	}
	
	/*
	//-------------------------------------------------------------------------
	//
	public void setFacebookSessionState(SessionState fbSessionState)
	{
		_state.setFacebookSessionState( fbSessionState );
	}
	*/
}
