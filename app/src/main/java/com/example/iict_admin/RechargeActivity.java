package com.example.iict_admin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.jar.Attributes;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class RechargeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    private DatabaseReference ref;
    private String display_name, balance_str;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(RechargeActivity.this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(RechargeActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]){
        switch (requestCode){
            case REQUEST_CAMERA:
            if(grantResults.length > 0){
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted){
                    Toast.makeText(RechargeActivity.this, "Permission is granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RechargeActivity.this, "Permission not granted", Toast.LENGTH_LONG).show();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                        if(shouldShowRequestPermissionRationale(CAMERA)){

                            displayAlertMessage("You need to allow access for both permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                        }
                                    });
                            return;
                        }
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                if(scannerView == null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){

        new AlertDialog.Builder(RechargeActivity.this).setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null).create().show();
    }

    @Override
    public void handleResult(Result result) {
        final String uid = result.getText();
        //Toast.makeText(RechargeActivity.this, uid, Toast.LENGTH_LONG).show();



        ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    if(ds.getKey().equals("display_name") || ds.getKey().equals("balance")) {
                        flag++;
                        if(ds.getKey().equals("display_name")){
                            display_name = ds.getValue().toString();
                        }
                        if(ds.getKey().equals("balance")){
                            balance_str = ds.getValue().toString();
                        }
                        //Toast.makeText(RechargeActivity.this, ds.getValue().toString(), Toast.LENGTH_LONG).show();
                    }

                    if(flag == 2){
                        AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.balance_input_dialogue, null);

                        TextView balanceView = view.findViewById(R.id.balancetext);
                        TextView nameview = view.findViewById(R.id.nametext);
                        final EditText editAmount = view.findViewById(R.id.editbalance);
                        Button rchg_btn = view.findViewById(R.id.rchgbtn);
                        Button cancel_btn = view.findViewById(R.id.cancelbtn);

                        String name_titl = "Name : ";
                        String bal_titl = "Current balance : ";

                        bal_titl = bal_titl.concat(balance_str);
                        final Integer curr_balance = Integer.parseInt(balance_str);
                        
                        nameview.setText(name_titl.concat(display_name));
                        balanceView.setText(bal_titl.concat(" Taka"));

                        builder.setView(view);
                        final AlertDialog dialog = builder.create();

                        final String finalBalance_str = balance_str;
                        rchg_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!editAmount.getText().toString().isEmpty()){
                                    //Toast.makeText(RechargeActivity.this, "Hoisa", Toast.LENGTH_LONG).show();
                                    Integer add_balance = Integer.parseInt(editAmount.getText().toString());
                                    recharge_account(uid, curr_balance, add_balance);

                                } else {
                                    Toast.makeText(RechargeActivity.this, "Please enter the amount", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                                Intent intent = new Intent(RechargeActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });

                        dialog.show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(RechargeActivity.this, "Couldn't access database", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void recharge_account(String uid, Integer curr_balance, Integer add_balance){

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("balance")
                .setValue(curr_balance+add_balance).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(RechargeActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(RechargeActivity.this, "User account successfully recharged", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RechargeActivity.this, "Operation failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
