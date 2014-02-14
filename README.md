
 Azure Monitoring Extension Design Specs
1) Introduction
a) Overview

Windows Azure is an Internet-scale computing and services platform hosted in Microsoft data centers. It includes a number of features with corresponding developer services which can be used individually or together.

Through the use of Azure REST API(http://msdn.microsoft.com/en-us/library/windowsazure/ff800682.aspx) and AppDynamics Machine Agent API, we plan to build the Azure Monitoring Extension.which will allow our customers to integrate and monitor Azure metrics on AppDynamics UI.
b) References

Please refer to AppDynamics Documentation about building monitoring extensions: http://docs.appdynamics.com/display/DASH/Build+an+AppDynamics+Extension

About Azure, please refer to http://msdn.microsoft.com/en-us/library/windowsazure/dd163896.aspx
c) Technology Used

We will use the Azure as our monitoring target. To retrieve metrics and monitor the behaviour of the Azure we will use the REST API exposed by Azure.
2) Architectural design
a) Create and export management certificate to azure

For steps to create a certificate and export to Azure visit http://gauravmantri.com/2013/08/25/consuming-windows-azure-service-management-api-in-java/
 b) Configuration file

in monitor.xml  subscriptionId, keyStorePath, keyStorePassword and x-ms-version are required fields and need to be configured.

Where

SubscriptionId : Azure subscription id

keyStorePath : Path of keystore created in step a

keyStorePassword : Password for the keystore

x-ms-version : Azure REST API version (For more info refer http://msdn.microsoft.com/en-us/library/windowsazure/dn166981.aspx)

include-metrics-path : Path to metrics.properties

metrics.properties

Individual metric names to display on metric browser, currently only Subscription level metrics and Storage account names are defined here. All other metrics will be displayed automatically.

STORAGE_ACCOUNT_NAMES_FOR_TABLE: storage account name(s) separated by comma  to show table level metrics

STORAGE_ACCOUNT_NAMES_FOR_BLOB: storage account name(s) separated by comma  to show blob level metrics


c) Monitor design

We are planning to use execution style as periodic because this would invoke the monitor at a set frequency. 
3) Metrics collected
Subscription

Metric Name
	

Metric Path
	

Aggregation Rollup Type
	

Time Rollup Type

MaxCoreCount
	

Custom Metrics | Azure | Subscription | <SubscriptionID> | MaxCoreCount
	

Average
	

Average

MaxStorageAccounts
	

Custom Metrics | Azure | Subscription | <SubscriptionID> |MaxStorageAccounts
	

Average
	

Average

MaxHostedServices
	

Custom Metrics | Azure | Subscription | <SubscriptionID> |MaxHostedServices
	

Average
	

Average

CurrentCoreCount
	

Custom Metrics | Azure | Subscription | <SubscriptionID> |CurrentCoreCount
	

Average
	

Average

CurrentHostedServices
	

Custom Metrics | Azure | Subscription | <SubscriptionID> |CurrentHostedServices
	

Average
	

Average

CurrentStorageAccounts
	

Custom Metrics | Azure | Subscription | <SubscriptionID> |CurrentStorageAccounts
	

Average
	

Average

MaxVirtualNetworkSites
	

Custom Metrics | Azure | Subscription | <SubscriptionID> |MaxVirtualNetworkSites
	

Average
	

Average

MaxLocalNetworkSites
	

Custom Metrics | Azure | Subscription | <SubscriptionID> |MaxLocalNetworkSites
	

Average
	

Average

MaxDnsServers
	

Custom Metrics | Azure | Subscription | <SubscriptionID> |MaxDnsServers
	

Average
	

Average
Disks

Metric Name
	

Metric Path
	

Aggregation Rollup Type
	

Time Rollup Type

Disk Size
	

Custom Metrics | Azure | Disk |<Disk Name>|Disk Size
	

Average
	

Average
OS Image

Metric Name
	

Metric Path
	

Aggregation Rollup Type
	

Time Rollup Type

LogicalSizeInGB
	

Custom Metrics | Azure | OS Image|<Name>|LogicalSizeInGB
	

Average
	

Average
Website Management

Metric Name
	

Metric Path
	

Aggregation Rollup Type
	

Time Rollup Type

CurrentNumberOfWorkers
	

Custom Metrics | Azure | Web Space | <Web Space Name> | CurrentNumberOfWorkers
	

Average
	

Average

Enabled
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Enabled
	

Average
	

Average

CPU Time - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | CPU Time | <Unit> | CurrentValue
	

Average
	

Average
CPU Time - Limit	Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | CPU Time | <Unit> | Limit	 	 

Data In - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Data In| <Unit> | CurrentValue
	

Average
	

Average

Data In - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Data In| <Unit> | Limit
	

Average
	

Average

Data Out - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Data Out| <Unit> | CurrentValue
	

Average
	

Average

Data Out - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Data Out| <Unit> | Limit
	

Average
	

Average

Local bytes read - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Local bytes read | <Unit> | CurrentValue
	

Average
	

Average

Local bytes read - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Local bytes read | <Unit> | Limit
	

Average
	

Average

Local bytes written - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Local bytes written | <Unit> | CurrentValue
	

Average
	

Average

Local bytes written - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Local bytes written | <Unit> | Limit
	

Average
	

Average

Network bytes read - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Network bytes read | <Unit> | CurrentValue
	

Average
	

Average

Network bytes read - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Network bytes read | <Unit> | Limit
	

Average
	

Average

Network bytes written - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Network bytes written | <Unit> | CurrentValue
	

Average
	

Average

Network bytes written - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Network bytes written | <Unit> | Limit
	

Average
	

Average

WP stop requests - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | WP stop requests | <Unit> | CurrentValue
	

Average
	

Average

WP stop requests - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | WP stop requests | <Unit> | Limit
	

Average
	

Average

Memory Usage - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Memory Usage | <Unit> | CurrentValue
	

Average
	

Average

Memory Usage - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | Memory Usage | <Unit> | CurrentValue
	

Average
	

Average

CPU Time - Minute Limit - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | CPU Time - Minute Limit | <Unit> | CurrentValue
	

Average
	

Average

CPU Time - Minute Limit - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | CPU Time - Minute Limit | <Unit> | Limit
	

Average
	

Average

File System Storage - Current Value
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | File System Storage | <Unit> | CurrentValue
	

Average
	

Average

File System Storage - Limit
	

Custom Metrics | Azure | Web Space | <Web Space Name> | <Web Site Name> | Usage Metrics | File System Storage | <Unit> | Limit
	

Average
	

Average
      Database Server 

Metric Name
	

Metric Path
	

Aggregation Rollup Type
	

Time Rollup Type

MaxSizeBytes
	

 Custom Metrics|Azure|DATABASE SERVER|<DB Server Name>|DATABASE|<DB Name>|MaxSizeBytes
	

Average
	

Average
SizeBytes	Custom Metrics|Azure|DATABASE SERVER|hd57z9kvth|DATABASE|master|SizeBytes	Average	Average
    Storage
       Table

Metric Name
	

Metric Path
	

Aggregation Rollup Type
	

Time Rollup Type

No Of Tables
	Custom Metrics|Azure|Storage|Table|<Storage Account Name>|No Of Tables	

Average
	

Average
       Blob

Metric Name
	

Metric Path
	

Aggregation Rollup Type
	

Time Rollup Type

Size
	Custom Metrics|Azure|Storage|Blob|<Storage Account Name>|Container|<Container Name>|Blobs|<Blob Name>|Size	

Average
	

Average
4) Dashboards

For our dashboard, we will graph the following metrics:

MaxCoreCount, CurrentCoreCount pie chart

MaxStorageAccounts, CurrentStorageAccounts pie chart

CPU Time for a website

Data In and Data Out pie chart
5) Size and Performance

Because of the large quantity of metrics we collect, we must be careful that this does not take up more than 1% of the CPU on the machine. Also we must be sure that the memory footprint is reasonable. Finally we must be sure that we don’t overload the controller, so we must only return a few hundred metrics to leave room for other extensions, as the total limit is 5,000 per machine agent.
6) Testing
a) Software to install

No additional software required.
b) Key Testing Considerations

i. Check for valid metrics at controller.

ii. Validate website data
