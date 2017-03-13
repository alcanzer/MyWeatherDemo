package com.example.alcanzer.myweatherdemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.textView)
    TextView mTextView;
    @BindView(R.id.editText)
    EditText edt;
    @BindView(R.id.button)
    Button btn;
    String city;
    String OPEN_API = "&appid=ced5712cfe0799ed6b72d4aa36a11e11";
    String SITE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

    public class WeatherDownloader extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStream inputStream = conn.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;

                    data = inputStreamReader.read();
                }
                Log.i("JSONObject", result);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jObject = new JSONObject(s);
                JSONArray jWeather = new JSONArray(jObject.getString("weather"));
                for(int i =0; i<jWeather.length(); i++){
                    JSONObject jPart = jWeather.getJSONObject(i);
                    mTextView.setText(jPart.getString("main") + "\n" + jPart.getString("description"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherDownloader task = new WeatherDownloader();
                if (edt.getText() != null) {
                    try {
                        city = URLEncoder.encode(edt.getText().toString(), "UTF-8");
                        task.execute(SITE_URL + city + OPEN_API).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "City cannot be null", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
