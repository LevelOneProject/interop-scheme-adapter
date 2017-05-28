FROM ubuntu:14.04
ENV MULE_VERSION=3.8.1 MULE_HOME=/opt/mule JAVA_VERSION=8 MAX_JVM_MEMORY=2048 MULE_ENV=dev MULE_LIB=/opt/mule/lib HTTP_CONN_PORT_1=8081 HTTP_CONN_PORT_2=8088 HTTP_CONN_PORT_3=9081 HTTP_CONN_PORT_4=9082  INTEROP_DOMAIN_VERSION=0.1.29
ENV JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-oracle
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections

RUN apt-get update && \
    apt-get install -y --no-install-recommends software-properties-common && \
    add-apt-repository ppa:webupd8team/java && \
    apt-get update && \
    apt-get install -y --no-install-recommends oracle-java${JAVA_VERSION}-installer && \
    rm -rf /var/lib/apt/lists/*
    
RUN if [ "${MULE_VERSION}" != "3.8.1" ] && [ "${MULE_VERSION}" != "3.8.0" ]; then echo "-----   Unsupported version: ${MULE_VERSION}   -----" && return 1; fi

RUN if [ "${MULE_VERSION}" = "3.8.1" ]; then cd ~ && wget https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/${MULE_VERSION}/mule-standalone-${MULE_VERSION}.tar.gz && echo "db079c0fc01c534d443277cfe96ab252 mule-standalone-${MULE_VERSION}.tar.gz" | md5sum -c ; fi

RUN cd /opt && tar xvzf ~/mule-standalone-${MULE_VERSION}.tar.gz && rm ~/mule-standalone-${MULE_VERSION}.tar.gz && ln -s /opt/mule-standalone-${MULE_VERSION} /opt/mule

RUN cd /opt/mule && wget https://modusbox.jfrog.io/modusbox/libs-release/com/l1p/interop/interop-domain/${INTEROP_DOMAIN_VERSION}/interop-domain-${INTEROP_DOMAIN_VERSION}.zip --output-document=domains/interop-domain.zip --http-user=docker-user --password=BMGFGates2016

COPY mule_artifacts/wrapper.conf /opt/mule/conf/wrapper.conf
COPY mule_artifacts/grizzly-http-servlet-2.3.24.jar /opt/mule/lib/user
COPY mule_artifacts/mule-l1p.properties /opt/mule/conf
COPY target/interop-scheme-adapter*.zip /opt/mule/apps

# Define mount points.
VOLUME ["/opt/mule/logs", "/opt/mule/conf", "/opt/mule/apps", "/opt/mule/domains"]

# Define working directory.
WORKDIR /opt/mule

CMD [ "/opt/mule/bin/mule" ]

# Default http ports
EXPOSE ${HTTP_CONN_PORT_1} ${HTTP_CONN_PORT_2} ${HTTP_CONN_PORT_3} ${HTTP_CONN_PORT_4}
