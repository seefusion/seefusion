FROM lucee/lucee5

ADD https://repo1.maven.org/maven2/net/sourceforge/jtds/jtds/1.3.1/jtds-1.3.1.jar /usr/local/tomcat/lib
ADD https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc10/19.15.0.0.1/ojdbc10-19.15.0.0.1.jar /usr/local/tomcat/lib
ADD seefusion.xml /usr/local/tomcat/lib
ADD www/* /var/www/
ADD seefusion5.jar /usr/local/tomcat/lib
RUN java -jar /usr/local/tomcat/lib/seefusion5.jar --install tomcat /usr/local/tomcat/conf
