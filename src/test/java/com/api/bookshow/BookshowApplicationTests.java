package com.api.bookshow;

import com.api.bookshow.dto.BookingStatistics;
import com.api.bookshow.model.Booking;
import com.api.bookshow.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookshowApplicationTests {

	@InjectMocks
	private BookingService bookingService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetBookingStatistics() {
		// Given
		Booking booking1 = new Booking();
		booking1.setTotalPrice(100.00);

		Booking booking2 = new Booking();
		booking2.setTotalPrice(200.00);

		List<Booking> bookings = Arrays.asList(booking1, booking2);

		// When
		BookingStatistics statistics = bookingService.getBookingStatistics(bookings);

		// Then
		assertEquals(2, statistics.getTotalBookings());
		assertEquals(new BigDecimal("300.00").doubleValue(), statistics.getTotalMoneyCollected().doubleValue());
	}

}
