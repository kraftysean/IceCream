Instructions for running the Java server

1. Install Tomcat
2. Compile PayfirmaSalesServlet.java

	javac src/com/tasty/icecream/PayfirmaSalesServlet.java -classpath "lib/servlet-api.jar" -d classes

3. Copy class file to $CATALINA_BASE/webapps/{PROJECT_NAME}/WEB-INF/classes/com/tasty/icecream/
3. Run $CATALINA_BASE/bin/startup.sh
4. Navigate to https://localhost:8080443/PROJECT_NAME/Sales
5. There should be a simple notification indicating the server is running


Required JARs:
- json-simple-1.1.1.jar
- servlet-api.jar
		

