package co.appguardian.peerfy.managers;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import co.appguardian.peerfy.dtoResponse.ActiveDemoResponse;
import co.appguardian.peerfy.interfaces.InterfaceRetrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jorge on 4/07/2017.
 */

public class ManagerActiveDemo {

    private String urlPeerfy = "http://appguardian.co/";
    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    public static final String TAG = "StateDemoPerfy";

    public ManagerActiveDemo(Context context){
        this.context = context;
        sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    public void isActiveDemo() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlPeerfy)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        InterfaceRetrofit retrofitIR = retrofit.create(InterfaceRetrofit.class);

        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        Call<ActiveDemoResponse> call = retrofitIR.getActiveDemo(id);

        call.enqueue(new Callback<ActiveDemoResponse>() {
            @Override
            public void onResponse(Call<ActiveDemoResponse> call, Response<ActiveDemoResponse> response) {

                if (response.body()!=null) {
                    sharedPreferencesManager.setDateDemo();
                    ActiveDemoResponse activeDemoResponse = response.body();
                    sharedPreferencesManager.setActiveDemo(activeDemoResponse.getSuccess());

                    Log.i(TAG, activeDemoResponse.getDetail());
                }
            }

            @Override
            public void onFailure(Call<ActiveDemoResponse> call, Throwable t) {
                if (t!=null) {
                    if (t.getLocalizedMessage()!=null) {
                        Log.e(TAG, t.getLocalizedMessage());
                    }
                }
            }
        });
    }
}
