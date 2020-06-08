package com.smartimagefinder.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartimagefinder.model.ErrorMessage;
import com.smartimagefinder.model.SearchResponse;
import com.smartimagefinder.repository.SearchRepoContract;
import com.smartimagefinder.repository.SearchRepository;

import java.util.List;

public class SearchViewModel extends ViewModel implements SearchRepoContract {
    private SearchRepository searchRepository;
    private MutableLiveData<Boolean> isCrossIconVisible;
    private MutableLiveData<List<SearchResponse.Results>> resultsList;
    private MutableLiveData<Boolean> showProgressForNetworkRequest;
    private MutableLiveData<Boolean> noResultsFoundInFirstCall;
    private MutableLiveData<ErrorMessage> errorMessage;

    public void init() {
        searchRepository = new SearchRepository(this);
        isCrossIconVisible = new MutableLiveData<>();
        resultsList = new MutableLiveData<>();
        showProgressForNetworkRequest = new MutableLiveData<>();
        noResultsFoundInFirstCall = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public void onDetached() {
        searchRepository.cancelRequest();
    }

    public MutableLiveData<Boolean> getIsCrossIconVisible() {
        return isCrossIconVisible;
    }

    public MutableLiveData<List<SearchResponse.Results>> getResultsList() {
        return resultsList;
    }

    public MutableLiveData<Boolean> getShowProgressForNetworkRequest() {
        return showProgressForNetworkRequest;
    }

    public MutableLiveData<Boolean> getNoResultsFoundInFirstCall() {
        return noResultsFoundInFirstCall;
    }

    public MutableLiveData<ErrorMessage> getErrorMessage() {
        return errorMessage;
    }

    public void onTextChanged(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence.toString())) {
            isCrossIconVisible.setValue(false);
        } else {
            isCrossIconVisible.setValue(true);
        }
    }

    @Override
    public void onValidResultsReceived(List<SearchResponse.Results> results) {
        resultsList.setValue(results);
    }

    public void shouldMoreResultsIfAlmostScrolledToBottom(int itemCount, int lastVisibleItem) {
        if (!searchRepository.isRequestRunning()
                && itemCount - lastVisibleItem <= 5
                && searchRepository.getOffset() != searchRepository.getTotalEstimatedMatches())
            searchRepository.fetchResults();
    }

    public void setNewSearchString(String searchQuery) {
        searchRepository.setSearchQueryString(searchQuery);
    }

    public void initiateNewSearch() {
        searchRepository.clearVariables();
        searchRepository.fetchResults();
    }

    @Override
    public void onRequestStarted() {
        showProgressForNetworkRequest.setValue(true);
    }

    @Override
    public void onRequestFinished() {
        showProgressForNetworkRequest.setValue(false);
    }

    @Override
    public void onNoResultsFoundInVeryFirstCall() {
        noResultsFoundInFirstCall.setValue(true);
    }

    @Override
    public void onResponseUnsuccessful(int offset) {
        showErrorMessage(offset, false);
    }

    @Override
    public void onNonNetworkErrorOccurred(int offset) {
        showErrorMessage(offset, false);
    }

    @Override
    public void onNetworkErrorOccurred(int offset) {
        showErrorMessage(offset, true);
    }

    private void showErrorMessage(int offset, boolean isNetworkError) {
        ErrorMessage errorMsg = errorMessage.getValue();
        if (errorMsg == null)
            errorMsg = new ErrorMessage();

        errorMsg.setOffSet(offset);
        errorMsg.setNetworkError(isNetworkError);

        errorMessage.setValue(errorMsg);
    }
}
