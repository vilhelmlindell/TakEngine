package TakEngine;

import java.net.*;
import java.io.*;

public class TakClient {
  public static final String SERVER_NAME = "playtak.com";
  public static final int PORT = 10000;

  public static void main(String[] args) {
    try (Socket clientSocket = new Socket(SERVER_NAME, PORT)) {
      // Get input stream from the socket
      InputStream input = clientSocket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
      // Read data from the server
      String message;
      while ((message = reader.readLine()) != null) {
        // System.out.println("Server message: " + message);
        System.out.println(message);
        if (!message.isEmpty()) {
          output.println(parseCommand(message));
        }
      }
    } catch (UnknownHostException ex) {
      System.out.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
      System.out.println("I/O error: " + ex.getMessage());
    }
  }

  private static String parseCommand(String command) {
    if (command.equals("Login or Register")) {
      return "Login Guest";
    }
    return null;
  }
}
