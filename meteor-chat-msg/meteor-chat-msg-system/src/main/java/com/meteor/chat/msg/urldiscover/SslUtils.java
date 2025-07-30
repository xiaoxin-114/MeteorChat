package com.meteor.chat.msg.urldiscover;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * @author Odinpeng
 * @since 2023/12/07
 **/
public class SslUtils {

    /**
     * 忽略HTTPS请求的SSL证书
     *
     * @throws Exception
     */
    public static void ignoreSsl() throws Exception {
        HostnameVerifier hv = (urlHostName, session) -> {
            System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
            return true;
        };
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    static class miTM implements TrustManager, X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }
    }
}

