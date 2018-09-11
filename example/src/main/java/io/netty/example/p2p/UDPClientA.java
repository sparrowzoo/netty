package io.netty.example.p2p;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * 客户端A 
 * @author ligc 
 * 153277817@qq.com
 *
 */
public class UDPClientA {
	
	private static Logger   logger = Logger.getLogger(UDPClientA.class.getName());
	public static ExecutorService pool = Executors.newCachedThreadPool();
	public static int initLocalServerPort=3008;
	public static int localServerPort=3008;
	public static boolean isNat=false;
	public static void main(String[] args) {
		//String serverHost="112.124.110.162";
		String serverHost="localhost";
		if (args.length>=1){
			serverHost= args[0];
		}
		startClientA(serverHost);
		//用于局域网直接连接
		startServerA();

	}
	/**
	 * 用于局域网直接连接
	 * @param serverHost
	 */
	private static void startServerA() {
		pool.execute(new Runnable(){
				@Override
				public void run() {
						//int port = Configuration.getInt("server.port");
						try {
							localServerPort=NetUtils.findFreePort(initLocalServerPort);
							DatagramSocket serverDatagramSocket = new DatagramSocket(localServerPort);
							logger.info("11.客户端的局域网直连监听启动成功！ 服务端口：" + localServerPort);
							byte[] buf = new byte[1024];
							DatagramPacket packet = new DatagramPacket(buf, buf.length);
							//while(true) {
								//从此套接字接收数据报包
								serverDatagramSocket.receive(packet);
								//接收信息
								String receiveMessage = new String(packet.getData(), "UTF-8").trim();
								logger.info("22.局域网直连从套接字接收数据报包： " + packet.getAddress() +":" +packet.getPort() +" 内容:"+ receiveMessage);
								//回复
								int port = packet.getPort();
								InetAddress address = packet.getAddress();
						 
								String reportMessage = "我是A，这是我发送的局域网直连测试数据！";
								byte[] sendBuf = reportMessage.getBytes("UTF-8");
								DatagramPacket sendPacket = new DatagramPacket(sendBuf,
										sendBuf.length, address, port);
								serverDatagramSocket.send(sendPacket);
								logger.info("33.局域网直连回复消息");
							//}
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
		});
	}
	private static void startClientA(String serverHost) {
		try {

			//logger.info("1. 客户端A启动");
			// 向server发起请求
			SocketAddress target = new InetSocketAddress(serverHost, 2008);
			DatagramSocket client = new DatagramSocket();
			String message = "I am ClientA;";
//			String[] ipArr=NetUtils.getLoaclIP();
//			String ipStr=null;
//			for (String ip : ipArr) {
//				ipStr=(ipStr==null)?ip:(ipStr+","+ip);
//			}
	        String ipStr=UserIP.getLocalIP();
			message=message+ipStr+";"+localServerPort;
			byte[] sendbuf = message.getBytes("UTF-8");
			DatagramPacket pack = new DatagramPacket(sendbuf, sendbuf.length,target);
			logger.info("1. 连接服务器 目标：" +pack.getAddress() +":"+ pack.getPort()  + "内容：" +new String(pack.getData()));
			client.send(pack);
			receive(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 接收请求 （接收请求的回复,可能不是server回复的，有可能来自UPDClientB的请求内）
	 * @param client
	 */
	private static void receive(DatagramSocket client) {
		final DatagramSocket clientf = client;
		pool.execute(new Runnable(){
			@Override
			public void run() {
				try {
					int i=0;
					for (;;) {
						i++;
						//logger.info("current i:"+i);
						byte[] buf = new byte[1024];
						DatagramPacket packet = new DatagramPacket(buf, buf.length);
						//logger.info("receiving");
						clientf.receive(packet);
						//logger.info("received!");
						String receiveMessage = new String(packet.getData(), 0, packet.getLength(),"UTF-8").trim();
						
						
						
						//logger.info("3. A接收数据2： " + receiveMessage);
						
						if (receiveMessage.startsWith("host:")){				 
							logger.info("2. 接收Server的回复内容：" +packet.getAddress() +":"+ packet.getPort()  + "内容：" +receiveMessage );
							//向B发送消息
							String[] params = receiveMessage.split(",");
							String host = params[0].substring(5);
							String port = params[1].substring(5);
							String intranetFlag=params[2].substring("intranetFlag:".length());
							//非局域网时
							if ("0".equals(intranetFlag)){
								sendMessaage2B(port,host,clientf);
							}
							//如果是局域网，等待clientB连接本地server端口
						}else{
							//回复
							logger.info("穿透已完成，已收到B的请求-----------------------------");
							isNat=true;
							int port = packet.getPort();
							InetAddress address = packet.getAddress();
							String reportMessage=null;
							if (receiveMessage.indexOf("控制命令")>=0){
								logger.info("6. 接收B的控制命令：" +packet.getAddress() +":"+ packet.getPort()  + "内容：" +receiveMessage );
								reportMessage = "我是A，响应B的控制命令！";
								sendMessaage(reportMessage, port, address, clientf,"7. 响应B的控制命令");
							}else{
								logger.info("4. 接收B的链接请求：" +packet.getAddress() +":"+ packet.getPort()  + "内容：" +receiveMessage ); 
								reportMessage = "我是A,响应B的链接请求！";		
								sendMessaage(reportMessage, port, address, clientf,"5. 响应B的链接请求");
							}
							
							
						 
							
						}	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}
	/**
	 * 回复内容  （获取接收到请问内容后并取到地址与端口,然后用获取到地址与端口回复内容）
	 * @param reportMessage
	 * @param port
	 * @param address
	 * @param client
	 */
	private static void sendMessaage2B( String port,
			String host, DatagramSocket client) {
		   //private static void sendMessage(String host, String port ) {		
				try {
					//logger.info("55. 向UPDClientB发起请求(为NAT打孔做准备)");
					SocketAddress target = new InetSocketAddress(host, Integer.parseInt(port));
					//DatagramSocket client = new DatagramSocket();
					//while(!isNat){
					for (int i=0;i<1;i++){
						String message = "我是A,发送连接命令！";
						byte[] sendbuf = message.getBytes("UTF-8");
						DatagramPacket pack = new DatagramPacket(sendbuf,sendbuf.length, target);
						client.send(pack);
						logger.info("3. 向B发送数据,NAT打洞：" + pack.getAddress() +":"+ pack.getPort()  + "内容：" +new String(pack.getData()));
						// 等待接收UDPClientA回复的内容
						receive(client);
						//Thread.sleep(500);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	}

	
	
	/**
	 * 回复内容  （获取接收到请问内容后并取到地址与端口,然后用获取到地址与端口回复内容）
	 * @param reportMessage
	 * @param port
	 * @param address
	 * @param client
	 */
	private static void sendMessaage(String reportMessage, int port,
			InetAddress address, DatagramSocket client,String msg) {
		try {
			
			//logger.info("4. 获取接收到请问内容后并取到地址与端口,然后用获取到地址与端口回复内容");
			byte[] sendBuf = reportMessage.getBytes("UTF-8");
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, address, port);
			client.send(sendPacket);
			logger.info(msg + sendPacket.getAddress() +":"+sendPacket.getPort() +"  内容："+ new String(sendPacket.getData()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
