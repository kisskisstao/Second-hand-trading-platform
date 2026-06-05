package com.example.Second_hand.trading.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.payment")
public class PaymentProperties {
	private String returnUrl = "";
	private Alipay alipay = new Alipay();
	private Wechat wechat = new Wechat();

	@Data
	public static class Alipay {
		private boolean enabled = false;
		private String gateway = "https://openapi.alipay.com/gateway.do";
		private String appId = "";
		private String privateKey = "";
		private String notifyUrl = "";
		private String returnUrl = "";
	}

	@Data
	public static class Wechat {
		private boolean enabled = false;
		private String gateway = "https://api.mch.weixin.qq.com";
		private String appId = "";
		private String mchId = "";
		private String merchantSerialNo = "";
		private String privateKey = "";
		private String notifyUrl = "";
	}
}
