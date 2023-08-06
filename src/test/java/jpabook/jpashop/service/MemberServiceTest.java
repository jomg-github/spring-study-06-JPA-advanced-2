package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("MEMBER A");

        // when
        Long newMemberId = memberService.signUp(member);

        // then
        assertThat(member.getId()).isEqualTo(newMemberId);
        assertThat(memberService.findOne(member.getId())).isSameAs(member);
    }

    @Test
    void 회원가입_실패_중복이름() {
        // given
        Member member1 = new Member();
        member1.setName("손흥민");

        Member member2 = new Member();
        member2.setName("손흥민");

        // when
        memberService.signUp(member1);

        // then
        assertThrows(IllegalStateException.class,
                () -> memberService.signUp(member2)
        );
    }
}