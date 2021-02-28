package com.example.countrylist.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.countrylist.R;
import com.example.countrylist.model.Country;
import com.example.countrylist.util.Const;
import com.example.countrylist.viewModels.CountryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CountryListFragment extends Fragment {

    private static final String ARG_LIST_OF_BORDERS = "ListOfBorderingCountries";
    private static final String ARG_COUNTRY_NAME = "CountryToWhomTheyBorder";
    private static final String TAG = "CountryListFragment";

    private ArrayList<Country> countryArrayList;
    private String countryToWhomBorders;
    private String requestedCountryBorders;
    private View rootView;
    private TableLayout tableLayout;
    private CountryViewModel viewModel;
    private TableRow headerRow;
    private TextView headline;
    private int sortingState;

    public CountryListFragment() {
        // Required empty public constructor
    }

    public static CountryListFragment newInstance(List<Country> countryList, String countryName) {
        Log.d(TAG, "new instance started");
        CountryListFragment fragment = new CountryListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LIST_OF_BORDERS, (ArrayList<? extends Parcelable>) countryList);
        args.putString(ARG_COUNTRY_NAME, countryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate started");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            countryArrayList = getArguments().getParcelableArrayList(ARG_LIST_OF_BORDERS);
            countryToWhomBorders = getArguments().getString(ARG_COUNTRY_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        rootView = inflater.inflate(R.layout.fragment_country_list, container, false);
        InitializeViewModel();
        initUi();

        if (countryArrayList != null) {
            countryArrayList.sort(Country.englishNameAscendingComparator);
            sortingState = Const.SORTED_ASCENDING_ENGLISH_NAME;
            addListOfCountriesToTable(countryArrayList);
            headline.setText(String.format("%s %s", getString(R.string.borders_of_headline), countryToWhomBorders));
            headline.setVisibility(View.VISIBLE);
        } else {
            viewModel.searchAllCountries();
        }

        return rootView;
    }

    private void InitializeViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);
        viewModel.init();

        viewModel.getCountryResponseLiveData().observe(getViewLifecycleOwner(), countryList -> {
                    if (countryList != null) {
                        addListOfCountriesToTable(countryList);
                    }
                });
        viewModel.getCountryListSortState().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                sortingState = integer;
                setSortingHeaderView();
            }
        });
        viewModel.getCountryBordersResponseLiveData().observe(getViewLifecycleOwner(), this::openNewFragmentShowingBorders);
    }

    private void openNewFragmentShowingBorders(List<Country> countries) {
        CountryListFragment nextFrag= newInstance(countries, requestedCountryBorders);
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)rootView.getParent()).getId(), nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    private void initUi() {
        tableLayout = rootView.findViewById(R.id.country_table);
        tableLayout.removeAllViews();
        headerRow = rootView.findViewById(R.id.table_header);
        setHeader();
        headline = rootView.findViewById(R.id.headline);
    }

    private void setHeader() {
        headerRow.setBackgroundColor(getResources().getColor(R.color.teal_700, null));
        headerRow.findViewById(R.id.english_country_name).setOnClickListener(view -> {
            sortingStateEnglishName();
        });
        headerRow.findViewById(R.id.area_country).setOnClickListener(view -> {
            sortingStateArea();
        });
    }

    private void sortingStateEnglishName() {
        if (countryArrayList != null) {
            if (sortingState != Const.SORTED_ASCENDING_ENGLISH_NAME) {
                countryArrayList.sort(Country.englishNameAscendingComparator);
                sortingState = Const.SORTED_ASCENDING_ENGLISH_NAME;
            } else {
                countryArrayList.sort(Country.englishNameDescendingComparator);
                sortingState = Const.SORTED_DESCENDING_ENGLISH_NAME;
            }
            addListOfCountriesToTable(countryArrayList);
            setSortingHeaderView();
        } else {
            viewModel.sortCountriesByEnglishName();
        }
    }

    private void sortingStateArea() {
        if (countryArrayList != null) {
            if (sortingState != Const.SORTED_ASCENDING_AREA) {
                countryArrayList.sort(Country.areaAscendingComparator);
                sortingState = Const.SORTED_ASCENDING_AREA;
            } else {
                countryArrayList.sort(Country.areaDescendingComparator);
                sortingState = Const.SORTED_DESCENDING_AREA;
            }
            addListOfCountriesToTable(countryArrayList);
            setSortingHeaderView();
        } else {
            viewModel.sortCountriesByArea();
        }
    }

    private void setSortingHeaderView() {
        String englishNameTxt = getString(R.string.english_name);
        String areaTxt = getString(R.string.area);
        switch (sortingState) {
            case Const.SORTED_ASCENDING_ENGLISH_NAME:
                englishNameTxt = String.format("%s %s", getString(R.string.english_name), getString(R.string.ascending));
                areaTxt = getString(R.string.area);
                break;
            case Const.SORTED_DESCENDING_ENGLISH_NAME:
                englishNameTxt = String.format("%s %s", getString(R.string.english_name), getString(R.string.descending));
                areaTxt = getString(R.string.area);
                break;
            case Const.SORTED_ASCENDING_AREA:
                englishNameTxt = getString(R.string.english_name);
                areaTxt = String.format("%s %s", getString(R.string.area), getString(R.string.ascending));
                break;
            case Const.SORTED_DESCENDING_AREA:
                englishNameTxt = getString(R.string.english_name);
                areaTxt = String.format("%s %s", getString(R.string.area), getString(R.string.descending));
                break;
        }
        ((TextView)(headerRow.findViewById(R.id.english_country_name))).setText(englishNameTxt);
        ((TextView)(headerRow.findViewById(R.id.area_country))).setText(areaTxt);
    }

    private void addListOfCountriesToTable(List<Country> countries) {
        tableLayout.removeAllViews();
        for (Country country : countries) {
            addRowToTable(country.getNativeName(), country.getName(), country.getArea(), country.getBorders());
        }
    }

    private void addRowToTable(String nativeName, String englishName, Double areaValue, List<String> borders) {
        TableRow row = (TableRow)LayoutInflater.from(getActivity()).inflate(R.layout.country_table_row, null);
        ((TextView)row.findViewById(R.id.native_country_name)).setText(nativeName);
        ((TextView)row.findViewById(R.id.english_country_name)).setText(englishName);
        ((TextView)row.findViewById(R.id.area_country)).setText(String.format("%s", areaValue));
        row.setOnClickListener(view -> getSpecificCountries(borders, englishName));
        tableLayout.addView(row);
    }

    private void getSpecificCountries(List<String> borders, String country) {
        viewModel.searchSpecificCountry(borders);
        requestedCountryBorders = country;
    }
}