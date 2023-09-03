#!/bin/bash

# All Path
FRAMEWORK=/home/tendry/Framework
TEMP=temp
PROJECT=test-framework

if [ ! -d "$FRAMEWORK/$TEMP" ]; then
    echo "Create new temp directory"
    mkdir $FRAMEWORK/$TEMP
    cd $FRAMEWORK/$TEMP
    mkdir WEB-INF
    cd WEB-INF
    mkdir classes
    mkdir lib
fi

# Create jar for framework and move this in temp
javac -parameters -cp /home/tendry/apache-tomcat-10.1.7/lib/servlet-api.jar:$FRAMEWORK/$TEMP/WEB-INF/lib/gson-2.10.1.jar:$FRAMEWORK/$TEMP/WEB-INF/lib/connection.jar:$FRAMEWORK/$TEMP/WEB-INF/lib/formulaire.jar -d $FRAMEWORK/Framework $FRAMEWORK/Framework/*.java
cd $FRAMEWORK/Framework
jar -cf framework.jar etu2070
mv ./framework.jar $FRAMEWORK/$TEMP/WEB-INF/lib

# Copy all jsp file in temp directory
cd $FRAMEWORK/test-framework/
cp *.jsp $FRAMEWORK/$TEMP

# Compile all java file and move this in temp directory
cd $FRAMEWORK/test-framework/
javac -parameters -cp $FRAMEWORK/$TEMP/WEB-INF/lib/framework.jar:$FRAMEWORK/$TEMP/WEB-INF/lib/connection.jar:$FRAMEWORK/$TEMP/WEB-INF/lib/formulaire.jar  -d $FRAMEWORK/$TEMP/WEB-INF/classes *.java
cd $FRAMEWORK/$TEMP

# Copy the web.xml file in temp directory
cp $FRAMEWORK/test-framework/web.xml $FRAMEWORK/$TEMP/WEB-INF


# Create an war file in this project
jar -cf $PROJECT.war .

# Deploye the project in tomcat
# mv $PROJECT.war /home/tendry/apache-tomcat-10.1.7/webapps/

# Restart tomcat
# /home/tendry/apache-tomcat-10.1.7/bin/shutdown.sh
# /home/tendry/apache-tomcat-10.1.7/bin/startup.sh