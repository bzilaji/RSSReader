package com.bzilaji.rssreader.RSS.service;


import com.bzilaji.rssreader.RSS.model.RSS;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NasaRssService {

    @GET("rss/dyn/{feed_name}")
    Call<RSS> getRss(@Path("feed_name") String user);

}


