package com.example.countrylist.apis;

import com.example.countrylist.model.Country;
import com.example.countrylist.model.CountryList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CountrySearchService {
//    https://restcountries.eu/rest/v2/all?fields=name;capital;currencies
    @GET("all?fields=name;nativeName;area;borders")
    //returns list of all country response by order...
    Call<Country[]> searchCountries();

    String finishingQuery = "?fields=name;nativeName;borders;area";
    //http://restcountries.eu/rest/v2/alpha/bra?fields=name;nativeName;borders;area
    @GET("?fields=name;nativeName;borders;area")
    //returns list of all country response by order...
    Call<Country> searchSpecificCountry(
            @Query("alphaCode") String alphaCode
    );

    @GET("alpha/{alphaCode}?fields=name;nativeName;borders;area")
    Call<Country> getSpecificCountry(@Path(value = "alphaCode", encoded = true) String alphaCode);
}
