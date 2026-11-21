# The Beacon of Release: A DevOps Quest

The harbor of Production has gone dark. Every release arrives by hand, nobody can rebuild the delivery tower, and the last deployment left no trail home. You are the engineer summoned to light the beacon again.

Your cargo is `simple-java-app`: a Java 17 Spring Boot service. `GET /` returns `hello [YOUR_NAME]`, and `/actuator/health` reports its health. Your tools remain Docker, Jenkins, Kubernetes, Docker Hub, and optionally ArgoCD. This time, Terraform must raise the delivery tower itself.

## Chapter 1 — Forge the Cargo

Write a multi-stage `Dockerfile` for the supplied app, then build it. The Dockerfile is intentionally left for you to create. Compilation and automated tests must run inside `docker build`; do not install or run Java or Maven on the host. Confirm the greeting, environment-variable behavior, and health endpoint from the running container.

Run Trivy against the final image, covering both OS packages and Java dependencies. Save the machine-readable report and image digest. Fix every reported vulnerability before advancing; do not hide findings with ignore lists or `--ignore-unfixed`. A clean scan means no known findings in that database snapshot, not a permanent security guarantee. Run the container as a non-root user with a read-only filesystem and a writable temporary directory.

**Checkpoint:** a tested image, a repeatable scan command, and a report tied to the exact image you will ship.

## Chapter 2 — Raise the Delivery Tower

Choose a local Kubernetes cluster such as Kind or Minikube, or a cloud cluster. Document the boundary: an existing local cluster may be a prerequisite; any cloud infrastructure you choose must be provisioned through Terraform.

Create a `terraform/` directory that automates Jenkins installation, its persistent storage, service access, namespace, service account, and scoped RBAC. Terraform may use the Helm and Kubernetes providers. Install and configure the Jenkins Kubernetes plugin so builds launch temporary agents in the cluster. Use Jenkins Configuration as Code and a reproducible plugin list; bootstrap the pipeline job from code as well. A clean environment must not require clicking through Jenkins setup.

Keep credentials out of Git and Terraform outputs. Document how secrets are injected and how Terraform state is protected, since sensitive values can still be stored in state. Pin provider and chart versions, commit the provider lock file, expose useful non-secret outputs, and supply example inputs.

**Checkpoint:** `terraform init`, a reviewed `terraform plan`, and `terraform apply` create a working Jenkins installation. A second plan shows no unexpected changes. Explain how to tear down the lab and which persistent data survives.

## Chapter 3 — Chart the Harbor

Create `k8s/deployment.yaml`, `k8s/service.yaml`, and `k8s/ingress.yaml`. Install an ingress controller compatible with your chosen cluster and document hostname resolution.

Give the application readiness and liveness probes, resource requests and limits, a non-root security context, disabled privilege escalation, dropped capabilities, and a read-only root filesystem with writable temporary storage. Supply `YOUR_NAME` through configuration. Keep the Service internal and route external traffic through Ingress. Deploy immutable image tags or digests rather than `latest`.

**Checkpoint:** the greeting is reachable through Ingress, health checks pass, and the workload recovers after a pod is deleted.

## Chapter 4 — Ignite the Pipeline

Write a root `Jenkinsfile` that runs on Kubernetes agents and performs these gates in order:

1. Check out the requested revision and identify the commit.
2. Build with Docker, running the Maven tests inside the Docker build. Preserve test results as Jenkins artifacts, including when tests fail.
3. Scan the final image with Trivy. Any reported vulnerability or scanner failure stops delivery. Archive the report and record the database timestamp and scanner version.
4. Authenticate using Jenkins credentials, then push the passing image to Docker Hub with a unique commit-based tag. Capture its digest.
5. Deploy that exact digest to Kubernetes, wait for rollout completion with a timeout, and smoke-test the greeting and health endpoint through Ingress.
6. On a failed deployment, restore the previous known-good revision and report the failure clearly. On success, record the commit, image digest, and deployment result.

Explain how the Kubernetes agents build images. If you choose Docker-in-Docker or a mounted Docker socket, document the security implications and isolate the build workers. Do not give application deployment credentials cluster-admin permissions. Prevent overlapping deployments from racing.

**Checkpoint:** a commit travels from source to a healthy deployment without manual intervention. Demonstrate that a failing test or vulnerability gate prevents publication and deployment, and that a failed rollout exercises recovery.

## Final Trial — Leave a Map

Submit the application, Dockerfile, Jenkinsfile, Terraform configuration, Jenkins configuration, Kubernetes manifests, and a concise runbook. Include prerequisite versions, bootstrap commands, credential setup, architecture, verification evidence, scan results, rollback steps, teardown, and known limitations. Another engineer should be able to rebuild your lab from the repository.

You complete the quest when infrastructure is reproducible, delivery is automated, security gates fail closed, and the beacon serves a healthy release.

## Optional Expedition — The GitOps Watchkeeper

Provision ArgoCD through Terraform. Move deployment manifests into a separate GitOps repository and configure ArgoCD to reconcile them. Change Jenkins to open or commit an image-digest update there after the security gates pass. ArgoCD becomes the deployment owner; remove direct application deployment from Jenkins. Demonstrate sync, health, drift correction, and rollback through a Git revert.

## Note
You Can add any tools to monitoring the system.