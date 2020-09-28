package com.example.fallanddead;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class RegisterActivity extends AppCompatActivity {

    EditText edit_name;
    Button qrcode_button;
    Button register_button;
    TextView qr_text;
    EditText content_text;
    IntentIntegrator integrator;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result!=null){
            if(result.getContents()==null){}
            else{

                qr_text.setText(result.getContents());

            }
        } else{
            super.onActivityResult(requestCode, resultCode, data);


        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        integrator=new IntentIntegrator(this);

        edit_name=findViewById(R.id.editTextTextPersonName2);
        content_text=findViewById(R.id.editTextTextPersonName);
        qr_text=findViewById(R.id.textView6);

        qrcode_button=findViewById(R.id.button);
        qrcode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                integrator.setPrompt("바코드를 사각형 안에 비춰주세요");
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);


                integrator.initiateScan();

            }
        });


        register_button=findViewById(R.id.button2);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent();
                intent.putExtra("name",edit_name.getText().toString());
                intent.putExtra("mac",qr_text.getText().toString());
                intent.putExtra("content",content_text.getText().toString());

                setResult(RESULT_OK,intent); //RESULT_OK=-1임
                finish();


            }
        });



    }
}