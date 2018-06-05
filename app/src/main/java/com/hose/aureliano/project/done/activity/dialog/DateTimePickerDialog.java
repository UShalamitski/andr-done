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
 * Dialog to select date and time.
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

    public DateTimePickerDialog(Context context, Long dateTimeInMilliseconds, OkClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        calendar = new GregorianCalendar();
        if (dateTimeInMilliseconds != null) {
            calendar.setTimeInMillis(dateTimeInMilliseconds);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.date_time_picker_dialog_layout, null);
        datePicker = view.findViewById(R.id.layoutDatePicker);
        timePicker = view.findViewById(R.id.layoutTimePicker);
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);
        datePicker.setMinDate(System.currentTimeMillis());
        timePicker.setIs24HourView(true);
        setView(view);

        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        setButton(BUTTON_POSITIVE, context.getString(R.string.ok), (OnClickListener) null);
        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), (OnClickListener) null);
        setButton(BUTTON_NEUTRAL, ActivityUtils.getStringTime(context, calendar.getTimeInMillis()), (OnClickListener) null);

        setOnShowListener(dialog -> {
            Button buttonNeutral = ((AlertDialog) dialog).getButton(BUTTON_NEUTRAL);
            Button buttonNegative = ((AlertDialog) dialog).getButton(BUTTON_NEGATIVE);
            Button buttonPositive = ((AlertDialog) dialog).getButton(BUTTON_POSITIVE);
            buttonNeutral.setOnClickListener(buttonNeutralView -> {
                if (timePicker.getVisibility() != View.VISIBLE) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    buttonNeutral.setVisibility(View.GONE);
                    buttonNegative.setVisibility(View.GONE);
                }
            });
            buttonNegative.setOnClickListener(buttonNegativeView -> {
                if (timePicker.getVisibility() == View.VISIBLE) {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                    buttonNeutral.setVisibility(View.VISIBLE);
                    buttonNeutral.setText(R.string.date_time_picker_time);
                    buttonNegative.setText(R.string.no);
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
                } else {
                    Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(),
                            datePicker.getDayOfMonth());
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    listener.okButtonClick(calendar.getTimeInMillis());
                    dismiss();
                }
            });
        });
    }

    public interface OkClickListener {
        void okButtonClick(Long dateTimeInMilliseconds);
    }
}
