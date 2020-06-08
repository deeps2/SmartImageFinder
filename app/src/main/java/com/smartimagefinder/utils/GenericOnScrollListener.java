package com.smartimagefinder.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GenericOnScrollListener extends RecyclerView.OnScrollListener {

    private OnScrollListener onScrollListener;

    public GenericOnScrollListener(OnScrollListener onScrollListenerArg) {
        this.onScrollListener = onScrollListenerArg;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (onScrollListener != null)
            onScrollListener.onScrolled(recyclerView, dx, dy);
    }

    public interface OnScrollListener {
        void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy);
    }
}
