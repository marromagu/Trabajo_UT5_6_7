/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConexionServidorHTTP;

import Datos.ConexionConBDD;
import Gestor.GestorDePaginas;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DAM_M
 */
public class AtenderCliente extends Thread {

    private final Socket skCliente;
    private InputStreamReader flujo_entrada;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private final Datos.ConexionConBDD misDatos = new ConexionConBDD();
    private final Gestor.GestorDePaginas miGestor = GestorDePaginas.getGestor();

    public AtenderCliente(Socket skCliente) {
        this.skCliente = skCliente;
    }

    @Override
    public void run() {
        try {
            flujo_entrada = new InputStreamReader(skCliente.getInputStream());
            bufferedReader = new BufferedReader(flujo_entrada);
            printWriter = new PrintWriter(skCliente.getOutputStream(), true);

            String contenidoHTML;
            String url = bufferedReader.readLine();
            String linea;
            int contentLength = 0;

            while ((linea = bufferedReader.readLine()) != null && !linea.isEmpty()) {
                if (linea.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(linea.substring("Content-Length:".length()).trim());
                }
            }

            System.out.println(url);
            String[] partes = url.split(" ");
            String metodo = partes[0];

            if (metodo != null) {
                switch (metodo) {
                    case "GET" -> {
                        atenderPorGet(url);
                    }
                    case "POST" -> {
                        atenderPorPost(contentLength);
                    }
                    default -> {
                        contenidoHTML = miGestor.getHTML_Error();
                        enviarRespuestaHTML(contenidoHTML);
                        System.out.println("-> Ups, ha ocurrido algo inesperado: ");
                    }
                }
            } else {
                contenidoHTML = miGestor.getHTML_Error();
                enviarRespuestaHTML(contenidoHTML);
                System.out.println("--> Ups.");
            }
        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void atenderPorGet(String url) {
        String contenidoHTML;
        //Extrae la subcadena entre 'GET' y 'HTTP/1.1'
        url = url.substring(3, url.lastIndexOf("HTTP"));
        url = url.trim(); // Elimina espacios en blanco antes y después
        String[] partes = url.split("\\?");// Separar la cadena de consulta
        partes = procesarUrlFormulario(partes);
        url = partes[0];
        System.out.println(url);
        switch (url) {
            case "/" -> {
                contenidoHTML = miGestor.getHTML_Index();
                enviarRespuestaHTML(contenidoHTML);
            }
            case "/GET" -> {
                contenidoHTML = miGestor.getHTML_GET();
                enviarRespuestaHTML(contenidoHTML);
                System.out.println("-----------------------------------------");
                //procesarPeticion(url);
                System.out.println(url);
            }
            default -> {
                contenidoHTML = miGestor.getHTML_Error();
                enviarRespuestaHTML(contenidoHTML);
            }
        }
    }

    private void atenderPorPost(int contentLength) {
        try {
            // Crear un StringBuilder para almacenar el cuerpo del mensaje POST
            StringBuilder bodyBuilder = new StringBuilder();

            // Leer el cuerpo del mensaje POST exactamente contentLength bytes
            char[] buffer = new char[contentLength];
            int bytesRead = bufferedReader.read(buffer, 0, contentLength);
            bodyBuilder.append(buffer, 0, bytesRead);

            // Convertir el cuerpo del mensaje a una cadena
            String body = bodyBuilder.toString();

            // Procesar el cuerpo del mensaje POST como desees, por ejemplo, puedes imprimirlo
            System.out.println("Cuerpo del mensaje POST:");
            procesarPeticion(body);

        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void procesarPeticion(String body) {
        String[] partes = body.split("=");
        int id = Integer.parseInt(partes[1]);
        System.out.println(body);
        String contenidoHTML = miGestor.getHTML_Tablero(id);
        enviarRespuestaHTML(contenidoHTML);
    }

    private void enviarRespuestaHTML(String contenidoHTML) {
        // Enviar una respuesta HTTP al cliente
        printWriter.println("HTTP/1.1 200 OK");
        printWriter.println("Content-Type: text/html");
        printWriter.println("Content-Length: " + contenidoHTML.length());
        printWriter.println();
        printWriter.println(contenidoHTML);

        // Cerrar la conexión
        try {
            skCliente.close();
        } catch (IOException e) {
            System.out.println("Error: enviarRespuestaHTML ");
        }
    }

    private String[] procesarUrlFormulario(String[] partes) {
        // Si no hay cadena de consulta, no es una URL de respuesta de formulario
        if (partes.length <= 1) {
            return partes;
        }
        String consulta = partes[1];
        String[] partesConsulta = consulta.split("=");
        int id = Integer.parseInt(partesConsulta[1]);
        String contenidoHTML = miGestor.getHTML_Tablero(id);
        enviarRespuestaHTML(contenidoHTML);
        return partes;
    }

}
