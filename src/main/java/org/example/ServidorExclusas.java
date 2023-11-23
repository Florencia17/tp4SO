package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServidorExclusas {
    private static final int PORT = 9876;
    private static final Queue<Socket> oesteQueue = new ConcurrentLinkedQueue<>();
    private static final Queue<Socket> esteQueue = new ConcurrentLinkedQueue<>();
    private static final Object lock = new Object();

    public static void main(String[] args) {
        ServidorExclusas server = new ServidorExclusas();
        server.iniciarServidor();
    }

    public void iniciarServidor() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Exclusas iniciado en el puerto " + PORT + " - Esperando Barcos - ");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> gestionarEsclusas(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gestionarEsclusas(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = reader.readLine();
            String[] parts = request.split(":");
            String direction = parts[1].trim();

            synchronized (lock) {
                if (direction.equals("OESTE")) {
                    oesteQueue.add(clientSocket);
                    System.out.println("Nuevo barco en esclusa OESTE. Total en el Oeste: " + oesteQueue.size());
                } else {
                    esteQueue.add(clientSocket);
                    System.out.println("Nuevo barco en esclusa ESTE. Total en el Este:" + esteQueue.size());
                }

                if (oesteQueue.size() == 2) {
                    permitirPaso(oesteQueue);
                } else if (esteQueue.size() == 2) {
                    permitirPaso(esteQueue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void permitirPaso(Queue<Socket> queue) {
        Socket ship1 = queue.poll();
        Socket ship2 = queue.poll();

        try {
            PrintWriter writer1 = new PrintWriter(ship1.getOutputStream(), true);
            PrintWriter writer2 = new PrintWriter(ship2.getOutputStream(), true);

            writer1.println("NAVEGACION_PERMITIDA");
            writer2.println("NAVEGACION_PERMITIDA");

            System.out.println("-- Navegando --");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
