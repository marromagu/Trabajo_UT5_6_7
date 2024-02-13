/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
    private int numeroHTML;
    private String nombreHTML;
    private String html_Respuesta;

    public static GestorDePaginas getCliente() {
        if (miGestor == null) {
            miGestor = new GestorDePaginas();
        }
        return miGestor;
    }

    public GestorDePaginas() {
    }

    public static String getHTML(int p) {
        StringBuilder contenidoHTML = new StringBuilder();

        // Obtener el directorio actual
        String directorioActual = System.getProperty("user.dir");

        // Ruta relativa del archivo GestorDePaginas.html
        String rutaArchivo;
        switch (p) {
            case 1 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\index.html";
            
            case 0 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\PaginaError.html";
            default ->
                throw new AssertionError();
        }

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
}
