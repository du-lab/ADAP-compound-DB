unzip adap-kdb-img.zip
docker load -i adap-kdb-img.tar
docker run -e DATABASE=cci-dulab.uncc.edu -e INTEGRATION_TEST=false -d -p8080:8080 adap-kdb-img