package io.netty.example.p2p;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;


public class UDPServer {

	private static Logger   logger = Logger.getLogger(UDPServer.class.getName());
	public static void main(String[] args) {
		//int port = Configuration.getInt("server.port");
		try {
			DatagramSocket serverDatagramSocket = new DatagramSocket(2008);
			logger.info("1. 服务端启动成功！ 服务端口：" + 2008);
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			String sendMessageA = "";
			String sendMessageB = "";
			int portA = 0;
			int portB = 0;
			InetAddress addressA = null;
			InetAddress addressB = null;
			
			String localAddressA=null; 
			int localPortA=0; 
            int intranetFlag=0;
			while(true) {
				//从此套接字接收数据报包
				serverDatagramSocket.receive(packet);
				//接收信息
				String receiveMessage = new String(packet.getData(), "UTF-8");
				logger.info("2.从套接字接收数据报包： " + packet.getAddress() +":" +packet.getPort() +" 内容:"+ receiveMessage);
				// 接收到clientA
				if (receiveMessage.contains("ClientA")) {
					portA = packet.getPort();
					addressA = packet.getAddress();
					sendMessageA = "host:" + addressA.getHostAddress() + ",port:" + portA;
					
					String[] datas=receiveMessage.trim().split(";");
					localAddressA=datas[1];
					localPortA=Integer.valueOf(datas[2]);
				}

				// 接收到clientB
				if (receiveMessage.contains("ClientB")) {
					portB = packet.getPort();
					addressB = packet.getAddress();
					sendMessageB = "host:" + addressB.getHostAddress()
							+ ",port:" + portB ;
 
				}
				// 两个都接收到后分别A、B址地交换互发
				if (!sendMessageA.equals("") && !sendMessageB.equals("")) {
					if (addressA.getHostAddress().equals(addressB.getHostAddress())){
						intranetFlag=1;
					}
					sendMessageB=sendMessageB+",intranetFlag:"+intranetFlag;
					sendMessageA=sendMessageA+",intranetFlag:"+intranetFlag;
					if (intranetFlag==1){
						sendMessageA=sendMessageA+",localHostA:"+localAddressA+",localPortA:"+localPortA;
					}
					logger.info("3. 两个都接收到后分别A、B址地交换互发");
					sendA(sendMessageB, portA, addressA, serverDatagramSocket);
					sendB(sendMessageA, portB, addressB, serverDatagramSocket);
					
					sendMessageA = "";
					sendMessageB = "";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendB(String sendMessageA, int portA,
			InetAddress addressA, DatagramSocket server) {
		try {
			byte[] sendBuf = sendMessageA.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, addressA, portA);
			server.send(sendPacket);
			logger.info("4.1 .给B发送消息： " + addressA.getHostAddress() +":" + portA +" 内容:"+ sendMessageA);
			logger.info("4.1.1 给B发送消息成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendA(String sendMessageB, int portB,
			InetAddress addressB, DatagramSocket server) {
		try {
			byte[] sendBuf = sendMessageB.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, addressB, portB);
			server.send(sendPacket);
			logger.info("4.2 .给A发送消息： " + addressB.getHostAddress() +":" + portB +" 内容:"+ sendMessageB);
			logger.info("4.2.1 给A发送消息成功!");
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}