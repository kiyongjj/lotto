package kr.co.finotek.lotto.service;

import java.util.List;

import kr.co.finotek.lotto.dto.board.UserDTO;

public interface UserService {

	UserDTO getUserById(Long userId);
	List<UserDTO> getAllUsers();
}
