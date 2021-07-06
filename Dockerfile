FROM java:8
# Add the service itself
ARG JAR_ARTIFACT
ADD target/${JAR_ARTIFACT} /users-be.jar
#Execute
CMD java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar users-be.jar
