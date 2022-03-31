package couch.forrest.domain.member.service;

import couch.forrest.domain.member.dao.MemberRepository;
import couch.forrest.domain.member.dto.response.MemberRegisterResponseDto;
import couch.forrest.domain.member.entity.Member;
import couch.forrest.exception.CustomException;
import couch.forrest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService implements UserDetailsService{

    final private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        return memberRepository.findByUid(uid)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("해당 회원이 존재하지 않습니다.");
                });
    }


    @Transactional
    public MemberRegisterResponseDto register(String email, String name, String picture, String uid) {
        Optional<Member> optionalMember = memberRepository.findByUid(uid);
        if (optionalMember.isPresent()) {
            throw new CustomException(ErrorCode.EXIST_USER, "해당 계정으로 이미 회원가입을 했습니다.");
        }
        Member member = Member.builder()
                .uid(uid)
                .name(name)
                .email(email)
                .picture(picture)
                .build();

        return new MemberRegisterResponseDto(memberRepository.save(member));
    }
}
