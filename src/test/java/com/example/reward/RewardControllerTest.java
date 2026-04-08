package com.example.reward;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.reward.controller.RewardController;
import com.example.reward.dto.RewardResponseDTO;
import com.example.reward.service.RewardService;

public class RewardControllerTest {

	@Mock
	private RewardService service;

	private RewardController controller;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		controller = new RewardController(service);
	}

	@Test
	void testGetCustomerRewards_Success() {

		RewardResponseDTO dto = new RewardResponseDTO();
		dto.setCustomerId(1L);
		dto.setTotalPoints(200);

		when(service.getCustomerRewards(anyLong(), any(), any())).thenReturn(dto);

		var response = controller.getCustomerRewards(1L, "2026-03-01", "2026-03-31");

		assertEquals(200, response.getBody().getTotalPoints());
	}

	// ❌ Service Throws Exception
	@Test
	void testGetCustomerRewards_Exception() {

		when(service.getCustomerRewards(anyLong(), any(), any())).thenThrow(new RuntimeException("Error"));

		assertThrows(RuntimeException.class, () -> controller.getCustomerRewards(1L, "2026-03-01", "2026-03-31"));
	}

	@Test
	void testGetAllCustomerRewards_Success() {

		RewardResponseDTO dto1 = new RewardResponseDTO();
		dto1.setCustomerId(1L);

		RewardResponseDTO dto2 = new RewardResponseDTO();
		dto2.setCustomerId(2L);

		when(service.getAllCustomerRewards()).thenReturn(List.of(dto1, dto2));

		var response = controller.getAllCustomerRewards();

		assertEquals(2, response.getBody().size());
	}

	@Test
	void testGetAllCustomerRewards_Empty() {

		when(service.getAllCustomerRewards()).thenReturn(Collections.emptyList());

		var response = controller.getAllCustomerRewards();

		assertTrue(response.getBody().isEmpty());
	}

}
