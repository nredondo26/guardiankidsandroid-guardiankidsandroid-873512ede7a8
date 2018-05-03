package co.appguardian.peerfy;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import co.appguardian.peerfy.activities.ServiceStarterActivity;
import co.appguardian.peerfy.managers.ManageSendImage;
import co.appguardian.peerfy.managers.ManagerActiveDemo;
import co.appguardian.peerfy.managers.SharedPreferencesManager;
import co.appguardian.peerfy.services.util.SkinFilter;
import co.appguardian.peerfy.services.util.UtilFile;

import static co.appguardian.peerfy.services.util.SkinFilter.totalThreshold;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenshotService extends Service {

    public static final String EXTRA_RESULT_CODE = "resultCode";
    public static final String EXTRA_RESULT_INTENT = "resultIntent";

    static final int VIRT_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private MediaProjection projection;
    private VirtualDisplay vdisplay;
    final private HandlerThread handlerThread =
            new HandlerThread(getClass().getSimpleName(),
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler handler;
    private MediaProjectionManager mgr;
    private WindowManager wmgr;
    private ImageTransmogrifier it;
    private int resultCode;
    private Intent resultData;
    private ManageSendImage manageSendImage;
    private PowerManager powerManager;
    private SharedPreferencesManager sharedPreferencesManager;

    private int k = 0;
    private int knum = 0;
    private int time = 30000; // time in milliseconds
    //vars file

    private static String imageName = "screenshot.png";
    private String nameTime;
    private String previousNameTime = "";

    private SkinFilter skinFilter;

    public static final String TAG = "ScreenshotServicePeerfy";

    @Override
    public void onCreate() {
        super.onCreate();

        mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        wmgr = (WindowManager) getSystemService(WINDOW_SERVICE);
        skinFilter = new SkinFilter();
        manageSendImage = new ManageSendImage(this);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        sharedPreferencesManager = new SharedPreferencesManager(this);

        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {

        if (i != null) {
            resultCode = i.getIntExtra(EXTRA_RESULT_CODE, 1337);
            resultData = i.getParcelableExtra(EXTRA_RESULT_INTENT);

            if (resultData != null) {
                startCapture();
                periodicallyTakeScreenShot();
            } else {
                startServicesStarterActivity();
            }
        } else {
            startServicesStarterActivity();
        }

        return (START_STICKY);
    }

    private void startServicesStarterActivity() {
        Intent ui = new Intent(this, ServiceStarterActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(ui);
    }

    private void periodicallyTakeScreenShot() {
        //TODO descomment for stop service
        //if (sharedPreferencesManager.isActiveDemo()) {
            Handler festHandler = new Handler();
            festHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (powerManager.isInteractive()) {

                        isActiveDemo();

                        //TODO remove if for stop service
                        if (sharedPreferencesManager.isActiveDemo()) {
                            startCapture();
                        }
                    }
                    periodicallyTakeScreenShot();
                    Log.d("numScreenShots = ", k + "");
                }
            }, time);
            //TODO descomment for stop service
            /*else {
           stopSelf();
           }*/

    }

    private void isActiveDemo() {
        if (sharedPreferencesManager.getDateDemo()) {
            ManagerActiveDemo managerActiveDemo = new ManagerActiveDemo(this);
            managerActiveDemo.isActiveDemo();
        }
    }

    @Override
    public void onDestroy() {
        stopCapture();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Binding not supported. Go away.");
    }

    WindowManager getWindowManager() {
        return (wmgr);
    }

    Handler getHandler() {
        return (handler);
    }

    void processImage(final byte[] png) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (!nameTime.equals(previousNameTime)) {

                        previousNameTime = nameTime;
                        imageName = "screenshot" + nameTime + ".png";
                        File imageFile = new File(getFilesDir(), imageName);

                        copyFile(png, imageFile);
                        skinDetection(png, imageFile);
                    }
                } catch (IOException e) {
                    if (e != null) {
                        if (e.getLocalizedMessage() != null) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    }
                }
            }
        }.start();
        stopCapture();
    }

    private void skinDetection(byte[] png, File imageFile) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(png, 0, png.length);

        Log.d("timepeerfy_start", "time");
        double percentage = skinFilter.skinDetector(bitmap);
        Log.d("timepeerfy_end", "time");

        if (percentage >= totalThreshold) {
            if (imageFile != null) {

                manageSendImage.sendImageContentModerator(imageFile, percentage);
            }
        } else {
            UtilFile.deleteFIleImage(imageFile);
        }
    }

    private void copyFile(byte[] png, File imageFile) throws IOException {

        OutputStream myOutput = new FileOutputStream(imageFile);

        byte[] buffer = new byte[1024];
        int length;

        myOutput.write(png);
        knum++;
        Log.d(TAG, knum + "  screenshot" + nameTime);

        myOutput.flush();
        myOutput.close();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    private void stopCapture() {
        if (projection != null) {
            projection.stop();
            vdisplay.release();
            projection = null;
        }
    }

    private void startCapture() {

        k++;
        createName();
        try {

            projection = mgr.getMediaProjection(resultCode, resultData);
            it = new ImageTransmogrifier(this);

            MediaProjection.Callback cb = new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    vdisplay.release();
                }
            };

            vdisplay = projection.createVirtualDisplay("peerfy",
                    it.getWidth(), it.getHeight(),
                    getResources().getDisplayMetrics().densityDpi,
                    VIRT_DISPLAY_FLAGS, it.getSurface(), null, handler);
            projection.registerCallback(cb, handler);
        } catch (Exception e) {
            if (e != null) {
                if (e.getLocalizedMessage() != null) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void createName() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String dateFormat = "dd-MM-yyyy hh:mm:ss";
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        nameTime = formatter.format(calendar.getTime());
    }

    private PendingIntent buildPendingIntent(String action) {
        Intent i = new Intent(this, getClass());

        i.setAction(action);

        return (PendingIntent.getService(this, 0, i, 0));
    }
}