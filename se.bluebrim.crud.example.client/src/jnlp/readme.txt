The pom.xml file expects a keystore file in this folder that is not checked in to the SCM for obvious reasons.

The kestore file is created by running the following windows command in this folder:
"%JAVA_HOME%"\bin\keytool -genkey -alias crud-example-client -keystore crud-example-client.jks

The passwords you enter in the genkey dialog must be defined as variables ${keypass} and ${storepass} in the
settings.xml file of your Maven installation.

Run "mvn install webstart:jnlp" to create a zip file with all the signed jars.

To delete the old certificate
"%JAVA_HOME%"\bin\keytool -delete -alias crud-example-client -keystore crud-example-client.jks