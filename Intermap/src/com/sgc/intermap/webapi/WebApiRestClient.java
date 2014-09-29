package com.sgc.intermap.webapi;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.*;


public final class WebApiRestClient {

	// TODO: Make this configurable.
	private static final String BASE_URL = "http://192.168.0.14:8080/";

	private static String CONTENT_TYPE = "application/json";
	private AsyncHttpClient _httpClient;

	private PersistentCookieStore _cookieStore;
	
	//-------------------------------------------------------------------------
	//
	public WebApiRestClient(PersistentCookieStore cookieStore) {
		_httpClient = new AsyncHttpClient();
		_httpClient.addHeader("content-type", "application/json"); 
		_cookieStore = cookieStore;
		
		_httpClient.setCookieStore( _cookieStore );
	}
	
	//-------------------------------------------------------------------------
	//
	public void get(
			Context context,
			String relativeUrl, 
			RequestParams params, 
			AsyncHttpResponseHandler responseHandler) {
		_httpClient.get(context, getAbsoluteUrl(relativeUrl), 
				params, responseHandler);
	}

	//-------------------------------------------------------------------------
	//
	public void post(
			Context context,
			String relativeUrl, 
			JSONObject jsonParams,  
			AsyncHttpResponseHandler responseHandler) 
					throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(jsonParams.toString());
		_httpClient.post(
				context, getAbsoluteUrl(relativeUrl), 
				entity, CONTENT_TYPE, responseHandler); 
	}

	//-------------------------------------------------------------------------
	//
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

}
