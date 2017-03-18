package com.example.admin.e_torn;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.admin.e_torn.adapters.SuperAdapter;
import com.example.admin.e_torn.asynctasks.GetGpsTask;
import com.example.admin.e_torn.listeners.RecyclerItemClickListener;
import com.example.admin.e_torn.models.Store;
import com.example.admin.e_torn.models.Super;
import com.example.admin.e_torn.services.RetrofitManager;
import com.example.admin.e_torn.services.SuperService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SuperActivity extends AppCompatActivity {
    private AppCompatActivity self;
    double userLatitude;
    double userLongitude;

    private List<Super> supers;
    private RecyclerView recyclerView;
    private Context context;
    private static final String TAG = "SuperActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);
        self = this;

        (findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        this.context = getApplicationContext();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GetGpsTask task = new GetGpsTask(this);
            task.execute(locationManager);
        }

       LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, location.toString());
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();


                inicialitzeData();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(self);
                recyclerView.setLayoutManager(linearLayoutManager);
                //recyclerView.setHasFixedSize(true); Per a quan sabem que el tamany del recyclerView no canviara
                // initializeAdapter();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);






    }

    public void inicialitzeData (){

        supers = new ArrayList<>();


        SuperService superService = RetrofitManager.retrofit.create(SuperService.class);
        final Call<List<Super>> call = superService.getSupers(userLatitude, userLongitude);

        call.enqueue(new Callback<List<Super>>() {
            @Override
            public void onResponse(Call<List<Super>> call, Response<List<Super>> response) {
                (findViewById(R.id.progressBar)).setVisibility(View.GONE);
                Log.d("Response", response.body().toString());
                for (Super superM: response.body()) {
                    supers.add(new Super(superM.getId(), superM.getCity(), superM.getAddress(), superM.getPhone(), superM.getFax(), superM.getStores(), superM.getCoords()));
                /*supers.add(new Super("Caprabo3", "Caprabo2 address", "111111", "22222", R.drawable.capraboicon));
                supers.add(new Super("Caprabo4", "Caprabo3 address", "111111", "22222", R.drawable.capraboicon));
                supers.add(new Super("Caprabo5", "Caprabo4 address", "111111", "22222", R.drawable.capraboicon));*/
                }
                SuperAdapter adapter = new SuperAdapter(context, supers);
                recyclerView.setAdapter(adapter);
                recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener(){

                        @Override
                        public void onItemClick(View view, int position) {
                            List<Store> stores = supers.get(position).getStores();
                            Intent intent = new Intent(context, StoreActivity.class);
                            intent.putParcelableArrayListExtra("stores", (ArrayList<? extends Parcelable>) stores); // Pasem a StoreActivity la array de Stores a carregar
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    })
                );

            }

            @Override
            public void onFailure(Call<List<Super>> call, Throwable t) {
                Log.d(Constants.RETROFIT_FAILURE_TAG, t.getMessage());
            }
        });

    }
}
