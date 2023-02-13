FROM lucee/lucee5

MAINTAINER Daryl Banttari <dbanttari@gmail.com>

ADD seefusion.xml /usr/local/tomcat/lib
ADD www/* /var/www/
ADD seefusion5.jar /usr/local/tomcat/lib
RUN java -jar /usr/local/tomcat/lib/seefusion5.jar --install tomcat /usr/local/tomcat/conf
