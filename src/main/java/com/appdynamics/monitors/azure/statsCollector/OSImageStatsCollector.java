package com.appdynamics.monitors.azure.statsCollector;

import com.appdynamics.monitors.azure.request.AzureHttpsClient;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class OSImageStatsCollector extends AbstractStatsCollector {

    private static final Logger LOG = Logger.getLogger(OSImageStatsCollector.class);

    private static final String OS_IMAGE_REST = "https://management.core.windows.net/%s/services/images";
    private static final String METRIC_PATH = "OS Image|%s|LogicalSizeInGB|";

    private final AzureHttpsClient azureHttpsClient;

    public OSImageStatsCollector(AzureHttpsClient azureHttpsClient) {
        this.azureHttpsClient = azureHttpsClient;
    }

    @Override
    public Map<String, Number> collectStats(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, Properties displayProperties) {
        URL url = azureHttpsClient.buildRequestUrl(OS_IMAGE_REST, subscriptionId);

        Document document = azureHttpsClient.processGetRequest(url, restApiVersion, keyStorePath, keyStorePassword);

        Map<String, Number> osImageStatsMap = new LinkedHashMap<String, Number>();

        NodeList imageNameNodeList = document.getElementsByTagName("OSImage");
        
        for(int i = 0; i < imageNameNodeList.getLength(); i++) {
            Element element = (Element)imageNameNodeList.item(i);
            String osName = element.getElementsByTagName("Name").item(0).getTextContent();
            String osImageSizeString = element.getElementsByTagName("LogicalSizeInGB").item(0).getTextContent();

            Integer statValue = null;
            try {
                statValue = Integer.parseInt(osImageSizeString);
                osImageStatsMap.put(String.format(METRIC_PATH, osName), statValue);
            } catch (NumberFormatException nfe) {
                LOG.error("Unable to parse os image size "+ osImageSizeString +" to integer");
            }
        }

        return osImageStatsMap;
    }
}
