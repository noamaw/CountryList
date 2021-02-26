package com.example.countrylist.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CountryList {
    @SerializedName("")
    private ArrayList<Country> countries;

    public ArrayList<Country> getCountries() {
        return countries;
    }
}
