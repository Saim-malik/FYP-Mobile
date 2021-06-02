package com.example.mernmarketplace;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.mernmarketplace.Adapters.ProductAdapter;
import com.example.mernmarketplace.conf.APIs;
import com.example.mernmarketplace.conf.AppConstants;
import com.example.mernmarketplace.conf.AppUtils;
import com.example.mernmarketplace.conf.NetworkClient;
import com.example.mernmarketplace.models.LoginResponse;
import com.example.mernmarketplace.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreen extends AppCompatActivity {
Spinner spinner;
ImageView profileIcon,logOutButton,cartButton;
RecyclerView recyclerView;
String token,email,name;
boolean isSeller;

List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        spinner = findViewById(R.id.filterSpinner);
        profileIcon = findViewById(R.id.profileImage);
        cartButton = findViewById(R.id.profileImage3);
        logOutButton = findViewById(R.id.logOutButton);
        recyclerView = findViewById(R.id.productList);
        token = getIntent().getStringExtra("token");
        email = getIntent().getStringExtra("email");
        AppUtils.setUserTokenSharedPreference(HomeScreen.this, AppConstants.token,token);
        name = getIntent().getStringExtra("name");
        isSeller = getIntent().getBooleanExtra("isSeller",false);
        List<String> list = new ArrayList<>();
        list.add("Default");
        list.add("By Name");
        list.add("Price: Low to High");
        list.add("Price: High to Low");
        list.add("By Category");
        spinner.setAdapter(new ArrayAdapter<String>(this,
                R.layout.spinner_item,list));
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreen.this,LoginActivity.class);
                finish();
                startActivity(i);
                
            }
        });
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(HomeScreen.this,CartListActivity.class);
                startActivity(i);
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreen.this,Profile.class);
                i.putExtra("token",token);
                i.putExtra("name",name);
                i.putExtra("email",email);
                i.putExtra("isSeller",isSeller);
                startActivity(i);
            }
        });
        APIs apiService = NetworkClient.getRetrofit().create(APIs.class);
        Call<List<Product>> call = apiService.getProducts();
        call.enqueue(new Callback<List<Product>>() {

            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.body() != null && response.code()==200) {
                    products = response.body();
                    for(Product p : products){
                        p.setImage("https://fyp-1.herokuapp.com/api/product/image/"+p.get_id());
                    }
                    ProductAdapter productAdapter = new ProductAdapter(products);
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager catLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(catLayoutManager);
                    recyclerView.setAdapter(productAdapter);
//                    if(spinner.getSelectedItem().toString().equalsIgnoreCase("Price: Low to High"))
//                    {
//                        double tempPrice = 0;
//                        for(Product p : products){
//                            if(p.getPrice()>tempPrice)
//                            {
//                                tempPrice = p.getPrice();
//                                products.
//                            }
//                        }
//                    }

                    }


                }


            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }
}