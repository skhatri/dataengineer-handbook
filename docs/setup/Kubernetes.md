### Setup Kubernetes
We will install microk8s to run kubernetes locally. Other approaches like minikube, k3s will work too.

```
brew install ubuntu/microk8s/microk8s
```

Once microk8s is installed, we can choose specific version of Kubernetes to run.
The following commands should give us a Kubernetes instance.

```
microk8s install --channel=1.26
microk8s status --wait-ready
microk8s kubectl get nodes
```

Shutting down Kubernetes by 

```
microk8s stop
```

Start Kubernetes again by running

```
microk8s start
```

