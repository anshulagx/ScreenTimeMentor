package android.Mentor;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by matt on 08/08/2016.
 */

public class OverlayService extends Service implements View.OnTouchListener {

    private static final String TAG = OverlayService.class.getSimpleName();

    private WindowManager windowManager;

    private View floatyView;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {


        super.onCreate();

        Log.d("Overlay Service..","Created");
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        addOverlayView();

    }

    private void addOverlayView() {

        final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        0,
                        PixelFormat.OPAQUE);

        params.gravity = Gravity.CENTER | Gravity.START;
        params.x = 0;
        params.y = 0;

        FrameLayout interceptorLayout = new FrameLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    // Check if the HOME button is pressed
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                        Log.v(TAG, "BACK Button Pressed");

                        // As we've taken action, we'll return true to prevent other apps from consuming the event as well
                        return true;
                    }
                    if (event.getKeyCode()==KeyEvent.KEYCODE_APP_SWITCH)
                    {
                        return true;
                    }
                    return true;
                }

                // Otherwise don't intercept the event
                return super.dispatchKeyEvent(event);
            }
        };

        floatyView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.floating_view, interceptorLayout);

        floatyView.setOnTouchListener(this);
        floatyView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);

                if (floatyView != null) {

                    windowManager.removeView(floatyView);
                    floatyView = null;
                }
                terminate();

            }
        });


        windowManager.addView(floatyView, params);
    }

    void terminate()
    {
        this.stopSelf();
    }
    @Override
    public void onDestroy() {

        Log.d("Overlay service","Destroyed");
        super.onDestroy();

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        Log.v(TAG, "onTouch...");

        // Kill service
       // onDestroy();


        return true;
    }
}