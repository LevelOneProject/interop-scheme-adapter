FROM anapsix/alpine-java:8_server-jre_unlimited

ENV MULE_VERSION=3.9.0 MULE_HOME=/opt/mule JAVA_VERSION=8 MAX_JVM_MEMORY=2048 MULE_ENV=`hostname`-scheme-adapter MULE_LIB=/opt/mule/lib HTTP_CONN_PORT_1=8088 HTTP_CONN_PORT_2=8089 HTTP_CONN_PORT_3=9081 HTTP_CONN_PORT_4=9082
ENV SCHEME_ADAPTER_VERSION=1.0.12 INTEROP_DOMAIN_VERSION=0.1.38
ENV MULE_USER=level1project MULE_PASS=level1project

# SSL Cert for downloading mule zip
RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add ca-certificates && \
    update-ca-certificates && \
    apk --no-cache add openssl
    
RUN if [ "${MULE_VERSION}" != "3.9.0" ] && \
    [ "${MULE_VERSION}" != "3.8.1" ] ; then echo "-----   Unsupported version: ${MULE_VERSION}   -----" && \
    return 1; fi

RUN if [ "${MULE_VERSION}" = "3.9.0" ]; then cd ~ && \
    wget https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/${MULE_VERSION}/mule-standalone-${MULE_VERSION}.tar.gz && \
    echo "39b773bf20702f614faf30b2ffca4716  mule-standalone-${MULE_VERSION}.tar.gz" | md5sum -c ; fi
RUN if [ "${MULE_VERSION}" = "3.8.1" ]; then cd ~ && \
    wget https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/${MULE_VERSION}/mule-standalone-${MULE_VERSION}.tar.gz && \
    echo "db079c0fc01c534d443277cfe96ab252  mule-standalone-${MULE_VERSION}.tar.gz" | md5sum -c ; fi

RUN cd /opt && \
    tar xvzf ~/mule-standalone-${MULE_VERSION}.tar.gz && \
    rm ~/mule-standalone-${MULE_VERSION}.tar.gz && \
    ln -s /opt/mule-standalone-${MULE_VERSION} /opt/mule

RUN cd /opt/mule && \
    wget https://${MULE_USER}:${MULE_PASS}@modusbox.jfrog.io/modusbox/libs-release/com/l1p/interop/interop-scheme-adapter/${SCHEME_ADAPTER_VERSION}/interop-scheme-adapter-${SCHEME_ADAPTER_VERSION}.zip -O apps/interop-scheme-adapter-${SCHEME_ADAPTER_VERSION}.zip && \
    wget https://${MULE_USER}:${MULE_PASS}@modusbox.jfrog.io/modusbox/libs-release/com/l1p/interop/interop-domain/${INTEROP_DOMAIN_VERSION}/interop-domain-${INTEROP_DOMAIN_VERSION}.zip -O domains/interop-domain.zip

COPY mule_artifacts/wrapper.conf /opt/mule/conf/wrapper.conf
COPY mule_artifacts/mule-l1p.properties /opt/mule/conf/mule-l1p.properties

# Define mount points.
VOLUME ["/opt/mule/logs", "/opt/mule/conf", "/opt/mule/apps", "/opt/mule/domains"]

# Define working directory.
WORKDIR /opt/mule

CMD [ "/opt/mule/bin/mule" ]

# Default http ports
EXPOSE ${HTTP_CONN_PORT_1} ${HTTP_CONN_PORT_2} ${HTTP_CONN_PORT_3} ${HTTP_CONN_PORT_4}
