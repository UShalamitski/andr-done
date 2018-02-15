package com.hose.aureliano.project.done.activity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hose.aureliano.project.done.R;

/**
 * Created by evere on 05.02.2018.
 */

public class TaskModal extends DialogFragment {

    private ListModal.NoticeDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Title");
        View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.modal_task, (ViewGroup) getView(), false);

        Bundle bundle = getArguments();
        if (null != bundle) {
            EditText name = view.findViewById(R.id.tasks_modal_name);
            name.setText((String) bundle.get("name"));
        }

        builder.setView(view);
        builder.setNegativeButton("No", (dialog, i) -> listener.onDialogNegativeClick(this));
        builder.setPositiveButton("Yes", (dialog, i) -> listener.onDialogPositiveClick(this));
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ListModal.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }
}
