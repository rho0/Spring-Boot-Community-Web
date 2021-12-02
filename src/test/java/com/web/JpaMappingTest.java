package com.web;


import com.web.domain.Board;
import com.web.domain.User;
import com.web.domain.enums.BoardType;
import com.web.repository.BoardRepository;
import com.web.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/** @RunWith(SpringRunner.class):
 * JUnit에 내장된 러너를 사용하는 대신 어노테이션에 정의된 클래스를 호출.
 * JUnit의 확장기능을 지정하여 각 테스트시 독립적인 애플리케이션 컨텍스트를 보장.
 * 애플리케이션 컨텍스트 : 빈의 생성과 관계 설정 같은 제어를 담당하는 IOC 객체를 빈 팩토리라 부르며, 빈팩토리를 더 확장한 개념 */
@RunWith(SpringRunner.class)
/** @DataJpaTest:
 * 스프링 부트에서 JPA 테스트를 위한 전용 어노테이션. 첫 설계 시 엔티티 간의 관계 설정 및 기능 테스트를 가능하게 도와줌.
 * 테스트가 끝날 때마다 자동 롤백을 해주어 편리한 JPA 테스트가 가능*/
@DataJpaTest
public class JpaMappingTest {
    private final String boardTestTitle = "테스트 타이틀";
    private final String email = "test.spring.boot.web@gmail.com";

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    /** @Before:
     * 각 테스트가 실행되기 전에 실행 될 메소드를 선언 */
    @Before
    public void init(){
        User user = userRepository.save(User.builder()
                .name("havi")
                .password("test")
                .email(email)
                .createDate(LocalDateTime.now())
                .build()
        );

        boardRepository.save(Board.builder()
                .title(boardTestTitle)
                .subTitle("서브 타이틀 테스트")
                .content("콘텐츠 테스트")
                .boardType(BoardType.free)
                .createDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .user(user).build()
        );

    }

    /** @Test:
     * 실제 테스트가 진행될 메소드를 선언 */
    @Test
    public void test() {
        User user = userRepository.findByEmail(email);    /** init()에서 저장된 user를 eamil로 조회 */
        assertThat(user.getName(), is("havi"));     /** 각 필드가 저장된 값과 일치하는지 검사 */
        assertThat(user.getPassword(), is("test"));
        assertThat(user.getEmail(), is(email));

        Board board = boardRepository.findByUser(user);     /** init()에 저장한 board를 작성자인 user를 사용하여 조회하고 해당 필드가 올바른지 체크 */
        assertThat(board.getTitle(), is(boardTestTitle));   
        assertThat(board.getSubTitle(), is("서브 타이틀 테스트"));
        assertThat(board.getContent(), is("콘텐츠 테스트"));
        assertThat(board.getBoardType(), is(BoardType.free));

    }

}
