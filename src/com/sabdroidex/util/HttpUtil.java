package com.sabdroidex.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class HttpUtil {

    private static HttpUtil _instance;
    private static DefaultHttpClient httpClient = new DefaultHttpClient();

    private HttpUtil() {
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 60000);
        HttpConnectionParams.setSoTimeout(params, 60000);
    }

    public static HttpUtil instance() {
        if (_instance == null)
            _instance = new HttpUtil();

        return _instance;
    }

    /**
     * Gets data from URL throws {@link RuntimeException} If anything goes wrong
     * 
     * @throws ServerConnectinoException
     */
    public String getData(String url) throws ServerConnectinoException {
        try {
            HttpGet request = new HttpGet(url);

            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                throw new ServerConnectinoException("Connection Error: " + response.getStatusLine().getReasonPhrase());
            }
            else {
                InputStream content = response.getEntity().getContent();

                return inputStreamAsString(content);
            }
        }
        catch (ServerConnectinoException e) {
            throw new ServerConnectinoException(e.getMessage());
        }
        catch (IOException e) {
            throw new ServerConnectinoException("Connection timeout!");
        }
        catch (Throwable e) {
            Log.w("HTTP", "Failed to connect to server", e);
            throw new RuntimeException(e);
        }
    }

    public static String inputStreamAsString(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }

        br.close();
        String result = sb.toString();
        stream.close();
        return result.substring(0, result.length() - 1);
    }

    public class ServerConnectinoException extends Exception {

        private static final long serialVersionUID = -7812290125811215338L;

        public ServerConnectinoException(String message) {
            super(message);
        }
    }
}
