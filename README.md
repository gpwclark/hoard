# hoard
-	an ndn-based intermediary caching and pushing node

## intent
-	Hoard is an ndn-based intermediate caching and pushing mechanism. Although
NFD provides the ability to cache data, there is no guarantee that data will 
be in the cache.  Furthermore, NFD only caches data that it forwards. This 
means that any given NFD may or may not have certain data.

-	Enter hoard. Hoard can be seen as serving two purposes: caching and pushing.
1. Caching
	-	Hoard is designed to be fed prefixes. Currently it supports DSync
	broadcast prefixes and "flat" data prefixes. Given a prefix hoard will 
	fetch all the data being made on that prefix, and register prefixes so it
	can serve that data to interested clients. This is especially important
	given any producer on a network can go offline and stop serving the data
	they generated.
	-	DSync broadcast prefixes
		-	Hoard understands the DSync protocol. This means, given the 
		broadcast prefix for a given instance of DSync, hoard will cache and 
		subsequently serve all the data generated in the given instance.
	-	Flat data prefixes
		-	Hoard can serve as a middleman for any normal prefix. Let's say 
		a file is being shared on the prefix "/ndn/data/document/1/". If hoard
		is informed of the existence of the prefix, it can listen for interests,
		e.g. "/ndn/data/document/1/s01, /ndn/data/document/1/s02, etc.", and 
		cache the corresponding data so as to serve as a producer of that data 
		in the future.
2. Pushing - Under construction
	-	Hoard is designed to be federated. This is important because it allows
	data that would not otherwise be proliferated across a network to be cached
	anywhere an instance of hoard is running. This implies that across a
	network, hoard is constantly striving for consistency (at any given node
	trading partial consistency in favor of constant availability). The 
	advantages to the underlying clients hoard is serving are readily apparent
	in the case of network partitions. Given the following topology:
		```
		├── node_1 (nfd_1,hoard_1)
		│   ├── A
		│   └── B
		└── node_2 (nfd_2,hoard_2)
			└── C
```
			* where node_1 and node_2 are connected intermediate nodes each 
			running instances of NFD and hoard.
			* where A, and B are data producers connected to node 1, and C 
			is a data producer connected to node 2.
	-	Let's say that A and B are communicating over a variety of prefixes
	known to hoard, and C is currently interested in none of that data. The
	instance of hoard on node 1 will cache all the data being created by A and 
	B and push that data to the instance of hoard on node 2. Both instances will
	become producers of all data they cache. If C were to decide to become 
	interested in any of the data A and B generated it would be able to find
	that data in the nfd_2 cache or hoard_2's cache if for some reason it was 
	not in node 2's nfd. In the case that the link between node_1 and node_2
	were to be severed C would still be able to observe some of the data that
	A and B produced. If the link between node_1 and node_2 was severed after
	A and B stopped producing and after hoard_1 and hoard_2 achieved full
	consistency, C would be able to discover all of the data produced on node_1
	despite the network partition.
	-	Hoard's pushing mechanism is useful even if no network partition is
	observed. Let's say that there are N nexthops between node_1 and node_2.
	Due to hoard's effort to be fully consistent, if C were to become interested
	in A and B's data while the long link between node_1 and node_2 was still up,
	C would receive the data in a more timely manner because hoard_1 will have 
	pushed the data to hoard_2.

