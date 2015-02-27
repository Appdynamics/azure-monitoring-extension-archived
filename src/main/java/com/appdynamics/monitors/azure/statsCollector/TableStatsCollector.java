package com.appdynamics.monitors.azure.statsCollector;

import com.appdynamics.monitors.azure.request.AzureHttpsClient;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TableStatsCollector extends StorageStatsCollector {

    private static final Logger LOG = Logger.getLogger(TableStatsCollector.class);

    private static final String TABLE_REST = "https://%s.table.core.windows.net/Tables";

    private static final String METRIC_PATH = "Storage|Table|%s|No Of Tables";

    public static final String STORAGE_ACCOUNT_NAMES_FOR_TABLE_KEY = "STORAGE_ACCOUNT_NAMES_FOR_TABLE";

    public TableStatsCollector(AzureHttpsClient azureHttpsClient) {
        super(azureHttpsClient);
    }

    @Override
    public Map<String, Number> collectStats(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, Properties displayProperties) {

        String storageAccountNamesForTableString = displayProperties.getProperty(STORAGE_ACCOUNT_NAMES_FOR_TABLE_KEY);

        if (storageAccountNamesForTableString == null || storageAccountNamesForTableString.length() == 0) {
            LOG.error("No storage account name(s) defined for " + STORAGE_ACCOUNT_NAMES_FOR_TABLE_KEY + ". To show stats add them in metrics.property");
            return null;
        }

        String[] storageAccountNamesForTable = storageAccountNamesForTableString.split(",");

        List<String> storageAccountNamesForTableList = Arrays.asList(storageAccountNamesForTable);
        Map<String, String> storageAccountNamesWithKey = getStorageAccountNamesWithKey(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, storageAccountNamesForTableList);

        Map<String, Number> tableStatsMap = new LinkedHashMap<String, Number>();
        for (Map.Entry<String, String> storageAccountNameKey : storageAccountNamesWithKey.entrySet()) {
            String tableURL = String.format(TABLE_REST, storageAccountNameKey.getKey());
            Document document = azureHttpsClient.createHttpConnectionWithHeadersForTable(tableURL, storageAccountNameKey.getKey(), storageAccountNameKey.getValue());
            NodeList containersNodeList = document.getElementsByTagName("entry");
            int length = containersNodeList.getLength();
            tableStatsMap.put(String.format(METRIC_PATH, storageAccountNameKey.getKey()), length);
        }
        return tableStatsMap;
    }
}
