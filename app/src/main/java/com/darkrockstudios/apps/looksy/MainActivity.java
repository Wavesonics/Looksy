package com.darkrockstudios.apps.looksy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.darkrockstudios.apps.looksy.settings.SettingsActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
	private static final long DRAWER_CLOSE_DELAY_MS = 250;

	@Bind(R.id.nav_drawer)
	DrawerLayout m_drawerLayout;

	@Bind(R.id.navigation)
	NavigationView m_navigationView;

	private Handler                     m_uiHandler;
	private LooksyActionBarDrawerToggle m_drawerToggle;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		ButterKnife.bind( this );

		m_uiHandler = new Handler();

		m_navigationView.setNavigationItemSelectedListener( this );

		m_drawerToggle = new LooksyActionBarDrawerToggle( this, m_drawerLayout );
		m_drawerLayout.setDrawerListener( m_drawerToggle );

		getSupportActionBar().setDisplayHomeAsUpEnabled( true );
		getSupportActionBar().setHomeButtonEnabled( true );

		if( savedInstanceState == null )
		{
			getSupportFragmentManager().beginTransaction()
			                           .replace( R.id.content_container, HomeFragment.newInstance() ).commit();
		}
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
	public boolean onNavigationItemSelected( final MenuItem menuItem )
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
		getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		final boolean handled;

		final int id = item.getItemId();
		if( id == R.id.action_settings )
		{
			startActivity( new Intent( this, SettingsActivity.class ) );
			handled = true;
		}
		else if( m_drawerToggle.onOptionsItemSelected( item ) )
		{
			handled = true;
		}
		else
		{
			handled = super.onOptionsItemSelected( item );
		}

		return handled;
	}
}
