package com.bzilaji.rssreader.RSS.model;


import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "item", strict = false)
public class Article implements Parcelable {


    @Element
    private String title;

    @Element
    private String description;

    @Element
    private String link;

    @Element(required = false)
    private String pubDate;

    @Element(name = "enclosure", required = false)
    private Enclosure enclosure;

    public String getImageUrl() {
        if (enclosure != null) {
            if (isImage(enclosure)) {
                return enclosure.getUrl();
            }
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    private boolean isImage(Enclosure enclosure) {
        return enclosure.getType().contains("image");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.link);
        dest.writeString(this.pubDate);
        dest.writeParcelable(this.enclosure, flags);
    }

    public Article() {
    }

    protected Article(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.link = in.readString();
        this.pubDate = in.readString();
        this.enclosure = in.readParcelable(Enclosure.class.getClassLoader());
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
