package com.tuto.library.service;

import com.tuto.library.exception.MemberNotFoundException;
import com.tuto.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    void shouldThrowMemberNotFoundException_whenDeletingNonExistentMember() {
        //TODO 1.0 : @Mohamed Sayed, implement this test
        //GIVEN
        given(memberRepository.findById("memberId1")).willReturn(Optional.empty());

        //WHEN
        Throwable thrown =catchThrowable(()-> {
            memberService.deleteMember("memberId1");
        });

        //THEN
        assertThat(thrown)
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("Member with ID memberId1 not found");
    }
}
