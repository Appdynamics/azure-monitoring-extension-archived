package com.appdynamics.monitors.azure.authenticator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Taken from microsoft azure api
 */
public class CanonicalizedResource {

    public static String getCanonicalizedResource(final java.net.URL address, String accountName) {
        // Resource path
        final StringBuilder resourcepath = new StringBuilder("/");
        resourcepath.append(accountName);

        // Note that AbsolutePath starts with a '/'.
        resourcepath.append(address.getPath());
        final StringBuilder canonicalizedResource = new StringBuilder(resourcepath.toString());

        // query parameters
        final Map<String, String[]> queryVariables = parseQueryString(address.getQuery());

        final Map<String, String> lowercasedKeyNameValue = new HashMap<String, String>();

        for (final Map.Entry<String, String[]> entry : queryVariables.entrySet()) {
            // sort the value and organize it as comma separated values
            final List<String> sortedValues = Arrays.asList(entry.getValue());
            Collections.sort(sortedValues);

            final StringBuilder stringValue = new StringBuilder();

            for (final String value : sortedValues) {
                if (stringValue.length() > 0) {
                    stringValue.append(",");
                }

                stringValue.append(value);
            }

            // key turns out to be null for ?a&b&c&d
            lowercasedKeyNameValue.put(entry.getKey() == null ? null : entry.getKey().toLowerCase(Locale.US),
                    stringValue.toString());
        }

        final ArrayList<String> sortedKeys = new ArrayList<String>(lowercasedKeyNameValue.keySet());

        Collections.sort(sortedKeys);

        for (final String key : sortedKeys) {
            final StringBuilder queryParamString = new StringBuilder();

            queryParamString.append(key);
            queryParamString.append(":");
            queryParamString.append(lowercasedKeyNameValue.get(key));

            appendCanonicalizedElement(canonicalizedResource, queryParamString.toString());
        }

        return canonicalizedResource.toString();
    }

    protected static void appendCanonicalizedElement(final StringBuilder builder, final String element) {
        builder.append("\n");
        builder.append(element);
    }


    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.length() == 0;
    }


    public static HashMap<String, String[]> parseQueryString(String parseString) {
        final HashMap<String, String[]> retVals = new HashMap<String, String[]>();
        if (isNullOrEmpty(parseString)) {
            return retVals;
        }

        // 1. Remove ? if present
        final int queryDex = parseString.indexOf("?");
        if (queryDex >= 0 && parseString.length() > 0) {
            parseString = parseString.substring(queryDex + 1);
        }

        // 2. split name value pairs by splitting on the 'c&' character
        final String[] valuePairs = parseString.contains("&") ? parseString.split("&") : parseString.split(";");

        // 3. for each field value pair parse into appropriate map entries
        for (int m = 0; m < valuePairs.length; m++) {
            final int equalDex = valuePairs[m].indexOf("=");

            if (equalDex < 0 || equalDex == valuePairs[m].length() - 1) {
                continue;
            }

            String key = valuePairs[m].substring(0, equalDex);
            String value = valuePairs[m].substring(equalDex + 1);

            key = safeDecode(key);
            value = safeDecode(value);

            // 3.1 add to map
            String[] values = retVals.get(key);

            if (values == null) {
                values = new String[] { value };
                if (!value.equals("")) {
                    retVals.put(key, values);
                }
            }
            else if (!value.equals("")) {
                final String[] newValues = new String[values.length + 1];
                for (int j = 0; j < values.length; j++) {
                    newValues[j] = values[j];
                }

                newValues[newValues.length] = value;
            }
        }

        return retVals;
    }

    public static String safeDecode(final String stringToDecode)  {
        if (stringToDecode == null) {
            return null;
        }

        if (stringToDecode.length() == 0) {
            return "";
        }

        try {
            if (stringToDecode.contains("+")) {
                final StringBuilder outBuilder = new StringBuilder();

                int startDex = 0;
                for (int m = 0; m < stringToDecode.length(); m++) {
                    if (stringToDecode.charAt(m) == '+') {
                        if (m > startDex) {
                            outBuilder.append(URLDecoder.decode(stringToDecode.substring(startDex, m), "UTF-8"));
                        }

                        outBuilder.append("+");
                        startDex = m + 1;
                    }
                }

                if (startDex != stringToDecode.length()) {
                    outBuilder.append(URLDecoder.decode(stringToDecode.substring(startDex, stringToDecode.length()),
                            "UTF-8"));
                }

                return outBuilder.toString();
            }
            else {
                return URLDecoder.decode(stringToDecode, "UTF-8");
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}