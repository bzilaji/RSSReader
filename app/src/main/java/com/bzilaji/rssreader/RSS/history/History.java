package com.bzilaji.rssreader.RSS.history;

/**
 * Created by Gabesz on 2016.08.04..
 */
public interface History {
    void addToHistory(String url);

    boolean isVisited(String url);
}
