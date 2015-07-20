package com.darkrockstudios.apps.looksy.settings;

import android.content.Context;
import android.os.Handler;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.darkrockstudios.apps.looksy.ReportUtils;
import com.darkrockstudios.apps.looksy.data.Unlock;

import org.joda.time.DateTime;

/**
 * Created by Adam on 7/20/2015.
 */
public class DebugInsertDataPreference extends Preference
{
	public DebugInsertDataPreference( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
	{
		super( context, attrs, defStyleAttr, defStyleRes );
		init();
	}

	public DebugInsertDataPreference( Context context, AttributeSet attrs, int defStyleAttr )
	{
		super( context, attrs, defStyleAttr );
		init();
	}

	public DebugInsertDataPreference( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init();
	}

	public DebugInsertDataPreference( Context context )
	{
		super( context );
		init();
	}

	private void init()
	{
		setTitle( "DEBUG: Insert fake data" );
		setSummary( "Populate the database with dummy data for testing. This will take a few seconds." );
	}

	@Override
	protected void onClick()
	{
		super.onClick();

		final Handler handler = new Handler();

		Toast.makeText( getContext(), "Inserting fake data...", Toast.LENGTH_SHORT ).show();
		new Thread()
		{
			@Override
			public void run()
			{
				insert_fake_data();
				handler.post( new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText( getContext(), "[COMPLETE]", Toast.LENGTH_SHORT ).show();
					}
				} );
			}
		}.start();
	}

	private void insert_fake_data()
	{
		for( int ii = 0; ii < 23; ++ii )
		{
			test_save_data( ReportUtils.getStartOfDay( 0 ).plusHours( ii ) );
		}

		for( int ii = 0; ii < 12; ++ii )
		{
			test_save_data( ReportUtils.getStartOfDay( 1 ).plusHours( ii ) );
		}

		for( int ii = 0; ii < 24; ii += 2 )
		{
			for( int xx = 0; xx < 2; ++xx )
			{
				test_save_data( ReportUtils.getStartOfDay( 2 ).plusHours( ii ).plusMinutes( xx ) );
			}
		}

		for( int ii = 0; ii < 12; ++ii )
		{
			for( int xx = 0; xx < 2; ++xx )
			{
				test_save_data( ReportUtils.getStartOfDay( 3 ).plusHours( ii ).plusMinutes( xx ) );
			}
		}

		for( int ii = 0; ii < 20; ii += 2 )
		{
			for( int xx = 0; xx < 2; ++xx )
			{
				test_save_data( ReportUtils.getStartOfDay( 4 ).plusHours( ii ).plusMinutes( xx ) );
			}
		}

		for( int ii = 0; ii < 12; ++ii )
		{
			for( int xx = 0; xx < 3; ++xx )
			{
				test_save_data( ReportUtils.getStartOfDay( 5 ).plusHours( ii ).plusMinutes( xx ) );
			}
		}

		for( int ii = 0; ii < 24; ++ii )
		{
			test_save_data( ReportUtils.getStartOfDay( 6 ).plusHours( ii ) );
		}
	}

	private void test_save_data( DateTime checkTime )
	{
		Unlock unlock = new Unlock( checkTime );
		unlock.save();
	}
}
