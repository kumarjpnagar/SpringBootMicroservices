FROM alpine

RUN apk --no-cache \
	add \
	apache2 \
	git \
	git-daemon \
	git-perl \
	perl-libwww \
        curl
	 
# Apparently alpine & httpd don't create this.
RUN mkdir /run/apache2

# Establish empty git repo
RUN mkdir -p /opt/config-repos
RUN git init --bare /opt/config-repos/config.git

RUN mkdir -p /opt/config-repos/temp_repo
COPY configuration.zip /opt/config-repos/temp_repo

# Copy Post receive hook
COPY post-receive /opt/config-repos/config.git/hooks/post-receive
RUN chmod +x /opt/config-repos/config.git/hooks/post-receive

RUN git -C /opt/config-repos/config.git/ config --bool receive.advertisePushOptions true

# Change ownership to allow apache to work with the repos
RUN chown -R apache:apache /opt/config-repos

# Add our configuration to load CGI and deal with the git repos
COPY intellig-config-repo.conf /etc/apache2/conf.d

EXPOSE 80
#CMD [ "httpd", "-DFOREGROUND" ]

ADD docker-entrypoint.sh /opt/config-repos/
RUN chmod +x /opt/config-repos/docker-entrypoint.sh
ENTRYPOINT ["/opt/config-repos/docker-entrypoint.sh"]
