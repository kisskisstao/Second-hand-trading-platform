package com.example.Second_hand.trading.platform.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Second_hand.trading.platform.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping(value = "/alipay/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String alipayNotify(@RequestParam Map<String, String> params) {
		return paymentService.handleAlipayNotify(params);
	}

	@PostMapping("/wechat/notify")
	public Map<String, String> wechatNotify(@RequestBody String body) {
		return paymentService.handleWechatNotify(body);
	}
}
