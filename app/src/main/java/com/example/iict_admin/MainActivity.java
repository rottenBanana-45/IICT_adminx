package com.example.iict_admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.iict_admin.classes.DataViewHolder;
import com.example.iict_admin.classes.OrderDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<OrderDetails> arrayList;
    private FirebaseRecyclerOptions<OrderDetails> options;
    private FirebaseRecyclerAdapter<OrderDetails, DataViewHolder> adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.maintoolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("IICT Cafe-Admin");

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("orders");
        databaseReference.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<OrderDetails>().setQuery(databaseReference, OrderDetails.class).build();

        adapter = new FirebaseRecyclerAdapter<OrderDetails, DataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DataViewHolder holder, int position, @NonNull final OrderDetails model) {
                holder.nameview.setText(model.getDisplay_name());
                holder.tableview.setText(model.getTableno());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Order for Table "+model.getTableno(), Toast.LENGTH_LONG).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Order from " + model.getDisplay_name());

                        builder.setPositiveButton("DELIVER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("delivery")
                                        .child(model.getOid());

                                ref.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            databaseReference.child(model.getOid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(MainActivity.this, "Order Delivered", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });

                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.setMessage(model.getOrder());
                        AlertDialog dialog = builder.create();
                        dialog.show();


                    }
                });
            }

            @NonNull
            @Override
            public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new DataViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.model, viewGroup, false));
            }
        };

        recyclerView.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.update_menu){
            Intent intent = new Intent(MainActivity.this, MenuUpdateActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.recharge){
            Intent intent = new Intent(MainActivity.this, RechargeActivity.class);
            startActivity(intent);
        }

        return true;
    }
}
