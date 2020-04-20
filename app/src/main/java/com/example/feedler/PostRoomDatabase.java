package com.example.feedler;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Database(entities = {Post.class}, version = 1, exportSchema = false)
public abstract class PostRoomDatabase extends RoomDatabase {

    public abstract PostDao postDao();

    private static PostRoomDatabase INSTANCE;

     public static PostRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PostRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PostRoomDatabase.class, "post_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries()//Очень плохо, но как исправить не знаю
                            .build();
                }
            }

        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    /**
     * Populate the database in the background.
     */
    static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final PostDao mDao;

        PopulateDbAsync(PostRoomDatabase db) {
            mDao = db.postDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
                final Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put(VKApiConst.FILTERS, "post");

                final VKParameters parameters = new VKParameters(paramsMap);
                VKRequest request = new VKRequest("newsfeed.get", parameters); //Запрос с фильтром params
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {

                            //добавление в бд
                            JSONObject jsonObject = (JSONObject) response.json.get("response");
                            JSONArray jsonArrayPost = (JSONArray) jsonObject.get("items");
                            JSONArray jsonArrayGroup = (JSONArray) jsonObject.get("groups");
                            JSONArray jsonArrayProfiles = (JSONArray) jsonObject.get("profiles");

                            for (int i = 0; i < jsonArrayPost.length(); i++) {
                                JSONObject jsonObjectPost = (JSONObject) jsonArrayPost.get(i);

                                String text = jsonObjectPost.optString("text");
                                int sourceId = jsonObjectPost.getInt("source_id");
                                String groupName = null;
                                long  dateInMillis = jsonObjectPost.getLong("date");
                                String date = dateToString(dateInMillis);

                                //получение имени источника
                                if (sourceId>0){
                                    for (int j = 0 ; j< jsonArrayProfiles.length(); j++){
                                        JSONObject profile = (JSONObject) jsonArrayProfiles.get(j);
                                        if (sourceId==profile.getInt("id")){
                                            String firstName = profile.getString("first_name");
                                            String lastName= profile.getString("last_name");
                                            groupName = firstName + " " + lastName;
                                        }
                                    }
                                } else {
                                    for (int j = 0; j< jsonArrayGroup.length(); j++ ){
                                        JSONObject group = (JSONObject) jsonArrayGroup.get(j);
                                        if (sourceId*(-1)==group.getInt("id")){
                                            groupName=group.getString("name");
                                        }
                                    }
                                }

                                Post post = new Post(groupName, date, text, i );
                                mDao.insert(post);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            return null;
            }

            private String dateToString(long dateInMillis){
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(1000*dateInMillis);
                String day = getRightStringNum(calendar.get(Calendar.DAY_OF_MONTH));
                String month = getRightStringNum (calendar.get(Calendar.MONTH)+1);     //не понятно, почему ставит не тот месяц
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

    }
}


