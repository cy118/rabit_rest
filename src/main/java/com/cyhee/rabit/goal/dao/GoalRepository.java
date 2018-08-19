package com.cyhee.rabit.goal.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.cyhee.rabit.goal.model.Goal;
import com.cyhee.rabit.user.model.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called goalRepository
// CRUD refers Create, Read, Update, Delete

public interface GoalRepository extends PagingAndSortingRepository<Goal, Long> {
	Page<Goal> findAllByAuthor(User author, Pageable pageable);
}