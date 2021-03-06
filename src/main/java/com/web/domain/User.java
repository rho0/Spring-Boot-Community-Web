package com.web.domain;


import com.web.domain.enums.SocialType;
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
public class User implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    //OAuth2인증으로 제공받는 키값
    @Column
    private String principal;

    //어떤 소셜 미디어로 인증받았는지 여부를 구분해주는 값
    @Column
    private SocialType socialType;

    @Builder
    public User( String name, String password, String email, LocalDateTime createdDate, LocalDateTime updatedDate
                ,String principal, SocialType socialType) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.principal = principal;
        this.socialType = socialType;

    }
}
