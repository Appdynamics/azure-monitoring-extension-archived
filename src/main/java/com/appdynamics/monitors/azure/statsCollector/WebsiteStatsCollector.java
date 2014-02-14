package com.appdynamics.monitors.azure.statsCollector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WebsiteStatsCollector extends AbstractStatsCollector {

    private static final Logger LOG = Logger.getLogger(WebsiteStatsCollector.class);

    private static final String WEB_SPACES_REST = "https://management.core.windows.net/%s/services/WebSpaces";
    private static final String WEBSITES_REST = "https://management.core.windows.net/%s/services/WebSpaces/%s/sites/";
    private static final String WEBSITE_USAGE_REST = "https://management.core.windows.net/%s/services/WebSpaces/%s/sites/%s/usages";
    
    
    private static final String METRIC_PATH = "Web Space|%s|";
    
    @Override
    public Map<String, Number> collectStats(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, Properties displayProperties) {
        URL webSpacesUrl = buildRequestUrl(WEB_SPACES_REST, subscriptionId);

        InputStream responseStream = processGetRequest(webSpacesUrl, restApiVersion, keyStorePath, keyStorePassword);

        Document webSpaceDocument = parseResponse(responseStream);

        Map<String, Number> websiteStatsMap = new LinkedHashMap<String, Number>();
        NodeList webSpaceNodeList = webSpaceDocument.getElementsByTagName("WebSpace");

        List<String> webSpaceNames = new ArrayList<String>();
        for(int i = 0; i < webSpaceNodeList.getLength(); i++) {
            Element element = (Element)webSpaceNodeList.item(i);
            String webSpaceName = element.getElementsByTagName("Name").item(0).getTextContent();

            String webSpaceMetricPath =  String.format(METRIC_PATH, webSpaceName);
            
            webSpaceNames.add(webSpaceName);
            
            //String availabilityState = element.getElementsByTagName("AvailabilityState").item(0).getTextContent();
            String currentNumberOfWorkersString = element.getElementsByTagName("CurrentNumberOfWorkers").item(0).getTextContent();

            Integer currentNumberOfWorkers = null; 
            try {
                currentNumberOfWorkers = Integer.valueOf(currentNumberOfWorkersString) ;
            } catch (Exception e) {
                currentNumberOfWorkers = 0;
                LOG.error("CurrentNumberOfWorkers for webspace "+webSpaceName+" is null");                
            }
            
            /*String currentWorkerSizeString = element.getElementsByTagName("CurrentWorkerSize").item(0).getTextContent();
            
            Integer currentWorkerSize = null;
            try {
                currentWorkerSize = CurrentWorkerSize.valueOf(currentWorkerSizeString).ordinal();
            } catch(Exception e) {
                currentWorkerSize = 0;
                LOG.error("CurrentWorkerSize for webspace "+webSpaceName+" is null");    
            }
            
            
            String statusString = element.getElementsByTagName("Status").item(0).getTextContent();

            Integer status = null;
            try {
                status = Status.valueOf(statusString).ordinal();
            } catch(Exception e) {
                LOG.error("Status for webspace "+webSpaceName+" is null");
            }
            
            String workerSizeString = element.getElementsByTagName("WorkerSize").item(0).getTextContent();

            Integer workerSize = null;
            try {
                workerSize = WorkerSize.valueOf(workerSizeString).ordinal();
            } catch(Exception e) {
                workerSize = 0;
                LOG.error("WorkerSize for webspace "+webSpaceName+" is null");
            }*/

            //websiteStatsMap.put(webSpaceMetricPath+"AvailabilityState", AvailabilityState.valueOf(availabilityState).ordinal());
            websiteStatsMap.put(webSpaceMetricPath+"CurrentNumberOfWorkers", currentNumberOfWorkers);
            /*websiteStatsMap.put(webSpaceMetricPath+"CurrentWorkerSize", currentWorkerSize);
            websiteStatsMap.put(webSpaceMetricPath+"Status", status);
            websiteStatsMap.put(webSpaceMetricPath+"WorkerSize", workerSize);*/
        }

        try {
            responseStream.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        for(String webSpace : webSpaceNames) {
            URL webSitesUrl = buildRequestUrl(WEBSITES_REST, subscriptionId, webSpace);
    
            responseStream = processGetRequest(webSitesUrl, restApiVersion, keyStorePath, keyStorePassword);

            Document websiteDocument = parseResponse(responseStream);

            NodeList websiteNodeList = websiteDocument.getElementsByTagName("Site");
            for(int i = 0; i < websiteNodeList.getLength(); i++) {
                Element element = (Element)websiteNodeList.item(i);
                String websiteName = element.getElementsByTagName("DeploymentId").item(0).getTextContent();

                String websiteMetricPath =  String.format(METRIC_PATH, webSpace)+websiteName+"|";
                               
                /*String availabilityState = element.getElementsByTagName("AvailabilityState").item(0).getTextContent();
                websiteStatsMap.put(websiteMetricPath+"AvailabilityState", AvailabilityState.valueOf(availabilityState).ordinal());

                String computeMode = element.getElementsByTagName("ComputeMode").item(0).getTextContent();
                try {
                    ComputeMode computeModeEnum = ComputeMode.valueOf(computeMode);
                    websiteStatsMap.put(websiteMetricPath + "ComputeMode", computeModeEnum.ordinal());
                } catch(Exception e) {
                    LOG.error("Unable to create ComputeMode from string "+computeMode); 
                }*/

                String enabled = element.getElementsByTagName("Enabled").item(0).getTextContent();
                websiteStatsMap.put(websiteMetricPath+"Enabled", Boolean.valueOf(enabled) ? 1 : 0);

                /*String runtimeAvailabilityState = element.getElementsByTagName("RuntimeAvailabilityState").item(0).getTextContent();
                websiteStatsMap.put(websiteMetricPath+"RuntimeAvailabilityState", RuntimeAvailabilityState.valueOf(runtimeAvailabilityState).ordinal());*/

                /*String siteMode = element.getElementsByTagName("SiteMode").item(0).getTextContent();
                websiteStatsMap.put(websiteMetricPath+"SiteMode", SiteMode.valueOf(siteMode).ordinal());

                String state = element.getElementsByTagName("State").item(0).getTextContent();
                websiteStatsMap.put(websiteMetricPath+"State", State.valueOf(state).ordinal());

                String usageState = element.getElementsByTagName("UsageState").item(0).getTextContent();
                websiteStatsMap.put(websiteMetricPath+"UsageState", UsageState.valueOf(usageState).ordinal());*/
                
                //website usage stats

                URL webSitesUsageUrl = buildRequestUrl(WEBSITE_USAGE_REST, subscriptionId, webSpace, websiteName);
                responseStream = processGetRequest(webSitesUsageUrl, restApiVersion, keyStorePath, keyStorePassword);
                Document websiteUsageDocument = parseResponse(responseStream);
                NodeList websiteUsageNodeList = websiteUsageDocument.getElementsByTagName("Usage");

                String websiteUsageMetricPath =  String.format(METRIC_PATH, webSpace)+websiteName+"|Usage Metrics|";
                
                for(int j = 0; j < websiteUsageNodeList.getLength(); j++) {

                    Element usageElement = (Element)websiteUsageNodeList.item(j);

                    String usageMetricName = usageElement.getElementsByTagName("DisplayName").item(0).getTextContent();
                    String usageMetricValueString = usageElement.getElementsByTagName("CurrentValue").item(0).getTextContent();
                    String unit = usageElement.getElementsByTagName("Unit").item(0).getTextContent();
                    Long usageMetricValue = null;
                    try {
                        usageMetricValue = Long.valueOf(usageMetricValueString) ;
                        websiteStatsMap.put(websiteUsageMetricPath+usageMetricName+"|"+unit+"|CurrentValue", usageMetricValue);
                    } catch (Exception e) {
                        LOG.error("Current value of "+usageMetricName +" for site "+websiteName+" in web space "+webSpace+" is not a number");
                    }
                    
                    String usageMetricLimitString = usageElement.getElementsByTagName("Limit").item(0).getTextContent();
                    Long usageMetricLimit = null;
                    try {
                        usageMetricLimit = Long.valueOf(usageMetricLimitString) ;
                        websiteStatsMap.put(websiteUsageMetricPath+usageMetricName+"|"+unit+"|Limit", usageMetricLimit);
                    } catch (Exception e) {
                        LOG.error("Limit of "+usageMetricName +" for site "+websiteName+" in web space "+webSpace+" is not a number");
                    }
                }
            }
            
        }
        return websiteStatsMap;
    }
    
    /*private enum AvailabilityState {
        Normal, Limited
    }
    
    private enum CurrentWorkerSize {
        Small, Medium, Large
    }
    
    private enum Status {
        Ready, Limited
    }
    
    private enum WorkerSize {
        Small, Medium, Large
    }

    private enum ComputeMode {
        Shared, Dedicated
    }
    
    private enum RuntimeAvailabilityState {
        Normal, Degraded, NotAvailable
    }
    
    private enum SiteMode {
        Limited, Basic
    }
    
    private enum State {
        Stopped, Running
    }
    
    private enum UsageState {
        Normal, Exceeded
    }*/
}
