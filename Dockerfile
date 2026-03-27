FROM tomcat:9.0

COPY *ROOT.war /usr/local/tomcat/webapps/app.war

EXPOSE 8080