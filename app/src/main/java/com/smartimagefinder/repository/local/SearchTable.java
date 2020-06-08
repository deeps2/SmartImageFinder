package com.smartimagefinder.repository.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_results")
public class SearchTable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "search_key")
    public String searchKey;

    @ColumnInfo(name = "search_offset")
    public int offset;

    @ColumnInfo(name = "search_result")
    public String searchResult;

    @ColumnInfo(name = "last_access_time")
    public long lastAccessTime;

    @Ignore
    public static final int MAX_ROWS = 1000;

    public static SearchTable getObject(String searchKey, int offset, String searchResult, long lastAccessTime) {
        SearchTable searchTable = new SearchTable();
        searchTable.searchKey = searchKey;
        searchTable.offset = offset;
        searchTable.searchResult = searchResult;
        searchTable.lastAccessTime = lastAccessTime;
        return searchTable;
    }
}
