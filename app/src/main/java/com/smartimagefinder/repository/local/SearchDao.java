package com.smartimagefinder.repository.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class SearchDao {

    //insert a new record
    @Insert
    public abstract void insert(SearchTable searchTable);

    //get the results for specific 'search_key' and 'search_offset'.
    @Query("SELECT * FROM  search_results WHERE search_key = (:searchKey) AND search_offset = (:offset)")
    public abstract List<SearchTable> getResult(String searchKey, int offset);

    //This is done so that before inserting we don't have to query the database again to get its size
    @Query("SELECT COUNT (id) FROM search_results")
    public abstract int getTotalRows();

    //DB can have max SearchTable.MAX_ROWS otherwise cache size of app will keep on increasing with new search results
    //After reaching the threshold size of MAX_ROWS we will delete the oldest record in DB and then insert new record
    @Query("DELETE FROM search_results WHERE last_access_time = (SELECT MIN(last_access_time) FROM search_results)")
    public abstract void deleteOldestRecord();

    //update 'last_access_time' if the same string is searched
    @Query("UPDATE search_results SET last_access_time = (:lastAccessTime) WHERE id = (:id)")
    public abstract void updateLastAccessTime(long lastAccessTime, long id);

}

