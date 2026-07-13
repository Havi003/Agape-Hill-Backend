package com.agapehill.agape_hill_backend.service;

import com.agapehill.agape_hill_backend.dto.request.EventRequest;
import com.agapehill.agape_hill_backend.dto.response.*;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface EventService {
    Mono<WsResponse<EventSummaryResponse>> createEvent(EventRequest request);
    Mono<WsResponse<List<EventSummaryResponse>>> getAllEvents();
    Mono<WsResponse<List<EventLedgerItemResponse>>> getEventLedger(UUID eventId);
    Mono<WsResponse<Void>> logEventPayment(UUID eventId, EventRequest request);
}