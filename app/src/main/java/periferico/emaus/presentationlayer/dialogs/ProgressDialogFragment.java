package periferico.emaus.presentationlayer.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import periferico.emaus.R;

/**
 * Created by maubocanegra on 07/12/17.
 */

public class ProgressDialogFragment extends DialogFragment {

    public String title;
    private ProgressBar progressBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_progress, null);
        progressBar = view.findViewById(R.id.progressBar2);
        builder.setView(view);

        builder.setTitle(title);
        return builder.create();
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    public void changeTitle(String newTitle){
        getDialog().setTitle(newTitle);
    }
}
