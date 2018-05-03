package co.appguardian.peerfy.services.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.appguardian.peerfy.activities.ServiceStarterActivity;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        startServicesStarterActivity(context);
    }
    private void startServicesStarterActivity(Context context) {
        Intent ui = new Intent(context, ServiceStarterActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(ui);
    }
}