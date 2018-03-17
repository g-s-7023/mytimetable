package com.androidapp.g_s_org.mytimetable.httpaccess;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// process http get
public class HttpGetTrafficAPI extends AsyncTask<Void, Void, JSONArray> {
    // URL to GET
    protected String mUrlString;
    // position to Update
    public int mPosition;
    // callback
    private WeakReference<HttpGetTrafficAPICallback> mCallback;

    // constructor
    public HttpGetTrafficAPI(String url, int pos, HttpGetTrafficAPICallback callback) {
        this.mUrlString = url;
        this.mPosition = pos;
        mCallback = new WeakReference<HttpGetTrafficAPICallback>(callback);
    }

    public HttpGetTrafficAPI(String url, HttpGetTrafficAPICallback callback) {
        this.mUrlString = url;
        this.mPosition = 0;
        mCallback = new WeakReference<HttpGetTrafficAPICallback>(callback);
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        HttpURLConnection con = null;
        URL url = null;
        String responseString = "";
        try {
            // make URL
            url = new URL(mUrlString);
            // make HttpURLConnection Object
            con = (HttpURLConnection) url.openConnection();
            // get response
            InputStream in = con.getInputStream();
            responseString = readInputStream(in);
            in.close();
            // convert to json object and return it
            return new JSONArray(responseString);
        } catch (MalformedURLException e) {
            Log.e("HttpGetTrafficAPI", "", e);
        } catch (IOException e) {
            Log.e("HttpGetTrafficAPI", "", e);
        } catch (JSONException e) {
            Log.e("HttpGetTrafficAPI", "", e);
        }
        return null;
    }

    public void onPostExecute(JSONArray result) {
        // callback must be implemented by the caller
        HttpGetTrafficAPICallback callback = this.mCallback.get();
        if (callback != null) {
            callback.callback(result, mPosition);
        } else {
            Log.e("HttpGetTrafficAPI", "callback is not set");
        }
    }

    public String readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        String st = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        int line = 0;
        while ((st = br.readLine()) != null) {
            sb.append(st);
            line++;
            if (line > 1) {
                break;
            }
        }
        return sb.toString();
    }

    public interface HttpGetTrafficAPICallback {
        public void callback(JSONArray result, int position);
    }
}
