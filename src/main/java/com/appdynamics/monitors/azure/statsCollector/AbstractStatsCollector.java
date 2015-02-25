package com.appdynamics.monitors.azure.statsCollector;

import java.util.Map;
import java.util.Properties;

public abstract class AbstractStatsCollector {

    protected abstract Map<String, Number> collectStats(String keyStorePath, String keyStorePassword, String subscriptionId, String restApiVersion, Properties displayProperties);
}
