package com.smartimagefinder.repository.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SearchTable.class}, version = 1)
public abstract class SearchLocalDB extends RoomDatabase {
    public abstract SearchDao searchDao();
}
