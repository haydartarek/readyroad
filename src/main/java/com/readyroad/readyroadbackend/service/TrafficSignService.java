package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.mapper.TrafficSignMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TrafficSignService {

    private final TrafficSignRepository trafficSignRepository;
    private final TrafficSignMapper trafficSignMapper;

    public TrafficSignService(TrafficSignRepository trafficSignRepository, TrafficSignMapper trafficSignMapper) {
        this.trafficSignRepository = trafficSignRepository;
        this.trafficSignMapper = trafficSignMapper;
    }

    public List<TrafficSignResponse> getAllActiveSigns() {
        return trafficSignRepository.findAllByIsActiveTrue()
                .stream()
                .map(trafficSignMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TrafficSignResponse> getSignsByCategory(Long categoryId) {
        return trafficSignRepository.findAllByCategoryIdAndIsActiveTrue(categoryId)
                .stream()
                .map(trafficSignMapper::toResponse)
                .collect(Collectors.toList());
    }

    public TrafficSignResponse getSignByCode(String signCode) {
        TrafficSign sign = trafficSignRepository.findBySignCode(signCode)
                .orElseThrow(() -> new RuntimeException("Traffic sign not found: " + signCode));
        return trafficSignMapper.toResponse(sign);
    }

    public List<TrafficSignResponse> searchTrafficSigns(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActiveSigns();
        }
        return trafficSignRepository.searchTrafficSigns(query.trim())
                .stream()
                .map(trafficSignMapper::toResponse)
                .collect(Collectors.toList());
    }
}

