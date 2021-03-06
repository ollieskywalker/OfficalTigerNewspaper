package com.example.eliaschang8.tabsandnavdrawer.Modler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.eliaschang8.tabsandnavdrawer.R;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/*
Works! Opens. Don't click on first link
*/
public class ArticlePage extends AppCompatActivity {

    ArrayList<String> pictureArray = new ArrayList<String>();
    TextView articleContent, date, authorName, title;
    ScrollView contentScrollView;

    private FABToolbarLayout layout;
    private View fab;
    private LinearLayout toolbar;
    ImageView shareButton, saveButton;
    private boolean checked = false;
    private String URL, linkUrl, titleToShare;
    private String href;

    public SharedPreferences.Editor editor;

    public static String SAVED_ARTICLE_KEY = "1234";

    CustomPagerAdapter adapter;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articlepage);
        wireWidgets();
        initialize();
        setOnClickListeners();

        Bundle extras = getIntent().getExtras();
        String titleValue = extras.getString("TITLE");
        title.setText(titleValue);
        String contentValue = extras.getString("CONTENT");

        String featuredImage = extras.getString("FEATURED");
        pictureArray.add(featuredImage);
        URL = extras.getString("URL");

        int http;
        int a = 0;
        while (a < contentValue.length()) {
            http = contentValue.indexOf("http://tigernewspaper.com/wordpress/wp-content/", a);
            if (http == -1)
                break;
            String urlOne = contentValue.substring(http, contentValue.indexOf(".jpg", http) + 4);
            //Log.d("HEEEEEEEEEEEYYYYYYYYYYY", urlOne);
            pictureArray.add(urlOne);
            a = http + urlOne.length();
        }

        Intent intent = new Intent(ArticlePage.this, CustomPagerAdapter.class);
        intent.putExtra("imageCount", pictureArray.size());

        Log.d("NUMBER OF IMAGES:", "" + pictureArray.size());

        //extract from Bundle
        contentValue = android.text.Html.fromHtml(contentValue).toString();
        articleContent.setText(contentValue);
        String authorValue = extras.getString("AUTHOR");
        authorName.setText(authorValue);
        String dateValue = extras.getString("DATE");
        date.setText(dateValue);
        linkUrl = extras.getString("LINK");
        titleToShare = extras.getString("TITLE");
        href = extras.getString("HREF");

        editor = getSharedPreferences(SAVED_ARTICLE_KEY, MODE_PRIVATE).edit();
    }

    private void wireWidgets() {
        articleContent=(TextView)findViewById(R.id.textView_article_content);
        date=(TextView)findViewById(R.id.textView_date);
        authorName=(TextView)findViewById(R.id.textView_writer_name);
        title=(TextView)findViewById(R.id.textView_article_title);
        contentScrollView=(ScrollView) findViewById((R.id.scrollView_screen));
        shareButton = (ImageView) findViewById(R.id.share);
        saveButton = (ImageView) findViewById(R.id.save);
        layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        fab = findViewById(R.id.fabtoolbar_fab);
        toolbar = (LinearLayout) findViewById(R.id.fabtoolbar_toolbar);
//
    }
    private void initialize() {
        adapter = new CustomPagerAdapter(getSupportFragmentManager(), this.getApplicationContext());
        adapter.sendImages(pictureArray);
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        //loadUrls();//good

    } private void setOnClickListeners() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*Toast.makeText(ArticlePage.this, "Shared!", Toast.LENGTH_SHORT).show();
               Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
               vibrator.vibrate(500);*/
                shareIt();
            }

            private void shareIt() {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody= linkUrl; //todo put URL in here
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, titleToShare); //TODO change subject
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via")); //can change this string if
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            public static final String TAG = "ON SAVED CLICK:";

            @Override
            public void onClick(View v) {

                Log.d(TAG, "hi");
                Log.d(TAG, href);

                Intent i = new Intent(getBaseContext(), SavedActivity.class);
                i.putExtra("URLjson", href);
                startActivity(i);

                //*** Yohans old code
//                Bundle extras = getIntent().getExtras();
//                String title = extras.getString("TITLE");
//                String date = extras.getString("DATE");
//                String author = extras.getString("AUTHOR");
//                String content = extras.getString("CONTENT");
//                String featured = extras.getString("FEATURED");
//                String link = extras.getString("LINK");
//                String jsonFile = loadJSONFromAsset();
//                Boolean check = false;
//                SaveToJson saveToJson = new SaveToJson(jsonFile, title, date, author, content, featured, link);
//                try {
//                    if (saveToJson.saveToJson()) {
//                        check = true;
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (check) {
//                    Toast.makeText(ArticlePage.this, "article content saved", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(ArticlePage.this, "FAILED!!@!@!@!@!@!@!@", Toast.LENGTH_SHORT).show();
//                }

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.show();
                checked = true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (checked)
        {
            layout.hide();
            checked = false;
        }
        else if (checked != true)
        {
            super.onBackPressed();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("savedArticle.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

//    private void loadUrls() { //leave this alone
//        urls = new ArrayList<String>();
//
//        //for (int i = 0; i < urlList.size(); i++)
//        //{
//         //   urls.add(urlList.get(i));
//       //}
//       urls.add("http://tigernewspaper.com/wordpress/wp-content/uploads/2017/05/DSC_0171.jpg");
//       urls.add("http://uat-wp-offload-aws-sacsconsult-com.s3-ap-southeast-2.amazonaws.com/wp-content/uploads/2015-09-02-Why-is-everything-code-1024x680.jpg");
//       urls.add("http://spvnews.com/sites/default/files/styles/article/public/Murphy%20SPHS%20Cheer%201.jpg?itok=6vJtF-Rp");
//    }
//HAS NOTHING IMPT NOT USED

    //executes Async
   /*private void loadImageFromUrl(String url) {
       Picasso.with(this).load(url).placeholder(R.mipmap.ic_launcher)
               .error(R.mipmap.ic_launcher)
               .into(imageView,
                       new com.squareup.picasso.Callback() {
                   @Override
                   public void onSuccess() {

                   }

                   @Override
                   public void onError() {

                   }
               });
   }*/

}

