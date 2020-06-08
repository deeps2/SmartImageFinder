package com.smartimagefinder.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {

    @SerializedName("value")
    private List<Results> resultsList;

    @SerializedName("nextOffset")
    private int nextOffset;

    @SerializedName("totalEstimatedMatches")
    private int totalEstimatedMatches;

    public List<Results> getResultsList() {
        return resultsList;
    }

    public int getNextOffset() {
        return nextOffset;
    }

    public int getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public static class Results {
        @SerializedName("thumbnailUrl")
        private String thumbnailUrl;

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
    }
}

