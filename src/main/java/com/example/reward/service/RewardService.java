package com.example.reward.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.reward.dto.RewardResponseDTO;
import com.example.reward.dto.TransactionDTO;
import com.example.reward.entity.Transaction;
import com.example.reward.exception.ResourceNotFoundException;
import com.example.reward.repository.TransactionRepository;
import com.example.reward.util.RewardUtil;

@Service
public class RewardService {

	private final TransactionRepository repo;
	

    private static final Logger log = LoggerFactory.getLogger(RewardService.class);


	public RewardService(TransactionRepository repo) {
		super();
		this.repo = repo;
	}

	public RewardResponseDTO getCustomerRewards(Long customerId, LocalDate start, LocalDate end) {
		log.info("Fetching rewards for customer {}", customerId);
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("Start date cannot be after end date");
		}
		List<Transaction> transactions = repo.findByCustomerIdAndDateBetween(customerId, start, end);

		if (transactions == null || transactions.isEmpty()) {
			throw new ResourceNotFoundException("No transactions found for given customer and date range");
		}
		return calculateReward(customerId, transactions);

	}

	private RewardResponseDTO calculateReward(Long customerId, List<Transaction> transactions) {
		RewardResponseDTO dto = new RewardResponseDTO();
		dto.setCustomerId(customerId);
		dto.setCustomeName(transactions.get(0).getCustomer().getName());

		int total = 0;

		for (Transaction t : transactions) {

			int points = RewardUtil.calculatePoints(t.getAmount());

			dto.getMonthlyPoints().merge(t.getDate().getMonth().toString(), points, Integer::sum);

			total += points;

			TransactionDTO tx = new TransactionDTO();
			tx.setAmount(t.getAmount());
			tx.setDate(t.getDate());
			tx.setPoints(points);

			dto.getTransactions().add(tx);
		}

		dto.setTotalPoints(total);

		return dto;

	}

	public List<RewardResponseDTO> getAllCustomerRewards() {
		 log.info("Fetching rewards for all customers");

		List<Transaction> transactions = repo.findAll();

		if (transactions.isEmpty()) {
			throw new ResourceNotFoundException("No transactions found");
		}

		Map<Long, List<Transaction>> grouped = transactions.stream()
				.collect(Collectors.groupingBy(t -> t.getCustomer().getId()));

		List<RewardResponseDTO> response = new ArrayList<>();

		for (Map.Entry<Long, List<Transaction>> entry : grouped.entrySet()) {
			response.add(calculateReward(entry.getKey(), entry.getValue()));
		}

		return response;
	}

}
