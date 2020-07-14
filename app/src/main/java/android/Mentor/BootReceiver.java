package android.Mentor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by ANSHUL on 13-10-2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences=context.getSharedPreferences("Locked apps list", Context.MODE_PRIVATE);
        boolean mLock=sharedPreferences.getBoolean("master lock state",false);
        Log.d("BootCompleated...",mLock+"");
        if(mLock)
        {            //play
            context.startService(new Intent(context.getApplicationContext(),CurrentActivityService.class));
        }
    }
}
