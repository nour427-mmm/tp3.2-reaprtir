package serverPackage;

import interPackage.Operation;
import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 5000;

    
    private static int compteurClients = 0;
    private static int compteurOperations = 0;

    
    private static synchronized int incrementerCompteurClients() {
        compteurClients++;
        return compteurClients;
    }

    private static synchronized int incrementerCompteurOperations() {
        compteurOperations++;
        return compteurOperations;
    }

    public static void main(String[] args) {
        try (ServerSocket serveur = new ServerSocket(PORT)) {
            System.out.println(" Serveur de calculatrice multi-thread démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serveur.accept();
                int idClient = incrementerCompteurClients();
                System.out.println(" Nouveau client connecté (Client #" + idClient + ")");
                new Thread(new ClientThread(clientSocket, idClient)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientThread implements Runnable {
        private Socket socket;
        private int clientId;

        public ClientThread(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
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
                        System.out.println(" Client #" + clientId + " s'est déconnecté.");
                        break;
                    }

                    double resultat = op.calculer();
                    int totalOps = incrementerCompteurOperations();

                    
                    out.writeObject("Résultat : " + resultat + " (Opération n°" + totalOps + ")");
                    out.flush();
                }

            } catch (Exception e) {
                System.out.println(" Erreur avec Client #" + clientId + " : " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}
