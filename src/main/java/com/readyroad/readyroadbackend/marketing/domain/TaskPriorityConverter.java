package com.readyroad.readyroadbackend.marketing.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TaskPriorityConverter implements AttributeConverter<TaskPriority, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TaskPriority attribute) {
        return attribute == null ? TaskPriority.NORMAL.value() : attribute.value();
    }

    @Override
    public TaskPriority convertToEntityAttribute(Integer dbData) {
        return dbData == null ? TaskPriority.NORMAL : TaskPriority.fromValue(dbData);
    }
}
