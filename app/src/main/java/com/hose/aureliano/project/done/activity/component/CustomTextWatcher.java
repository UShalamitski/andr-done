package com.hose.aureliano.project.done.activity.component;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Extends {@link TextWatcher}.
 * <p/>
 * Date: 12.22.2018
 *
 * @author Uladzislau Shalamitski
 */
public class CustomTextWatcher implements TextWatcher {

    private AfterTextChangedListener<CharSequence> handler;

    /**
     * Constructor.
     *
     * @param handler to handle onTextChanged event
     */
    public CustomTextWatcher(AfterTextChangedListener<CharSequence> handler) {
        this.handler = handler;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Empty method
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Empty method
    }

    @Override
    public void afterTextChanged(Editable text) {
        handler.handle(text);
    }

    public interface AfterTextChangedListener<T> {
        void handle(T item);
    }
}
