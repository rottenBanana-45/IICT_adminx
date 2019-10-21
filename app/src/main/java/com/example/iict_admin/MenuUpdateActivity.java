package com.example.iict_admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.iict_admin.classes.FoodItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MenuUpdateActivity extends AppCompatActivity {

    private DatabaseReference ref;
    Spinner Rice , Chicken , Lentil , Khichuri , Pudding , Eggcurry;
    private Button update_btn;
    private Toolbar toolbar ;
    private ArrayList<FoodItem> foodItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_update);

        toolbar = findViewById(R.id.update_menu_toolbar);
        Rice = findViewById(R.id.rice);
        Chicken = findViewById(R.id.chicken);
        Lentil = findViewById(R.id.lentil);
        Eggcurry = findViewById(R.id.eggcurry);
        Pudding = findViewById(R.id.pudding);
        Khichuri = findViewById(R.id.khichuri);
        update_btn = findViewById(R.id.menu_update_btn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Menu");






        ref = FirebaseDatabase.getInstance().getReference();

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readDataFromSpinners();
                ref.child("food").setValue(foodItems).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MenuUpdateActivity.this, "Updated", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MenuUpdateActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(MenuUpdateActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
    public void readDataFromSpinners(){

        String chicken ,eggcurry , lentil , khichuri , pudding, rice;
        rice=Rice.getSelectedItem().toString();
        chicken=Chicken.getSelectedItem().toString();
        eggcurry=Eggcurry.getSelectedItem().toString();
        pudding=Pudding.getSelectedItem().toString();
        lentil=Lentil.getSelectedItem().toString();
        khichuri=Khichuri.getSelectedItem().toString();

        FoodItem foodItem = new FoodItem();

        foodItem.setItemName("Rice");
        foodItem.setAvailability(rice);
        foodItems.add(foodItem);

        foodItem = new FoodItem();

        foodItem.setItemName("Chicken");
        foodItem.setAvailability(chicken);
        foodItems.add(foodItem);

        foodItem = new FoodItem();

        foodItem.setItemName("Egg Curry");
        foodItem.setAvailability(eggcurry);
        foodItems.add(foodItem);

        foodItem = new FoodItem();

        foodItem.setItemName("Khichuri");
        foodItem.setAvailability(khichuri);
        foodItems.add(foodItem);

        foodItem = new FoodItem();

        foodItem.setItemName("Pudding");
        foodItem.setAvailability(pudding);
        foodItems.add(foodItem);

        foodItem = new FoodItem();

        foodItem.setItemName("Lentil");
        foodItem.setAvailability(lentil);
        foodItems.add(foodItem);
    }
}
