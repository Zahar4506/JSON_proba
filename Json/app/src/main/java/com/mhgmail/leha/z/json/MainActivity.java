package com.mhgmail.leha.z.json;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends ActionBarActivity {

    public static String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParseTask().execute();
    }



    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";



        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
               // URL url = new URL("http://mob.ugrasu.ru/oracle/database_tmtb_json_derji.php");
                URL url = new URL("http://mob.ugrasu.ru/oracle/database_timetable.php");

                urlConnection = (HttpURLConnection) url.openConnection();
              //  urlConnection.setRequestMethod("GET");
                        urlConnection.setReadTimeout(15000);
                        urlConnection.setConnectTimeout(15000);
                    urlConnection.setRequestMethod("POST");

                         urlConnection.setDoInput(true);
                         urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("timetable_query", "1541б")
                        .appendQueryParameter("date_query", "24.05.2016");
                String query = builder.build().getEncodedQuery();


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                Log.d(LOG_TAG, query);

                urlConnection.connect();



                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONObject dataJsonObj = null;
            String nameTeach = "";
            String discipline = "";
            String subgrup = "";
            String pair = "";
            String aud = "";
            String vid = "";
            String korp = "";
            String gr_num = "";


            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray dateUgrasu = dataJsonObj.getJSONArray("24052016");

                // 2. перебираем и выводим контакты каждого друга
                for (int i = 0; i < dateUgrasu.length(); i++) {
                    JSONObject studDay = dateUgrasu.getJSONObject(i);
                    nameTeach = studDay.getString("TEAC_FIO");
                    discipline = studDay.getString("DISCIPLINE");
                    subgrup = studDay.getString("SUBGRUP");
                    pair = studDay.getString("PAIR");
                    aud = studDay.getString("AUD");
                    vid = studDay.getString("VID");
                    korp = studDay.getString("KORP");
                    gr_num = studDay.getString("GR_NUM");


                    Log.d(LOG_TAG, "Преподаватель: " + nameTeach+" "+discipline+" "+subgrup+" "+pair+" "+aud+" "+vid+" "+korp+" "+gr_num);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}