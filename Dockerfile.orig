FROM java:openjdk-8-jdk

ENV MULE_VERSION=3.8.1 HTTP_CONN_PORT_1=8088 INTEROP_DOMAIN_VERSION=0.1.31
    
RUN if [ "${MULE_VERSION}" != "3.8.1" ] && [ "${MULE_VERSION}" != "3.8.0" ]; then echo "-----   Unsupported version: ${MULE_VERSION}   -----" && return 1; fi

RUN if [ "${MULE_VERSION}" = "3.8.1" ]; then cd ~ && wget https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/${MULE_VERSION}/mule-standalone-${MULE_VERSION}.tar.gz && echo "db079c0fc01c534d443277cfe96ab252 mule-standalone-${MULE_VERSION}.tar.gz" | md5sum -c ; fi

RUN cd /opt && tar xvzf ~/mule-standalone-${MULE_VERSION}.tar.gz && rm ~/mule-standalone-${MULE_VERSION}.tar.gz && ln -s /opt/mule-standalone-${MULE_VERSION} /opt/mule

RUN cd /opt/mule && wget https://modusbox.jfrog.io/modusbox/libs-release/com/l1p/interop/interop-domain/${INTEROP_DOMAIN_VERSION}/interop-domain-${INTEROP_DOMAIN_VERSION}.zip --output-document=domains/interop-domain.zip --http-user=docker-user --password=BMGFGates2016

ENV MULE_HOME /opt/mule

COPY mule_artifacts/wrapper.conf /opt/mule/conf/wrapper.conf
COPY mule_artifacts/mule-l1p.properties /opt/mule/conf
COPY mule_artifacts/dfsp-quote.zip /opt/mule
COPY target/interop-scheme-adapter*.zip /opt/mule/apps

# Define mount points.
VOLUME ["/opt/mule/logs", "/opt/mule/conf", "/opt/mule/apps", "/opt/mule/domains"]

# Define working directory.
WORKDIR /opt/mule

CMD [ "/opt/mule/bin/mule" ]

# Default http ports
EXPOSE ${HTTP_CONN_PORT_1}
