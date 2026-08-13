package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorialBacklogService {

    private final EditorialBacklogStore store;

    @Transactional(readOnly = true)
    public EditorialDtos.Backlog backlog() {
        List<EditorialDtos.Topic> topics = store.topics();
        int pillars = (int) topics.stream().filter(EditorialDtos.Topic::pillar).count();
        int unresolved = (int) topics.stream()
                .filter(topic -> !topic.strategyContextResolved())
                .count();
        return new EditorialDtos.Backlog(topics.size(), pillars, unresolved, List.copyOf(topics));
    }
}
