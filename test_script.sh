unzip adap-kdb-img.zip
docker load -i adap-kdb-img.tar
docker run --name adap-kdb-container -p8080:8080 adap-kdb-img