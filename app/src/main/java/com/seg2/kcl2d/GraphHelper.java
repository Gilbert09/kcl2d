package com.seg2.kcl2d;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.AxisValue;

public class GraphHelper {

    public static float[] getRangeMaxMin(JSONArray jsonArray) {

        double min = 0;
        double max = 0;

        double tempValueFloat;
        try {
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            Log.i("ff", jsonObject.toString());

            if(jsonObject.isNull("value")) {
                Log.i("isNull", "isNull");
            }

            if(!jsonObject.isNull("value")) {
                min = Double.parseDouble(jsonObject.getString("value"));
                max = min;
            }

            for(int i = 1; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);



                if(!jsonObject.isNull("value")) {
                    String tempValue = jsonObject.getString("value");
                    tempValueFloat = Double.parseDouble(tempValue);
                    if (tempValueFloat < min) {
                        min = tempValueFloat;
                    }
                    if (tempValueFloat > max) {
                        max = tempValueFloat;
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        float minFloat = (float) min;
        float maxFloat = (float) max;

        Log.i("floats", min + "");
        Log.i("floats", max + "");
        Log.i("floats", max - min + "");

        return new float[]{minFloat, maxFloat, maxFloat-minFloat};
    }

    public static float scaleValues(float xMax, float xMin, float yMax, float yMin, float yInput) {
        float scaler = (yInput - yMin) / (yMax - yMin);
        return scaler * (xMax - xMin) + xMin;
    }

    /*public static float getAxisLineRange(float minFloat, float maxFloat, int axisLinesNumber){

        float range = maxFloat - minFloat;
        float unroundedTickSize = range/(axisLinesNumber-1);
        Double.parseDouble(new Float(unroundedTickSize).toString());
        double x = Math.ceil(Math.log10(unroundedTickSize)-1);
        double pow10x = Math.pow(10, x);
        double roundedTickRange = Math.ceil(unroundedTickSize / pow10x) * pow10x;
        return (float)roundedTickRange;
    }

    public static List<AxisValue> getAxesLabel(float minFloat, float maxFloat, int axisLinesNumber){

        float tickRange = getAxisLineRange(minFloat, maxFloat, axisLinesNumber);
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        float j = minFloat;
        for(int i = 0; i <= axisLinesNumber; i++){
            axisValues.add(new AxisValue(j));
            j = j + tickRange;
        }

        return axisValues;
    }*/

}
