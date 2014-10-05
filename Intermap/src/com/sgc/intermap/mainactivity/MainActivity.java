package com.sgc.intermap.mainactivity;

import org.json.JSONObject;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.sgc.intermap.R;

import com.sgc.intermap.webapi.WebApiService;
import com.sgc.intermap.webapi.WebApiService.OnSignInStateChangeListener;
import com.sgc.intermap.webapi.WebApiService.WebApiServiceBinder;

public class MainActivity extends ActionBarActivity 
	implements OnSignInStateChangeListener {
	// implements OnSessionStateChangeListener {

	private static final String TAG = "MainActivity";

	private static final int FRAGMENT_INDEX_SPLASH = 0;
	private static final int FRAGMENT_INDEX_LOADING = 1;
	private static final int FRAGMENT_INDEX_SIGN_IN = 2;
	private static final int FRAGMENT_INDEX_REGISTER = 3;

	private static final int FRAGMENT_COUNT = FRAGMENT_INDEX_REGISTER + 1;

	private Fragment[] _fragments = new Fragment[FRAGMENT_COUNT];

	// private int _currentFragment;

	private boolean _isResumed = false;

	// private WebApiLoginManager _loginManager;
	private UiLifecycleHelper _fbUiHelper;

	private WebApiService _webService;
	private boolean _webServiceBound;

	// -------------------------------------------------------------------------
	//
	private Session.StatusCallback _fbSessionStatusCb = new Session.StatusCallback() {
		@Override
		public void call(Session fbSession, SessionState fbSessionState,
				Exception exception) {
			onFacebookSessionStateChange(fbSession, fbSessionState, exception);
		}
	};

	// -------------------------------------------------------------------------
	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // TODO: Restore _currentFragment

		// _loginManager = new WebApiLoginManager(this, this);
		// _loginManager.onCreate(savedInstanceState);

		_fbUiHelper = new UiLifecycleHelper(this, _fbSessionStatusCb);
		_fbUiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");

		FragmentManager fm = getSupportFragmentManager();

		fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			public void onBackStackChanged() {
				updateActionBar();
			}
		});

		_fragments[FRAGMENT_INDEX_SPLASH] = fm
				.findFragmentById(R.id.fragment_splash);
		_fragments[FRAGMENT_INDEX_LOADING] = fm
				.findFragmentById(R.id.fragment_loading);
		_fragments[FRAGMENT_INDEX_SIGN_IN] = fm
				.findFragmentById(R.id.fragment_signin);
		_fragments[FRAGMENT_INDEX_REGISTER] = fm
				.findFragmentById(R.id.fragment_register);

		/*
		 * FragmentTransaction transaction = fm.beginTransaction(); for(int i =
		 * `; i < _fragments.length; i++) { transaction.hide(_fragments[i]); }
		 * transaction.commit();
		 */
		// TODO: Remember last visible fragment index in bundle.
		// if(savedBundleState != null)
		// _loginManager.getActiveSession()

		showFragment(FRAGMENT_INDEX_SPLASH, false);
	}

	// -------------------------------------------------------------------------
	//
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection _connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the
			// IBinder and get LocalService instance
			WebApiServiceBinder binder = (WebApiServiceBinder) service;
			_webService = binder.getService();
			_webService.setSignInStateChangeListener(MainActivity.this);
			_webServiceBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			_webServiceBound = false;
			_webService.setSignInStateChangeListener(null);
			_webService = null;
		}

	};

	// -------------------------------------------------------------------------
	//
	@Override
	protected void onStart() {
		super.onStart();
		// Bind to WebApiService
		// TODO: Bind the service to the application context
		Intent intent = new Intent(this, WebApiService.class);
		bindService(intent, _connection, Context.BIND_AUTO_CREATE);
	}

	// -------------------------------------------------------------------------
	//
	@Override
	public void onResume() {
		super.onResume();
		// _loginManager.onResume();
		_fbUiHelper.onResume();
		_isResumed = true;
	}

	// -------------------------------------------------------------------------
	//
	@Override
	public void onPause() {
		super.onPause();
		// _loginManager.onPause();
		_fbUiHelper.onPause();
		_isResumed = false;
	}

	// -------------------------------------------------------------------------
	//
	@Override
	public void onStop() {
		super.onStop();
		// _loginManager.onStop();
		_fbUiHelper.onStop();

		// Unbind from the service
		if (_webServiceBound) {
			unbindService(_connection);
			_webServiceBound = false;
		}
	}

	/*
	 * public WebApiLoginManager getLoginManager() { return _loginManager; }
	 */

	// -------------------------------------------------------------------------
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

	// -------------------------------------------------------------------------
	//
	private void updateActionBar() {
		// Set action bar title and visibility depending
		// on selected fragment.
		ActionBar actionBar = getSupportActionBar();
		boolean isVisible = true;

		if (!_fragments[FRAGMENT_INDEX_SPLASH].isHidden()) {
			// No action bar in splash screen.
			isVisible = false;

		} else if (!_fragments[FRAGMENT_INDEX_SIGN_IN].isHidden()) {
			actionBar.setTitle(R.string.com_sgc_signin_actionbar_title);
		} else if (!_fragments[FRAGMENT_INDEX_REGISTER].isHidden()) {
			actionBar.setTitle(R.string.com_sgc_register_actionbar_title);
		}

		if (isVisible) {
			if (!actionBar.isShowing()) {
				actionBar.show();
			}
		} else if (actionBar.isShowing()) {
			actionBar.hide();
		}

	}

	// -------------------------------------------------------------------------
	//
	public void onFacebookSessionStateChange(
			Session fbSession,
			SessionState fbSessionState, 
			Exception exception) {
		if (_isResumed) {
			if(!_fragments[FRAGMENT_INDEX_SIGN_IN].isHidden() && 
					fbSessionState.isOpened()) {
				try {
					if (!_webServiceBound) {
						// TODO: use string resource.
						throw new AssertionError(
								"WebApiService must be bound to MainActivity.");
					}
					_webService.facebookLogin(fbSession);
					showFragment(FRAGMENT_INDEX_LOADING, false);
				} catch (Exception ex) {
					String msg = "Facebook login failed.";
					Log.e(TAG, msg, ex);
					showToast(msg);
				}
			}
			else if(!_fragments[FRAGMENT_INDEX_REGISTER].isHidden() && 
					fbSessionState.isOpened()) {
				// Rename FB button.
				RegisterFragment f = 
						(RegisterFragment)_fragments[FRAGMENT_INDEX_REGISTER];
				f.getLinkToFacebookButton().setText("Linked to: <account name>");
			}
		}
	}

	// -------------------------------------------------------------------------
	// This is the fragment-orientated version of onResume() that you can
	// override to perform operations in the Activity at the same point where
	// its fragments are resumed. Be sure to always call through to
	// the super-class.
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		updateActionBar();
	}

	// -------------------------------------------------------------------------
	//
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// _loginManager.onActivityResult(requestCode, resultCode, data);
		_fbUiHelper.onActivityResult(requestCode, resultCode, data);
	}

	// -------------------------------------------------------------------------
	//
	@Override
	public void onDestroy() {
		super.onDestroy();
		// _loginManager.onDestroy();
		_fbUiHelper.onDestroy();
	}

	// -------------------------------------------------------------------------
	// TODO Call this!
	private boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(
				Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	// -------------------------------------------------------------------------
	//
	public void setActionBarTitle(int resId) {
		getSupportActionBar().setTitle(resId);
	}

	// -------------------------------------------------------------------------
	//
	public void hideActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar.isShowing()) {
			actionBar.hide();
		}
	}

	// -------------------------------------------------------------------------
	//
	public void showActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (!actionBar.isShowing()) {
			actionBar.show();
		}
	}

	// -------------------------------------------------------------------------
	//
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// _loginManager.onSaveInstanceState(outState);
		_fbUiHelper.onSaveInstanceState(outState);
	}

	// -------------------------------------------------------------------------
	//
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { return true; }
	 */

	// -------------------------------------------------------------------------
	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Up / home button has been pressed.
			onBackPressed();
			return true;
		// case R.id.
		}
		return super.onOptionsItemSelected(item);
	}

	// -------------------------------------------------------------------------
	//
	public void onClickSignIn(View v) {
		showFragment(FRAGMENT_INDEX_SIGN_IN, true);
	}

	// -------------------------------------------------------------------------
	//
	public void onClickRegister(View v) {
		showFragment(FRAGMENT_INDEX_REGISTER, true);
	}

	// -------------------------------------------------------------------------
	//
	public void onClickLinkToFacebook(View v) {
		// TODO move to register fragment
		onClickFacebookSignIn(v);
	}

	// -------------------------------------------------------------------------
	//
	public void onClickBasicSignIn(View v) {
		final SignInFragment f = 
				(SignInFragment)_fragments[FRAGMENT_INDEX_SIGN_IN];
		final String email = f.getEmail();
		final String password = f.getPassword();
		
		if (!_webServiceBound) {
			// TODO: use string resource.
			throw new AssertionError(
					"WebApiService must be bound to MainActivity.");
		}
		
		try {
			_webService.basicLogin(email, password);
			showFragment(FRAGMENT_INDEX_LOADING, false);
		} catch(Exception ex) {
			String msg = "Login failed.";
			Log.e(TAG, msg, ex);
			showToast(msg);
			showFragment(FRAGMENT_INDEX_SIGN_IN, false);
		}
		
	}
	
	// -------------------------------------------------------------------------
	//
	public void onClickFacebookSignIn(View v) {
		// TODO: _webService.facebookLogin();
		Session fbSession = Session.getActiveSession();
		if(fbSession != null && fbSession.isOpened()) {
			// Already logged in.  Trigger the callback to log into the platform. 
			// TODO: call _webService.facebookLogin() here directly!
			onFacebookSessionStateChange(
					fbSession, fbSession.getState(), null);
		}
		else {
			// Open a new session (this will show the Facebook login UI).
			Session.openActiveSession(
					this, true, _fbSessionStatusCb);
		}
	}
	
	//-------------------------------------------------------------------------
	//	
	private void showToast(String text) {
		Toast.makeText(this.getApplicationContext(), text,
				Toast.LENGTH_LONG).show();
	}
	
	//-------------------------------------------------------------------------
	//	
	@Override
	public void onSignInStateChange(JSONObject response, Throwable exception) {
		if(exception == null) {
			// TODO: Use resource!
			showToast("Signed in.");
			// TODO: Launch IntermapActivity.
		}
		else {
			// TODO: Use resource!
			showToast("Could not sign in.");

			// Go back to sign in screen.
			showFragment(FRAGMENT_INDEX_SIGN_IN, false);
		}
	}
}
