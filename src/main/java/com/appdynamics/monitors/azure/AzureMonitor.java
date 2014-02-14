package com.appdynamics.monitors.azure;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class AzureMonitor extends AManagedMonitor {

    private static final Logger LOG = Logger.getLogger(AzureMonitor.class);
    private static final String metricPathPrefix = "Custom Metrics|Azure|";
    private Properties displayProperties;

    public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        
        if(LOG.isDebugEnabled()) {
            LOG.debug("Collecting stats from Azure");
        }
        
        String subscriptionId = taskArguments.get("subscriptionId");
        String keyStorePath = taskArguments.get("keyStorePath");
        String keyStorePassword = taskArguments.get("keyStorePassword");

        String restApiVersion = taskArguments.get("x-ms-version");

        String includeMetricsPath = taskArguments.get("include-metrics-path");

        loadDisplayProperties(includeMetricsPath);

        SubscriptionBasedStatsCollector subscriptionBasedStatsCollector = new SubscriptionBasedStatsCollector();
        Map<String, Number> subscriptionBasedStats = subscriptionBasedStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : subscriptionBasedStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        DiskStatsCollector diskStatsCollector = new DiskStatsCollector();
        Map<String, Number> diskStats = diskStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : diskStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        OSImageStatsCollector osImageStatsCollector = new OSImageStatsCollector();
        Map<String, Number> osImageStats = osImageStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : osImageStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        WebsiteStatsCollector websiteStatsCollector = new WebsiteStatsCollector();
        Map<String, Number> websiteStats = websiteStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : websiteStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        DatabaseStatsCollector databaseStatsCollector = new DatabaseStatsCollector();
        Map<String, Number> databaseStats = databaseStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : databaseStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        BlobStatsCollector blobStatsCollector = new BlobStatsCollector();
        Map<String, Number> blobStats = blobStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : blobStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        TableStatsCollector tableStatsCollector = new TableStatsCollector();
        Map<String, Number> tableStats = tableStatsCollector.collectStats(keyStorePath, keyStorePassword, subscriptionId, restApiVersion, displayProperties);

        for (Map.Entry<String, Number> stat : tableStats.entrySet()) {
            Number statValue = stat.getValue();
            if (statValue == null) {
                statValue = 0;
            }

            printMetric(metricPathPrefix, stat.getKey(), statValue);
        }

        if(LOG.isDebugEnabled()) {
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