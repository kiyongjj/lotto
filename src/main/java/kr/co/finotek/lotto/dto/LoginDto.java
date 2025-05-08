package kr.co.finotek.lotto.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LoginDto {

	private String member_id;
	private String user_name;
	private String password;
	private String email;
	private String mobile;
	private Date birth_day;
}
