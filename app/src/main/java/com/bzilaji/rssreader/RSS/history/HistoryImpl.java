package com.bzilaji.rssreader.RSS.history;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

public class HistoryImpl implements History {

    private static final String KEY_URL_SET = "URL_SET";
    private final SharedPreferences prefs;
    private final Set<String> historySet;

    public HistoryImpl(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        historySet = new HashSet<>(prefs.getStringSet(KEY_URL_SET, new HashSet<String>()));
    }

    @Override
    public void addToHistory(String url) {
        if (!TextUtils.isEmpty(url)) {
            historySet.add(url);
            prefs.edit().putStringSet(KEY_URL_SET, new HashSet<String>(historySet)).commit();
        }
    }

    @Override
    public boolean isVisited(String url) {
        return historySet.contains(url);
    }


}
