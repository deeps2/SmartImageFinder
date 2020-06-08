package com.smartimagefinder.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smartimagefinder.R;
import com.smartimagefinder.model.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SearchResponse.Results> resultsList;
    private OnItemClickListener callback;

    public SearchAdapter(List<SearchResponse.Results> resultsList, OnItemClickListener callback) {
        this.resultsList = resultsList;
        this.callback = callback;
    }

    void addMoreData(List<SearchResponse.Results> resultsListArg) {
        int startPosition;

        if (resultsList == null)
            resultsList = new ArrayList<>();

        startPosition = resultsList.size();
        int numberOfItemsAdded = resultsListArg.size();

        resultsList.addAll(resultsListArg);
        notifyItemRangeInserted(startPosition, numberOfItemsAdded);
    }

    void clearData() {
        if (resultsList != null) {
            resultsList.clear();
            notifyDataSetChanged();
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_image)
        ImageView listItemImage;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.list_item_image)
        public void onImageClicked() {
            callback.onItemClicked(resultsList.get(getAdapterPosition()).getThumbnailUrl(), listItemImage, listItemImage.getTransitionName());

        }

        void setData(SearchResponse.Results result) {
            Glide.with(listItemImage.getContext()).load(result.getThumbnailUrl()).placeholder(R.drawable.placeholder).centerCrop().into(listItemImage);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchViewHolder viewHolder = (SearchViewHolder) holder;
        viewHolder.setData(resultsList.get(position));
    }

    @Override
    public int getItemCount() {
        return (resultsList == null) ? 0 : resultsList.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(String clickedImageUrl, ImageView imgView, String transitionName);
    }
}

