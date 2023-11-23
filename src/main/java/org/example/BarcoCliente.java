package org.example;

import java.io.*;
import java.net.Socket;

public class BarcoCliente {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9876;
    private int id;
    private String direction;

    public BarcoCliente(int id, String direction) {
        this.id = id;
        this.direction = direction;
    }

    public static void main(String[] args) {
        int numBarcos = 8;

        for (int i = 0; i < numBarcos; i++) {
            String direction = (i % 2 == 1) ? "OESTE" : "ESTE";
            int id = i;
            new Thread(() -> new BarcoCliente(id, direction).realizarNavegacion()).start();
        }
    }

    public void realizarNavegacion() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            System.out.println("Barco:" + id + " " + direction + ": Conectado al servidor de esclusas.");

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Quiere cruzar" + id + ": " + direction);

            System.out.println("Barco:" + id + ": Navegación no permitida. Esperando un barco mas.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();

            if (line.equals("NAVEGACION_PERMITIDA"))
                System.out.println("Barco:" + id + ": Navegacion permitida.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
