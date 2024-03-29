package com.digiclack.unsplash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.digiclack.unsplash.api.endpoints.CollectionsEndpointInterface;
import com.digiclack.unsplash.api.endpoints.StatsEndpointInterface;
import com.digiclack.unsplash.models.Collection;
import com.digiclack.unsplash.models.Download;
import com.digiclack.unsplash.models.Photo;
import com.digiclack.unsplash.api.HeaderInterceptor;
import com.digiclack.unsplash.api.Order;
import com.digiclack.unsplash.api.endpoints.PhotosEndpointInterface;
import com.digiclack.unsplash.models.SearchResults;
import com.digiclack.unsplash.models.Stats;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Unsplash {

    private static final String BASE_URL = "https://api.unsplash.com/";

    public static final String ORIENTATION_PORTRAIT = "portrait";
    public static final String ORIENTATION_LANDSCAPE = "landscape";
    public static final String ORIENTATION_SQUARISH = "squarish";

    private PhotosEndpointInterface photosApiService;
    private CollectionsEndpointInterface collectionsApiService;
    private StatsEndpointInterface statsApiService;
    private String TAG = "Unsplash";

    public Unsplash(String clientId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(clientId)).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        photosApiService = retrofit.create(PhotosEndpointInterface.class);
        collectionsApiService = retrofit.create(CollectionsEndpointInterface.class);
        statsApiService = retrofit.create(StatsEndpointInterface.class);
    }

    public void getPhotos(Integer page, Integer perPage, Order order, final OnPhotosLoadedListener listener) {
        Call<List<Photo>> call = photosApiService.getPhotos(page, perPage, order.getOrder());
        call.enqueue(getMultiplePhotoCallback(listener));
    }

    @Deprecated
    public void getCuratedPhotos(Integer page, Integer perPage, Order order, final OnPhotosLoadedListener listener) {
        Call<List<Photo>> call = photosApiService.getCuratedPhotos(page, perPage, order.getOrder());
        call.enqueue(getMultiplePhotoCallback(listener));
    }

    public void getPhoto(@NonNull String id, final OnPhotoLoadedListener listener) {
        getPhoto(id, null, null, listener);
    }

    public void getRandomPhoto(@Nullable String collections,
                               @Nullable Boolean featured, @Nullable String username,
                               @Nullable String query, @Nullable Integer width,
                               @Nullable Integer height, @Nullable String orientation, OnPhotoLoadedListener listener) {
        Call<Photo> call = photosApiService.getRandomPhoto(collections, featured, username, query, width, height, orientation);
        call.enqueue(getSinglePhotoCallback(listener));
    }

    public void getRandomPhotos(@Nullable String collections,
                                @Nullable Boolean featured, @Nullable String username,
                                @Nullable String query, @Nullable Integer width,
                                @Nullable Integer height, @Nullable String orientation,
                                @Nullable Integer count, OnPhotosLoadedListener listener) {
        Call<List<Photo>> call = photosApiService.getRandomPhotos(collections, featured, username, query, width, height, orientation, count);
        call.enqueue(getMultiplePhotoCallback(listener));
    }

    public void searchPhotos(@NonNull String query, OnSearchCompleteListener listener) {
        searchPhotos(query, null, null, null, listener);
    }

    public void searchPhotos(@NonNull String query, @Nullable Integer page, @Nullable Integer perPage, @Nullable String orientation, OnSearchCompleteListener listener) {
        Call<SearchResults> call = photosApiService.searchPhotos(query, page, perPage, orientation);
        call.enqueue(getSearchResultsCallback(listener));

    }

    public void getPhotoDownloadLink(@NonNull String id, final OnLinkLoadedListener listener) {
        Call<Download> call = photosApiService.getPhotoDownloadLink(id);
        call.enqueue(new UnsplashCallback<Download>() {
                         @Override
                         void onComplete(Download response) {
                             listener.onComplete(response);
                         }

                         @Override
                         void onError(Call<Download> call, String message) {
                             listener.onError(message);
                         }
                     }
        );
    }

    public void getPhoto(@NonNull String id, @Nullable Integer width, @Nullable Integer height, final OnPhotoLoadedListener listener) {
        Call<Photo> call = photosApiService.getPhoto(id, width, height);
        call.enqueue(getSinglePhotoCallback(listener));
    }

    public void getCollections(Integer page, Integer perPage, final OnCollectionsLoadedListener listener) {
        Call<List<Collection>> call = collectionsApiService.getCollections(page, perPage);
        call.enqueue(getMultipleCollectionsCallback(listener));
    }

    public void getFeaturedCollections(Integer page, Integer perPage, final OnCollectionsLoadedListener listener) {
        Call<List<Collection>> call = collectionsApiService.getFeaturedCollections(page, perPage);
        call.enqueue(getMultipleCollectionsCallback(listener));
    }

    @Deprecated
    public void getCuratedCollections(Integer page, Integer perPage, final OnCollectionsLoadedListener listener) {
        Call<List<Collection>> call = collectionsApiService.getCuratedCollections(page, perPage);
        call.enqueue(getMultipleCollectionsCallback(listener));
    }

    public void getRelatedCollections(String id, final OnCollectionsLoadedListener listener) {
        Call<List<Collection>> call = collectionsApiService.getRelatedCollections(id);
        call.enqueue(getMultipleCollectionsCallback(listener));
    }

    public void getCollection(String id, final OnCollectionLoadedListener listener) {
        Call<Collection> call = collectionsApiService.getCollection(id);
        call.enqueue(getSingleCollectionCallback(listener));
    }

    @Deprecated
    public void getCuratedCollection(String id, final OnCollectionLoadedListener listener) {
        Call<Collection> call = collectionsApiService.getCuratedCollection(id);
        call.enqueue(getSingleCollectionCallback(listener));
    }

    public void getCollectionPhotos(String id, Integer page, Integer perPage, final OnPhotosLoadedListener listener) {
        Call<List<Photo>> call = collectionsApiService.getCollectionPhotos(id, page, perPage);
        call.enqueue(getMultiplePhotoCallback(listener));
    }

    @Deprecated
    public void getCuratedCollectionPhotos(String id, Integer page, Integer perPage, final OnPhotosLoadedListener listener) {
        Call<List<Photo>> call = collectionsApiService.getCuratedCollectionPhotos(id, page, perPage);
        call.enqueue(getMultiplePhotoCallback(listener));
    }

    public void getStats(final OnStatsLoadedListener listener) {
        Call<Stats> call = statsApiService.getStats();
        call.enqueue(new UnsplashCallback<Stats>() {
                         @Override
                         void onComplete(Stats response) {
                             listener.onComplete(response);
                         }

                         @Override
                         void onError(Call<Stats> call, String message) {
                             listener.onError(message);
                         }
                     }
        );
    }

    // CALLBACKS

    private Callback<Photo> getSinglePhotoCallback(final OnPhotoLoadedListener listener) {
        return new UnsplashCallback<Photo>() {
            @Override
            void onComplete(Photo response) {
                listener.onComplete(response);
            }

            @Override
            void onError(Call<Photo> call, String message) {
                listener.onError(message);
            }
        };
    }

    private Callback<List<Photo>> getMultiplePhotoCallback(final OnPhotosLoadedListener listener) {
        return new UnsplashCallback<List<Photo>>() {
            @Override
            void onComplete(List<Photo> response) {

                listener.onComplete(response);
            }

            @Override
            void onError(Call<List<Photo>> call, String message) {
                Log.d(TAG, "Url = " + call.request().url());
                listener.onError(message);
            }
        };
    }

    private Callback<Collection> getSingleCollectionCallback(final OnCollectionLoadedListener listener) {
        return new UnsplashCallback<Collection>() {
            @Override
            void onComplete(Collection response) {
                listener.onComplete(response);
            }

            @Override
            void onError(Call<Collection> call, String message) {
                listener.onError(message);
            }
        };
    }

    private Callback<SearchResults> getSearchResultsCallback(final OnSearchCompleteListener listener) {
        return new UnsplashCallback<SearchResults>() {
            @Override
            void onComplete(SearchResults response) {
                listener.onComplete(response);
            }

            @Override
            void onError(Call<SearchResults> call, String message) {
                listener.onError(message);
            }
        };
    }

    private Callback<List<Collection>> getMultipleCollectionsCallback(final OnCollectionsLoadedListener listener) {
        return new UnsplashCallback<List<Collection>>() {
            @Override
            void onComplete(List<Collection> response) {
                listener.onComplete(response);
            }

            @Override
            void onError(Call<List<Collection>> call, String message) {
                listener.onError(message);
            }
        };
    }

    private abstract class UnsplashCallback<T> implements Callback<T> {

        abstract void onComplete(T response);

        abstract void onError(Call<T> call, String message);

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            int statusCode = response.code();
            Log.d(TAG, "Status Code = " + statusCode);
            if (statusCode == 200) {
                onComplete(response.body());
            } else if (statusCode >= 400) {
                onError(call, String.valueOf(statusCode));

                if (statusCode == 401) {
                    Log.d(TAG, "Unauthorized, Check your client Id");
                }
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            onError(call, t.getMessage());
        }
    }

    public interface OnPhotosLoadedListener {
        void onComplete(List<Photo> photos);

        void onError(String error);
    }

    public interface OnSearchCompleteListener {
        void onComplete(SearchResults results);

        void onError(String error);
    }

    public interface OnPhotoLoadedListener {
        void onComplete(Photo photo);

        void onError(String error);
    }

    public interface OnLinkLoadedListener {

        void onComplete(Download downloadLink);

        void onError(String error);
    }

    public interface OnCollectionsLoadedListener {
        void onComplete(List<Collection> collections);

        void onError(String error);
    }

    public interface OnCollectionLoadedListener {
        void onComplete(Collection photos);

        void onError(String error);
    }

    public interface OnStatsLoadedListener {
        void onComplete(Stats stats);

        void onError(String error);
    }
}
