package co.appguardian.peerfy.managers;

import android.content.Context;
import android.content.SharedPreferences;

import co.appguardian.peerfy.services.util.Util;

public class SharedPreferencesManager {
    private static final String SHARED_PREFS_FILE = "peerfyPrefs";
    private static final String KEY_DATE_DEMO = "dateDemo";
    private static final String KEY_IS_ACTIVE_DEMO = "isActiveDemo";

    private Context mContext;

    public SharedPreferencesManager(Context context){
        mContext = context;
    }
    private SharedPreferences getSettings(){
        return mContext.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
    }

    public Boolean getDateDemo(){

        Long lastTime = getSettings().getLong(KEY_DATE_DEMO,0);
        boolean stated = !Util.isToday(lastTime);
        return stated;
    }

    public void setDateDemo(){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putLong(KEY_DATE_DEMO, System.currentTimeMillis());
        editor.apply();
    }

    public Boolean isActiveDemo(){
        return getSettings().getBoolean(KEY_IS_ACTIVE_DEMO, Boolean.TRUE);
    }

    public void setActiveDemo(Boolean active){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(KEY_IS_ACTIVE_DEMO, active);
        editor.apply();
    }


}
