javac -parameters -cp "D:\Server local\Tomcat 10.1\lib\servlet-api.jar";./temp/WEB-INF/lib/gson-2.10.1.jar;./temp/WEB-INF/lib/connection.jar;./temp/WEB-INF/lib/formulaire.jar -d ./Framework/ ./Framework/*.java
cd .\Framework
jar -cf framework.jar etu2070
move .\framework.jar "D:\Project\gestion-entreprise\lib"
cd D:\Project\gestion-entreprise\
.\compile.bat