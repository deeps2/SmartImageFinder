package com.smartimagefinder.repository;

import com.smartimagefinder.model.SearchResponse;

import java.util.List;

//interface between SearchViewModel and SearchRepository
//implemented by SearchViewModel
public interface SearchRepoContract {

    void onRequestStarted();

    void onRequestFinished();

    void onResponseUnsuccessful(int offset);

    void onNoResultsFoundInVeryFirstCall();

    void onNetworkErrorOccurred(int offset);

    void onNonNetworkErrorOccurred(int offset);

    void onValidResultsReceived(List<SearchResponse.Results> resultsList);

}
