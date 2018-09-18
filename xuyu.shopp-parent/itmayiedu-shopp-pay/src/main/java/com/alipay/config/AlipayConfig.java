package com.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2016091700529991";

	// 商户私钥，您的PKCS8格式RSA2私钥
	public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDj3fdQbQY4KU0pmjEk8mPB+ssyHOiKVKck7qg9eoZRFCcYJBpmBMPJAkSPkB+WTwwCqxx39uAKK5m6ARu1Uhu2TGlsTZ3IeWU7Sq2nRTrFNh9i60XVG5S5ttr2cLP/2qKVNjld7CjkoWse9294+ifER+Ou77GKdDqR0kYWoQ70wUwTJIraBph64ZSbPf0vJiK8VP3Edt5S0+vKwvk+sP3KOhYe3/OvDyJIQszTscG6W0rZorXGKWJofASJKWg58A46bIpUTrafvumkhNg9JEt9VQymXlOBUDfjF7wO8xVWUMJnbAiiBZuZZeTLVqHY7D0Qz3VPcTzCpiUm4RTSZupVAgMBAAECggEAQd3nn1fkdFd79oqbqhJmw6u7EMFKdrIBnqtd9md0a/tnUZMeDl9SOMhvCRCgENLIpfzQPd0e5dG0ng+z8rXJTOjJkITfFxQALyNnDvL0Cg2bBPz9MpzGYOh6YsIxRcPtOzRxYLTjHTOLw2fC0TV+ST5+khK3P05Zn80odWIY8KZjoAzIQ/gitDIv9+CuzAnPuXzv0z6ecDWhpU0JDL979wmD+wkRv48zo/sM31cTX7NLeQ8IRx0ka3RwI2aXxIf+jnSO/SwRwOrl8Smf8xbr7nx/nGgjYTUzm4P9Kx0C95ATgBSN43ZmRXY4hPlVEQpb+zQatktK426GjtirIZmAAQKBgQD6DHFf/iQCZwTMcoIppOCpygtor2PfVxeP7Nq/TBHGiWl06sgfMtRyksNw44xTRa99f12OsvSPMh0mAQG7PzbUkyk0m9apPKup+3L0oIGwpxu2Xgc7PPTMD/eYXIQAHmcTER4UTHLkM1lVlvDXuoQHTpdmy9PonvwKTkeRhR+pEQKBgQDpSl67EKLtsdmy1XtNvyDF3/REbmR8nuyMzNgITVFCn+BDvJC9BG7AUIrOm8S/A0ifx7/yBNziqe0y92pV6P88zziO/pb8WL33f1iTqAB0ta14imXM5cExpyGJHVLI1Q+W5w9+0g5CsqMSuqSSoJO1rvl9gjxypJ5LUGTWaAbNBQKBgQCEqx1sZRcN/0LYhtjw534cGAs9m/FNipQB1RWq8d1sjXPaiMdtZja6I4x21QSbGhomZ/FnySNg5qH4jAgzJTMeToZKV3Tpjjs2kSdJC/BDniZh+Dm2AH2R19byjAOtXHQr9aUT+AvtcCEx/EgE7f0nM1e5PuRMRC5N+YKXfx5xQQKBgQC8T3jaHRq3LZmhnmsynbXxo85DsqxQX+Rn3y3+vZJ7ahwX8U0QFlTjzd/N2LTezuK/fvi0fjJikHHUcxyVjkcBKe/olR8BQQzOjC/OuIztqH0HUQfNdiQVVWhfipj5XeqAj7DVWO+D7Thu9NPFO/Mfc8cWBZuPmsvf1VwgKcZblQKBgBDRBYk29YNv2oItu7jZmJx5l6+w+7wD8JETNUutoO3EzgGYyrNr9naMm3kkroT5csbb8+iiRAkSZBgQbtpQgLRWNaF/IYysw6zDZJ/kS8ASCVl65on/MDa641i2vhgbUa7JrF++rYEidNwKf4kU3JkWfxf97tMykY8TWzBLCz7K";

	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
	public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvOzrohhGdtqm2+Yn0kWUoP67d0+KWY8QgI1j8mspRKgH8A2M6VmrFHC/g/M8QMomXjsi491l/KusGtsgjJvxQUb2d9ZgHYrHBYtAfbGzIJGwM4tTKbKQdibApBiyJlMfm1jBAz5z+pHrawYZ8ivMEw6LX4PhyOom4dlHVu3e76g7QlpHsEJOqk5YaY3tFoRzRv54+gVxL0RwMTyYyLfCRJ9snjYdCd5V80PiyZrPn6WyWMtgw/pd2YivpbjIWY9H0OfJo2TQWpnpFF6hi3DIyjUARMdvjYc2WuUPYtC71UJlj0yuOdW5Te6Stsqp/ggcvr9HtLXp1rAjEPlcQt/F0QIDAQAB\r\n"
			+ "";

	// 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://itmayiedu.tunnel.qydev.com/callBack/asynCallBack";



	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://itmayiedu.tunnel.qydev.com/callBack/synCallBack";



	// 签名方式
	public static String sign_type = "RSA2";

	// 字符编码格式
	public static String charset = "utf-8";

	// 支付宝网关
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

	// 支付宝网关
	public static String log_path = "C:\\";

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	/**
	 * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
	 * 
	 * @param sWord
	 *            要写入日志里的文本内容
	 */
	public static void logResult(String sWord) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis() + ".txt");
			writer.write(sWord);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
