package com.example.trojan0project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

public class RemoveImageFragment extends DialogFragment {
    private Image image;
    private removeImageListener listener;

    interface removeImageListener {
        void removeImage(Image image);
    }

    public RemoveImageFragment(Image image) {
        this.image = image;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof removeImageListener) {
            listener = (RemoveImageFragment.removeImageListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement removeImageListener");
        }
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_remove_image, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        ImageView imageViewFragment = view.findViewById(R.id.fragment_image_view);
        Button deleteImage = view.findViewById(R.id.confirm_button);
        Button noButton = view.findViewById(R.id.cancel_button);

        if (image != null && image.getImageId() != null && !image.getImageId().isEmpty()){
            Log.d("RemoveImageFragment", "Loading image: " + image.getImageId());
            Glide.with(requireContext()).load(image.getImageId()).into(imageViewFragment);
        }

        deleteImage.setOnClickListener(v ->{
            Log.d("RemoveImageFragment", "Delete button clicked.");
            if (listener != null){
                listener.removeImage(image);
            } else{
                Log.e("RemoveImageFragment", "Listener is null.");

            }
            dismiss();
        });

        noButton.setOnClickListener(v ->
                dismiss());

        return builder.create();
    }
}

