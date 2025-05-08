package kr.co.finotek.lotto.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.finotek.lotto.dto.board.UserDTO;
import kr.co.finotek.lotto.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	
	@PostMapping("/{userId}")
	public UserDTO getUserById(@PathVariable Long userId) {
		System.out.println(userId);
		return userService.getUserById(userId);
//		return null;
	}
	
	@PostMapping("/all")
	public List<UserDTO> getAllUsers() {
		return userService.getAllUsers();
	}
}
