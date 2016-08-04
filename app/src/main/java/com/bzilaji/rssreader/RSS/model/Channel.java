package com.bzilaji.rssreader.RSS.model;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@Root(name = "channel", strict = false)
public class Channel {
    @ElementList(name = "item", inline = true)
    private List<Article> articleList;
    @Element
    private String title;

    // Can have multiple link in other namespace
    @ElementList(entry = "link", inline = true, required = false)
    public List<Link> links;

    @Element
    private String description;

    public List<Article> getArticleList() {
        return articleList;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


}

