package com.ak.search.app;

import com.ak.search.model.Answers;

import java.util.HashMap;

/**
 * Created by dg hdghfd on 14-12-2016.
 */

public interface UpdateReviewAnswer {

    public void onReviewUpdate(HashMap<Long, Answers> answers);
}
