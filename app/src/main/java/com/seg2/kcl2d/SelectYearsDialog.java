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

public class SelectYearsDialog extends DialogFragment {

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
        firstYearPicker.setMinValue(1960);
        final NumberPicker lastYearPicker = (NumberPicker) view.findViewById(R.id.end_year);
        lastYearPicker.setMinValue(1970);
        setUpNumberPicker(lastYearPicker);

        // Set default values to the previously selected ones
        if(this.getArguments() != null) {
            firstYearPicker.setValue(this.getArguments().getInt("firstYear"));
            lastYearPicker.setValue(this.getArguments().getInt("lastYear"));
        }


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

                firstYear = Integer.valueOf(firstYearValue).toString();
                lastYear = Integer.valueOf(secondYearValue).toString();

                if (secondYearValue - firstYearValue < 10){
                    createSelectionErrorDialog();
                } else {
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
        picker.setWrapSelectorWheel(true);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void createSelectionErrorDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please select a range of at least 10 years.")
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
