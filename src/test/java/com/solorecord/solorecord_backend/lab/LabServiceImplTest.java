package com.solorecord.solorecord_backend.lab;

import com.solorecord.solorecord_backend.events.publisher.EventPublisher;
import com.solorecord.solorecord_backend.identity.data.model.AccountStatus;
import com.solorecord.solorecord_backend.identity.dto.response.UserResponse;
import com.solorecord.solorecord_backend.identity.service.UserService;
import com.solorecord.solorecord_backend.lab.data.model.OrderStatus;
import com.solorecord.solorecord_backend.lab.data.model.ResultStatus;
import com.solorecord.solorecord_backend.lab.data.model.TestOrder;
import com.solorecord.solorecord_backend.lab.data.model.TestResult;
import com.solorecord.solorecord_backend.lab.data.model.TestType;
import com.solorecord.solorecord_backend.lab.data.repository.TestOrderRepository;
import com.solorecord.solorecord_backend.lab.data.repository.TestResultRepository;
import com.solorecord.solorecord_backend.lab.dto.request.LabInitiatedResultRequest;
import com.solorecord.solorecord_backend.lab.dto.request.ResultValueRequest;
import com.solorecord.solorecord_backend.lab.dto.request.TestOrderRequest;
import com.solorecord.solorecord_backend.lab.dto.response.TestOrderResponse;
import com.solorecord.solorecord_backend.lab.dto.response.TestResultResponse;
import com.solorecord.solorecord_backend.lab.exception.InvalidLabStateException;
import com.solorecord.solorecord_backend.lab.exception.InvalidReviewerException;
import com.solorecord.solorecord_backend.lab.exception.InvalidTestTypeException;
import com.solorecord.solorecord_backend.lab.exception.LabAccessDeniedException;
import com.solorecord.solorecord_backend.lab.exception.OrderNotFoundException;
import com.solorecord.solorecord_backend.lab.exception.PatientNotFoundException;
import com.solorecord.solorecord_backend.lab.exception.ResultNotFoundException;
import com.solorecord.solorecord_backend.lab.service.LabServiceImpl;
import com.solorecord.solorecord_backend.patient.service.AccessGrantService;
import com.solorecord.solorecord_backend.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabServiceImplTest {

    private final UUID doctorId = UUID.randomUUID();
    private final UUID techId = UUID.randomUUID();
    private final UUID facilityId = UUID.randomUUID();
    private final UUID patientId = UUID.randomUUID();

    private TestOrderRepository orderRepository;
    private TestResultRepository resultRepository;
    private AccessGrantService accessGrantService;
    private PatientService patientService;
    private UserService userService;
    private EventPublisher eventPublisher;
    private LabServiceImpl service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(TestOrderRepository.class);
        resultRepository = mock(TestResultRepository.class);
        accessGrantService = mock(AccessGrantService.class);
        patientService = mock(PatientService.class);
        userService = mock(UserService.class);
        eventPublisher = mock(EventPublisher.class);
        service = new LabServiceImpl(orderRepository, resultRepository, accessGrantService,
                patientService, userService, eventPublisher);
        when(resultRepository.save(any(TestResult.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(TestOrder.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void orderWithoutActiveGrantIsRejected() {
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(false);

        assertThrows(LabAccessDeniedException.class, () ->
                service.createOrder(doctorId, facilityId, new TestOrderRequest(patientId, TestType.MALARIA, null, null)));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void otherTypeRequiresNameAndNameIsStrippedAndLowerCased() {
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(true);

        assertThrows(InvalidTestTypeException.class, () ->
                service.createOrder(doctorId, facilityId, new TestOrderRequest(patientId, TestType.OTHER, "  ", null)));

        TestOrderResponse response = service.createOrder(doctorId, facilityId,
                new TestOrderRequest(patientId, TestType.OTHER, "  Blood Sugar ", null));

        assertEquals("blood sugar", response.otherTestName());
        assertEquals(OrderStatus.PENDING, response.status());
    }

    @Test
    void knownTestTypeIgnoresAnyOtherName() {
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(true);

        TestOrderResponse response = service.createOrder(doctorId, facilityId,
                new TestOrderRequest(patientId, TestType.MALARIA, "something", null));

        assertNull(response.otherTestName());
    }

    @Test
    void orderFromAnotherFacilityIsNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findByIdAndFacilityId(orderId, facilityId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                service.enterOrderResult(techId, facilityId, orderId, new ResultValueRequest("positive")));
    }

    @Test
    void orderResultCopiesPatientAndTypeFromOrderAndReviewerIsOrderingDoctor() {
        TestOrder order = new TestOrder(patientId, doctorId, facilityId, TestType.TYPHOID, null, null);
        when(orderRepository.findByIdAndFacilityId(any(), any())).thenReturn(Optional.of(order));

        TestResultResponse response = service.enterOrderResult(techId, facilityId, UUID.randomUUID(),
                new ResultValueRequest(" positive "));

        assertEquals(patientId, response.patientId());
        assertEquals(TestType.TYPHOID, response.testType());
        assertEquals(doctorId, response.reviewingDoctorId());
        assertEquals("positive", response.resultValue());
        assertEquals(ResultStatus.PENDING, response.status());
    }

    @Test
    void secondResultForSameOrderIsRejected() {
        TestOrder order = new TestOrder(patientId, doctorId, facilityId, TestType.TYPHOID, null, null);
        when(orderRepository.findByIdAndFacilityId(any(), any())).thenReturn(Optional.of(order));
        when(resultRepository.existsByTestOrderId(any())).thenReturn(true);

        assertThrows(InvalidLabStateException.class, () ->
                service.enterOrderResult(techId, facilityId, UUID.randomUUID(), new ResultValueRequest("positive")));
    }

    @Test
    void labInitiatedResultForUnknownPatientIsRejected() {
        when(patientService.hasCompletedProfile(patientId)).thenReturn(false);

        assertThrows(PatientNotFoundException.class, () ->
                service.enterLabInitiatedResult(techId, facilityId,
                        new LabInitiatedResultRequest(patientId, TestType.MALARIA, null, "negative")));
    }

    @Test
    void onlyTheEnteringTechCanCompleteAndCompletedResultIsImmutable() {
        TestResult result = new TestResult(patientId, techId, facilityId, null, TestType.MALARIA, null, "positive", null);
        when(resultRepository.findByIdAndFacilityId(any(), any())).thenReturn(Optional.of(result));

        assertThrows(ResultNotFoundException.class, () ->
                service.completeResult(UUID.randomUUID(), facilityId, UUID.randomUUID()));

        TestResultResponse done = service.completeResult(techId, facilityId, UUID.randomUUID());
        assertEquals(ResultStatus.COMPLETED, done.status());
        verify(eventPublisher).publish(any());

        assertThrows(InvalidLabStateException.class, () ->
                service.updateResultValue(techId, facilityId, UUID.randomUUID(), new ResultValueRequest("changed")));
        assertThrows(InvalidLabStateException.class, () ->
                service.completeResult(techId, facilityId, UUID.randomUUID()));
    }

    @Test
    void completingOrderResultFulfilsTheOrder() {
        TestOrder order = new TestOrder(patientId, doctorId, facilityId, TestType.MALARIA, null, null);
        UUID orderId = UUID.randomUUID();
        TestResult result = new TestResult(patientId, techId, facilityId, orderId, TestType.MALARIA, null, "positive", doctorId);
        when(resultRepository.findByIdAndFacilityId(any(), any())).thenReturn(Optional.of(result));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        service.completeResult(techId, facilityId, UUID.randomUUID());

        assertEquals(OrderStatus.FULFILLED, order.getStatus());
    }

    @Test
    void routingRequiresLabInitiatedCompletedResultAndActiveDoctorAtFacility() {
        UUID reviewer = UUID.randomUUID();
        TestResult pending = new TestResult(patientId, techId, facilityId, null, TestType.MALARIA, null, "positive", null);
        when(resultRepository.findByIdAndFacilityId(any(), any())).thenReturn(Optional.of(pending));

        assertThrows(InvalidLabStateException.class, () ->
                service.routeResult(facilityId, UUID.randomUUID(), reviewer));

        pending.markCompleted();
        when(userService.getUserByFacilityId(facilityId)).thenReturn(List.of(
                new UserResponse("Lab Person", "l@x.com", AccountStatus.ACTIVE, "LAB_TECHNICIAN", reviewer)));

        assertThrows(InvalidReviewerException.class, () ->
                service.routeResult(facilityId, UUID.randomUUID(), reviewer));

        when(userService.getUserByFacilityId(facilityId)).thenReturn(List.of(
                new UserResponse("Dr A", "d@x.com", AccountStatus.ACTIVE, "DOCTOR", reviewer)));

        TestResultResponse routed = service.routeResult(facilityId, UUID.randomUUID(), reviewer);
        assertEquals(reviewer, routed.reviewingDoctorId());
    }

    @Test
    void doctorResultReadRequiresActiveGrant() {
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(false);

        assertThrows(LabAccessDeniedException.class, () ->
                service.getResultsForDoctor(doctorId, facilityId, patientId));
    }

    @Test
    void completedResultLookupDelegatesToRepository() {
        UUID resultId = UUID.randomUUID();
        when(resultRepository.existsByIdAndPatientIdAndStatus(resultId, patientId, ResultStatus.COMPLETED)).thenReturn(true);

        assertTrue(service.isCompletedResultForPatient(resultId, patientId));
    }
}
