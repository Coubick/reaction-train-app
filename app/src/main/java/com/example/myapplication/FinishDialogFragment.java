package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;



public class FinishDialogFragment extends DialogFragment {

    private FinishMenuListener listener;
    private Button exitButton;
    Button restartButton;

    public interface FinishMenuListener {
        void onRestartGame();

        void onExitGame();
    }

    public static FinishDialogFragment newInstance() {
        return new FinishDialogFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FinishMenuListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement FinishMenuListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_finish, null);
        view.setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
        view.setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
        setupViews(view);

        builder.setView(view);

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.7f);
        }

        return dialog;
    }

    private void setupViews(View view) {
        exitButton = view.findViewById(R.id.exit_button);
        restartButton = view.findViewById(R.id.restart_button);

        restartButton.setOnClickListener(v -> {
            listener.onRestartGame();
            dismiss();
        });

        exitButton.setOnClickListener(v -> {
            listener.onExitGame();
            dismiss();
        });
    }
}