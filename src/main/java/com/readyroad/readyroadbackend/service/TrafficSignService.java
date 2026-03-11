package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.dto.CreateTrafficSignRequest;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.PageResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.mapper.TrafficSignMapper;
import com.readyroad.readyroadbackend.exception.TrafficSignNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // Allowed sort fields to prevent injection
    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "signCode", "nameEn", "nameAr", "nameNl", "nameFr",
            "category.code", "isActive", "createdAt", "updatedAt", "id");

    public TrafficSignService(TrafficSignRepository trafficSignRepository,
            CategoryRepository categoryRepository,
            TrafficSignMapper trafficSignMapper) {
        this.trafficSignRepository = trafficSignRepository;
        this.categoryRepository = categoryRepository;
        this.trafficSignMapper = trafficSignMapper;
    }

    // ─── Public endpoints (unchanged) ──────────────────

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
                .orElseThrow(() -> new TrafficSignNotFoundException(signCode));
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

    // ─── Admin: Paginated list ─────────────────────────

    /**
     * Paginated, sorted, filtered admin signs list.
     *
     * @param page         0-based page index
     * @param size         page size (capped at 100)
     * @param sortParam    Spring-style sort e.g. "signCode,asc" or "nameEn,desc"
     * @param categoryCode optional category filter (e.g. "A", "B")
     * @param q            optional search query
     */
    public PageResponse<AdminTrafficSignResponse> getAdminSignsPaginated(
            int page, int size, String sortParam, String categoryCode, String q) {

        // Sanitize
        page = Math.max(0, page);
        size = Math.max(1, Math.min(size, 100));

        // Parse sort
        Sort sort = parseSort(sortParam);

        Pageable pageable = PageRequest.of(page, size, sort);

        // Normalize empty strings to null for the JPQL :param IS NULL check
        String catFilter = (categoryCode != null && !categoryCode.isBlank()) ? categoryCode.trim() : null;
        String qFilter = (q != null && !q.isBlank()) ? q.trim() : null;

        Page<TrafficSign> signPage = trafficSignRepository.findAdminSigns(catFilter, qFilter, pageable);

        List<AdminTrafficSignResponse> items = signPage.getContent().stream()
                .map(trafficSignMapper::toAdminResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, page, size, signPage.getTotalElements());
    }

    // ─── Admin: Single sign by ID ──────────────────────

    public AdminTrafficSignResponse getAdminSignById(Long id) {
        TrafficSign sign = trafficSignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Traffic sign not found with id: " + id));
        return trafficSignMapper.toAdminResponse(sign);
    }

    // ─── Admin: Create ─────────────────────────────────

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
        // Governance: long_description fields are canonical — set only via import
        // pipeline
        sign.setImageUrl(request.getImageUrl());
        sign.setIsActive(true);

        TrafficSign saved = trafficSignRepository.save(sign);
        return trafficSignMapper.toResponse(saved);
    }

    // ─── Admin: Update ─────────────────────────────────

    @Transactional
    public TrafficSignResponse updateSign(Long id, CreateTrafficSignRequest request) {
        TrafficSign sign = trafficSignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Traffic sign not found with id: " + id));

        // If signCode changed, check for conflicts (excluding current sign)
        if (!sign.getSignCode().equals(request.getSignCode())) {
            if (trafficSignRepository.existsBySignCodeAndIdNot(request.getSignCode(), id)) {
                throw new IllegalArgumentException("Sign code already exists: " + request.getSignCode());
            }
            sign.setSignCode(request.getSignCode());
        }

        // Update category if changed
        if (!sign.getCategory().getCode().equals(request.getCategoryCode())) {
            Category category = categoryRepository.findByCode(request.getCategoryCode())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Category not found: " + request.getCategoryCode()));
            sign.setCategory(category);
        }

        // Update all fields
        sign.setNameEn(request.getNameEn());
        sign.setNameAr(request.getNameAr() != null ? request.getNameAr() : "");
        sign.setNameNl(request.getNameNl() != null ? request.getNameNl() : "");
        sign.setNameFr(request.getNameFr() != null ? request.getNameFr() : "");
        sign.setDescriptionEn(request.getDescriptionEn() != null ? request.getDescriptionEn() : "");
        sign.setDescriptionAr(request.getDescriptionAr() != null ? request.getDescriptionAr() : "");
        sign.setDescriptionNl(request.getDescriptionNl() != null ? request.getDescriptionNl() : "");
        sign.setDescriptionFr(request.getDescriptionFr() != null ? request.getDescriptionFr() : "");
        // Governance: long_description fields are canonical — set only via import
        // pipeline
        if (request.getImageUrl() != null) {
            sign.setImageUrl(request.getImageUrl());
        }

        TrafficSign saved = trafficSignRepository.save(sign);
        return trafficSignMapper.toResponse(saved);
    }

    // ─── Admin: Delete ─────────────────────────────────

    @Transactional
    public void deleteSign(Long id) {
        TrafficSign sign = trafficSignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Traffic sign not found with id: " + id));
        trafficSignRepository.delete(sign);
    }

    // ─── Private helpers ───────────────────────────────

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "signCode");
        }

        String[] parts = sortParam.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // Map frontend-friendly field names
        if ("categoryCode".equals(field)) {
            field = "category.code";
        }

        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            return Sort.by(Sort.Direction.ASC, "signCode");
        }

        return Sort.by(direction, field);
    }
}
