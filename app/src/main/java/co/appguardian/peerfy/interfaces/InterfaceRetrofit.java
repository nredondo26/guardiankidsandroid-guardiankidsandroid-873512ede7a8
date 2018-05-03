package co.appguardian.peerfy.interfaces;

import co.appguardian.peerfy.dtoResponse.ActiveDemoResponse;
import co.appguardian.peerfy.dtoResponse.ContentModeratorResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface InterfaceRetrofit {

    @Headers({"Content-Type: image/png", "Ocp-Apim-Subscription-Key: b3550a183c054bdeb3556d05207a1d36"})
    @POST("contentmoderator/moderate/v1.0/ProcessImage/Evaluate")
    Call<ContentModeratorResponse> contentModerator(
            @Body RequestBody image,
            @Header("Content-Type") String contentType,@Header("Ocp-Apim-Subscription-Key") String ocpApimSubscriptionKey);

    @Multipart
    @POST("captures_Android/uploads.php")
    Call<ResponseBody> sendImage(
            @Part("piel") RequestBody skinDetectionScore,
            @Part("porno") RequestBody adultClassificationScore,
            @Part("id_dispo") RequestBody Id_dispo,
            @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("newsite/android/lib/balida-licencia.php")
    Call<ActiveDemoResponse> getActiveDemo(
            @Field("id_telefono") String id);

}
