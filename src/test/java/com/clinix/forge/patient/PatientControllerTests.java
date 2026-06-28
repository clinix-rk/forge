package com.clinix.forge.patient;

import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.dto.*;
import com.clinix.forge.patient.entity.Gender;
import com.clinix.forge.patient.entity.PhoneType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PatientControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private PatientService patientService;

    private CreatePatientRequest createRequest;
    private UpdatePatientRequest updateRequest;
    private PatientResponse patientResponse;

    @BeforeEach
    public void setUp() {
        PhoneNumberRequest phoneReq = new PhoneNumberRequest("+1234567890", PhoneType.PRIMARY);
        createRequest = new CreatePatientRequest(
                1L, "Gregory Patient", LocalDate.of(1980, 1, 1),
                Gender.MALE, "gregory@example.com", "Baker St", "London",
                "NW1", "Referred", List.of(phoneReq),
                Set.of("Hypertension"), Set.of("Penicillin")
        );

        updateRequest = new UpdatePatientRequest(
                "Gregory Updated", LocalDate.of(1980, 1, 1),
                Gender.MALE, "gregory@example.com", "Baker St", "London",
                "NW1", "Referred", List.of(phoneReq),
                Set.of("Diabetes"), Set.of("Aspirin")
        );

        PhoneNumberResponse phoneResp = new PhoneNumberResponse(1L, "+1234567890", PhoneType.PRIMARY, java.time.Instant.now(), java.time.Instant.now());
        patientResponse = new PatientResponse(
                1L, "H11", "Gregory Patient", LocalDate.of(1980, 1, 1),
                Gender.MALE, "gregory@example.com", "Baker St", "London",
                "NW1", "Referred", List.of(phoneResp),
                Set.of("Hypertension"), Set.of("Penicillin"), null, null
        );
    }

    @Test
    public void testAddPatient_Success() throws Exception {
        when(patientService.createPatient(any(CreatePatientRequest.class))).thenReturn(patientResponse);

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Gregory Patient"))
                .andExpect(jsonPath("$.data.caseNo").value("H11"));
    }

    @Test
    public void testAddPatient_ValidationFailure() throws Exception {
        CreatePatientRequest invalidRequest = new CreatePatientRequest(
                -10L, "", null, null, "invalid-email", null, null, null, null, List.of(), null, null
        );

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testGetPatientById_Success() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(patientResponse);

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.caseNo").value("H11"));
    }

    @Test
    public void testGetPatientByCaseNo_Success() throws Exception {
        when(patientService.getPatientByCaseNo("H11")).thenReturn(patientResponse);

        mockMvc.perform(get("/patients/case-no/H11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.caseNo").value("H11"));
    }

    @Test
    public void testSearchPatients_Success() throws Exception {
        PaginatedPayload<PatientResponse> paginatedPayload = new PaginatedPayload<>(
                List.of(patientResponse), 0, 10, 1, 1, true
        );

        when(patientService.searchPatients(eq("Gregory"), eq("+1234567890"), eq("H11"), eq(0), eq(10)))
                .thenReturn(paginatedPayload);

        mockMvc.perform(get("/patients")
                        .param("pageNo", "0")
                        .param("pageSize", "10")
                        .param("name", "Gregory")
                        .param("phoneNo", "+1234567890")
                        .param("caseNo", "H11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].name").value("Gregory Patient"));
    }

    @Test
    public void testUpdatePatient_Success() throws Exception {
        when(patientService.updatePatientById(eq(1L), any(UpdatePatientRequest.class))).thenReturn(patientResponse);

        mockMvc.perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    public void testDeletePatient_Success() throws Exception {
        mockMvc.perform(delete("/patients/1"))
                .andExpect(status().isNoContent());
    }
}
