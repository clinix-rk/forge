package com.clinix.forge.patient;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.doctors.DoctorEntity;
import com.clinix.forge.doctors.DoctorRepository;
import com.clinix.forge.patient.dto.*;
import com.clinix.forge.patient.entity.*;
import com.clinix.forge.patient.repositories.DrugAllergyRepository;
import com.clinix.forge.patient.repositories.MedicalConditionRepository;
import com.clinix.forge.patient.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTests {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private MedicalConditionRepository medicalConditionRepository;

    @Mock
    private DrugAllergyRepository drugAllergyRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    private DoctorEntity doctor;
    private PatientEntity patient;
    private PatientResponse response;
    private CreatePatientRequest createRequest;
    private UpdatePatientRequest updateRequest;

    @BeforeEach
    public void setUp() {
        doctor = DoctorEntity.builder()
                .name("Dr. House")
                .caseNoPrefix("H")
                .totalPatients(10)
                .build();
        doctor.setId(2L);

        patient = PatientEntity.builder()
                .name("Gregory Patient")
                .caseNo("H11")
                .serial(11)
                .doctor(doctor)
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .gender(Gender.MALE)
                .build();
        patient.setId(1L);

        PhoneNumberResponse phoneResponse = new PhoneNumberResponse(1L, "1234567890", PhoneType.PRIMARY, java.time.Instant.now(), java.time.Instant.now());
        response = new PatientResponse(
                1L, "H11", "Gregory Patient", LocalDate.of(1980, 1, 1),
                Gender.MALE, "gregory@example.com", "Baker St", "London",
                "NW1", "Referred", List.of(phoneResponse),
                Set.of("Hypertension"), Set.of("Penicillin"), null, null
        );

        PhoneNumberRequest phoneReq = new PhoneNumberRequest("1234567890", PhoneType.PRIMARY);
        createRequest = new CreatePatientRequest(
                2L, "Gregory Patient", LocalDate.of(1980, 1, 1),
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
    }

    @Test
    public void testCreatePatient_Success() {
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(patientMapper.toEntity(createRequest)).thenReturn(patient);
        when(patientMapper.toPhoneEntity(any(PhoneNumberRequest.class)))
                .thenReturn(PhoneNumberEntity.builder().phoneNumber("1234567890").type(PhoneType.PRIMARY).build());
        when(patientRepository.save(any(PatientEntity.class))).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        // Mock nested condition/allergy repos
        when(medicalConditionRepository.findByNameIgnoreCase("Hypertension"))
                .thenReturn(Optional.of(MedicalConditionEntity.builder().name("Hypertension").build()));
        when(drugAllergyRepository.findByNameIgnoreCase("Penicillin"))
                .thenReturn(Optional.of(DrugAllergyEntity.builder().name("Penicillin").build()));

        PatientResponse created = patientService.createPatient(createRequest);

        assertThat(created).isNotNull();
        assertThat(created.caseNo()).isEqualTo("H11");
        assertThat(doctor.getTotalPatients()).isEqualTo(11);

        verify(doctorRepository, times(1)).save(doctor);
        verify(patientRepository, times(1)).save(any(PatientEntity.class));
    }

    @Test
    public void testCreatePatient_DoctorNotFound() {
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createPatient(createRequest);
        });

        verify(patientRepository, never()).save(any());
    }

    @Test
    public void testGetPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PatientResponse found = patientService.getPatientById(1L);

        assertThat(found).isNotNull();
        assertThat(found.id()).isEqualTo(1L);
    }

    @Test
    public void testGetPatientById_NotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientById(1L);
        });
    }

    @Test
    public void testGetPatientByCaseNo_Success() {
        when(patientRepository.findByCaseNo("H11")).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PatientResponse found = patientService.getPatientByCaseNo("H11");

        assertThat(found).isNotNull();
        assertThat(found.caseNo()).isEqualTo("H11");
    }

    @Test
    public void testGetPatientByCaseNo_NotFound() {
        when(patientRepository.findByCaseNo("H11")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientByCaseNo("H11");
        });
    }

    @Test
    public void testSearchPatients_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PatientEntity> patientPage = new PageImpl<>(List.of(patient), pageRequest, 1);

        when(patientRepository.searchPatients("Gregory", null, null, pageRequest))
                .thenReturn(patientPage);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PaginatedPayload<PatientResponse> payload = patientService.searchPatients(
                "Gregory", null, null, 0, 10);

        assertThat(payload).isNotNull();
        assertThat(payload.items()).hasSize(1);
        assertThat(payload.totalElements()).isEqualTo(1);
        assertThat(payload.pageNumber()).isEqualTo(0);
    }

    @Test
    public void testUpdatePatient_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toPhoneEntity(any(PhoneNumberRequest.class)))
                .thenReturn(PhoneNumberEntity.builder().phoneNumber("1234567890").type(PhoneType.PRIMARY).build());
        when(medicalConditionRepository.findByNameIgnoreCase("Diabetes"))
                .thenReturn(Optional.of(MedicalConditionEntity.builder().name("Diabetes").build()));
        when(drugAllergyRepository.findByNameIgnoreCase("Aspirin"))
                .thenReturn(Optional.of(DrugAllergyEntity.builder().name("Aspirin").build()));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PatientResponse updated = patientService.updatePatientById(1L, updateRequest);

        assertThat(updated).isNotNull();
        verify(patientMapper, times(1)).updateEntityFromRequest(updateRequest, patient);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    public void testUpdatePatient_PhoneNumbersInPlaceUpdate() {
        // Set up existing phone number in patient
        PhoneNumberEntity existingPhone = PhoneNumberEntity.builder()
                .phoneNumber("9999999999")
                .type(PhoneType.PRIMARY)
                .patient(patient)
                .build();
        existingPhone.setId(10L);
        patient.setPhoneNumbers(new HashSet<>(Set.of(existingPhone)));

        // Create update request with updated phone number for same type (PRIMARY)
        PhoneNumberRequest updatedPhoneReq = new PhoneNumberRequest("1234567890", PhoneType.PRIMARY);
        UpdatePatientRequest updateReq = new UpdatePatientRequest(
                "Gregory Updated", LocalDate.of(1980, 1, 1),
                Gender.MALE, "gregory@example.com", "Baker St", "London",
                "NW1", "Referred", List.of(updatedPhoneReq),
                Set.of("Diabetes"), Set.of("Aspirin")
        );

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicalConditionRepository.findByNameIgnoreCase("Diabetes"))
                .thenReturn(Optional.of(MedicalConditionEntity.builder().name("Diabetes").build()));
        when(drugAllergyRepository.findByNameIgnoreCase("Aspirin"))
                .thenReturn(Optional.of(DrugAllergyEntity.builder().name("Aspirin").build()));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PatientResponse updated = patientService.updatePatientById(1L, updateReq);

        assertThat(updated).isNotNull();
        // Assert phone number set is updated in place
        assertThat(patient.getPhoneNumbers()).hasSize(1);
        PhoneNumberEntity phone = patient.getPhoneNumbers().iterator().next();
        assertThat(phone.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(phone.getId()).isEqualTo(10L); // Verify ID did not change

        // Verify mapstruct toPhoneEntity was not called since we updated in place
        verify(patientMapper, never()).toPhoneEntity(any());
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    public void testDeletePatient_Success() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        patientService.deletePatientById(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeletePatient_NotFound() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deletePatientById(1L);
        });

        verify(patientRepository, never()).deleteById(anyLong());
    }
}
