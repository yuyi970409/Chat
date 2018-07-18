package Test;

import MD5.*;

public class MD5test {
	// 计算 "a" 的 MD5 代码，应该为：0cc175b9c0f1b6a831c399e269772661
	public static void main(String xu[]) {
		System.out.println(MD5.getMD5("2123123".getBytes()));
	}
}
