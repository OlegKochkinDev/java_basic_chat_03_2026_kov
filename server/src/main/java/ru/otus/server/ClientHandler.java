package ru.otus.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String username;
    private boolean isAuthenticate;
    private UserRole role;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connected port:" + socket.getPort());
        username = "user" + socket.getPort();
        sendMsg("Вы подключились под ником: " + username);

        new Thread(() -> {
            try {
                // цикл аутентификации
                while (!isAuthenticate) {
                    sendMsg("Перед работой с чатом необходимо выполнить аутентификацию \n" +
                            ConsoleColors.GREEN_BOLD + "/auth login password" + ConsoleColors.RESET +
                            " или зарегистрироваться \n" +
                            ConsoleColors.GREEN_BOLD + "/reg login password username" + ConsoleColors.RESET);

                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.equals("/exit")) {
                            sendMsg("/exitok");
                            break;
                        }
                        // /auth login password
                        if (message.startsWith("/auth ")) {
                            String[] token = message.trim().split(" ");
                            if (token.length != 3) {
                                sendMsg(ConsoleColors.RED + "Неверный формат команды /auth " + ConsoleColors.RESET);
                                continue;
                            }
                            if (server.getAuthenticatedProvider()
                                    .authenticate(this, token[1], token[2])) {
                                isAuthenticate = true;
                                break;
                            }
                            continue;
                        }
                        // /reg login password username
                        if (message.startsWith("/reg")) {
                            String[] token = message.trim().split(" ");
                            if (token.length != 4) {
                                sendMsg(ConsoleColors.RED + "Неверный формат команды /reg " + ConsoleColors.RESET);
                                continue;
                            }
                            if (server.getAuthenticatedProvider()
                                    .register(this, token[1], token[2], token[3])) {
                                isAuthenticate = true;
                                break;
                            }
                        }
                    }
                }
                while (isAuthenticate) {
                    String message = in.readUTF();
                    //  /служебные сообщения
                    if (message.startsWith("/")) {
                        if (message.equals("/exit")) {
                            sendMsg("/exitok");
                            break;
                        } else if (message.startsWith("/w")) {
                            System.out.println("личное сообщение: " + message);
                            Message privateMessage = server.getClientHandlerFromMessage(message);
                            privateMessage.setSenderClient(this);
                            if (privateMessage.getRecieverClient() == null) {
                                sendMsg("Клиент не найден!");
                            } else if (getUsername().equals(privateMessage.getRecieverClient().getUsername())) {
                                sendMsg("Нельзя отправлять сообщения самому себе");
                            } else {
                                server.privateMessage(privateMessage);
                            }
                        } else if (message.startsWith("/kick")) {
                            kickUser(message.split(" ")[1]);
                        }
//                        String[] token = "12 erter 234 werw we".split(" ", 3);
                    } else {
                        server.broadcastMessage(username, message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMsg(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    private void disconnect() {
        server.unsubscribe(this);
        System.out.println("Client disconnected port:" + socket.getPort());
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kickUser(String username) {
        ClientHandler kickedUser = server.getClientHandlerByUsername(username);
        if (role == UserRole.ADMIN) {
            if (kickedUser == null) {
                sendMsg(ConsoleColors.RED_BOLD + " Пользователь " + username + " не найден!" + ConsoleColors.RESET);
            } else {
                if (kickedUser.getRole() == UserRole.ADMIN) {
                    sendMsg(ConsoleColors.RED_BOLD + "Невозможно отключать Администратора." + ConsoleColors.RESET);
                }

                kickedUser.sendMsg(ConsoleColors.RED_BOLD + "Администратор отключил Вас от чата." + ConsoleColors.RESET);
                kickedUser.sendMsg("/exitok");
                kickedUser.disconnect();
            }

        } else {
            sendMsg(ConsoleColors.RED_BOLD + " нет прав на удаление пользователей из чата." + ConsoleColors.RESET);
            return;
        }

    }
}
