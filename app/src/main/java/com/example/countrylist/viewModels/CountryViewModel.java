package com.example.countrylist.viewModels;

import android.app.Application;

import com.example.countrylist.model.CountriesRepository;
import com.example.countrylist.model.Country;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CountryViewModel extends AndroidViewModel {
    private CountriesRepository countriesRepository;
    private LiveData<List<Country>> countryResponseLiveData;
    private LiveData<List<Country>> countryBordersResponseLiveData;
    private LiveData<Integer> countryListSortState;

    public CountryViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        countriesRepository = new CountriesRepository();
        countryResponseLiveData = countriesRepository.getCountryResponseLiveData();
        countryListSortState = countriesRepository.getCountriesSortState();
        countryBordersResponseLiveData = countriesRepository.getCountryBordersResponseLiveData();
    }

    public void searchAllCountries() {
        countriesRepository.searchForCountries();
    }

    public void searchSpecificCountry(List<String> borders) {
        countriesRepository.searchForSpecificCountries(borders);
    }

    public LiveData<List<Country>> getCountryResponseLiveData() {
        return countryResponseLiveData;
    }

    public LiveData<List<Country>> getCountryBordersResponseLiveData() {
        return countryBordersResponseLiveData;
    }

    public LiveData<Integer> getCountryListSortState() {
        return countryListSortState;
    }

    public void sortCountriesByEnglishName() {
        countriesRepository.sortCountriesByEnglishName();
    }

    public void sortCountriesByArea() {
        countriesRepository.sortCountriesByArea();
    }
}