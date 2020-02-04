package com.example.scheduli.ui.service;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.scheduli.R;
import com.example.scheduli.data.Service;
import com.example.scheduli.ui.mainScreen.MainActivity;
import com.example.scheduli.ui.provider.ProviderActivity;

public class AddServiceActivity extends AppCompatActivity {

    private static final String TAG_ADD_SERVICE = "ADD_SERVICE";
    private EditText serviceName, serviceCost, serviceDuration;
    private Button serviceContinueButton, serviceBackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        initView();

        serviceContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewService();
            }
        });

        serviceBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToServices();
            }
        });
    }

    private void goBackToServices() {
        if (!isFormClear()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("If you Quit now your data wont be saved")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), ProviderActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void createNewService() {
        Log.i(TAG_ADD_SERVICE, "Creating new service");
        displayErrorToUserIfThereIsOne();

        if (!checkifInputValid()) {
            String name = serviceName.getText().toString();
            float cost = Float.parseFloat(serviceCost.getText().toString());

            Service service = new Service();

        }


        Log.i(TAG_ADD_SERVICE, "New Service created successfully");

    }

    private boolean checkifInputValid() {
        return checkIfEmpty(serviceCost) && checkIfEmpty(serviceDuration) && checkIfEmpty(serviceName) && checkIfNumberLegal(serviceCost) && checkIfNumberLegal(serviceDuration);
    }

    private boolean checkIfEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }

    private void displayErrorToUserIfThereIsOne() {
        if (checkIfEmpty(serviceName))
            serviceName.setError("You must fill Service Name");
        if (checkIfEmpty(serviceDuration))
            serviceDuration.setError("You Must fill the Duration of Each Session in Minutes");
        else if (!checkIfNumberLegal(serviceDuration))
            serviceDuration.setError("You Must use numbers only");
        if (checkIfEmpty(serviceCost))
            serviceCost.setError("You must add Your service Cost");
        else if (!checkIfNumberLegal(serviceCost))
            serviceCost.setError("You Must enter a numbers only");
    }

    private boolean checkIfNumberLegal(EditText serviceDuration) {
        try {
            TextUtils.isDigitsOnly(serviceDuration.getText().toString());
            return true;
        } catch (Exception e) {
            serviceDuration.setError("Your Input is not a number, Please enter a number");
            return false;
        }
    }

    private void initView() {
        Log.i(TAG_ADD_SERVICE, "initView()");
        serviceName = findViewById(R.id.ed_service_name);
        serviceCost = findViewById(R.id.ed_service_cost);
        serviceDuration = findViewById(R.id.et_service_duration);
        serviceContinueButton = findViewById(R.id.btn_create_service);
        serviceBackButton = findViewById(R.id.btn_back_service);

        Log.i(TAG_ADD_SERVICE, "finished initView() ");
    }

    private boolean isFormClear() {
        return checkIfEmpty(serviceName) && checkIfEmpty(serviceCost) && checkIfEmpty(serviceDuration);
    }
}
