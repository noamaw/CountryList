package com.example.countrylist.apis;

import com.example.countrylist.model.Country;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CountrySearchService {
//    https://restcountries.eu/rest/v2/all?fields=name;capital;currencies
    @GET("all?fields=name;nativeName;area;borders")
    //returns list of all country response by order...
    Call<Country[]> searchCountries();

    //http://restcountries.eu/rest/v2/alpha/{alphaCode}?fields=name;nativeName;borders;area
    @GET("alpha/{alphaCode}?fields=name;nativeName;borders;area")
    Call<Country> getSpecificCountry(@Path(value = "alphaCode", encoded = true) String alphaCode);
}
