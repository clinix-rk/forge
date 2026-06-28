package com.clinix.forge.appointment;

import com.clinix.forge.appointment.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for managing AppointmentEntity objects.
 */
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    Optional<AppointmentEntity> findByPatientIdAndDatetime(Long patientId, LocalDateTime datetime);
}
