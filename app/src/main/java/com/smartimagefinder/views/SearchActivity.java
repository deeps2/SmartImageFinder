package com.smartimagefinder.views;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartimagefinder.R;
import com.smartimagefinder.base.BaseActivity;
import com.smartimagefinder.model.ErrorMessage;
import com.smartimagefinder.utils.AppUtils;
import com.smartimagefinder.utils.GenericOnScrollListener;
import com.smartimagefinder.utils.GenericTextWatcher;
import com.smartimagefinder.viewmodel.SearchViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity
        extends BaseActivity
        implements MenuItem.OnMenuItemClickListener,
        TextView.OnEditorActionListener,
        GenericTextWatcher.TextChangeListener,
        GenericOnScrollListener.OnScrollListener,
        SearchAdapter.OnItemClickListener {

    @BindView(R.id.search_edit_text)
    EditText searchEditText;

    @BindView(R.id.cross_icon)
    ImageView crossIcon;

    @BindView(R.id.search_progress)
    ProgressBar searchProgressBar;

    @BindView(R.id.message_layout)
    LinearLayout messageLayout;

    @BindView(R.id.message_image)
    ImageView messageImage;

    @BindView(R.id.message_text)
    TextView messageText;

    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;

    private SearchAdapter searchAdapter;
    private GridLayoutManager layoutManager;

    private SearchViewModel searchViewModel;

    private int spanCount = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        searchViewModel.init();

        initDefaultUI();
        addListeners();
        showKeyboard();
        addObservers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchViewModel.onDetached();
    }

    private void initDefaultUI() {
        AppUtils.changeStatusBarColor(this, R.color.status_bar_color);
        layoutManager = new GridLayoutManager(this, spanCount);
    }

    private void addListeners() {
        searchEditText.setOnEditorActionListener(this);
        searchEditText.addTextChangedListener(new GenericTextWatcher(this));
        searchRecycler.addOnScrollListener(new GenericOnScrollListener(this));
    }

    private void addObservers() {
        searchViewModel.getIsCrossIconVisible().observe(this, isVisible -> {
            int visibility = isVisible ? View.VISIBLE : View.GONE;
            crossIcon.setVisibility(visibility);
        });

        searchViewModel.getResultsList().observe(this, resultsList -> {
            showOnlyDataLayout();
            if (searchAdapter == null) {
                searchAdapter = new SearchAdapter(resultsList, this);
                searchRecycler.setLayoutAnimation(null);
                searchRecycler.setLayoutManager(layoutManager);
                searchRecycler.setAdapter(searchAdapter);
            } else {
                searchAdapter.addMoreData(resultsList);
            }
        });

        searchViewModel.getShowProgressForNetworkRequest().observe(this, showProgressForNetworkRequest -> {
            if (showProgressForNetworkRequest)
                showProgressBar();
            else
                hideProgressBar();
        });

        searchViewModel.getNoResultsFoundInFirstCall().observe(this, noResultsFound -> {
            if (noResultsFound)
                showOnlyMessageLayout(AppUtils.getAppDrawable(R.drawable.no_results, this), AppUtils.getAppString(R.string.no_results_found, this));
        });

        searchViewModel.getErrorMessage().observe(this, this::showMessage);
    }

    @Override
    public void onItemClicked(String clickedImageUrl, ImageView imgView, String transitionName) {
        openSearchDetailActivity(clickedImageUrl, imgView, transitionName);
    }

    @OnClick({R.id.search_container, R.id.search_icon, R.id.search_edit_text})
    public void onSearchBarClicked(View view) {
        showKeyboard();
    }

    @OnClick(R.id.cross_icon)
    public void onCrossIconClicked() {
        searchEditText.setText("");
        showKeyboard();
    }

    @OnClick(R.id.grid_size_options)
    public void onGridSizeOptionsClicked() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.grid_size_options));
        getMenuInflater().inflate(R.menu.grid_menu, popupMenu.getMenu());

        MenuItem twoColumn = popupMenu.getMenu().findItem(R.id.two_column);
        twoColumn.setOnMenuItemClickListener(this);

        MenuItem threeColumn = popupMenu.getMenu().findItem(R.id.three_column);
        threeColumn.setOnMenuItemClickListener(this);

        MenuItem fourColumn = popupMenu.getMenu().findItem(R.id.four_column);
        fourColumn.setOnMenuItemClickListener(this);

        if (spanCount == 2)
            twoColumn.setChecked(true);
        else if (spanCount == 3)
            threeColumn.setChecked(true);
        else
            fourColumn.setChecked(true);

        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        menuItem.setChecked(true);

        if (menuItem.getItemId() == R.id.two_column) {
            spanCount = 2;
        } else if (menuItem.getItemId() == R.id.three_column) {
            spanCount = 3;
        } else {
            spanCount = 4;
        }

        layoutManager.setSpanCount(spanCount);

        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            AppUtils.hideKeyboard(SearchActivity.this);
            searchEditText.clearFocus();

            final String searchQuery = searchEditText.getText().toString().trim();
            searchViewModel.setNewSearchString(searchQuery);

            if (searchQuery.isEmpty())
                return false;

            if (searchAdapter != null)
                searchAdapter.clearData();

            searchViewModel.initiateNewSearch();

            return true;
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s) {
        searchViewModel.onTextChanged(s);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if (dy >= 0 && layoutManager != null && searchAdapter != null) {
            int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            searchViewModel.shouldMoreResultsIfAlmostScrolledToBottom(searchAdapter.getItemCount(), lastVisibleItem);
        }
    }

    private void showMessage(ErrorMessage errorMessage) {
        int drawableID = errorMessage.isNetworkError() ? R.drawable.no_internet : R.drawable.generic_error;
        int stringID = errorMessage.isNetworkError() ? R.string.no_internet_msg : R.string.generic_error_msg;

        if (errorMessage.getOffSet() == 0)
            showOnlyMessageLayout(AppUtils.getAppDrawable(drawableID, this), AppUtils.getAppString(stringID, this));
        else
            Toast.makeText(this, stringID, Toast.LENGTH_SHORT).show();
    }

    private void showOnlyMessageLayout(Drawable drawable, String message) {
        searchProgressBar.setVisibility(View.INVISIBLE);
        searchRecycler.setVisibility(View.INVISIBLE);
        messageLayout.setVisibility(View.VISIBLE);
        messageImage.setImageDrawable(drawable);
        messageText.setText(message);
    }

    private void showProgressBar() {
        searchProgressBar.setVisibility(View.VISIBLE);
        searchRecycler.setVisibility(View.VISIBLE);
        messageLayout.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        searchProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showOnlyDataLayout() {
        searchProgressBar.setVisibility(View.INVISIBLE);
        searchRecycler.setVisibility(View.VISIBLE);
        messageLayout.setVisibility(View.GONE);
    }

    private void showKeyboard() {
        searchEditText.requestFocus();
        AppUtils.showKeyboard(this, searchEditText);
    }

}
