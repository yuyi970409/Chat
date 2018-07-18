package chat;

import java.net.*;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class chatClient extends JFrame implements Runnable, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7103711638195532808L;
	// private static final int SERVICE_PORT=13;
	int SERVICE_PORT;
	DataInputStream in;
	DataOutputStream out;
	ServerSocket server;
	Socket socket;
	String hostname;
	JTextField textField, nameField, portField;
	JTextArea textArea, textArea2;
	JButton button, fButton;
	JFileChooser chooser;
	MyradioButton conButton, disButton;
	ButtonGroup group;
	Font font = new Font("Adobe 黑体 Std", 0, 16);
	JLabel label;

	public chatClient() {
		this.setTitle("Client");
		init();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setBounds(0, 0, 800, 500);
		this.setResizable(false);
		// this.setLocationRelativeTo(null);
		// this.setUndecorated(true);
		this.setLocation(200, 200);
		this.setVisible(true);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);

				if (!conButton.isSelected())
					continue;
				SERVICE_PORT = Integer.valueOf(portField.getText()).intValue();
				// hostname=nameField.getText();
				socket = new Socket(hostname, SERVICE_PORT);
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				textArea.append("收到并接受客户端的连接请求!\n");
				while (true) {
					String strUTF = in.readUTF();
					if (strUTF.equals("&&close&&")) {
						disButton.setSelected(true);
						out.writeUTF("&&close&&");
						break;
					}
					if (strUTF.equals("&&file.start&&")) {
						String fileName = in.readUTF();
						chooser.setSelectedFile(new File(fileName));
						int returnVal = chooser.showSaveDialog(this.getContentPane());
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile().getAbsolutePath());
							int data;
							while (-1 != (data = in.readInt())) {
								fos.write(data);
							}
							System.out.println("文件接受完毕");
							textArea.append(fileName + "接受完毕!\n");
							fos.close();
						}
					} else
						textArea.append(strUTF + "\n");
				}
				in.close();
				out.close();
				textArea.append("连接已断开!\n");
			} catch (Exception e) {
			}
		}
	}

	void init() {
		Container con = this.getContentPane();

		label = new JLabel();
		label.setBounds(0, 0, 800, 500);

		button = new JButton("发送消息");
		button.setFont(font);
		button.setBounds(670, 425, 110, 30);
		button.addActionListener(this);
		con.add(button);

		fButton = new JButton("选择文件");
		fButton.setFont(font);
		fButton.setBounds(550, 425, 110, 30);
		fButton.addActionListener(this);
		con.add(fButton);

		textField = new JTextField();
		textField.setBounds(10, 375, 780, 40);
		textField.setFont(font);
		textField.setOpaque(false);
		textField.setBorder(null);
		textField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						out.writeUTF(textField.getText());
						textField.setText("");
					} catch (IOException e2) {
					}
				}
			}
		});
		con.add(textField);

		// add
		nameField = new JTextField();
		nameField.setBounds(235, 20, 120, 25);
		nameField.setText("locallhost");
		nameField.setFont(font);
		nameField.setOpaque(false);
		nameField.setBorder(null);
		con.add(nameField);

		portField = new JTextField();
		portField.setBounds(440, 20, 120, 25);
		portField.setText("1000");
		portField.setFont(font);
		portField.setOpaque(false);
		portField.setBorder(null);
		con.add(portField);

		conButton = new MyradioButton("监听");
		disButton = new MyradioButton("断开");
		disButton.setSelected(true);
		conButton.setBounds(610, 17, 85, 30);
		disButton.setBounds(700, 17, 85, 30);
		conButton.addActionListener(this);
		disButton.addActionListener(this);
		group = new ButtonGroup();
		group.add(conButton);
		group.add(disButton);
		con.add(conButton);
		con.add(disButton);
		//

		textArea = new JTextArea();
		textArea.setBounds(426, 85, 347, 265);
		textArea.setFont(font);
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setBorder(null);
		con.add(textArea);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.setBounds(426, 85, 347, 265);
		con.add(scrollPane);

		textArea2 = new JTextArea();
		textArea2.setBounds(36, 85, 347, 265);
		textArea2.setFont(font);
		textArea2.setEditable(false);
		textArea2.setOpaque(false);
		textArea2.setBorder(null);
		con.add(textArea2);
		JScrollPane scrollPane2 = new JScrollPane(textArea2);
		scrollPane2.setOpaque(false);
		scrollPane2.setBorder(null);
		scrollPane2.setBounds(36, 85, 347, 265);
		con.add(scrollPane2);

		chooser = new JFileChooser();

		label.setIcon(new ImageIcon("/Users/yuyi/java/talkonTCP/src/chat/icon"));
		con.add(label);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button)
			try {
				out.writeUTF(textField.getText());
				textArea2.append(textField.getText() + "\n");
				textField.setText("");
			} catch (IOException e2) {
			}
		else if (e.getSource() == fButton) {
			try {
				int returnVal = chooser.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String filePath = chooser.getSelectedFile().getAbsolutePath();
					String fileName = chooser.getSelectedFile().getName();

					out.writeUTF("&&file.start&&");
					out.writeUTF(fileName);
					textArea.append("开始发送" + fileName + "...\n");
					FileInputStream fileInputStream = new FileInputStream(filePath);
					int data;
					while (-1 != (data = fileInputStream.read())) {
						out.writeInt(data);
					}
					out.writeInt(data);
					fileInputStream.close();
					System.out.println("文件已发送完毕");
					textArea.append(fileName + "发送完毕!\n");
				}
			} catch (IOException e3) {
			}
		} else if (e.getSource() == disButton) {
			try {
				out.writeUTF("&&close&&");
			} catch (IOException e2) {
			}
		}
	}

}