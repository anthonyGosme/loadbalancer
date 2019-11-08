*load balancer project*

1.A - part 1 - Implementation of a proxy
===
1.A.1 - Implementation
---
the server is written in java 11

I've implemented GET, POST, PUT and DELETE messages types.

The headers are resent as it to the downstream servers.

The proxy is automatically configured with the proxy.yaml file found in the /helm/lbproxy directory.

1.A.2 - Code quality
------
I've validated the code quality with :

- sonar
- codesmell
- code coverage

1.A.3 - Unitary Tests
-------
- I've done 10 unitary test with Junit
- I've code embedded server mock server deployed and called by the Junit test

1.A.4 - Local test launches
-----
install and unitary test:

*cd /loadbalancer/lbproxy*

*mvn clean install test*

run the proxy from the local environment

*cd /loadbalancer/lbproxy*

*java -jar ./target/load-balancer-1.0-SNAPSHOT-jar-with-dependencies.jar*

check if the proxy call http://httpstat.us/404 (the resolution is in conf.d/proxy.yaml)*

*curl http://127.0.0.1:8080/404

1.A.5 - Why this solution?
--------
I chose to implement the proxy with a low level HTPP/1.1 java frameworK.

At this level of abstraction easy to deal with the protocol.

I use the Java 11 sun Http server for inbound and Apache Http client for outbound traffic requests.

1.B - Implementation of a downstream server (mock)
=======
1.B.1 - Implementation
-------
I've implemented a downstream mock server in Node.js

It sends back the body, the headers received, a timestamp key

1.B.2 Code quality & test
-------
I use JSlint to check the code.

no unitary test, but the mock it used in integration test.

1.B.4 - Why this solution?
------
A serve downstream mock allows validating easily the proxy in a real environment.

I choose Node.js because is fast to implements and package a server mock with it.

2 - Automation & integration
=======
2.A - Implementation
-------
The two servers are dockerized, then the setup is done via Kubernetes and the packaging with helm.

When running helm "install lbproxy" the proxy configuration file is automatically sent to the proxy server

2.B - part 2 - Launch the mock
--------
The first server to launch is the downstream server mock (lbserverdown) :

*cd helm*

*helm install lbproxy lbproxy*

retrieve the internal IP ... wait 1 minute to IP to be set

*kubectl get pods -o wide | grep lbserverdown*

Set up the proxy these 3 IP in the proxy under my-service3 service

*vi /helm/lbproxy/proxy.yaml*

check the access of the service

(for test purpose the service expose the IP via minikube, and should be disabled for production)

*minikube service lbserverdown*

2.C - Launch the proxy
-------
check that /helm/lbproxy/proxy.yaml with the service IP (done in 2.B)

*install the proxy*

*cd helm*

*helm install lbproxy lbproxy*

open the service in a browser

*minikube service lbproxy*

add the service IP found in hostname resolution Linux: /etc/hosts Windows: C:\Windows\System32\drivers\etc

*172.17.169.222 my-service.my-company.com my-service2.my-company.com my-service3.my-company.com unknown-service.my-company.com curl http://my-service3.my-company.com:30080/test*

2.D - Run the Integration Test
-------
the solution includes integration test with JMeter

- 1 Jmeter file with 1 performance test
- 1 Jmeter file with 9 integrations test Installs the last version of JMeter.

open the test in /intg_test and run them to check multiple scenarios

2.E - Why this solution?
-------
docker, helm, and Kubernetes are states of the art solution to create, package and ship cloud applications based on docker image, and all in a very replicable way.

Docker builds images with no dependencies of the hosts and automates the setup of the included service.

Kubernetes build resilient services and replicable configuration around docker images.

helm package safe release without hardcoded information and manual steps and assure the deployment lifecycle

I do Jmeter integrations to test all 'the case in the target environment

3 - part 3 -  Monitoring the service & SLI / not finished
======
The target is to have a resilient and well-monitored service

3. A - (done) - do a test performance shoot
-------
to stress the proxy, get the max TPS supported and metric about the system I've done a JMeter perf test. in my laptop with 100 Virtual users for 10 minutes:

- proxy add a 2ms latency
- max performance is 1100 TPS
- memory consumption stays lower than 90MBi
- the error rate is 0%
- CPU is 60%
- see images for more information

3.B - (started) - Use elastic search to aggregate into data issue by the proxy
--------
create an ELK node

post the data from the proxy to elastic search

- URI
- response time
- proxy success/error status
- HTTP code of downstream server
- response size
- server downstream connection time

3.C - (started) - Get health status of the proxy via :
----
Heartbeat :
- liveness
- proxy response time

metric beat + Jolokia : 
- %CPU
- %memory
- bandwith
- memory usage 
- HTTP pool

3.D - (to do) - Define and test an operational range / SLO
------
use the JMeter performance test with different parameters

run the stress, charge and disconnections test 

observe how the metric change 

finally, define SLO for the different metric based on the results

3.E - (to do) Monitoring and alert via Kibana (SLI)
--------
Monitor SLI health and other interesting metrics with Kibana views and dashboards.

implement the SLI based on metrics with sufficient margins :

<=60% SLO green

=8O% for 5 minutes: orange, send an alert to people in charge od SLI

=100% for 5 minutes: red sends an alert to people in charge of SLO and SLI

create views and dashboards of these metric metrics

create alert base on SLI

3.F - Why this solution ?
-----
I set up and work with different SIEM, SLI and APM platforms.

For  DevOps task, ELK-B (Open Distro) is my personal choice :

- It's very versatile and covers a variety of need: SIEM, SIA, APM, custom monitoring and dashboard

- It's flexible and can't connect to many services and can't be set up for every special need

- It seems to be here to stay a while

- It's free and open-source and always ready to setup

- two other good alternatives to consider: Splunk and Dynatrace
