package com.utils;


import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import com.sabdroidex.utils.Preferences;

public class ApacheCredentialProvider {
    
   public static CredentialsProvider getCredentialsProvider() {
        CredentialsProvider cp = new BasicCredentialsProvider();

        if (Preferences.isEnabled(Preferences.APACHE)) {
            AuthScope scope = new AuthScope(AuthScope.ANY);
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
                   Preferences.get(Preferences.APACHE_USERNAME),
                   Preferences.get(Preferences.APACHE_PASSWORD));

            cp.setCredentials(scope, creds);
        }
        
        return cp;
   }
}
