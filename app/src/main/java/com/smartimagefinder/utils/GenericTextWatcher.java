package com.smartimagefinder.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class GenericTextWatcher implements TextWatcher {

    private TextChangeListener textChangeListener;

    public GenericTextWatcher(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (textChangeListener != null)
            textChangeListener.onTextChanged(s);
    }

    public interface TextChangeListener {
        void onTextChanged(CharSequence s);
    }
}
