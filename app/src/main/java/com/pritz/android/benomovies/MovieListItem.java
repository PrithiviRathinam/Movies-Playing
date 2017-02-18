package com.pritz.android.benomovies;

import static android.R.attr.value;

/**
 * Created by HP-PC on 10-02-2017.
 */

public class MovieListItem {
    private long mId;
    private String mTitle;
    private String mLanguage;
    private String mReleaseDate;

    private String mImgUrl;
    private String mOverview;
    private double mPopularity;



    public void setmLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public void setmImgUrl(String mImgUrl) {
        this.mImgUrl = mImgUrl;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public void setmPopularity(double mPopularity) {
        this.mPopularity = mPopularity;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof MovieListItem) {
           MovieListItem mov = (MovieListItem) object;
            String title = this.getTitle();
            long id = this.getId();
            if(title.equals(mov.getTitle()) && id == mov.getId()){
                return true;
            }else
            {
                return false;
            }
        }

        return false;
    }

    public MovieListItem(long vId, String vTitle){
        this.mId = vId;
        mTitle = vTitle;


    }

    public long getId(){
        return mId;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLanguage() {
        return mLanguage;
    }





    public String getImageUrl() {
        return mImgUrl;
    }

    public String getReleaseDate(){
        return mReleaseDate;
    }
}
