package Datos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ConexionConBDD implements Serializable {

    private final String NameDataBase = "BDD_HundirLaFlota";
    private final String User = "root";
    private final String Password = "root";
    private final String Driver = "com.mysql.cj.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost:3306/" + NameDataBase;

    public ConexionConBDD() {
    }

    public Connection getConexion() {
        Connection conexion = null;
        try {
            Class.forName(Driver);
            conexion = DriverManager.getConnection(URL, User, Password);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Error en ConexionConBDD: Controlador JDBC no encontrado");
            cnfe.printStackTrace(); // Imprimir detalles de la excepción
        } catch (SQLException sqle) {
            System.out.println("Error en ConexionConBDD: al conectar a la BDD");
            sqle.printStackTrace(); // Imprimir detalles de la excepción
        }

        return conexion;
    }

    public void cerrarConexion(Connection conexion) {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Error en ConexionConBDD: Se cerró la conexión a la BDD.");
            }
        } catch (SQLException sqle) {
            System.out.println("Error en ConexionConBDD: al cerrar la conexión a la BDD");
        }
    }

    public HashMap<Integer, String> obtenerPartidasTerminadas() {
        HashMap<Integer, String> mapaPartidasTerminadas = new HashMap<>();

        try (Connection conexion = getConexion()) {
            String sql = "SELECT id_partida, jugador_1, jugador_2, ganador, ultimo_turno FROM Partidas "
                    + "WHERE estado = 'X'";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idPartida = resultSet.getInt("id_partida");
                        int jugador1ID = resultSet.getInt("jugador_1");
                        int jugador2ID = resultSet.getInt("jugador_2");
                        int ganadorID = resultSet.getInt("ganador");
                        int ultimoTurnoID = resultSet.getInt("ultimo_turno");

                        // Crear cadena representativa de la partida con nombres de jugadores
                        String representacionPartida = String.format("%d;%s;%s;%s;%s",
                                idPartida, jugador1ID, jugador2ID, ganadorID, ultimoTurnoID);

                        // Agregar la representación al HashMap
                        mapaPartidasTerminadas.put(idPartida, representacionPartida);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al obtener las partidas terminadas: " + e.getMessage());
        }

        return mapaPartidasTerminadas;
    }

}
