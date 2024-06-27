package com.api.bookshow.controller;

import com.api.bookshow.model.Event;
import com.api.bookshow.service.EventService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/events")
@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @RolesAllowed("ADMIN")
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String theaterName) {
        return ResponseEntity.ok(eventService.getAllEvents(name,type,theaterName));
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/page")
    public Page<Event> getPaginatedEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return eventService.getPaginatedEvents(page, size, sortBy, direction);
    }

    @RolesAllowed("ADMIN")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
