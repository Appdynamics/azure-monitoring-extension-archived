package com.appdynamics.monitors.azure.authenticator;

import java.net.URL;
import java.security.InvalidKeyException;
import org.apache.log4j.Logger;

public class TableSharedKeyAuth extends SharedKeyAuth {

    private static final Logger LOG = Logger.getLogger(TableSharedKeyAuth.class);
    
    public TableSharedKeyAuth(String accountName, String accountKey) {
        super(accountName, accountKey);
    }

    public String sign(String reqMethod, String contentMD5, String contentType, String date, URL path) {
        String stringToSign = reqMethod + "\n" + contentMD5 + "\n"
                + contentType + "\n" + date + "\n";

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
}


