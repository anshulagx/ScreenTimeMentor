package android.Mentor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ANSHUL on 09-10-2017.
 */

public class CurrentActivityService extends Service {

    int DELAY=60;// set here in hours


    long DELAY2=1000*75;

     List<String> lApps;
    List<Calendar> lAppsCal;
    AppChecker appChecker,adminAppChecker;
    String PHONE,LAUNCHER;
    int overallUsage=0;
    Calendar adminCal;
    SharedPreferences sharedpreferences;
    long totalTime;

    List<TheApp> stats=new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedpreferences = getSharedPreferences("Locked apps list", Context.MODE_PRIVATE);
        lApps=new ArrayList<>();
        lAppsCal=new ArrayList<>();
        Calendar tempCal=Calendar.getInstance();
        tempCal.add(Calendar.SECOND,DELAY);
        for (int i=1;i<=sharedpreferences.getInt("total locked",0);i++)
        {
            lApps.add(sharedpreferences.getString(""+i,null));
            Log.d("StringService:",sharedpreferences.getString(""+i,null));

            lAppsCal.add(tempCal);
        }
        PHONE=sharedpreferences.getString("phone",null);
        LAUNCHER=sharedpreferences.getString("launcher",null);
        adminCal=Calendar.getInstance();
        adminCal.add(Calendar.MONTH,-1);


    }


    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {



        Log.d("Service..","Started");
        //Log.d("Service",InstAppList.theApp.get(0).getPackageName());
        appChecker = new AppChecker();
        adminAppChecker=new AppChecker(5);

        adminAppChecker
                .when("com.android.settings.DeviceAdminAdd", new AppChecker.Listener() {
                    @Override
                    public void onForeground(String packageName) {
                        // do something when com.other.app is in the foreground
                        Log.d("CAlender",Calendar.getInstance().after(adminCal)+"");
                        if (Calendar.getInstance().after(adminCal))
                        {
                            visualLock();
                        }
                    }}
                ).timeout(200).start(this);



        appChecker
                .whenList(lApps, new AppChecker.Listener() {
                    @Override
                    public void onForeground(String process) {
                       //if (Calendar.getInstance().after(lAppsCal.get(lApps.indexOf(process))) ) {

                        //if (toBLocked())
                        visualLock();

                    }
                    }
                )
                .timeout(500)
                .start(this);



                /*AppChecker smarty=new AppChecker();
                smarty.whenAny(new AppChecker.Listener() {
                    @Override
                     public void onForeground(String process) {
                        if (!(process.equals(LAUNCHER)||process.equals(PHONE)))
                            {
                                //current activity (not lancher)
                                overallUsage++;
                                if (overallUsage>120)
                                {
                                    Toast.makeText(getApplicationContext(),"To be closed soon",Toast.LENGTH_LONG);
                                    //lock for 2 hours

                                }
                            }
                        }
                })
                .timeout(60*1000)
                ;*/

        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Intent svc = new Intent(this, OverlayService.class);
//        Log.d("starting overlay",null);
        stopService(svc);}
        appChecker.stop();
        adminAppChecker.stop();
        //startActivity(new Intent(this,FullscreenActivity.class));


        Log.d("Service..","Destroyed");

        if(sharedpreferences.getBoolean("edit",true))
         startService(new Intent(this,CurrentActivityService.class));
    }

    private void launchMainService() {

        Intent svc = new Intent(this, OverlayService.class);
//        Log.d("starting overlay",null);
        startService(svc);


    }
    void visualLock()
    {

        final Intent i=new Intent(this,FullscreenActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            launchMainService();
        }startActivity(i);

    }

    //TODO
    private boolean toBLocked()
    {
        //totalTie=shared                    shared do
        totalTime+=500;

        int max=1000*60;
        int unlock=1000*30;

        if (totalTime>max)
        {
            if (totalTime>(max+unlock))
            {
                totalTime=0;
                return false;
            }
            return true;
        }
        return false;
    }

}