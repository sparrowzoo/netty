package io.netty.example.p2p;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * 客户端B
 * 
 * @author ligc
 * 153277817@qq.com
 */
public class UDPClientB {
	
	public static ExecutorService pool = Executors.newCachedThreadPool();
	private static Logger   logger = Logger.getLogger(UDPClientB.class.getName());
	public static boolean isNat = false;
	public static AtomicInteger isSendCmd = new AtomicInteger(0);
	public static void main(String[] args) {
		//String serverHost="112.124.110.162";
		String serverHost="localhost";
		if (args.length>=1){
			serverHost= args[0];
		}		
		startClientB(serverHost);
		
		
 
//		String localHostA="192.168.0.73";
//		//String localHostA="192.168.0.228";
//		String localPortA="3008";
//		DatagramSocket client;
//		try {
//			client = new DatagramSocket();
//			sendMessageIntranet(client, localHostA,localPortA);
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}

	private static void startClientB(String serverHost) {
		try {
			
			//logger.info("1. 客户端B启动成功！");
			// 向server发起请求
			SocketAddress target = new InetSocketAddress(serverHost, 2008);
			DatagramSocket client = new DatagramSocket();
			String message = "Server, I am ClientB;";
//			String[] ipArr=NetUtils.getLoaclIP();
//			String ipStr=null;
//			for (String ip : ipArr) {
//				ipStr=(ipStr==null)?ip:(ipStr+","+ip);
//			}
//	 
//			message=message+ipStr+";"+"0";
			
			byte[] sendbuf = message.getBytes();
			DatagramPacket pack = new DatagramPacket(sendbuf, sendbuf.length,
					target);
			logger.info("1. 连接服务器， 目标：" +pack.getAddress() +":"+ pack.getPort()  + "内容：" +new String(pack.getData()));
			client.send(pack); 
			// 接收server的回复内容
			byte[] buf = new byte[1024];
			DatagramPacket recpack = new DatagramPacket(buf, buf.length);
			client.receive(recpack);
			//logger.info("4. 处理server回复的内容，然后向内容中的地址与端口发起请求（打洞）");
			String receiveMessage = new String(recpack.getData(), 0, recpack.getLength());
			logger.info("2. 接收server的回复内容：" +recpack.getAddress() +":"+ recpack.getPort()  + "内容：" +receiveMessage);
			
			
			String[] params = receiveMessage.split(",");
			String host = params[0].substring(5);
			String port = params[1].substring(5);
			String intranetFlag=params[2].substring("intranetFlag:".length());
			//非局域网时
			if ("0".equals(intranetFlag)){
				sendMessage(host, port,client);
			}else{
				String localHostA=params[3].substring("localHostA:".length());
				String localPortA=params[4].substring("localPortA:".length());
				sendMessageIntranet(client, localHostA,localPortA);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendMessageIntranet(DatagramSocket client, String localHostA,String localPortA)
			throws UnsupportedEncodingException, IOException {
		logger.info("4. 本地局域网直接连接请求A");
		String reportMessage="9. 直接发送直连测试数据";

		
//				String[] localArr=localHostA.split(",");
//				for (String localA : localArr) {
		logger.info("目标地址："+localHostA+",目标端口："+localPortA+",发送内容:"+reportMessage);
			SocketAddress targetA = new InetSocketAddress(localHostA, Integer.valueOf(localPortA));
			byte[] sendBuf = reportMessage.getBytes("UTF-8");
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length,targetA);
			client.send(sendPacket);
			
			receive(client);
//				}
	}

	/**
	 * 向UPDClientA发起请求(在NAT上打孔)
	 */
	private static void sendMessage(String host, String port,
			DatagramSocket client) {
   //private static void sendMessage(String host, String port ) {		
		try {
			//logger.info("5. 向UPDClientA发起请求(在NAT上打孔)");
			SocketAddress target = new InetSocketAddress(host, Integer.parseInt(port));
			//DatagramSocket client = new DatagramSocket();
			while(!isNat){
				String message = "我是B,发送连接命令!";
				byte[] sendbuf = message.getBytes("UTF-8");
				DatagramPacket pack = new DatagramPacket(sendbuf,sendbuf.length, target);
				client.send(pack);
				logger.info("3. 向A发送数据：" + pack.getAddress() +":"+ pack.getPort()  + "内容：" +message);
				// 等待接收UDPClientA回复的内容
				receive(client);
				Thread.sleep(500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**收到UDPClientA的回复内容，穿透已完成*/
	private static void receive(DatagramSocket client) {
		final DatagramSocket clientF = client;
		pool.execute(new Runnable(){
			@Override
			public void run() {
				try {
					
					for (;;) {
						// 将接收到的内容打印
						byte[] buf = new byte[1024];
						DatagramPacket recpack = new DatagramPacket(buf, buf.length);
						clientF.receive(recpack);
						//logger.info("7. 收到UDPClientA的回复内容，穿透已完成!");
						//System.out.println("recpack.getLength():" + recpack.getLength() +"---:---"+ isNat);
						if(recpack.getLength() >1){
							
							
							
							String receiveMessage = new String(recpack.getData(), 0,
									recpack.getLength(), "UTF-8");
						 	 
							logger.info((isNat?6:4)+". 接收A发来的信息：" +recpack.getAddress() +":"+ recpack.getPort()  + "内容：" +receiveMessage +(isNat?"":"\r\n穿透已完成--------------") );
							//logger.info("8. 接收A发来的信息2：" + receiveMessage);
							
						    if (!isNat){	
								// 记得重新收地址与端口，然后在以新地址发送内容到UPDClientA,就这样互发就可以了。
								//logger.info("9. 发送控制命令。");
								int port = recpack.getPort();
								InetAddress address = recpack.getAddress();
								
								String reportMessage = "我是B,模拟发送控制命令！";
								// 发送消息
							/*	System.out.print("B:请输入发送内容： ");
								Reader in = new InputStreamReader(System.in);
								BufferedReader br = new BufferedReader(in);
								String reportMessage = br.readLine();
								System.out.println("B:输入内容为：" + reportMessage);*/
								sendMessageTTT(reportMessage, port, address, clientF);
						   }
						   isNat = true;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}

	private static synchronized void sendMessageTTT(String reportMessage, int port,
			InetAddress address, DatagramSocket client) {
		try {
			//DatagramSocket clientNew = new DatagramSocket();
			//logger.info("10. 向A发送数据");
			if (isSendCmd.get()==0){
				byte[] sendBuf = reportMessage.getBytes("UTF-8");
				DatagramPacket sendPacket = new DatagramPacket(sendBuf,
						sendBuf.length, address, port);
				client.send(sendPacket);
				logger.info("5 . 向A发送信息：" +sendPacket.getAddress() +":"+ sendPacket.getPort()  + "内容：" +new String(sendPacket.getData()));
				isSendCmd.set(1);
				receive(client);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
