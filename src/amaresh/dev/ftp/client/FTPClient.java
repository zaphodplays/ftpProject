package amaresh.dev.ftp.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class FTPClient {
	
	private Socket client;
	private String hostName;
	private int port;
	private String fileName;
	
	private BufferedWriter out;
	
	private FileOutputStream fileOut;
	
	private InputStream in;
	
	private InetAddress address;
	
	public FTPClient(String hostName, int port, String fileName) {
		this.hostName = hostName;
		this.port = port;
		try {
			address = Inet4Address.getByName(hostName);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.fileName = fileName;
		
		try {
			client = new Socket(address, port);
			
			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			
			in = client.getInputStream();
			
			out.write(fileName + "\n");
			
			out.flush();
			
			fileOut = new FileOutputStream(fileName);
			fileOut.flush();
			
			stream(in, fileOut);
						

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				out.close();
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void stream(InputStream in, OutputStream out) throws IOException{
		byte[] buf = new byte[1024*1024*250];
		int bytesRead = 0;
		int totalBytesStoredInBuf = 0;
		try {
			while((bytesRead = in.read(buf, 0, buf.length)) != -1) {
				bytesRead = fillBuf(in, buf, bytesRead);
				out.write(buf, 0, bytesRead);
				System.out.println("FLUSHING bytes "+bytesRead);
				out.flush();
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
	
	public static int fillBuf(InputStream in, byte[] buf, int totalBytesStoredInBuf) throws IOException{
		while(totalBytesStoredInBuf < buf.length) {
			int len = buf.length - totalBytesStoredInBuf;
			int offSet = totalBytesStoredInBuf;
			int bytesRead = in.read(buf, offSet, len);
			if(bytesRead == -1) {
				return totalBytesStoredInBuf;
			}
			totalBytesStoredInBuf = totalBytesStoredInBuf + bytesRead;
		}
		return totalBytesStoredInBuf;
	}
	
	public static void main(String args[]) {
		FTPClient client = new FTPClient("192.168.0.5", 1234, "matrix3.mkv");
	}

}
