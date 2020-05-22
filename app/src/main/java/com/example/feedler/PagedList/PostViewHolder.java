package com.example.feedler.PagedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.Images.Image;
import com.example.feedler.InnerWebBrowser.WebActivity;
import com.example.feedler.Post;
import com.example.feedler.R;
import com.squareup.picasso.Picasso;

import org.bluecabin.textoo.LinksHandler;
import org.bluecabin.textoo.Textoo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder {
    private TextView postText;
    private TextView mShare;
    private TextView dataText;
    private TextView groupName;
    public Button favorite;
    private LinearLayout imageMainLinearLayout;
    private int screenWidth;
    private Context context;
    private List<ImageView> imageViews;

   public PostViewHolder(View view, Context context) {
        super(view);
        postText =  view.findViewById(R.id.postText);
        dataText = view.findViewById(R.id.postTime);
        groupName= view.findViewById(R.id.groupName);
        mShare = view.findViewById(R.id.share);
        favorite = view.findViewById(R.id.favoriteButton);
        imageMainLinearLayout=view.findViewById(R.id.imageMainLinearLayout);
        this.context=context;

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth=metrics.widthPixels-pxFromDp(40);

       imageViews = new ArrayList<>();
        for (int i=0; i<10; i++){
            ImageView imageView = new ImageView(context);
            imageView.setPadding(0,0,4,4);
            imageViews.add(imageView);
        }

        for(int i=0; i<3; i++){
            LinearLayout row = new LinearLayout(context);
            for (int numImageView=i*3; numImageView<((i+1)*3); numImageView++){
                row.addView(imageViews.get(numImageView));
            }
            if(i==2) row.addView(imageViews.get(9));
            imageMainLinearLayout.addView(row);
        }
   }


    public void bind(Post post) {
       boolean flag = true;
       postText.setText(post.getPostText());
       postText= Textoo
               .config(postText)
               .linkifyWebUrls()
               .addLinksHandler(new LinksHandler() {
                   @Override
                   public boolean onClick(View view, String url) {
                       Log.e("bind", "in texttoo");
                       Intent intent = new Intent(context, WebActivity.class);
                       intent.putExtra(WebActivity.URLKEY, url);
                       context.startActivity(intent);
                       return true;
                       // event handled
                       // continue default processing i.e. launch browser app to display link
                   }
               })
               .apply();

        dataText.setText(dateToString(post.getDate()));
        groupName.setText(post.getGroupName());
        setImageInPost(post.imageList);


//        String text = post.getPostText();
//        Spannable spannable = new SpannableString( Html.fromHtml(text) );

//        Linkify.addLinks(spannable, Linkify.WEB_URLS);
//        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
//        for (URLSpan urlSpan : spans) {
//            LinkSpan linkSpan = new LinkSpan(urlSpan.getURL());
//            int spanStart = spannable.getSpanStart(urlSpan);
//            int spanEnd = spannable.getSpanEnd(urlSpan);
//            spannable.setSpan(linkSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannable.removeSpan(urlSpan);
//        }
//        postText.setMovementMethod(EnhancedLinkMovementMethod.getInstance());
//        postText.setText(spannable, TextView.BufferType.SPANNABLE);






        if (post.favorite){
            favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade));
        } else {
            favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade_empty));
        }
        mShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, post.getPostText());
            Intent chosenIntent = Intent.createChooser(intent, post.getPostText());
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

    private int pxFromDp(int dp) {
        return (int) (dp * context
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
                    loadImage(imageList, 0, 0, screenWidth, 0);
                    break;
                case (2):
                    int heightOfImage2Row1 = heightRow(imageList.get(0), imageList.get(1));
                    for(int i=2 ;i<imageViews.size(); i++){
                        imageViews.get(i).setImageDrawable(null);
                    }
                    for (int i = 0; i < imageList.size(); i++) {
                        loadImage(imageList, i, i, 0, heightOfImage2Row1);
                    }
                    break;
                case (3):
                    int heightOfImage3Row2 = heightRow(imageList.get(1), imageList.get(2));
                    loadImage(imageList, 0, 0, screenWidth, 0);
                    loadImage(imageList, 1, 3, 0, heightOfImage3Row2);
                    loadImage(imageList, 2, 4, 0, heightOfImage3Row2);

                    break;
                case (4):
                    int heightOfImage4Row1 = heightRow(imageList.get(0), imageList.get(1));
                    int heightOfImage4Row2 = heightRow(imageList.get(2), imageList.get(3));
                    loadImage(imageList, 0, 0, 0, heightOfImage4Row1);
                    loadImage(imageList, 1, 1, 0, heightOfImage4Row1);
                    loadImage(imageList, 2, 3, 0, heightOfImage4Row2);
                    loadImage(imageList, 3, 4, 0, heightOfImage4Row2);
                    break;
                case (5):
                    int heightOfImage5Row1 = heightRow(imageList.get(0), imageList.get(1));
                    int heightOfImage5Row2 = heightRow(imageList.get(2), imageList.get(3), imageList.get(4));
                    loadImage(imageList, 0, 0, 0, heightOfImage5Row1);
                    loadImage(imageList, 1, 1, 0, heightOfImage5Row1);
                    loadImage(imageList, 2, 3, 0, heightOfImage5Row2);
                    loadImage(imageList, 3, 4, 0, heightOfImage5Row2);
                    loadImage(imageList, 4, 5, 0, heightOfImage5Row2);
                    break;
                case (6):
                    int heightOfImage6Row1 = heightRow(imageList.get(0), imageList.get(1), imageList.get(2));
                    int heightOfImage6Row2 = heightRow(imageList.get(3), imageList.get(4), imageList.get(5));
                    loadImage(imageList, 0, 0, 0, heightOfImage6Row1);
                    loadImage(imageList, 1, 1, 0, heightOfImage6Row1);
                    loadImage(imageList, 2, 2, 0, heightOfImage6Row1);
                    loadImage(imageList, 3, 3, 0, heightOfImage6Row2);
                    loadImage(imageList, 4, 4, 0, heightOfImage6Row2);
                    loadImage(imageList, 5, 5, 0, heightOfImage6Row2);
                    break;
                case (7):
                    int heightOfImage7Row1 = heightRow(imageList.get(0), imageList.get(1));
                    int heightOfImage7Row2 = heightRow(imageList.get(2), imageList.get(3), imageList.get(4));
                    int heightOfImage7Row3 = heightRow(imageList.get(5), imageList.get(6));
                    loadImage(imageList, 0, 0, 0, heightOfImage7Row1);
                    loadImage(imageList, 1, 1, 0, heightOfImage7Row1);
                    loadImage(imageList, 2, 3, 0, heightOfImage7Row2);
                    loadImage(imageList, 3, 4, 0, heightOfImage7Row2);
                    loadImage(imageList, 4, 5, 0, heightOfImage7Row2);
                    loadImage(imageList, 5, 6, 0, heightOfImage7Row3);
                    loadImage(imageList, 6, 7, 0, heightOfImage7Row3);
                    break;
                case (8):
                    int heightOfImage8Row1 = heightRow(imageList.get(0), imageList.get(1));
                    int heightOfImage8Row2 = heightRow(imageList.get(2), imageList.get(3), imageList.get(4));
                    int heightOfImage8Row3 = heightRow(imageList.get(5), imageList.get(6), imageList.get(6));
                    loadImage(imageList, 0, 0, 0, heightOfImage8Row1);
                    loadImage(imageList, 1, 1, 0, heightOfImage8Row1);
                    loadImage(imageList, 2, 3, 0, heightOfImage8Row2);
                    loadImage(imageList, 3, 4, 0, heightOfImage8Row2);
                    loadImage(imageList, 4, 5, 0, heightOfImage8Row2);
                    loadImage(imageList, 5, 6, 0, heightOfImage8Row3);
                    loadImage(imageList, 6, 7, 0, heightOfImage8Row3);
                    loadImage(imageList, 7, 8, 0, heightOfImage8Row3);
                    break;
                case (9):
                    int heightOfImage9Row1 = heightRow(imageList.get(0), imageList.get(1), imageList.get(2));
                    int heightOfImage9Row2 = heightRow(imageList.get(3), imageList.get(4), imageList.get(5));
                    int heightOfImage9Row3 = heightRow(imageList.get(6), imageList.get(7), imageList.get(8));
                    loadImage(imageList, 0, 0, 0, heightOfImage9Row1);
                    loadImage(imageList, 1, 1, 0, heightOfImage9Row1);
                    loadImage(imageList, 2, 2, 0, heightOfImage9Row1);
                    loadImage(imageList, 3, 3, 0, heightOfImage9Row2);
                    loadImage(imageList, 4, 4, 0, heightOfImage9Row2);
                    loadImage(imageList, 5, 5, 0, heightOfImage9Row2);
                    loadImage(imageList, 6, 6, 0, heightOfImage9Row3);
                    loadImage(imageList, 7, 7, 0, heightOfImage9Row3);
                    loadImage(imageList, 8, 8, 0, heightOfImage9Row3);
                    break;
                case (10):
                    int heightOfImage10Row1 = heightRow(imageList.get(0), imageList.get(1), imageList.get(2));
                    int heightOfImage10Row2 = heightRow(imageList.get(3), imageList.get(4), imageList.get(5));
                    int heightOfImage10Row3 = heightRow(imageList.get(6), imageList.get(7), imageList.get(8), imageList.get(9));
                    loadImage(imageList, 0, 0, 0, heightOfImage10Row1);
                    loadImage(imageList, 1, 1, 0, heightOfImage10Row1);
                    loadImage(imageList, 2, 2, 0, heightOfImage10Row1);
                    loadImage(imageList, 3, 3, 0, heightOfImage10Row2);
                    loadImage(imageList, 4, 4, 0, heightOfImage10Row2);
                    loadImage(imageList, 5, 5, 0, heightOfImage10Row2);
                    loadImage(imageList, 6, 6, 0, heightOfImage10Row3);
                    loadImage(imageList, 7, 7, 0, heightOfImage10Row3);
                    loadImage(imageList, 8, 8, 0, heightOfImage10Row3);
                    loadImage(imageList, 9, 9, 0, heightOfImage10Row3);
                    break;
            }
        }
    }


    private void loadImage(List<Image> imageList, int numOfImage, int numOfView, int widthOfImage, int heightOfImage){
        Picasso.with(context).
                load(imageList.get(numOfImage).smallSizeURL).
                placeholder(R.drawable.loading).
                error(R.drawable.error).
                resize(widthOfImage , heightOfImage).
                into(imageViews.get(numOfView));
    }

}

