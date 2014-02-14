package com.appdynamics.monitors.azure.statsCollector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TableStatsCollector extends StorageStatsCollector {

    private static final Logger LOG = Logger.getLogger(TableStatsCollector.class);

    private static final String TABLE_REST = "https://%s.table.core.windows.net/Tables";

    private static final String METRIC_PATH = "Storage|Table|%s|No Of Tables";
    
    public static final String STORAGE_ACCOUNT_NAMES_FOR_TABLE_KEY = "STORAGE_ACCOUNT_NAMES_FOR_TABLE";
    
    @Override
    public Map<String, Number> collectStats(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, Properties displayProperties) {

        String storageAccountNamesForTableString = displayProperties.getProperty(STORAGE_ACCOUNT_NAMES_FOR_TABLE_KEY);

        if(storageAccountNamesForTableString == null || storageAccountNamesForTableString.length() == 0) {
            LOG.error("No storage account name(s) defined for "+ STORAGE_ACCOUNT_NAMES_FOR_TABLE_KEY +". To show stats add them in metrics.property");
            return null;
        }

        String[] storageAccountNamesForTable = storageAccountNamesForTableString.split(",");

        List<String> storageAccountNamesForTableList = Arrays.asList(storageAccountNamesForTable);
        Map<String, String> storageAccountNamesWithKey = getStorageAccountNamesWithKey(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, storageAccountNamesForTableList);

        Map<String, Number> tableStatsMap = new LinkedHashMap<String, Number>();
        for(Map.Entry<String, String> storageAccountNameKey : storageAccountNamesWithKey.entrySet()) {
            String tableURL = String.format(TABLE_REST, storageAccountNameKey.getKey());

            HttpURLConnection httpConnection = createHttpConnectionWithHeadersForTable(tableURL, storageAccountNameKey.getKey(), storageAccountNameKey.getValue());

                try {
                    InputStream responseStream =  (InputStream) httpConnection.getContent();
                    Document document = parseResponse(responseStream);
                    NodeList containersNodeList = document.getElementsByTagName("entry");
                    int length = containersNodeList.getLength();
                    tableStatsMap.put(String.format(METRIC_PATH, storageAccountNameKey.getKey()), length);
                } catch (IOException e) {
                    LOG.error("Unable to process response", e);
                }
        }
        return tableStatsMap;
    }

    public static void main(String[] args) throws IOException {
        String subscriptionId = "4814ab8f-ebb8-42b5-ac5c-69b675d19e2c";
        String keyStorePath = "/home/satish/WindowsAzureKeyStore.jks";
        String keyStorePassword = "appdAzure123";

        TableStatsCollector tableStatsCollector = new TableStatsCollector();

        Properties properties = new Properties();
        properties.load(new FileInputStream("/home/satish/AppDynamics/Code/extensions/azure-monitoring-extension/src/main/resources/config/metrics.properties"));
        
        Map<String, Number> stringNumberMap = tableStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, "2013-11-01", properties);
        System.out.println(stringNumberMap);
    }
}
