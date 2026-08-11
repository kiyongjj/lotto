package kr.co.finotek.lotto.service;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("loginService")
@AllArgsConstructor
@RequiredArgsConstructor
@Slf4j
@Component
public class LoginService {

	@Value("${login.naver.baseUrl}")
	private String baseUrl;
	
	@Value("${login.naver.clientId}")
	private String clientId;
	
	@Value("${login.naver.redirectUrl}")
	private String redirectUrl;

	public String getNaverAuthorizeUrl(String type) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		log.info("service : " + type);
		UriComponents uriComponents = UriComponentsBuilder
				.fromUriString(baseUrl + "/" + type)
				.queryParam("response_type", "code")
				.queryParam("client_id", clientId)
				.queryParam("redirect_uri", URLEncoder.encode(redirectUrl, "UTF-8"))
				.queryParam("state", URLEncoder.encode("1234", "UTF-8"))
				.build();
		return uriComponents.toString();
	}

}
