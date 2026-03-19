package ru.otus.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        clients = new CopyOnWriteArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. port: " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                subscribe(new ClientHandler(this, socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        broadcastMessage("Подключился пользователь " + clientHandler.getUsername());
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        broadcastMessage("Пользователь " + clientHandler.getUsername() + " покинул чат");
        clients.remove(clientHandler);
    }

    public void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    public void privateMessage(Message message) {
        ClientHandler receiverClient = message.getRecieverClient();
        ClientHandler senderClient = message.getSenderClient();
        String messageText = message.getMessage();

        receiverClient.sendMsg(senderClient.getUsername()+"->"+receiverClient.getUsername()+": "+ messageText);
        senderClient.sendMsg(senderClient.getUsername()+"->"+receiverClient.getUsername()+": "  + messageText);
    }

    public Message getClientHandlerFromMessage(String message) {
        ClientHandler clientHandler = null;
        String[] splitMessage = message.split(" ");
        String messageText = "";
        Message msg = new Message();

        //Ищем клиента по юзернейму
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(splitMessage[1])) {
                clientHandler = c;
            }
        }
        msg.setRecieverClient(clientHandler);

        //Собираем сообщение
        for  (int i = 2; i <= splitMessage.length-1; i++) {
            messageText +=  splitMessage[i]+" ";
        }
        msg.setMessage(messageText.trim());
        return msg;
    }
}
