package com.bzilaji.rssreader.RSS.history;


public interface History {
    void addToHistory(String url);

    boolean isVisited(String url);
}
