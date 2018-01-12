package periferico.emaus.domainlayer.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.firebase_objects.CatalogItem_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

/**
 * Created by maubocanegra on 10/12/17.
 */

public class AdapterCatalog extends RecyclerView.Adapter<AdapterCatalog.ViewHolder> {

    private ArrayList<Object_Firebase> mDataset;
    Activity activity;

    public AdapterCatalog(ArrayList<Object_Firebase> myDataset, AppCompatActivity activity){
        mDataset = myDataset;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(activity).load(Uri.parse( ((CatalogItem_Firebase)mDataset.get(position)).getLink() )).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        public ViewHolder(View v){
            super(v);
            img = v.findViewById(R.id.item_catalog_img);
        }
    }
}
