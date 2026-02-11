package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.dto.CreateTrafficSignRequest;
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
    private final CategoryRepository categoryRepository;
    private final TrafficSignMapper trafficSignMapper;

    public TrafficSignService(TrafficSignRepository trafficSignRepository,
                              CategoryRepository categoryRepository,
                              TrafficSignMapper trafficSignMapper) {
        this.trafficSignRepository = trafficSignRepository;
        this.categoryRepository = categoryRepository;
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

    @Transactional
    public TrafficSignResponse createSign(CreateTrafficSignRequest request) {
        if (trafficSignRepository.existsBySignCode(request.getSignCode())) {
            throw new IllegalArgumentException("Sign code already exists: " + request.getSignCode());
        }

        Category category = categoryRepository.findByCode(request.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategoryCode()));

        TrafficSign sign = new TrafficSign();
        sign.setSignCode(request.getSignCode());
        sign.setCategory(category);
        sign.setNameEn(request.getNameEn());
        sign.setNameAr(request.getNameAr() != null ? request.getNameAr() : "");
        sign.setNameNl(request.getNameNl() != null ? request.getNameNl() : "");
        sign.setNameFr(request.getNameFr() != null ? request.getNameFr() : "");
        sign.setDescriptionEn(request.getDescriptionEn() != null ? request.getDescriptionEn() : "");
        sign.setDescriptionAr(request.getDescriptionAr() != null ? request.getDescriptionAr() : "");
        sign.setDescriptionNl(request.getDescriptionNl() != null ? request.getDescriptionNl() : "");
        sign.setDescriptionFr(request.getDescriptionFr() != null ? request.getDescriptionFr() : "");
        sign.setImageUrl(request.getImageUrl());
        sign.setIsActive(true);

        TrafficSign saved = trafficSignRepository.save(sign);
        return trafficSignMapper.toResponse(saved);
    }
}

