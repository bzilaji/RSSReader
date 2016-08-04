package com.bzilaji.rssreader.view;


import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bzilaji.rssreader.R;


public class ToolBarSearchView extends LinearLayout {

    private class ShowAnimationListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {
            setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            editText.requestFocus();
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    private class HideAnimationListener  implements Animator.AnimatorListener {

        void startAnimation() {
            animate(getWidth(), 0, this);
        }


        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            setVisibility(INVISIBLE);
            editText.setText("");
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    private final ShowAnimationListener showAnimationListener = new ShowAnimationListener();
    private final HideAnimationListener hideAnimationListener = new HideAnimationListener();
    private boolean isAlwaysVisible;
    private SearchEditText editText;
    private ImageView backButton;
    private ImageView clearButton;
    private boolean isEmpty = true;
    private ToolBarSearchViewListener listener;
    private InputMethodManager inputMethodManager;

    public ToolBarSearchView(Context context) {
        super(context, null);
        init(context);
    }

    public ToolBarSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ToolBarSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setListener(ToolBarSearchViewListener listener) {
        this.listener = listener;
    }

    public void show() {
        if (getVisibility() != VISIBLE) {
            animate(0, getWidth(), showAnimationListener);
        }
    }

    public void setAlwaysVisible(boolean alwaysVisible) {
        this.isAlwaysVisible = alwaysVisible;
        setVisibility(alwaysVisible ? VISIBLE : INVISIBLE);
    }

    private void animate(float startRadius, float endRadius, Animator.AnimatorListener animatorListener) {
        final int cx = getRight();
        final int cy = getHeight() / 2;
        Animator anim =
                ViewAnimationUtils.createCircularReveal(this, cx, cy, startRadius, endRadius);
        anim.setDuration(200);
        anim.addListener(animatorListener);
        anim.start();
    }

    public void hide() {
        if (getVisibility() == VISIBLE) {
            editText.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0, null);
            hideAnimationListener.startAnimation();
        }
    }

    private void init(Context context) {
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.tool_bar_search, this);
        backButton = (ImageView) findViewById(android.R.id.home);
        clearButton = (ImageView) findViewById(R.id.clear_input);
        editText = (SearchEditText) findViewById(R.id.search_input);
        editText.setText("");
        clearButton.setVisibility(INVISIBLE);
        editText.setParent(this);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });

        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    clearButton.setVisibility(View.INVISIBLE);
                    isEmpty = true;
                } else {
                    if (isEmpty) {
                        clearButton.setVisibility(View.VISIBLE);
                        isEmpty = false;
                    }
                }
                if (listener != null) {
                    listener.onSearchTextChanged(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void clear() {
        CharSequence old = editText.getText();
        editText.setText("");
        if (listener != null && !TextUtils.isEmpty(old)) {
            listener.onSearchCleared(old);
        }
    }

    public void setHint(int resId) {
        editText.setHint(resId);
    }

    private void backPressed() {
        editText.clearFocus();
        if (listener != null) {
            listener.onSearchBarHomePressed();
        }
    }


    public CharSequence getText() {
        return editText.getText();
    }

    /**
     * Local subclass for EditText.
     *
     * @hide
     */
    public static class SearchEditText extends AppCompatEditText {
        private ToolBarSearchView parent;

        public SearchEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setParent(ToolBarSearchView parent) {
            this.parent = parent;
        }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && TextUtils.isEmpty(getText())) {
                    if (!parent.isAlwaysVisible) {
                        parent.backPressed();
                    }
                }
            }
            return super.onKeyPreIme(keyCode, event);
        }
    }

}

