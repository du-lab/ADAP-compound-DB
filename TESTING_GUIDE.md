**Guide for testing adap-kdb on docker**

Prerequisites: have docker installed. 
1. Download artifact from pipeline once it's passed
2. In the same directory, run "docker  run --name [container name] -p 8080:8080 adap-kdb-img". 
3. Open http://localhost:8080/adap-compound-db in the browser. 