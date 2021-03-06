package com.darkrockstudios.apps.looksy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.darkrockstudios.apps.looksy.data.Unlock;
import com.darkrockstudios.apps.looksy.settings.SettingsActivity;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.f2prateek.dart.Optional;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
	private static final long DRAWER_CLOSE_DELAY_MS = 250;

	public static final String EXTRA_NAVIGATE_TO = "com.darkrockstudios.apps.looksy.NAVIGATE_TO";

	public enum InitialLocation
	{
		Home,
		DailyReport
	}

	@Optional
	@InjectExtra(EXTRA_NAVIGATE_TO)
	InitialLocation m_initialLocation;

	@Bind(R.id.nav_drawer)
	DrawerLayout m_drawerLayout;

	@Bind(R.id.navigation)
	NavigationView m_navigationView;

	TextView m_lastDayReportTotalView;

	View m_reportContainerView;

	View m_reportProgressBar;

	private Handler                     m_uiHandler;
	private LooksyActionBarDrawerToggle m_drawerToggle;
	private PopulateDataTask m_populateTask;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		Dart.inject( this );
		ButterKnife.bind( this );

		m_uiHandler = new Handler();

		m_navigationView.setNavigationItemSelectedListener( this );

		View navDrawerHeader = m_navigationView.getHeaderView( 0 );
		m_lastDayReportTotalView = (TextView) navDrawerHeader.findViewById( R.id.DRAWER_report_header_total );
		m_reportContainerView = navDrawerHeader.findViewById( R.id.DRAWER_report_container );
		m_reportProgressBar = navDrawerHeader.findViewById( R.id.DRAWER_report_progress_bar );

		m_drawerToggle = new LooksyActionBarDrawerToggle( this, m_drawerLayout );
		m_drawerLayout.addDrawerListener( m_drawerToggle );

		getSupportActionBar().setDisplayHomeAsUpEnabled( true );
		getSupportActionBar().setHomeButtonEnabled( true );

		if( savedInstanceState == null )
		{
			getSupportFragmentManager().beginTransaction()
									   .replace( R.id.content_container, getInitialFragment() ).commit();
		}
	}

	private Fragment getInitialFragment()
	{
		final Fragment fragment;

		if( m_initialLocation == null )
		{
			m_initialLocation = InitialLocation.Home;
		}

		switch( m_initialLocation )
		{
			case Home:
				fragment = HomeFragment.newInstance();
				break;
			case DailyReport:
				fragment = DailyReportFragment.newInstance();
				break;
			default:
				throw new IllegalStateException( "No initial location set" );
		}

		return fragment;
	}

	private void updateViews()
	{
		cancelTask();

		m_populateTask = new PopulateDataTask();
		m_populateTask.execute();
	}

	private void cancelTask()
	{
		if( m_populateTask != null )
		{
			m_populateTask.cancel( true );
			m_populateTask = null;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		updateViews();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		cancelTask();
	}

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		Icepick.saveInstanceState( this, outState );
	}

	@Override
	protected void onRestoreInstanceState( @NonNull Bundle savedInstanceState )
	{
		super.onRestoreInstanceState( savedInstanceState );
		Icepick.restoreInstanceState( this, savedInstanceState );
	}

	@Override
	protected void onPostCreate( Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		// Sync the toggle state after onRestoreInstanceState has occurred.
		m_drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged( newConfig );
		m_drawerToggle.onConfigurationChanged( newConfig );
	}

	@Override
	public boolean onNavigationItemSelected( @NonNull final MenuItem menuItem )
	{
		menuItem.setChecked( true );

		m_uiHandler.postDelayed( new Runnable()
		{
			@Override
			public void run()
			{
				navigateTo( menuItem.getItemId() );
			}
		}, DRAWER_CLOSE_DELAY_MS );

		m_drawerLayout.closeDrawers();

		return true;
	}

	private void navigateTo( final int itemId )
	{
		switch( itemId )
		{
			case R.id.navigation_item_home:
				getSupportFragmentManager().beginTransaction()
										   .replace( R.id.content_container, HomeFragment.newInstance() ).commit();
				break;
			case R.id.navigation_item_daily:
				getSupportFragmentManager().beginTransaction()
										   .replace( R.id.content_container, DailyStatsFragment.newInstance() ).commit();
				break;
			case R.id.navigation_item_lifetime:
				getSupportFragmentManager().beginTransaction()
										   .replace( R.id.content_container, LifeTimeStatsFragment.newInstance() ).commit();
				break;
			case R.id.navigation_item_settings:
				startActivity( new Intent( this, SettingsActivity.class ) );
				break;
		}
	}

	private class LooksyActionBarDrawerToggle extends android.support.v7.app.ActionBarDrawerToggle
	{
		public LooksyActionBarDrawerToggle( Activity activity, DrawerLayout drawerLayout )
		{
			super( activity, drawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close );
		}

		public void onDrawerClosed( View view )
		{
			super.onDrawerClosed( view );
			//getSupportActionBar().setTitle( R.string.app_name );
			invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
		}

		public void onDrawerOpened( View drawerView )
		{
			super.onDrawerOpened( drawerView );
			//getSupportActionBar().setTitle( R.string.app_name );
			invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		//getMenuInflater().inflate( R.menu.menu_main, menu );
		return false;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		final boolean handled;

		final int id = item.getItemId();
		if( m_drawerToggle.onOptionsItemSelected( item ) )
		{
			handled = true;
		}
		else
		{
			handled = super.onOptionsItemSelected( item );
		}

		return handled;
	}

	public void onHeaderClick( View view )
	{
		m_uiHandler.postDelayed( new Runnable()
		{
			@Override
			public void run()
			{
				getSupportFragmentManager().beginTransaction()
										   .replace( R.id.content_container, DailyReportFragment.newInstance() ).commit();
			}
		}, DRAWER_CLOSE_DELAY_MS );

		m_drawerLayout.closeDrawers();
	}

	private class PopulateDataTask extends AsyncTask<Void, Void, Integer>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			m_reportProgressBar.setVisibility( View.VISIBLE );
			m_reportContainerView.setVisibility( View.GONE );
		}

		@Override
		protected Integer doInBackground( Void... params )
		{
			return Unlock.countAllInRange( ReportUtils.getStartOfYesterday(), ReportUtils.getStartOfToday() );
		}

		@Override
		protected void onPostExecute( Integer totalLooks )
		{
			super.onPostExecute( totalLooks );

			m_lastDayReportTotalView.setText( String.valueOf( totalLooks ) );

			m_reportProgressBar.setVisibility( View.GONE );
			m_reportContainerView.setVisibility( View.VISIBLE );
		}
	}
}
