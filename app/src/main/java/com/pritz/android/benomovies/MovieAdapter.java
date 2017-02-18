package com.pritz.android.benomovies;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;

/**
 * Created by HP-PC on 10-02-2017.
 */

public class MovieAdapter extends ArrayAdapter<MovieListItem> {

    public MovieAdapter(Context context, ArrayList<MovieListItem> moviedata) {
        super(context, 0, moviedata);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.movie_list_item, parent, false);
        }
        MovieListItem list_movieListItem = getItem(position);
        TextView title = (TextView)listItem.findViewById(R.id.movie_title);
        TextView lang = (TextView)listItem.findViewById(R.id.movie_release_date);
        ImageView img = (ImageView) listItem.findViewById(R.id.movie_image);
        TextView overview = (TextView)listItem.findViewById(R.id.movie_overview);

        TextView popularityView = (TextView) listItem.findViewById(R.id.movie_popularity_value);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormatter.parse(list_movieListItem.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateFormatter = new SimpleDateFormat("MMM yyyy",new Locale("en"));
        String dateString = dateFormatter.format(date);
        DecimalFormat decimalFormatter = new DecimalFormat("0.0");
        double popular = list_movieListItem.getPopularity();
        String formPopularity = decimalFormatter.format(popular);
       GradientDrawable gradientDrawable = (GradientDrawable)popularityView.getBackground();

        int magColor = 0;


        if(popular >= 70.0){
            magColor = R.color.pop1;
        }else if(popular >= 40 && popular < 70){
            magColor = R.color.pop2;
        }
        else if(popular < 40){
            magColor = R.color.pop3;
        }
        int mColor = ContextCompat.getColor(getContext(),magColor);
        gradientDrawable.setColor(mColor);
        popularityView.setText(formPopularity);
        if(!(list_movieListItem.getImageUrl().equals(""))) {
            Picasso.with(getContext())
                    .load(list_movieListItem.getImageUrl())
                    .placeholder(R.drawable.final_placeholder)   // optional
                    .resize(100, 100)
                    // optional

                    .into(img);
        }

            title.setText(list_movieListItem.getTitle());



        lang.setText(dateString);
        String overviewString = list_movieListItem.getOverview().trim();

            overview.setText(overviewString);

        if(overviewString.equals("")){
            overview.setText(R.string.no_overview);
        }
        return listItem;
    }
}
