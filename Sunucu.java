import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sunucu {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static BlockingQueue<ClientHandler> messageQueue = new LinkedBlockingQueue<>();
    private static int clientIdCounter = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Sunucu başlatıldı. Port: " + PORT);

            // İstemci dinleme döngüsü
            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Yeni istemci bağlandı.");

                        ClientHandler clientHandler = new ClientHandler(clientSocket, clientIdCounter++);
                        clients.add(clientHandler);
                        new Thread(clientHandler).start();

                        // İstemcilere kendi numarasını bildir
                        clientHandler.sendMessage(String.valueOf(clientHandler.getClientId()));

                        // İstemcilere sıradaki cihazın kim olduğunu bildir
                        messageQueue.put(clientHandler);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // İstemcilere sıradaki mesajları gönderme döngüsü
            new Thread(() -> {
                while (true) {
                    try {
                        // Bir sonraki cihazı bekleyin
                        ClientHandler sender = messageQueue.take();

                        // Tüm istemcilere sıradaki cihazın mesajını gönder
                        broadcastMessage(sender.getClientId() + ". cihazdan mesaj alındı", sender);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
}
