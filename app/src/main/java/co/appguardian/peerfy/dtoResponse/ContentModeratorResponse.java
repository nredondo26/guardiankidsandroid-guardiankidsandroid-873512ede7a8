package co.appguardian.peerfy.dtoResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentModeratorResponse {

    @SerializedName("AdultClassificationScore")
    @Expose
    private Double adultClassificationScore;

    @SerializedName("IsImageAdultClassified")
    @Expose
    private Boolean isImageAdultClassified;

    @SerializedName("RacyClassificationScore")
    @Expose
    private Double racyClassificationScore;

    @SerializedName("IsImageRacyClassified")
    @Expose
    private Boolean isImageRacyClassified;

    @SerializedName("TrackingId")
    @Expose
    private String trackingId;

    public Double getAdultClassificationScore() {
        return adultClassificationScore;
    }

    public void setAdultClassificationScore(Double adultClassificationScore) {
        this.adultClassificationScore = adultClassificationScore;
    }

    public Boolean getImageAdultClassified() {
        return isImageAdultClassified;
    }

    public void setImageAdultClassified(Boolean imageAdultClassified) {
        isImageAdultClassified = imageAdultClassified;
    }

    public Double getRacyClassificationScore() {
        return racyClassificationScore;
    }

    public void setRacyClassificationScore(Double racyClassificationScore) {
        this.racyClassificationScore = racyClassificationScore;
    }

    public Boolean getImageRacyClassified() {
        return isImageRacyClassified;
    }

    public void setImageRacyClassified(Boolean imageRacyClassified) {
        isImageRacyClassified = imageRacyClassified;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
}
