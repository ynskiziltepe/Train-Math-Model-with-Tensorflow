package com.yunus.tf_inference_math;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TRAINED_MATH_MODEL = "mathModel.tflite";
    private EditText independedVal;
    private TextView txtInferred, txtInferredAndRounded;
    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale.setDefault(new Locale("en", "US")); // set local language

        setContentView(R.layout.activity_main);

        independedVal = findViewById(R.id.independedValue); // assign global values
        txtInferred = findViewById(R.id.inferredResult); // assign global values
        txtInferredAndRounded = findViewById(R.id.inferredAndRoundedResult); // assign global values

        try {
            // get interpreter handle
            interpreter = new Interpreter(loadModelFile(TRAINED_MATH_MODEL),null);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    // load trained tflite file
    private MappedByteBuffer loadModelFile(String fileName) throws IOException{
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd(fileName);
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long  length = assetFileDescriptor.getLength();
        return  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }

    // get inference
    public float getInference(Float xValue){
        float [] inputVal = new float[1];
        inputVal [0] = xValue;

        float [][] inferenceResult = new float[1][1];
        interpreter.run(inputVal, inferenceResult); // inference value via tensorflow.
        return inferenceResult[0][0]; // return inferred value
    }

    // show result
    public void onClick(View view) {

        if(view.getId() == R.id.btnInference)
        {
            if(independedVal.getText().toString().length() == 0)
            {
                txtInferred.setText(("Please enter a value"));
                txtInferredAndRounded.setText(("Please enter a value"));
            }
            else
            {
                float inferredValue = getInference(Float.parseFloat(independedVal.getText().toString()));
                txtInferred.setText(("Inferred y: " + inferredValue));
                txtInferredAndRounded.setText(("Inferred and rounded y: " + Math.round(inferredValue)));
            }
        }
    }
}