package com.example.reward.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reward.dto.RewardResponseDTO;
import com.example.reward.service.RewardService;

@RestController
@RequestMapping("/api/v1/rewards")
@Validated
public class RewardController {

	private final RewardService service;

	public RewardController(RewardService service) {
		super();
		this.service = service;
	}

	@GetMapping("/{customerId}")
	public ResponseEntity<RewardResponseDTO> getCustomerRewards(@PathVariable Long customerId, @RequestParam  String startDate,
			@RequestParam  String endDate) {

		return ResponseEntity.ok(service.getCustomerRewards(customerId, LocalDate.parse(startDate), LocalDate.parse(endDate)));

	}

	@GetMapping
	public ResponseEntity<List<RewardResponseDTO>> getAllCustomerRewards() {

		List<RewardResponseDTO> response = service.getAllCustomerRewards();
		return ResponseEntity.ok(response);

	}

}
