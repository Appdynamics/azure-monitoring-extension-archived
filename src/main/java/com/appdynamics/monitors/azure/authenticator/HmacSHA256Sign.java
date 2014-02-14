package com.appdynamics.monitors.azure.authenticator;

import com.google.common.io.BaseEncoding;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HmacSHA256Sign {
    private Mac hmacSha256;
    private String accountKey;
    public HmacSHA256Sign(String accountKey) {
        this.accountKey = accountKey;    
    }

    public synchronized String computeMacSha256(final String stringToSign)
            throws InvalidKeyException {
        if (hmacSha256 == null) {
            initHmacSha256(accountKey);
        }

        byte[] utf8Bytes = null;
        try {
            utf8Bytes = stringToSign.getBytes("UTF8");
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }

        return BaseEncoding.base64().encode(hmacSha256.doFinal(utf8Bytes));
    }

    private void initHmacSha256(String accountKey) throws InvalidKeyException {

        SecretKey key256 = new SecretKeySpec(BaseEncoding.base64().decode(accountKey), "HmacSHA256");
        try {
            this.hmacSha256 = Mac.getInstance("HmacSHA256");
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException();
        }
        this.hmacSha256.init(key256);
    }
}
