# Hazelcast Sample with Spring Boot

To run:

    java -Dspring.profiles.active=dev -Dserver.port=9999 -jar target/hazelcast-sample.jar
    
Change the port number if you plan to run more than one instance in the same machine.

After the application is running, call the URL:

    curl "http://localhost:9999/hello?name=Batman"

Try with different machines and ports. Each different name will be added to the 
shared set. 