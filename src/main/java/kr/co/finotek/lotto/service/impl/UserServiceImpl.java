package kr.co.finotek.lotto.service.impl;

import java.util.List;

import kr.co.finotek.lotto.dto.board.UserDTO;
import kr.co.finotek.lotto.service.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public UserDTO getUserById(Long userId) {
		System.out.println("getUserById");
		return new UserDTO(1L, "user1", "user1@example.com");
	}

	@Override
	public List<UserDTO> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

}
