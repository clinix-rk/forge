package com.clinix.forge.finance;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.finance.dto.CreatePaymentRequest;
import com.clinix.forge.finance.dto.PaymentResponse;
import com.clinix.forge.finance.dto.UpdatePaymentRequest;
import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.finance.entity.PaymentMethod;
import com.clinix.forge.finance.entity.ReciptEntity;
import com.clinix.forge.treatment.repository.TreatmentRepository;
import com.clinix.forge.treatment.entity.TreatmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReciptRepository reciptRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private ReciptEntity reciptEntity;
    private TreatmentEntity treatmentEntity;
    private PaymentEntity paymentEntity;
    private PaymentResponse paymentResponse;
    private CreatePaymentRequest createRequest;
    private UpdatePaymentRequest updateRequest;

    @BeforeEach
    public void setUp() {
        reciptEntity = ReciptEntity.builder().build();
        reciptEntity.setId(1L);

        treatmentEntity = TreatmentEntity.builder().build();
        treatmentEntity.setId(2L);

        paymentEntity = PaymentEntity.builder()
                .recipt(reciptEntity)
                .treatment(treatmentEntity)
                .amount(500.0)
                .method(PaymentMethod.CASH)
                .reference("REF1")
                .build();
        paymentEntity.setId(10L);

        paymentResponse = new PaymentResponse(10L, 1L, 2L, 500.0, PaymentMethod.CASH, "REF1", Instant.now(), Instant.now());
        createRequest = new CreatePaymentRequest(1L, 2L, 500.0, PaymentMethod.CASH, "REF1");
        updateRequest = new UpdatePaymentRequest(600.0, PaymentMethod.ONLINE, "REF2");
    }

    @Test
    public void createPayment_Success() {
        when(reciptRepository.findById(1L)).thenReturn(Optional.of(reciptEntity));
        when(treatmentRepository.findById(2L)).thenReturn(Optional.of(treatmentEntity));
        when(paymentRepository.findByTreatmentId(2L)).thenReturn(Optional.empty());
        when(paymentMapper.toEntity(createRequest)).thenReturn(paymentEntity);
        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(paymentEntity);
        when(paymentMapper.toResponse(paymentEntity)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.createPayment(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        verify(paymentRepository).save(any(PaymentEntity.class));
    }

    @Test
    public void createPayment_ReciptNotFound() {
        when(reciptRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPayment(createRequest));
    }

    @Test
    public void createPayment_TreatmentNotFound() {
        when(reciptRepository.findById(1L)).thenReturn(Optional.of(reciptEntity));
        when(treatmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPayment(createRequest));
    }

    @Test
    public void getPaymentById_Success() {
        when(paymentRepository.findById(10L)).thenReturn(Optional.of(paymentEntity));
        when(paymentMapper.toResponse(paymentEntity)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.getPaymentById(10L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    public void getPaymentById_NotFound() {
        when(paymentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(10L));
    }

    @Test
    public void getAllPayments_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PaymentEntity> page = new PageImpl<>(List.of(paymentEntity));
        when(paymentRepository.findAll(pageRequest)).thenReturn(page);
        when(paymentMapper.toResponse(paymentEntity)).thenReturn(paymentResponse);

        PaginatedPayload<PaymentResponse> result = paymentService.getAllPayments(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updatePayment_Success() {
        when(paymentRepository.findById(10L)).thenReturn(Optional.of(paymentEntity));
        doNothing().when(paymentMapper).updateEntityFromRequest(updateRequest, paymentEntity);
        when(paymentRepository.save(paymentEntity)).thenReturn(paymentEntity);
        when(paymentMapper.toResponse(paymentEntity)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.updatePaymentById(10L, updateRequest);
        assertThat(result).isNotNull();
        verify(paymentRepository).save(paymentEntity);
    }

    @Test
    public void deletePayment_Success() {
        when(paymentRepository.existsById(10L)).thenReturn(true);
        doNothing().when(paymentRepository).deleteById(10L);

        paymentService.deletePaymentById(10L);
        verify(paymentRepository).deleteById(10L);
    }

    @Test
    public void deletePayment_NotFound() {
        when(paymentRepository.existsById(10L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> paymentService.deletePaymentById(10L));
    }
}
