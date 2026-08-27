package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class EditorialSourcePolicyTest {

    private final EditorialSourcePolicy policy = new EditorialSourcePolicy();

    @Test
    void permitsOnlyApprovedSourceTypesAndHttpsExternalLocations() {
        var valid = request(source(
                "OFFICIAL_GOVERNMENT_SOURCE", "EXTERNAL", "https://Mobilit.Belgium.be/rules/",
                null, "OFFICIAL", "VERIFIED", true, "VERIFIED"));

        policy.validate(valid);

        assertThat(policy.canonicalUrl("https://Mobilit.Belgium.be/rules/#section"))
                .isEqualTo("https://mobilit.belgium.be/rules");
        assertThatThrownBy(() -> policy.validate(request(source(
                "OPENAI", "EXTERNAL", "https://openai.com", null,
                "UNTRUSTED", "UNVERIFIED", false, "NOT_REQUIRED"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("source type");
        assertThatThrownBy(() -> policy.validate(request(source(
                "APPROVED_REFERENCE_SOURCE", "EXTERNAL", "http://example.com", null,
                "APPROVED_REFERENCE", "UNVERIFIED", false, "NOT_REQUIRED"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTPS");
    }

    @Test
    void legalClaimsAlwaysRequireReviewAndCannotTreatApprovedReferencesAsAuthority() {
        assertThatThrownBy(() -> policy.validate(new EditorialSourceDtos.SourceCollectionRequest(
                1L, "BRIEF-1", List.of(new EditorialSourceDtos.ClaimInput(
                        "claim", "Legal claim", "LEGAL", "EN", false, List.of())), "legal")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Legal claims require legal review");

        var reference = new EditorialSourceStore.StoredSource(
                1L, "APPROVED_REFERENCE_SOURCE", "EXTERNAL", "VERIFIED",
                "APPROVED_REFERENCE", true, "VERIFIED", "abc", null, null,
                true, 1L, false);
        var official = new EditorialSourceStore.StoredSource(
                2L, "OFFICIAL_LEGAL_SOURCE", "EXTERNAL", "VERIFIED",
                "OFFICIAL", true, "VERIFIED", "def", null, null,
                true, 2L, false);

        assertThat(policy.supportsClaim("LEGAL", reference)).isFalse();
        assertThat(policy.supportsClaim("LEGAL", official)).isTrue();
    }

    @Test
    void acceptsOnlyTheRijViaCoreSourceIdentity() {
        var rijViaCore = source(
                "RIJVIA_CORE_DATA", "INTERNAL", null, "lessons/les-1",
                "CORE_TRUSTED", "VERIFIED", false, "NOT_REQUIRED");

        policy.validate(request(rijViaCore));

        assertThatThrownBy(() -> policy.validate(request(source(
                "READYROAD_CORE_DATA", "INTERNAL", null, "lessons/les-1",
                "CORE_TRUSTED", "VERIFIED", false, "NOT_REQUIRED"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("source type");
    }

    private static EditorialSourceDtos.SourceCollectionRequest request(EditorialSourceDtos.SourceInput source) {
        return new EditorialSourceDtos.SourceCollectionRequest(
                1L, "BRIEF-1", List.of(new EditorialSourceDtos.ClaimInput(
                        "claim", "Legal claim", "LEGAL", "EN", true, List.of(source))), "request-1");
    }

    private static EditorialSourceDtos.SourceInput source(
            String type,
            String location,
            String url,
            String internalReference,
            String trust,
            String verification,
            boolean legalRequired,
            String legalStatus) {
        return new EditorialSourceDtos.SourceInput(
                type, location, "Source title", "Publisher", url, internalReference,
                "BE", "EN", verification, trust, legalRequired, legalStatus,
                "fingerprint", null, null);
    }
}
