package com.example.reward;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.reward.dto.RewardResponseDTO;
import com.example.reward.entity.Customer;
import com.example.reward.entity.Transaction;
import com.example.reward.exception.ResourceNotFoundException;
import com.example.reward.repository.TransactionRepository;
import com.example.reward.service.RewardService;

class RewardServiceTest {

	@Mock
	private TransactionRepository repo;

	@InjectMocks
	private RewardService service;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	private Transaction createTransaction(Long customerId, double amount, LocalDate date) {
		Customer customer = new Customer();
		customer.setId(customerId);
		customer.setName("Sumit");

		Transaction t = new Transaction();
		t.setAmount(amount);
		t.setDate(date);
		t.setCustomer(customer);
		return t;
	}

	@Test
	void testGetCustomerRewards_Success() {
		List<Transaction> transactions = List.of(createTransaction(1L, 120, LocalDate.now()),
				createTransaction(1L, 80, LocalDate.now()));

		when(repo.findByCustomerIdAndDateBetween(anyLong(), any(), any())).thenReturn(transactions);

		RewardResponseDTO response = service.getCustomerRewards(1L, LocalDate.now().minusDays(10), LocalDate.now());

		assertNotNull(response);
		assertEquals(1L, response.getCustomerId());
		assertTrue(response.getTotalPoints() > 0);
		assertEquals(2, response.getTransactions().size());
	}

	@Test
	void testGetCustomerRewards_NoTransactions() {
		when(repo.findByCustomerIdAndDateBetween(anyLong(), any(), any())).thenReturn(Collections.emptyList());

		assertThrows(ResourceNotFoundException.class,
				() -> service.getCustomerRewards(1L, LocalDate.now().minusDays(10), LocalDate.now()));
	}

	@Test
	void testGetCustomerRewards_InvalidDate() {
		assertThrows(IllegalArgumentException.class,
				() -> service.getCustomerRewards(1L, LocalDate.now(), LocalDate.now().minusDays(1)));
	}

	@Test
	void testCalculateRewards_MultipleTransactions() {
		List<Transaction> transactions = List.of(createTransaction(1L, 120, LocalDate.of(2026, 3, 1)),
				createTransaction(1L, 150, LocalDate.of(2026, 3, 5)),
				createTransaction(1L, 90, LocalDate.of(2026, 4, 1)));

		when(repo.findByCustomerIdAndDateBetween(anyLong(), any(), any())).thenReturn(transactions);

		RewardResponseDTO response = service.getCustomerRewards(1L, LocalDate.of(2026, 3, 1),
				LocalDate.of(2026, 4, 30));

		assertEquals(3, response.getTransactions().size());
		assertEquals(2, response.getMonthlyPoints().size()); // MARCH & APRIL
	}

	@Test
	void testGetAllCustomerRewards_Success() {

		List<Transaction> transactions = List.of(createTransaction(1L, 120, LocalDate.now()),
				createTransaction(2L, 150, LocalDate.now()));

		when(repo.findAll()).thenReturn(transactions);

		List<RewardResponseDTO> response = service.getAllCustomerRewards();

		assertEquals(2, response.size());
	}

	@Test
	void testGetAllCustomerRewards_NoData() {

		when(repo.findAll()).thenReturn(Collections.emptyList());

		assertThrows(ResourceNotFoundException.class, () -> service.getAllCustomerRewards());
	}
}
