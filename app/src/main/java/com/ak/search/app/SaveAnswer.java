package com.ak.search.app;

import com.ak.search.realm_model.Answers;

/**
 * Created by dg hdghfd on 14-12-2016.
 */

public interface SaveAnswer {
    void onAnswerSave(Answers ans);
    void onAddSurvey(long id, int pos, int parentPos);
    void saveCollection();
}
