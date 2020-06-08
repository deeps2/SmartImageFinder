package com.smartimagefinder.repository;

import androidx.annotation.NonNull;

import com.smartimagefinder.model.SearchResponse;
import com.smartimagefinder.repository.api.RestClient;
import com.smartimagefinder.repository.api.SearchRemoteWebService;
import com.smartimagefinder.repository.local.SearchLocal;
import com.smartimagefinder.repository.local.SearchLocalDBHelper;
import com.smartimagefinder.repository.local.SearchTable;
import com.smartimagefinder.utils.AppUtils;
import com.smartimagefinder.utils.Constants;
import com.smartimagefinder.utils.ExecutorUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Repository class
//This will fetch the data either from API or locally
public class SearchRepository implements Callback<SearchResponse> {
    private int offset = 0;
    private int totalEstimatedMatches = 1;
    private boolean requestRunning = false;
    private Call<SearchResponse> call;
    private String searchQueryString = "";
    private SearchRepoContract searchRepoContract;
    private SearchRemoteWebService searchRemoteWebService;

    public SearchRepository(SearchRepoContract searchRepoContract) {
        this.searchRepoContract = searchRepoContract;
    }

    public void clearVariables() {
        offset = 0;
        totalEstimatedMatches = 1;
        requestRunning = false;
        if (call != null)
            call.cancel();
    }

    public void setSearchQueryString(String searchQueryString) {
        this.searchQueryString = searchQueryString;
    }

    public boolean isRequestRunning() {
        return requestRunning;
    }

    public int getOffset() {
        return offset;
    }

    public int getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public void cancelRequest() {
        if (call != null)
            call.cancel();
    }

    public void fetchResults() {
        ExecutorUtils.getInstance().diskIO().execute(() -> {
            requestRunning = true;

            List<SearchTable> searchTableList = SearchLocalDBHelper.getInstance().searchDao().getResult(searchQueryString.toLowerCase(), offset);

            if (searchTableList == null || searchTableList.isEmpty()) {
                //no result found in table, fetch from API
                ExecutorUtils.getInstance().mainThread().execute(this::makeAPIRequest);

            } else {
                //result found in table
                //only 1 response will be present in DB for a particular searchQueryString and offset
                SearchResponse searchResponse = (SearchResponse) AppUtils.getObject(searchTableList.get(0).searchResult, SearchResponse.class);
                if (searchResponse == null) {
                    requestRunning = false;
                    return;
                }

                //update last access time
                SearchLocalDBHelper.getInstance().searchDao().updateLastAccessTime(System.currentTimeMillis(), searchTableList.get(0).id);

                //pass the results to main thread
                ExecutorUtils.getInstance().mainThread().execute(() -> {
                    requestRunning = false;
                    onValidResultsReceived(searchResponse);
                });
            }
        });
    }

    private void makeAPIRequest() {
        call = getWebService().searchList(searchQueryString, offset);
        searchRepoContract.onRequestStarted();
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
        onNetworkRequestFinished();

        if (!response.isSuccessful()) {
            searchRepoContract.onResponseUnsuccessful(offset);
            return;
        }

        SearchResponse searchResponse = response.body();
        if (isResponseEmpty(searchResponse)) {
            notifyViewModelNoResponse();
            return;
        }

        int currentOffset = offset;
        onValidResultsReceived(searchResponse);
        makeChangesInDatabase(currentOffset, searchResponse);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull Throwable t) {
        onNetworkRequestFinished();
        if (call.isCanceled())
            return;

        if (t instanceof IOException && Constants.ERROR_NO_NET.equals(t.getMessage())) {
            searchRepoContract.onNetworkErrorOccurred(offset);
        } else {
            searchRepoContract.onNonNetworkErrorOccurred(offset);
        }
    }

    private boolean isResponseEmpty(SearchResponse searchResponse) {
        return (searchResponse == null || searchResponse.getResultsList() == null || searchResponse.getResultsList().isEmpty());
    }

    private void notifyViewModelNoResponse() {
        if (offset == 0)
            searchRepoContract.onNoResultsFoundInVeryFirstCall();
    }

    private void onNetworkRequestFinished() {
        requestRunning = false;
        searchRepoContract.onRequestFinished();
    }

    private void onValidResultsReceived(SearchResponse searchResponse) {
        offset = searchResponse.getNextOffset();
        totalEstimatedMatches = searchResponse.getTotalEstimatedMatches();
        searchRepoContract.onValidResultsReceived(searchResponse.getResultsList());
    }

    //insert new record in DB and delete oldest record if MAX_ROWS size is reached
    private void makeChangesInDatabase(int currentOffset, SearchResponse searchResponse) {
        ExecutorUtils.getInstance().diskIO().execute(() -> {
            SearchTable searchTable = SearchTable.getObject(searchQueryString.toLowerCase(),
                    currentOffset,
                    AppUtils.getSerialisedStringFromObject(searchResponse),
                    System.currentTimeMillis());

            if (SearchLocal.getInstance().getTotalRows() == SearchTable.MAX_ROWS) {
                SearchLocalDBHelper.getInstance().searchDao().deleteOldestRecord();
                SearchLocal.getInstance().decrementTotalRows();
            }

            SearchLocalDBHelper.getInstance().searchDao().insert(searchTable);
            SearchLocal.getInstance().incrementTotalRows();
        });

    }

    private SearchRemoteWebService getWebService() {
        if (searchRemoteWebService == null)
            searchRemoteWebService = RestClient.getInstance().getRestClient().create(SearchRemoteWebService.class);
        return searchRemoteWebService;
    }
}

