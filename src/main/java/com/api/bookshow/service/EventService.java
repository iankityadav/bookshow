package com.api.bookshow.service;

import com.api.bookshow.model.Event;

import java.util.List;
import java.util.Optional;

import com.api.bookshow.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event) {
        if (eventRepository.existsByTheaterAndStartTimeAndEndTime(event.getTheater(), event.getStartTime(), event.getEndTime())) {
            throw new RuntimeException("Event already exists at the same time in the same theater");
        }
        event.setAvailableSeats(event.getMaxOccupancy());
        return eventRepository.save(event);
    }

    public Optional<Event> getEvent(Long eventId) {
        return eventRepository.findById(eventId);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Page<Event> getPaginatedEvents(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return eventRepository.findAll(pageable);
    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        if (event.getAvailableSeats() != event.getMaxOccupancy()) {
            throw new RuntimeException("Cannot delete event with bookings");
        }
        eventRepository.delete(event);
    }
}