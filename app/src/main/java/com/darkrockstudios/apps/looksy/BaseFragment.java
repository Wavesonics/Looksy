package com.darkrockstudios.apps.looksy;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f2prateek.dart.Dart;

import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * Created by Adam on 7/20/2015.
 */
public abstract class BaseFragment extends Fragment
{
	@LayoutRes
	protected abstract int getLayoutId();

	@Override
	public void onCreate( @Nullable Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		Dart.inject( this, getArguments() );
		Icepick.restoreInstanceState( this, savedInstanceState );
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		final View view = inflater.inflate( getLayoutId(), container, false );
		ButterKnife.bind( this, view );

		return view;
	}

	@Override
	public void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		Icepick.saveInstanceState( this, outState );
	}
}
