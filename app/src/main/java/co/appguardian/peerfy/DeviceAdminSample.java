package co.appguardian.peerfy;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by NestNet on 19/04/2018.
 */

public class DeviceAdminSample extends DeviceAdminReceiver {

    @Override
    public void onDisabled(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //Toast.makeText(context, "Desistalacion Desbloqueada", Toast.LENGTH_SHORT).show();
        super.onDisabled(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        // TODO Auto-generated method stub
       // Toast.makeText(context, "Desistalacion Bloqueada", Toast.LENGTH_SHORT).show();
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //Toast.makeText(context, "Desactivando Bloqueo", Toast.LENGTH_SHORT).show();
        return super.onDisableRequested(context, intent);
    }

}