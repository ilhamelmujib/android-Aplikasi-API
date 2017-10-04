package com.example.pepey.androidjsonparsing;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
//    private RecyclerView recyclerView;
    private ListView listView;

    private static String url = "http://www.androidbegin.com/tutorial/jsonparsetutorial.txt";

    ArrayList<HashMap<String,String>> populationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populationList = new ArrayList<>();
//        recyclerView = (RecyclerView) findViewById(R.id.list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        listView = (ListView) findViewById(R.id.list);
        new GetContact().execute();

    }

    private class GetContact extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Mengambil Data . . .");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler handler = new HttpHandler();
            String jsonStr = handler.makeServiceCall(url);

            Log.e(TAG, "Respon dari url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObject     = new JSONObject(jsonStr);
                    JSONArray worldpopulation = jsonObject.getJSONArray("worldpopulation");

                    for (int i = 0; i < worldpopulation.length(); i++) {
                        String country, population, foto;
                        HashMap<String, String> worldpop = new HashMap<>();

                        JSONObject c = worldpopulation.getJSONObject(i);

                        country     = c.getString("country");
                        population  = c.getString("population");


                        worldpop.put("country", country);
                        worldpop.put("population", population);

                        populationList.add(worldpop);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "JSON parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Tidak dapat mendapatkan JSON dari server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Tidak dapat mendapatkan JSON dari server. Cek LogCat.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (progressDialog.isShowing()) {
//                AdapterJSONParsing adapter = new AdapterJSONParsing(populationList);
//                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
                ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, populationList,
                        R.layout.list_item, new String[]{"country", "population"},
                        new int[]{R.id.country,
                        R.id.population});

                listView.setAdapter(adapter);

            }
        }
    }
}
