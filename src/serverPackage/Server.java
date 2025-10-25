package serverPackage;

import interPackage.Operation;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int PORT = 5000;
    private static AtomicInteger compteurGlobal = new AtomicInteger(0);

    public static void main(String[] args) {
        try (ServerSocket serveur = new ServerSocket(PORT)) {
            System.out.println(" Serveur de calculatrice multi-thread démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serveur.accept();
                System.out.println(" Nouveau client connecté : " + clientSocket.getInetAddress());
                new Thread(new ClientThread(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientThread implements Runnable {
        private Socket socket;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                out.flush(); 

                while (true) {
                    Operation op;
                    try {
                        op = (Operation) in.readObject();
                    } catch (EOFException e) {
                        break; 
                    }

                    double resultat = op.calculer();
                    int total = compteurGlobal.incrementAndGet();

                    System.out.println(" Opération traitée : " + op + " = " + resultat);
                    System.out.println(" Total des opérations traitées : " + total);

                    out.writeObject("Résultat : " + resultat + " (Opération n°" + total + ")");
                    out.flush();
                }

            } catch (Exception e) {
                System.out.println("⚠ Erreur avec un client : " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}
