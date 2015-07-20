package com.darkrockstudios.apps.looksy.background.recordcheck;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.darkrockstudios.apps.looksy.data.Unlock;

import org.joda.time.DateTime;

public class RecordCheckIntentService extends IntentService
{
	private static final String TAG = RecordCheckIntentService.class.getSimpleName();

	private static final String ACTION_RECORD_CHECK = RecordCheckIntentService.class.getName() + ".action.RECORD_CHECK";
	private static final String EXTRA_CHECK_TIME    = RecordCheckIntentService.class.getName() + ".extra.CHECK_TIME";

	public static Intent startCheckRecordIntent( Context context )
	{
		Intent intent = new Intent( context, RecordCheckIntentService.class );
		intent.setAction( ACTION_RECORD_CHECK );
		intent.putExtra( EXTRA_CHECK_TIME, DateTime.now().getMillis() );
		return intent;
	}

	public RecordCheckIntentService()
	{
		super( "RecordCheckIntentService" );
	}

	@Override
	protected void onHandleIntent( Intent intent )
	{
		if( intent != null )
		{
			final String action = intent.getAction();
			if( ACTION_RECORD_CHECK.equals( action ) )
			{
				final long checkTimeMs = intent.getLongExtra( EXTRA_CHECK_TIME, Long.MIN_VALUE );
				if( checkTimeMs != Long.MIN_VALUE )
				{
					DateTime checkTime = new DateTime( checkTimeMs );
					recordCheck( checkTime );
				}
			}
		}

		RecordCheckReceiver.completeWakefulIntent( intent );
	}

	private void recordCheck( @NonNull final DateTime checkTime )
	{
		Log.i( TAG, "Recording unlock in: " + checkTime );
		Unlock unlock = new Unlock( checkTime );
		unlock.save();
	}
}
