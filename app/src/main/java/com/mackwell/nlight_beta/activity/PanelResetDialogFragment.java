package com.mackwell.nlight_beta.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.mackwell.nlight_beta.R;

/**
 * Created by weiyuan zhu on 13/10/14.
 */
public class PanelResetDialogFragment extends DialogFragment {

    private String ip;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public PanelResetDialogFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog title
        builder.setTitle(getString(R.string.dialog_panelreset_title,ip));
        builder.setMessage(getString(R.string.dialog_panelrest_message));
        builder.setPositiveButton(getString(R.string.dialog_panelreset_positivebutton),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //return to loading screen
                Intent intent = new Intent(getActivity(),LoadingScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(getString(R.string.dialog_panelreset_negtivebutton),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        //set dialog message panel is busy


        return builder.create();


    }




}
