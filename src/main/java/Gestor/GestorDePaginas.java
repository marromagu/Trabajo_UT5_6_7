/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public static String getHTML(int p, List<String> nuevasFilasHTML, List<String> nuevasOpcionHTML) {
        StringBuilder contenidoHTML = new StringBuilder();

        // Obtener el directorio actual
        String directorioActual = System.getProperty("user.dir");

        // Ruta relativa del archivo GestorDePaginas.html
        String rutaArchivo;
        switch (p) {
            case 1 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\index.html";
            case 2 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\Tableros.html";

            case 0 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\PaginaError.html";
            default ->
                throw new AssertionError();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean insertarFilas = false;
            while ((linea = reader.readLine()) != null) {
                AutoCompletar(linea, insertarFilas, nuevasFilasHTML, contenidoHTML, nuevasOpcionHTML);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contenidoHTML.toString();
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

}
