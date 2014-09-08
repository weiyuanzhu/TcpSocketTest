package com.mackwell.nlight.nlight;

import com.mackwell.nlight.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.*;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingsFragment extends PreferenceFragment {

    public static final int RESULT_LOAD_IMAGE = 0; //request code
    private Preference customIconPref;

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
	}

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String imageLocation = sharedPref.getString("pref_app_icons","default image");
        Uri uri = Uri.parse(imageLocation);


//      update preference icon and subtitle
        if(!imageLocation.equals("default image")) {
            try {
                InputStream stream = getActivity().getContentResolver().openInputStream(uri);
                Drawable d = Drawable.createFromStream(stream, "test");
                customIconPref.setIcon(d);
                customIconPref.setSummary(getResources().getString(R.string.pref_subTitle_customIcon, imageLocation));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                customIconPref.setSummary(getResources().getString(R.string.pref_subTitle_customIcon, getResources().getString(R.string.text_image_not_found)));
            }

        }else{
            String defaultImage = getResources().getString(R.string.pref_icon_default);
            customIconPref.setSummary(getResources().getString(R.string.pref_subTitle_customIcon, defaultImage));

        }


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
}
