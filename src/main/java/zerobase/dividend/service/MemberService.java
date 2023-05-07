package zerobase.dividend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import zerobase.dividend.exception.impl.AlreadyExistUserException;
import zerobase.dividend.exception.impl.IncorrectPassword;
import zerobase.dividend.exception.impl.NoMemberException;
import zerobase.dividend.model.Auth;
import zerobase.dividend.persist.MemberRepository;
import zerobase.dividend.persist.entity.MemberEntity;

@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {
	
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.memberRepository.findByUsername(username)
				.orElseThrow(() -> new NoMemberException());
	}
	
	public MemberEntity register(Auth.SignUp member) {
		boolean exists = this.memberRepository.existsByUsername(member.getUsername());
		if(exists) {
			throw new AlreadyExistUserException();
		}
		
		member.setPassword(this.passwordEncoder.encode(member.getPassword()));
		var result = this.memberRepository.save(member.toEntity());
		
		return result;
	}
	
	public MemberEntity authenticate(Auth.SignIn member) {
		var user = this.memberRepository.findByUsername(member.getUsername())
	                .orElseThrow(() -> new NoMemberException());

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new IncorrectPassword();
        }

        return user;
	}
}
