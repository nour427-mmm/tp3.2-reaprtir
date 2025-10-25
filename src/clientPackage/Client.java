package clientPackage;

import interPackage.Operation;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print(" Entrez l’adresse IP du serveur : ");
            String ipServeur = scanner.nextLine();

            try (Socket socket = new Socket(ipServeur, 5000);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.flush(); 
                System.out.println(" Connecté au serveur !");

                while (true) {
                    System.out.print("Entrez le premier nombre (ou 'q' pour quitter) : ");
                    String input = scanner.next();
                    if (input.equalsIgnoreCase("q")) break;

                    double a = Double.parseDouble(input);
                    System.out.print("Entrez l’opérateur (+, -, *, /) : ");
                    char op = scanner.next().charAt(0);
                    System.out.print("Entrez le deuxième nombre : ");
                    double b = scanner.nextDouble();

                    Operation operation = new Operation(a, b, op);
                    out.writeObject(operation);
                    out.flush();

                    String resultat = (String) in.readObject();
                    System.out.println(" " + resultat);
                }

            } catch (Exception e) {
                System.out.println(" Erreur de connexion ou d’exécution : " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
