package com.appdynamics.monitors.azure.request;

import com.appdynamics.monitors.azure.authenticator.BlobQueueSharedKeyAuth;
import com.appdynamics.monitors.azure.authenticator.TableSharedKeyAuth;
import com.appdynamics.monitors.azure.beans.ProxyDetails;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class AzureHttpsClient {

    private static final Logger LOG = Logger.getLogger(AzureHttpsClient.class);

    public static final String REQUEST_METHOD_GET = "GET";
    public static final String X_MS_VERSION_HEADER = "x-ms-version";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String CONTENT_TYPE_XML = "application/atom+xml";
    public static final String ACCEPT_TYPE_XML = "application/atom+xml,application/xml";
    public static final String X_MS_DATE_HEADER = "x-ms-date";

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String X_MS_VERSION = "2013-08-15";

    private final ProxyDetails proxyDetails;

    public AzureHttpsClient(ProxyDetails proxyDetails) {
        this.proxyDetails = proxyDetails;
    }

    private KeyStore getKeyStore(String keyStoreName, String password) {
        KeyStore ks = null;
        FileInputStream fis = null;
        try {
            ks = KeyStore.getInstance("JKS");
            char[] passwordArray = password.toCharArray();
            fis = new java.io.FileInputStream(keyStoreName);
            ks.load(fis, passwordArray);
            fis.close();

        } catch (CertificateException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (KeyStoreException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return ks;
    }

    private SSLSocketFactory getSSLSocketFactory(String keyStoreName, String password) {
        KeyStore ks = getKeyStore(keyStoreName, password);
        KeyManagerFactory keyManagerFactory = null;
        try {
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(ks, password.toCharArray());
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
            return context.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (KeyStoreException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (UnrecoverableKeyException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (KeyManagementException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Document processGetRequest(URL url, String restApiVersion, String keyStore, String keyStorePassword) {
        SSLSocketFactory sslFactory = getSSLSocketFactory(keyStore, keyStorePassword);
        HttpsURLConnection con = null;

        try {
            con = createConnection(url, proxyDetails);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        con.setSSLSocketFactory(sslFactory);
        try {
            con.setRequestMethod(REQUEST_METHOD_GET);
        } catch (ProtocolException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        con.addRequestProperty(X_MS_VERSION_HEADER, restApiVersion);

        InputStream responseStream = null;
        try {
            responseStream = (InputStream) con.getContent();
            Document document = parseResponse(responseStream);
            return document;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (responseStream != null) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public URL buildRequestUrl(String urlString, String... subscriptionId) {
        String subscriptionURL = String.format(urlString, subscriptionId);

        URL url = null;
        try {
            url = new URL(subscriptionURL);
        } catch (MalformedURLException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return url;
    }

    private Document parseResponse(InputStream responseStream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        Document document = null;
        try {
            document = documentBuilder.parse(responseStream);
        } catch (SAXException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        document.getDocumentElement().normalize();
        return document;
    }

    public Document createHttpConnectionWithHeadersForTable(String uri, String accountName, String accountKey) {

        try {
            URL myURL = new URL(uri);
            HttpsURLConnection con = createConnection(myURL, proxyDetails);

            setDefaultHeaders(con);

            String utcDate = getUTCDate();
            con.setRequestProperty(X_MS_DATE_HEADER, utcDate);

            TableSharedKeyAuth tableSharedKeyAuth = new TableSharedKeyAuth(accountName, accountKey);
            String sign = tableSharedKeyAuth.sign(REQUEST_METHOD_GET, "", CONTENT_TYPE_XML, utcDate, myURL);
            con.setRequestProperty(AUTHORIZATION_HEADER, sign);

            InputStream responseStream = (InputStream) con.getContent();
            try {
                Document document = parseResponse(responseStream);
                return document;
            } finally {
                responseStream.close();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Document processRequestWithHeadersForBlobQueue(String uri, String accountName, String accountKey) {

        try {
            URL myURL = new URL(uri);
            HttpsURLConnection con = createConnection(myURL, proxyDetails);

            setDefaultHeaders(con);

            String utcDate = getUTCDate();
            con.setRequestProperty(X_MS_DATE_HEADER, utcDate);

            BlobQueueSharedKeyAuth blobQueueSharedKeyAuth = new BlobQueueSharedKeyAuth(accountName, accountKey);

            Map<String, String> headers = new LinkedHashMap<String, String>();
            headers.put(X_MS_DATE_HEADER, utcDate);
            headers.put(X_MS_VERSION_HEADER, X_MS_VERSION);
            String sign = blobQueueSharedKeyAuth.sign(REQUEST_METHOD_GET, headers, myURL);
            con.setRequestProperty(AUTHORIZATION_HEADER, sign);

            InputStream responseStream = (InputStream) con.getContent();
            try {
                Document document = parseResponse(responseStream);
                return document;
            } finally {
                responseStream.close();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private HttpsURLConnection createConnection(URL url, final ProxyDetails proxyDetails) throws IOException {
        HttpsURLConnection con = null;
        if (proxyDetails != null && !Strings.isNullOrEmpty(proxyDetails.getProxyHost())) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyDetails.getProxyHost(), proxyDetails.getProxyPort()));
            con = (HttpsURLConnection) url.openConnection(proxy);

            if (!Strings.isNullOrEmpty(proxyDetails.getProxyUsername()) && !Strings.isNullOrEmpty(proxyDetails.getProxyPassword())) {
                Authenticator authenticator = new Authenticator() {

                    public PasswordAuthentication getPasswordAuthentication() {
                        return (new PasswordAuthentication(proxyDetails.getProxyUsername(),
                                proxyDetails.getProxyPassword().toCharArray()));
                    }
                };
                Authenticator.setDefault(authenticator);
            }

        } else {
            con = (HttpsURLConnection) url.openConnection();
        }
        return con;
    }

    private void setDefaultHeaders(HttpURLConnection con) throws ProtocolException {
        con.setRequestMethod(REQUEST_METHOD_GET);
        con.setRequestProperty(X_MS_VERSION_HEADER, X_MS_VERSION);
        con.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE_XML);
        con.setRequestProperty(ACCEPT_HEADER, ACCEPT_TYPE_XML);
    }

    private static String getUTCDate() {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        return SIMPLE_DATE_FORMAT.format(new Date());
    }

}
