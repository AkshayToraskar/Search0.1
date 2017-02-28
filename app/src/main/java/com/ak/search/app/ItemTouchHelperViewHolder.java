package com.ak.search.app;

/**
 * Created by dg hdghfd on 28-02-2017.
 */

public interface ItemTouchHelperViewHolder {

    /* Implementations should update the item view to indicate it's active state. */
    void onItemSelected();

/*
Called when completed the move or swipe, and the active item * state should be cleared.
*/

    void onItemClear();

}