package com.hose.aureliano.project.done;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by evere on 05.02.2018.
 */

public class AddListModal extends DialogFragment {

    private NoticeDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Title");
        View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.modal_add_list, (ViewGroup) getView(), false);

        Bundle bundle = getArguments();
        if (null != bundle) {
            EditText name = view.findViewById(R.id.list_name);
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
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
