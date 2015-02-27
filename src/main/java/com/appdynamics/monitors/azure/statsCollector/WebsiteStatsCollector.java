package com.appdynamics.monitors.azure.statsCollector;

import com.appdynamics.monitors.azure.request.AzureHttpsClient;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class WebsiteStatsCollector extends AbstractStatsCollector {

    private static final Logger LOG = Logger.getLogger(WebsiteStatsCollector.class);

    private static final String WEB_SPACES_REST = "https://management.core.windows.net/%s/services/WebSpaces";
    private static final String WEBSITES_REST = "https://management.core.windows.net/%s/services/WebSpaces/%s/sites/";
    private static final String WEBSITE_USAGE_REST = "https://management.core.windows.net/%s/services/WebSpaces/%s/sites/%s/usages";


    private static final String METRIC_PATH = "Web Space|%s|";

    private final AzureHttpsClient azureHttpsClient;

    public WebsiteStatsCollector(AzureHttpsClient azureHttpsClient) {
        this.azureHttpsClient = azureHttpsClient;
    }

    @Override
    public Map<String, Number> collectStats(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, Properties displayProperties) {
        URL webSpacesUrl = azureHttpsClient.buildRequestUrl(WEB_SPACES_REST, subscriptionId);

        Document webSpaceDocument = azureHttpsClient.processGetRequest(webSpacesUrl, restApiVersion, keyStorePath, keyStorePassword);

        Map<String, Number> websiteStatsMap = new LinkedHashMap<String, Number>();
        NodeList webSpaceNodeList = webSpaceDocument.getElementsByTagName("WebSpace");

        List<String> webSpaceNames = new ArrayList<String>();
        for (int i = 0; i < webSpaceNodeList.getLength(); i++) {
            Element element = (Element) webSpaceNodeList.item(i);
            String webSpaceName = element.getElementsByTagName("Name").item(0).getTextContent();

            String webSpaceMetricPath = String.format(METRIC_PATH, webSpaceName);

            webSpaceNames.add(webSpaceName);

            String currentNumberOfWorkersString = element.getElementsByTagName("CurrentNumberOfWorkers").item(0).getTextContent();

            Integer currentNumberOfWorkers = null;
            try {
                currentNumberOfWorkers = Integer.valueOf(currentNumberOfWorkersString);
            } catch (Exception e) {
                currentNumberOfWorkers = 0;
                LOG.error("CurrentNumberOfWorkers for webspace " + webSpaceName + " is null");
            }

            websiteStatsMap.put(webSpaceMetricPath + "CurrentNumberOfWorkers", currentNumberOfWorkers);
        }

        for (String webSpace : webSpaceNames) {
            URL webSitesUrl = azureHttpsClient.buildRequestUrl(WEBSITES_REST, subscriptionId, webSpace);

            Document websiteDocument = azureHttpsClient.processGetRequest(webSitesUrl, restApiVersion, keyStorePath, keyStorePassword);

            NodeList websiteNodeList = websiteDocument.getElementsByTagName("Site");
            for (int i = 0; i < websiteNodeList.getLength(); i++) {
                Element element = (Element) websiteNodeList.item(i);
                String websiteName = element.getElementsByTagName("DeploymentId").item(0).getTextContent();

                String websiteMetricPath = String.format(METRIC_PATH, webSpace) + websiteName + "|";
                String enabled = element.getElementsByTagName("Enabled").item(0).getTextContent();
                websiteStatsMap.put(websiteMetricPath + "Enabled", Boolean.valueOf(enabled) ? 1 : 0);

                //website usage stats

                URL webSitesUsageUrl = azureHttpsClient.buildRequestUrl(WEBSITE_USAGE_REST, subscriptionId, webSpace, websiteName);
                Document websiteUsageDocument = azureHttpsClient.processGetRequest(webSitesUsageUrl, restApiVersion, keyStorePath, keyStorePassword);
                NodeList websiteUsageNodeList = websiteUsageDocument.getElementsByTagName("Usage");

                String websiteUsageMetricPath = String.format(METRIC_PATH, webSpace) + websiteName + "|Usage Metrics|";

                for (int j = 0; j < websiteUsageNodeList.getLength(); j++) {

                    Element usageElement = (Element) websiteUsageNodeList.item(j);

                    String usageMetricName = usageElement.getElementsByTagName("DisplayName").item(0).getTextContent();
                    String usageMetricValueString = usageElement.getElementsByTagName("CurrentValue").item(0).getTextContent();
                    String unit = usageElement.getElementsByTagName("Unit").item(0).getTextContent();
                    Long usageMetricValue = null;
                    try {
                        usageMetricValue = Long.valueOf(usageMetricValueString);
                        websiteStatsMap.put(websiteUsageMetricPath + usageMetricName + "|" + unit + "|CurrentValue", usageMetricValue);
                    } catch (Exception e) {
                        LOG.error("Current value of " + usageMetricName + " for site " + websiteName + " in web space " + webSpace + " is not a number");
                    }

                    String usageMetricLimitString = usageElement.getElementsByTagName("Limit").item(0).getTextContent();
                    Long usageMetricLimit = null;
                    try {
                        usageMetricLimit = Long.valueOf(usageMetricLimitString);
                        websiteStatsMap.put(websiteUsageMetricPath + usageMetricName + "|" + unit + "|Limit", usageMetricLimit);
                    } catch (Exception e) {
                        LOG.error("Limit of " + usageMetricName + " for site " + websiteName + " in web space " + webSpace + " is not a number");
                    }
                }
            }
        }
        return websiteStatsMap;
    }
}
