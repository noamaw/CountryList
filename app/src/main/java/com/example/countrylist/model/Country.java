package com.example.countrylist.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

public class Country implements Parcelable {
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("nativeName")
        @Expose
        String nativeName;

        @SerializedName("borders")
        @Expose
        List<String> borders = null;

        @SerializedName("area")
        @Expose
        double area;

        protected Country(Parcel in) {
                name = in.readString();
                nativeName = in.readString();
                borders = in.createStringArrayList();
                area = in.readDouble();
        }

        public static final Creator<Country> CREATOR = new Creator<Country>() {
                @Override
                public Country createFromParcel(Parcel in) {
                        return new Country(in);
                }

                @Override
                public Country[] newArray(int size) {
                        return new Country[size];
                }
        };

        public String getName() {
                return name;
        }

        public String getNativeName() {
                return nativeName;
        }

        public List<String> getBorders() {
                return borders;
        }

        public double getArea() {
                return area;
        }

        public static Comparator<Country> englishNameAscendingComparator = (country1, country2) -> {
                String c1 = country1.name.toUpperCase();
                String c2 = country2.name.toUpperCase();

                return c1.compareTo(c2);
        };

        public static Comparator<Country> englishNameDescendingComparator = (country1, country2) -> {
                String c1 = country1.name.toUpperCase();
                String c2 = country2.name.toUpperCase();

                return c2.compareTo(c1);
        };

        public static Comparator<Country> areaAscendingComparator = (country1, country2) -> (int) ((country1.area - country2.area)*100);

        public static Comparator<Country> areaDescendingComparator = (country1, country2) -> (int)((country2.area - country1.area)*100);

        @Override
        public int describeContents() {
                return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
                parcel.writeString(name);
                parcel.writeString(nativeName);
                parcel.writeStringList(borders);
                parcel.writeDouble(area);
        }
}
