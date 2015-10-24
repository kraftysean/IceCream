# IceCream
Sample client-server project that uses the Strip API for payment processing

========================================
Instructions for running the Java server
========================================

1. Install Tomcat
2. Compile IcecreamServlet.java

    javac src/com/tasty/IcecreamServlet.java -classpath "lib/servlet-api.jar:lib/org.json-chargebee-1.0.jar:lib/stripe-java-1.37.1.jar" -d classes

3. Copy class file to $CATALINA_BASE/webapps/{PROJECT_NAME}/WEB-INF/classes/com/tasty/
4. Make sure required jars are in $CATALINA_BASE/webapps/{PROJECT_NAME}/WEB-INF/lib/
5. Run $CATALINA_BASE/bin/startup.sh
6. Navigate to http://localhost:8080/PROJECT_NAME/Sales.  If server is running a message will be displayed.


Required JARs:
- org.json-chargebee-1.0.jar
- servlet-api.jar
- stripe-java-1.37.1.jar

NOTE: If trying to run client from emulator, localhost's IP address should be set to 10.0.2.2
~                                                                                                                                                    
"ReadMe.MD" 19L, 741C                                                                                                              1,1           All

