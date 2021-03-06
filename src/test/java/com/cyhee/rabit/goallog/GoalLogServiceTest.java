package com.cyhee.rabit.goallog;

import static org.assertj.core.api.Assertions.assertThat;

import com.cyhee.rabit.service.goallog.GoalLogService;
import com.cyhee.rabit.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.cyhee.rabit.cmm.AuthTestUtil;
import com.cyhee.rabit.cmm.CmmTestUtil;
import com.cyhee.rabit.model.cmm.ContentStatus;
import com.cyhee.rabit.model.goal.Goal;
import com.cyhee.rabit.service.goal.GoalService;
import com.cyhee.rabit.model.goallog.GoalLog;
import com.cyhee.rabit.model.user.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
@TestPropertySource(properties="spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
@Import({UserService.class, BCryptPasswordEncoder.class, GoalService.class, GoalLogService.class})
public class GoalLogServiceTest {
	@Autowired
	private GoalLogService goalLogService;
	@Autowired
	private TestEntityManager entityManager;
	
	User user1;
	User user2;
	Goal goal1;
	Goal goal2;
	GoalLog gl1;
	GoalLog gl2;
	GoalLog gl3;
	GoalLog gl4;
		
	@Before
	public void setup() {
		AuthTestUtil.setAdmin();
		
		user1 = new User().setEmail("email1@com").setUsername("user1");		
		user2 = new User().setEmail("email2@com").setUsername("user2");
		
		goal1 = new Goal().setAuthor(user1).setContent("content1");
		goal2 = new Goal().setAuthor(user2).setContent("content2");
		
		gl1 = new GoalLog().setGoal(goal1).setContent("content1");
		gl2 = new GoalLog().setGoal(goal2).setContent("content2");
		gl3 = new GoalLog().setGoal(goal1).setContent("content-3");
		gl4 = new GoalLog().setGoal(goal2).setContent("content-4");
		
		entityManager.persist(user1);
		entityManager.persist(user2);

		entityManager.persist(goal1);
		entityManager.persist(goal2);
	}

	@Test
	public void createAndGet() {
		goalLogService.addGoalLog(gl1);
		
		GoalLog gl = goalLogService.getGoalLog(gl1.getId());
		
		assertThat(gl)
			.isEqualTo(gl1);
	}
	
	@Test
	public void createAndGetAll() {
		goalLogService.addGoalLog(gl1);
		goalLogService.addGoalLog(gl2);
		goalLogService.addGoalLog(gl3);
		goalLogService.addGoalLog(gl4);

		Pageable pageable = PageRequest.of(1, 2);
		Page<GoalLog> page = goalLogService.getGoalLogs(pageable);
		assertThat(page)
			.hasSize(2)
			.containsExactlyInAnyOrder(gl3, gl4);
	}
	
	@Test
	public void update() {
		goalLogService.addGoalLog(gl1);
		try {
			CmmTestUtil.updateWithAuthentication(gl1, long.class, goalLogService, "content");
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
	}
	
	@Test
	public void delete() {
		goalLogService.addGoalLog(gl1);
		try {
			CmmTestUtil.deleteWithAuthentication(gl1, long.class, goalLogService);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
	}
	
	@Test
	public void search() {
		goalLogService.addGoalLog(gl1);
		goalLogService.addGoalLog(gl2);
		goalLogService.addGoalLog(gl3);
		goalLogService.addGoalLog(gl4);
		Pageable pageable = PageRequest.of(0, 10);
		assertThat(goalLogService.search("ent-", ContentStatus.all(), pageable))
			.hasSize(2)
			.contains(gl3,gl4);
	}
}
