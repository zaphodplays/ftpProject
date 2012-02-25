package amaresh.dev.ftp.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SocketChannel;
/**
 * 
 * @author Amaresh
 *
 */
public class FTPServer {
	
	private String defaultPath;
	
	private ServerSocket serverSocket;
	
	public FTPServer(String defaultPath) {
		this.defaultPath = defaultPath;	
		try {
			serverSocket = new ServerSocket(1234);
			while(true) {
				Socket client = serverSocket.accept();
				Thread t = new Thread(new FTPServerThread(client));
				t.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class FTPServerThread implements Runnable {
		
		private Socket clientSocket;
		
		private PrintWriter printWriter;
		
		private BufferedReader in;
		
		private OutputStream out;
		
		String fileName;
		
		public FTPServerThread(Socket client) {
			this.clientSocket = client;
			try {
				
				out = clientSocket.getOutputStream();
				out.flush();
				in = new BufferedReader((new InputStreamReader(clientSocket.getInputStream())));
				
				
				fileName = in.readLine();
				fileName = defaultPath + fileName;
				//in.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
		
		public void run() {
			try {
				InputStream fileIn = new FileInputStream(fileName);
				stream(fileIn, out);
				
				System.out.println("CLOSING OUT SOCKET STREAM");
				//out.close();
				//in.close();
				
				//clientSocket.close();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				try {
					in.close();
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	class NIOServerThread implements Runnable {
		
		private Socket clientSocket;
		
		private BufferedReader in;
		
		private FileChannel fileIn;
		
		private SocketChannel out;
		
		private String fileName;
		
		public NIOServerThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			try {
				out = clientSocket.getChannel();
				
				in = new BufferedReader((new InputStreamReader(clientSocket.getInputStream())));
				
				
				fileName = in.readLine();
				fileName = defaultPath + fileName;
				
				fileIn = new FileInputStream(fileName).getChannel();
				
				ByteBuffer buf = ByteBuffer.allocate(1024*1024*50);
				
				channel(fileIn, out, buf);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					in.close();
					out.close();
					fileIn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
			
			
		}
		
	}
	
	public static void stream(InputStream in, OutputStream out) throws IOException{
		byte[] buf = new byte[1024*1024];
		int bytesRead = 0;
		try {
			while((bytesRead = in.read(buf, 0, buf.length)) != -1) {
				out.write(buf, 0, bytesRead);
			}
		}
		catch(IOException ie) {
			System.out.println("ERROR in stream transfer");
			throw ie;
		}
		finally {
			out.flush();
			out.close();
			in.close();
		}
	}
	
	public static void channel(FileChannel in, SocketChannel out, ByteBuffer buf) throws IOException {
		while(true) {
			buf.clear();
			int r = in.read(buf);
			if(r == -1) {
				break;
			}
			buf.flip();
			out.write(buf);
		}
	}
	
	
	
	public static void main(String args[]) {
		
		FTPServer server = new FTPServer("/Users/amareshshukla/Documents/workspaceEE/");
	}

}