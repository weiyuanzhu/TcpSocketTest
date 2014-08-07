package com.mackwell.nlight.nlight;

import com.mackwell.nlight.R;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingsActivity extends BaseActivity {
	
	public static final String SAVE_PANEL_LOCATION = "pref_key_panel_save";
	public static final String SAVE_CHECKED = "pref_key_save_checked";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
	}

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
			default: return super.onOptionsItemSelected(item);
		}
		
	}
	
	

}
