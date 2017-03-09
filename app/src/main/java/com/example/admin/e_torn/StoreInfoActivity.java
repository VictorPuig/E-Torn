package com.example.admin.e_torn;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.e_torn.Response.PostUserAddResponse;
import com.example.admin.e_torn.Services.RetrofitManager;
import com.example.admin.e_torn.Services.StoreService;
import com.example.admin.e_torn.Services.SuperService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "StoreInforActivity" ;
    int storeTurn;
    int usersTurn;
    int queue;

    TextView actualTurnText;
    TextView disponibleTurnText;
    TextView queueText;
    TextView aproxTimeText;
    Button getTurnBtn;

    Store store;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info);
        store = new Store();
        user = new User();
        //CANVIAR PER ID FIREBASE!!!!
        //user.setId("58c15dff051e1529b8be52aa");
        //Generar ID (POST /users)

        actualTurnText = (TextView) findViewById(R.id.actualTurn);
        disponibleTurnText = (TextView) findViewById(R.id.disponibleTurn);
        queueText = (TextView) findViewById(R.id.queue);
        aproxTimeText = (TextView) findViewById(R.id.aproxTime);
        getTurnBtn = (Button) findViewById(R.id.getTurnBtn);
        getTurnBtn.setOnClickListener(this);

        store.setId(getIntent().getStringExtra("id"));

        StoreService storeService = RetrofitManager.retrofit.create(StoreService.class);
        final Call<Store> call = storeService.getStoreById(store.getId());
        call.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                Log.d("Response", response.body().toString());

                storeTurn = response.body().getStoreTurn();
                usersTurn = response.body().getUsersTurn();
                store.setStoreTurn(storeTurn);
                store.setUsersTurn(usersTurn);
                queue = store.getReloadedQueue();

                actualTurnText.setText(String.valueOf(storeTurn));
                disponibleTurnText.setText(String.valueOf(usersTurn));
                queueText.setText(String.valueOf(queue));
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Log.d(Constants.RETROFIT_FAILURE_TAG, t.getMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.getTurnBtn) {
            if (user.getTurn() == 0) {
                StoreService storeService = RetrofitManager.retrofit.create(StoreService.class);
                final Call<PostUserAddResponse> call = storeService.addUserToStore(store.getId(), user.getId());
                call.enqueue(new Callback<PostUserAddResponse>() {
                    @Override
                    public void onResponse(Call<PostUserAddResponse> call, Response<PostUserAddResponse> response) {
                        user.setTurn(response.body().getTurn());
                        Log.d(TAG, "Torn demanat");

                        Context context = getApplicationContext();
                        Intent intent = new Intent(context, UserTurnInfo.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<PostUserAddResponse> call, Throwable t) {
                        Log.d(Constants.RETROFIT_FAILURE_TAG, t.getMessage());
                    }
                });
            }
            else {
                //L'usuari ja ha demanat torn
                Log.d(TAG, "Usuari ja ha demanat torn");
            }
        }
    }
}
