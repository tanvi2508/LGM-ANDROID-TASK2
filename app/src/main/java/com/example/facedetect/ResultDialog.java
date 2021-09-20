package com.example.facedetect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultDialog extends DialogFragment {
    Button button;
    TextView textView;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_resultdialog,container,false);
        String text="";
        button=view.findViewById(R.id.ok_btn);
        textView=view.findViewById(R.id.dialog);


        Bundle bundle=getArguments();
        text=bundle.getString(LCOFaceDetection.RESULT_TEXT);
        textView.setText(text);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }


}
