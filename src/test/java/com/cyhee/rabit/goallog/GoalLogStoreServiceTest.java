package com.cyhee.rabit.goallog;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.cyhee.rabit.cmm.AuthTestUtil;
import com.cyhee.rabit.cmm.CmmTestUtil;
import com.cyhee.rabit.model.cmm.ContentStatus;
import com.cyhee.rabit.model.cmm.ContentType;
import com.cyhee.rabit.model.cmm.RadioStatus;
import com.cyhee.rabit.model.comment.Comment;
import com.cyhee.rabit.model.goal.Goal;
import com.cyhee.rabit.model.goallog.GoalLog;
import com.cyhee.rabit.model.like.Like;
import com.cyhee.rabit.model.user.User;
import com.cyhee.rabit.service.comment.CommentService;
import com.cyhee.rabit.service.comment.CommentStoreService;
import com.cyhee.rabit.service.goallog.GoalLogService;
import com.cyhee.rabit.service.goallog.GoalLogStoreService;
import com.cyhee.rabit.service.like.LikeService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
@TestPropertySource(properties="spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
@Import({GoalLogStoreService.class, CommentService.class, GoalLogService.class, CommentStoreService.class, LikeService.class})
public class GoalLogStoreServiceTest {
	@Autowired
	private GoalLogStoreService goalLogStoreService;
	@Autowired
	private TestEntityManager entityManger;
	
	User user1;
	User user2;
	Goal goal1;
	Goal goal2;
	GoalLog gl1;
	GoalLog gl2;
	Comment comment1;
	Comment comment2;
	Like like1;
	Like like2;
		
	@Before
	public void setup() {
		AuthTestUtil.setAdmin();
		
		user1 = new User().setEmail("email1@com").setUsername("user1");		
		user2 = new User().setEmail("email2@com").setUsername("user2");
		
		goal1 = new Goal().setAuthor(user1).setContent("content1");
		goal2 = new Goal().setAuthor(user2).setContent("content2");
		
		gl1 = new GoalLog().setGoal(goal1).setContent("content1");
		gl2 = new GoalLog().setGoal(goal2).setContent("content2");
		
		entityManger.persist(user1);
		entityManger.persist(user2);
		entityManger.persist(goal1);
		entityManger.persist(goal2);
		entityManger.persist(gl1);
		entityManger.persist(gl2);
		
		comment1 = new Comment().setAuthor(user1).setType(ContentType.GOALLOG).setContent("comment").setParentId(gl1.getId());
		comment2 = new Comment().setAuthor(user1).setType(ContentType.GOALLOG).setContent("comment").setParentId(gl2.getId());
		like1 = new Like().setAuthor(user2).setType(ContentType.GOALLOG).setParentId(gl1.getId());
		like2 = new Like().setAuthor(user2).setType(ContentType.GOALLOG).setParentId(gl1.getId());

		entityManger.persist(comment1);
		entityManger.persist(comment2);
		entityManger.persist(like1);
		entityManger.persist(like2);
	}
	
	@Test
	public void getComments() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Comment> comments = goalLogStoreService.getComments(gl1, pageable);
		
		assertThat(comments.getContent())
			.hasSize(1).contains(comment1);
		
		comments = goalLogStoreService.getComments(gl2, pageable);
		
		assertThat(comments.getContent())
			.hasSize(1).contains(comment2);
	}

	@Test
	public void getLikes() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Like> likes = goalLogStoreService.getLikes(gl1, pageable);

		assertThat(likes)
				.hasSize(2)
				.containsExactlyInAnyOrder(like1, like2);
	}

	@Test
	public void deleteAndGet() {
		try {
			CmmTestUtil.deleteWithAuthentication(gl1, long.class, goalLogStoreService);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}

		assertThat(gl1)
				.extracting(GoalLog::getStatus)
				.containsExactly(ContentStatus.DELETED);

		assertThat(comment1)
				.extracting(Comment::getStatus)
				.containsExactly(ContentStatus.DELETED);

		assertThat(like1)
				.extracting(Like::getStatus)
				.containsExactly(RadioStatus.INACTIVE);
	}
}
