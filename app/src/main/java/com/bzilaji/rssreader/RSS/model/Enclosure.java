package com.bzilaji.rssreader.RSS.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Enclosure implements Parcelable {

    @Attribute
    private String url;

    @Attribute
    private String type;



    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.type);
    }

    public Enclosure() {
    }

    protected Enclosure(Parcel in) {
        this.url = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Enclosure> CREATOR = new Parcelable.Creator<Enclosure>() {
        @Override
        public Enclosure createFromParcel(Parcel source) {
            return new Enclosure(source);
        }

        @Override
        public Enclosure[] newArray(int size) {
            return new Enclosure[size];
        }
    };
}
