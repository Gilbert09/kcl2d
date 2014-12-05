package com.seg2.kcl2d;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CountryData {
    public static ArrayList<Country> countries = new ArrayList<Country>();
    public static HashMap<String,Country> countryHashMap = new HashMap<String, Country>();

    public static void loadCountries(Context context) {
        // Remove all items (if any) before adding them (orientation change)
        countries.clear();

        String json;
        JSONArray fullJSONArray;

        try {
            InputStream is = context.getAssets().open("countries_filtered.json");

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

            fullJSONArray = new JSONArray(json);
            JSONArray countriesJSONArray = fullJSONArray.getJSONArray(1);

            JSONObject obj;

            for (int i = 0; i < countriesJSONArray.length(); i++) {
                obj = countriesJSONArray.getJSONObject(i);
                countries.add(new Country(obj.get("id").toString(), obj.get("name").toString()));
                Country country = new Country(obj.get("id").toString(), obj.get("name").toString());
                countryHashMap.put(country.getName(),country);
            }

            // Sort countries based on their name (default is ID which is confusing)
            Collections.sort(countries, new Comparator<Country>() {
                @Override
                public int compare(Country o1, Country o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method searches for a country with the given name
     *
     * @param name The name of the country
     * @return The country if it was found, else null
     */
    public static Country searchCountry(String name) {
        for (Country country : countries) {
            if (country.name.equals(name))
                return country;
        }
        return null;
    }

    public static Country getCountry(int id) {
        return countries.get(id);
    }
}
