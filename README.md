# raspi

Mono repo containing microservices/components that are meant to be deployed to k3s cluster setup on my own Raspberry Pi 5.
Everything is suited here to run on ARM architecture. If you wish to use that project in your own different environment, you'll have to change it accordingly. Considerations are covered in **Other Platforms** section.

## Background

Project started with the idea of having a way of deploying applications for my personal use and being able to connect them to database, without reliance on SaaS or other subscription based systems, that come with the cost of regular monthly fees regardless of usage, or unpredictable and risky payment plans.
At the same time I wanted a way to bring convienence in terms of maintenance in the long run that could be brought by Kubernetes and Helm. I also wanted being able to learn them by being exposed to different category of problems than I face at my full-time job - a Developer who only slighly works in DevOps territory.
Above led me to purchase of Raspberry Pi 5 and then to K3s that is optimized for ARM and is more lightweight than a classic K8s.

# Installation and Setup

## K3s

Following section assumes that you have Raspberry Pi or other machine that has OS already installed.
If you do not, see: https://www.raspberrypi.com/documentation/computers/getting-started.html

### K3s installation on Raspbian OS

Before installing K3s, you need to make sure that OS starts with **cgroups** enabled.
In order to do so append following options in `/boot/firmware/cmdline.txt`:

```
cgroup_memory=1 cgroup_enable=memory
```

After doing so and rebooting the machine K3s can be installed using one of the two following commands:

Default installation, that will require you to follow the steps in **Disabling Traefik Ingress Controller**:

```
curl -sfL https://get.k3s.io | sh -
```

Installation with disabled traefik. By using that you will be able to skip the steps in **Disabling Traefik Ingress Controller**:

```
curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC="--disable=traefik" sh -
```

### Disabling Traefik Ingress Controller

When K3s is installed with it's default options, Traefik Ingress Controller is used by default. This project relies on Nginx Ingress Controller, so Traefik has to be disabled.

First startup options of K3s has to be changed in `/etc/systemd/system/k3s.service` from:

```
ExecStart=/usr/local/bin/k3s \
    server \
```

to:

```
ExecStart=/usr/local/bin/k3s \
    server \
    --disable=traefik
```

Then we have to make sure that K3s will not install traefik helm chart that is defined in it's manifest.
For that `*.skip` file has to be added for traefik helm chart manifest:

```
sudo touch /var/lib/rancher/k3s/server/manifests/traefik.yaml.skip
```

Last step is to restart k3s:

```
sudo systemctl stop k3s
sudo systemctl daemon-reload
sudo systemctl start k3s
```

### Installing Nginx Ingress Controller

Installation is easy due to the fact that nginx-ingress is published as a helm chart.

```
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install nginx-ingress ingress-nginx/ingress-nginx --namespace nginx-ingress --create-namespace --atomic --wait --debug
```

## Other platforms

If you with to use it in environment other than K3s or/and Raspberry Pi or/and Raspbian OS, you need to consider the following:
1. K3s setup differs depending on platform, see: https://docs.k3s.io/installation/requirements
2. Github workflows responsible for building docker images only build the image for linux/arm64 platform

## Services

### Installation

For now services are installed via locally built helm charts. Each of them is installed one by one.
Example process of packaging and installing mongo helm chart:

```
$ helm package mongo/helm
Successfully packaged chart and saved it to: /home/irdcat/projects/raspi/mongo-0.1.0.tgz

$ helm install mongo ./mongo-0.1.0.tgz --atomic --wait --debug
```

### Upgrade

Upgrades are done in a very similar fashion as upgrades. In fact one command can be used for both, see example for mongo below:

```
$ helm package mongo/helm
Successfully packaged chart and saved it to: /home/irdcat/projects/raspi/mongo-0.1.0.tgz

$ helm upgrade mongo ./mongo-0.1.0.tgz --atomic --wait --debug --install
```

### Helm values

For documentation of helm values, refer to README.md files in the directory refering to the specific component.

## Local Development

For the details of working with the components in the local environment, refer to README.md files in the directory refering to the specific component.

## Authors

* irdcat - Owner, Development, DevOps
* MatiQQQ - DevOps