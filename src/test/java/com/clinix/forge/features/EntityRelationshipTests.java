package com.clinix.forge.features;

import com.clinix.forge.appointment.AppointmentRepository;
import com.clinix.forge.appointment.entity.AppointmentEntity;
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
import com.clinix.forge.patient.entity.MedicalConditionEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.entity.PhoneNumberEntity;
import com.clinix.forge.patient.entity.PhoneType;
import com.clinix.forge.patient.entity.DrugAllergyEntity;
import com.clinix.forge.patient.repositories.MedicalConditionRepository;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.patient.repositories.PhoneNumberRepository;
import com.clinix.forge.patient.repositories.DrugAllergyRepository;
import com.clinix.forge.prescription.DrugDosageRepository;
import com.clinix.forge.prescription.MedicineRepository;
import com.clinix.forge.prescription.PrescriptionMedicineRepository;
import com.clinix.forge.prescription.PrescriptionRepository;
import com.clinix.forge.prescription.entity.DrugDosageEntity;
import com.clinix.forge.prescription.entity.MedicineEntity;
import com.clinix.forge.prescription.entity.PrescriptionEntity;
import com.clinix.forge.prescription.entity.PrescriptionMedicineEntity;
import com.clinix.forge.storage.FileRepository;
import com.clinix.forge.storage.entity.FileEntity;
import com.clinix.forge.suggestion.SuggestionRepository;
import com.clinix.forge.suggestion.entity.SuggestionEntity;
import com.clinix.forge.suggestion.entity.SuggestionStatus;
import com.clinix.forge.treatment.repository.TreatmentCategoryRepository;
import com.clinix.forge.treatment.repository.TreatmentRepository;
import com.clinix.forge.treatment.entity.TreatmentCategoryEntity;
import com.clinix.forge.treatment.entity.TreatmentEntity;
import com.clinix.forge.user.UserRepository;
import com.clinix.forge.user.entity.Role;
import com.clinix.forge.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EntityRelationshipTests {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalConditionRepository medicalConditionRepository;

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Autowired
    private DrugAllergyRepository drugAllergyRepository;

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
    private DrugDosageRepository drugDosageRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PrescriptionMedicineRepository prescriptionMedicineRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReciptRepository reciptRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndPersistAllEntitiesAndRelationships() {
        // 1. Doctor
        DoctorEntity doctor = DoctorEntity.builder()
                .name("Dr. Smith")
                .caseNoPrefix("S")
                .totalPatients(0)
                .build();
        doctor = doctorRepository.save(doctor);
        assertThat(doctor.getId()).isNotNull();

        // 2. Medical Condition & Drug Allergy
        MedicalConditionEntity condition = MedicalConditionEntity.builder()
                .name("Hypertension")
                .build();
        condition = medicalConditionRepository.save(condition);
        assertThat(condition.getId()).isNotNull();

        DrugAllergyEntity allergy = DrugAllergyEntity.builder()
                .name("Penicillin")
                .build();
        allergy = drugAllergyRepository.save(allergy);
        assertThat(allergy.getId()).isNotNull();

        // 3. Patient
        PatientEntity patient = PatientEntity.builder()
                .caseNo("S-100")
                .serial(100)
                .name("John Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .email("john.doe@example.com")
                .doctor(doctor)
                .medicalConditions(Set.of(condition))
                .drugAllergies(Set.of(allergy))
                .build();
        patient = patientRepository.save(patient);
        assertThat(patient.getId()).isNotNull();
        assertThat(patient.getMedicalConditions()).contains(condition);
        assertThat(patient.getDrugAllergies()).contains(allergy);

        // 3a. Patient Phone Number
        PhoneNumberEntity primaryPhone = PhoneNumberEntity.builder()
                .phoneNumber("1234567890")
                .type(PhoneType.PRIMARY)
                .patient(patient)
                .build();
        primaryPhone = phoneNumberRepository.save(primaryPhone);
        assertThat(primaryPhone.getId()).isNotNull();

        // 4. User
        UserEntity user = UserEntity.builder()
                .username("admin_user")
                .password("secure_pwd")
                .role(Role.ADMIN)
                .build();
        user = userRepository.save(user);
        assertThat(user.getId()).isNotNull();

        // 5. Complain Category & Complain
        ComplainCategoryEntity parentCategory = ComplainCategoryEntity.builder()
                .name("General")
                .build();
        parentCategory = complainCategoryRepository.save(parentCategory);

        ComplainCategoryEntity childCategory = ComplainCategoryEntity.builder()
                .name("Headache")
                .parent(parentCategory)
                .build();
        childCategory = complainCategoryRepository.save(childCategory);

        ComplainEntity complain = ComplainEntity.builder()
                .date(LocalDate.now())
                .details("Mild persistent headache")
                .category(childCategory)
                .patient(patient)
                .build();
        complain = complainRepository.save(complain);
        assertThat(complain.getId()).isNotNull();
        assertThat(complain.getCategory().getParent()).isEqualTo(parentCategory);

        // 6. Suggestion
        SuggestionEntity suggestion = SuggestionEntity.builder()
                .date(LocalDate.now())
                .category("Diet")
                .details("Reduce sodium intake")
                .cost(500)
                .status(SuggestionStatus.SUGGESTED)
                .patient(patient)
                .build();
        suggestion = suggestionRepository.save(suggestion);
        assertThat(suggestion.getId()).isNotNull();

        // 7. Treatment Category & Treatment
        TreatmentCategoryEntity treatmentCategory = TreatmentCategoryEntity.builder()
                .name("Therapy")
                .build();
        treatmentCategory = treatmentCategoryRepository.save(treatmentCategory);

        TreatmentEntity treatment = TreatmentEntity.builder()
                .date(LocalDate.now())
                .details("Physical therapy session")
                .category(treatmentCategory)
                .patient(patient)
                .build();
        treatment = treatmentRepository.save(treatment);
        assertThat(treatment.getId()).isNotNull();

        // 8. Drug Dosage, Medicine & Prescription
        DrugDosageEntity dosage = DrugDosageEntity.builder()
                .dosage("Once Daily")
                .build();
        dosage = drugDosageRepository.save(dosage);

        MedicineEntity medicine = MedicineEntity.builder()
                .name("Paracetamol")
                .type("Tablet")
                .instruction("Take after meals")
                .build();
        medicine = medicineRepository.save(medicine);

        PrescriptionEntity prescription = PrescriptionEntity.builder()
                .date(LocalDate.now())
                .details("Follow for 5 days")
                .patient(patient)
                .build();
        prescription = prescriptionRepository.save(prescription);

        PrescriptionMedicineEntity prescriptionMedicine = PrescriptionMedicineEntity.builder()
                .prescription(prescription)
                .medicine(medicine)
                .dosage(dosage)
                .quantity(10)
                .build();
        prescriptionMedicine = prescriptionMedicineRepository.save(prescriptionMedicine);
        assertThat(prescriptionMedicine.getId()).isNotNull();

        // 9. Recipt & Payment
        ReciptEntity recipt = ReciptEntity.builder()
                .doctorIdentityCharacter("S")
                .financialYear("2026-2027")
                .serial(1)
                .build();
        recipt = reciptRepository.save(recipt);

        PaymentEntity payment = PaymentEntity.builder()
                .recipt(recipt)
                .treatment(treatment)
                .amount(1500.0)
                .method(PaymentMethod.ONLINE)
                .reference("REF-123456")
                .build();
        payment = paymentRepository.save(payment);
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getRecipt()).isEqualTo(recipt);
        assertThat(payment.getTreatment()).isEqualTo(treatment);

        // 10. Appointment
        AppointmentEntity appointment = AppointmentEntity.builder()
                .patient(patient)
                .notes("Follow-up checkup")
                .datetime(LocalDateTime.now().plusDays(7))
                .build();
        appointment = appointmentRepository.save(appointment);
        assertThat(appointment.getId()).isNotNull();

        // 11. Storage File
        FileEntity file = FileEntity.builder()
                .patient(patient)
                .name("scan_john_doe.pdf")
                .location("/storage/scans/scan_john_doe.pdf")
                .build();
        file = fileRepository.save(file);
        assertThat(file.getId()).isNotNull();
        assertThat(file.getPatient()).isEqualTo(patient);

        // Fetch back and assert relations are correctly mapped
        Optional<PatientEntity> foundPatientOpt = patientRepository.findById(patient.getId());
        assertThat(foundPatientOpt).isPresent();
        PatientEntity foundPatient = foundPatientOpt.get();
        assertThat(foundPatient.getDoctor().getName()).isEqualTo("Dr. Smith");

        Optional<PaymentEntity> foundPayment = paymentRepository.findByTreatmentId(treatment.getId());
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getAmount()).isEqualTo(1500.0);
    }
}
