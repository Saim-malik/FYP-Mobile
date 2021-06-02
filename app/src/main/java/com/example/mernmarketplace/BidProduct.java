package com.example.mernmarketplace;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mernmarketplace.conf.APIs;
import com.example.mernmarketplace.conf.AppConstants;
import com.example.mernmarketplace.conf.AppUtils;
import com.example.mernmarketplace.conf.NetworkClient;
import com.example.mernmarketplace.models.AuctionCreationResponse;
import com.example.mernmarketplace.models.ProductCreationResponse;
import com.squareup.picasso.Picasso;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidProduct extends AppCompatActivity {
    private EditText startDate,endDate;
    private ImageView prodImage;

    private TextView quantityText,mainText;
    private Button saveProductButton,addButton,subButton;

    String prodName,img;
    int quantity = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_product);
        startDate = findViewById(R.id.shopNameText2);
        endDate = findViewById(R.id.shopNameText3);
        mainText = findViewById(R.id.textView14);
        prodImage = findViewById(R.id.prodImg);
        saveProductButton = findViewById(R.id.saveButton);
        quantityText = findViewById(R.id.productQuantity);
        addButton = findViewById(R.id.addButton);
        prodName = getIntent().getStringExtra("title");
       img = getIntent().getStringExtra("image");
        Picasso.with(BidProduct.this).load(img).into(prodImage);
        mainText.setText(prodName);
        subButton = findViewById(R.id.subButton);
        if(AppUtils.getbidIdSharedPreference(BidProduct.this,AppConstants.bidID)!=null){
            APIs apiService = NetworkClient.getRetrofit().create(APIs.class);
            Call<AuctionCreationResponse> call = apiService.getAuction(AppUtils.getbidIdSharedPreference(BidProduct.this,AppConstants.bidID));
            call.enqueue(new Callback<AuctionCreationResponse>() {
                @Override
                public void onResponse(Call<AuctionCreationResponse> call, Response<AuctionCreationResponse> response) {
                    quantityText.setText(response.body().getStartingBid());
                   startDate.setText(response.body().getBidStart());
                    endDate.setText(response.body().getBidEnd());
                }

                @Override
                public void onFailure(Call<AuctionCreationResponse> call, Throwable t) {

                }
            });
        }
      String  token = "Bearer "+AppUtils.getUserTokenSharedPreference(BidProduct.this, AppConstants.token);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity += 1;
                quantityText.setText(""+quantity);
            }
        });
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity -1;
                if(quantity<0)
                    quantity = 0;
                quantityText.setText(""+quantity);
            }
        });
        saveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startDate.getText().toString()!=""&& endDate.getText().toString()!="" && quantity!=0 ) {
                    RequestBody itemName = RequestBody.create(MediaType.parse("text/plain"), mainText.getText().toString());
                    RequestBody startingBid = RequestBody.create(MediaType.parse("text/plain"), quantityText.getText().toString());
                    RequestBody bidStart = RequestBody.create(MediaType.parse("text/plain"), startDate.getText().toString());
                    RequestBody bidEnd = RequestBody.create(MediaType.parse("text/plain"), endDate.getText().toString());


                    APIs apiService = NetworkClient.getRetrofit().create(APIs.class);
                    Call<AuctionCreationResponse> call = apiService.createAuction(token,AppUtils.getUserIDSharedPreference(BidProduct.this,AppConstants.userID),itemName,startingBid,bidStart,bidEnd);
                    call.enqueue(new Callback<AuctionCreationResponse>() {
                        @Override
                        public void onResponse(Call<AuctionCreationResponse> call, Response<AuctionCreationResponse> response) {
                            Log.d("Server Response", "Response: "+response.code());
                            if(response.body()!=null && response.code()==200){
                                Toast.makeText(BidProduct.this,"Bid placed Successfully",Toast.LENGTH_LONG).show();
                                AuctionCreationResponse auctionCreationResponse = response.body();
                                AppUtils.setbidSharedPreference(BidProduct.this,AppConstants.bidID,auctionCreationResponse.getId());
                            }
                        }

                        @Override
                        public void onFailure(Call<AuctionCreationResponse> call, Throwable t) {
                            Toast.makeText(BidProduct.this,"Could not place bid",Toast.LENGTH_LONG).show();

                        }
                    });
                }
    }
});
    }
}