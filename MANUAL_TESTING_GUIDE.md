**Guide for testing adap-kdb on docker**

Prerequisites: have docker installed and running
1. Download artifact from pipeline once it's passed
2. In the same directory where the image is downloaded, run "unzip adap-kdb-img.zip"
3. Run "docker load -i adap-kdb-img.tar"
4. Run "docker run --name [container name] -p 8080:8080 adap-kdb-img". 
5. Open http://localhost:8080/ in the browser. 