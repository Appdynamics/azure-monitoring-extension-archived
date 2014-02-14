package com.appdynamics.monitors.azure.authenticator;

import java.net.URL;
import java.security.InvalidKeyException;
import java.util.Map;
import org.apache.log4j.Logger;

public class BlobQueueSharedKeyAuth extends SharedKeyAuth {

    private static final Logger LOG = Logger.getLogger(BlobQueueSharedKeyAuth.class);

    public BlobQueueSharedKeyAuth(String accountName, String accountKey) {
        super(accountName, accountKey);
    }

    public String sign(String reqMethod, Map<String, String> headers, URL path) {
        String stringToSign =  reqMethod + "\n" +
                        "" + "\n" +
                        "" + "\n" +
                        "" + "\n"  +
                        "" + "\n" +
                        "application/atom+xml" + "\n" +
                        "" + "\n" +
                        "" + "\n" +
                        "" + "\n" +
                        "" + "\n" +
                        "" + "\n" +
                        "" + "\n" +
                        buildHeaders(headers); 
        stringToSign += CanonicalizedResource.getCanonicalizedResource(path, getAccountName());

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("String to sign: \"%s\"", stringToSign));
        }

        String signature = null;
        try {
            signature = this.getSigner().computeMacSha256(stringToSign);
        } catch (InvalidKeyException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return "SharedKey " + this.getAccountName() + ":" + signature;
    }

    private String buildHeaders(Map<String, String> headers) {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");            
        }
        
        return builder.toString();
    }
}
