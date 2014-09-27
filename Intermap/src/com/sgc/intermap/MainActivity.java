package com.sgc.intermap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sgc.intermap.WebApiLoginManager.OnSessionStateChangeListener;
import com.sgc.intermap.WebApiService.WebApiServiceBinder;

public class MainActivity extends FragmentActivity implements OnSessionStateChangeListener {
	
	private static final String TAG = "MainActivity";
	
	private static final int SPLASH = 0;
	private static final int LOADING = 1;
	
	//private static final int SELECTION = 1;
	
	//private static final int SETTINGS = 2;

	private static final int FRAGMENT_COUNT = LOADING +1;

	private Fragment[] _fragments = new Fragment[FRAGMENT_COUNT];
	
	private boolean _isResumed = false;
	
	private WebApiLoginManager _loginManager;
	
	private WebApiService _webService;
	private boolean _webServiceBound;
	
	
	//private MenuItem _settings;
	
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection _connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(
        		ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	WebApiServiceBinder binder = (WebApiServiceBinder)service;
            _webService = binder.getService();
            _webServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
        	_webServiceBound = false;
        	_webService = null;
        }

    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to WebApiService
        Intent intent = new Intent(this, WebApiService.class);
        bindService(intent, _connection, Context.BIND_AUTO_CREATE);
    }
	

	@Override
	public void onResume() {
	    super.onResume();
	    _loginManager.onResume();
	    _isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    _loginManager.onPause();
	    _isResumed = false;
	}
	
	// TODO: On stop?
	@Override
	public void onStop() {
	    super.onStop();
	    _loginManager.onStop();
	    
        // Unbind from the service
        if (_webServiceBound) {
            unbindService(_connection);
            _webServiceBound = false;
        }
	}
	
	/*
	public WebApiLoginManager getLoginManager() {
		return _loginManager;
	}
	*/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    _loginManager = new WebApiLoginManager(this, this);
	    _loginManager.onCreate(savedInstanceState);
	    
	    //_fbUiHelper = new UiLifecycleHelper(this, _fbSessionStatusCb);
	    //_fbUiHelper.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_main);

	    FragmentManager fm = getSupportFragmentManager();
	    _fragments[SPLASH] = fm.findFragmentById(R.id.fragment_splash);
	    _fragments[LOADING] = fm.findFragmentById(R.id.fragment_loading);
	    //_fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
	    //_fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = 0; i < _fragments.length; i++) {
	        transaction.hide(_fragments[i]);
	    }
	    transaction.commit();
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < _fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(_fragments[i]);
	        } else {
	            transaction.hide(_fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
	}
	
	@Override
	public void onSessionStateChange(WebApiSession session) 
	{
	    // Only make changes if the activity is visible
	    if (_isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        
	        if(!isOnline()) {
	        	String msg = "No network connectivity available.";
	        	Log.e(TAG, msg);
	        	// TODO showFragment(NOCONNECTION, false);
	        	showFragment(SPLASH, false);
				// TODO: Use resource.
				Toast.makeText(this.getApplicationContext(), 
						msg, Toast.LENGTH_SHORT).show();
	        }
	        else if (_webService != null && 
	        		_webService.isLoggedInPlatform()) { // TODO: session.isPlatformConnected()
	        	// Show main application screen.
	        	showFragment(LOADING, false);
	        }
	        else if (session.isOpened()) {
	            // We are now connected to Facebook (or other).
	        	// Upload the facebook token to the platform.
	        	if (!_webServiceBound) 
	        		throw new AssertionError("WebApiService must be bound to MainActivity."); // TODO: use resource
	        	
	        	// If the session state is open, we can now log into the platform.
	        	try {
					_webService.loginPlatform(session);
					showFragment(LOADING, false);
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
					showFragment(SPLASH, false);
					// TODO: Use resource.
					Toast.makeText(this.getApplicationContext(), 
							"Login failed.", Toast.LENGTH_SHORT).show();
				}	        	
	            
	        } else if (session.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(SPLASH, false);
	        }
	    }
	}
	
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    
	    WebApiSession session = _loginManager.getActiveSession();
	    
	    
	    if (session != null && session.isOpened()) {
	    	// session.isOpened()
	    	// if the session is already open,
	        // try to show the selection fragment
	    	// TODO: Find which fragment to show?
	        showFragment(LOADING, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	        showFragment(SPLASH, false);
	    }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    _loginManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    _loginManager.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    _loginManager.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // only add the menu when the selection fragment is showing
	    /*
		if (_fragments[SELECTION].isVisible()) {
	        if (menu.size() == 0) {
	            _settings = menu.add(R.string.settings);
	        }
	        return true;
	    } else {
	        menu.clear();
	        _settings = null;
	    }
	    */
	    //return false;
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    /*
		if (item.equals(_settings)) {
	        showFragment(SETTINGS, true);
	        return true;
	    }*/
	    return false;
	}
	
	public void onClickFacebookLogin(View v) {
		_loginManager.loginFacebook();
	}
	
	private boolean isOnline() {
	    ConnectivityManager connMgr = (ConnectivityManager) 
	            getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return networkInfo != null && networkInfo.isConnected();
	}
	
}
