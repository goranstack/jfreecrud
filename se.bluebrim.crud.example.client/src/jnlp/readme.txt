The pom.xml file expects a keystore file in this folder that is not checked in to the SCM for obvious reasons.

The kestore file is created by running the following windows command in this folder:
%JAVA_HOME%\bin\keytool -genkey -alias jfreecrud -keystore jfreecrud.jks

The passwords you enter in the genkey dialog must be defined as variables ${keypass} and ${storepass} in the
settings.xml file of your Maven installation.

To delete the old certificate
%JAVA_HOME%\bin\keytool -delete -alias jfreecrud -keystore jfreecrud.jks

Run "mvn install webstart:jnlp" to create a zip file with all the signed jars.

Tip: Install the Tarlog Eclipse plugin for easy open of command windows from Eclipse. http://code.google.com/p/tarlog-plugins/