package com.appdynamics.monitors.azure.statsCollector;

import com.appdynamics.monitors.azure.request.AzureHttpsClient;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class DatabaseStatsCollector extends AbstractStatsCollector {

    private static final Logger LOG = Logger.getLogger(DatabaseStatsCollector.class);

    private static final String DATABASE_SERVERS_REST = "https://management.core.windows.net/%s/services/sqlservers/servers?contentview=generic";
    private static final String DATABASE_REST = "https://management.core.windows.net/%s/services/sqlservers/servers/%s/databases?contentview=generic";
    private static final String METRIC_PATH = "DATABASE SERVER|%s|DATABASE|%s|";
    private static final int MB_TO_BYTE = 1048576;

    private final AzureHttpsClient azureHttpsClient;

    public DatabaseStatsCollector(AzureHttpsClient azureHttpsClient) {
        this.azureHttpsClient = azureHttpsClient;
    }

    @Override
    public Map<String, Number> collectStats(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, Properties displayProperties) {
        URL url = azureHttpsClient.buildRequestUrl(DATABASE_SERVERS_REST, subscriptionId);

        InputStream responseStream = azureHttpsClient.processGetRequest(url, restApiVersion, keyStorePath, keyStorePassword);

        Document document = azureHttpsClient.parseResponse(responseStream);

        Map<String, Number> databaseStatsMap = new LinkedHashMap<String, Number>();

        NodeList databaseServersNodeList = document.getElementsByTagName("ServiceResource");

        for(int i = 0; i < databaseServersNodeList.getLength(); i++) {
            Element element = (Element)databaseServersNodeList.item(i);
            String databaseServerName = element.getElementsByTagName("Name").item(0).getTextContent();

            URL databaseURL = azureHttpsClient.buildRequestUrl(DATABASE_REST, subscriptionId, databaseServerName);

            InputStream databaseResponseStream = azureHttpsClient.processGetRequest(databaseURL, restApiVersion, keyStorePath, keyStorePassword);

            Document databaseDocument = azureHttpsClient.parseResponse(databaseResponseStream);
            NodeList databaseNodeList = databaseDocument.getElementsByTagName("ServiceResource");
            
            for(int j = 0; j < databaseNodeList.getLength(); j++) {
                Element databaseElement = (Element)databaseNodeList.item(j);
                
                String databaseName = databaseElement.getElementsByTagName("Name").item(0).getTextContent();
                
                String maxSizeBytesString = databaseElement.getElementsByTagName("MaxSizeBytes").item(0).getTextContent();
                
                Long maxSizeBytes = null;
                try {
                    maxSizeBytes = Long.parseLong(maxSizeBytesString);
                    databaseStatsMap.put(String.format(METRIC_PATH, databaseServerName, databaseName)+"MaxSizeBytes", maxSizeBytes);
                } catch (NumberFormatException nfe) {
                    LOG.error("Unable to parse MaxSizeBytes "+ maxSizeBytesString +" of database ");
                }
                
                String sizeMBString = databaseElement.getElementsByTagName("SizeMB").item(0).getTextContent();
                if(sizeMBString != null && sizeMBString.equals("")) {
                    sizeMBString = "0";
                }
                Double sizeMB = null;
                try {
                    sizeMB = Double.parseDouble(sizeMBString);
                    long sizeInBytes = megaByteToByte(sizeMB);
                    databaseStatsMap.put(String.format(METRIC_PATH, databaseServerName, databaseName)+"SizeBytes", sizeInBytes);
                } catch (NumberFormatException nfe) {
                    LOG.error("Unable to parse SizeBytes "+ sizeMBString +" of database "+databaseName);
                }
            }
        }
        return databaseStatsMap;
    }
    
    private long megaByteToByte(double sizeMB) {
        return Math.round(sizeMB * MB_TO_BYTE);        
    }
}
