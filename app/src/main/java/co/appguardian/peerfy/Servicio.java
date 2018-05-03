package co.appguardian.peerfy;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


public class Servicio extends  AccessibilityService{


    private static final int PERMISSION_REQUEST_CODE = 1;

    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo rowNode = event.getSource();
        if (rowNode == null) {
            return;
        }
        Log.d("node info: ",event.toString());
    }


    @Override
    protected void onServiceConnected() {
        //super.onServiceConnected();

        String cuenta=Mostrarpreferencia();
        if(cuenta.equals("Not_config")) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
    }

    @Override
    public void onInterrupt() {

    }

    public String Mostrarpreferencia()
    {
        SharedPreferences prefs = getSharedPreferences("Cuenta_number", MODE_PRIVATE);
        return prefs.getString("Number","Not_config");
    }

}
