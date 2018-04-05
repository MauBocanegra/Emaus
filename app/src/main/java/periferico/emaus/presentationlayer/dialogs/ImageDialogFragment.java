package periferico.emaus.presentationlayer.dialogs;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import periferico.emaus.R;

/**
 * Created by maubocanegra on 07/12/17.
 */

public class ImageDialogFragment extends DialogFragment {

    public String title;
    private ProgressBar progressBar;
    private ImageView imageView;
    private String imgUrl;
    //private Line constraintLayout;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()/*,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen*/);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = View.inflate(getActivity(),R.layout.dialog_catalog_item, null);
        imageView = view.findViewById(R.id.imageView5);
        //constraintLayout = view.findViewById(R.id.constraintLayout);
        builder.setView(view);

        //builder.setTitle(title);
        return builder.create();
    }


    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        int height = (int)(getResources().getDisplayMetrics().heightPixels);
        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);
        try {
            Picasso.with(getActivity()).load(Uri.parse(imgUrl)).into(imageView);
        }catch(Exception e){
            getDialog().hide();
        }
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("imgurl",imgUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        String url = savedInstanceState.getString("imgurl");
        if(url!=null){imgUrl=url;}
        super.onViewStateRestored(savedInstanceState);
    }

    /*
    @Override
    public void onStart() {
        super.onStart();

        int height = (int)(getResources().getDisplayMetrics().heightPixels);
        int width = (int)(getResources().getDisplayMetrics().widthPixels);

        getDialog().getWindow().setLayout(width, height);
    }
    */

    public void setImage(String imgURL) {
        imgUrl = imgURL;
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    public void changeTitle(String newTitle){
        getDialog().setTitle(newTitle);
    }
}
