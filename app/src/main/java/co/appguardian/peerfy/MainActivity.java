package co.appguardian.peerfy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.appguardian.peerfy.services.util.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SCREENSHOT = 59706;
    private MediaProjectionManager mgr;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 321;
    private Button btnStartService;
    EditText numbrer_indicativo,numbrer_tele;
    String indicativo, telefono,marca,iddispositivo;
    private static final String URL_MENSAJE = "http://appguardian.co/newsite/android/lib/guarda-cuenta.php";
    private RequestQueue rq;
    private PolicyManager policyManager;
    private boolean clickBtn = false;
    final Context contexto = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String cuenta = Mostrarpreferencia();
        if (!cuenta.equals("Not_config")) finish();

        policyManager = new PolicyManager(this);
        numbrer_indicativo = findViewById(R.id.txt_numbrer_indicativo) ;
        numbrer_tele = findViewById(R.id.txt_numbrer_tele) ;
        btnStartService = findViewById(R.id.btn_start_service);
        rq = Volley.newRequestQueue(getApplicationContext());

       checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }



    public void vesionAndroid() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }


    public String Mostrarpreferencia()
    {
        SharedPreferences prefs = getSharedPreferences("Cuenta_number",this.MODE_PRIVATE);
        String valor = prefs.getString("Number","Not_config");
        return valor;
    }
    private void enviar(final View view){
        StringRequest str = new StringRequest(Request.Method.POST, URL_MENSAJE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean estado = jsonObject.getBoolean("success");
                            String mensaje = jsonObject.getString("detalle");

                            if (estado) {

                                SharedPreferences prefs = getSharedPreferences("Cuenta_number", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("Number", "Config");
                                editor.commit();
                                btnStartService(view);

                                /*btnStartService(view);
                                vesionAndroid();
                                if (!policyManager.isAdminActive()) {
                                    alert_dialog_activaAdmin();
                                }
                                    */
                                }
                            else {
                                alert_dialog(mensaje);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error->", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("numero_telefono", telefono);
                parametros.put("code_pais", indicativo);
                parametros.put("marca_dispositivo", marca);
                parametros.put("id_dispositivo", iddispositivo);
                return parametros;

            }
        };
        rq.add(str);
    }

    private void alert_dialog(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setTitle("SAFESON ERROR")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void alert_dialog_activaAdmin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Es necesario activar la  aplicación como administrador para proteger de la desinstalación")
                .setTitle("Activar administrador")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (!policyManager.isAdminActive()) {

                            Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, policyManager.getAdminComponent());
                            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "After activating admin, you will be able to block application uninstallation.");
                            startActivityForResult(activateDeviceAdmin, PolicyManager.DPM_ACTIVATION_REQUEST_CODE);

                            dialog.cancel();
                            finish();

                        }

                    }
                });
        builder.show();
    }

    @SuppressLint("HardwareIds")
    public void marca_modelo_Id(){
        marca= Build.BRAND +" "+ Build.MODEL;
        iddispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    private boolean checkPermission(String permission) {

        if (ContextCompat.checkSelfPermission(this,
                permission) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(this, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                    permission, true);
            return false;
        }else{
            return true;
        }

    }

    private void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_SCREENSHOT);
        } else {
            //TODO add implementation lower than 5.0 Android
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCREENSHOT) {
            if (resultCode == RESULT_OK) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent i = new Intent(this, ScreenshotService.class)
                            .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                            .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);
                    startService(i);
                }
            }
        }
    }

    public void btnStartService(View view) {
        clickBtn = true;
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startService();
        }
    }



    public void ValidarDatos(View view) {
       // Toast.makeText(getApplicationContext(),"Hola nelson",Toast.LENGTH_LONG).show();
        //numbrer_indicativo,numbrer_tele
        telefono= numbrer_tele.getText().toString().trim();
        indicativo= numbrer_indicativo.getText().toString().trim();

        if(telefono.equals("")||indicativo.equals("")){
            Toast.makeText(getApplicationContext(),"Campos Vacíos",Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(getApplicationContext(),"Llenos",Toast.LENGTH_LONG).show();
            marca_modelo_Id();
            enviar(view);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (clickBtn) {
                        startService();
                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
//                    PermissionUtils.PermissionDeniedDialog.newInstance(true)
//                            .show(getSupportFragmentManager(), "dialog");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}




