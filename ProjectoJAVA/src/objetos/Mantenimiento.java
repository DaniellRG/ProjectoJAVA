/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objetos;

/**
 * Datos de un mantenimiento.
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
