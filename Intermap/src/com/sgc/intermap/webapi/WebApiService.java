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

	//private boolean _isLoggedInPlatform;

	private WebApiServiceBinder _binder;

	private PersistentCookieStore _cookieStore;

	//private WebApiSession _session;
	private WebApiRestClient _client;

	public interface OnSignInStateChangeListener {
		
		public void onSignInStateChange(Throwable exception);
		
	}
	
	private OnSignInStateChangeListener _signInChangeListener;
	
	//-------------------------------------------------------------------------
	//
	public WebApiService() {
		super();
		
		_client = new WebApiRestClient( _cookieStore );
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
		_cookieStore = new PersistentCookieStore(this);
		return _binder;
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public boolean onUnbind(Intent intent) {
		_cookieStore = null;
		return super.onUnbind(intent);
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

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
	public void facebookLogin(Session fbSession) 
			throws UnsupportedEncodingException, JSONException {  
		// TODO: Handle these exceptions here.
		if(fbSession == null) {
			throw new AssertionError("Session must not be null."); 
			// TODO Throw PlatformLoginException ?
		}

		// TODO: WebApiLoginManager.getActiveSession();
		// TODO: Do not keep reference of session here.
		//_session = session;


		//if(_session.isOpened()) {
		//_session = session;

		String accessToken = fbSession.getAccessToken();
		//Log.i(TAG, "Access token: " + accessToken);

		JSONObject jsonParams = new JSONObject();
		jsonParams.put("access_token", accessToken);

		Context context = this.getApplicationContext();

		// TODO: Pick different endpoint based on different auth type.
		_client.post(context, "login/facebook", 
				jsonParams, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(
					int statusCode, 
					Header[] headers, 
					JSONObject response) {
				// If the response is JSONObject 
				// instead of expected JSONArray
				// [content-type: application/json; charset=utf-8, set-cookie: sid=Fe26.2**4de8c977a3c18a4be004c6fa4c80c66507a548372e49b2a86f7fb1911fcb14bb*lvM_gamvR1FDJN4TA4QoVA*kmaX-CGoCID7z6aguCIbhXJBmJeoZzBxZuOb1cCNk_Hvo67PZOMizwNPhEzOT-YbGDz_MU3hmwAw3XpBOdXLOL3PiDICNOwiW7MlCyIo_VIsuTlBjeAFzTSIwn9zJkDKz_IWJNpZVYzRKF8-U1utuA**1daee8db38f1932fd17b58a35f68ecf7c9f22b9b6e53585649c3201ccc3523ee*98LQ12ttV811Yer8bRaWDgfSDD2-NElk3LOfigAVBt8; HttpOnly; Path=/, cache-control: no-cache, content-encoding: gzip, vary: accept-encoding, Date: Sun, 28 Sep 2014 21:41:03 GMT, Connection: keep-alive, Transfer-Encoding: chunked]
				if(_signInChangeListener != null)
					_signInChangeListener.onSignInStateChange(null);

				Log.i(TAG, "response = " + response.toString());
				// TODO: Platform login callback.

			}

			//-------------------------------------------------------------
			//
			@Override
			public void onFailure(
					int statusCode,
					org.apache.http.Header[] headers,
					java.lang.Throwable throwable,
					org.json.JSONObject errorResponse)
			{
				if(_signInChangeListener != null)
					_signInChangeListener.onSignInStateChange(throwable);
				// errorResponse will be null if connection failed!
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

		});
		//}

	}

	/*
	//-------------------------------------------------------------------------
    // Always called when first connecting to the platform.
    // At this point, a session object should be opened.  This object
    // will be used as a "key" to get into the platform.
    public void loginPlatform(WebApiSession session) 
    		throws UnsupportedEncodingException, JSONException {  
    	// TODO: Handle these exceptions here.
    	if(session == null) {
    		throw new AssertionError("Session must not be null."); 
    		// TODO Throw PlatformLoginException ?
    	}

    	// TODO: WebApiLoginManager.getActiveSession();
    	// TODO: Do not keep reference of session here.
    	_session = session;

    	if(_session.isOpened()) {
    		_session = session;

    		String accessToken = _session.getAccessToken();
    		//Log.i(TAG, "Access token: " + accessToken);

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("access_token", accessToken);

            Context context = this.getApplicationContext();

            // TODO: Pick different endpoint based on different auth type.
            _client.post(context, "login/facebook", 
            		jsonParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(
                		int statusCode, 
                		Header[] headers, 
                		JSONObject response) {
                    // If the response is JSONObject 
                	// instead of expected JSONArray
                	// [content-type: application/json; charset=utf-8, set-cookie: sid=Fe26.2**4de8c977a3c18a4be004c6fa4c80c66507a548372e49b2a86f7fb1911fcb14bb*lvM_gamvR1FDJN4TA4QoVA*kmaX-CGoCID7z6aguCIbhXJBmJeoZzBxZuOb1cCNk_Hvo67PZOMizwNPhEzOT-YbGDz_MU3hmwAw3XpBOdXLOL3PiDICNOwiW7MlCyIo_VIsuTlBjeAFzTSIwn9zJkDKz_IWJNpZVYzRKF8-U1utuA**1daee8db38f1932fd17b58a35f68ecf7c9f22b9b6e53585649c3201ccc3523ee*98LQ12ttV811Yer8bRaWDgfSDD2-NElk3LOfigAVBt8; HttpOnly; Path=/, cache-control: no-cache, content-encoding: gzip, vary: accept-encoding, Date: Sun, 28 Sep 2014 21:41:03 GMT, Connection: keep-alive, Transfer-Encoding: chunked]
                	Log.i(TAG, "response = " + response.toString());
                	// TODO: Platform login callback.

                }

            	//-------------------------------------------------------------
            	//
                @Override
                public void onFailure(
                		int statusCode,
                        org.apache.http.Header[] headers,
                        java.lang.Throwable throwable,
                        org.json.JSONObject errorResponse)
                {
                	// errorResponse will be null if connection failed!
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

            });
        }

    }

	 */


}
