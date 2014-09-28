package com.sgc.intermap.mainactivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sgc.intermap.R;

import com.sgc.intermap.webapi.WebApiLoginManager;
import com.sgc.intermap.webapi.WebApiService;
import com.sgc.intermap.webapi.WebApiSession;
import com.sgc.intermap.webapi.WebApiLoginManager.OnSessionStateChangeListener;
import com.sgc.intermap.webapi.WebApiService.WebApiServiceBinder;

public class MainActivity extends ActionBarActivity 
implements OnSessionStateChangeListener {
	
	private static final String TAG = "MainActivity";
	
	private static final int FRAGMENT_INDEX_SPLASH = 0;
	private static final int FRAGMENT_INDEX_LOADING = 1;
	private static final int FRAGMENT_INDEX_SIGN_IN = 2;
	private static final int FRAGMENT_INDEX_REGISTER = 3;

	private static final int FRAGMENT_COUNT = FRAGMENT_INDEX_REGISTER + 1;

	private Fragment[] _fragments = new Fragment[FRAGMENT_COUNT];
	
	//private int _currentFragment;
	
	private boolean _isResumed = false;
	
	private WebApiLoginManager _loginManager;
	
	private WebApiService _webService;
	private boolean _webServiceBound;
	
	//-------------------------------------------------------------------------
	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);  // TODO: Restore _currentFragment
	    
	    _loginManager = new WebApiLoginManager(this, this);
	    _loginManager.onCreate(savedInstanceState);
	    
	    //_fbUiHelper = new UiLifecycleHelper(this, _fbSessionStatusCb);
	    //_fbUiHelper.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_main);
	    
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("");
	    
	    FragmentManager fm = getSupportFragmentManager();
	    
	    fm.addOnBackStackChangedListener(
	    		new FragmentManager.OnBackStackChangedListener() {
	                public void onBackStackChanged() {
	                	updateActionBar();
	                }
	            });
	    
	    _fragments[FRAGMENT_INDEX_SPLASH] = 
	    		fm.findFragmentById(R.id.fragment_splash);
	    _fragments[FRAGMENT_INDEX_LOADING] = 
	    		fm.findFragmentById(R.id.fragment_loading);
	    _fragments[FRAGMENT_INDEX_SIGN_IN] = 
	    		fm.findFragmentById(R.id.fragment_signin);
	    _fragments[FRAGMENT_INDEX_REGISTER] = 
	    		fm.findFragmentById(R.id.fragment_register);

	    /*
	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = `; i < _fragments.length; i++) {
	        transaction.hide(_fragments[i]);
	    }
	    transaction.commit();
	    */
	    // TODO: Remember last visible fragment index in bundle.
	    showFragment(FRAGMENT_INDEX_SPLASH, false);
	}
	
	//-------------------------------------------------------------------------
	//
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection _connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(
        		ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the 
        	// IBinder and get LocalService instance
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

	//-------------------------------------------------------------------------
	//
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to WebApiService
        Intent intent = new Intent(this, WebApiService.class);
        bindService(intent, _connection, Context.BIND_AUTO_CREATE);
    }
	

	//-------------------------------------------------------------------------
	//
	@Override
	public void onResume() {
	    super.onResume();
	    _loginManager.onResume();
	    _isResumed = true;
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public void onPause() {
	    super.onPause();
	    _loginManager.onPause();
	    _isResumed = false;
	}
	
	//-------------------------------------------------------------------------
	//
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
	

	
	//-------------------------------------------------------------------------
	//
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
	
	//-------------------------------------------------------------------------
	//
	private void updateActionBar()
	{	    
	    // Set action bar title and visibility depending
	    // on selected fragment.
        ActionBar actionBar = getSupportActionBar();
		boolean isVisible = true;
		
		if( !_fragments[FRAGMENT_INDEX_SPLASH].isHidden() ) {
	    	// No action bar in splash screen.
	    	isVisible = false;
	    	
		}
		/*else if( !_fragments[FRAGMENT_INDEX_SIGN_IN].isHidden() ) {
			actionBar.setTitle(R.string.com_sgc_signin_actionbar_title);
		}
		else if( !_fragments[FRAGMENT_INDEX_REGISTER].isHidden() ) {
			actionBar.setTitle(R.string.com_sgc_register_actionbar_title);
		}*/

	    if(isVisible) {
	    	if(!actionBar.isShowing()) {
	    		actionBar.show();
	    	}
	    }
	    else if(actionBar.isShowing()) {
	    	actionBar.hide();
	    }
	    
	}
	
	//-------------------------------------------------------------------------
	//
	@Override
	public void onSessionStateChange(WebApiSession session) 
	{
	    // Only make changes if the activity is visible
	    if (_isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        
	        // Get the number of entries in the back stack
	        // TODO: Is this required???
	        int backStackSize = manager.getBackStackEntryCount();
	        
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        
	        if(!isOnline()) {
	        	String msg = "No network connectivity available.";
	        	Log.e(TAG, msg);
	        	// TODO showFragment(NOCONNECTION, false);
	        	showFragment(FRAGMENT_INDEX_SPLASH, false);
				// TODO: Use resource.
				Toast.makeText(this.getApplicationContext(), 
						msg, Toast.LENGTH_SHORT).show();
	        }
	        else if (_webService != null && 
	        		_webService.isLoggedInPlatform()) { 
	        	// TODO: session.isPlatformConnected()
	        	// Show main application screen.
	        	showFragment(FRAGMENT_INDEX_LOADING, false);
	        }
	        else if (session.isOpened()) {
	            // We are now connected to Facebook (or other).
	        	// Upload the facebook token to the platform.
	        	if (!_webServiceBound) 
	        		// TODO: use resource
	        		throw new AssertionError(
	        				"WebApiService must be bound to MainActivity."); 
	        	
	        	// If the session state is open, we can now 
	        	// log into the platform.
	        	try {
					_webService.loginPlatform(session);
					showFragment(FRAGMENT_INDEX_LOADING, false);
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
					showFragment(FRAGMENT_INDEX_SPLASH, false);
					// TODO: Use resource.
					Toast.makeText(this.getApplicationContext(), 
							"Login failed.", Toast.LENGTH_SHORT).show();
				}	        	
	            
	        } else if (session.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(FRAGMENT_INDEX_SPLASH, false);
	        }
	    }
	}
	
	//-------------------------------------------------------------------------
	// This is the fragment-orientated version of onResume() that you can 
	// override to perform operations in the Activity at the same point where 
	// its fragments are resumed. Be sure to always call through to 
	// the super-class.
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    

	    updateActionBar();
	    
	    //
	    /*
	    WebApiSession session = _loginManager.getActiveSession();
	    
	    
	    if (session != null && session.isOpened()) {
	    	// session.isOpened()
	    	// if the session is already open,
	        // try to show the selection fragment
	    	// TODO: Find which fragment to show?
	        showFragment(FRAGMENT_INDEX_LOADING, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	        showFragment(FRAGMENT_INDEX_SPLASH, false);
	    }
	    */
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public void onActivityResult(
			int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    _loginManager.onActivityResult(requestCode, resultCode, data);
	}

	//-------------------------------------------------------------------------
	//
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    _loginManager.onDestroy();
	}

	//-------------------------------------------------------------------------
	//
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    _loginManager.onSaveInstanceState(outState);
	}
	
	//-------------------------------------------------------------------------
	//
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Call this manually?
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
	
	//-------------------------------------------------------------------------
	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    	case android.R.id.home: 
	    		// Up / home button has been pressed. 
	    		onBackPressed();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	//-------------------------------------------------------------------------
	//
	public void onClickSignIn(View v) {
		showFragment(FRAGMENT_INDEX_SIGN_IN, true);
	}
	
	//-------------------------------------------------------------------------
	//
	public void onClickRegister(View v) {
		showFragment(FRAGMENT_INDEX_REGISTER, true);
	}
	
	//-------------------------------------------------------------------------
	//
	public void onClickFacebookLogin(View v) {
		_loginManager.loginFacebook();
	}
	
	//-------------------------------------------------------------------------
	//
	private boolean isOnline() {
	    ConnectivityManager connMgr = (ConnectivityManager) 
	            getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return networkInfo != null && networkInfo.isConnected();
	}
	
	//-------------------------------------------------------------------------
	//
	public void setActionBarTitle(int resId) {
		getSupportActionBar().setTitle(resId);
	}
	
	//-------------------------------------------------------------------------
	//
	public void hideActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if(actionBar.isShowing()) {
			actionBar.hide();
		}
	}
	
	//-------------------------------------------------------------------------
	//
	public void showActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if(!actionBar.isShowing()) {
			actionBar.show();
		}
	}
}
