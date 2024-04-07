package com.example.evenz;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;

public class ImageOptionsFragment extends DialogFragment {

    private ImageOptionsListener listener;

    public interface ImageOptionsListener {
        void onSelectImage();
        void onDeleteImage();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.image_options_fragment, null);

        builder.setView(view)
                .setTitle("Select an action");

        view.findViewById(R.id.uploadImageButton).setOnClickListener(v -> {
            listener.onSelectImage();
            dismiss(); // Close the dialog
        });
        view.findViewById(R.id.deleteImageButton).setOnClickListener(v -> {
            listener.onDeleteImage();
            dismiss(); // Close the dialog
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ImageOptionsListener) {
            listener = (ImageOptionsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ImageOptionsListener");
        }
    }
}