package com.esmaeel.softask.Utils;

import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = "EndlessScrollListener";

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    //    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount, currentVisiableItem;
    public int findLastCompeletlyVisiblePosition = 0;
    public int currentPage = 1; // lets make it public to access it anywhere
    private boolean isLastItemDisplaying = false;

    RecyclerViewPositionHelper mRecyclerViewHelper;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
        findLastCompeletlyVisiblePosition = mRecyclerViewHelper.findLastCompletelyVisibleItemPosition();
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mRecyclerViewHelper.getItemCount();
        firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
        currentVisiableItem = mRecyclerViewHelper.findLastVisibleItemPosition();
        isLastItemDisplaying = mRecyclerViewHelper.isLastItemDisplaying(recyclerView);

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            // Do something
            currentPage++;
            onLoadMore(currentPage, currentVisiableItem);
            loading = true;
        }
    }

    //Start loading
    public abstract void onLoadMore(int currentPage, int visibleItemPosition);

}