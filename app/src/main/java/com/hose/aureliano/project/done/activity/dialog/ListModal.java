package com.hose.aureliano.project.done.activity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.component.CustomTextWatcher;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by evere on 05.02.2018.
 */
public class ListModal extends DialogFragment {

    private DialogListener listener;
    private EditText textField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.list_new));
        View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.modal_list, (ViewGroup) getView(), false);

        textField = view.findViewById(R.id.list_name);

        Bundle bundle = getArguments();
        if (null != bundle) {
            textField.setText((String) bundle.get("name"));
        }

        builder.setView(view);
        builder.setPositiveButton(getString(R.string.confirm), (dialog, i) -> listener.confirm(this));
        builder.setNegativeButton(getString(R.string.cancel), (dialog, i) -> {
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
            textField.addTextChangedListener(
                    new CustomTextWatcher(text -> positiveButton.setEnabled(StringUtils.isNoneBlank(text))));
        });
        return alertDialog;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    public interface DialogListener {
        void confirm(DialogFragment dialog);
    }
}
