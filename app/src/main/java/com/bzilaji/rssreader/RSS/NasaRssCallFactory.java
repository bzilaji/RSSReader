package com.bzilaji.rssreader.RSS;


import com.bzilaji.rssreader.RSS.model.RSS;
import com.bzilaji.rssreader.RSS.service.NasaRssService;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NasaRssCallFactory {

    private static final String BREAKING_NEWS_SUFFIX = "breaking_news.rss";
    private static final String EDU_NEWS_SUFFIX = "educationnews.rss";
    private static final String NASA_URL = "https://www.nasa.gov/";

    public static Call<RSS> createCallForBreakingNews() {
        return createCall(BREAKING_NEWS_SUFFIX);
    }

    public static Call<RSS> createCallForEducationNews() {
        return createCall(EDU_NEWS_SUFFIX);
    }

    private static Call<RSS> createCall(String suffix) {
        return getService().getRss(suffix);
    }

    private static NasaRssService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NASA_URL).addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        return retrofit.create(NasaRssService.class);
    }
}
