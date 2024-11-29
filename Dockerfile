# Base Image
FROM jenkins/jenkins:lts

# Switch to root to install packages
USER root

# Install prerequisites
RUN apt-get update && apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common \
    git \
    python3 \
    unzip \
    && apt-get clean

# Add Docker's official GPG key
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker repository
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list

# Install Docker
RUN apt-get update && apt-get install -y docker-ce docker-ce-cli containerd.io && apt-get clean

# Install AWS CLI
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && ./aws/install && \
    rm -rf awscliv2.zip ./aws

# Install Gitleaks
RUN curl -sSL https://github.com/zricethezav/gitleaks/releases/download/v8.16.4/gitleaks_8.16.4_linux_x64.tar.gz | tar -xz -C /usr/local/bin && \
    chmod +x /usr/local/bin/gitleaks

# Verify installations
RUN docker --version && \
    aws --version && \
    gitleaks version && \
    git --version

# Switch back to Jenkins user
USER jenkins

# Set default command
ENTRYPOINT ["/usr/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
