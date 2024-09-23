## Setup

- To create a cluster with Workload Identity
```shell
gcloud container clusters create surbhidemocluster --location=us-central1 --workload-pool=span-cloud-testing.svc.id.goog
```

- To associate to an existing cluster - 
```shell
gcloud container clusters update autopilot-surbhidemo-cluster --location=us-central1 --workload-pool=span-cloud-testing.svc.id.goog
```

- Install plugin - `gcloud components install gke-gcloud-auth-plugin`. Then, add to path

- Get kube credentials - 
```shell
gcloud container clusters get-credentials autopilot-surbhidemo-cluster --region us-central1 --project span-cloud-testing
```

- Refernce to further steps - https://cloud.google.com/kubernetes-engine/docs/how-to/workload-identity?cloudshell=true#authenticating_to. In short
    - Create namespace - `kubectl create namespace surbhins`
    - Create Service Account - `kubectl create serviceaccount surbhiksa --namespace surbhins`
    - Create IAM policy - 
    ```shell
    gcloud projects add-iam-policy-binding projects/span-cloud-testing \
    --role=roles/container.clusterViewer \
    --member=principal://iam.googleapis.com/projects/874422006620/locations/global/workloadIdentityPools/span-cloud-testing.svc.id.goog/subject/ns/surbhins/sa/surbhiksa \
    --condition=None
    ```
    - To apply k8s file - `kubectl apply -f k8s.yaml`
    - To check for Ingress IP - `kubectl get ingress --output wide -n <namespace>`
