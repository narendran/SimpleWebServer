package com.naren.projects.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

/**
 * This class waits for client connections and delegates the control of the socket connection to {@link HttpServerThread} class.
 * @author nart
 *
 */
public class SimpleWebServer 
{
	
	static Properties serverProps = new Properties();
	
	/**
	 * Read the properties file and start the server socket.
	 * If its run as a java program it expects it in src/main/resources,
	 * If its run from a jar, it expects in to be loaded by a class loader. Accommodate all forms of execution
	 */
	static{
		try {
			URL filePath = Thread.currentThread().getContextClassLoader().getResource("server.properties");
			System.out.println(filePath);
			File propFile = new File(filePath.getFile());
			if(!propFile.exists()){
				propFile = new File("src/main/resources/server.properties");
			}
			serverProps.load(new FileInputStream(propFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static void main( String[] args )
    {
        try {
        	int serverPort = Integer.parseInt(serverProps.get("listenPort").toString());
        	// default server port to 9000
        	serverPort = serverPort==0 ? 9000 : serverPort;
			ServerSocket serverSocket = new ServerSocket(serverPort, 20, InetAddress.getByName("127.0.0.1"));
			System.out.println("Starting the server on port "+serverPort+". Waiting for client requests on the server thread ID "+Thread.currentThread().getId());
			while(true){
				Socket connection = serverSocket.accept();
				System.out.println("A client connected to the server. Details : "+connection.getInetAddress()+" and port "+connection.getPort());
				new HttpServerThread(connection,serverProps).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
