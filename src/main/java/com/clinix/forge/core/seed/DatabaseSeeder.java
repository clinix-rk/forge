package com.clinix.forge.core.seed;

import com.clinix.forge.catalog.complains.ComplainCategoryEntity;
import com.clinix.forge.catalog.complains.ComplainCategoryRepository;
import com.clinix.forge.catalog.dosages.DrugDosageEntity;
import com.clinix.forge.catalog.dosages.DrugDosageRepository;
import com.clinix.forge.catalog.medicines.MedicineEntity;
import com.clinix.forge.catalog.medicines.MedicineRepository;
import com.clinix.forge.catalog.treatments.TreatmentCategoryEntity;
import com.clinix.forge.catalog.treatments.TreatmentCategoryRepository;
import com.clinix.forge.doctors.DoctorEntity;
import com.clinix.forge.doctors.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final DoctorRepository doctorRepository;
    private final ComplainCategoryRepository complainCategoryRepository;
    private final TreatmentCategoryRepository treatmentCategoryRepository;
    private final DrugDosageRepository drugDosageRepository;
    private final MedicineRepository medicineRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Checking database seed status...");

        // 1. Doctors
        if (doctorRepository.count() == 0) {
            log.info("Seeding doctors...");
            doctorRepository.save(DoctorEntity.builder().name("Dr. Yogesh Trivedi").caseNoPrefix("Y").build());
            doctorRepository.save(DoctorEntity.builder().name("Dr. Mehula Trivedi").caseNoPrefix("M").build());
            doctorRepository.save(DoctorEntity.builder().name("Dr. Vaidehi Trivedi").caseNoPrefix("v").build());
        }

        // 2. Complain Categories
        if (complainCategoryRepository.count() == 0) {
            log.info("Seeding complain categories...");
            ComplainCategoryEntity pain = complainCategoryRepository.save(
                    ComplainCategoryEntity.builder().name("Pain").build()
            );
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Continues").parent(pain).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Intermittent").parent(pain).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Dull").parent(pain).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Sharp").parent(pain).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Nocturnal").parent(pain).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Radiating").parent(pain).build());

            ComplainCategoryEntity referred = complainCategoryRepository.save(
                    ComplainCategoryEntity.builder().name("Reffered").parent(pain).build()
            );
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Ear").parent(referred).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("TMJ").parent(referred).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Maxillary Sinus").parent(referred).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Temporal Region").parent(referred).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Neck").parent(referred).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Other Arch").parent(referred).build());

            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Localized").parent(pain).build());

            ComplainCategoryEntity swelling = complainCategoryRepository.save(
                    ComplainCategoryEntity.builder().name("Swelling").build()
            );
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Generalized").parent(swelling).build());

            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Foul Smell").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Tarter").build());

            ComplainCategoryEntity clicking = complainCategoryRepository.save(
                    ComplainCategoryEntity.builder().name("Clicking sound in TMJ").build()
            );
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Unilateral").parent(clicking).build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Bilateral").parent(clicking).build());

            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Irregular teeth").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Caries").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Missing Tooth").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Mobility in Teeth").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Fractured Teeth").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Trauma").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Bleeding Gums").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Asthetic Complain").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("General Check up").build());
            complainCategoryRepository.save(ComplainCategoryEntity.builder().name("Others").build());
        }

        // 3. Treatment Categories
        if (treatmentCategoryRepository.count() == 0) {
            log.info("Seeding treatment categories...");
            TreatmentCategoryEntity rct = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("RCT").build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("RCO").parent(rct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("BMP").parent(rct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("RCD").parent(rct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("RCF").parent(rct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("PRCF").parent(rct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("CAST CORE").parent(rct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("TEMP. CAP").parent(rct).build());

            TreatmentCategoryEntity prosthesisRct = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("PROSTHESIS OF RCT").build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("NI-CR FCC").parent(prosthesisRct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("PFM FCC").parent(prosthesisRct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("FZ FCC").parent(prosthesisRct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("PFZ FCC").parent(prosthesisRct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("PFZ+FCC").parent(prosthesisRct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("LAVA").parent(prosthesisRct).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("GOLD").parent(prosthesisRct).build());

            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("SC").build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("DEEP SC").build());

            TreatmentCategoryEntity filling = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("FILLING").build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("CLASS 1").parent(filling).build());
            TreatmentCategoryEntity class2 = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("CLASS 2").parent(filling).build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("MO").parent(class2).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("DO").parent(class2).build());

            TreatmentCategoryEntity class3 = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("CLASS 3").parent(filling).build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("MO").parent(class3).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("DO").parent(class3).build());

            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("CLASS 4").parent(filling).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("CLASS 5").parent(filling).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("CLASS 6").parent(filling).build());

            TreatmentCategoryEntity fd = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("FD").build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("PI").parent(fd).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("FI").parent(fd).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("MR").parent(fd).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("DD").parent(fd).build());

            TreatmentCategoryEntity pd = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("PD").build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("PI").parent(pd).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("FI").parent(pd).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("MR").parent(pd).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("DD").parent(pd).build());

            TreatmentCategoryEntity xray = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("XRAY").build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("IOPA").parent(xray).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("OPG").parent(xray).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("CBCT").parent(xray).build());

            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("SCAN").build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("CO").build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("FOLLOW UP").build());

            TreatmentCategoryEntity extraction = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("EXTRACTION").build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("EXTRACTION").parent(extraction).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("DISIMPACTION").parent(extraction).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("SR").parent(extraction).build());

            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("GINGIVECTOMY").build());

            TreatmentCategoryEntity implant = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("IMPLANT").build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("DSL").parent(implant).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("ISL").parent(implant).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("NORMAL").parent(implant).build());
            TreatmentCategoryEntity prosthesis = treatmentCategoryRepository.save(
                    TreatmentCategoryEntity.builder().name("PROSTHESIS").parent(implant).build()
            );
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("TEMPORARY").parent(prosthesis).build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("PERMENENT").parent(prosthesis).build());

            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("ORTHODONTIA").build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("NIGHT GUARD").build());
            treatmentCategoryRepository.save(TreatmentCategoryEntity.builder().name("PEDO. TREATMENT").build());
        }

        // 4. Drug Dosages
        if (drugDosageRepository.count() == 0) {
            log.info("Seeding drug dosages...");
            drugDosageRepository.save(DrugDosageEntity.builder().dosage("1-1-1").build());
            drugDosageRepository.save(DrugDosageEntity.builder().dosage("1-0-1").build());
            drugDosageRepository.save(DrugDosageEntity.builder().dosage("2-1-1").build());
            drugDosageRepository.save(DrugDosageEntity.builder().dosage("1 HS").build());
        }

        // 5. Medicines
        if (medicineRepository.count() == 0) {
            log.info("Seeding medicines...");
            // ANALGESICS
            String[] analgesics = {"Dan-P", "Diclogesic", "Enzoflam", "Keterol-DT 50 mg", "Contramal DT 50 mg", "Ultracet", "Dolo-650", "Combiflam Syrup", "Diclogesic Syrup"};
            for (String name : analgesics) {
                medicineRepository.save(MedicineEntity.builder().name(name).type("ANALGESICS").instruction("Before Meal").build());
            }

            // ANTIBIOTICS
            String[] antibiotics = {"Augmentin 625 mg", "Zifi CV 200 mg", "Metrogyl 400 mg", "Merogyl 200 mg", "Droxyl 500 mg", "Droxyl 250 mg"};
            for (String name : antibiotics) {
                medicineRepository.save(MedicineEntity.builder().name(name).type("ANTIBIOTICS").instruction("Before Meal").build());
            }

            // ANTACIDS
            String[] antacids = {"PAN-D", "Veloz-D", "Rantac 150 mg", "Spolac -DS 120 M"};
            for (String name : antacids) {
                medicineRepository.save(MedicineEntity.builder().name(name).type("ANTACIDS").instruction("Before Meal").build());
            }

            // LOCAL APPLICATION
            String[] localApp = {"Dologel CT", "Metrogyl 1% gel", "Metrogyl 2% Gel", "Conacort Paste", "Hexigel 1%", "Thermoseal RA", "Sensofoam Gum Paint", "Stolin Gum Paint"};
            for (String name : localApp) {
                medicineRepository.save(MedicineEntity.builder().name(name).type("LOCAL APPLICATION").instruction("Before Meal").build());
            }

            // TOOTH PASTE
            String[] toothPaste = {"Enaflix Toothpaste", "Sensodyne"};
            for (String name : toothPaste) {
                medicineRepository.save(MedicineEntity.builder().name(name).type("TOOTH PASTE").instruction("Before Meal").build());
            }

            // MOUTH WASH
            String[] mouthWash = {"Betadine 2% Gargle Mint", "Hexidine Mouthwash", "Colgate maxFresh Plax antibactarial Mouthwash"};
            for (String name : mouthWash) {
                medicineRepository.save(MedicineEntity.builder().name(name).type("MOUTH WASH").instruction("Before Meal").build());
            }
        }

        log.info("Database seeding completed.");
    }
}
