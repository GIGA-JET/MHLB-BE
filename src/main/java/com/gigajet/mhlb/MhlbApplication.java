package com.gigajet.mhlb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class MhlbApplication {

	/*
		EC2 인스턴스 시간이 달라 채팅 시간이 제대로 불러오지 못한 이슈 발생
		Spring 서버 실행 시 시간 디폴트 값을 "Asia/Seoul"로 설정해 해결
	 */
	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(MhlbApplication.class, args);
	}

}
