package com.cyhee.rabit.dao.goal;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.cyhee.rabit.model.cmm.ContentStatus;
import com.cyhee.rabit.model.goal.Goal;

public interface CompanionGoalRepository extends Repository<Goal, Long> {
	@Query("FROM Goal g WHERE (g.parent = :root OR g = :root) AND g <> :goal AND g.status In :statusList")
	Page<Goal> findAllByGoal(@Param("root") Goal root, @Param("goal") Goal goal,
			@Param("statusList") List<ContentStatus> statusList, Pageable pageable);

	@Query("SELECT g.id FROM Goal g WHERE (g.parent = :root OR g = :root) AND g <> :goal AND g.status In :statusList")
	List<Long> findAllByGoal(@Param("root") Goal root, @Param("goal") Goal goal,
							 @Param("statusList") List<ContentStatus> statusList);
}