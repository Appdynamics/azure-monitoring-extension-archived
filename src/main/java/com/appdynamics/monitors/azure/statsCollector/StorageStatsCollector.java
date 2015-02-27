package com.appdynamics.monitors.azure.statsCollector;

import com.appdynamics.monitors.azure.request.AzureHttpsClient;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class StorageStatsCollector extends AbstractStatsCollector {

    private static final Logger LOG = Logger.getLogger(StorageStatsCollector.class);  
    private static final String STORAGE_ACCOUNT_REST = "https://management.core.windows.net/%s/services/storageservices";
    private static final String STORAGE_ACCOUNT_KEYS_REST = "https://management.core.windows.net/%s/services/storageservices/%s/keys";

    protected final AzureHttpsClient azureHttpsClient;

    public StorageStatsCollector(AzureHttpsClient azureHttpsClient) {
        this.azureHttpsClient = azureHttpsClient;
    }

    protected Map<String, String> getStorageAccountNamesWithKey(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, List<String> storageAccountNames) {
        Map<String, String> storageAccountNameKey = new HashMap<String, String>();
        
        URL url = azureHttpsClient.buildRequestUrl(STORAGE_ACCOUNT_REST, subscriptionId);

        Document document = azureHttpsClient.processGetRequest(url, restApiVersion, keyStorePath, keyStorePassword);

        NodeList storageAccountsNodeList = document.getElementsByTagName("StorageService");
        for(int i = 0; i < storageAccountsNodeList.getLength(); i++) {
            Element element = (Element)storageAccountsNodeList.item(i);
            String storageAccountName = element.getElementsByTagName("ServiceName").item(0).getTextContent();

            if(storageAccountNames.contains(storageAccountName)) {
                URL storageAccountKeysUrl = azureHttpsClient.buildRequestUrl(STORAGE_ACCOUNT_KEYS_REST, subscriptionId, storageAccountName);

                Document storageAccountKeysDocument = azureHttpsClient.processGetRequest(storageAccountKeysUrl, restApiVersion, keyStorePath, keyStorePassword);
                NodeList storageAccountKeysNodeList = storageAccountKeysDocument.getElementsByTagName("StorageServiceKeys");
                String primaryKey = ((Element) storageAccountKeysNodeList.item(0)).getElementsByTagName("Primary").item(0).getTextContent();
                storageAccountNameKey.put(storageAccountName, primaryKey);
            }
        }
       
        //In case we are unable to find storage account keys..log the not found account names.
        if(storageAccountNames.size() != storageAccountNameKey.size() ) {
            Set<String> storageAccountNamesFound = storageAccountNameKey.keySet();
            storageAccountNames.removeAll(storageAccountNamesFound);
            LOG.error("Unable to find storage account details for accounts "+storageAccountNames);            
        }
        return storageAccountNameKey;
    }
}
