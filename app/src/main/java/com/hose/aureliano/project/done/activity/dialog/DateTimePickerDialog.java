package com.hose.aureliano.project.done.activity.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.utils.ActivityUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Header.
 * <p>
 * Date: 3/7/2018.
 *
 * @author Uladzislau_Shalamits
 */
public class DateTimePickerDialog extends AlertDialog {

    private Context context;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private GregorianCalendar calendar;
    private OkClickListener listener;
    private View view;
    private boolean timeIsSet;

    DateTimePickerDialog(Context context, Long dateTimeInMilliseconds, boolean timeIsSet, OkClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.timeIsSet = timeIsSet;
        calendar = new GregorianCalendar();
        if (dateTimeInMilliseconds != null) {
            calendar.setTimeInMillis(dateTimeInMilliseconds);
        }
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.date_time_picker_dialog_layout, null);
        datePicker = view.findViewById(R.id.layoutDatePicker);
        timePicker = view.findViewById(R.id.layoutTimePicker);
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);
        timePicker.setIs24HourView(true);
        setView(view);

        String neutralButtonText = context.getString(R.string.date_time_picker_time);

        if (timeIsSet) {
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            neutralButtonText = ActivityUtils.getStringTime(context, calendar.getTimeInMillis());
        }

        setButton(BUTTON_POSITIVE, context.getString(R.string.ok), (OnClickListener) null);
        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), (OnClickListener) null);
        setButton(BUTTON_NEUTRAL, neutralButtonText, (OnClickListener) null);

        setOnShowListener(dialog -> {
            Button buttonNeutral = ((AlertDialog) dialog).getButton(BUTTON_NEUTRAL);
            Button buttonNegative = ((AlertDialog) dialog).getButton(BUTTON_NEGATIVE);
            Button buttonPositive = ((AlertDialog) dialog).getButton(BUTTON_POSITIVE);
            buttonNeutral.setOnClickListener(buttonNeutralView -> {
                if (timePicker.getVisibility() != View.VISIBLE) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    buttonNeutral.setVisibility(View.GONE);
                    buttonNegative.setText(R.string.date_time_picker_without_time);
                }
            });
            buttonNegative.setOnClickListener(buttonNegativeView -> {
                if (timePicker.getVisibility() == View.VISIBLE) {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                    buttonNeutral.setVisibility(View.VISIBLE);
                    buttonNeutral.setText(R.string.date_time_picker_time);
                    buttonNegative.setText(R.string.no);
                    timeIsSet = false;
                } else {
                    dismiss();
                }
            });
            buttonPositive.setOnClickListener(buttonPositiveView -> {
                if (timePicker.getVisibility() == View.VISIBLE) {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                    buttonNeutral.setVisibility(View.VISIBLE);
                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                    gregorianCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    gregorianCalendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    buttonNeutral.setText(ActivityUtils.getStringTime(context, gregorianCalendar.getTimeInMillis()));
                    buttonNegative.setText(R.string.no);
                    timeIsSet = true;
                } else {
                    Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(),
                            datePicker.getDayOfMonth());
                    if (timeIsSet) {
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    }
                    listener.okButtonClick(calendar.getTimeInMillis(), timeIsSet);
                    dismiss();
                }
            });
        });
    }

    public interface OkClickListener {
        void okButtonClick(long dateTimeInMilliseconds, boolean isTimeSet);
    }
}
