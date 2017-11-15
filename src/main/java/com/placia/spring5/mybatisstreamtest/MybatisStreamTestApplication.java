package com.placia.spring5.mybatisstreamtest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootApplication
public class MybatisStreamTestApplication implements CommandLineRunner {
	private final ResultStreamServiceFromMyBatis resultStreamServiceFromMyBatis;
	private final UserMapper userMapper;

	public MybatisStreamTestApplication(ResultStreamServiceFromMyBatis resultStreamServiceFromMyBatis, UserMapper userMapper) {
		this.resultStreamServiceFromMyBatis = resultStreamServiceFromMyBatis;
		this.userMapper = userMapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(MybatisStreamTestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userMapper.deleteAll();

		IntStream.range(0, 4).boxed()
				.map(i -> new User(UUID.randomUUID().toString(), new Random().nextInt(100)))
				.forEach(userMapper::save);

		ResultHandlerWithSubscriber subscriber = new ResultHandlerWithSubscriber();
		subscriber.getResultStream().subscribe(result -> System.out.println("result: "+result));

		resultStreamServiceFromMyBatis.publish(subscriber);
	}
}
