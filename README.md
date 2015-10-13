# Project-IceCream

Sample client-server project that demonstrates the use of the Payfirma API

For quick setup use the following two files
- Server: PayfirmaSalesServlet.class
- Client: app-release.apk

==========================================
Instructions for running the Wec Container
==========================================

Requirements:
- Apache tomcat (or other web container)
- servlet-api.jar (available in $CATALINA_BASE/lib)

1. Install Tomcat  ** NOTE: For test purposes - only http is available in app **
2. [Only required if you need to compile the source code.]
     Compile PayfirmaSalesServlet.java
	   $terminal>	javac src/com/tasty/icecream/PayfirmaSalesServlet.java -classpath "lib/servlet-api.jar" -d classes
3. Copy PayfirmaSalesServlet.class to $CATALINA_BASE/webapps/icecream-server/WEB-INF/classes/com/tasty/icecream/
3. Run $CATALINA_BASE/bin/startup.sh
4. Navigate to https://localhost:8080/icecream-server/Sales
5. There should be a simple notification: "Server is running"
6. Take note of the server ip address and port number (will be required by client)

======================================
Instructions for running android app
======================================

1. Download apk to android device
2. Accept the permissions
3. After the splash screen, a dialog will pop up.
     - Enter server ip address (e.g. 192.168.1.10)
     - Enter port server is running (e.g. 8080)
     - Dialog appears only once, unless reinstalled or app data is cleared
     - ** If a mistake is made or fields are left empty; will not be able to connect to Tomcat.
4. Select flavor/style and checkout
5. Enter card number(4111 1111 1111 1111), card month(12), card year(34) and CVV2(123) and select Pay Now.
6. If purchase amount is odd, will return the Success screen.
	 If purchase amount is even, will return the Declined screen.
7. Go buy yourself and ice-cream - you deserve it if you made it this far ;)
