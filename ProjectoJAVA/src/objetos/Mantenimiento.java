/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objetos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import usuario.Conexion;

/**
 * Modelo de negocio y acceso a datos para Mantenimientos (conectado a MySQL).
 * @author danie
 */
public class Mantenimiento {
    
    private int idMantenimiento;
    private int idEquipo;
    private String equipo;
    private String marcaModelo;
    private int idTecnico;
    private String tecnico;
    private String usuarioTecnico;
    private String fecha;
    private int idTipo;
    private String tipoMantenimiento;
    private int idEstado;
    private String estado;
    private double costo;
    private String descripcion;

    
    public Mantenimiento(int idMantenimiento, int idEquipo, String equipo, String marcaModelo,
                         int idTecnico, String tecnico, String usuarioTecnico,
                         String fecha, int idTipo, String tipoMantenimiento,
                         int idEstado, String estado, double costo, String descripcion) {
        this.idMantenimiento = idMantenimiento;
        this.idEquipo = idEquipo;
        this.equipo = equipo;
        this.marcaModelo = marcaModelo;
        this.idTecnico = idTecnico;
        this.tecnico = tecnico;
        this.usuarioTecnico = usuarioTecnico;
        this.fecha = fecha;
        this.idTipo = idTipo;
        this.tipoMantenimiento = tipoMantenimiento;
        this.idEstado = idEstado;
        this.estado = estado;
        this.costo = costo;
        this.descripcion = descripcion;
    }

    
    public Mantenimiento(String equipo, String tecnico, String fecha, String tipoMantenimiento, String estado, double costo) {
        this(0, 0, equipo, "", 0, tecnico, "", fecha, 0, tipoMantenimiento, 0, estado, costo, "");
    }

    
    public static List<Mantenimiento> obtenerMantenimientos() {
        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT id_mantenimiento, id_equipo, equipo, marca_modelo, id_tecnico, tecnico, usuario_tecnico, fecha, id_tipo, tipo_mantenimiento, id_estado, estado, costo, descripcion FROM vw_mantenimientos_realizados";
        
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new Mantenimiento(
                    rs.getInt("id_mantenimiento"),
                    rs.getInt("id_equipo"),
                    rs.getString("equipo"),
                    rs.getString("marca_modelo"),
                    rs.getInt("id_tecnico"),
                    rs.getString("tecnico"),
                    rs.getString("usuario_tecnico"),
                    rs.getString("fecha"),
                    rs.getInt("id_tipo"),
                    rs.getString("tipo_mantenimiento"),
                    rs.getInt("id_estado"),
                    rs.getString("estado"),
                    rs.getDouble("costo"),
                    rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar mantenimientos: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    
    public static List<Mantenimiento> obtenerEquiposPendientes() {
        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT id_mantenimiento, equipo, marca_modelo, tecnico, fecha, tipo_mantenimiento, estado, costo FROM vw_equipos_pendientes";
        
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new Mantenimiento(
                    rs.getInt("id_mantenimiento"),
                    0,
                    rs.getString("equipo"),
                    rs.getString("marca_modelo"),
                    0,
                    rs.getString("tecnico"),
                    "",
                    rs.getString("fecha"),
                    0,
                    rs.getString("tipo_mantenimiento"),
                    0,
                    rs.getString("estado"),
                    rs.getDouble("costo"),
                    ""
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar equipos pendientes: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    
    public static List<Mantenimiento> obtenerMantenimientosPorTecnico(int idTecnico) {
        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "{CALL sp_mantenimientos_por_tecnico(?)}";
        
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idTecnico);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Mantenimiento(
                        rs.getInt("id_mantenimiento"),
                        0,
                        rs.getString("equipo"),
                        "",
                        idTecnico,
                        "",
                        "",
                        rs.getString("fecha"),
                        0,
                        rs.getString("tipo_mantenimiento"),
                        0,
                        rs.getString("estado"),
                        rs.getDouble("costo"),
                        rs.getString("descripcion")
                    ));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar mantenimientos por técnico: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    
    public static double obtenerCostoTotalGeneral() {
        String sql = "SELECT fn_costo_total_general() AS total";
        try (Connection con = Conexion.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener costo total: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
        return 0.0;
    }

    
    public static int obtenerOCrearEquipo(String codigoEquipo) {
        String sqlBuscar = "SELECT id_equipo FROM equipo WHERE codigo_equipo = ? LIMIT 1";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
            ps.setString(1, codigoEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_equipo");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar equipo: " + e.getMessage());
        }

        
        String sqlInsertar = "INSERT INTO equipo (codigo_equipo, marca_modelo, id_tipo_equipo) VALUES (?, ?, 1)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, codigoEquipo);
            ps.setString(2, "Equipo " + codigoEquipo);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar nuevo equipo: " + e.getMessage());
        }
        return 1;
    }

    
    public static boolean registrarMantenimiento(int idEquipo, int idTecnico, String fecha, int idTipo, int idEstado, double costo, String descripcion) {
        String sql = "{CALL sp_insertar_mantenimiento(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idEquipo);
            cs.setInt(2, idTecnico);
            cs.setString(3, fecha);
            cs.setInt(4, idTipo);
            cs.setInt(5, idEstado);
            cs.setDouble(6, costo);
            cs.setString(7, descripcion);
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar en MySQL: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    
    public static boolean actualizarMantenimiento(int idMantenimiento, int idEquipo, int idTecnico, String fecha, int idTipo, int idEstado, double costo, String descripcion) {
        String sql = "{CALL sp_actualizar_mantenimiento(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idMantenimiento);
            cs.setInt(2, idEquipo);
            cs.setInt(3, idTecnico);
            cs.setString(4, fecha);
            cs.setInt(5, idTipo);
            cs.setInt(6, idEstado);
            cs.setDouble(7, costo);
            cs.setString(8, descripcion);
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar mantenimiento: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    
    public static boolean eliminarMantenimiento(int idMantenimiento) {
        String sql = "{CALL sp_eliminar_mantenimiento(?)}";
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idMantenimiento);
            cs.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar mantenimiento: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

   
    public static List<ItemCombo> obtenerEquiposCombo() {
        List<ItemCombo> lista = new ArrayList<>();
        String sql = "{CALL sp_combo_equipos()}";
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(new ItemCombo(rs.getInt("id_equipo"), rs.getString("codigo_equipo") + " - " + rs.getString("marca_modelo")));
            }
        } catch (SQLException e) {
            System.err.println("Error combo equipos: " + e.getMessage());
        }
        return lista;
    }

    public static List<ItemCombo> obtenerTecnicosCombo() {
        List<ItemCombo> lista = new ArrayList<>();
        String sql = "{CALL sp_combo_tecnicos()}";
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(new ItemCombo(rs.getInt("id_usuario"), rs.getString("nombre_completo")));
            }
        } catch (SQLException e) {
            System.err.println("Error combo tecnicos: " + e.getMessage());
        }
        return lista;
    }

    public static List<ItemCombo> obtenerTiposCombo() {
        List<ItemCombo> lista = new ArrayList<>();
        String sql = "{CALL sp_combo_tipos()}";
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(new ItemCombo(rs.getInt("id_tipo_mantenimiento"), rs.getString("nombre_tipo")));
            }
        } catch (SQLException e) {
            System.err.println("Error combo tipos: " + e.getMessage());
        }
        return lista;
    }

    public static List<ItemCombo> obtenerEstadosCombo() {
        List<ItemCombo> lista = new ArrayList<>();
        String sql = "{CALL sp_combo_estados()}";
        try (Connection con = Conexion.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(new ItemCombo(rs.getInt("id_estado_mantenimiento"), rs.getString("nombre_estado")));
            }
        } catch (SQLException e) {
            System.err.println("Error combo estados: " + e.getMessage());
        }
        return lista;
    }

    public static List<String> obtenerTecnicos() {
        List<String> tecnicos = new ArrayList<>();
        for (ItemCombo item : obtenerTecnicosCombo()) {
            tecnicos.add(item.getTexto());
        }
        return tecnicos;
    }

   
    public int getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(int idMantenimiento) { this.idMantenimiento = idMantenimiento; }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) { this.equipo = equipo; }

    public String getMarcaModelo() { return marcaModelo; }
    public void setMarcaModelo(String marcaModelo) { this.marcaModelo = marcaModelo; }

    public int getIdTecnico() { return idTecnico; }
    public void setIdTecnico(int idTecnico) { this.idTecnico = idTecnico; }

    public String getTecnico() { return tecnico; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }

    public String getUsuarioTecnico() { return usuarioTecnico; }
    public void setUsuarioTecnico(String usuarioTecnico) { this.usuarioTecnico = usuarioTecnico; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public String getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(String tipoMantenimiento) { this.tipoMantenimiento = tipoMantenimiento; }

    public int getIdEstado() { return idEstado; }
    public void setIdEstado(int idEstado) { this.idEstado = idEstado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
