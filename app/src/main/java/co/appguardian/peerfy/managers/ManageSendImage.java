package co.appguardian.peerfy.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.appguardian.peerfy.dtoResponse.ContentModeratorResponse;
import co.appguardian.peerfy.interfaces.InterfaceRetrofit;
import co.appguardian.peerfy.services.util.UtilFile;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by andres on 18/10/16.
 */
public class ManageSendImage {

    private Context context;
    private String urlMicrosoft = "https://eastus.api.cognitive.microsoft.com/";
    private String urlPeerfy = "http://admin.appguardian.co/";
    private String key = "b3550a183c054bdeb3556d05207a1d36";
    private String contentType = "image/png";

    public static double adultClassificationScoreMin = 0.60;

    private String shippingId;
    private String state;
    private Retrofit retrofit;

    public static final String TAG = "ScreenshotService";

    public ManageSendImage(Context context) {
        this.context = context;
    }

    public synchronized void sendImageContentModerator(final File imageFile, final double percentageSkin) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(urlMicrosoft)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), imageFile);

        InterfaceRetrofit retrofitIR = retrofit.create(InterfaceRetrofit.class);
        Call<ContentModeratorResponse> call = retrofitIR.contentModerator(
                requestFile,
                contentType, key);
        call.enqueue(new Callback<ContentModeratorResponse>() {
            @Override
            public void onResponse(Call<ContentModeratorResponse> call, Response<ContentModeratorResponse> response) {
                int code = response.code();
                if (code == 200) {
                    ContentModeratorResponse contentModeratorResponse = response.body();

                    Log.d(TAG, "AdultClassificationScore" + contentModeratorResponse.getAdultClassificationScore() + " classified " + contentModeratorResponse.getImageAdultClassified() );
                    if (adultClassificationScoreMin <= contentModeratorResponse.getAdultClassificationScore()) {
                        sendImage(imageFile, contentModeratorResponse, percentageSkin);
                    } else {
                        UtilFile.deleteFIleImage(imageFile);
                    }
                } else {
                    UtilFile.deleteFIleImage(imageFile);
                }
            }

            @Override
            public void onFailure(Call<ContentModeratorResponse> call, Throwable t) {
                Log.e(TAG, "AdultClassificationScore" + t.getLocalizedMessage());
                UtilFile.deleteFIleImage(imageFile);
            }
        });
    }

    private void sendImage(final File imageFile, ContentModeratorResponse contentModeratorResponse, double percentageSkin) {

        retrofit = new Retrofit.Builder()
                .baseUrl(urlPeerfy)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imagen", imageFile.getName(), requestFile);

        RequestBody skinDetectionScore = RequestBody.create(MediaType.parse("multipart/form-data"), percentageSkin + "");

        String id_dispositivo=marca_modelo_Id();
        RequestBody Id_dispo = RequestBody.create(MediaType.parse("multipart/form-data"), id_dispositivo );

        RequestBody adultClassificationScore = RequestBody.create(MediaType.parse("multipart/form-data"), contentModeratorResponse.getAdultClassificationScore() + "");

        InterfaceRetrofit retrofitIR = retrofit.create(InterfaceRetrofit.class);
        Call<ResponseBody> call = retrofitIR.sendImage(skinDetectionScore, adultClassificationScore,Id_dispo, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    ResponseBody responseBody = response.body();
                    UtilFile.deleteFIleImage(imageFile);
                    try {
                        Log.d(TAG, responseBody.string());

                    } catch (IOException e) {
                        Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                    }
                } else {
                    UtilFile.deleteFIleImage(imageFile);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                UtilFile.deleteFIleImage(imageFile);
            }
        });
    }

    public String marca_modelo_Id(){

        String id_dispositivo = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return id_dispositivo;

    }
}