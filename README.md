# SpringBoot K8s
[![CI](https://github.com/NicoMincuzzi/springboot-k8s/actions/workflows/ci.yml/badge.svg)](https://github.com/NicoMincuzzi/springboot-k8s/actions/workflows/ci.yml)
![GitHub repo size](https://img.shields.io/github/repo-size/NicoMincuzzi/springboot-k8s)

Simple SpringBoot web app, which is containerized by Docker and orchestrated by K8s. In particular, the following K8s manifests kind have been defined:
- `kind: Deployment`
- `kind: Service` : in order to communicate with other pods and external world
- `kind: Ingress` : in **Minikube** it's used by the relative add-on

## Installing Kubernetes

### Kubernetes Configuration
Kubernetes can be installed using different cluster configurations. The major installation types are described below:
 - **All-in-One Single-Node Installation**


 - **Single-Master and Multi-Worker Installation**


 - **Single-Master with Single-Node etcd, and Multi-Worker Installation**


 - **Multi-Master and Multi-Worker Installation**

 
- **Multi-Master with Multi-Node etcd, and Multi-Worker Installation**

### Infrastructure for Kubernetes Installation
Once we decide on the installation type, we need to decide on the infrastructure. Infrastructure related decisions are typically guided by the desired environment type, either learning or production environment.

There are a variety of installation tools allowing us to deploy single- or multi-node Kubernetes clusters on our workstations. While not an exhaustive list, below we enumerate a few popular ones:
  - **Minikube** - single-node local Kubernetes cluster, recommended for a learning environment deployed on a single host. 
  - **Kind** - multi-node Kubernetes cluster deployed in Docker containers acting as Kubernetes nodes, recommended for a learning environment.
  - [**Docker Desktop**](https://www.docker.com/products/docker-desktop) - including a local Kubernetes cluster for Docker users.
  - [**MicroK8s**](https://microk8s.io/) - local and cloud Kubernetes cluster, from Canonical.
  - [**K3S**](https://k3s.io/) - lightweight Kubernetes cluster for local, cloud, edge, IoT deployments, from Rancher.

**Minikube** is the easiest and most preferred method to create an all-in-one Kubernetes setup locally. 

In my project, I use **Minikube** as a single-node on my local machine and **MicroK8s** to create a Kubernetes cluster on Raspberry Pis. It has needed to build Docker image for arm architecture, as follows.

## Multi-Architecture Docker
Docker images can support multiple architectures, which means that a single image may contain variants for different architectures, and sometimes for different operating systems, such as Windows.

When running an image with multi-architecture support, `docker` will automatically select an image variant which matches your OS and architecture.

Most of the official images on Docker Hub provide a variety of architectures. For example, the `busybox` image supports `amd64`, `arm32v5`, `arm32v6`, `arm32v7`, `arm64v8`, `i386`, `ppc64le`, and `s390x`. When running this image on an `x86_64` / `amd64` machine, the `x86_64` variant will be pulled and run.
### Docker Buildx(Experimental)
Docker is now making it easier than ever to develop containers on, and for Arm servers and devices. Using the standard Docker tooling and processes, you can start to build, push, pull, and run images seamlessly on different compute architectures. Note that you don’t have to make any changes to Dockerfiles or source code to start building for Arm.

Docker introduces a new CLI command called buildx. You can use the `buildx` command on Docker Desktop for Mac and Windows to build multi-arch images, link them together with a manifest file, and push them all to a registry using a single command. With the included emulation, you can transparently build more than just native images. Buildx accomplishes this by adding new builder instances based on BuildKit, and leveraging Docker Desktop’s technology stack to run non-native binaries.

If you are on Mac or Windows, you have nothing to worry about, `buildx` is shipped with Docker Desktop. If you are on Linux, you might need to install it by following the documentation here https://github.com/docker/buildx

#### Build and run multi-architecture images

Run the command docker buildx ls to list the existing builders. This displays the default builder, which is our old builder.

```
$ docker buildx ls

NAME/NODE DRIVER/ENDPOINT STATUS  PLATFORMS
default * docker
default default         running linux/amd64, linux/arm64, linux/arm/v7, linux/arm/v6
```

Test the workflow to ensure you can build, push, and run multi-architecture images. Create a simple example Dockerfile, build a couple of image variants, and push them to Docker Hub.

```
$ docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7 -t username/demo:latest --push .

[+] Building 6.9s (19/19) FINISHED
...
=> => pushing layers                                                             2.7s
=> => pushing manifest for docker.io/username/demo:latest                       2.2
```

Where, username is a valid Docker username.

> **Notes**:
>
> - The `--platform` flag informs buildx to generate Linux images for AMD 64-bit, Arm 64-bit, and Armv7 architectures.
> - The `--push` flag generates a multi-arch manifest and pushes all the images to Docker Hub.

Inspect the image using `imagetools`.

```
$ docker buildx imagetools inspect username/demo:latest

Name:      docker.io/username/demo:latest
MediaType: application/vnd.docker.distribution.manifest.list.v2+json
Digest:    sha256:2a2769e4a50db6ac4fa39cf7fb300fa26680aba6ae30f241bb3b6225858eab76

Manifests:
Name:      docker.io/username/demo:latest@sha256:8f77afbf7c1268aab1ee7f6ce169bb0d96b86f585587d259583a10d5cd56edca
MediaType: application/vnd.docker.distribution.manifest.v2+json
Platform:  linux/amd64

Name:      docker.io/username/demo:latest@sha256:2b77acdfea5dc5baa489ffab2a0b4a387666d1d526490e31845eb64e3e73ed20
MediaType: application/vnd.docker.distribution.manifest.v2+json
Platform:  linux/arm64

Name:      docker.io/username/demo:latest@sha256:723c22f366ae44e419d12706453a544ae92711ae52f510e226f6467d8228d191
MediaType: application/vnd.docker.distribution.manifest.v2+json
Platform:  linux/arm/v7
```

The image is now available on Docker Hub with the tag `username/demo:latest`. You can use this image to run a container on Intel laptops, Amazon EC2 A1 instances, Raspberry Pis, and on other architectures. Docker pulls the correct image for the current architecture, so Raspberry Pis run the 32-bit Arm version and EC2 A1 instances run 64-bit Arm. The SHA tags identify a fully qualified image variant. You can also run images targeted for a different architecture on Docker Desktop.

You can run the images using the SHA tag, and verify the architecture. For example, when you run the following on a macOS:

```
$ docker run --rm docker.io/username/demo:latest@sha256:2b77acdfea5dc5baa489ffab2a0b4a387666d1d526490e31845eb64e3e73ed20 uname -m
aarch64
```

```
$ docker run --rm docker.io/username/demo:latest@sha256:723c22f366ae44e419d12706453a544ae92711ae52f510e226f6467d8228d191 uname -m
armv7l
```

In the above example, `uname -m` returns `aarch64` and `armv7l` as expected, even when running the commands on a native macOS developer machine.
