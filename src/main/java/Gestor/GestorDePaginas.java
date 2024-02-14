/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestor;

import Datos.ConexionConBDD;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DAM_M
 */
public class GestorDePaginas {

    private static GestorDePaginas miGestor = null;

    public static GestorDePaginas getMiGestor() {
        return miGestor;

    }

    public static void setMiGestor(GestorDePaginas aMiGestor) {
        miGestor = aMiGestor;
    }

    public static GestorDePaginas getCliente() {
        if (miGestor == null) {
            miGestor = new GestorDePaginas();
        }
        return miGestor;
    }

    public GestorDePaginas() {
    }

    public static String getHTML_Index(List<String> nuevasFilasHTML, List<String> nuevasOpcionHTML) {
        StringBuilder contenidoHTML = new StringBuilder();
        // Obtener el directorio actual
        String directorioActual = System.getProperty("user.dir");
        // Ruta relativa del archivo GestorDePaginas.html
        String rutaArchivo;
        rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\index.html";
        escribirIndex(rutaArchivo, nuevasFilasHTML, contenidoHTML, nuevasOpcionHTML);
        return contenidoHTML.toString();
    }

    public static String getHTML_Tablero(int id) {
        StringBuilder contenidoHTML = new StringBuilder();
        String directorioActual = System.getProperty("user.dir");
        String rutaArchivo;
        rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\Tableros.html";
        escribirTablero(rutaArchivo, contenidoHTML, id);
        return contenidoHTML.toString();
    }

    public static String getHTML_Error() {
        StringBuilder contenidoHTML = new StringBuilder();
        String directorioActual = System.getProperty("user.dir");
        String rutaArchivo;
        rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\PaginaError.html";
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenidoHTML.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenidoHTML.toString();
    }

    private static void escribirIndex(String rutaArchivo, List<String> nuevasFilasHTML, StringBuilder contenidoHTML, List<String> nuevasOpcionHTML) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean insertarFilas = false;
            while ((linea = reader.readLine()) != null) {
                AutoCompletar(linea, insertarFilas, nuevasFilasHTML, contenidoHTML, nuevasOpcionHTML);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void AutoCompletar(String linea, boolean insertarFilas, List<String> nuevasFilasHTML, StringBuilder contenidoHTML, List<String> nuevasOpcionHTML) {
        // Activar el marcador de inserción cuando encuentres el marcador correspondiente
        if (linea.contains("<!-- INSERTAR_NUEVAS_FILAS -->")) {
            insertarFilas = true;
        }
        // Insertar las nuevas filas HTML si el marcador de inserción está activo
        if (insertarFilas) {
            for (String nuevaFilaHTML : nuevasFilasHTML) {
                contenidoHTML.append(nuevaFilaHTML).append("\n");
            }
            insertarFilas = false; // Desactivar el marcador después de insertar las nuevas filas
        }
        // Activar el marcador de inserción cuando encuentres el marcador correspondiente para las opciones de las partidas
        if (linea.contains("<!-- OPTIONES_DE_PARTIDAS -->")) {
            insertarFilas = true;
        }
        // Insertar las nuevas opciones HTML si el marcador de inserción está activo
        if (insertarFilas) {
            for (String nuevaOpcionHTML : nuevasOpcionHTML) {
                contenidoHTML.append(nuevaOpcionHTML).append("\n");
            }
            insertarFilas = false; // Desactivar el marcador después de insertar las nuevas opciones
        }
        // Añadir la línea actual al contenido HTML
        contenidoHTML.append(linea).append("\n");
    }

    private static void escribirTablero(String rutaArchivo, StringBuilder contenidoHTML, int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            Datos.ConexionConBDD miCone = new ConexionConBDD();
            String jugador = miCone.obtenerPartidas(id);
            String[] jugadores = jugador.split(";");
            String jugador1 = jugadores[0];
            String jugador2 = jugadores[1];
            while ((linea = reader.readLine()) != null) {
                setTitulos(linea, contenidoHTML, id, miCone, jugadores);

                ArrayList<String> disparos = miCone.obtenerDisparosDePartida(id);
                ArrayList<String> barcos = miCone.consultarBarcosEnPartida(id);

                if (linea.contains("<!-- INSERTAR_Tablero1 -->")) {
                    disparos(disparos, jugador2, contenidoHTML);
                    pintarBarcos(barcos, jugador1, contenidoHTML);
                } else if (linea.contains("<!-- INSERTAR_Tablero2 -->")) {
                    disparos(disparos, jugador1, contenidoHTML);
                    pintarBarcos(barcos, jugador2, contenidoHTML);
                }

                // Añadir la línea actual al contenido HTML
                contenidoHTML.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void tableroVacio() {
        String[][] tablero = new String[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                tablero[i][j] = "<div class=\"casilla\"></div>";
            }
        }
    }

    private static void disparos(ArrayList<String> disparos, String jugador1, StringBuilder contenidoHTML) throws NumberFormatException {
        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                boolean disparado = false;
                for (String disparo : disparos) {
                    String[] partes = disparo.split("-");
                    String jugadorId = partes[0];
                    int posicionX = Integer.parseInt(partes[1]);
                    int posicionY = Integer.parseInt(partes[2]);

                    if (jugador1.equals(jugadorId) && posicionX == row && posicionY == col) {
                        disparado = true;
                        String mensajeResultado = partes[3];
                        if (mensajeResultado.equals("A")) {
                            contenidoHTML.append("<div class=\"casilla\" style=\"background-color: blue;\">A " + posicionX + "-" + posicionY + "</div>").append("\n");
                        } else if (mensajeResultado.equals("T")) {
                            contenidoHTML.append("<div class=\"casilla\" style=\"background-color: red;\">T " + posicionX + "-" + posicionY + "</div>").append("\n");
                        } else if (mensajeResultado.equals("H")) {
                            contenidoHTML.append("<div class=\"casilla\" style=\"background-color: black;\">H " + posicionX + "-" + posicionY + "</div>").append("\n");
                        }
                        break;
                    }
                }
                if (!disparado) {
                    contenidoHTML.append("<div class=\"casilla\"></div>").append("\n");
                }
            }
        }
    }

    private static void pintarBarcos(ArrayList<String> barcos, String jugador2, StringBuilder contenidoHTML) throws NumberFormatException {
        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                boolean casillaConBarco = false;
                for (String barco : barcos) {
                    String[] partes = barco.split("-");
                    String jugadorId = partes[0];
                    int tamaño = Integer.parseInt(partes[1]);
                    int posicionX = Integer.parseInt(partes[2]);
                    int posicionY = Integer.parseInt(partes[3]);
                    String orientacion = partes[4];

                    if (jugador2.equals(jugadorId)) {
                        // Verificar si la casilla actual está ocupada por el barco
                        if (orientacion.equals("H")) {
                            if (posicionX == row && col >= posicionY && col < posicionY + tamaño) {
                                casillaConBarco = true;
                                contenidoHTML.append("<div class=\"casilla\" style=\"background-color: green;\">B</div>").append("\n");
                                break;
                            }
                        } else if (orientacion.equals("V")) {
                            if (posicionY == col && row >= posicionX && row < posicionX + tamaño) {
                                casillaConBarco = true;
                                contenidoHTML.append("<div class=\"casilla\" style=\"background-color: green;\">B</div>").append("\n");
                                break;
                            }
                        }
                    }
                }
                // Si la casilla no contiene un barco, se marca como vacía
                if (!casillaConBarco) {
                    contenidoHTML.append("<div class=\"casilla\"></div>").append("\n");
                }
            }
        }
    }

    private static void setTitulos(String linea, StringBuilder contenidoHTML, int id, ConexionConBDD miCone, String[] jugadores) {
        if (linea.contains("<!-- H1 -->")) {
            contenidoHTML.append("<h1>Tablas " + id + "</h1>").append("\n");
        }
        miCone.obtenerDisparosDePartida(id);

        if (linea.contains("<!-- H2 -->")) {
            contenidoHTML.append("<h2>Jugador " + jugadores[0] + "</h2>").append("\n");
        }
        if (linea.contains("<!-- H2.2 -->")) {
            contenidoHTML.append("<h2>Jugador " + jugadores[1] + "</h2>").append("\n");
        }
    }

}
