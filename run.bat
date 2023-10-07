javac -source 8 -target 8 -parameters -cp "D:\Server Local\Tomcat 10.1\lib\servlet-api.jar";./temp/WEB-INF/lib/gson-2.10.1.jar;./temp/WEB-INF/lib/connection.jar;./temp/WEB-INF/lib/formulaire.jar -d ./Framework/ ./Framework/*.java
cd .\Framework
jar -cf framework.jar etu2070
