package com.darkrockstudios.apps.looksy.settings;

import android.content.Context;
import android.os.Handler;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.darkrockstudios.apps.looksy.App;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.io.File;

/**
 * Created by Adam on 7/21/2015.
 */
public class DebugSwapDatabasesPreference extends Preference
{
	public DebugSwapDatabasesPreference( Context context )
	{
		super( context );
		init();
	}

	public DebugSwapDatabasesPreference( Context context, AttributeSet attrs, int defStyleAttr )
	{
		super( context, attrs, defStyleAttr );
		init();
	}

	public DebugSwapDatabasesPreference( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init();
	}

	public DebugSwapDatabasesPreference( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
	{
		super( context, attrs, defStyleAttr, defStyleRes );
		init();
	}

	private void init()
	{
		setTitle( "DEBUG: Swap Databases" );
		setSummary( "Swap test and production databases [requires app restart]" );
	}

	@Override
	protected void onClick()
	{
		super.onClick();

		final Handler handler = new Handler();

		final Context context = getContext();
		Toast.makeText( context, "Swapping databases...", Toast.LENGTH_SHORT ).show();
		new Thread()
		{
			@Override
			public void run()
			{
				swapDatabase();
				ProcessPhoenix.triggerRebirth( context );
			}
		}.start();
	}

	private void swapDatabase()
	{
		final App app = (App) getContext().getApplicationContext();

		final File mainDbFile = app.getLooksyDatabaseFile();
		final File mainJournalFile = new File( mainDbFile.getParentFile(), mainDbFile.getName() + "-journal" );

		swapBackups( mainDbFile );
		swapBackups( mainJournalFile );
	}

	private void swapBackups( final File mainFile )
	{
		final String BACKUP_FILE_EXT = ".bak";
		File backDbFile = new File( mainFile.getParentFile(), mainFile.getName() + BACKUP_FILE_EXT );

		final String TEMP_FILE_EXT = ".tmp";
		File tempDbFile = new File( mainFile.getParentFile(), mainFile.getName() + TEMP_FILE_EXT );

		if( backDbFile.exists() )
		{
			if( tempDbFile.exists() )
			{
				tempDbFile.delete();
			}
			backDbFile.renameTo( tempDbFile );
		}

		mainFile.renameTo( backDbFile );

		if( tempDbFile.exists() )
		{
			tempDbFile.renameTo( mainFile );
		}
	}
}
