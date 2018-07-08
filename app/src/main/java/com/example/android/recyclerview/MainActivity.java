/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.recyclerview;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// COMPLETED (8) Implement GreenAdapter.ListItemClickListener from the MainActivity
public class MainActivity extends AppCompatActivity
        implements GreenAdapter.ListItemClickListener {

    private Retrofit retrofit;
    public ArrayList<Pokemon> my_list = new ArrayList<Pokemon>();
    private int NUM_LIST_ITEMS = my_list.size();;

    /*
     * References to RecyclerView and Adapter to reset the list to its
     * "pretty" state when the reset menu item is clicked.
     */
    private GreenAdapter mAdapter;
    private RecyclerView mNumbersList;

    // COMPLETED (9) Create a Toast variable called mToast to store the current Toast
    /*
     * If we hold a reference to our Toast, we can cancel it (if it's showing)
     * to display a new Toast. If we didn't do this, Toasts would be delayed
     * in showing up if you clicked many list items in quick succession.
     */
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        retrofit = new Retrofit.Builder()
        .baseUrl("https://www.pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

        getData();
        mNumbersList = (RecyclerView) findViewById(R.id.rv_numbers);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNumbersList.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersList.setHasFixedSize(true);

        // COMPLETED (13) Pass in this as the ListItemClickListener to the GreenAdapter constructor
        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new GreenAdapter(NUM_LIST_ITEMS, this, my_list);
        mNumbersList.setAdapter(mAdapter);
    }

    private void getData(){
        PokeApiService service = retrofit.create(PokeApiService.class);
        Call<PokemonRespuesta> pokemonRespuestaCall = service.getListaPokemon();
        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                if(response.isSuccessful()){
                    PokemonRespuesta pokemonRespuesta = response.body();
                    ArrayList<Pokemon> listaPokemon = pokemonRespuesta.getResult();
                    my_list = pokemonRespuesta.getResult();

                    if (listaPokemon != null) {
                        for (int i = 0; i < listaPokemon.size(); i++) {
                            Pokemon p = listaPokemon.get(i);
                            Log.i("POKEMON", p.getName());
                        }
                    }
                }
                else {
                    Log.e("MainActivity", "TENGO SUENO");
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                Log.e("MainActivity", "TENGO SUENO");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            /*
             * When you click the reset menu item, we want to start all over
             * and display the pretty gradient again. There are a few similar
             * ways of doing this, with this one being the simplest of those
             * ways. (in our humble opinion)
             */
            case R.id.action_refresh:
                // COMPLETED (14) Pass in this as the ListItemClickListener to the GreenAdapter constructor
                mAdapter = new GreenAdapter(NUM_LIST_ITEMS, this, my_list);
                mNumbersList.setAdapter(mAdapter);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // COMPLETED (10) Override ListItemClickListener's onListItemClick method
    /**
     * This is where we receive our callback from
     * {@link com.example.android.recyclerview.GreenAdapter.ListItemClickListener}
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        // COMPLETED (11) In the beginning of the method, cancel the Toast if it isn't null
        /*
         * Even if a Toast isn't showing, it's okay to cancel it. Doing so
         * ensures that our new Toast will show immediately, rather than
         * being delayed while other pending Toasts are shown.
         *
         * Comment out these three lines, run the app, and click on a bunch of
         * different items if you're not sure what I'm talking about.
         */
        if (mToast != null) {
            mToast.cancel();
        }

        // COMPLETED (12) Show a Toast when an item is clicked, displaying that item number that was clicked
        /*
         * Create a Toast and store it in our Toast field.
         * The Toast that shows up will have a message similar to the following:
         *
         *                     Item #42 clicked.
         */
        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        //new _getJson().execute("https://dog.ceo/api/breeds/list/all");


        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);

        mToast.show();
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("param1", clickedItemIndex);
        startActivity(intent);
    }

    protected class _getJson extends AsyncTask<String, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params)
        {

            String str=params[0];
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }

                return new JSONObject(stringBuffer.toString());
            }
            catch(Exception ex)
            {
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject response)
        {
            if(response != null)
            {
                try {
                    Log.e("App", "Success: " + response.getString("yourJsonElement") );
                } catch (JSONException ex) {
                    Log.e("App", "Failure", ex);
                }
            }
        }
    }

    public void getJson(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                try {
                    URL githubEndpoint = new URL("http://www.example.com/docs/resource1.html");

                    // Create connection
                    HttpsURLConnection myConnection = (HttpsURLConnection) githubEndpoint.openConnection();
                    //myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");

//                    if (myConnection.getResponseCode() == 200) {
//                        InputStream responseBody = myConnection.getInputStream();
//                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
//                        JsonReader jsonReader = new JsonReader(responseBodyReader);
//                        //System.out.print(jsonReader);
//                    } else {
//                        // Error handling code goes here
//                    }
                }
                catch (IOException e){
                    Log.e("MainActivity", e.toString());
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
