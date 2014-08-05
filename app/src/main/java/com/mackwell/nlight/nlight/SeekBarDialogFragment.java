package com.mackwell.nlight.nlight;

import com.mackwell.nlight.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;


public class SeekBarDialogFragment extends DialogFragment
{

	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		final View dialogView = inflater.inflate(R.layout.dialog_seekbar, null);
		
		dialogView.setMinimumHeight(400);
		
		builder.setView(dialogView);
		
		
		
		builder.setMessage("Adjust Brightness")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
		
		
		;
		
		Dialog d = builder.create();
	
		return d;
		
	}

	
	
}
