package com.bzilaji.rssreader;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.bzilaji.rssreader.RSS.NasaRssCallFactory;
import com.bzilaji.rssreader.RSS.model.Channel;
import com.bzilaji.rssreader.RSS.model.RSS;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class NasaRssCallFactoryTest {


    @Test
    public void testCallBreakingNews() throws IOException {
        Response<RSS> result = NasaRssCallFactory.createCallForBreakingNews().execute();
        Channel channel = result.body().getChannel();
        assertEquals("NASA Breaking News", channel.getTitle());
        assertFalse(channel.getArticleList().isEmpty());
    }

    @Test
    public void testCallEducationNews() throws IOException {
        Response<RSS> result = NasaRssCallFactory.createCallForEducationNews().execute();
        Channel channel = result.body().getChannel();
        assertEquals("Education News", channel.getTitle());
        assertFalse(channel.getArticleList().isEmpty());
    }


}
