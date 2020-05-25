package com.example.feedler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.Favorites.FavoriteAdapter;
import com.example.feedler.Images.Image;
import com.example.feedler.InnerWebBrowser.WebActivity;
import com.example.feedler.PagedList.PostAdapter;
import com.example.feedler.PagedList.PostViewHolder;
import com.squareup.picasso.Picasso;

import org.bluecabin.textoo.LinksHandler;
import org.bluecabin.textoo.Textoo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private PostViewModel model;
    private int screenWidth;
    private List<ImageView> imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity_layout);

        Log.e("PostActivity", "OnClick");

        model=new ViewModelProvider(this).get(PostViewModel.class);
        Post post = PostViewModel.postForTransmission;


        TextView postText =  findViewById(R.id.postText);
        TextView dataText = findViewById(R.id.postTime);
        TextView groupName= findViewById(R.id.groupName);
        TextView mShare = findViewById(R.id.share);
        Button favorite = findViewById(R.id.favoriteButton);
        ImageView groupPhoto = findViewById(R.id.groupPhoto);
        LinearLayout imageMainLinearLayout = findViewById(R.id.imageMainLinearLayout);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth=metrics.widthPixels-pxFromDp(40);

        imageViews = new ArrayList<>();
        for (int i=0; i<10; i++){
            ImageView imageView = new ImageView(this);
            imageView.setPadding(0,0,4,4);
            imageViews.add(imageView);
        }

        for(int i=0; i<3; i++){
            LinearLayout row = new LinearLayout(this);
            for (int numImageView=i*3; numImageView<((i+1)*3); numImageView++){
                row.addView(imageViews.get(numImageView));
            }
            if(i==2) row.addView(imageViews.get(9));
            imageMainLinearLayout.addView(row);
        }

        if (post.attachedLink!=null){
            postText.setText(post.getPostText()+"\n"+"\n"+"Ссылка: "+post.attachedLink);
        } else {
            postText.setText(post.getPostText());
        }

        postText= Textoo
                .config(postText)
                .linkifyWebUrls()
                .addLinksHandler(new LinksHandler() {
                    @Override
                    public boolean onClick(View view, String url) {
                        SharedPreferences mSettings=PostActivity.this.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                        boolean isInInnerBrowser=false;
                        if (mSettings.contains(MainActivity.APP_PREFERENCES_INNER_BROWSER)){
                            isInInnerBrowser=mSettings.getBoolean(MainActivity.APP_PREFERENCES_INNER_BROWSER, false);
                        }
                        if (isInInnerBrowser){
                            Intent intent = new Intent(PostActivity.this, WebActivity.class);
                            intent.putExtra(WebActivity.URLKEY, url);
                            startActivity(intent);
                        }
                        return isInInnerBrowser;

                    }
                })
                .apply();

        dataText.setText(dateToString(post.getDate()));
        groupName.setText(post.getGroupName());

        Picasso.with(this).
                load(post.groupImageURL).
                placeholder(getDrawable(pxFromDp(62), pxFromDp(62), R.drawable.loading)).
                error(getDrawable(pxFromDp(62), pxFromDp(62), R.drawable.error)).
                resize(pxFromDp(62) , pxFromDp(62)).
                into(groupPhoto);

        setImageInPost(post.imageList);


        if (post.favorite){
            favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade));
        } else {
            favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade_empty));
        }

        favorite.setOnClickListener(v -> {
            if (post.favorite) {
                model.deleteFavorite(post);
                favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade_empty));
            }  else  {
                post.favorite=true;
                model.insertFavorite(post);
                favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade));
            }
        });


        mShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, post.linkOfPost);
            Intent chosenIntent = Intent.createChooser(intent, post.linkOfPost);
            mShare.getContext().startActivity(chosenIntent);
        });


    }



    private String dateToString(long dateInMillis){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(1000*dateInMillis);
        String day = getRightStringNum(calendar.get(Calendar.DAY_OF_MONTH));
        String month = getRightStringNum (calendar.get(Calendar.MONTH)+1);
        String year = getRightStringNum( calendar.get(Calendar.YEAR));
        String hour = getRightStringNum( calendar.get(Calendar.HOUR_OF_DAY));
        String minute = getRightStringNum(calendar.get(Calendar.MINUTE));
        String dateString= day+"."+month+"."+ year+ " "+ hour+":"+minute;
        return dateString;
    }

    private String getRightStringNum(int num){
        if (num<10){
            return "0"+num;
        } else return ""+num;
    }

    public int pxFromDp(int dp) {
        return (int) (dp * this
                .getResources()
                .getDisplayMetrics()
                .density);
    }

    private int heightRow( Image image1 , Image image2 ){
        float sum= (float) image1.width/image1.height+ (float) image2.width/image2.height;
        return (int) ((screenWidth-pxFromDp(4))/sum);
    }

    private int heightRow( Image image1 , Image image2 , Image image3){
        float sum= (float) image1.width/image1.height+ (float) image2.width/image2.height+(float) image3.width/image3.height;
        return (int) ((screenWidth-pxFromDp(8))/sum);
    }

    private int heightRow( Image image1 , Image image2 , Image image3,Image image4){
        float sum= (float) image1.width/image1.height+ (float) image2.width/image2.height+(float) image3.width/image3.height+(float) image4.width/image4.height;
        return (int) ((screenWidth-pxFromDp(12))/sum);
    }

    private void setImageInPost(List<Image> imageList) {

        if (imageList!=null){
            switch (imageList.size()) {
                case (0):
                    for(int i=0 ;i<imageViews.size(); i++){
                        imageViews.get(i).setImageDrawable(null);
                    }
                    break;
                case (1):
                    for(int i=1 ;i<imageViews.size(); i++){
                        imageViews.get(i).setImageDrawable(null);
                    }
                    loadImage(imageList.get(0), 0, screenWidth, 0);
                    break;
                case (2):
                    int heightOfImage2Row1 = heightRow(imageList.get(0), imageList.get(1));
                    for (int i = 0; i < imageList.size(); i++) {
                        loadImage(imageList.get(i), i, 0, heightOfImage2Row1);
                    }
                    break;
                case (3):
                    int heightOfImage3Row2 = heightRow(imageList.get(1), imageList.get(2));
                    loadImage(imageList.get(0), 0, screenWidth, 0);
                    loadImage(imageList.get(1), 3, 0, heightOfImage3Row2);
                    loadImage(imageList.get(2), 4, 0, heightOfImage3Row2);
                    break;
                case (4):
                    int heightOfImage4Row1 = heightRow(imageList.get(0), imageList.get(1));
                    int heightOfImage4Row2 = heightRow(imageList.get(2), imageList.get(3));
                    loadImage(imageList.get(0), 0, 0, heightOfImage4Row1);
                    loadImage(imageList.get(1), 1, 0, heightOfImage4Row1);
                    loadImage(imageList.get(2), 3, 0, heightOfImage4Row2);
                    loadImage(imageList.get(3), 4, 0, heightOfImage4Row2);
                    break;
                case (5):
                    int heightOfImage5Row1 = heightRow(imageList.get(0), imageList.get(1));
                    int heightOfImage5Row2 = heightRow(imageList.get(2), imageList.get(3), imageList.get(4));
                    loadImage(imageList.get(0), 0, 0, heightOfImage5Row1);
                    loadImage(imageList.get(1), 1, 0, heightOfImage5Row1);
                    loadImage(imageList.get(2), 3, 0, heightOfImage5Row2);
                    loadImage(imageList.get(3), 4, 0, heightOfImage5Row2);
                    loadImage(imageList.get(4), 5, 0, heightOfImage5Row2);
                    break;
                case (6):
                    int heightOfImage6Row1 = heightRow(imageList.get(0), imageList.get(1), imageList.get(2));
                    int heightOfImage6Row2 = heightRow(imageList.get(3), imageList.get(4), imageList.get(5));
                    loadImage(imageList.get(0), 0, 0, heightOfImage6Row1);
                    loadImage(imageList.get(1), 1, 0, heightOfImage6Row1);
                    loadImage(imageList.get(2), 2, 0, heightOfImage6Row1);
                    loadImage(imageList.get(3), 3, 0, heightOfImage6Row2);
                    loadImage(imageList.get(4), 4, 0, heightOfImage6Row2);
                    loadImage(imageList.get(5), 5, 0, heightOfImage6Row2);
                    break;
                case (7):
                    int heightOfImage7Row1 = heightRow(imageList.get(0), imageList.get(1));
                    int heightOfImage7Row2 = heightRow(imageList.get(2), imageList.get(3), imageList.get(4));
                    int heightOfImage7Row3 = heightRow(imageList.get(5), imageList.get(6));
                    loadImage(imageList.get(0), 0, 0, heightOfImage7Row1);
                    loadImage(imageList.get(1), 1, 0, heightOfImage7Row1);
                    loadImage(imageList.get(2), 3, 0, heightOfImage7Row2);
                    loadImage(imageList.get(3), 4, 0, heightOfImage7Row2);
                    loadImage(imageList.get(4), 5, 0, heightOfImage7Row2);
                    loadImage(imageList.get(5), 6, 0, heightOfImage7Row3);
                    loadImage(imageList.get(6), 7, 0, heightOfImage7Row3);
                    break;
                case (8):
                    int heightOfImage8Row1 = heightRow(imageList.get(0), imageList.get(1));
                    int heightOfImage8Row2 = heightRow(imageList.get(2), imageList.get(3), imageList.get(4));
                    int heightOfImage8Row3 = heightRow(imageList.get(5), imageList.get(6), imageList.get(6));
                    loadImage(imageList.get(0), 0, 0, heightOfImage8Row1);
                    loadImage(imageList.get(1), 1, 0, heightOfImage8Row1);
                    loadImage(imageList.get(2), 3, 0, heightOfImage8Row2);
                    loadImage(imageList.get(3), 4, 0, heightOfImage8Row2);
                    loadImage(imageList.get(4), 5, 0, heightOfImage8Row2);
                    loadImage(imageList.get(5), 6, 0, heightOfImage8Row3);
                    loadImage(imageList.get(6), 7, 0, heightOfImage8Row3);
                    loadImage(imageList.get(7), 8, 0, heightOfImage8Row3);
                    break;
                case (9):
                    int heightOfImage9Row1 = heightRow(imageList.get(0), imageList.get(1), imageList.get(2));
                    int heightOfImage9Row2 = heightRow(imageList.get(3), imageList.get(4), imageList.get(5));
                    int heightOfImage9Row3 = heightRow(imageList.get(6), imageList.get(7), imageList.get(8));
                    loadImage(imageList.get(0), 0, 0, heightOfImage9Row1);
                    loadImage(imageList.get(1), 1, 0, heightOfImage9Row1);
                    loadImage(imageList.get(2), 2, 0, heightOfImage9Row1);
                    loadImage(imageList.get(3), 3, 0, heightOfImage9Row2);
                    loadImage(imageList.get(4), 4, 0, heightOfImage9Row2);
                    loadImage(imageList.get(5), 5, 0, heightOfImage9Row2);
                    loadImage(imageList.get(6), 6, 0, heightOfImage9Row3);
                    loadImage(imageList.get(7), 7, 0, heightOfImage9Row3);
                    loadImage(imageList.get(8), 8, 0, heightOfImage9Row3);
                    break;
                case (10):
                    int heightOfImage10Row1 = heightRow(imageList.get(0), imageList.get(1), imageList.get(2));
                    int heightOfImage10Row2 = heightRow(imageList.get(3), imageList.get(4), imageList.get(5));
                    int heightOfImage10Row3 = heightRow(imageList.get(6), imageList.get(7), imageList.get(8), imageList.get(9));
                    loadImage(imageList.get(0), 0, 0, heightOfImage10Row1);
                    loadImage(imageList.get(1), 1, 0, heightOfImage10Row1);
                    loadImage(imageList.get(2), 2, 0, heightOfImage10Row1);
                    loadImage(imageList.get(3), 3, 0, heightOfImage10Row2);
                    loadImage(imageList.get(4), 4, 0, heightOfImage10Row2);
                    loadImage(imageList.get(5), 5, 0, heightOfImage10Row2);
                    loadImage(imageList.get(6), 6, 0, heightOfImage10Row3);
                    loadImage(imageList.get(7), 7, 0, heightOfImage10Row3);
                    loadImage(imageList.get(8), 8, 0, heightOfImage10Row3);
                    loadImage(imageList.get(9), 9, 0, heightOfImage10Row3);
                    break;
            }
        }
    }


    private void loadImage(Image image, int numOfView, int widthOfImage, int heightOfImage){
        int widthOfPlaceHolder;
        int heightOfPlaceHolder;
        if(widthOfImage==0){
            widthOfPlaceHolder= getWidthOfImage(image, heightOfImage);
            heightOfPlaceHolder=heightOfImage;
        } else {
            heightOfPlaceHolder=getHeightOfImage(image, widthOfImage);
            widthOfPlaceHolder=widthOfImage;
        }

        Picasso.with(this).
                load(image.smallSizeURL).
                placeholder(getDrawable(widthOfPlaceHolder, heightOfPlaceHolder, R.drawable.loading)).
                error(getDrawable(widthOfPlaceHolder, heightOfPlaceHolder, R.drawable.loading)).
                resize(widthOfImage , heightOfImage).
                into(imageViews.get(numOfView));
    }

    private Drawable getDrawable(int width, int height, int resource){
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(),
                resource), width, height, false);
        return new BitmapDrawable(this.getResources(), bitmap );
    }

    private int getWidthOfImage(Image image, int heightOfImage){
        return (int) (heightOfImage*(((float)image.width/image.height)));
    }

    private int getHeightOfImage(Image image, int widthOfImage){
        return (int) (widthOfImage*(((float)image.height/image.width)));
    }
}