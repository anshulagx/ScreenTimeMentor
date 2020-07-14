package android.Mentor;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ANSHUL on 09-10-2017.
 */

public class AdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.d("debug","Admin enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.d("debug","Admin disabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        context.startActivity(new Intent(context.getApplicationContext(),FullscreenActivity.class));
        Log.d("debug","disable requested");

        return "Deactivating this may erase all device data!To proceed click OK";
        //return super.onDisableRequested(context, intent);
    }
}
