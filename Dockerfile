FROM tomcat:10.1-jdk11

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy ROOT.war directly
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Copy MySQL driver into Tomcat lib
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar /usr/local/tomcat/lib/

# Copy context.xml for DB connection
COPY context.xml /usr/local/tomcat/conf/context.xml

EXPOSE 8080

CMD ["catalina.sh", "run"]
