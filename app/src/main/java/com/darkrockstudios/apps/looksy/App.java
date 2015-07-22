package com.darkrockstudios.apps.looksy;


import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.darkrockstudios.apps.looksy.background.report.ReportService;

import java.io.File;

/**
 * Created by Adam on 7/20/2015.
 */
public class App extends Application
{
	public static final String TAG = App.class.getSimpleName();

	public static final String EXTERNAL_APP_DIR = "Looksy";
	public static final String DATABASE_DIR     = "databases";

	private DatabaseContext m_databaseContext;

	@Override
	public void onCreate()
	{
		super.onCreate();

		m_databaseContext = new DatabaseContext( this );
		ActiveAndroid.initialize( m_databaseContext );
		PreferenceManager.setDefaultValues( this, R.xml.settings, false );

		// Make sure we have a report scheduled
		ReportService.scheduleReport( this );
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
		ActiveAndroid.dispose();
	}

	public File getLooksyDatabaseFile()
	{
		return m_databaseContext.getApplicationDatabaseFile();
	}

	/**
	 * Context wrapper to trick ActiveAndroid into writing it's database
	 * into public storage. We want a much more durable database than private
	 * storage will afford us. This will allow users to uninstall and reinstall
	 * the app without losing our data.
	 */
	private static class DatabaseContext extends ContextWrapper
	{
		private File m_databaseFile;

		public DatabaseContext( Context base )
		{
			super( base );
		}

		public File getApplicationDatabaseFile()
		{
			return m_databaseFile;
		}

		public File getStoragePath()
		{
			return new File( Environment.getExternalStorageDirectory(), EXTERNAL_APP_DIR );
		}

		@Override
		public File getDatabasePath( String name )
		{
			File databaseDir = new File( getStoragePath(), DATABASE_DIR );
			return new File( databaseDir, name );
		}

		@Override
		public Context getApplicationContext()
		{
			return this;
		}

		@Override
		public SQLiteDatabase openOrCreateDatabase( String name, int mode, SQLiteDatabase.CursorFactory factory )
		{
			return openOrCreateDatabase( name, mode, factory, null );
		}

		@Override
		public SQLiteDatabase openOrCreateDatabase( String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler )
		{
			final File dir = getStoragePath();
			if( !dir.isDirectory() )
			{
				if( !dir.mkdir() )
				{
					Log.e( TAG, "Failed to create database directory" );
				}
			}
			m_databaseFile = getDatabasePath( name );
			return SQLiteDatabase.openOrCreateDatabase( m_databaseFile.getAbsolutePath(), factory, errorHandler );
		}
	}
}
