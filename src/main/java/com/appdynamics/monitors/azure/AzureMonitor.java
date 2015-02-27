package com.appdynamics.monitors.azure;

import com.appdynamics.monitors.azure.beans.ProxyDetails;
import com.appdynamics.monitors.azure.request.AzureHttpsClient;
import com.appdynamics.monitors.azure.statsCollector.BlobStatsCollector;
import com.appdynamics.monitors.azure.statsCollector.DatabaseStatsCollector;
import com.appdynamics.monitors.azure.statsCollector.DiskStatsCollector;
import com.appdynamics.monitors.azure.statsCollector.OSImageStatsCollector;
import com.appdynamics.monitors.azure.statsCollector.SubscriptionBasedStatsCollector;
import com.appdynamics.monitors.azure.statsCollector.TableStatsCollector;
import com.appdynamics.monitors.azure.statsCollector.WebsiteStatsCollector;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class AzureMonitor extends AManagedMonitor {

    private static final Logger LOG = Logger.getLogger(AzureMonitor.class);
    private static final String metricPathPrefix = "Custom Metrics|Azure|";
    private Properties displayProperties;

    public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Collecting stats from Azure");
        }

        String subscriptionId = taskArguments.get("subscriptionId");
        String keyStorePath = taskArguments.get("keyStorePath");
        String keyStorePassword = taskArguments.get("keyStorePassword");

        String restApiVersion = taskArguments.get("x-ms-version");

        String includeMetricsPath = taskArguments.get("include-metrics-path");

        String proxyHost = taskArguments.get("proxyHost");
        String proxyPort = taskArguments.get("proxyPort");
        String proxyUsername = taskArguments.get("proxyUsername");
        String proxyPassword = taskArguments.get("proxyPassword");

        ProxyDetails proxyDetails = new ProxyDetails();
        proxyDetails.setProxyHost(proxyHost);
        proxyDetails.setProxyPort(proxyPort);
        proxyDetails.setProxyUsername(proxyUsername);
        proxyDetails.setProxyPassword(proxyPassword);

        loadDisplayProperties(includeMetricsPath);

        AzureHttpsClient azureHttpsClient = new AzureHttpsClient(proxyDetails);

        SubscriptionBasedStatsCollector subscriptionBasedStatsCollector = new SubscriptionBasedStatsCollector(azureHttpsClient);
        Map<String, Number> subscriptionBasedStats = subscriptionBasedStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : subscriptionBasedStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        DiskStatsCollector diskStatsCollector = new DiskStatsCollector(azureHttpsClient);
        Map<String, Number> diskStats = diskStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : diskStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        OSImageStatsCollector osImageStatsCollector = new OSImageStatsCollector(azureHttpsClient);
        Map<String, Number> osImageStats = osImageStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : osImageStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        WebsiteStatsCollector websiteStatsCollector = new WebsiteStatsCollector(azureHttpsClient);
        Map<String, Number> websiteStats = websiteStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : websiteStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        DatabaseStatsCollector databaseStatsCollector = new DatabaseStatsCollector(azureHttpsClient);
        Map<String, Number> databaseStats = databaseStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : databaseStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        BlobStatsCollector blobStatsCollector = new BlobStatsCollector(azureHttpsClient);
        Map<String, Number> blobStats = blobStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : blobStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        TableStatsCollector tableStatsCollector = new TableStatsCollector(azureHttpsClient);
        Map<String, Number> tableStats = tableStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : tableStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Finished collecting stats from Azure");
        }

        return new TaskOutput("AzureMonitor completed successfully");
    }

    private void printMetric(String metricPrefix, String metricPath, Object metricValue) {
        MetricWriter metricWriter = super.getMetricWriter(metricPrefix + metricPath, MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE, MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
        );
        if (metricValue instanceof Double) {
            metricWriter.printMetric(String.valueOf(Math.round((Double) metricValue)));
        } else if (metricValue instanceof Float) {
            metricWriter.printMetric(String.valueOf(Math.round((Float) metricValue)));
        } else {
            metricWriter.printMetric(String.valueOf(metricValue));
        }
    }

    protected void loadDisplayProperties(String includeMetricsPath) {
        displayProperties = new Properties();
        try {
            displayProperties.load(new FileInputStream(includeMetricsPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}