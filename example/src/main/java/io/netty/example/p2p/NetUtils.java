package io.netty.example.p2p;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

public class NetUtils {
	private static Logger   logger = Logger.getLogger(UDPServer.class.getName());
	public static String[] getLoaclIP(){
		List<String> ipList=new ArrayList<String>();
		Enumeration allNetInterfaces = null;  
        try {  
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();  
        } catch (java.net.SocketException e) {  
            e.printStackTrace();  
        }  
        InetAddress ip = null;  
        while (allNetInterfaces.hasMoreElements())  
        {  
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces  
                    .nextElement();  
            //System.out.println(netInterface.getName());  
            Enumeration addresses = netInterface.getInetAddresses();  
            while (addresses.hasMoreElements())  
            {  
                ip = (InetAddress) addresses.nextElement();  
                if (ip != null && ip instanceof Inet4Address)  {    
                	String ipstr=ip.getHostAddress().replace("/", "");
                	if (!"127.0.0.1".equals(ipstr)){
                		ipList.add(ipstr);
                	}
                }  
                if (ip != null && ip instanceof Inet6Address)  {  
                	
                }
            }  
        }  
        String[] ipArr=new String[ipList.size()];
        int i=0;
        for(String ips:ipList){
        	ipArr[i++]=ips;
        }
        return ipArr;
	}
	
	public static int findFreePort(int initPort) {
		ServerSocket tmpSocket = null;
		while (tmpSocket == null) {
			try {
				tmpSocket = new ServerSocket(initPort);
			} catch (Exception e) {

				initPort++;
				logger.info("port "+(initPort-1) + " is used,try new port：" + initPort);
			}
		}
		return initPort;

	}
}
