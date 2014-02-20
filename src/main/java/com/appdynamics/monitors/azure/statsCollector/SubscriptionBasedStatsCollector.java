package com.appdynamics.monitors.azure.statsCollector;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SubscriptionBasedStatsCollector extends AbstractStatsCollector {

    private static final Logger LOG = Logger.getLogger(SubscriptionBasedStatsCollector.class);
    private static final String SUBSCRIPTION_PROPERTY_KEY = "Subscription";

    private static final String METRIC_PATH = "Subscription|%s|";
    
    private static final String SUBSCRIPTION_REST = "https://management.core.windows.net/%s";

    @Override
    public Map<String, Number> collectStats(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, Properties displayProperties)  {

        
        String subscriptionPropertyString = displayProperties.getProperty(SUBSCRIPTION_PROPERTY_KEY);
        
        if(subscriptionPropertyString == null || subscriptionPropertyString.length() == 0) {
            LOG.error("No stats defined for "+ SUBSCRIPTION_PROPERTY_KEY +". To show stats add them in metrics.property");
            return null;
        }
        
        String[] subscriptionStatsToShow = subscriptionPropertyString.split(",");

        URL url = buildRequestUrl(SUBSCRIPTION_REST, subscriptionId);
        
        InputStream responseStream = processGetRequest(url, restApiVersion, keyStorePath, keyStorePassword);

        Document document = parseResponse(responseStream);

        Map<String, Number> subscriptionStatsMap = new LinkedHashMap<String, Number>();
        for(String subscriptionProperty : subscriptionStatsToShow) {
            NodeList nodeList = document.getElementsByTagName(subscriptionProperty);
            String textContent = nodeList.item(0).getTextContent();
            Integer statValue = null;
            try {
                statValue = Integer.parseInt(textContent);
                subscriptionStatsMap.put(String.format(METRIC_PATH, subscriptionId)+subscriptionProperty, statValue);
            } catch (NumberFormatException nfe) {
                LOG.error("Unable to parse "+subscriptionProperty+" value to integer");
            }
        }

        return subscriptionStatsMap;
    }
}
