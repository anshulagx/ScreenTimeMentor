package android.Mentor;

import android.graphics.drawable.Drawable;

/**
 * Created by ANSHUL on 11-10-2017.
 */

public class TheApp {
    private String name;
    private String packageName;
    private Drawable icon;
    private boolean isLocked;

    public long getForgroundTime() {
        return forgroundTime;
    }

    public void setForgroundTime(long forgroundTime) {
        this.forgroundTime = forgroundTime;
    }

    public int getMinute() {
       int   minutes=(int) ((forgroundTime / (1000 * 60)) % 60);

        return minutes;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        int hours = (int) ((forgroundTime / (1000 * 60 * 60)) % 24);
        return hours;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getSecond() {
        int   secs=(int) ((forgroundTime / (1000 *60))%60 );
        return secs;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    private long forgroundTime;
    private  int minute,hour,second;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }


    public TheApp(String name, String packageName, Drawable icon, boolean isLocked, long forgroundTime, int minute, int hour, int second) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.isLocked = isLocked;
        this.forgroundTime = forgroundTime;
        this.minute = minute;
        this.hour = hour;
        this.second = second;
    }

    TheApp()

    {
        isLocked=false;
        forgroundTime=0;
        minute=0;
        hour=0;
        second=0;
    }
    TheApp(String packageName)
    {
        this.packageName=packageName;
    }

}
