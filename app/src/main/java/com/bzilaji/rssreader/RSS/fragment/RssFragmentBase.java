package com.bzilaji.rssreader.RSS.fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bzilaji.rssreader.R;
import com.bzilaji.rssreader.RSS.history.History;
import com.bzilaji.rssreader.RSS.history.HistoryImpl;
import com.bzilaji.rssreader.RSS.model.Article;
import com.bzilaji.rssreader.RSS.model.RSS;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public abstract class RssFragmentBase extends Fragment {


    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean downloading = false;
    private Call<RSS> callForBreakingNews;
    private History history;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleAdapter = new ArticleAdapter();
        if (!articleAdapter.restoreState(savedInstanceState)) {
            downloadRss();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (history == null) {
            history = new HistoryImpl(getActivity().getApplicationContext());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        articleAdapter.saveState(outState);
        super.onSaveInstanceState(outState);
    }


    private void downloadRss() {
        cancelCallToDownload();
        callForBreakingNews = createCallToRss();
        downloading = true;
        callForBreakingNews.enqueue(new Callback<RSS>() {
            @Override
            public void onResponse(Call<RSS> call, Response<RSS> response) {
                downloading = false;
                articleAdapter.setArticles(response.body().getChannel().getArticleList());
                hideRefreshView();
            }

            @Override
            public void onFailure(Call<RSS> call, Throwable t) {
                downloading = false;
                Toast.makeText(getActivity(), R.string.download_error, Toast.LENGTH_SHORT).show();
                hideRefreshView();
            }
        });
    }

    abstract protected Call<RSS> createCallToRss();


    private void cancelCallToDownload() {
        if (callForBreakingNews != null) {
            callForBreakingNews.cancel();
        }
    }

    private void hideRefreshView() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.breaking_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(articleAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadRss();
            }
        });
        refreshSwipeLayoutState();
    }

    private void refreshSwipeLayoutState() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(downloading);
            }
        });
    }

    @Override
    public void onDestroyView() {
        recyclerView = null;
        swipeRefreshLayout = null;
        super.onDestroyView();
    }

    private class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

        private List<Article> articles;
        private List<Article> filteredArticles;
        private FilterTask filterTask;
        private CharSequence searchTerm;


        public void setArticles(List<Article> articles) {
            this.articles = articles;
            filteredArticles = new ArrayList<>(articles);
            startFiltering();
        }

        public void setSearchTerm(CharSequence searchTerm) {
            this.searchTerm = searchTerm;
            startFiltering();
        }

        @Override
        public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ArticleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ArticleViewHolder holder, int position) {
            holder.setModel(filteredArticles.get(position));
        }


        @Override
        public int getItemCount() {
            return filteredArticles != null ? filteredArticles.size() : 0;
        }

        private void startFiltering() {
            if (articles != null && articles.size() > 0) {
                if (filterTask != null) {
                    filterTask.cancel(true);
                }
                filterTask = new FilterTask(this, articles, searchTerm);
                filterTask.execute();
            }
        }

        public void notifyItemChanged(Article article) {
            if (filteredArticles.contains(article)) {
                notifyItemChanged(filteredArticles.indexOf(article));
            }
        }

        public boolean restoreState(Bundle inState) {
            if (inState != null && inState.containsKey("KEY_ITEMS")) {
                setArticles(inState.<Article>getParcelableArrayList("KEY_ITEMS"));
                return true;
            }
            return false;
        }

        public void saveState(Bundle outState) {
            if (outState != null && articles != null) {
                outState.putParcelableArrayList("KEY_ITEMS", new ArrayList<Parcelable>(articles));
            }
        }

        private class FilterTask extends AsyncTask<Void, Void, List<Article>> {

            private final ArticleAdapter adapter;
            private final List<Article> originalList;
            private final CharSequence searchTerm;

            private FilterTask(ArticleAdapter adapter, List<Article> originalList, CharSequence searchTerm) {
                this.adapter = adapter;
                this.originalList = new LinkedList<Article>(originalList);
                this.searchTerm = searchTerm;
            }

            @Override
            protected List<Article> doInBackground(Void... params) {
                List<Article> results = new ArrayList<Article>();
                if (TextUtils.isEmpty(searchTerm)) {
                    results.addAll(originalList);
                } else {
                    for (final Article product : originalList) {
                        if (isCancelled()) {
                            return results;
                        }
                        final String title = product.getTitle().toLowerCase();
                        if (title.contains(searchTerm.toString().toLowerCase())) {
                            results.add(product);
                        }
                    }
                }
                return results;
            }

            @Override
            protected void onPostExecute(List<Article> products) {
                adapter.filteredArticles.clear();
                adapter.filteredArticles.addAll(products);
                adapter.notifyDataSetChanged();
            }

        }
    }

    private class ArticleViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;
        private TextView desc;
        private Article article;
        private View visitedIndicator;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemSelected(article);
                }
            });
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            visitedIndicator = itemView.findViewById(R.id.visited_indicator);
        }


        public void setModel(Article article) {
            this.article = article;
            Picasso.with(itemView.getContext()).load(article.getImageUrl()).into(image);
            title.setText(article.getTitle());
            desc.setText(article.getDescription());
            visitedIndicator.setVisibility(history.isVisited(article.getLink()) ? VISIBLE : GONE);
        }
    }

    private void itemSelected(Article article) {
        try {
            startExternalBrowser(Uri.parse(article.getLink()));
            addToHistory(article);
        } catch (Exception ex) {
            //invalid url
        }
    }

    private void addToHistory(Article article) {
        history.addToHistory(article.getLink());
        articleAdapter.notifyItemChanged(article);
    }

    private void startExternalBrowser(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager manager = getContext().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            startActivity(intent);
        }
    }

    public void setSearchTerm(CharSequence searchTerm) {
        articleAdapter.setSearchTerm(searchTerm);
    }

    @Override
    public void onDestroy() {
        cancelCallToDownload();
        super.onDestroy();
    }

    public void refreshContent() {
        if (!downloading) {
            if (swipeRefreshLayout != null) {
                refreshSwipeLayoutState();
            }
            downloadRss();
        }
    }

}


