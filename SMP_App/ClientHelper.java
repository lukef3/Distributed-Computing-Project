import java.net.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * This class is a module which provides the application logic
 * for an Echo client using stream-mode socket.
 * @author M. L. Liu
 */

public class ClientHelper {
   static String endMessage = ".";
   private MyStreamSocket mySocket;
   private InetAddress serverHost;
   private int serverPort;

   ClientHelper(String hostName, String portNum) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
      this.serverHost = InetAddress.getByName(hostName);
      this.serverPort = Integer.parseInt(portNum);
      //Instantiates a stream-mode socket and wait for a connection.
   	  this.mySocket = new MyStreamSocket(this.serverHost, this.serverPort);
   } // end constructor

   private String sendAndReceive(String request) throws IOException {
      mySocket.sendMessage(request);
       return mySocket.receiveMessage();
   }

   public String login(String username, String password) throws IOException {
      String request = "100:" + username + "," + password;
      return sendAndReceive(request);
   }

   public String logOff() throws IOException {
      String request = "200:";
      return sendAndReceive(request);
   }

   public String uploadMessage(String username, String message) throws IOException {
      String request = "300:" + username + "," + message;
      return sendAndReceive(request);
   }

   public String downloadMessage(String messageId) throws IOException {
      String request = "400:" + messageId;
      return sendAndReceive(request);
   }

   public String downloadAllMessages() throws IOException {
      String request = "500:";
      return sendAndReceive(request);
   }

   public void done() throws IOException{
      mySocket.close();
   } // end done 
} //end class
