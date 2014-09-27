package com.sgc.intermap;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

public class WebApiService extends Service {
	
	private static final String TAG = "WebApiService";
	
	private boolean _isLoggedInPlatform;
	
	private WebApiServiceBinder _binder;
	
	private WebApiSession _session;
	private WebApiRestClient _client;
	
	public WebApiService() {
		super();
		_client = new WebApiRestClient();
		_binder = new WebApiServiceBinder();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return _binder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
	    return super.onUnbind(intent);
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
     public class WebApiServiceBinder extends Binder {
    	WebApiService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WebApiService.this;
        }
    }
    
    public boolean isLoggedInPlatform() {
    	return _isLoggedInPlatform;
    }
    
    /** method for clients */
    public int getRandomNumber() {
      return 10;
    }
    

    
    // Always called when first connecting to the platform.
    // At this point, a session object should be opened.  This object
    // will be used as a "key" to get into the platform.
    public void loginPlatform(WebApiSession session) 
    		throws UnsupportedEncodingException, JSONException {  // TODO: Handle these exceptions here.
    	if(session == null) {
    		throw new AssertionError("Session must not be null."); // TODO Throw PlatformLoginException ?
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
            _client.post(context, "login/facebook", jsonParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(
                		int statusCode, 
                		Header[] headers, 
                		JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                	Log.i(TAG, "response = " + response.toString());
                	// TODO: Platform login callback.
                	
                }
                
                @Override
                public void onFailure(
                		int statusCode,
                        org.apache.http.Header[] headers,
                        java.lang.Throwable throwable,
                        org.json.JSONObject errorResponse)
                {
                	Log.e(TAG, "Connection failed: " + throwable.toString());
                }
                
                @Override
                public void onRetry(int retryNo) {
                	// TODO: How to retry?
                	Log.w(TAG, "Retrying... (" + retryNo + ")");
                	// called when request is retried
            	}

            });
        }

    }
    

}
