package com.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class HttpUtil {

    private static final HttpUtil _instance = new HttpUtil();

    private HttpUtil() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 5000);        
    }

    public static HttpUtil getInstance() {
        return _instance;
    }

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    /**
                     * We accept all certificates as Sickbeard's is self signed and cannot be verified
                     */
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    /**
                     * We accept all certificates as Sickbeard's is self signed and cannot be verified
                     */
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        }
        catch (Exception e) {
            Log.w("ERROR", " " + e.getLocalizedMessage());
        }
    }
    
    /**
     * Gets data from URL as String throws {@link RuntimeException} If anything goes wrong
     * 
     * @return The content of the URL as a String
     * @throws ServerConnectinoException
     */
    public String getDataAsString(String url) throws RuntimeException {
        URLConnection urlc = null;
        InputStreamReader re = null;
        BufferedReader rd = null;
        
    	try {
            String responseBody = "";
            urlc = getConnection(new URL(url));
            re = new InputStreamReader(urlc.getInputStream());
            rd = new BufferedReader(re);
            
            String line = "";
            while ((line = rd.readLine()) != null) {
                responseBody += line;
                responseBody += "\n";
                line = null;
            }
            rd.close();
            re.close();

            return responseBody;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets data from URL as byte[] throws {@link RuntimeException} If anything goes wrong
     * 
     * @return The content of the URL as a byte[]
     * @throws ServerConnectinoException
     */
    public byte[] getDataAsByteArray(String url) {
        byte[] dat = null;
        URLConnection urlc = null;
        InputStream is = null;
        
        try {
        	
            urlc = getConnection(new URL(url));    
            is = urlc.getInputStream();
            int len = urlc.getContentLength();

            if (len < 0) {
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                for (;;) {
                    int nb = is.read(buf);
                    if (nb <= 0)
                        break;
                    bao.write(buf, 0, nb);
                }
                dat = bao.toByteArray();
                bao.close();
            }
            else {
                dat = new byte[len];
                int i = 0;
                while (i < len) {
                    int n = is.read(dat, i, len - i);
                    if (n <= 0)
                        break;
                    i += n;
                }
            }
            is.close();
            return dat;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets data from URL as char[] throws {@link RuntimeException} If anything goes wrong
     * 
     * @return The content of the URL as a char[]
     * @throws ServerConnectinoException
     */
    public byte[] postDataAsByteArray(String url, String contentType, String contentName, byte[] content) {

        URLConnection urlc = null;
        OutputStream os = null;
        InputStream is = null;
        byte[] dat = null;
        final String boundary = "" + new Date().getTime();

        try {
            urlc = new URL(url).openConnection();
            urlc.setDoOutput(true);
            urlc.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------" + boundary);
            os = urlc.getOutputStream();
            
            String message1 = "-----------------------------" + boundary + "\r\n";
            message1 += "Content-Disposition: form-data; name=\"nzbfile\"; filename=\"" + contentName + "\"" + "\r\n";
            message1 += "Content-Type: " + contentType + "\r\n";
            message1 += "\r\n";
            String message2 = "\r\n" + "-----------------------------" + boundary + "--" + "\r\n";
            
            os.write(message1.getBytes());
            os.write(content);
            os.write(message2.getBytes());
            os.flush();

            is = urlc.getInputStream();
            
            ByteArrayOutputStream bis = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bis.write(ch);
            }
            dat = bis.toByteArray();

            os.close();
            is.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dat;
    }

    /**
     * Gets data from URL as char[] throws {@link RuntimeException} If anything goes wrong
     * @param parameterMap 
     * 
     * @return The content of the URL as a char[]
     * @throws ServerConnectinoException
     */
    public char[] getDataAsCharArray(String url, Map<String, String> parameterMap) {
    	
        char[] dat = null;
        URLConnection urlc = null;
        InputStream is = null;
        BufferedReader reader = null;

        try {
            urlc = getConnection(new URL(url),parameterMap);
            is = urlc.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));

            int len = urlc.getContentLength();

            dat = new char[len];
            int i = 0;
            int c;
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                dat[i] = character;
                i++;
            }

            is.close();
            return dat;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	/**
     * Gets data from URL as char[] throws {@link RuntimeException} If anything goes wrong
     * 
     * @return The content of the URL as a char[]
     * @throws ServerConnectinoException
     */
    public char[] postDataAsCharArray(String url, String contentType, String contentName, char[] content) {

        URLConnection urlc = null;
        OutputStream os = null;
        InputStream is = null;
        char[] dat = null;
        final String boundary = "" + new Date().getTime();
        
        try {
            urlc = new URL(url).openConnection();
            urlc.setDoOutput(true);
            urlc.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------" + boundary);
            
            String message1 = "-----------------------------" + boundary + "\r\n";
            message1 += "Content-Disposition: form-data; name=\"nzbfile\"; filename=\"" + contentName + "\"" + "\r\n";
            message1 += "Content-Type: " + contentType + "\r\n";
            message1 += "\r\n";
            String message2 = "\r\n" + "-----------------------------" + boundary + "--" + "\r\n";
            
            os = urlc.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.defaultCharset()));
            
            writer.write(message1);
            writer.write(content);
            writer.write(message2);
            writer.flush();
            
            is = urlc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));

            int len = urlc.getContentLength();

            dat = new char[len];
            int i = 0;
            int c;
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                dat[i] = character;
                i++;
            }

            is.close();
            return dat;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static final URLConnection getConnection(URL url) throws IOException {
        URLConnection urlc;

        urlc = url.openConnection();
        urlc.setUseCaches(false);
        urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.9.1.9) Gecko/20100414 Iceweasel/3.5.9 (like Firefox/3.5.9)");
        urlc.setRequestProperty("Accept-Encoding", "gzip");

        return urlc;
    }
    
    private URLConnection getConnection(URL url,
			Map<String, String> parameterMap) throws IOException {
    	URLConnection urlc = getConnection(url);
    	for (String key : parameterMap.keySet()) {
    		urlc.setRequestProperty(key, parameterMap.get(key));
    	}
		return urlc;
	}

}
