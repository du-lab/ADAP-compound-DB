**Guide for testing adap-kdb on docker**

Prerequisites: have docker and mvn installed. 
1. From project root, run mvn clean install.
2. (Optional) Check if a war file is generated in target folder. If it is, move on to next step, otherwise check step 1.
3. Run "docker image build -t [image name] ."
4. Run "docker  run --name [container name] -p 8080:8080  [image name]". This will create a container from the image in step 3.
5. Open http://localhost:8080/adap-compound-db in the browser. 