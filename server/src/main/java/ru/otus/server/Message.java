package ru.otus.server;

public class Message {

    private String message;
    private ClientHandler recieverClient;
    private ClientHandler senderClient;

    public Message(ClientHandler senderClient, ClientHandler receiverClient, String message) {
        this.recieverClient = receiverClient;
        this.senderClient = senderClient;
        this.message = message;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public ClientHandler getRecieverClient() {
        return recieverClient;
    }

    public ClientHandler getSenderClient() {
        return senderClient;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRecieverClient(ClientHandler recieverClient) {
        this.recieverClient = recieverClient;
    }

    public void setSenderClient(ClientHandler senderClient) {
        this.senderClient = senderClient;
    }


}
