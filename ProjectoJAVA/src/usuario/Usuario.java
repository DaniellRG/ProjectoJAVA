/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package usuario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import mantenimiento.Conexion;

/**
 *
 * @author danie
 */
public class Usuario {

    private int id;
    private String usuario;
    private String nombreCompleto;
    private int idPerfil;
    private String nombreRol;

    public Usuario(int id, String usuario, String nombreCompleto, int idPerfil, String nombreRol) {
        this.id = id;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.idPerfil = idPerfil;
        this.nombreRol = nombreRol;
    }

    public Usuario(int id, String usuario, String clave, int idPerfil) {
        this(id, usuario, usuario, idPerfil, idPerfil == 1 ? "Administrador" : "Técnico");
    }

    public static Usuario autenticar(String usuarioIngresado, String claveIngresada) {
        String sql = "{CALL sp_iniciar_sesion(?, ?)}";
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, usuarioIngresado);
            cs.setString(2, claveIngresada);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("usuario"),
                        rs.getString("nombre_completo"),
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario en la base de datos: " + e.getMessage());
        }
        return null;
    }

    public boolean esAdministrador() {
        return this.idPerfil == 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}
