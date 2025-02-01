Zookeepers:

	- zk 1
		- myid 1
		- 10.0.0.10:2181
		- Leader Election Port 2888
		- Follower-to-Leader Communication Port 3888

	- zk 2
		- myid 2
		- 10.0.0.20:2181
		- Leader Election Port 2888
		- Follower-to-Leader Communication Port 3888

	- zk 3
		- myid 3
		- 10.0.0.30:2181
		- Leader Election Port 2888
		- Follower-to-Leader Communication Port 3888

	- Configuration file:
		server.1=10.0.0.10:2888:3888
		server.2=10.0.0.20:2888:3888
		server.3=10.0.0.30:2888:3888

		- /var/lib/zookeeper/myid
			- put the id of the zookeeper

Kafka Brokers:
	
	- europe
		- broker id 1
		- 10.0.0.101:9092
		- log.dirs=/tmp/kafka-europe-1-logs

		- broker id 4
		- 10.0.0.101:9093
		- log.dirs=/tmp/kafka-europe-2-logs

	- north-america
		- broker id 2
		- 10.0.0.102:9092
		- log.dirs=/tmp/kafka-north-america-1-logs

		- broker id 5
		- 10.0.0.102:9093
		- log.dirs=/tmp/kafka-north-america-2-logs
	
	- asia
		- broker id 3
		- 10.0.0.103:9092
		- log.dirs=/tmp/kafka-asia-1-logs

		- broker id 6
		- 10.0.0.103:9093
		- log.dirs=/tmp/kafka-asia-2-logs

Topics:
	- <continent>-temperature
	- <continent>-humidity
	- <continent>-airQuality

	- europe:
		- replication factor: 3
		- replicated on brokers:
			- broker id 1 : europe's first broker
			- broker id 4 : europe's second broker
			- broker id 6 : asia's second broker 
				
	- north-america:
		- replication factor: 3
		- replicated on brokers:
			- broker id 2 : north-america's first broker
			- broker id 5 : north-america's second broker
			- broker id 4 : europe's second broker 

	- asia:
		- replication factor: 3
		- replicated on brokers:
			- broker id 3 : asia's first broker
			- broker id 6 : asia's second broker
			- broker id 5 : north-america's second broker 







Starting commands:

bin/zookeeper-server-start.sh -daemon config/zookeeper.properties 

bin/kafka-server-start.sh -daemon config/server-europe-1.properties
bin/kafka-server-start.sh -daemon config/server-europe-2.properties

bin/kafka-server-start.sh -daemon config/server-north-america-1.properties 
bin/kafka-server-start.sh -daemon config/server-north-america-2.properties 

bin/kafka-server-start.sh -daemon config/server-asia-1.properties 
bin/kafka-server-start.sh -daemon config/server-asia-2.properties


Topic creation:

bin/kafka-topics.sh --create --topic europe-temperature --replica-assignment 1:4:6 --bootstrap-server 10.0.0.101:9092

bin/kafka-topics.sh --create --topic north-america-airQuality --replica-assignment 2:5:4 --bootstrap-server 10.0.0.102:9092

bin/kafka-topics.sh --create --topic asia-humidity --replica-assignment 3:6:5 --bootstrap-server 10.0.0.103:9092
