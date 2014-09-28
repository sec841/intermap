package com.sgc.intermap.webapi;

import com.facebook.SessionState;

public class WebApiSessionState {

	private SessionState _fbSessionState;
	
	public WebApiSessionState() 
	{
		
	}
	
	public void setFacebookSessionState( SessionState fbSessionState )
	{
		_fbSessionState = fbSessionState;
	}
	
	public boolean isOpened()
	{
		// TODO: Support more login types
		return _fbSessionState != null && 
				_fbSessionState.isOpened();
	}
	
	public boolean isClosed()
	{
		return _fbSessionState != null && 
				_fbSessionState.isClosed();
	}
	
}
