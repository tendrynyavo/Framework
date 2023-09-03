javac -parameters -cp "C:\Program Files\Apache Software Foundation\Tomcat 9.0\lib\servlet-api.jar";./temp/WEB-INF/lib/gson-2.10.1.jar;./temp/WEB-INF/lib/connection.jar;./temp/WEB-INF/lib/formulaire.jar -d ./Framework/ ./Framework/*.java
cd .\Framework
jar -cf framework.jar etu2070
