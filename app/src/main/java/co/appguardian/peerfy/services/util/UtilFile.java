package co.appguardian.peerfy.services.util;

import android.util.Log;

import java.io.File;

public class UtilFile {

    public static final String TAG = "ScreenshotService";

    public static void deleteFIleImage(File imageFile) {
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
