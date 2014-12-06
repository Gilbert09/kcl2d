package com.seg2.kcl2d;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seg2.kcl2d.json.IndicatorClass;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SimpleValueFormatter;
import lecho.lib.hellocharts.view.LineChartView;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks, SelectYearsDialog.SelectYearsDialogListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * The content this fragment is presenting
     */
    private Country mCountry;

    /**
     * The graph that the data is added to
     */
    private LineChartView chart;

    /**
     * Toast that shows when a point is selected
     */
    private Toast infoToast;


    String indicatorString = null;
    String indicatorNameString;
    IndicatorClass[] population = null;
    static float popMax;
    static float popMin;

    IndicatorClass[] indicator = null;
    static float indicatorMax;
    static float indicatorMin;
    private String firstYear = "1980";
    private String lastYear = "2010";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);

        if (!isConnected()) {
            createNetErrorDialog();
        }

        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_dropdown_item);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {

            String[] indicators = getResources().getStringArray(R.array.indicator_string_array);
            String[] indicatorNames = getResources().getStringArray(R.array.spinner_array);

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                if (isConnected()) {
                    indicatorString = indicators[itemPosition];
                    indicatorNameString = indicatorNames[itemPosition];
                    Toast.makeText(getBaseContext(), "You selected : " + indicatorNames[itemPosition], Toast.LENGTH_SHORT).show();
                    new DownloadJson().execute();
                }
                return false;

            }
        };

        getActionBar().setListNavigationCallbacks(mSpinnerAdapter, navigationListener);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if(isConnected()) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(String countryName, int position) {
        if(isConnected()) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(countryName, position + 1))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        /*switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }*/
        mCountry = CountryData.getCountry(number - 1);

        DownloadJson dj = new DownloadJson();
        dj.execute();
    }

    public void onSectionAttached(String countryName) {

        mCountry = CountryData.getCountry(countryName);

        DownloadJson dj = new DownloadJson();
        dj.execute();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        registerReceiver(mConnReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mConnReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.about:
                Intent i = new Intent(this, HomeScreenActivity.class);
                startActivity(i);
                break;
            case R.id.select_time_range:
                SelectYearsDialog syd = new SelectYearsDialog();
                syd.show(getFragmentManager(), "Dialog");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String firstYear, String lastYear) {
        this.firstYear = firstYear;
        this.lastYear = lastYear;

        new DownloadJson().execute();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";


        private static final String COUNTRY_NAME = "country_name";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public static PlaceholderFragment newInstance(String countryName, int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(COUNTRY_NAME, countryName);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            Bundle args = getArguments();
            if(!args.containsKey(COUNTRY_NAME)) {
                ((MainActivity) activity).onSectionAttached(
                        getArguments().getInt(ARG_SECTION_NUMBER));
            }else{
                ((MainActivity) activity).onSectionAttached(
                        getArguments().getString(COUNTRY_NAME));
            }
        }
    }

    /*
    public void setUpGraph() {
        chart = (LineChartView) findViewById(R.id.country_detail);
        //chart.setValueSelectionEnabled(true);
        LineChartData data = new LineChartData();

        data.setAxisXBottom(new Axis());
        data.setAxisYLeft(new Axis().setHasLines(true));

        List<PointValue> values = new ArrayList<PointValue>();

        for (int i = 0; i < population.length; i++) {
            Indicator populationData = population[i];
            int populationYear = Integer.parseInt(populationData.getDate());
            int populationValue = Integer.parseInt(populationData.getValue());

            values.add(new PointValue(populationYear, populationValue));
        }

        List<Line> lines = new ArrayList<Line>();
        Line line = new Line(values).setColor(Color.argb(255, 48, 170, 211));
        line.setHasLabelsOnlyForSelected(true);
        lines.add(line);

        data.setLines(lines);

        chart.setLineChartData(data);
    }
    */

    private void setUpGraph() {
        final HashMap<PointValue, String> originalPopulations = new HashMap<PointValue, String>();
        LineChartView chart = (LineChartView) findViewById(R.id.country_detail);
        LineChartData data;
        Line line;
        List<PointValue> values;
        List<Line> lines = new ArrayList<Line>();

        values = new ArrayList<PointValue>();

        for (int i = 0; i < population.length; i++) {
            String populationValue = population[i].getValue();
            String populationYear = population[i].getDate();

            float populationValueFloat = Float.parseFloat(populationValue);
            float populationYearFloat = Float.parseFloat(populationYear);

            float scalePopulation = GraphHelper.scaleValues(indicatorMax, indicatorMin, popMax, popMin, populationValueFloat);
            PointValue pv = new PointValue(populationYearFloat, scalePopulation);
            values.add(pv);
            originalPopulations.put(pv, populationValue);
        }

        line = new Line(values);
        line.setColor(Color.parseColor("#CC0000"));
        line.setHasPoints(false);
        lines.add(line);

        values = new ArrayList<PointValue>();

        for (int i = 0; i < indicator.length; i++) {
            if(indicator[i].getValue() != null) {
                float healthValue = Float.parseFloat(indicator[i].getValue());
                float healthYear = Float.parseFloat(indicator[i].getDate());

                values.add(new PointValue(healthYear, healthValue));
            }
        }

        line = new Line(values);
        line.setColor(Color.parseColor("#0099CC"));
        lines.add(line);

        data = new LineChartData(lines);

        // Bottom X Axis
        Axis bottomXAxis = new Axis();
        bottomXAxis.setName("Year");
        bottomXAxis.setMaxLabelChars(5);
        bottomXAxis.setHasLines(true);
        data.setAxisXBottom(bottomXAxis);

        // Left Y Axis
        Axis leftYAxis = new Axis();
        leftYAxis.setName(indicatorNameString);
        leftYAxis.setTextColor(Color.parseColor("#0099CC"));
        leftYAxis.setHasLines(true);
        data.setAxisYLeft(leftYAxis);

        // Right Y Axis
        Axis rightYAxis = new Axis();
        rightYAxis.setName("Population");
        rightYAxis.setTextColor(Color.parseColor("#CC0000"));
        rightYAxis.setFormatter(new HeightValueFormatter(0, null, null));
        rightYAxis.setMaxLabelChars(10);
        data.setAxisYRight(rightYAxis);

        chart.setLineChartData(data);
        chart.setOnValueTouchListener(new LineChartView.LineChartOnValueTouchListener() {
            DecimalFormat df = new DecimalFormat("#.##");

            @Override
            public void onValueTouched(int i, int i2, PointValue pointValue) {
                // Hide toast when a new one should appear
                if (infoToast != null)
                    infoToast.cancel();

                String text = "Year: " + Math.round(pointValue.getX());
                if (i == 0) {
                    text += "\nPopulation: " + originalPopulations.get(pointValue);
                } else {
                    text += "\n" + indicatorNameString + ": " + df.format(pointValue.getY());
                }

                infoToast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
                infoToast.show();
            }

            @Override
            public void onNothingTouched() {

            }
        });
    /*
        Viewport v = chart.getMaximumViewport();
        v.set(v.left, healthPercentRange, v.right, 0);
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v, false);
        */

    }

    private static class HeightValueFormatter extends SimpleValueFormatter {

        public HeightValueFormatter(int digits, char[] prependedText, char[] apendedText) {
            super(digits, true, prependedText, apendedText);
        }

        @Override
        public int formatAutoValue(char[] formattedValue, float[] values, int digits) {

            int index = values.length - 1;
            values[index] = GraphHelper.scaleValues(popMax, popMin, indicatorMax, indicatorMin, values[index]);

            return super.formatAutoValue(formattedValue, values, digits);
        }


    }




    private class DownloadJson extends AsyncTask<JSONArray, String, String[]> {

        ProgressDialog progressDialog;

        private DownloadJson() {
            progressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting Data");
            progressDialog.show();
        }


        @Override
        protected String[] doInBackground(JSONArray... strings) {
            String url;
            JSONArray jsonArray = null;

            StringBuilder builder = new StringBuilder();

            url = "http://api.worldbank.org/countries/"+ mCountry.getId() +"/indicators/SP.POP.TOTL?date=" + firstYear + ":"
                    + lastYear + "&format=json";

            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if(statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    Log.e("DATA JSON ERROR", "Failed to download file");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                jsonArray = new JSONArray(builder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String indicatorUrl;
            JSONArray jsonArrayIndicator = null;

            StringBuilder builderIndicator = new StringBuilder();

            url = "http://api.worldbank.org/countries/"+ mCountry.getId() +"/indicators/"+ indicatorString +"?date=" + firstYear + ":" +
                    lastYear + "&format=json";

            HttpClient clientIndicator = new DefaultHttpClient();
            HttpGet httpGetIndicator = new HttpGet(url);

            try {
                HttpResponse responseIndicator = clientIndicator.execute(httpGetIndicator);
                StatusLine statusLineIndicator = responseIndicator.getStatusLine();
                int statusCodeIndicator = statusLineIndicator.getStatusCode();

                if(statusCodeIndicator == 200) {
                    HttpEntity entityIndicator = responseIndicator.getEntity();
                    InputStream contentIndicator = entityIndicator.getContent();
                    BufferedReader readerIndicator = new BufferedReader(new InputStreamReader(contentIndicator));
                    String lineIndicator;
                    while ((lineIndicator = readerIndicator.readLine()) != null) {
                        builderIndicator.append(lineIndicator);
                    }
                } else {
                    Log.e("DATA JSON ERROR", "Failed to download file");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                jsonArrayIndicator = new JSONArray(builderIndicator.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return new String[]{jsonArray.toString(), jsonArrayIndicator.toString()};
        }

        @Override
        protected void onPostExecute(String[] strings) {

            //We can now access the data direct from the Indicator object.
            //The Indicator[] array object contains each year, so we can
            //easily manipulate the data and use it in the graphs.

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            String populationCountriesString = strings[0];
            String indicatorCountriesString = strings[1];

            try {
                JSONArray populationCountries = new JSONArray(populationCountriesString).getJSONArray(1);
                Gson gson = new GsonBuilder().create();
                population = gson.fromJson(populationCountries.toString(), IndicatorClass[].class);

                float[] populationMinMax = GraphHelper.getRangeMaxMin(populationCountries);
                popMin = populationMinMax[0];
                popMax = populationMinMax[1];


                JSONArray indicatorCountries = new JSONArray(indicatorCountriesString).getJSONArray(1);
                Gson gsonIndicator = new GsonBuilder().create();
                indicator = gsonIndicator.fromJson(indicatorCountries.toString(), IndicatorClass[].class);

                float[] indicatorMinMax = GraphHelper.getRangeMaxMin(indicatorCountries);
                indicatorMin = indicatorMinMax[0];
                indicatorMax = indicatorMinMax[1];

                TextView tv = (TextView)findViewById(R.id.country_name);
                tv.setText(mCountry.getName());

                setUpGraph();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if(noConnectivity){
                Toast.makeText(context, "No connection", Toast.LENGTH_LONG).show();
                createNetErrorDialog();
            }
        }
    };

    /*protected void createNetErrorDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                startActivity(i);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(isConnected()) {
                                    dialog.dismiss();
                                }else{
                                    finish();
                                }
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }*/

    protected void createNetErrorDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
