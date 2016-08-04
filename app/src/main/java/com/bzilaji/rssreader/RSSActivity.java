package com.bzilaji.rssreader;

import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bzilaji.rssreader.RSS.fragment.NasaBreakingNewsFragment;
import com.bzilaji.rssreader.RSS.fragment.RssFragmentBase;
import com.bzilaji.rssreader.view.ToolBarSearchView;
import com.bzilaji.rssreader.view.ToolBarSearchViewListener;

public class RSSActivity extends AppCompatActivity {

    private static final String SEARCH_VISIBILITY = "search_visibility";
    private ToolBarSearchView searchView;
    private RssFragmentBase breakingNewsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        searchView = (ToolBarSearchView) findViewById(R.id.search_bar);
        breakingNewsFragment = (NasaBreakingNewsFragment) getSupportFragmentManager().findFragmentById(R.id.breaking_news);
        breakingNewsFragment.setRetainInstance(true);
        searchView.setListener(new ToolBarSearchViewListener() {
            @Override
            public void onSearchCleared(CharSequence old) {
                breakingNewsFragment.setSearchTerm("");
            }

            @Override
            public void onSearchBarHomePressed() {
                searchView.hide();
            }

            @Override
            public void onSearchTextChanged(CharSequence s) {
                breakingNewsFragment.setSearchTerm(s);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rss_menu, menu);
        VectorDrawableCompat search = getCompactDrawable(R.drawable.ic_search_black_24dp);
        tintToWhite(search);
        menu.findItem(R.id.search).setIcon(search);
        VectorDrawableCompat refresh = getCompactDrawable(R.drawable.ic_refresh_black_24dp);
        tintToWhite(refresh);
        menu.findItem(R.id.refresh).setIcon(refresh);
        return super.onCreateOptionsMenu(menu);
    }

    private VectorDrawableCompat getCompactDrawable(int ic_refresh_black_24dp) {
        return VectorDrawableCompat.create(getResources(), ic_refresh_black_24dp, getTheme());
    }

    private void tintToWhite(VectorDrawableCompat drawable) {
        drawable.setTint(ResourcesCompat.getColor(getResources(), android.R.color.white, getTheme()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;
        if (item.getItemId() == R.id.search) {
            searchView.show();
            handled = true;
        }
        if (item.getItemId() == R.id.refresh) {
            breakingNewsFragment.refreshContent();
            handled = true;
        }
        return handled || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.getVisibility() == View.VISIBLE) {
            searchView.hide();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SEARCH_VISIBILITY, searchView.getVisibility());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(SEARCH_VISIBILITY)) {
            searchView.setVisibility(savedInstanceState.getInt(SEARCH_VISIBILITY));
        }
    }
}
