package com.pdftron.cadviewer;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();

    private static final String BASE_URL = "https://demo.pdftron.com/"; // Your server root
    private static final String BCID = "showcase";

    private static final String SAMPLE_URL = "https://pdftron.s3.amazonaws.com/downloads/pl/visualization_condominium_with_skylight.dwg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPdf();
    }

    private void getPdf() {
        String requestUrl = BASE_URL + "blackbox/GetPDF";
        HttpUrl httpUrl = HttpUrl.parse(requestUrl);
        HttpUrl.Builder httpBuilder = httpUrl.newBuilder();

        httpBuilder.addQueryParameter("uri", SAMPLE_URL);
        httpBuilder.addQueryParameter("ext", "dwg");
        httpBuilder.addQueryParameter("bcid", BCID);

        get(httpBuilder.build());
    }

    private void openPdf(String pdfUrl) {
        ViewerConfig config = new ViewerConfig.Builder().openUrlCachePath(this.getCacheDir().getAbsolutePath()).build();
        final Uri fileLink = Uri.parse(pdfUrl);
        DocumentActivity.openDocument(this, fileLink, config);
        finish();
    }

    private void get(HttpUrl url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String result = responseBody.string();
                        System.out.println(result);

                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String pdfUrl = jsonObject.getString("uri");
                                openPdf(BASE_URL + "demo/" + pdfUrl + "?bcid=" + BCID);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}