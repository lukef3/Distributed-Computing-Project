import java.io.*;

/**
 * This module contains the presentaton logic of an Echo Client.
 * @author M. L. Liu
 */
public class Client {
   public static void main(String[] args) {
      InputStreamReader is = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(is);
      String username = "";

      try {
         System.out.println("Welcome to the Echo client.\n" +
                 "What is the name of the server host?");
         String hostName = br.readLine();

         if (hostName.isEmpty()) {
            // if user did not enter a name, use the default host name
            hostName = "localhost";
         }

         System.out.println("What is the port number of the server host?");
         String portNum = br.readLine();

         if (portNum.isEmpty()) {
            // default port number
            portNum = "12345";
         }

         ClientHelper helper = new ClientHelper(hostName, portNum);
         boolean running = true;

         while (running) {
            System.out.println("\nSelect an action:");
            System.out.println("1. Log-on");
            System.out.println("2. Upload");
            System.out.println("3. Download Specific");
            System.out.println("4. Download All");
            System.out.println("5. Log-off");
            System.out.print("Enter choice (1-5): ");

            String choice = br.readLine().trim();
            String request = "";

            switch (choice) {
               case "1":
                  // Log-on request: 100:username,password
                  System.out.println("Enter username:");
                  String usernameLogin = br.readLine().trim();
                  System.out.println("Enter password:");
                  String password = br.readLine().trim();
                  username = usernameLogin;
                  request = "100:" + usernameLogin + "," + password;
                  break;
               case "2":
                  // Upload request: 300:username,message
                  System.out.println("Enter message to upload:");
                  String message = br.readLine().trim();
                  request = "300:" + username + "," + message;
                  break;
               case "3":
                  System.out.println("Enter id of message to download:");
                  String id = br.readLine().trim();
                  request = "400:" + id;
                  break;
               case "4":
                  // Download All request: 500:username
                  request = "500:";
                  break;
               case "5":
                  // Log-off request: 200:
                  request = "200:";
                  break;
               default:
                  System.out.println("Please select a number between 1 and 5.");
                  continue; // Keep going if the choice is invalid
            }

            // Send the message following the protocol and display the server response
            //String response = helper.getEcho(request);
            //System.out.println("Server response: " + response);
         }

         // Close the connection when finished
         helper.done();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}

