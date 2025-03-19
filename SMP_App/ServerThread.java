import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * @author M. L. Liu
 */

class ServerThread implements Runnable {
    MyStreamSocket myDataSocket;
    private boolean loggedIn = false;

    ServerThread(MyStreamSocket myDataSocket) {
        this.myDataSocket = myDataSocket;
    }

    public void run() {
        boolean done = false;
        String request;
        try {
            while (!done) {
                request = myDataSocket.receiveMessage();
                System.out.println("SERVER Request received: " + request);
                done = handleRequest(request);
            }
        } catch (Exception ex) {
            System.out.println("SERVER Exception caught in thread: " + ex);
        } finally {
            try {
                myDataSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean handleRequest(String request) throws IOException {
        String[] requestElements = request.split(":", 2);
        String requestCode = requestElements[0];
        String[] requestParams = requestElements[1].split(",");

        switch (requestCode){
            case "100":
                if (loggedIn) {
                    myDataSocket.sendMessage("Already logged in");
                    break;
                }
                if (requestParams.length < 2) {
                    myDataSocket.sendMessage("Error: Login request missing parameters");
                    break;
                }
                String username = requestParams[0];
                String password = requestParams[1];
                verifyLogin(username, password);
                break;
            case "200":
                if (!loggedIn) {
                    myDataSocket.sendMessage("Error: Not logged in");
                }
                else {
                    loggedIn = false;
                    myDataSocket.sendMessage("201: Logout Successful");
                    return true;
                }
                break;
            case "300":
                if (!loggedIn) {
                    myDataSocket.sendMessage("Error: Not logged in");
                    break;
                }
                if (requestParams.length < 2) {
                    myDataSocket.sendMessage("Error: Upload request missing parameters");
                    break;
                }
                // Call upload method
                boolean uploadSuccess = uploadMessage(requestParams[0], requestParams[1]);
                if (uploadSuccess) {
                    myDataSocket.sendMessage("301: Upload Successful");
                } else {
                    myDataSocket.sendMessage("Error: Unable to upload message");
                }
                break;
            case "400":
                if (!loggedIn) {
                    myDataSocket.sendMessage("Error: Not logged in");
                    break;
                }
                String message = downloadMessage(Integer.parseInt(requestParams[0]));
                myDataSocket.sendMessage("401:" + message);
                break;
            case "500":
                if (!loggedIn) {
                    myDataSocket.sendMessage("Error: Not logged in");
                    break;
                }
                myDataSocket.sendMessage("501:" + downloadAllMessages());
                break;
            default:
                myDataSocket.sendMessage("Error: Unknown Request");
                break;
        }
        return false;
    }

    private void verifyLogin(String username, String password) throws IOException {
        if ("admin".equals(username) && "password".equals(password)) {
            loggedIn = true;
            myDataSocket.sendMessage("101: Login Successful. Welcome " + username + ".");
        } else {
            loggedIn = false;
            myDataSocket.sendMessage("102: Login Failed");
        }
    }

    private boolean uploadMessage(String username, String message) throws IOException {
        int id = getNextMessageId();
        // Append the message to the file "messages.txt" in the format "username-message"
        String currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("../messages.txt", true))) {
            bw.write(id + "-" + currentTime + "-" + username + "-" + message);
            bw.newLine();
            return true;
        } catch (IOException ex) {
            System.err.println("Error uploading message: " + ex.getMessage());
            return false;
        }
    }

    private String downloadAllMessages() {
        try (BufferedReader br = new BufferedReader(new FileReader("../messages.txt"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return String.join("|", lines);
        } catch (IOException e) {
            return "";
        }
    }

    private String downloadMessage(int messageId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("../messages.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("-");
                int currentId = Integer.parseInt(parts[0]);
                if (currentId == messageId) {
                    return line;
                }
            }
        } catch (IOException e) {
            return "Error reading messages: " + e.getMessage();
        }
        return "Message not found";
    }

    private int getNextMessageId() throws IOException {
        int highestId = 0;
        File file = new File("../messages.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("-", 2);
                int currentId = Integer.parseInt(parts[0]);
                highestId = Math.max(highestId, currentId);
            }
        }
        return highestId + 1;
    }
}