package com.agapehill.agape_hill_backend.controller;

import com.agapehill.agape_hill_backend.dto.request.EventRequest;
import com.agapehill.agape_hill_backend.dto.response.*;
import com.agapehill.agape_hill_backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/create")
    public Mono<WsResponse<EventSummaryResponse>> createEvent(@RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping
    public Mono<WsResponse<List<EventSummaryResponse>>> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{eventId}/ledger")
    public Mono<WsResponse<List<EventLedgerItemResponse>>> getEventLedger(@PathVariable UUID eventId) {
        return eventService.getEventLedger(eventId);
    }

    @PostMapping("/{eventId}/payments")
    public Mono<WsResponse<Void>> recordPayment(
            @PathVariable UUID eventId, 
            @RequestBody EventRequest request) {
        return eventService.logEventPayment(eventId, request);
    }
}