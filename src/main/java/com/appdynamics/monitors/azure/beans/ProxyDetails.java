package com.appdynamics.monitors.azure.beans;


import com.google.common.base.Strings;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;

public class ProxyDetails {

    private String proxyHost;
    private Integer proxyPort;
    private String proxyUsername;
    private String proxyPassword;

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) throws TaskExecutionException {
        Integer port = null;
        try {
            if (!Strings.isNullOrEmpty(proxyPort)) {
                port = Integer.parseInt(proxyPort);
            }
        } catch (NumberFormatException e) {
            throw new TaskExecutionException("Unable to parse proxy port", e);
        }
        this.proxyPort = port;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
}
