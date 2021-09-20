package com.example.facedetect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button button;
    private final static int REQUEST_IMAGE_CAPTURE=124;
    FirebaseVisionImage firebaseVision;
    FirebaseVisionFaceDetector visionFaceDetector;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.detect);
        imageView=findViewById(R.id.imageView);
        FirebaseApp.initializeApp(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile();


            }
        });

    }



    private void openFile() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }
        else {
            Toast.makeText(MainActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
        }


    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RESULT_OK) {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Bundle bundle=data.getExtras();
                    Bitmap bitmap=(Bitmap)bundle.get("data");
                    vision(bitmap);
                }
            }


        }

        private void vision(Bitmap bitmap){
        FirebaseVisionFaceDetectorOptions options=new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();


        try {
            firebaseVision= FirebaseVisionImage.fromBitmap(bitmap);
            visionFaceDetector=FirebaseVision.getInstance().getVisionFaceDetector(options);

        } catch (Exception e) {
            e.printStackTrace();
        }

        visionFaceDetector.detectInImage(firebaseVision).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                String text="";
                int i=1;
                for(FirebaseVisionFace visionFace:firebaseVisionFaces){
                    text=text.concat("\nSmile:" +visionFace.getSmilingProbability()*100 + "%")
                            .concat("LeftEyeOpen:"+visionFace.getLeftEyeOpenProbability()*100 +"%")
                            .concat("RightEyeOpen:"+visionFace.getRightEyeOpenProbability()*100 +"%")
                            .concat("trackingId:"+visionFace.getTrackingId()+i+":");
                    i++;


                    if(firebaseVisionFaces.size()==0){
                        Toast.makeText(MainActivity.this,"No detection",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Bundle bundle=new Bundle();
                        bundle.putString(LCOFaceDetection.RESULT_TEXT,text);
                        DialogFragment dialogFragment=new ResultDialog();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.setCancelable(true);
                        dialogFragment.show(getSupportFragmentManager(),LCOFaceDetection.RESULT_DIALOG);
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                  Toast.makeText(MainActivity.this,"Not Detect Image",Toast.LENGTH_SHORT).show();
            }
        });

        }

}