package com.example.eliaschang8.tabsandnavdrawer.Modler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by csaper6 on 5/15/17.
 */

public class JSONParser extends AsyncTask<String, Void, String> {
    private static final String TAG = "TAG";
    private ArrayList<String> imageUrlList = new ArrayList<>();
    private String[] ActualURL = null;

    String postJSON = "";

    private ArrayList<PostItem>postsArray;
    Fragment fragmentActivity;
    ListView list;

    public JSONParser(Fragment fragmentActivity, ListView list){
        this.fragmentActivity = fragmentActivity;
        this.list = list;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            java.net.URL url = new java.net.URL(urls[0]);
            ActualURL = urls;
            URLConnection connection = url.openConnection();

            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = reader.readLine()) != null){
                postJSON += line;
                Log.d("onAsyncClass" , "Looping");
            }
            return postJSON;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("onAsyncClass" , "NOT WORKING dsfsdfsdfs");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        postsArray = new ArrayList<PostItem>();

        if(s != null){
            try {

                JSONArray postsJSON = null;

                if(postJSON.charAt(0) == '['){
                    postsJSON = new JSONArray(postJSON);
                } else {
                    postJSON = "[" + postJSON + "]";
                    postsJSON = new JSONArray(postJSON);
                }

                for(int i = 0; i < postsJSON.length(); i++){

                    JSONObject objectPost =  postsJSON.optJSONObject(i);

                    //Get the Title
                    JSONObject renderedTitle = objectPost.optJSONObject("title");
                    String title = renderedTitle.optString("rendered");
                    title = android.text.Html.fromHtml(title).toString();

                    //Get the excerpt
                    JSONObject renderedExcerpt = objectPost.optJSONObject("excerpt");
                    String excerpt = renderedExcerpt.optString("rendered");
                    excerpt = android.text.Html.fromHtml(excerpt).toString();

                    //Get the author
                    String name = "";
                    try{
                        JSONObject _embed = objectPost.optJSONObject("_embedded");
                        JSONArray array = _embed.optJSONArray("author");
                        JSONObject author = array.optJSONObject(0);
                        name = author.optString("name");
                    } catch (Exception e){
                        name = "Tiger Staff";
                    }


                    //Get the date
                    String date  = objectPost.getString("date");
                    date = "Date: " + date.substring(date.indexOf("-")+1, date.indexOf("T")) + "-" + date.substring(0, date.indexOf("-"));

                    //Get the JSON LINK
                    JSONObject _links = objectPost.getJSONObject("_links");
                    JSONArray self = _links.getJSONArray("self");
                    JSONObject slef_zero = self.getJSONObject(0);
                    String href = slef_zero.getString("href");


                    String imageString;
                    String featured;
                    //Comments are bad \(^-^)/
                    try{
                        JSONObject embeddedImageObject = objectPost.getJSONObject("_embedded");
                        JSONArray featureMedia = embeddedImageObject.getJSONArray("wp:featuredmedia");
                        JSONObject imageDetails = featureMedia.getJSONObject(0);
                        JSONObject mediaDetailObject = imageDetails.getJSONObject("media_details");
                        JSONObject sizes = mediaDetailObject.getJSONObject("sizes");
                        JSONObject thumbnailImageVersion2 = sizes.optJSONObject("medium");
                        imageString = thumbnailImageVersion2.getString("source_url");
                    } catch (Exception e) {
                        Log.d("IMAGES", "Not working");
                        imageString = "http://tigernewspaper.com/wordpress/wp-content/uploads/2017/07/TigerPlaceholderImage.jpg";
                    }
                    featured =  "http://tigernewspaper.com/wordpress/wp-content/uploads/2017/07/TigerPlaceholderImage.jpg";

                    JSONObject content = objectPost.optJSONObject("content");
                    String contentRendered = content.optString("rendered");
                    //contentRendered = android.text.Html.fromHtml(contentRendered).toString();

                    //get new JSON link
                    //JSONObject renderedLink = objectPost.optJSONObject("link");
                    //String link = renderedLink.optString("rendered");
                    //link = android.text.Html.fromHtml(link).toString();

                    String link = objectPost.getString("link");
                    String fetchingTitle = objectPost.getString("title");

                    PostItem currentPost = new PostItem(title, excerpt, name, date, imageString, contentRendered, featured, link, href);

                    postsArray.add(currentPost);

                    //Log.d(TAG, "onPostExecute: " + ImgURL);
                }

                fillList();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void fillList(){
        PostAdapter adapter = new PostAdapter(fragmentActivity.getActivity(), postsArray);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(fragmentActivity.getActivity(), "" + postsArray.get(position).getContent(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(fragmentActivity.getActivity(), ArticlePage.class);
                Bundle bundle = new Bundle();
                bundle.putString("URL", ActualURL[0]);
                bundle.putString("TITLE", postsArray.get(position).getTitle());
                bundle.putString("DATE", postsArray.get(position).getDate());
                bundle.putString("AUTHOR", postsArray.get(position).getAuthor());
                bundle.putString("CONTENT", postsArray.get(position).getContent());
                bundle.putString("FEATURED", postsArray.get(position).getFeaturedImage());
                bundle.putString("LINK", postsArray.get(position).getLink());
                bundle.putString("HREF",postsArray.get(position).getHref());
                intent.putExtras(bundle);

                fragmentActivity.startActivity(intent);
            }
        });
    }
}

