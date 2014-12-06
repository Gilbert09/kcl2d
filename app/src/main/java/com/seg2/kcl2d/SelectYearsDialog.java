package com.seg2.kcl2d;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class SelectYearsDialog extends DialogFragment{

    SelectYearsDialogListener mListener;
    private String firstYear;
    private String lastYear;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.year_picker_dialog, null);

        builder.setTitle("Select Time Range");
        builder.setMessage("Select the range of the data you wish to be displayed.");
        builder.setView(view);

        final NumberPicker firstYearPicker = (NumberPicker) view.findViewById(R.id.start_year);
        setUpNumberPicker(firstYearPicker);
        final NumberPicker lastYearPicker = (NumberPicker) view.findViewById(R.id.end_year);
        setUpNumberPicker(lastYearPicker);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogNegativeClick(SelectYearsDialog.this);
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                int firstYearValue = firstYearPicker.getValue();
                int secondYearValue = lastYearPicker.getValue();
                firstYear = new Integer(firstYearValue).toString();
                lastYear = new Integer(secondYearValue).toString();

                if(firstYearValue >= secondYearValue){
                    createSelectionErrorDialog();
                }else {
                    mListener.onDialogPositiveClick(SelectYearsDialog.this, firstYear, lastYear);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SelectYearsDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    public interface SelectYearsDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String firstYear, String lastYear);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private void setUpNumberPicker(NumberPicker picker){
        picker.setMaxValue(2010);
        picker.setMinValue(1960);
        picker.setWrapSelectorWheel(true);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void createSelectionErrorDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Final year cannot be less than or equal to first year. Please select again.")
                .setTitle("Selection Error")
                .setCancelable(false)
                .setNegativeButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }
}
