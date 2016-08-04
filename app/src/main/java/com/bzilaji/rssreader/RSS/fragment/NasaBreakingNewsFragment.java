package com.bzilaji.rssreader.RSS.fragment;

import com.bzilaji.rssreader.RSS.NasaRssCallFactory;
import com.bzilaji.rssreader.RSS.model.RSS;

import retrofit2.Call;

public class NasaBreakingNewsFragment extends RssFragmentBase {


    @Override
    protected Call<RSS> createCallToRss() {
        return NasaRssCallFactory.createCallForBreakingNews();
    }

}

