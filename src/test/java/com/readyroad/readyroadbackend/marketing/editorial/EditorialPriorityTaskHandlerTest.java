package com.readyroad.readyroadbackend.marketing.editorial;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import org.junit.jupiter.api.Test;

class EditorialPriorityTaskHandlerTest {

    private final EditorialPriorityService priorities = mock(EditorialPriorityService.class);
    private final EditorialPrioritySettingsService settings = mock(EditorialPrioritySettingsService.class);
    private final MarketingAuditService audit = mock(MarketingAuditService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EditorialPriorityTaskHandler handler =
            new EditorialPriorityTaskHandler(priorities, settings, audit);

    @Test
    void settingsChangeIsAuditedAndRecalculatesInTheSameTask() {
        var payload = objectMapper.createObjectNode();
        payload.set("settings", objectMapper.createObjectNode().put("approved", true));
        payload.put("actor", "admin@readyroad.be");
        ClaimedTask task = new ClaimedTask(
                77L,
                EditorialPrioritySettingsService.AGENT_TYPE,
                EditorialPriorityTaskHandler.SETTINGS_UPDATE,
                payload,
                1,
                TaskPriority.HIGH,
                1,
                "settings-77");
        when(settings.update(any(), eq("admin@readyroad.be")))
                .thenReturn(EditorialPriorityConfig.defaults());

        handler.execute(task);

        verify(settings).update(any(), eq("admin@readyroad.be"));
        verify(audit).recordEntityEvent(
                eq("EDITORIAL_PRIORITY_SETTINGS_UPDATED"),
                eq("admin@readyroad.be"),
                eq("AGENT_SETTING"),
                eq(EditorialPrioritySettingsService.SETTING_KEY),
                eq(77L),
                eq("settings-77"));
        verify(priorities).recalculate(77L, "PRIORITY_SETTINGS_CHANGE", "admin@readyroad.be");
    }
}
