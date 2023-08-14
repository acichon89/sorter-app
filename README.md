# Weird legal requirements

### Requiremends
- JDK 17
- Sense of humor

### How to build
Linux/Unix:
```console
./gradlew build
```
Windows:
```console
gradlew.bat build
```
### How to test
Linux/Unix:
```console
./gradlew test
```
Windows:
```console
gradlew.bat test
```

### How to run locally
Linux/Unix:
```console
./gradlew bootRun
```
Windows:
```console
gradlew.bat bootRun
```

### API documentation
After run successfully open:
- OpenApi: http://localhost:8080/api-docs
- Swagger: http://localhost:8080/swagger-ui/index.html
- H2 console: http://localhost:8080/h2-console (jdbc:h2:mem:testdb sa/sa)

### Pre-populated data:
#### Racks:

| Rack ID | Capacity |
|---------|----------|
| 1       | 20       |
| 2       | 20       |
| 3       | 20       |
| 4       | 50       |
| 5       | 50       |

#### Samples:
| Sample ID | Rack ID | Age | Company    | City District     | Vision Defect |
|-----------|---------|-----|------------|-------------------|---------------|
| 1         | 1       | 33  | ECB        | Trojkat Bermudzki | Astigmatism   |
| 2         | 1       | 35  | Metroplan  | Osiedle Utopia    | Blind         |
| 3         | 2       | 33  | Metroplan  | Jawor             | Blind         |
| 4         | 2       | 21  | Mrowks PSB | Jaworzno          | Astigmatism   |
| 5         | 3       | 21  | EY         | Wroclaw Biskupin  | Astigmatism   |

### General concepts
The main problem of this domain is saving an agregate that holds collection of sub-entities. Because of globally shared entity there is a challenge to keep consistent entity with all logic constraints in multithreading and scaling service.

To solve that issue in easy and convenient way, it is recommended to provide Optimistic-Locking. Pessimistic-Locking may lead to slow, deadlock-aware thread processing.
Moreover, the simple locking strategy here is not enough, because Optimistic-Locking solution does not increase `@Version` field while changing `@OneToMany` relation child-objects. Therefore it is recommended to implement `OPTIMISTIC_FORCE_INCREMENT` locking strategy.
Additional problem (however, not specified in the description) is having limited capacity of single rack, because it is impossible for particular Rack object to hold unlimited bunch of different Samples.

SQL query in `RackRepository` checks the fastest conditions, whether rack:
* is empty
* is not fully-loaded
* doesn't have relation with any Sample that matches given criteria

Then, we select randomly one of the returned rack IDs (otherwise top-queried Rack will be most often returned and there will be higher chance for version change by another thread) and acquire a lock.
Once again we check the capacity and criteria, if rack can hold given sample, we persist a new relation. If everything goes well, process is finished and assigned ID are returned. If not, we retry operation thanks to `@Retry` aspect and repeat whole scenario once again.
If number of attempts will reach to maximum, the 503 HTTP response status is given (too heavy traffic). It is possible to modify configuration of retry policy by specify two params:
- `sorter.retry.backoffDelayInMillis`
- `sorter.retry.maxAttempts`

in the runtime.

It is also possible that none of the Racks can hold new sample data and the server returns 422 status code in that case.