# loadbalancer project



1.A - part 1 implementaion of a proxy
===========

1.A.1 - Implementation
-----
the server is writen in java 11

I've implemented GET, POST, PUT and DELELE messages types.

The headers are resend as it to the downstreams servers.

The proxy is automatly configured with the proxy.yaml file found in the /helm/lbproxy directory.

1.A.2 - Code quality
------------
I've validate the code quality with :
- sonar
- codesmell
- code coverage 


1.A.3 - unitary Tests 
-------------
- i've done 10 unitary test with Junit 
- I've code embeded server mock server deployed and called by the Junit test


1.A.4 - local test launchs
-----------
install and unitary test test :

cd /loadbalancer/lbproxy

*mvn clean install test*

run the proxy from the local envirronement

*cd /loadbalancer/lbproxy*

*java -jar ./target/load-balancer-1.0-SNAPSHOT-jar-with-dependencies.jar*

check if the proxy call http://httpstat.us/404  (the resolution is in conf.d/proxy.yaml)

*curl http://127.0.0.1:8080/404*


1.A.5 - why this solution ?
---------
I chose to implement the proxy with a low levels HTPP/1.1 java frameworK.

At this level of abstraction easy to deal with the protocle.

I use the Java 11 sun httpsever for inbound and Apache http client for outbound traffic request.

1.B implementaion of a downstream server (mock)
======
1.B.1 implementaion
-----

I've implement a downstream mock server in Node.js

It send back the body, the headers received, a timestamp key

1.B.2 code quality & test
------
I use JSlint to check the code.

no unitary test, but the mock it used in integration test.


1.B.4 - why this solution ?
-------
A servedownstram mock allow to validate easyly the proxy in real envirronement.

I choose Node.js beacaue is fast to implements and package a server mock with it. 

2 - Automation & integrationn
===========

2.A - Implementation
---------
The two server are dockerized, then the setup is done via kubernetes and the packaging with helm.

When runnig helm "install lbproxy" the proxy configuration file is automatically send to the proxy server 

2.B - launch the mock
------------
The first server to launch is the downstream server mock (lbserverdown) :

*cd helm*

*helm install lbproxy lbproxy*

retrieve the internal IP ... wait 1 minute to IP to be set

*kubectl get pods -o wide | grep lbserverdown*

Set up the proxy these 3 IP in the proxy under my-service3 service 

*vi /helm/lbproxy/proxy.yaml*

check the acces of the service

(for test purpose the service expose the ip via minikube, and should be disabled for production)

*minikube service lbserverdown*

2.C - Launch the proxy
-----------
check that /helm/lbproxy/proxy.yaml with the service IP (done in 2.B)

install the proxy

*cd helm*

*helm install lbproxy lbproxy*

open the service in a browser

*minikube service lbproxy*

add the service ip found in hostname resolution linux: /etc/hosts windows: C:\Windows\System32\drivers\etc

*172.17.169.222 aa my-service.my-company.com my-service2.my-company.com my-service3.my-company.com unknown-service.my-company.com
curl http://my-service3.my-company.com:30080/test*

2.D - run the Integratin Test
-----------
the solution include integration test with jmeter
- 1 Jmeter file with 1 performance test
- 1 Jmeter file with 9 integrations test 
Install the last version of jmeter.

open the test in /intg*test and run them to check multtiple scenariots 

2.E - why this solution ?
---------
docker, helm and kubernetes are states of art solution to create, package and ship cloud aplication based on docker image, and all in a very replicable way.

Docker build image with no dependencies of the hosts and automate the setup of the include service.

kubernetes build resiliant services and replicable configuration arround docker images.

helm package safe realease without harcoded information and manual stept and assure the deployment lifecycle

I do Jmeter integrations to test all 'the case in the target envirronement
	
	

3 - monitoring the service & SLI / not finished
======
The target is to have a resiliant and well monitored service

3.A - (done) - do a test performace shoot  
--------
to stress the proxy , get the max TPS supported and metric about the system
I've done a jmeter perf test.
in my laptop with 100 Virtual users for 10minutes:
- proxy add an 2ms lattency   
- max performance are 1100 TPS 
- memory cunsumption stay lower than 90MBi
- error rate is 0%
- CPU is 60%
see images for more informations



3.B - (started) - use elastic search to agregate into data issue by the proxy
--------------
 - create an ELK node
 - post the data from the proxy to elasticsearch 
 	- URI
 	- response time
 	- succes/status status
 	- htpp code  
 	- response size
 	- server downstream connection time

4.C - (started) - get health status of the proxy via : 
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
	
4.D - (to do) - define and test an operational range / SLO
----------
use the jmeter performance test with different parameters
run the stress, charge and deconnections test and observe how the metric cahnge
Define SLO for the different metric based on result



5 - (to do) monitoring and alert via kibana (SLI)
-----------
Monitor SLI health and other interrestingd metrics with kibana views and dashboarbs.

implement the SLI based on metric with suffisant marging :

<=60% SLO green

>=8O% for 5 minutes : orange, send an alert to pepole in charge od SLI

>=100% for 5 minutes : red send an alert to pepole in charge of SLO and SLI

- create views and dashboards of these metric metrrics

- create alert base on SLI 


6 - why this solution ?
-----
I setup and work with differents SIEM, SLI and  APM platforms,and for devops task ELK (Open Distro) is my personnal choice :
- It's very versitille and cover a variaty of need : SIEM, SIA, APM, custom monotoring and dashboard
- It's flexible and can't conect to many services and can't be setup for every specials needs
- It's seems to be here to stay a while 
- It's free and open source and always ready to setup

two other good alternatives to consider : splunk and dynatrace

