package com.example.countrylist.model;


import android.util.Log;

import com.example.countrylist.apis.CountrySearchService;
import com.example.countrylist.util.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class CountriesRepository {
    private static final String COUNTRY_SEARCH_SERVICE_BASE_URL = "https://restcountries.eu/rest/v2/";
    private static final String TAG = "CountriesRepo";

    private Runnable networkRunnable;
    private CountrySearchService countrySearchService;
    private MutableLiveData<List<Country>> countryResponseMutableLiveData;
    private MutableLiveData<List<Country>> specificCountryResponseMutableLiveData;
    private MutableLiveData<Integer> sortingStateLiveData;
    private int sortingState;
    private int amountOfExpectedBorders = 0;
    private List<Country> borderCountries;

    public CountriesRepository() {
        sortingState = Const.SORTED_ASCENDING_ENGLISH_NAME;
        countryResponseMutableLiveData = new MutableLiveData<>();
        specificCountryResponseMutableLiveData = new MutableLiveData<>();
        sortingStateLiveData = new MutableLiveData<>();
        borderCountries = new ArrayList<>();

        OkHttpClient client = new OkHttpClient.Builder().build();

        countrySearchService = new retrofit2.Retrofit.Builder()
                .baseUrl(COUNTRY_SEARCH_SERVICE_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CountrySearchService.class);
    }

    public void searchForCountries() {
        networkRunnable = this::searchAllCountries;
        networkRunnable.run();
    }

    public void searchForSpecificCountries(List<String> borders) {
        borderCountries.clear();
        networkRunnable = () -> {
            amountOfExpectedBorders = borders.size();
            for (String border : borders) {
                searchSpecificCountries(border);
            }
        };
        networkRunnable.run();
    }

    private void searchAllCountries() {
        countrySearchService.searchCountries()
                .enqueue(new Callback<Country[]>() {
                    @Override
                    public void onResponse(Call<Country[]> call, Response<Country[]> response) {
                        if (response.isSuccessful()) {
                            Country[] countries = response.body();
                            countryResponseMutableLiveData.postValue(Arrays.asList(countries));
                        }
                    }

                    @Override
                    public void onFailure(Call<Country[]> call, Throwable t) {
                        Log.d(TAG, "onFailure: didnt work = " +t.getMessage());
                        countryResponseMutableLiveData.postValue(null);
                    }
                });
    }

    public void searchSpecificCountries(String country) {
        countrySearchService.getSpecificCountry(country)
                .enqueue(new Callback<Country>() {
                    @Override
                    public void onResponse(Call<Country> call, Response<Country> response) {
                        if (response.body() != null) {
                            borderCountries.add(response.body());
                            if (borderCountries.size() == amountOfExpectedBorders) {
                                Log.d(TAG, "finished requesting all the borders" + borderCountries.toString());
                                specificCountryResponseMutableLiveData.postValue(borderCountries);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Country> call, Throwable t) {
                        Log.d(TAG, "onFailure: didnt work = " +t.getMessage());
                        specificCountryResponseMutableLiveData.postValue(null);
                    }
                });
    }

    public LiveData<List<Country>> getCountryResponseLiveData() {
        return countryResponseMutableLiveData;
    }

    public LiveData<List<Country>> getCountryBordersResponseLiveData() {
        return specificCountryResponseMutableLiveData;
    }

    public LiveData<Integer> getCountriesSortState() {
        return sortingStateLiveData;
    }

    public void sortCountriesByEnglishName() {
        if (sortingState == Const.SORTED_ASCENDING_ENGLISH_NAME) {
            sort(Const.SORTED_DESCENDING_ENGLISH_NAME, Country.englishNameDescendingComparator);
        } else {
            sort(Const.SORTED_ASCENDING_ENGLISH_NAME, Country.englishNameAscendingComparator);
        }
    }

    public void sortCountriesByArea() {
        if (sortingState == Const.SORTED_ASCENDING_AREA) {
            sort(Const.SORTED_DESCENDING_AREA, Country.areaDescendingComparator);
        } else {
            sort(Const.SORTED_ASCENDING_AREA, Country.areaAscendingComparator);
        }
    }

    private void sort(int sortType, Comparator<Country> countryComparator) {
        ((Runnable) () -> {
            List<Country> result = countryResponseMutableLiveData.getValue();
            if (result == null) {
                return;
            }
            result.sort(countryComparator);
            sortingState = sortType;
            countryResponseMutableLiveData.postValue(result);
            sortingStateLiveData.postValue(sortingState);
        }).run();
    }

}