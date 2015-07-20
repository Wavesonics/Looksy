package com.darkrockstudios.apps.looksy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkrockstudios.apps.looksy.data.Unlock;
import com.darkrockstudios.apps.looksy.settings.SettingsActivity;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{
	@Bind(R.id.test_view)
	TextView m_testView;

	@Bind(R.id.chart_fragment_container)
	ViewGroup m_fragmentContainerView;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		ButterKnife.bind( this );

		getSupportFragmentManager().beginTransaction()
		                           .replace( R.id.chart_fragment_container, LatestWeekChartFragment.newInstance() ).commit();

		updateViews();
	}

	private void updateViews()
	{
		final DateTime startToday = ReportUtils.getStartOfToday();

		DateTime end = DateTime.now();

		List<Unlock> today = Unlock.getAllInRange( startToday, end );
		List<Unlock> today_1 = Unlock.getAllInRange( ReportUtils.getStartOfYesterday(), startToday );
		List<Unlock> allUnlocks = Unlock.getAll();

		m_testView.setText( getString( R.string.summary, today.size(), today_1.size(), allUnlocks.size() ) );
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		updateViews();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if( id == R.id.action_settings )
		{
			startActivity( new Intent( this, SettingsActivity.class ) );
			return true;
		}

		return super.onOptionsItemSelected( item );
	}
}
