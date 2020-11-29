package com.machopiggies.famedpanic.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HTTPRequest {

    String url;
    Map<String, String> headers = null;
    JsonObject body = null;

    public HTTPRequest(String url) {
        this.url = url;
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addBody(JsonObject body) {
        this.body = body;
    }

    public JsonObject sendGET() throws IOException {
        if(url != null) {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                final JsonObject resp = (JsonObject) new JsonParser().parse(in.readLine());
                in.close();
                return resp;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public JsonElement sendPOST() {
        try {
            if (url != null) {
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                if (headers != null) {
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        connection.setRequestProperty(header.getKey(), header.getValue());
                    }
                }

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("User-Agent", "FamedPanic-By-MachoPiggies");

                if (body != null) {
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                }

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    try {
                        return new JsonParser().parse(in.readLine());
                    } catch (NullPointerException e) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
