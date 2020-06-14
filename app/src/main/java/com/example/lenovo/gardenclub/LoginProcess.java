package com.example.lenovo.gardenclub;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class LoginProcess extends AppCompatActivity {
    EditText UsernameEt, PasswordEt;
    Button submit, btnHome;
    static String JSON_STRING;
    static String json_string;
    private static final String TAG = "LoginProcess";
    WebView mWebView;
    String password, username;
    Intent intent, in;
    StringBuilder str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_only);
        mWebView = findViewById(R.id.webview);

        // Enable JS
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        intent = new Intent(this, ContactList.class);
        submit = findViewById(R.id.submitButton);
        UsernameEt = findViewById(R.id.usernameEditText);
        PasswordEt = findViewById(R.id.passwordEditText);
        btnHome = findViewById(R.id.homeButton10);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            in = new Intent(LoginProcess.this, MainActivity.class);
            startActivity(in);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();
                username = UsernameEt.getText().toString().trim();
                Log.i(TAG, "username:" + username + ";");
                password = PasswordEt.getText().toString();
                Log.i(TAG, "password:" + password + ";");
//                WebSettings webSettings = mWebView.getSettings();
//                webSettings.setJavaScriptEnabled(true);
                final WebAppInterface webAppInterface = new WebAppInterface(LoginProcess.this);
                mWebView.addJavascriptInterface(webAppInterface, "Android");

                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.i(TAG, "shouldOverrideUrlLoading(view, url)");
                        Log.i(TAG, "url:" + url + ";");
                        view.loadUrl(url);
                        return true;
                    }

                    public boolean isLoggedIn(String url) {
                        return url.equals("http://www.capefeargardenclub.org/");
                    }

                    public boolean needLogin(String url) {
                        return url.equals("http://www.capefeargardenclub.org/wp-login.php?redirect_to=%2f");
                    }

                    public boolean isURLSet(String inStr) {
                        return (inStr != null);
                    }

                    public String JSLoginText() {
                        String js =
                            "var objPWD = '';" +
                            "var objAccount = '';" +
                            "var str = '';" +
                            "var inputs = document.getElementsByTagName('input');" +
                            "for (var i = 0; i < inputs.length; i++) {" +
                                "if (inputs[i].name.toLowerCase() === 'pwd') {" +
                                    "inputs[i].value = objPWD;" +
                            "}" +
                            "else if (inputs[i].name.toLowerCase() === 'log') {" +
                                    "inputs[i].value = objAccount;" +
                                "}" +
                            "}" +
                            "if (objAccount != null) {" +
                                "console.log('objAccount: ' + objAccount + ';');" +
                                "str += objAccount.value;" +
                            "}" +
                            "if (objPWD != null) { " +
                                 "str += ' , ' + objPWD.value;" +
                            "}" +
                            "document.getElementById('loginform').submit();";
                        return js;
                    }

                    public void Login(WebView view) {
                        Log.i(TAG, "Initiating Login");
                        view.loadUrl("javascript:" + JSLoginText());
                    }

                    public void DisplayPage() {
                        new BackgroundTask().execute();
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        String viewURL = view.getUrl();
                        Log.i(TAG, "onPageFinished(view, url)");
                        Log.i(TAG, "url:" + url + ";");
                        Log.i(TAG, "viewURL:" + viewURL + ";");

                        if (isURLSet(viewURL)) {
                            if (needLogin(viewURL)) {
                                Log.i(TAG, "First Time Through");
                                Login(view);
                            } else if (isLoggedIn(viewURL)) {
                                Log.i(TAG, "Login Success.");
                                mWebView.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Login Successful: Loading directory data...", Toast.LENGTH_LONG).show();
                                DisplayPage();
                            } else  {
                                Log.i(TAG, "Unexpected URL -> " + viewURL);
                                Toast.makeText(getApplicationContext(), "Email or password is incorrect. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

                LoginProcess.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(TAG, "LoginProcess.this.runOnUiThread()");
                        mWebView.loadUrl("http://www.capefeargardenclub.org/wp-login.php?redirect_to=%2f");
                    }
                });

            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "postCreate()");
        super.onPostCreate(savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    class BackgroundTask extends AsyncTask<Void,Void,String> {

        String json_url;
        private static final String TAG = "BackgroundTask";

        @Override
        protected String doInBackground(Void... voids) {
            Log.i(TAG, "doInBackground()");
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(json_url);
            str = new StringBuilder();
            String url = "http://capefeargardenclub.org/cfgcTestingJSON/login.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("method", "some_json"));
            try {
                Log.i(TAG, "Initial Post");
                Log.i(TAG, "params: " + params + ';');
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) { // Status OK
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((JSON_STRING = bufferedReader.readLine()) != null) {
                        stringBuilder.append(JSON_STRING+"\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    return stringBuilder.toString().trim();
                } else {
                    Log.e("Log", "Failed to download result..");
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Client Protocol exception:" + e.toString() + ";");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "IO exception:" + e.toString() + ";");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            json_url = "http://capefeargardenclub.org/cfgcTestingJSON/login.php";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute()");
            json_string = result;
            if (json_string != null) {
                intent.putExtra("json_data", json_string);
                intent.putExtra("login_email", username);
                intent.putExtra("password", password);
                startActivity(intent);
            } else {
                new BackgroundTask().execute();

            }
        }
    }
}
