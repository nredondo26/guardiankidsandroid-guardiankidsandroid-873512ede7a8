package co.appguardian.peerfy.dtoResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActiveDemoResponse {


    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("detalle")
    @Expose
    private String detail;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
