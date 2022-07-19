FROM openjdk:jre-alpine
COPY entrypoint.sh /entrypoint.sh
COPY releases/sarus-2.0.2.jar /sarus.jar
LABEL version="2.0.2"
WORKDIR /data
ENTRYPOINT ["/entrypoint.sh"]
CMD ["ru.autosome.SARUS", "--help"]
# docker run --rm
#            --mount type=bind,src=/home/ilya/iogen/tools/sarus/examples/,dst=/data
#            -m 800M
#            -e JAVA_OPTIONS="-Xmx300m"
#            sarus
#            ru.autosome.SARUS
#            sequences.fa motif.pwm besthit
