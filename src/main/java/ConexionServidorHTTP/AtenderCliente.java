/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConexionServidorHTTP;

import Datos.ConexionConBDD;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
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

            System.out.println(url);

            while ((linea = bufferedReader.readLine()) != null && !linea.isEmpty()) {
                System.out.println(linea);
                if (linea.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(linea.substring("Content-Length:".length()).trim());
                }
            }
            if (!url.equals("GET /favicon.ico HTTP/1.1")) {
                System.out.println(url);
                String[] partes = url.split(" ");
                String metodo = partes[0];
                if (null == metodo) {
                    contenidoHTML = Gestor.GestorDePaginas.getHTML(0);
                    enviarRespuestaHTML(contenidoHTML);
                    System.out.println("--> Ups.");
                }else{
                    atenderPorGet(url);
                }
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
        Datos.ConexionConBDD misDatos = new ConexionConBDD();
        HashMap<Integer, String> mapaPartidasTerminadas = new HashMap<>();
        switch (url) {
            case "/" -> {
                contenidoHTML = Gestor.GestorDePaginas.getHTML(1);
                enviarRespuestaHTML(contenidoHTML);
                mapaPartidasTerminadas = misDatos.obtenerPartidasTerminadas();
                System.out.println("_________________________________________");
                System.out.println(mapaPartidasTerminadas.toString());
            }
            default -> {
                contenidoHTML = Gestor.GestorDePaginas.getHTML(0);
                enviarRespuestaHTML(contenidoHTML);
            }
        }
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
            e.printStackTrace();
        }
    }

}
