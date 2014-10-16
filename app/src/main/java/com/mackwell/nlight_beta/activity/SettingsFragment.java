package com.mackwell.nlight_beta.activity;

import com.mackwell.nlight_beta.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.*;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingsFragment extends PreferenceFragment implements  SharedPreferences.OnSharedPreferenceChangeListener{

    public static final int RESULT_LOAD_IMAGE = 0; //request code
    private Preference customIconPref;
    private Preference paneListPref;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //inflate preference fragmet layout
		addPreferencesFromResource(R.xml.preferences);

        //find preference pref_app_icon
        customIconPref = findPreference("pref_app_icons");

        //set preference onCLidkListener
        customIconPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //start media image activity for pick images
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                flat test,which will result in RESULT_CANCELLED
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivityForResult(i, RESULT_LOAD_IMAGE);

                return true;
            }
        });

        paneListPref = findPreference("pref_panel_list");

        paneListPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(),CachedPanelList.class);
                startActivity(intent);

                return true;
            }
        });
	}

    @Override
    public void onResume() {
        super.onResume();

        //register listener
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

//      update preference icon and subtitle
        updateAppImage();


    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * when get result back from start intent for result
     * @param requestCode see which request it is
     * @param resultCode ok or not
     * @param data intent for pass data back
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//      put uri information to preference object "pref_app_icons"
//      checks if result data is null
        if (data!=null) {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("pref_app_icons",data.getDataString());
            editor.commit();
        }


    }

    //listener, implements SharedPreference.onSharedPreferenceChanged
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_icon_checkbox")) {
            updateAppImage();
        }
    }

    private void updateAppImage(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String imageLocation = sharedPref.getString("pref_app_icons","default image");
        CheckBoxPreference test = (CheckBoxPreference) findPreference("pref_icon_checkbox");
        boolean customIcon = sharedPref.getBoolean("pref_icon_checkbox",false);
        Uri uri = Uri.parse(imageLocation);



        if(customIcon && !imageLocation.equals("default image"))
        {
            test.setSummary(getResources().getString(R.string.pref_summary_customIcon_checked));
            try {
                InputStream stream = getActivity().getContentResolver().openInputStream(uri);
                Drawable appImage = Drawable.createFromStream(stream, "test");
                if (getActivity().getActionBar() != null) {
                    getActivity().getActionBar().setIcon(appImage);
                    customIconPref.setIcon(appImage);
                    customIconPref.setSummary(getResources().getString(R.string.pref_subTitle_customIcon, imageLocation));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            test.setSummary(getResources().getString(R.string.pref_summary_customIcon_unchecked));

            getActivity().getActionBar().setIcon(R.drawable.mackwell_logo);
            String defaultImage = getResources().getString(R.string.pref_icon_default);
            customIconPref.setSummary(getResources().getString(R.string.pref_subTitle_customIcon, defaultImage));
            customIconPref.setIcon(getResources().getDrawable(R.drawable.mackwell_logo));

        }
    }
}
