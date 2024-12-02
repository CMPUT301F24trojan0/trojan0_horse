/**
 * A DialogFragment that displays a confirmation dialog for removing an image.
 * It shows the image and asks the user to either confirm or cancel the removal action.
 * The fragment uses the Glide library to load the image into an ImageView.
 * Upon confirmation, the fragment communicates with the hosting activity or fragment
 * through the `removeImageListener` interface to remove the image.
 */

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

    /**
     * Interface for communicating with the host activity/fragment to remove an image.
     */
    interface removeImageListener {
        void removeImage(Image image);
    }

    /**
     * Constructor for the RemoveImageFragment that initializes the image to be removed.
     *
     * @param image The image to be displayed and potentially removed.
     */
    public RemoveImageFragment(Image image) {
        this.image = image;
    }

    /**
     * Called when the fragment is attached to the host context.
     * Ensures that the context implements the `removeImageListener` interface.
     *
     * @param context The context to which the fragment is attached.
     * @throws RuntimeException If the context does not implement `removeImageListener`.
     */
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

    /**
     * Creates and returns the dialog for removing the image.
     * This dialog displays the image and asks the user to either confirm or cancel the removal action.
     *
     * @param savedInstanceState The saved instance state of the fragment.
     * @return A Dialog object representing the remove image confirmation dialog.
     */
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
            Glide.with(requireContext()).load(image.getImageId()).into(imageViewFragment);
        }

        deleteImage.setOnClickListener(v ->{
            if (listener != null){
                listener.removeImage(image);
            }
            dismiss();
        });

        noButton.setOnClickListener(v ->
                dismiss());

        return builder.create();
    }
}

