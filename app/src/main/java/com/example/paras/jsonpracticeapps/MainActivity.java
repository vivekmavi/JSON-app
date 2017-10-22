package com.example.paras.jsonpracticeapps;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import models.MovieModel;

public class MainActivity extends AppCompatActivity
{
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView_main);

 DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
 ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
 ImageLoader.getInstance().init(config);

    }

    public class JSONTask extends AsyncTask<String,String,List<MovieModel>>
    {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected List<MovieModel> doInBackground(String... urlString)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String jsonResponse = null ;
            String dataForTextView = null;
            List<MovieModel> movieModelList = null;

            try
            {
                URL url = new URL(urlString[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer stringBuffer = new StringBuffer();

                String line = "";


                while((line=bufferedReader.readLine())!=null)
                {
                    stringBuffer.append(line);
                }

               jsonResponse = stringBuffer.toString();

                JSONObject jsonParentObject = new JSONObject(jsonResponse);
                JSONArray  jsonArray = jsonParentObject.getJSONArray("movies");
//                StringBuffer stringBufferForTextView = new StringBuffer();

                movieModelList = new ArrayList<>();

                Gson gson = new Gson();

                for( int i=0; i<jsonArray.length(); i++)
                {
                    JSONObject jsonObjectFinal = jsonArray.getJSONObject(i);
                    MovieModel movieModel = gson.fromJson(jsonObjectFinal.toString(),MovieModel.class);



//                    movieModel.setMovie(jsonObjectFinal.getString("movie"));
//                    movieModel.setYear(jsonObjectFinal.getInt("year"));
//                    movieModel.setRating((float) jsonObjectFinal.getDouble("rating"));
//                    movieModel.setDuration(jsonObjectFinal.getString("duration"));
//                    movieModel.setDirector(jsonObjectFinal.getString("director"));
//                    movieModel.setTagline(jsonObjectFinal.getString("tagline"));
//                    movieModel.setImage(jsonObjectFinal.getString("image"));
//                    movieModel.setStory(jsonObjectFinal.getString("story"));
//
//                 List<MovieModel.CastArrayClass> cast = new ArrayList<>();
//
//                 for (int j=0 ; j<jsonObjectFinal.getJSONArray("cast").length(); j++)
//                 {
//                  JSONObject jsonCastObject = jsonObjectFinal.getJSONArray("cast").getJSONObject(j);
//                  MovieModel.CastArrayClass castArrayClass = new MovieModel.CastArrayClass();
//                  castArrayClass.setName(jsonCastObject.getString("name"));
//                  cast.add(castArrayClass);
//                 }
//
//                 movieModel.setCast(cast);

                 movieModelList.add(movieModel);
                }

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(urlConnection!=null)
                {
                    urlConnection.disconnect();
                }
                if(bufferedReader!=null)
                {
                    try
                    {
                        bufferedReader.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            return movieModelList;
        }

        @Override
        protected void onPostExecute(List<MovieModel> movieModelList)
        {

MovieAdapter movieAdapter = new MovieAdapter(getApplicationContext(),R.layout.custom_list_view_row,movieModelList) ;
            listView.setAdapter(movieAdapter);
            super.onPostExecute(movieModelList);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
       if(item.getItemId()==R.id.loadJsonMenuItem)
       {
           String jsonUrl="https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesData.txt";

           JSONTask jsonTask_object = new JSONTask();
           jsonTask_object.execute(jsonUrl);
       }
        return super.onOptionsItemSelected(item);
    }

public class MovieAdapter extends ArrayAdapter
{
    private List<MovieModel> movieModelList2 ;
    private int resource ;
    private LayoutInflater inflater ;

    public MovieAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<MovieModel> movieModelList1)
    {
        super(context, resource, movieModelList1);
        movieModelList2 = movieModelList1 ;
        this.resource = resource ;
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder viewHolder = null;

        if(convertView==null)
        {
//            convertView = inflater.inflate(R.layout.custom_list_view_row,null);
//            both are same observe carefully
            convertView = inflater.inflate(resource,null);

            viewHolder = new ViewHolder();

            viewHolder.tvMovieName = (TextView)     convertView.findViewById(R.id.movieNameTV);
            viewHolder.tvTagLine   = (TextView)     convertView.findViewById(R.id.tagLineTV);
            viewHolder.tvYear      = (TextView)     convertView.findViewById(R.id.yearTV);
            viewHolder.tvDuration  = (TextView)     convertView.findViewById(R.id.durationTV);
            viewHolder.tvDirector  = (TextView)     convertView.findViewById(R.id.directorTV);
            viewHolder.tvCast      = (TextView)     convertView.findViewById(R.id.castTV);
            viewHolder.tvStory     = (TextView)     convertView.findViewById(R.id.storyTV);
            viewHolder.ratingBar   = (RatingBar)    convertView.findViewById(R.id.ratingBar);
            viewHolder.ivMovie     = (ImageView)    convertView.findViewById(R.id.movieIV);
            convertView.setTag(viewHolder);

        } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

        final ProgressBar progressBar;


        progressBar = (ProgressBar)  convertView.findViewById(R.id.progressBar);

        viewHolder.tvMovieName.setText(movieModelList2.get(position).getMovie());
        viewHolder.tvTagLine.setText(movieModelList2.get(position).getTagline());
        viewHolder.tvYear.setText("year : "+movieModelList2.get(position).getYear());
        viewHolder.tvDuration.setText("Duration : "+movieModelList2.get(position).getDuration());
        viewHolder.tvDirector.setText("Director : "+movieModelList2.get(position).getDirector());
        viewHolder.tvStory.setText(movieModelList2.get(position).getStory());

        StringBuffer bufferForCast = new StringBuffer();

        for( MovieModel.CastArrayClass castArrayClass : movieModelList2.get(position).getCast())
        {
            bufferForCast.append(castArrayClass.getName()+" , ");
        }

        viewHolder.tvCast.setText("cast : "+bufferForCast);

        viewHolder.ratingBar.setRating((movieModelList2.get(position).getRating()/2));

        String imageUrl = movieModelList2.get(position).getImage();
        ImageLoader.getInstance().displayImage(imageUrl, viewHolder.ivMovie, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
            {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view)
            {
                progressBar.setVisibility(View.GONE);
            }
        });

        return convertView;
    }

    class ViewHolder
    {
        // so that there is no need to find views for each ListView Row that is loaded

        private TextView tvMovieName;
        private TextView tvTagLine;
        private TextView tvYear;
        private TextView tvDuration;
        private TextView tvDirector;
        private TextView tvCast;
        private TextView tvStory;
        private RatingBar ratingBar;
        private ImageView ivMovie ;
    }
}

}

