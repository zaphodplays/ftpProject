package amaresh.dev.ftp.client;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NIOChannelClient {
	
	private Socket client;
	private String hostName;
	private int port;
	private String fileName;
	
private BufferedWriter out;
	
	private FileChannel fileChannel;
	
	private SocketChannel in;
	
	private InetAddress address;
	
	public NIOChannelClient(String hostName, int port, String fileName) {
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
			
			in = client.getChannel();
			
			out.write(fileName + "\n");
			
			out.flush();
			
			fileChannel = new FileOutputStream(fileName).getChannel();
			
			ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024*250);
			
			//write from network to file
			channel(fileChannel, in, byteBuffer);
			
						

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				out.close();
				in.close();
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void channel(FileChannel fileChannel, SocketChannel network, ByteBuffer buf) throws IOException {
		while(true) {
			buf.clear();
			int r = fileChannel.read(buf);
			if(r == -1) {
				break;
			}
			buf.flip();
			network.write(buf);
		}
	}
	
	public static void main(String args[]) {
		FTPClient client = new FTPClient("localhost", 1234, "amaresh.txt");
	}

}
