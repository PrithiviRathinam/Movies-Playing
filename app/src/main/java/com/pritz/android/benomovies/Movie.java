package com.pritz.android.benomovies;

/**
 * Created by HP-PC on 10-02-2017.
 */

public class Movie {
    private String mTitle;
    private String mLanguage;
    private Boolean mAdult;

    public Movie(String vTitle, String vLanguage,Boolean vAdult){
        this.mTitle = vTitle;
        this.mLanguage = vLanguage;
        this.mAdult = vAdult;
    }
    public String getTitle() {
        return mTitle;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public Boolean getAdult() {
        return mAdult;
    }
}
