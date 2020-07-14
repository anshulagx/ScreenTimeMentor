package android.Mentor;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by ANSHUL on 14-10-2017.
 */

public class Analizer {
    void abcd(Context context) {


            long TimeInforground = 500;
            int minutes = 500, seconds = 500, hours = 500;
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);



        if (stats != null) {


                for (UsageStats usageStats1 : stats) {

                    TimeInforground = usageStats1.getTotalTimeInForeground();

                    String PackageName = usageStats1.getPackageName();

                    minutes = (int) ((TimeInforground / (1000 * 60)) % 60);

                    seconds = (int) (TimeInforground / 1000) % 60;

                    hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);

                    Log.i("BAC", "PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");

                }


            }
        }

    }
