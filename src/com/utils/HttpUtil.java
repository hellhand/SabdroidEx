/*
 * Copyright (C) 2011-2012  Marc Boulanger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.*
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class HttpUtil {

    public static final String TEXT_PLAIN = "text/plain";

    private static final String BNL = "\r\n";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String USER_AGENT = "User-Agent";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String NL = "\n";
    private static final String GZIP = "gzip";
    private static final String UTF_8 = "UTF-8";
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
                     * We accept all certificates as Sickbeard's is self signed
                     * and cannot be verified
                     */
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    /**
                     * We accept all certificates as Sickbeard's is self signed
                     * and cannot be verified
                     */
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        }
        catch (Exception e) {
            Log.e("ERROR", " " + e.getLocalizedMessage());
        }
    }

    /**
     * Gets data from URL as String throws {@link RuntimeException} If anything
     * goes wrong
     * 
     * @return The content of the URL as a String
     * @throws IOException
     */
    public String getDataAsString(String url) throws IOException {
        URLConnection urlc = null;
        InputStream is = null;
        InputStreamReader re = null;
        BufferedReader rd = null;
        String responseBody = "";

        try {
            urlc = getConnection(new URL(url));
            if (urlc.getContentEncoding() != null && urlc.getContentEncoding().equalsIgnoreCase(HttpUtil.GZIP)) {
                is = new GZIPInputStream(urlc.getInputStream());
            }
            else {
                is = urlc.getInputStream();
            }

            re = new InputStreamReader(is, Charset.forName(HttpUtil.UTF_8));
            rd = new BufferedReader(re);

            String line = "";
            while ((line = rd.readLine()) != null) {
                responseBody += line;
                responseBody += HttpUtil.NL;
                line = null;
            }
        }
        catch (IOException exception) {
            throw exception;
        }
        finally {
            try {
                rd.close();
                re.close();
            }
            catch (Exception e) {
                // we do not care about this
            }
        }
        return responseBody;
    }

    /**
     * Gets data from URL as byte[] throws {@link RuntimeException} If anything
     * goes wrong
     * 
     * @return The content of the URL as a byte[]
     * @throws IOException
     */
    public byte[] getDataAsByteArray(String url, CredentialsProvider cp) throws IOException {
    	HttpClient httpClient = getNewHttpClient();

        URL urlObj = new URL(url);
        HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());

        HttpContext credContext = new BasicHttpContext();
        credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

        HttpGet job = new HttpGet(url);
        HttpResponse response = httpClient.execute(host, job, credContext);
        
        HttpEntity entity = response.getEntity();
        return EntityUtils.toByteArray(entity);
    }

    /**
     * Gets data from URL as char[] throws {@link RuntimeException} If anything
     * goes wrong
     * 
     * @return The content of the URL as a char[]
     * @throws IOException
     */
    public byte[] postDataAsByteArray(String url, String contentType, String contentName, byte[] content) throws IOException {

        URLConnection urlc = null;
        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream bis = null;
        byte[] dat = null;
        final String boundary = "" + new Date().getTime();

        try {
            urlc = new URL(url).openConnection();
            urlc.setDoOutput(true);
            urlc.setRequestProperty(HttpUtil.CONTENT_TYPE, "multipart/form-data; boundary=---------------------------" + boundary);
            os = urlc.getOutputStream();

            String message1 = "-----------------------------" + boundary + HttpUtil.BNL;
            message1 += "Content-Disposition: form-data; name=\"nzbfile\"; filename=\"" + contentName + "\"" + HttpUtil.BNL;
            message1 += "Content-Type: " + contentType + HttpUtil.BNL;
            message1 += HttpUtil.BNL;
            String message2 = HttpUtil.BNL + "-----------------------------" + boundary + "--" + HttpUtil.BNL;

            os.write(message1.getBytes());
            os.write(content);
            os.write(message2.getBytes());
            os.flush();

            if (urlc.getContentEncoding() != null && urlc.getContentEncoding().equalsIgnoreCase(HttpUtil.GZIP)) {
                is = new GZIPInputStream(urlc.getInputStream());
            }
            else {
                is = urlc.getInputStream();
            }

            bis = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bis.write(ch);
            }
            dat = bis.toByteArray();
        }
        catch (IOException exception) {
            throw exception;
        }
        finally {
            try {
                bis.close();
                os.close();
                is.close();
            }
            catch (Exception e) {
                // we do not care about this
            }
        }
        return dat;
    }

    static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        }
        catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    /**
     * Gets data from URL as char[] throws {@link RuntimeException} If anything
     * goes wrong
     * 
     * @param url the url from which to retrieve the data.
     * @param cp the credential provider
     * 
     * @return The content of the URL as a char[]
     * @throws IOException
     */
    public synchronized char[] getDataAsCharArray(String url, CredentialsProvider cp) throws IOException {
        HttpClient httpClient = getNewHttpClient();

        URL urlObj = new URL(url);
        HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());

        HttpContext credContext = new BasicHttpContext();
        credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

        HttpGet job = new HttpGet(url);
        HttpResponse response = httpClient.execute(host, job, credContext);

        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity).toCharArray();
    }

    /**
     * Gets data from URL as char[] throws {@link RuntimeException} If anything
     * goes wrong
     * 
     * @return The content of the URL as a char[]
     * @throws IOException
     */
    public char[] postDataAsCharArray(String url, String contentType, String contentName, char[] content) throws IOException {

        URLConnection urlc = null;
        OutputStream os = null;
        InputStream is = null;
        CharArrayWriter dat = null;
        BufferedReader reader = null;
        String boundary = "" + new Date().getTime();

        try {
            urlc = new URL(url).openConnection();
            urlc.setDoOutput(true);
            urlc.setRequestProperty(HttpUtil.CONTENT_TYPE, "multipart/form-data; boundary=---------------------------" + boundary);

            String message1 = "-----------------------------" + boundary + HttpUtil.BNL;
            message1 += "Content-Disposition: form-data; name=\"nzbfile\"; filename=\"" + contentName + "\"" + HttpUtil.BNL;
            message1 += "Content-Type: " + contentType + HttpUtil.BNL;
            message1 += HttpUtil.BNL;
            String message2 = HttpUtil.BNL + "-----------------------------" + boundary + "--" + HttpUtil.BNL;

            os = urlc.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName(HttpUtil.UTF_8)));

            writer.write(message1);
            writer.write(content);
            writer.write(message2);
            writer.flush();

            dat = new CharArrayWriter();
            if (urlc.getContentEncoding() != null && urlc.getContentEncoding().equalsIgnoreCase(HttpUtil.GZIP)) {
                is = new GZIPInputStream(urlc.getInputStream());
            }
            else {
                is = urlc.getInputStream();
            }
            reader = new BufferedReader(new InputStreamReader(is, Charset.forName(HttpUtil.UTF_8)));

            int c;
            while ((c = reader.read()) != -1) {
                dat.append((char) c);
            }
        }
        catch (IOException exception) {
            throw exception;
        }
        finally {
            try {
                reader.close();
                os.close();
                is.close();
            }
            catch (Exception e) {
                // we do not care about this
            }
        }

        return dat.toCharArray();
    }

    private static URLConnection getConnection(URL url) throws IOException {
        URLConnection urlc;

        urlc = url.openConnection();
        urlc.setUseCaches(false);
        urlc.setRequestProperty(HttpUtil.CONTENT_TYPE, "application/x-www-form-urlencoded");
        urlc.setRequestProperty(HttpUtil.USER_AGENT,
                "Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.9.1.9) Gecko/20100414 Iceweasel/3.5.9 (like Firefox/3.5.9)");
        urlc.setRequestProperty(HttpUtil.ACCEPT_ENCODING, HttpUtil.GZIP);

        return urlc;
    }
}
