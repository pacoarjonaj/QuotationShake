package fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

// DIALOG TO DELETE ALL QUOTES

public class DialogClassFragment extends DialogFragment {

    public DialogClassFragment(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        builder.setMessage("Are you sure that you want to remove all?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getParentFragmentManager().setFragmentResult("remove_all", savedInstanceState);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }
}
