import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.net.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * A wrapper class of Socket which contains
 * methods for sending and receiving messages
 * @author M. L. Liu
 */
public class MyStreamSocket extends Socket {
   private SSLSocket sslSocket;
   private BufferedReader input;
   private PrintWriter output;

   String tsName = "clientTrustStore.jks";
   char[] tsPassword = "password".toCharArray();

   MyStreamSocket(InetAddress acceptorHost, int acceptorPort ) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
      // create JKS keystore
      KeyStore trustStore = KeyStore.getInstance("JKS");
      // load the truststore from the clientTrustStore file
      trustStore.load(new FileInputStream(tsName), tsPassword);

      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
      trustManagerFactory.init(trustStore);

      // create ssl context
      SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
      // initialise SSL context with the trust store manager factory
      sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      sslSocket = (SSLSocket) sslSocketFactory.createSocket(acceptorHost, acceptorPort);
      setStreams(); // set input and output streams for the socket
   }

   MyStreamSocket(SSLSocket sslSocket)  throws IOException {
      this.sslSocket = sslSocket;
      setStreams();
   }

   private void setStreams() throws IOException{
      // get an input stream for reading from the data socket
      InputStream inStream = sslSocket.getInputStream();
      input = new BufferedReader(new InputStreamReader(inStream));
      OutputStream outStream = sslSocket.getOutputStream();
      // create a PrinterWriter object for character-mode output
      output = new PrintWriter(new OutputStreamWriter(outStream));
   }

   public void sendMessage(String message) throws IOException {
      output.print(message + "\n");
      //The ensuing flush method call is necessary for the data to
      // be written to the socket data stream before the
      // socket is closed.
      output.flush();
   } // end sendMessage

   public String receiveMessage() throws IOException {
      // read a line from the data stream
      String message = input.readLine();
      return message;
   } //end receiveMessage

   public void close() throws IOException {
      sslSocket.close();
   }
} //end class
