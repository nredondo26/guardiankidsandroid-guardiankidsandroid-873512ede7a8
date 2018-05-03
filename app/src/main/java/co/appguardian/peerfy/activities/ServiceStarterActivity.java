package co.appguardian.peerfy.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import co.appguardian.peerfy.MainActivity;
import co.appguardian.peerfy.ScreenshotService;

public class ServiceStarterActivity extends Activity {

    private static final int REQUEST_SCREENSHOT=59702;
    private MediaProjectionManager mgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            startService();
        }
    }

    private boolean checkPermission(String permission) {

        if (ContextCompat.checkSelfPermission(this,
                permission) != PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            return false;
        }else{
            return true;
        }
    }

    private void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_SCREENSHOT);
        }else{
            //TODO add implementation lower than 5.0 Android
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_SCREENSHOT) {
            if (resultCode==RESULT_OK) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Intent i = new Intent(this, ScreenshotService.class)
                            .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                            .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);
                    startService(i);
                }
            }
        }
        finish();
    }
}




