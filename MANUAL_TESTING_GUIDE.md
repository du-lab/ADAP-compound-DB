**Guide for testing adap-kdb running on docker**

Prerequisites: have docker installed and running
1. Download artifact from the gitlab pipeline once it's passed
2. Extract the file. 
3. In the extracted folder, run "chmod u+x test_script.sh" to give permission to the file
4. Run ./test_script.sh
5. Open http://localhost:8080/ in the browser. 