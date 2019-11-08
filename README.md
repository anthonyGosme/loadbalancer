# loadbalancer project
part 1a implementaion of a proxy
===========

1 Implementation
-----
	I chose to implement the proxy with a low level of abstraction of HTPP/1.1 to be the most flexibilitty when dealing with protocle.
	Todo soo I use the Java 11 sun httpsever for inbound and Apache http client for outbound traffic.
	I've implement GET, POST, PUT and DELELE messages types
	The headers are resend as it to the downstreams servers.

2 Code quality
------------
	I've validate the code quality with :
	- sonar
	- codesmell
	- code coverage 

3 unitary Tests 
-------------
	- 10 unitary test with Junit 
	- I've code server mock server for unit test 


4 launch
-----------
	mvn clean test

part 1b implementaion of a downstream server
------------

	I've implement downstream mock server in nodejs
	It send back the body, the headers received, a timestamp key

part 2 automation & integrationn
===========

1 Implementation
---------
	The two server setup are dockerized (see the dockerFiles) then setup via helm
	when runnig helm "install lbproxy" the proxy configuration file is autamtly send to the proxy . 

2 launch the mock
------------
	The first server to launch is de downstreams server mock (lbserverdown) :
		./lbserverdown/helm lbserverdown lbserverdown
	configure the 3 IP
		retr
		modify these ip in ./lbproxy/
	configure the hostname
		in /etc/host (linux) or (windows) add the hostname
	navigate the lbserverdown server
		open http://
		refresh the page to see the time stamp change

3 Launch the proxy
-----------
	modify the ./helm/lbproxy/proxy.yaml accordind to your needs
	launch ./helm/


4 Integratin Test
-----------
	- 1 Jmeter file with 1 performance test
	- 1 Jmeter file with 9 integrations test 
	start the test using a jmeter installation

6 - why this solution ?
---------
	docker, helm and kubernetes are states of art solution to create, package and ship cloud aplication based on docker image, and all in a very replicable way.
	Docker build image no dependencies of the hosts and automate the setup of the include service.
	kubernetes build resiliant services and replicable configuration arround docker images
	helm package safe realease without harcoded information and manual stept and assure the deployment lifecycle
	I do Jmeter integrations to test all 'the case in the target envirronement
	
	

part 3  monitoring the service & SLI
=========

	
===== part 3  monitoring the service & SLI / not finished =========
	'
I've not finish it yet. 
A good target to have resiliant and well monitored service is

1 - (done) - do a test performace shoot  
--------
	to stress the proxy + get the max TPS supported + get cpu & memory metric 
	I've done a jmeter test.
	in my laptop with 100 Virtual users for 10minutes:
		- proxy add an 2ms lattency   
		- max performance are 1100 TPS 
		- memory cunsumption stay lower than 40MB
		- error rate is 0%
	see images for more informations

2 - (to do) - define and test an operational range (SLO) (to do)
----------
use the same jmeter with different parameters
I don't run the stress, charge and deconnections

3 - (started) - use elastic search to agregate into data issue by the proxy
--------------
 - create an ELK node
 - post the data from the proxy to elasticsearch 
 	- URI
 	- response time
 	- succes/status status
 	- htpp code  
 	- response size
 	- server downstream connection time

4 - (started) - get health status of the proxy via : 
---------
	Hearthbeat :
	 - liveness
	 - proxy response time
	metric beat + jolokia : 
	- %CPU
	- %memory
	- bandwith
	- memory usage 
	- http pool

5 - (to do) monitoring and alert via kibana (SLI)
-----------
 - create view and dashboard of the metrrics
 - create alert base on SIA & 
 - Monitor health and metric with kibana views and dashboarbs
create Alert 

6 - why this solution ?
-----
	I setup and work with differents SIEM, SLI and  APM platforms, 
	and for devops task ELK (Open Distro) is my preffered  choice :
		- It's very versitille and cover a variaty of need : SIEM, SIA, APM, custom monotoring and dashboard
		- It's flexible and can't conect to many services and can't be setup for every specials needs
		- It's seems to be here to stay a while 
		- It's free and open source and always ready to setup
	Other good alternatives are splunk and dynatrace

