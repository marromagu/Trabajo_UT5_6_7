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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                        contenidoHTML = Gestor.GestorDePaginas.getHTML(0, null, null);
                        enviarRespuestaHTML(contenidoHTML);
                        System.out.println("-> Ups, ha ocurrido algo inesperado: ");
                    }
                }
            } else {
                contenidoHTML = Gestor.GestorDePaginas.getHTML(0, null, null);
                enviarRespuestaHTML(contenidoHTML);
                System.out.println("--> Ups.");
            }
        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void atenderPorGet(String url) {
        String contenidoHTML = null;
        //Extrae la subcadena entre 'GET' y 'HTTP/1.1'
        url = url.substring(3, url.lastIndexOf("HTTP"));
        url = url.trim(); // Elimina espacios en blanco antes y después

        switch (url) {
            case "/":
                crearHTML_TablaPartidas();
                break;
            default:
                contenidoHTML = Gestor.GestorDePaginas.getHTML(0, null, null);
                enviarRespuestaHTML(contenidoHTML);
                break;
        }
    }

    private void crearHTML_TablaPartidas() {

        String contenidoHTML;
        Datos.ConexionConBDD misDatos = new ConexionConBDD();
        HashMap<Integer, String> mapaPartidasTerminadas;

        mapaPartidasTerminadas = misDatos.obtenerPartidasTerminadas();

        // Lista para las nuevas filas HTML
        List<String> nuevasFilasHTML = new ArrayList<>();
        // Lista para las nuevas opciones HTML del formulario
        List<String> nuevasOpcionHTML = new ArrayList<>();

        // Iterar sobre las partidas terminadas y generar las filas HTML correspondientes
        for (Map.Entry<Integer, String> entrada : mapaPartidasTerminadas.entrySet()) {

            int idPartida = entrada.getKey();
            String[] detallesPartida = entrada.getValue().split(";");
            String nombreJugador1 = detallesPartida[1];
            String nombreJugador2 = detallesPartida[2];
            String nombreGanador = detallesPartida[3];
            String nombreUltimoTurno = detallesPartida[4];

            // Crear la fila HTML con los detalles de la partida
            String filaHTML = String.format("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                    idPartida, nombreJugador1, nombreJugador2, nombreGanador, nombreUltimoTurno);
            // Crear la opción HTML para el formulario de selección
            String opcionHTML = String.format("<option value=%d>Partida %d</option>",
                    idPartida, idPartida);

            // Agregar la fila HTML a la lista de nuevas filas
            nuevasFilasHTML.add(filaHTML);
            // Agregar la opción HTML a la lista de nuevas opciones
            nuevasOpcionHTML.add(opcionHTML);
        }

        contenidoHTML = Gestor.GestorDePaginas.getHTML(1, nuevasFilasHTML, nuevasOpcionHTML);
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
            e.printStackTrace();
        }
    }

    private void atenderPorPost(int contentLength) {
        try {
            Datos.ConexionConBDD misDatos = new ConexionConBDD();
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
            System.out.println(body);

            String[] partes = body.split("=");
            int id = Integer.parseInt(partes[1]);

            System.out.println(id);
            ArrayList<String> disparos = misDatos.obtenerDisparosDePartida(id);
            System.out.println(disparos);
            ArrayList<String> barcos = misDatos.consultarBarcosEnPartida(id);
            System.out.println(barcos);

            String contenidoHTML = Gestor.GestorDePaginas.getHTML(2, null, null);
            enviarRespuestaHTML(contenidoHTML);

        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
