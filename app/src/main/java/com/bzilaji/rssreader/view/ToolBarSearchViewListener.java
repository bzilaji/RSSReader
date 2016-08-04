package com.bzilaji.rssreader.view;


public interface ToolBarSearchViewListener {

    void onSearchCleared(CharSequence old);

    void onSearchBarHomePressed();

    void onSearchTextChanged(CharSequence s);

}
