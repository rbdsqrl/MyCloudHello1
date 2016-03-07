package com.tutorial.rbdsqrl.mycloudhello;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.narendran.myapplication.backend.quoteEndpoint.QuoteEndpoint;
import com.example.narendran.myapplication.backend.quoteEndpoint.model.Quote;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class EndpointsAsyncTask extends AsyncTask<Void, Void, List<Quote>> {
    private static QuoteEndpoint myApiService = null;
    private Context context;

    EndpointsAsyncTask(Context context) {
        this.context = context;
    }

    // options for running against local devappserver
// - 10.0.2.2 is localhost's IP address in Android emulator
// - turn off compression when running against local devappserver

    @Override
    protected List<Quote> doInBackground(Void... params) {
        if(myApiService == null) { // Only do this once
            QuoteEndpoint.Builder builder = new QuoteEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null).setRootUrl("https://myhellocloud-1218.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
// end options for devappserver

            myApiService = builder.build();
        }

        Quote q = new Quote();
        q.setId(1234L);
        q.setWho("Satya");
        q.setWhom("THIS IS A TEST");

        try {
            // myApiService.insertQuote(q).execute();
            return myApiService.listQuote().execute().getItems();

        } catch (IOException e) {
            Log.i("empty",e.getMessage(),e);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<Quote> result) {
        Log.i("asyn","in "+result.toString());
        for (Quote q : result) {
            Toast.makeText(context, q.getWho() + " : " + q.getWhom(), Toast.LENGTH_LONG).show();
            Log.i("asyn",q.getWho());
        }
    }
}

public class MainActivity extends AppCompatActivity {
    private static QuoteEndpoint myApiService = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getQuotes(View v) {
        Quote quote = new Quote();
        if(myApiService == null) { // Only do this once
            QuoteEndpoint.Builder builder = new QuoteEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null).setRootUrl("https://myhellocloud-1218.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            myApiService = builder.build();
        }

        quote.setWhom("George");
        quote.setWho("Philip");
        quote.setId(1L);
        try {
            myApiService.insertQuote(quote).execute();
        }catch (Exception e){

        }
        new EndpointsAsyncTask(getBaseContext()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
