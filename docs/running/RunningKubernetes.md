docker save skhatri/k8s-read:v1.0.4 > k8s-read.tar
multipass transfer k8s-read.tar microk8s-vm:/tmp/k8s-read.tar
microk8s ctr image import /tmp/k8s-read.tar
microk8s ctr images ls

