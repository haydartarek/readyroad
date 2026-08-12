package com.readyroad.readyroadbackend.marketing.approval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ApprovalMetadataTest {

    @Test
    void createsStandingAuthorizationMetadataFromMasterSpecV3() {
        ApprovalMetadata metadata = ApprovalMetadata.standingOwnerAuthorization();

        assertThat(metadata.approvalMode()).isEqualTo(ApprovalMode.STANDING_OWNER_AUTHORIZATION);
        assertThat(metadata.approvalSource()).isEqualTo("MASTER_SPEC_V3");
        assertThat(metadata.approvalRequired()).isFalse();
    }

    @Test
    void createsExplicitHumanApprovalMetadata() {
        ApprovalMetadata metadata = ApprovalMetadata.humanApproval("MASTER_SPEC_V3");

        assertThat(metadata.approvalMode()).isEqualTo(ApprovalMode.HUMAN_APPROVAL);
        assertThat(metadata.approvalRequired()).isTrue();
    }

    @Test
    void rejectsBlankApprovalSources() {
        assertThatThrownBy(() -> ApprovalMetadata.humanApproval(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
