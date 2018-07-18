package test;


import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import chat.chatClient;
import chat.chatServer;

public class mainTest {

	public static void  main(String[] args) {
		try {
			for(LookAndFeelInfo info:UIManager.getInstalledLookAndFeels()){
				if("Windows".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		chatServer server=new chatServer();
		chatClient client=new chatClient();
		new Thread(server).start();
		new Thread(client).start();
	
	}
}
