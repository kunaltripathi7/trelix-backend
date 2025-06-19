package com.trelix.trelix_app.util;

import com.trelix.trelix_app.dto.TaskSearchCriteria;
import com.trelix.trelix_app.entity.Task;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {
    public static Specification<Task> byCriteria(TaskSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.getAssigneeId() != null) predicates.add(cb.equal(root.get("assignedTo").get("id"), criteria.getAssigneeId()));
            if (criteria.getStatus() != null) predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            if (criteria.getPriority() != null) predicates.add(cb.equal(root.get("priority"), criteria.getPriority()));
            if (criteria.getDueBefore() != null) predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), criteria.getDueBefore()));
            if (criteria.getSearch() != null && !criteria.getSearch().trim().isEmpty()) {
                String searchPattern = "%" + criteria.getSearch().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                Predicate assigneeMatch = cb.like(cb.lower(root.get("assignedTo").get("name")), searchPattern);
                predicates.add(cb.or(titleMatch, descriptionMatch, assigneeMatch));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
