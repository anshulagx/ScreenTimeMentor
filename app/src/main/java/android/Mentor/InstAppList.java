
//main activity ,programe starts here

package android.Mentor;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstAppList extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    boolean mLock=false;    //master lock state
     FloatingActionButton masterButton; //pink master floating button


    //useless probably
    List<String> lockedApp=new ArrayList<String>();                 //locked app names
    static List<String> lockedAppPackage=new ArrayList<String>();   //locked app package list

   static List<TheApp> theApp=new ArrayList<>();                //All app biodata of type theApp

    Map<String, UsageStats> map;
    UsageStatsManager mUsageStatsManager;
    String launcher,contacts;//some app names to be removed from the app list shown ,preventing them beign locked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inst_app_list);
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Starting..");
        progressDialog.setMessage("Please wait while we do the background work");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.show();

        sharedpreferences = getSharedPreferences("Locked apps list", Context.MODE_PRIVATE);//the app directory"locked apps list"

        editor = sharedpreferences.edit();

        stopService(new Intent(getApplicationContext(),CurrentActivityService.class));//stop any earlier services
        editor.putBoolean("edit",true);//a pacemark to allow in App editing
        editor.commit();

        mUsageStatsManager = (UsageStatsManager)getSystemService(Service.USAGE_STATS_SERVICE);

        //get last "hrr" hrs usage stats
        int hrr=24;//specify hrs here
         map=mUsageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis()-(hrr*60*60*1000),System.currentTimeMillis());

        setNonDeleatable();//activate device admionistrator

        //seek overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkDrawOverlayPermission();
        }


        //initialize master lock from existing inference
        mLock=sharedpreferences.getBoolean("master lock state",false);


        requestUsageStatsPermission();

        //get a profile of all the apps installed in the phone
        PackageManager pm=getPackageManager();
        Intent tempIntent=new Intent("android.intent.action.MAIN");
        tempIntent.addCategory("android.intent.category.HOME");
        launcher=pm.resolveActivity(tempIntent,PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        editor.putString("launcher",launcher);//initiallize launcher name

        tempIntent=null;
        tempIntent=new Intent("android.intent.action.MAIN");
        tempIntent.addCategory(Intent.CATEGORY_APP_CONTACTS);
        contacts=pm.resolveActivity(tempIntent,PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        editor.putString("phone",contacts);//initiallize contacts name
        editor.commit();
        List<ApplicationInfo> packages=pm.getInstalledApplications(PackageManager.GET_META_DATA);

        final List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();

        for(ApplicationInfo app : packages) {
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                installedApps.add(app);
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                //in this case, it should be a user-installed app
                installedApps.add(app);
            }

            //else if (app.flags & ApplicationInfo.)
            else {
                installedApps.add(app);
            }
        }

         List<String> appList=new ArrayList<>();

        TheApp aApp=new TheApp();

        for(ApplicationInfo i:installedApps)
        {
            if (    ((String) i.loadLabel(pm)).equals(getResources().getString( R.string.app_name))
                    || ((String) i.loadLabel(pm)).equals(launcher)
                    ||   ((String) i.loadLabel(pm)).equals(contacts)
                    //add more here
                    ) continue;

                aApp=new TheApp();
                aApp.setName((String) i.loadLabel(pm));
                aApp.setIcon(i.loadIcon(pm));
                aApp.setPackageName(i.packageName);
                checkSetLock(aApp);

                appList.add((String)i.loadLabel(pm));

            if(map.containsKey(aApp.getPackageName()))
            {
                int ik= (int) map.get(aApp.getPackageName()).getTotalTimeInForeground();

                aApp.setForgroundTime(ik);
                Log.d("Forground time of "+aApp.getPackageName()," is "+ik);
            }
                theApp.add(aApp);


        }


        masterButton= (FloatingActionButton) findViewById(R.id.masterButton);

        if(mLock)
        {
            //playing
            masterButton.setImageResource(R.drawable.ic_pause_white_24dp);
        }
        else {
            //paused
            masterButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);

        }

        masterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLock)
                {
                    //playing
                    masterButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    mLock=!mLock;
                    Toast.makeText(getApplicationContext(),"Service paused",Toast.LENGTH_LONG).show();
                }
                else {
                    //pause
                    masterButton.setImageResource(R.drawable.ic_pause_white_24dp);
                    mLock=!mLock;
                    Toast.makeText(getApplicationContext(),"Service started",Toast.LENGTH_LONG).show();

                }
            }
        });

        ListView listView= (ListView) findViewById(R.id.list);
        final instAppArrayAdapter adapter=new instAppArrayAdapter(getApplicationContext(),appList,theApp);
        Log.d("Testing",""+"Seting adapter");
        listView.setAdapter(adapter);
        Log.d("Testing",""+"adapter set");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView status = (ImageView) view.findViewById(R.id.imageView2);
                TextView textView = (TextView) view.findViewById(R.id.textView);

                Log.d("Test",(String) textView.getText());
                Log.d("Test",""+lockedApp.contains(textView.getText().toString()));


                if(theApp.get(position).isLocked()) {
                    //app locked,unlock here
                    theApp.get(position).setLocked(false);

                    adapter.notifyDataSetChanged();
                }
                else {
                    //lock here
                    theApp.get(position).setLocked(true);
                    adapter.notifyDataSetChanged();
                    }


            }
        });
        progressDialog.dismiss();
    }


    void requestUsageStatsPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveList(theApp);

        if(mLock)
        {
            //play

            stopService(new Intent(getApplicationContext(),CurrentActivityService.class));
            startService(new Intent(getApplicationContext(),CurrentActivityService.class));

        }
        else {
            //pause
            stopService(new Intent(getApplicationContext(),CurrentActivityService.class));

        }


        Log.d("Stopped","Data Written");
        editor.putBoolean("edit",false);
        editor.commit();

        finishActivity(5);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveList(theApp);

        if(mLock)
        {
            //play

            stopService(new Intent(getApplicationContext(),CurrentActivityService.class));
            startService(new Intent(getApplicationContext(),CurrentActivityService.class));

        }
        else {
            //pause
            stopService(new Intent(getApplicationContext(),CurrentActivityService.class));

        }


        Log.d("Paused","Data Written");
        editor.putBoolean("edit",false);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void saveList(List<TheApp> list)
    {

        int q=1;
        for(TheApp app:list)
        {
            editor.putBoolean(app.getPackageName(),app.isLocked());
            if (app.isLocked())
            {
                editor.putString(""+q,app.getPackageName());
                        q++;
            }
        }
        editor.putInt("total locked",q-1);
        editor.putBoolean("master lock state",mLock);

        editor.commit();
        Log.d("Saving..........","Saved");
    }

    List<TheApp> getList(String s)
    {

        int q=1;
        List<TheApp> list=new ArrayList<>();
        sharedpreferences.getString(s,null);
        for (int i=1;i<=sharedpreferences.getInt("total",0);i++)
        {
            //list.add(sharedpreferences.getString(s+q,null));
            q++;
        }
        return list;
    }

    void checkSetLock(TheApp app)
    {
        String pack=app.getPackageName();

        Log.d("Testing",""+pack);

       Log.d("Testing.......",""+sharedpreferences.getBoolean(pack,false));

        if(sharedpreferences.getBoolean(pack,false) )
            {
                app.setLocked(true);


            }
            else app.setLocked(false);
    }
    void setNonDeleatable()
    {
        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(this, AdminReceiver.class);

        //request admin access
        if(!mDevicePolicyManager.isAdminActive(mAdminName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please activate device administrator so that the app can " +
                    "function properly.");
            startActivityForResult(intent, 5556);
        }
    }






    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {

        // Checks if app already has permission to draw overlays
        if (!Settings.canDrawOverlays(this)) {

            // If not, form up an Intent to launch the permission request
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

            // Launch Intent, with the supplied request code
            startActivityForResult(intent, 5675);
        }
    }
}