wget --output-document=.\src\test\resources\lucee-5.1.0-SNAPSHOT.jar http://snapshot.lucee.org/rest/update/provider/download/5.1.0.17-SNAPSHOT
mvn install:install-file -DgroupId=org.lucee -DartifactId=lucee-core -Dversion=5.1.0-SNAPSHOT -Dpackaging=jar -Dfile=.\src\test\resources\lucee-5.1.0-SNAPSHOT.jar -DgeneratePom=true
