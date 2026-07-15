package com.readyroad.readyroadbackend.domain.repository.custom;

public interface UserWeakAreaUpsertRepository {

    void upsertBySignCode(Long userId, String signCode, int totalQ, int correct, int wrong);

    void upsertByCategoryName(Long userId, String category, int totalQ, int correct, int wrong);
}
