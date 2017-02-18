package com.pritz.android.benomovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP-PC on 12-02-2017.
 */

public class Movie {
    private String mTagline;

    private String mBackdrop;

    private ArrayList<String> mProductionCompanies;

    private ArrayList<String> mProductionCountries;

    private ArrayList<String> mGenres;

    private String mOriginalTitle;

    private Integer mRuntime;

    private String mVideoId;

    private String homePage;

    private int mVoteCount;

    private double mAverage;

    private long mRevenue;

    private long mBudget;


    public String getmBackdrop() {
        return mBackdrop;
    }

    public Movie(String vOriginalTitle,
                 String vTagline, ArrayList<String> vProdComp, ArrayList<String> vProdCont, ArrayList<String> vGen,
                 Integer vRuntime, String vVidId, String home, int count, double average,
                 long vRevenue, long vBudget, String backdrop) {

        mTagline = vTagline;
        mProductionCompanies = vProdComp;
        mProductionCountries = vProdCont;
        mRevenue = vRevenue;
        mBudget = vBudget;
        mVoteCount = count;
        mAverage = average;
        mGenres = vGen;
        mRuntime = vRuntime;
        mVideoId = vVidId;
        homePage = home;
        mBackdrop = backdrop;

        mOriginalTitle = vOriginalTitle;

    }



    public double getmAverage() {
        return mAverage;
    }

    public long getmRevenue() {
        return mRevenue;
    }

    public long getmBudget() {
        return mBudget;
    }



    public List<String> getmProductionCompanies() {
        return mProductionCompanies;
    }

    public List<String> getmProductionCountries() {
        return mProductionCountries;
    }

    public List<String> getmGenres() {
        return mGenres;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public Integer getmRuntime() {
        return mRuntime;
    }

    public String getmTrailerUrl() {
        return mVideoId;
    }

    public String getHomePage() {
        return homePage;
    }

    public int getmVoteCount() {
        return mVoteCount;
    }





    public String getTagline(){
        return mTagline;
    }


}
