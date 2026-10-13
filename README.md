# CS4801 DevOps Lab — CI/CD Pipeline Lab Assessment

**Name:** Narenkarthik K  **Roll No:** 23011102055  **Question Set:** F — Successful-registration audit log

This is a Java course registration service built with Maven, versioned with Git and GitHub, built and tested by a Jenkins Declarative Pipeline, and accompanied by an Nginx deployment on Kubernetes using Minikube.

**Repository:** https://github.com/narenkumarchandran/student_registration_cia
**Feature branch:** `feature/course-registration-refactor` (merged into `main`)

---

## 1. Setup and Execution Instructions

### Requirements

You need Java JDK 17, Apache Maven, Git, Jenkins, Docker Desktop, Minikube and kubectl. All commands below were run in Windows PowerShell.

### Clone

```powershell
git clone https://github.com/narenkumarchandran/student_registration_cia.git
cd student_registration_cia
git checkout main
```

### Build, test and package (run from the repository root)

```powershell
mvn clean test
mvn package
```

The packaged artifact is written to `course-registration-app/target/course-registration-app-1.0-SNAPSHOT.jar`, and JUnit reports are written to `course-registration-app/target/surefire-reports/`.

### Deploy to Minikube

```powershell
minikube start --driver=docker
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl get deployments
kubectl get pods -l app=nginx
kubectl get services
minikube service nginx-service --url
```

With the Docker driver on Windows, `minikube service --url` opens a tunnel, so the terminal must stay open while you access the URL.

---

## 2. Git Evidence

### Branch and commit history

```text
PS> git log --oneline --graph --all -n 12
*   18a1635 (HEAD -> main, origin/main) Merge branch 'feature/course-registration-refactor' into main
|\
| * 8c85608 (origin/feature/course-registration-refactor, feature/course-registration-refactor) added read me
| * 2cf44ec feat: add Kubernetes deployment and service
| * ca23c0f fix: remove BOM from Jenkinsfile
| * af05d68 fix: use explicit Maven path in Jenkins
| * f2b05c3 fix: save Jenkinsfile without BOM
| * 50892f8 build: restructure project and add Jenkins pipeline
|/
* 4da0a14 feat: add registration audit log
* edcfda2 reject registration when course is full
* 234e0f1 cover course capacity boundaries
* cef2bff Initial student assessment starter

PS> git branch -vv
  feature/course-registration-refactor 8c85608 [origin/feature/course-registration-refactor] added read me
* main                                 18a1635 [origin/main] Merge branch 'feature/course-registration-refactor' into main
```

### Summary of changes by commit

* `234e0f1`: Added JUnit tests for a course at full capacity and a course with exactly one seat left.
* `edcfda2`: Fixed the capacity check so a full course rejects new registrations.
* `4da0a14`: Added the in-memory audit log for successful registrations, with its tests (Set F).
* `50892f8`: Restructured the project into a parent POM plus the `course-registration-app` child module, and added the Jenkinsfile.
* `f2b05c3`, `af05d68`, `ca23c0f`: Fixed the Jenkinsfile so it runs on the Windows Jenkins agent. These removed the UTF-8 BOM that Notepad added and switched to an explicit Maven path.
* `2cf44ec`: Added the Kubernetes `deployment.yaml` and `service.yaml`.
* `18a1635`: Merged the feature branch into `main` with `--no-ff`, so the merge is visible in the history.

The `target/` directory is not committed.

---

## 3. Maven Configuration and Build Evidence

### Project structure

```text
Student-Starter/
├── pom.xml                         (parent: com.university:student-starter-parent, packaging pom)
├── Jenkinsfile
├── deployment.yaml
├── service.yaml
├── README.md
└── course-registration-app/
    ├── pom.xml                     (child: com.university:course-registration-app, packaging jar)
    └── src/
        ├── main/java/com/university/   Course.java, RegistrationService.java, Student.java
        └── test/java/com/university/   CourseTest.java, RegistrationServiceTest.java
```

### Parent and child POMs

* **Parent POM (`pom.xml`):**
  * It uses `pom` packaging and lists `course-registration-app` in its `<modules>`.
  * It defines shared properties, including Java 17 and the JUnit 5 version.
  * It declares the JUnit 5 version once in `<dependencyManagement>`.
  * It configures the Maven Compiler plugin for Java 17 and the Surefire plugin to run JUnit 5 tests.
* **Child POM (`course-registration-app/pom.xml`):**
  * It inherits from the parent through a `<parent>` block (the parent's groupId, artifactId and version, with `<relativePath>../pom.xml</relativePath>`).
  * It declares the JUnit 5 dependency without a version, so the version comes from the parent's `dependencyManagement`.

The existing source and test files were moved unchanged into the child module's `src/main/java` and `src/test/java`.

### Build results (run from the repository root, excerpt of genuine output)

```text
PS> mvn clean test
[INFO] Reactor Build Order:
[INFO] student-starter-parent                                             [pom]
[INFO] course-registration-app                                            [jar]
...
[INFO] Running com.university.CourseTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.033 s -- in com.university.CourseTest
[INFO] Running com.university.RegistrationServiceTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.015 s -- in com.university.RegistrationServiceTest
[INFO] Results:
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] Reactor Summary for student-starter-parent 1.0-SNAPSHOT:
[INFO] student-starter-parent ............................. SUCCESS [  0.299 s]
[INFO] course-registration-app ............................ SUCCESS [  3.008 s]
[INFO] BUILD SUCCESS
[INFO] Finished at: 2026-10-13T11:08:24+05:30

PS> mvn clean package
...
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jar:3.5.1:jar (default-jar) @ course-registration-app ---
[INFO] Building jar: C:\Users\iot-a2\Downloads\ci cd cia lab\Student-Starter\course-registration-app\target\course-registration-app-1.0-SNAPSHOT.jar
[INFO] student-starter-parent ............................. SUCCESS [  0.289 s]
[INFO] course-registration-app ............................ SUCCESS [  3.031 s]
[INFO] BUILD SUCCESS
[INFO] Finished at: 2026-10-13T11:08:30+05:30
```

The reactor builds the parent first and then the child module.

| Stage | CourseTest | RegistrationServiceTest | Total | Failures | Errors |
|---|---|---|---|---|---|
| Baseline (before changes) | 3 | 6 | 9 | 0 | 0 |
| Capacity tests added, before fix | 3 | 8 | 11 | 1 | 0 |
| After capacity fix | 3 | 8 | 11 | 0 | 0 |
| After audit log (final) | 3 | 11 | 14 | 0 | 0 |

---

## 4. Jenkins Pipeline Explanation

**Target environment:** The pipeline targets a **Windows** Jenkins agent, so every build command uses `bat` steps. The final Jenkinsfile calls Maven by its full install path on the agent and is saved without a BOM. Maven commands run from the repository root, which is the workspace root, so the parent POM builds the child module through the reactor.

**Stages:**

1. `Checkout` runs `checkout scm` to fetch the branch from GitHub.
2. `Build and Test` runs `mvn clean test`.
3. `Package` runs `mvn package`.

**Post actions:**

```groovy
post {
    always  { junit 'course-registration-app/target/surefire-reports/TEST-*.xml' }
    success { archiveArtifacts artifacts: 'course-registration-app/target/*.jar', fingerprint: true }
    failure { echo 'Pipeline failed. Review the build output and test reports.' }
}
```

**How test reports are published:** The `junit` step is in `post { always }`, so Jenkins records the Surefire XML reports from the child module after every run. This includes failed runs, so a failing test can still be inspected in the Jenkins test report.

**How packaging and archiving are prevented when tests fail:**

* A failing test makes `mvn clean test` exit with a non-zero code. The `bat` step then fails, which marks the `Build and Test` stage and the build as failed.
* In a Declarative Pipeline, the stages after a failed stage are skipped, so `Package` never runs.
* `archiveArtifacts` is in `post { success }`, so it runs only when the whole pipeline succeeds. A JAR is never archived from a failed build.

**Jenkins run evidence (job `course_registration`, build #3):**

* The build ran from branch `origin/feature/course-registration-refactor` at revision `ca23c0f`.
* The console showed 14 tests run with 0 failures, recorded the test results, and archived the artifacts. It ended with `Finished: SUCCESS`.
* The test report page showed all 14 tests passing in package `com.university`.
* The archived artifact was `course-registration-app-1.0-SNAPSHOT.jar` (4.79 KiB).

Screenshots of these are in the submitted report.

---

## 5. Kubernetes Deployment Evidence (Minikube)

### `deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
        - name: nginx
          image: nginx:1.27
          ports:
            - containerPort: 80
```

### `service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: NodePort
  selector:
    app: nginx
  ports:
    - port: 80
      targetPort: 80
      nodePort: 30080
```

The Deployment's selector, the Pod template label and the Service selector all use `app: nginx`. This is how the Service finds the Deployment's Pods.

### Deployment (2 replicas)

```text
PS> kubectl apply -f .\deployment.yaml
deployment.apps/nginx-deployment created
PS> kubectl get pods -l app=nginx
NAME                                READY   STATUS    RESTARTS   AGE
nginx-deployment-75cc477c85-5zcrf   1/1     Running   0          25s
nginx-deployment-75cc477c85-lbkqn   1/1     Running   0          25s
```

### Service and access

```text
PS> kubectl apply -f .\service.yaml
service/nginx-service created
PS> kubectl get services
NAME            TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
kubernetes      ClusterIP   10.96.0.1       <none>        443/TCP        7m55s
nginx-service   NodePort    10.102.46.166   <none>        80:30080/TCP   46s
PS> kubectl get endpoints nginx-service
Warning: v1 Endpoints is deprecated in v1.33+; use discovery.k8s.io/v1 EndpointSlice
NAME            ENDPOINTS                     AGE
nginx-service   10.244.0.3:80,10.244.0.4:80   53s
PS> minikube service nginx-service --url
http://127.0.0.1:60831
```

The two endpoints are the two Nginx Pods, which confirms the Service selects the intended Pods. Opening `http://127.0.0.1:60831` in a browser showed the "Welcome to nginx!" page.

### Scaling from 2 to 3 replicas

```text
PS> kubectl scale deployment nginx-deployment --replicas=3
deployment.apps/nginx-deployment scaled
PS> kubectl get pods -l app=nginx
NAME                                READY   STATUS    RESTARTS   AGE
nginx-deployment-75cc477c85-5zcrf   1/1     Running   0          4m53s
nginx-deployment-75cc477c85-f89ss   1/1     Running   0          1s
nginx-deployment-75cc477c85-lbkqn   1/1     Running   0          4m53s
PS> kubectl get deployments
NAME               READY   UP-TO-DATE   AVAILABLE   AGE
nginx-deployment   3/3     3            3           5m41s
```

### Self-healing

```text
PS> kubectl delete pod nginx-deployment-75cc477c85-5zcrf
pod "nginx-deployment-75cc477c85-5zcrf" deleted from default namespace
PS> kubectl get pods -l app=nginx
NAME                                READY   STATUS    RESTARTS   AGE
nginx-deployment-75cc477c85-55smm   1/1     Running   0          10s
nginx-deployment-75cc477c85-f89ss   1/1     Running   0          87s
nginx-deployment-75cc477c85-lbkqn   1/1     Running   0          6m19s
```

Pod `5zcrf` was deleted, and a new Pod `55smm` (age 10s) replaced it. The count stayed at 3, and all Pods are `Running`.

### How the Deployment controller maintains the replica count

A Deployment manages a ReplicaSet, which holds the desired replica count (`spec.replicas`). The controller runs a reconciliation loop: it keeps comparing the number of running Pods that match the `app: nginx` selector against the desired count. When we scaled to 3, it created one extra Pod to close the gap. When a Pod was deleted, the actual count dropped to 2, so the ReplicaSet controller immediately created a replacement from the Pod template to restore 3.

---

## 6. Change Summary and Limitations

### Defect correction (Task 3)

The baseline capacity check let a student register for a course that was already full. The new test `shouldRejectRegistrationWhenCourseIsFull` failed with `expected: <false> but was: <true>`. The condition was corrected to reject a registration when `registeredCount >= capacity`. A full course now rejects new students, and the last remaining seat can still be filled. Capacity validation was kept, not removed, and all existing tests still pass.

### Assigned change request — Set F audit log (Task 4)

`RegistrationService` now keeps an in-memory audit log. It adds one entry only when a registration succeeds. Rejected registrations add nothing, and earlier entries are never removed or duplicated.

**Entry format:** `Student <studentId> successfully registered for course <courseCode>`
Example: `Student S101 successfully registered for course JAVA101`

Focused JUnit tests cover:

* a successful registration;
* a rejected registration;
* the audit-log entry count and content.

### Modified and added files

* `pom.xml` (now the parent POM)
* `course-registration-app/pom.xml` (new child POM)
* `course-registration-app/src/main/java/com/university/*` (moved; capacity fix and audit log added)
* `course-registration-app/src/test/java/com/university/*` (moved; new capacity and audit-log tests)
* `Jenkinsfile`
* `deployment.yaml`
* `service.yaml`
* `README.md`

### Known limitations

* The audit log lives in memory only, so it is lost when the application restarts. It does not use a database or external logging service.
* The tests, defect fix and audit-log commits (`234e0f1`, `edcfda2`, `4da0a14`) were made before the feature branch was created, so they sit below the branch point in the history. The restructure, Jenkins and Kubernetes work was done on the feature branch and merged into `main`.
* The Jenkins build ran from the feature branch (build #3), before the merge into `main`.
* The Jenkinsfile targets a Windows agent (`bat` and an explicit Maven path), so it would need `sh` steps to run on a Linux agent.
* Minikube is a local, single-node learning environment, not a production cluster. With the Docker driver on Windows, the service is reached through a `minikube service` tunnel rather than directly on NodePort 30080.
