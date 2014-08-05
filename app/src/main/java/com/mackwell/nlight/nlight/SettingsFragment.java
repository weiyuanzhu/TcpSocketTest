package com.mackwell.nlight.nlight;

import com.mackwell.nlight.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.*;
import android.os.*;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
	}
	
	

}
