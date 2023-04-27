**Guide for testing adap-kdb on docker**

Prerequisites: have docker installed. 
1. Download artifact from pipeline once it's passed
2. In the same directory where the image is downloaded, run "tar -xvf adap-kdb-img.zip [image_name_of_your_choice].tar"
3. In the same directory, run "docker  run --name [container name] -p 8080:8080 adap-kdb-img". 
4. Open http://localhost:8080/ in the browser. 