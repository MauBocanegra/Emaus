package periferico.emaus.domainlayer.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import periferico.emaus.domainlayer.WS;

/**
 * Created by maubocanegra on 30/01/18.
 */

public class AppCompatActivity_Job extends AppCompatActivity{
    @Override
    protected void onStart() {
        Log.d("Debug", "Should start job");
        WS.startJob();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
        dispatcher.cancelAll();
        //Ha
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
