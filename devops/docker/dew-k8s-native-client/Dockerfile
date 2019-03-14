FROM docker:18
MAINTAINER gudaoxuri <i@sunisle.org>

ARG HOME=/root

RUN apk add --no-cache curl

# ----------------- Add Kubectl

ARG KUBE_VERSION=1.13.3

RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/v$KUBE_VERSION/bin/linux/amd64/kubectl \
    && chmod +x ./kubectl && mv ./kubectl /usr/local/bin/kubectl \
    && mkdir -p $HOME/.kube

# ----------------- Add Helm

ARG HELM_VERSION=2.12.3

RUN curl -LO https://kubernetes-helm.storage.googleapis.com/helm-v$HELM_VERSION-linux-amd64.tar.gz \
    && tar -zxvf helm-v$HELM_VERSION-linux-amd64.tar.gz \
    && mv linux-amd64/helm /usr/local/bin/helm \
    && rm -rf helm-v$HELM_VERSION-linux-amd64.tar.gz \
    && rm -rf linux-amd64

CMD ["sh"]