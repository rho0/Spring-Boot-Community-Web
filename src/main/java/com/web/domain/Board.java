package com.web.domain;


import com.web.domain.enums.BoardType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table
public class Board implements Serializable{

    @Id
    @Column
    /** @GeneratedValue(strategy = GenerationType.IDENTITY) :
     * 기본키가 자동으로 할당되도록 설정하는 어노테이션. 기본키 할당 전략을 선택할 수 있는데, 키 생성을 데이터베이스에 위임하는 IDENTITY 전략을 사용*/
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String title;

    @Column
    private String subTitle;

    @Column
    private String content;

    @Column
    /**
     * @Enumerated(EnumType.STRING):
     * Enum 타입 매핑용 어노테이션. 자바 enum형과 데이터베이스 데이터 변환을 지원.
     * 실제로 자바 enum형이지만 데이터베이스의 String형으로 반환하여 저장하겠다고 선언한 것.*/
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    /**
     * @OneToOne(fetch = FetchType.LAZY):
     * 도메인 Board와 Board가 필드값으로 갖고잇는 User 도메인을 1:1관계로 설정하는 어노테이션.
     * 실제로 DB에 저장될 때는 User 객체가 저장되는 것이 아니라 User의 PK인 user_idx값이 저장됨.
     * fetch의 eager : 처음 Board 도메인을 조회할 때 즉시 관련 User 객체를 함께 조회한다는 뜻.
     * fetch의 lazy : User 객체를 조회하는 시점이 아닌 객체가 실제로 사용될 때 조회한다는 뜻. */
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public Board (String title, String subTitle, String content, BoardType boardType, LocalDateTime createdDate, LocalDateTime updatedDate, User user) {
        this.title = title;
        this.subTitle = subTitle;
        this.content = content;
        this.boardType = boardType;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.user = user;

    }
}
