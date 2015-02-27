# Windows Azure Monitoring Extension

This extension works only with the standalone machine agent.

##Use Case

Windows Azure is an Internet-scale computing and services platform hosted in Microsoft data centers. It includes a number of features with corresponding developer services which can be used individually or together.


##Prerequisite
Create and export management certificate to azure
For steps to create a certificate and export to Azure visit http://gauravmantri.com/2013/08/25/consuming-windows-azure-service-management-api-in-java/

##Installation

1. Run "mvn clean install"
2. Download and unzip the file 'target/AzureMonitor.zip' to \<machineagent install dir\}/monitors
3. Open <b>monitor.xml</b> and configure the Azure arguments

```
<argument name="subscriptionId" is-required="true" default-value="4814ab8f-ebb8-42b5-ac5c-69b675d19e2c" />
<argument name="keyStorePath" is-required="true" default-value="/home/satish/AppDynamics/Azure/WindowsAzureKeyStore.jks" />
<argument name="keyStorePassword" is-required="true" default-value="appdAzure123" />

<!-- Azure REST API version, More info at http://msdn.microsoft.com/en-us/library/windowsazure/dn166981.aspx -->
 <argument name="x-ms-version" is-required="true" default-value="2013-11-01" />
 <!-- The configuration file which lists out the metrics to be included from monitoring on controller-->
 <argument name="include-metrics-path" is-required="true" default-value="monitors/AzureMonitor/metrics.properties" />

 <!--Proxy server details-->
<argument name="proxyHost" is-required="false" default-value="" />
<argument name="proxyPort" is-required="false" default-value="" />
<argument name="proxyUsername" is-required="false" default-value="" />
<argument name="proxyPassword" is-required="false" default-value="" />
```

<b>SubscriptionId</b> : Azure subscription id <br/>
<b>keyStorePath</b> : Path of keystore which is created and uploaded to azure <br/>
<b>keyStorePassword</b> : Password for the keystore <br/>
<b>x-ms-version</b> : Azure REST API version (For more info refer http://msdn.microsoft.com/en-us/library/windowsazure/dn166981.aspx) <br/>
<b>include-metrics-path</b> : Path to metrics.properties <br/>
<b>proxyHost</b> : Proxy server host if any <br/>
<b>proxyPort</b> : Proxy server port if any <br/>
<b>proxyUsername</b> : Proxy server user name if any <br/>
<b>proxyPassword</b> : Proxy server password if any <br/>

 Open <b>metrics.properties</b> and configure storage account names <br/>
	<b>STORAGE_ACCOUNT_NAMES_FOR_TABLE</b>: Storage account name for which table level metrics to be fetched <br/>
	<b>STORAGE_ACCOUNT_NAMES_FOR_BLOB</b>: Storage account name for which blob level metrics to be fetched <br/>


##Metrics
The following metrics are reported.

###Subscription

| Metric Path  | Description  |
|---------------- |------------- |
|Azure/Subscription/{SubscriptionID}/MaxCoreCount|MaxCoreCount|
|Azure/Subscription/{SubscriptionID}/MaxStorageAccounts|MaxStorageAccounts|
|Azure/Subscription/{SubscriptionID}/MaxHostedServices|MaxHostedServices|
|Azure/Subscription/{SubscriptionID}/CurrentCoreCount|CurrentCoreCount|
|Azure/Subscription/{SubscriptionID}/CurrentHostedServices|CurrentHostedServices|
|Azure/Subscription/{SubscriptionID}/CurrentStorageAccounts|CurrentStorageAccounts|
|Azure/Subscription/{SubscriptionID}/MaxVirtualNetworkSites|MaxVirtualNetworkSites|
|Azure/Subscription/{SubscriptionID}/MaxLocalNetworkSites|MaxLocalNetworkSites|
|Azure/Subscription/{SubscriptionID}/MaxDnsServers|MaxDnsServers|

###Disks
| Metric Path  | Description  |
|---------------- |------------- |
|Azure/Disk/{Disk Name}/Disk Size|Disk Size|

###OS Image
| Metric Path  | Description  |
|---------------- |------------- |
|Azure/OS Image/{Name}/LogicalSizeInGB|Logical OS Disk Size|

###Website Management
| Metric Path  | Description  |
|---------------- |------------- |
|Azure/Web Space/{Web Space Name}/CurrentNumberOfWorkers|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Enabled|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/CPU Time/{Unit}/CurrentValue|CPU Time currentvalue|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/CPU Time/{Unit}/Limit|CPU Time limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Data In/{Unit}/CurrentValue|Data in current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Data In/{Unit}/Limit|Data in limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Data Out/{Unit}/CurrentValue|Data out current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Data Out/{Unit}/Limit|Data out limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Local bytes read/{Unit}/CurrentValue|Local bytes read current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Local bytes read/{Unit}/Limit|Local bytes read limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Local bytes written/{Unit}/CurrentValue|Local bytes written current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Local bytes written/{Unit}/Limit|Local bytes written limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Network bytes read/{Unit}/CurrentValue|Network bytes read current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Network bytes read/{Unit}/Limit|Network bytes read limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Network bytes written/{Unit}/CurrentValue|Network bytes written current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Network bytes written/{Unit}/Limit|Network bytes written limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/WP stop requests/{Unit}/CurrentValue|WP stop requests current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/WP stop requests/{Unit}/Limit|WP stop requests  limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Memory Usage/{Unit}/CurrentValue|Memory usage current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/Memory Usage/{Unit}/Limit|Memory usage limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/CPU Time - Minute Limit/{Unit}/CurrentValue|CPU time in minutes current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/CPU Time - Minute Limit/{Unit}/Limit|CPU time in minutes  limit|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/File System Storage/{Unit}/CurrentValue|File system storage current value|
|Azure/Web Space/{Web Space Name}/{Web Site Name}/Usage Metrics/File System Storage/{Unit}/Limit|File system storage  limit|

###Database Server 
| Metric Path  | Description  |
|---------------- |------------- |
|Azure/DATABASE SERVER/{DB Server Name}/DATABASE/{DB Name}/MaxSizeBytes|Max DB size in bytes|
|Azure/DATABASE SERVER/{DB Server Name}/DATABASE/{DB Name}/SizeBytes|DB Size in bytes|

###Storage

####Table
| Metric Path  | Description  |
|---------------- |------------- |
|Azure/Storage/Table/{Storage Account Name}/No Of Tables|Number of tables in storage account|

####Blob
| Metric Path  | Description  |
|---------------- |------------- |
|Azure/Storage/Blob/{Storage Account Name}/Container/{Container Name}/Blobs/{Blob Name}/Size|Blob size|

#Custom Dashboard
![](https://github.com/Appdynamics/azure-monitoring-extension/raw/master/Azure_Custom_Dashboard.png)

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/AppDynamics-eXchange/AppDynamics-Azure-Monitoring-Extension/idi-p/6863) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:help@appdynamics.com).
