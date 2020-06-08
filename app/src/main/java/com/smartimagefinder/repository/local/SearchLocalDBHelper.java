package com.smartimagefinder.repository.local;

import androidx.room.Room;

import com.smartimagefinder.base.MyApplication;

public class SearchLocalDBHelper {
    private static final String SEARCH_DB_NAME = "search_local_room_db";
    private static final Object LOCK = new Object();
    private static SearchLocalDB sInstance;

    public static SearchLocalDB getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null)
                    sInstance = Room
                            .databaseBuilder(MyApplication.getInstance(),
                                    SearchLocalDB.class,
                                    SEARCH_DB_NAME)
                            .build();
            }
        }
        return sInstance;
    }

}