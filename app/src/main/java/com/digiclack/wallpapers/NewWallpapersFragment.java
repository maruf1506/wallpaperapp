package com.digiclack.wallpapers;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.digiclack.unsplash.Unsplash;
import com.digiclack.unsplash.api.Order;
import com.digiclack.unsplash.models.Photo;
import com.digiclack.unsplash.models.SearchResults;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NewWallpapersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    String CLIENT_ID ;
    SwipeRefreshLayout mSwipeRefreshLayout;
    GridLayoutManager mLayoutManager;
    private Unsplash unsplash;
    private PhotoRecyclerAdapter adapter;
    private int pageNo = 1;
    private AdView mAdView;
    public NewWallpapersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.new_wallpapers_fragment, container, false);

        CLIENT_ID = getContext().getString(R.string.unsplash_access_key);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        unsplash = new Unsplash(CLIENT_ID);

        // load banner ad
        mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setLayoutManager(mLayoutManager);



        adapter = new PhotoRecyclerAdapter(getContext());
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(adapter);
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(this);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        fetchPhotos();

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                // TODO Fetching data from server
                fetchPhotos();
                //don't forget to cancel refresh when work is done
            }
        });

        //next btn


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pageNo++;

                unsplash.getPhotos(pageNo, 30, Order.LATEST, new Unsplash.OnPhotosLoadedListener() {
                    @Override
                    public void onComplete(List<Photo> photos) {

                        recyclerView.smoothScrollToPosition(0);
                        adapter.setPhotos(photos);

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });

        //indicate that this fragment has menu_search
        setHasOptionsMenu(true);


        return v;
    }


    @Override
    public void onRefresh() {

        pageNo = 1;
        fetchPhotos();

    }

    private void fetchPhotos() {
        unsplash.getPhotos(pageNo, 30, Order.LATEST, new Unsplash.OnPhotosLoadedListener() {
            @Override
            public void onComplete(List<Photo> photos) {

                adapter.setPhotos(photos);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted

                unsplash.searchPhotos(query, null, null, null, new Unsplash.OnSearchCompleteListener() {
                    @Override
                    public void onComplete(SearchResults results) {
                        Log.d("Photos", "Total Results Found " + results.getTotal());
                        List<Photo> photos = results.getResults();
                        adapter.setPhotos(photos);

                    }

                    @Override
                    public void onError(String error) {
                        Log.d("Unsplash", error);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (!TextUtils.isEmpty(query)) {
                    unsplash.searchPhotos(query, pageNo, 10, null, new Unsplash.OnSearchCompleteListener() {
                        @Override
                        public void onComplete(SearchResults results) {
                            Log.d("Photos", "Total Results Found " + results.getTotal());
                            List<Photo> photos = results.getResults();
                            adapter.setPhotos(photos);

                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                }
                return false;
            }

        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
