package com.bzilaji.rssreader.RSS.model;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class RSS {
    @Element
    private Channel channel;
    @Attribute
    private String version;

    public Channel getChannel() {
        return channel;
    }
}

