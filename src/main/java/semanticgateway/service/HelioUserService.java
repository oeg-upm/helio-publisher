package semanticgateway.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import semanticgateway.model.HelioUser;
import semanticgateway.model.User;
import semanticgateway.repository.HelioUserRepository;

@Service
public class HelioUserService {

	@Autowired
	private HelioUserRepository helioUserRepository;
	private PasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();


	public Boolean existUsername(String username){
		return helioUserRepository.existsById(username);
	}

	public void save(User user) {
		HelioUser newUser = createHelioUser(user);
		helioUserRepository.save(newUser);
	}
	
	private HelioUser createHelioUser(User user) {
		HelioUser newUser = new HelioUser();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return newUser;
	}

	
	public Boolean checkLogin(User user) {
		Boolean loginCorrect = false;
		Optional<HelioUser> helioUserOptional = helioUserRepository.findById(user.getUsername());
		if (helioUserOptional.isPresent()) {
			HelioUser helioUser = helioUserOptional.get();
			loginCorrect = helioUser != null && user.getUsername().equals(helioUser.getUsername())
					&& bcryptEncoder.matches(user.getPassword(), helioUser.getPassword());
		}
		return loginCorrect;
	}
	
	public void remove(String username) {
		helioUserRepository.deleteById(username);
	}
}
