//package com.meurer.ratelimiter;
//
//import com.meurer.ratelimiter.config.RateLimiterInterceptor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Order
//public class RateLimiterTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	private static final String TEST_URL = "/api/test";
//
//	@BeforeEach
//	public void init() throws InterruptedException {
//		sleep();
//	}
//
//
//	@Test
//	public void shouldAllowRequestsWithinLimit() throws Exception {
//		// English: Tests if the first 10 requests are allowed (status code 200 OK)
//		for (int i = 0; i < RateLimiterInterceptor.MAX_REQUESTS; i++) {
//			mockMvc.perform(get(TEST_URL))
//					.andExpect(status().is(HttpStatus.OK.value()));
//		}
//	}
//
//	@Test
//	public void shouldBlockRequestsExceedingLimit() throws Exception {
//		// English: First, make the 10 allowed requests
//		for (int i = 0; i < RateLimiterInterceptor.MAX_REQUESTS; i++) {
//			mockMvc.perform(get(TEST_URL))
//					.andExpect(status().is(HttpStatus.OK.value()));
//		}
//
//		// English: Test the 11th request, which should be blocked (status code 429 Too Many Requests)
//		mockMvc.perform(get(TEST_URL))
//				.andExpect(status().is(429));
//	}
//
//	@Test
//	public void shouldResetLimitAfterWindowExpires() throws Exception {
//		// English: Make the 10 requests to exhaust the limit
//		for (int i = 0; i < RateLimiterInterceptor.MAX_REQUESTS; i++) {
//			mockMvc.perform(get(TEST_URL))
//					.andExpect(status().is(HttpStatus.OK.value()));
//		}
//
//		// English: Confirm that the next request is blocked
//		mockMvc.perform(get(TEST_URL))
//				.andExpect(status().is(429));
//
//		// English: Wait for the window to expire (60 seconds + a little margin)
//		sleep();
//
//		// English: Test if a new request is allowed after expiration
//		mockMvc.perform(get(TEST_URL))
//				.andExpect(status().is(HttpStatus.OK.value()));
//	}
//
//	private void sleep() throws InterruptedException {
//		Thread.sleep((long) RateLimiterInterceptor.WINDOW_IN_SECONDS * 1000 + 1);
//	}
//}