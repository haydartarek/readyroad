package com.readyroad.readyroadbackend.marketing.admin;

import jakarta.validation.constraints.Size;

public record ApprovalDecisionRequest(@Size(max = 1000) String reason) {
}
