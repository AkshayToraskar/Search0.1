package com.ak.search.app;

/**
 * Created by dg hdghfd on 28-02-2017.
 *
 * arrange the survey questions
 *
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);

}