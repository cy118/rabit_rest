package com.cyhee.rabit.comment;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.cyhee.rabit.cmm.AuthTestUtil;
import com.cyhee.rabit.cmm.CmmTestUtil;
import com.cyhee.rabit.model.cmm.ContentType;
import com.cyhee.rabit.model.comment.Comment;
import com.cyhee.rabit.model.goal.Goal;
import com.cyhee.rabit.model.user.User;
import com.cyhee.rabit.service.comment.CommentService;
import com.cyhee.rabit.service.goal.GoalService;
import com.cyhee.rabit.service.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
@TestPropertySource(properties="spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
@Import({UserService.class, BCryptPasswordEncoder.class, GoalService.class, CommentService.class})
public class CommentServiceTest {
	@Autowired
	private CommentService commentService;
	@Autowired
	private TestEntityManager entityManger;

	User user1;
	User user2;
	Goal goal1;
	Goal goal2;
	Goal goal3;
	Goal goal4;
	Comment comment1;


	@Before
	public void setup() {
    	AuthTestUtil.setAdmin();
    	
		user1 = new User().setEmail("user@email.com").setUsername("username");		
		user2 = new User().setEmail("email2@a").setUsername("testuser2");

		goal1 = new Goal().setAuthor(user1).setContent("content1");
		goal2 = new Goal().setAuthor(user2).setContent("content2");
		goal3 = new Goal().setAuthor(user2).setContent("content3");
		goal4 = new Goal().setAuthor(user2).setContent("content2").setParent(goal1);
		
		entityManger.persist(user1);
		entityManger.persist(user2);
		
		entityManger.persist(goal1);
		entityManger.persist(goal2);
		entityManger.persist(goal3);
		entityManger.persist(goal4);
		
		comment1 = new Comment().setAuthor(user1)
			.setType(ContentType.GOAL).setContent("comment").setParentId(goal1.getId());
		
		commentService.addComment(comment1);
	}

	@Test
	public void createAndGet() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Comment> page = commentService.getComments(ContentType.GOAL, goal1.getId(), pageable);
		
		System.out.println(page);
		assertThat(page)
			.hasSize(1)
			.containsExactlyInAnyOrder(comment1);
	}

	@Test
	public void update() {
		try {
			CmmTestUtil.updateWithAuthentication(comment1, long.class, commentService, "content");
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
	}
	
	@Test
	public void delete() {
		try {
			CmmTestUtil.deleteWithAuthentication(comment1, long.class, commentService);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
	}
}
