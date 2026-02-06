package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener, CityRecyclerAdapter.OnCityClickListener {

    private Button addCityButton;
    private RecyclerView cityRecyclerView;

    private ArrayList<City> cityArrayList;
    private CityRecyclerAdapter cityRecyclerAdapter;

    private FirebaseFirestore db;

    private CollectionReference citiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set views
        addCityButton = findViewById(R.id.buttonAddCity);
        cityRecyclerView = findViewById(R.id.recyclerviewCities);

        // Set up Firebase
        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        // create city array
        cityArrayList = new ArrayList<>();
        cityRecyclerAdapter = new CityRecyclerAdapter(this, cityArrayList, citiesRef, this);
        
        // Set up RecyclerView
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cityRecyclerView.setAdapter(cityRecyclerAdapter);
        
        // Set up swipe to delete
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(cityRecyclerAdapter, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(cityRecyclerView);

        // set listeners
        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(),"Add City");
        });

        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null)
                Log.e("Firestore", error.toString());

            if (value != null) {
                cityArrayList.clear();
                for (QueryDocumentSnapshot snapshot: value) {
                    String name = snapshot.getString("name");
                    String province = snapshot.getString("province");

                    cityArrayList.add(new City(name, province));
                }
                cityRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void updateCity(City city, String title, String year) {
        // Implementation for updating a city
        // Usually involves deleting the old document if name changes, or just setting new data
        HashMap<String, String> data = new HashMap<>();
        data.put("province", year);
        data.put("name", title);

        citiesRef.document(title)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error updating document", e);
                    }
                });
    }

    @Override
    public void addCity(City city) {
        HashMap<String, String> data = new HashMap<>();
        data.put("province", city.getProvince());
        data.put("name", city.getName());

        citiesRef
                .document(city.getName())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error writing document", e);
                    }
                });
    }

    public void addDummyData(){
        City m1 = new City("Edmonton", "AB");
        City m2 = new City("Vancouver", "BC");
        cityArrayList.add(m1);
        cityArrayList.add(m2);
        cityRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCityClick(City city) {
        CityDialogFragment cityDialogFragment = CityDialogFragment.newInstance(city);
        cityDialogFragment.show(getSupportFragmentManager(),"City Details");
    }
}