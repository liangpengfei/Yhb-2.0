package com.example.fei.yhb_20.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.fei.yhb_20.R;
import com.marshalchen.common.uimodule.blurdialogfragment.BlurDialogFragment;

/**
 * 暂且废止不用，以后优化界面的时候使用
 * Email luckyliangfei@gmail.com
 * Created by fei on 2/22/15.
 */
public class Photo extends BlurDialogFragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "Photo";

    private LinearLayout gallery;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gallery = (LinearLayout) getActivity().findViewById(R.id.ll_gallery);
    }

    /**
     * 直接生成Dialog
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View customView = getActivity().getLayoutInflater().inflate(R.layout.add_dialog, null);

        ImageButton take = (ImageButton) customView.findViewById(R.id.takephoto);
        ImageButton gallery = (ImageButton) customView.findViewById(R.id.gallery);

        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        builder.setView(customView);
        return builder.create();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageBitmap(imageBitmap);
            gallery.addView(imageView);
            Log.d(TAG,"onactivityResult");
        }
    }
}
