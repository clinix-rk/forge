package com.clinix.forge.features;

import com.clinix.forge.complain.repository.ComplainCategoryRepository;
import com.clinix.forge.complain.repository.ComplainRepository;
import com.clinix.forge.complain.entity.ComplainCategoryEntity;
import com.clinix.forge.complain.entity.ComplainEntity;
import com.clinix.forge.doctors.DoctorEntity;
import com.clinix.forge.doctors.DoctorRepository;
import com.clinix.forge.finance.PaymentRepository;
import com.clinix.forge.finance.ReciptRepository;
import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.finance.entity.PaymentMethod;
import com.clinix.forge.finance.entity.ReciptEntity;
import com.clinix.forge.patient.entity.Gender;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.prescription.PrescriptionRepository;
import com.clinix.forge.prescription.entity.PrescriptionEntity;
import com.clinix.forge.suggestion.SuggestionRepository;
import com.clinix.forge.suggestion.entity.SuggestionEntity;
import com.clinix.forge.suggestion.entity.SuggestionStatus;
import com.clinix.forge.treatment.repository.TreatmentCategoryRepository;
import com.clinix.forge.treatment.repository.TreatmentRepository;
import com.clinix.forge.treatment.entity.TreatmentCategoryEntity;
import com.clinix.forge.treatment.entity.TreatmentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NewFeatureRepositoryTests {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ComplainCategoryRepository complainCategoryRepository;

    @Autowired
    private ComplainRepository complainRepository;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private TreatmentCategoryRepository treatmentCategoryRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReciptRepository reciptRepository;

    @Test
    public void testDoctorNameSearch() {
        DoctorEntity d1 = doctorRepository.save(DoctorEntity.builder().name("Dr. Christopher Smith").caseNoPrefix("C").totalPatients(0).build());
        DoctorEntity d2 = doctorRepository.save(DoctorEntity.builder().name("Dr. Alice Jones").caseNoPrefix("A").totalPatients(0).build());

        List<DoctorEntity> results = doctorRepository.findByNameContainingIgnoreCase("christopher");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Dr. Christopher Smith");

        results = doctorRepository.findByNameContainingIgnoreCase("smith");
        assertThat(results).hasSize(1);

        results = doctorRepository.findByNameContainingIgnoreCase("DR.");
        assertThat(results).hasSize(2);
    }

    @Test
    public void testPatientIdFiltering() {
        DoctorEntity doctor = doctorRepository.save(DoctorEntity.builder().name("Dr. Test").caseNoPrefix("T").totalPatients(0).build());
        PatientEntity patient = patientRepository.save(PatientEntity.builder()
                .caseNo("T-1")
                .serial(1)
                .name("Jane Doe")
                .gender(Gender.FEMALE)
                .doctor(doctor)
                .build());

        // Complain
        ComplainCategoryEntity category = complainCategoryRepository.save(ComplainCategoryEntity.builder().name("TestCategory").build());
        complainRepository.save(ComplainEntity.builder().date(LocalDate.now()).details("Detail").category(category).patient(patient).build());

        Page<ComplainEntity> complainPage = complainRepository.findByPatientId(patient.getId(), PageRequest.of(0, 10));
        assertThat(complainPage.getContent()).hasSize(1);

        // Suggestion
        suggestionRepository.save(SuggestionEntity.builder().date(LocalDate.now()).category("Category").details("Details").cost(100).status(SuggestionStatus.SUGGESTED).patient(patient).build());
        Page<SuggestionEntity> suggestionPage = suggestionRepository.findByPatientId(patient.getId(), PageRequest.of(0, 10));
        assertThat(suggestionPage.getContent()).hasSize(1);

        // Treatment
        TreatmentCategoryEntity tCat = treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("TCat").build());
        TreatmentEntity treatment = treatmentRepository.save(TreatmentEntity.builder().date(LocalDate.now()).details("Details").category(tCat).patient(patient).build());
        Page<TreatmentEntity> treatmentPage = treatmentRepository.findByPatientId(patient.getId(), PageRequest.of(0, 10));
        assertThat(treatmentPage.getContent()).hasSize(1);

        // Prescription
        prescriptionRepository.save(PrescriptionEntity.builder().date(LocalDate.now()).details("PrescDetails").patient(patient).build());
        Page<PrescriptionEntity> prescriptionPage = prescriptionRepository.findByPatientId(patient.getId(), PageRequest.of(0, 10));
        assertThat(prescriptionPage.getContent()).hasSize(1);

        // Receipt & Payment
        ReciptEntity recipt = reciptRepository.save(ReciptEntity.builder().doctorIdentityCharacter("T").financialYear("2026-2027").serial(1).build());
        PaymentEntity payment = paymentRepository.save(PaymentEntity.builder().recipt(recipt).treatment(treatment).amount(500.0).method(PaymentMethod.CASH).reference("REF-1").build());

        Page<PaymentEntity> paymentPage = paymentRepository.findByPatientId(patient.getId(), PageRequest.of(0, 10));
        assertThat(paymentPage.getContent()).hasSize(1);

        Page<ReciptEntity> reciptPage = reciptRepository.findByPatientId(patient.getId(), PageRequest.of(0, 10));
        assertThat(reciptPage.getContent()).hasSize(1);
    }

    @Test
    public void testEnrichedPaymentsDynamicQuery() {
        DoctorEntity doctor = doctorRepository.save(DoctorEntity.builder().name("Dr. Test").caseNoPrefix("T").totalPatients(0).build());
        PatientEntity patient = patientRepository.save(PatientEntity.builder()
                .caseNo("T-1")
                .serial(1)
                .name("Jane Doe")
                .gender(Gender.FEMALE)
                .doctor(doctor)
                .build());

        TreatmentCategoryEntity tCat = treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("TCat").build());
        TreatmentEntity treatment = treatmentRepository.save(TreatmentEntity.builder().date(LocalDate.of(2026, 6, 1)).details("Details").category(tCat).patient(patient).build());

        ReciptEntity recipt = reciptRepository.save(ReciptEntity.builder().doctorIdentityCharacter("T").financialYear("2026-2027").serial(1).build());
        paymentRepository.save(PaymentEntity.builder().recipt(recipt).treatment(treatment).amount(500.0).method(PaymentMethod.CASH).reference("REF-12345").build());

        // 1. Filter by method
        Page<PaymentEntity> results = paymentRepository.findEnrichedPayments(PaymentMethod.CASH, null, null, null, PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(1);

        results = paymentRepository.findEnrichedPayments(PaymentMethod.ONLINE, null, null, null, PageRequest.of(0, 10));
        assertThat(results.getContent()).isEmpty();

        // 2. Filter by date range
        results = paymentRepository.findEnrichedPayments(null, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 30), null, PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(1);

        results = paymentRepository.findEnrichedPayments(null, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 30), null, PageRequest.of(0, 10));
        assertThat(results.getContent()).isEmpty();

        // 3. Search text (patient name, caseNo, or reference)
        results = paymentRepository.findEnrichedPayments(null, null, null, "Jane", PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(1);

        results = paymentRepository.findEnrichedPayments(null, null, null, "T-1", PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(1);

        results = paymentRepository.findEnrichedPayments(null, null, null, "REF-123", PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(1);

        results = paymentRepository.findEnrichedPayments(null, null, null, "NonExistent", PageRequest.of(0, 10));
        assertThat(results.getContent()).isEmpty();
    }
}
