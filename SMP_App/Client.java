import java.io.*;

/**
 * This module contains the presentaton logic of an Echo Client.
 * @author M. L. Liu
 */
public class Client {
   public static void main(String[] args) {
      InputStreamReader is = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(is);

      try {
         System.out.println("Welcome to the Echo client.\n" +
                 "What is the name of the server host?");
         String hostName = br.readLine();

         if (hostName.length() == 0) {
            // if user did not enter a name, use the default host name
            hostName = "localhost";
         }

         System.out.println("What is the port number of the server host?");
         String portNum = br.readLine();

         if (portNum.length() == 0) {
            // default port number
            portNum = "7";
         }

         ClientHelper helper = new ClientHelper(hostName, portNum);

         System.out.println("Enter username:");
         String username = br.readLine().trim();
         System.out.println("Enter password:");
         String password = br.readLine().trim();

         // Send message in correct protocol format
         String loginRequest = "100:" + username + "," + password;
         String response = helper.getEcho(loginRequest);

         // Display server response
         System.out.println("Server response: " + response);

         // Close the connection
         helper.done();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}
