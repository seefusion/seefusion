#!/bin/bash
gradle clean compileJava
mkdir -p target/seefusion-aws-java-sdk
cd target
wget -q -N https://s3.amazonaws.com/seefusion-build/aws-java-sdk.zip
unzip -ou aws-java-sdk.zip -x "*documentation/*" "*samples/*"
cd seefusion-aws-java-sdk
unzip -ou ../aws-java-sdk-1.11.19/lib/aws-java-sdk-1.11.19.jar `unzip -l ../aws-java-sdk-1.11.19/lib/aws-java-sdk-1.11.19.jar | grep -of ../../awspackages.txt`
unzip -ou ../aws-java-sdk-1.11.19/third-party/lib/commons-logging-*.jar
unzip -ou ../aws-java-sdk-1.11.19/third-party/lib/httpclient-*.jar
unzip -ou ../aws-java-sdk-1.11.19/third-party/lib/httpcore-*.jar
unzip -ou ../aws-java-sdk-1.11.19/third-party/lib/jackson-annotations-*.jar
unzip -ou ../aws-java-sdk-1.11.19/third-party/lib/jackson-core-*.jar
unzip -ou ../aws-java-sdk-1.11.19/third-party/lib/jackson-databind-*.jar
unzip -ou ../aws-java-sdk-1.11.19/third-party/lib/jackson-dataformat-cbor-*.jar
unzip -ou ../aws-java-sdk-1.11.19/third-party/lib/joda-time-*.jar
rm -rf META-INF
cp -r ../classes/* .
zip -r ../seefusion-aws-java-sdk.zip *
cd ..
cd ..