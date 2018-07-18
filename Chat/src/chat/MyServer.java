package chat;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

import AES.AES;
import AES.ParseSystemUtil;
import RSA.RSAUtils;

/**
 * 简单聊天软件的服务器
 */
public class MyServer extends JFrame implements Runnable, ActionListener {

	JTextArea jTextArea = null;// 用来显示纯文本的单行区域
	JTextField jTextField = null;// 可以允许用来编辑单行文本
	JTextField jTextIP = null;
	JTextField jTextPort = null;
	JButton sendButton = null;
	JButton sendfButton = null;
	JPanel jPanel = null;
	JScrollPane jScrollPane = null;
	// 把信息发给客户端对象
	PrintWriter printWriter = null;
	JTextField textfield=null;

	int pwdseed=(int)(Math.random()*1000);
	String password=String.valueOf(pwdseed);
	String publicKey;
	/**
	 * 服务端的主函数
	 */
	// public static void main(String[] args) {
	// // TODO Auto-generated method stub
	// new MyServer();
	// }

	/**
	 * 服务端的构造函数,用来进行初始化
	 */
	public MyServer() {
		// 这里是对GUI的初始化
		jTextArea = new JTextArea();
		jTextField = new JTextField(20);
		sendButton = new JButton("发送");
		sendfButton = new JButton("发送文件");
		sendButton.addActionListener(this);
		sendfButton.addActionListener(this);

		sendButton.setActionCommand("send");
		sendfButton.setActionCommand("sendf");
		jScrollPane = new JScrollPane(jTextArea);
		jPanel = new JPanel();

		jPanel.add(jTextField);// 添加编辑框
		jPanel.add(sendButton);// 添加按钮
		jPanel.add(sendfButton);
		// 将两个面板添加布局
		this.add(jScrollPane, BorderLayout.CENTER);
		this.add(jPanel, BorderLayout.SOUTH);

		this.setSize(600, 300);
		this.setTitle("聊天服务器");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置退出按钮
		this.setVisible(true);
		this.setResizable(true);
	}

	/**
	 * 用来获取当前的时间
	 * 
	 * @return 当前的时间
	 */
	public String getTime() {
		// 可以对每个单独时间域进行修改
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);// 获取小时
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		return hour + ":" + minute + ":" + second;
	}
	
	// 公钥加密
	public static byte[] encrypt(byte[] content, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");// java默认"RSA"="RSA/ECB/PKCS1Padding"
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(content);
	}

	public void run() {
		// 下面是socket服务器的搭建
		try {
			// 服务器监听
			ServerSocket ss = new ServerSocket(3030);
			// 等待客户端连接
			Socket socket = ss.accept();
			// 获得客户端发送过来的数据的流
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			printWriter = new PrintWriter(socket.getOutputStream(), true);
			publicKey = br.readLine();
			// System.out.println(publicKey);
			try {
				String encodedData = RSAUtils.publicEncrypt(password, RSAUtils.getPublicKey(publicKey));
				printWriter.println(encodedData);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 读取从客户端发送过来的信息
			while (true) {

				String info = br.readLine();
				// System.out.println(info);

				String a[] = info.split(",");
				String info2 = new String(a[0]);
				String digest = new String(a[1]);

				// System.out.println(info2);
				// System.out.println(digest);

				byte[] info3 = ParseSystemUtil.toBytes(info2);
				byte[] decrypt = AES.decrypt(info3, password);

				String digest2 = MD5.MD5.getMD5(decrypt);
				// System.out.println(digest2);

				if (digest.equals(digest2)) {
					jTextArea.append("客户端 " + getTime() + "\r\n" + new String(decrypt) + "\r\n");
				} else {
					JOptionPane.showMessageDialog(null, "消息不完整", "alert", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 当button被点击的时候调用
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// 当按钮按下的时候调用
		if (e.getActionCommand().equals("send")) {
			// 把服务器在jTextField写的内容发送给客户端
			String info = jTextField.getText();

			byte[] encrypt = AES.encrypt(info, password);
			// System.out.println(encrypt);
			String hexStrResult = ParseSystemUtil.bytesToHexFun1(encrypt);
			// System.out.println(hexStrResult);

			String digest = MD5.MD5.getMD5(info.getBytes());
			// System.out.println(digest);

			String string = hexStrResult + "," + digest;
			// System.out.println(string);

			jTextArea.append("服务器 " + getTime() + "\r\n" + info + "\r\n");
			printWriter.println(string);
			// 清楚发送框内容
			jTextField.setText("");
		} else if (e.getActionCommand().equals("sendf")) {
			printWriter.println("传输文件");
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);  
			fileChooser.showDialog(new JLabel(), "选择");  
	        File file = fileChooser.getSelectedFile();  
	        String name = fileChooser.getSelectedFile().getName();
	        
	        System.out.println(file);
	        System.out.println(name);
		}
	}
}