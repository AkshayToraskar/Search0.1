package com.ak.search.app;

import android.support.v7.widget.RecyclerView;

/**
 * Created by dg hdghfd on 28-02-2017.
 */

public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}