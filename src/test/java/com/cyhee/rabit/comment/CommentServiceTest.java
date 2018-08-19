package com.cyhee.rabit.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.cyhee.rabit.cmm.model.ContentType;
import com.cyhee.rabit.comment.model.Comment;
import com.cyhee.rabit.comment.service.BasicCommentService;
import com.cyhee.rabit.comment.service.CommentService;
import com.cyhee.rabit.goal.model.Goal;
import com.cyhee.rabit.goal.service.BasicGoalService;
import com.cyhee.rabit.goal.service.GoalService;
import com.cyhee.rabit.user.model.User;
import com.cyhee.rabit.user.service.BasicUserService;
import com.cyhee.rabit.user.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
@Import({BasicUserService.class, BCryptPasswordEncoder.class, BasicGoalService.class, BasicCommentService.class})
public class CommentServiceTest {
	@Autowired
	private UserService userService;
	@Autowired
	private GoalService goalService;
	@Autowired
	private CommentService commentService;	

	User user1;
	User user2;
	Goal goal1;
	Goal goal2;
	Goal goal3;
	Goal goal4;
	Comment comment1;
	Comment comment2;
	Comment comment3;
	Comment comment4;


	@Before
	public void setup() {
		user1 = new User().setEmail("user@email.com").setPassword("password1@").setUsername("username");		
		user2 = new User().setEmail("email2@a").setPassword("password2@").setUsername("testuser2");

		goal1 = new Goal().setAuthor(user1).setContent("content1");
		goal2 = new Goal().setAuthor(user2).setContent("content2");
		goal3 = new Goal().setAuthor(user2).setContent("content3");
		goal4 = new Goal().setAuthor(user2).setContent("content2").setParent(goal1);
		
		userService.addUser(user1);
		goalService.addGoal(goal1);
		comment1 = new Comment().setAuthor(user1)
			.setType(ContentType.GOAL).setContent("comment").setParentId(goal1.getId());
		
		commentService.addComment(comment1);
	}

	@Test
	public void createAndGet() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Comment> page = commentService.getComments(ContentType.GOAL, goal1.getId(), pageable);
		
		assertThat(page)
			.hasSize(1)
			.containsExactlyInAnyOrder(comment1);
	}


	@Test
	public void deleteAndGet() {
		commentService.deleteComment(comment1.getId());
		
		assertThatThrownBy(() -> {
			commentService.getComment(comment1.getId());
		});
	}

	@Test
	public void update() {
		Comment source = new Comment().setContent("new Content");
		commentService.updateComment(comment1.getId(), source);
		
		assertThat(comment1)
			.extracting(Comment::getContent)
			.containsExactly(source.getContent());
	}
}
