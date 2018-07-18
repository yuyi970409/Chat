package Test;

import RSA.*;
import AES.*;

public class AEStest {
	public static void main(String[] args) {
		String content = "美女，约吗？";
		String password = "123";
		System.out.println("加密之前：" + content);
		// 加密
		byte[] encrypt = AES.encrypt(content, password);
		System.out.println("加密后的内容：" + new String(encrypt));

		// 如果想要加密内容不显示乱码，可以先将密文转换为16进制
		String hexStrResult = ParseSystemUtil.parseByte2HexStr(encrypt);
		System.out.println("16进制的密文：" + hexStrResult);

		// 如果的到的是16进制密文，别忘了先转为2进制再解密
		byte[] twoStrResult = ParseSystemUtil.parseHexStr2Byte(hexStrResult);

		// 解密
		byte[] decrypt = AES.decrypt(encrypt, password);
		System.out.println("解密后的内容：" + new String(decrypt));
	}
}
