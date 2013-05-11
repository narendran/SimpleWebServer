package com.naren.projects.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * The main method in the {@link SimpleWebServer} class just waits for incoming connections. 
 * Once a client establishes a connection with the Socket, we hand over the connection socket to this HttpServerThread which will take care of serving the pages which the client asks for. 
 * @author nart
 *
 */
public class HttpServerThread extends Thread {
	
	Socket connection = new Socket();
	BufferedReader inputFromClient;
	OutputStream outputToClientStream;
	BufferedWriter outputToClient;
	Properties serverProps;
	
	/**
	 * Constructor that gets the clientSocket as input and from that point onwards takes care of processing the requests and sending back responses for that client.
	 * @param connectionSocket
	 */
	public HttpServerThread(Socket connectionSocket, Properties serverProps){
		this.connection = connectionSocket;
		this.serverProps = serverProps;
	}
	
	public void run(){
		System.out.println("The client's socket connection is delegated to the thread ID : "+Thread.currentThread().getId());
		try {
			outputToClientStream = this.connection.getOutputStream();
			outputToClient = new BufferedWriter(new OutputStreamWriter(this.outputToClientStream));
			inputFromClient = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
			String httpRequest = inputFromClient.readLine();
			StringTokenizer tokenizer = new StringTokenizer(httpRequest);
			if(tokenizer.countTokens()==0){
				outputToClient.write("<html><head><title>Welcome to Light!</title></head><body>This is the landing page of Light server..<br/><b><font color=\"red\">You are redirected to this page since the request sent by your client was invalid</font></b></body></html>");
				outputToClient.close();
			} else {
				String httpMethod = tokenizer.nextToken();
				String httpPath = tokenizer.nextToken();
				String httpVersion = tokenizer.nextToken();
				
				// DEBUG
				System.out.println("Path : "+httpPath);
				String rootDirectory = this.serverProps.getProperty("rootDirectory").isEmpty() ? "root" : this.serverProps.getProperty("rootDirectory");
				StringBuffer actualPath = new StringBuffer(rootDirectory);
				actualPath.append(httpPath);
				System.out.println("Constructed path : "+actualPath.toString());
				
				// Before you start processing the request, check if the request is for index page.
				if(httpPath.equals("/")){
					outputToClient.write("<html><head><title>Welcome to Light!</title></head><body>This is the landing page of Light server..<br/><b><font color=\"green\">You have made a GET request to the Light server. Server is processing your request..</font></b></body></html>");
					outputToClient.close();
				} else {
					File f = new File(actualPath.toString());
					if(f.exists() && f.isFile()){
						int bytesRead = 0;
						byte[] fileBuffer = new byte[1024];
						FileInputStream fis = new FileInputStream(f);
						while((bytesRead = fis.read(fileBuffer)) > 0){
							outputToClientStream.write(fileBuffer,0,bytesRead);
						}
						outputToClientStream.close();
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
