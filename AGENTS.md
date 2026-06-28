# Clinix Forge: AI Agent Developer Guide

**Project**: Clinix Forge - A healthcare management system for managing patients, doctors, prescriptions, treatments, and appointments.

**Tech Stack**: Spring Boot 4.0.6, Java 21, PostgreSQL, MapStruct, Lombok, Spring Security, WebSocket, Flyway migrations.

---

## Architecture Overview

Clinix Forge uses a **modular domain-driven layout**. Current implementation is uneven by module: `patient/` is the most complete REST module, while `doctor/` currently exposes entity/service/repository/mapper + DTOs without a controller in source.

Domain modules commonly include:
- `*Controller.java` - REST endpoints (e.g., `PatientController`)
- `*Service.java` - Business logic
- `*Repository.java` - Data access (Spring Data JPA)
- `*Mapper.java` - MapStruct DTOs
- `*Entity.java` (or domain-named entity like `Doctor.java`) - JPA entity models
- `exception/` - Custom domain exceptions
- `types/` or `dto/` - DTOs and request/response objects (value types)

**Data Flow**: REST Request → Controller → Service → Repository → Database (see `patient/`); some modules are currently service/repository only (see `doctor/`).

**Key Principle**: Each domain module is independently deployable and testable. Services are stateless and use dependency injection with Lombok's `@RequiredArgsConstructor`.

---

## Build & Development Workflows

### Build
```bash
./mvnw clean package           # Full build with tests
./mvnw clean compile          # Compile only
./mvnw test                   # Run tests
```

### Local Development
```bash
docker-compose up -d          # Start PostgreSQL (credentials in compose.yaml)
./mvnw spring-boot:run        # Start application (port 8080)
```

**Database**: PostgreSQL with Flyway migrations enabled. Connection details in `src/main/resources/application.yaml`.  
Migrations are auto-applied on startup from `src/main/resources/db/migration/`.

Current migration coverage is partial: only `V1__Create_doctors_table.sql` exists at the moment.

### Debug & Monitoring
- API Docs: `http://localhost:8080/api/docs`
- Health Check: `http://localhost:8080/actuator/health` (Spring Actuator)
- Log files: `logs/application.log` (JSON format), with 30-day rolling archive to `logs/archived/`

---

## Code Patterns & Conventions

### Service Layer Pattern
```java
@Slf4j
@Service
@RequiredArgsConstructor
@Validated  // Enable method-level validation
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional
    public PatientDTO createPatient(@Valid CreatePatientRequest request) {
        // 1. Validate input
        if (request == null) throw new IllegalArgumentException("...");
        
        // 2. Check business rules (e.g., duplicates)
        if (patientRepository.existsByCaseNumber(request.caseNumber())) {
            log.warn("Duplicate case number: {}", request.caseNumber());
            throw new DuplicatePatientException(request.caseNumber());
        }
        
        // 3. Map and persist
        PatientEntity entity = patientMapper.toEntity(request);
        entity = patientRepository.save(entity);
        
        // 4. Log success and return DTO
        log.info("Patient created: {}", entity.getCaseNumber());
        return patientMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(@NotNull UUID id) { /* ... */ }
}
```

### Controller Pattern with Swagger Documentation
```java
@Slf4j
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "...")
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "...", description = "...")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "..."),
        @ApiResponse(responseCode = "400", description = "...")
    })
    public ResponseEntity<SuccessResponse<PatientDTO>> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {
        log.info("Creating patient");
        PatientDTO patient = patientService.createPatient(request);
        
        // Build URI for Location header
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(patient.getId())
            .toUri();
        
        return ResponseEntity.created(location)
            .body(SuccessResponse.<PatientDTO>builder().data(patient).build());
    }
}
```

### MapStruct DTOs
- Use `@Mapper(componentModel = "spring")` for Spring integration
- Ignore auto-generated fields: `@Mapping(target = "id", ignore = true)`
- DTOs are in `types/` folder: `CreatePatientRequest`, `PatientDTO`, `UpdatePatientRequest`
- All DTOs include Swagger `@Schema` annotations with examples

### Entity Modeling with Soft Deletes
```java
@Entity
@Table(name = "clinix_patients")
@SQLDelete(sql = "UPDATE clinix_patients SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")  // Always exclude soft-deleted records
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // PostgreSQL JSON column support
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private List<String> medicalConditions = new ArrayList<>();
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

---

## Exception Handling

### Exception Hierarchy
- Base: `BaseException` (extends `RuntimeException`, includes `HttpStatus`)
- Current shared extensions: `ResourceNotFoundException`, `DuplicateResourceException`, `BusinessRuleViolationException`, `UnauthorizedException`, `AccessDeniedException`

### Exception Usage
```java
if (doctorRepository.findById(id).isEmpty()) {
    throw new ResourceNotFoundException("Doctor not found with ID: " + id);
}
```

**Note**: `GlobalExceptionHandler` is implemented and handles `BaseException`, `MethodArgumentNotValidException`, and fallback `Exception` into `ApiResponse`.

### API Response Format
Core responses are standardized with `ApiResponse<T>` in `src/main/java/com/clinix/forge/core/payload/ApiResponse.java`:
```java
ApiResponse.success(200, "Doctor fetched successfully", doctorDto)
```

For paginated service results, use `PaginatedPayload<T>` (`src/main/java/com/clinix/forge/core/payload/PaginatedPayload.java`).

---

## Data Access Patterns

### Repositories (Spring Data JPA)
- Use custom query methods: `existsByCaseNumber()`, `findById()`, `findAll(Pageable)`
- Pagination: `PageRequest.of(pageNo, pageSize)` → `Page<Entity>` → `map().getContent()`
- Soft-deleted records automatically excluded via `@SQLRestriction`

### Transactions
- Read operations: `@Transactional(readOnly = true)` (better performance)
- Write operations: `@Transactional` with default rollback on exceptions
- Explicit: `@Transactional(rollbackFor = Exception.class)`

---

## Logging & Observability

### Logging Configuration
- Console output with trace ID support: `%X{traceId:-no-trace}`
- File output: JSON format (Logstash encoder) for log aggregation
- Rolling policy: 100MB per file, 30-day retention, 5GB total cap
- Package levels: `com.clinix: DEBUG`, `org.springframework: WARN`

### Logging Best Practices
```java
log.info("Patient created", StructuredArguments.kv("case_number", entity.getCaseNumber()));
log.warn("Duplicate detected", StructuredArguments.kv("case_number", request.caseNumber()));
log.debug("Fetching patient with ID: {}", id);
```

Use Logstash structured args for machine-readable metadata.

---

## Security & Configuration

### JWT Authority
- Configured in `application.yaml` under `clinix.security.jwt`
- Secret: `${JWT_SECRET:73746f6e655f616e645f737465656c5f6f665f7468655f666f726765}` (environment var or default)
- Expiration: 86400000ms (24 hours)

### Spring Boot Profiles
- Active profile in `spring.application.name: forge`
- Override in environment: `SPRING_PROFILES_ACTIVE=prod`, `JWT_SECRET=...`

---

## When Adding New Features

1. **New Domain Module**: Create folder at `src/main/java/com/clinix/forge/{domain}/`
2. **Layer Structure**:
   ```
   {domain}/
   ├── {Domain}Entity.java or {Domain}.java
   ├── {Domain}Controller.java     (REST endpoints, Swagger docs)
   ├── {Domain}Service.java        (@Transactional, validated)
   ├── {Domain}Repository.java     (Spring Data interface)
   ├── {Domain}Mapper.java         (@Mapper interface)
   ├── exception/
   │   └── {Domain}NotFoundException.java
   └── types/ or dto/
       ├── {Domain}DTO.java        (@Schema annotations)
       ├── Create{Domain}Request.java
       └── Update{Domain}Request.java
   ```

3. **Database Migration**: Add `.sql` file to `src/main/resources/db/migration/V{N}__{description}.sql`
4. **Validation**: Use `@Valid` on controller methods and `@Validated` on service classes
5. **Testing**: Place tests in `src/test/java/com/clinix/forge/features/{domain}/`

---

## Key Files & References

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies and compiler plugin wiring (Spring Boot 4.0.6, Lombok, MapStruct) |
| `src/main/resources/application.yaml` | Config: DB, JWT, logging, Flyway |
| `logback-spring.xml` | Logging: console + JSON file appender, trace ID support |
| `compose.yaml` | Local PostgreSQL setup |
| `src/main/java/com/clinix/forge/patient/` | Reference REST implementation (controller + service + mapper + repository + entity) |
| `src/main/java/com/clinix/forge/doctor/` | Alternate module pattern (`Doctor.java`, `dto/`, service-level pagination payload usage) |
| `src/main/java/com/clinix/forge/core/` | Shared exceptions, payload wrappers, and logging infrastructure |

---

## Compile Considerations

- **Annotation Processing**: Lombok + MapStruct via `maven-compiler-plugin` (there are multiple compiler plugin declarations in `pom.xml`; preserve existing wiring when editing)
- **Java Version**: 21 (records available, virtual threads ready)
- **IDE Setup**: Enable annotation processing

## Common Commands for Rapid Development

```bash
# Fresh start
rm -rf target/ && ./mvnw clean compile

# Watch mode (requires spring-boot-devtools)
./mvnw spring-boot:run

# Format & check
./mvnw spotless:apply  # (if configured)

# Run single test
./mvnw test -Dtest=UserServiceTests

# Generate API docs
curl http://localhost:8080/api/docs > openapi.json
```

---

**Remember**: This is a healthcare system. Prioritize data safety (audit trails, soft deletes), validation (input + business rules), and security (JWT, proper exception handling). Always test with the PostgreSQL container running.

