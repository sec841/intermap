package com.sgc.intermap.webapi;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.facebook.Session;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

public class WebApiService extends Service {

	private static final String TAG = "WebApiService";

	private WebApiServiceBinder _binder;

	private PersistentCookieStore _cookieStore;

	//private WebApiSession _session;
	private WebApiRestClient _client;

	public interface OnSignInStateChangeListener {
		
		public void onSignInStateChange(
				JSONObject response, Throwable exception);
		
	}
	
	private OnSignInStateChangeListener _signInChangeListener;
	
	//-------------------------------------------------------------------------
	//
	public WebApiService() {
		super();
		
		_client = new WebApiRestClient();
		_binder = new WebApiServiceBinder();
	}
	
	//-------------------------------------------------------------------------
	//
	public void setSignInStateChangeListener(
			OnSignInStateChangeListener listener) {
		_signInChangeListener = listener;
		
	}
	
	//-------------------------------------------------------------------------
	//
	@Override
	public IBinder onBind(Intent intent) {
		Context context = this.getApplicationContext();
		_cookieStore = new PersistentCookieStore(context);
		_client.setCookieStore(_cookieStore);
		return _binder;
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public boolean onUnbind(Intent intent) {
		_cookieStore = null;
		_client.setCookieStore(null);
		return super.onUnbind(intent);
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	//-------------------------------------------------------------------------
	// Returns true if we have a non-expired / valid cookie that can be used
	// to authenticate calls to the REST API.
	public boolean isLoggedIn() {
		List<Cookie> cookies = _cookieStore.getCookies();
		for (Cookie c : cookies) {
			if (c.getName().equals("sid") && !c.isExpired(new Date())) {
				return true;
			}
		}
		return false;
	}

	//-------------------------------------------------------------------------
	//
	public void logOut() {
		// TODO: Make rest call to /logout

		_cookieStore.clear();
	}

	//-------------------------------------------------------------------------
	//
	// Test request to validate if we are authenticated.
	// TODO: Move this to unit test class.
	private void testRequest() {
		this._cookieStore.clear();
		@SuppressWarnings("unused")
		boolean loggedIn = this.isLoggedIn();
		Context context = this.getApplicationContext();
		
		_client.get(context, "test", null, new JsonHttpResponseHandler() { 
			@Override
			public void onSuccess(int statusCode, 
					Header[] headers, 
					JSONObject response) {
				Log.i(TAG, "test response = " + response);
			}
			

			@Override
			public void onFailure(
				int statusCode,
				Header[] headers,
				Throwable throwable,
				JSONObject errorResponse) {
				Log.i(TAG, "test error response = " + errorResponse);
			}
		});
	}
	
	//-------------------------------------------------------------------------
	//
	public void basicLogin(String email, String password) 
			throws UnsupportedEncodingException, JSONException {  
		// TODO: Handle these exceptions here.
		if(email == null || password == null) {
			throw new AssertionError("Email or password must not be null."); 
		}

		JSONObject jsonParams = new JSONObject();
		jsonParams.put("email", email);
		jsonParams.put("password", password);
		
		Context context = this.getApplicationContext();
		
		// TODO: Pick different endpoint based on different auth type.		
		_client.post(context, "login", 
				jsonParams, new LoginResponseHandler());

	}
	
	//-------------------------------------------------------------------------
	//
	public void facebookLogin(Session fbSession) 
			throws UnsupportedEncodingException, JSONException {  
		// TODO: Handle these exceptions here.
		if(fbSession == null) {
			throw new AssertionError("Session must not be null."); 
			// TODO Throw PlatformLoginException ?
		}

		String accessToken = fbSession.getAccessToken();
		//Log.i(TAG, "Access token: " + accessToken);

		JSONObject jsonParams = new JSONObject();
		jsonParams.put("access_token", accessToken);

		Context context = this.getApplicationContext();
		
		// TODO: Pick different endpoint based on different auth type.		
		_client.post(context, "login/facebook", 
				jsonParams, new LoginResponseHandler() );
	}
	
	//-------------------------------------------------------------------------
	// Nested classes and interfaces.
	//-------------------------------------------------------------------------
	
	//-------------------------------------------------------------------------
	//
	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class WebApiServiceBinder extends Binder {
		public WebApiService getService() {
			// Return this instance of LocalService so clients can call public methods
			return WebApiService.this;
		}
	}
	
	//-------------------------------------------------------------------------
	//
	private class LoginResponseHandler extends JsonHttpResponseHandler {

		//-------------------------------------------------------------
		//
		@Override
		public void onSuccess(
				int statusCode, 
				Header[] headers, 
				JSONObject response) {

			if(_signInChangeListener != null)
				_signInChangeListener.onSignInStateChange(response, null);

			Log.i(TAG, "response = " + response.toString());
		}

		//-------------------------------------------------------------
		//
		@Override
		public void onFailure(
				int statusCode,
				Header[] headers,
				Throwable throwable,
				JSONObject errorResponse) {
			
			if(_signInChangeListener != null)
				_signInChangeListener.onSignInStateChange(
						errorResponse, throwable);
			// TODO: errorResponse will be null if connection failed!
			Log.e(TAG, "Connection failed: " + throwable.toString());
		}

		//-------------------------------------------------------------
		//
		@Override
		public void onRetry(int retryNo) {
			// TODO: How to retry?
			Log.w(TAG, "Retrying... (" + retryNo + ")");
			// called when request is retried
		}
		
	}


}
