/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package mantenimiento;

import objetos.ItemCombo;
import objetos.Mantenimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import usuario.Conexion;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author danie
 */
public class IFrmMantenimientos extends javax.swing.JInternalFrame {
    
    private static final long serialVersionUID = 1L;
    
    private java.util.List<ItemCombo> listaTecnicosCombo;
    private java.util.List<Mantenimiento> listaMantenimientosActual;
    private int idMantenimientoSeleccionado = -1;
    private final boolean esAdministrador;

    public IFrmMantenimientos(boolean esAdministrador) {
        initComponents();
        tblMantenimientos.setDefaultEditor(Object.class, null);
        this.esAdministrador = esAdministrador;
        
        agruparRadioButtons();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cargarTecnicos();
        cargarDatos();
        aplicarPermisos();
    }
    
    private void aplicarPermisos() {
        btnEditar.setVisible(esAdministrador);
        btnActualizar.setVisible(esAdministrador);
        btnEliminar.setVisible(esAdministrador);
    }
    
    
    private void cargarTecnicos() {
        listaTecnicosCombo = new ArrayList<>();
        cmbTecnico.removeAllItems();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement pstm = con.prepareCall("call sp_combo_tecnicos()");
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ItemCombo item = new ItemCombo(rs.getInt("id_usuario"), rs.getString("nombre_completo"));
                listaTecnicosCombo.add(item);
                cmbTecnico.addItem(item.getTexto());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar técnicos: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agruparRadioButtons() {
        rbtPreventivo.setSelected(true);
        rbtRealizado.setSelected(true);
    }
    
    private void cargarDatos() {
        DefaultTableModel modeloTabla = new DefaultTableModel();
        listaMantenimientosActual = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement pstm = con.prepareCall("call sp_listar_mantenimientos()");
             ResultSet rs = pstm.executeQuery()) {
            ResultSetMetaData metadata = rs.getMetaData();
            modeloTabla.addColumn(metadata.getColumnLabel(1));
            modeloTabla.addColumn(metadata.getColumnLabel(3));
            modeloTabla.addColumn(metadata.getColumnLabel(6));
            modeloTabla.addColumn(metadata.getColumnLabel(8));
            modeloTabla.addColumn(metadata.getColumnLabel(10));
            modeloTabla.addColumn(metadata.getColumnLabel(12));
            modeloTabla.addColumn(metadata.getColumnLabel(13));

            while (rs.next()) {
                Mantenimiento mantenimiento = new Mantenimiento(
                    rs.getInt(1),
                    rs.getInt(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getInt(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),
                    rs.getInt(9),
                    rs.getString(10),
                    rs.getInt(11),
                    rs.getString(12),
                    rs.getDouble(13),
                    rs.getString(14)
                );
                listaMantenimientosActual.add(mantenimiento);
                modeloTabla.addRow(new Object[]{
                    mantenimiento.getIdMantenimiento(),
                    mantenimiento.getEquipo(),
                    mantenimiento.getTecnico(),
                    mantenimiento.getFecha(),
                    mantenimiento.getTipoMantenimiento(),
                    mantenimiento.getEstado(),
                    "$" + String.format("%,.2f", mantenimiento.getCosto())
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar mantenimientos: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
        tblMantenimientos.setModel(modeloTabla);
    }
    
    private void registrarMantenimiento() {
        try {
            String equipo = txtEquipo.getText().trim();
            String fecha = txtFecha.getText().trim();
            String costoTexto = txtCosto.getText().trim();
            int idxTecnico = cmbTecnico.getSelectedIndex();

            if (equipo.isEmpty() || fecha.isEmpty() || costoTexto.isEmpty() || idxTecnico < 0) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            java.time.LocalDate.parse(fecha);

            double costo = Double.parseDouble(costoTexto);

            if (costo <= 0) {
                JOptionPane.showMessageDialog(this, "El costo debe ser mayor que cero.", "Costo Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection con = Conexion.obtenerConexion()) {
                int idEquipo = obtenerOCrearEquipo(con, equipo);
                int idTecnico = listaTecnicosCombo.get(idxTecnico).getId();
                int idTipo = rbtPreventivo.isSelected() ? 1 : 2;
                int idEstado = rbtRealizado.isSelected() ? 1 : (rbtPendiente.isSelected() ? 2 : 3);

                try (PreparedStatement pstm = con.prepareCall("call sp_insertar_mantenimiento(?, ?, ?, ?, ?, ?, ?)")) {
                    pstm.setInt(1, idEquipo);
                    pstm.setInt(2, idTecnico);
                    pstm.setString(3, fecha);
                    pstm.setInt(4, idTipo);
                    pstm.setInt(5, idEstado);
                    pstm.setDouble(6, costo);
                    pstm.setString(7, "Registro desde interfaz");
                    pstm.execute();
                }

                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Mantenimiento registrado correctamente en MySQL.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar mantenimiento: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato AAAA-MM-DD (ejemplo: 2026-10-16).", "Fecha Inválida", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El costo debe ser un valor numérico válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarCampos() {
        txtEquipo.setText("");
        txtFecha.setText("");
        txtCosto.setText("");
        if (cmbTecnico.getItemCount() > 0) {
            cmbTecnico.setSelectedIndex(0);
        }
        rbtPreventivo.setSelected(true);
        rbtRealizado.setSelected(true);
        idMantenimientoSeleccionado = -1;
        txtEquipo.requestFocus();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btgTipo = new javax.swing.ButtonGroup();
        btgEstado = new javax.swing.ButtonGroup();
        txtEquipo = new javax.swing.JTextField();
        txtFecha = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMantenimientos = new javax.swing.JTable();
        rbtPreventivo = new javax.swing.JRadioButton();
        rbtCorrectivo = new javax.swing.JRadioButton();
        rbtRealizado = new javax.swing.JRadioButton();
        rbtPendiente = new javax.swing.JRadioButton();
        rbtProximo = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbTecnico = new javax.swing.JComboBox<>();
        txtCosto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnEditar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();

        btnGuardar.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        btnGuardar.setText("Registrar Mantenimiento");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        tblMantenimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblMantenimientos);

        btgTipo.add(rbtPreventivo);
        rbtPreventivo.setText("Preventivo");

        btgTipo.add(rbtCorrectivo);
        rbtCorrectivo.setText("Correctivo");

        btgEstado.add(rbtRealizado);
        rbtRealizado.setText("Realizado");

        btgEstado.add(rbtPendiente);
        rbtPendiente.setText("Pendiente");

        btgEstado.add(rbtProximo);
        rbtProximo.setText("Próximo");

        jLabel1.setText("Equipo");

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Mantenimientos registrados");

        jLabel3.setText("Técnico que atendió");

        jLabel4.setText("Fecha (AAAA-MM-DD)");

        cmbTecnico.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Costo");

        btnEditar.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        btnEditar.setText("Editar");
        btnEditar.addActionListener(this::btnEditarActionPerformed);

        btnActualizar.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        btnEliminar.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addComponent(txtEquipo)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCosto)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(57, 57, 57)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtProximo)
                            .addComponent(rbtCorrectivo)
                            .addComponent(rbtPreventivo)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cmbTecnico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnGuardar)
                                .addGap(15, 15, 15)
                                .addComponent(btnEditar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnActualizar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEliminar))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(332, 332, 332)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(rbtRealizado)
                                    .addComponent(rbtPendiente)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(65, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel2)
                        .addGap(68, 68, 68)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEquipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rbtPreventivo)
                                    .addComponent(rbtRealizado))
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rbtCorrectivo)
                                    .addComponent(rbtPendiente))
                                .addGap(26, 26, 26)
                                .addComponent(rbtProximo)))
                        .addGap(73, 73, 73)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnEditar)
                                .addComponent(btnActualizar)
                                .addComponent(btnEliminar))
                            .addComponent(btnGuardar))))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        registrarMantenimiento();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        int fila = tblMantenimientos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una fila de la tabla para editar.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Mantenimiento m = listaMantenimientosActual.get(fila);
        idMantenimientoSeleccionado = m.getIdMantenimiento();

        txtEquipo.setText(m.getEquipo());
        txtFecha.setText(m.getFecha());
        txtCosto.setText(String.valueOf(m.getCosto()));

        // Seleccionar el técnico en el combo
        for (int i = 0; i < listaTecnicosCombo.size(); i++) {
            if (listaTecnicosCombo.get(i).getId() == m.getIdTecnico() || listaTecnicosCombo.get(i).getTexto().equalsIgnoreCase(m.getTecnico())) {
                cmbTecnico.setSelectedIndex(i);
                break;
            }
        }

        // Tipo
        if ("Preventivo".equalsIgnoreCase(m.getTipoMantenimiento())) {
            rbtPreventivo.setSelected(true);
        } else {
            rbtCorrectivo.setSelected(true);
        }

        // Estado
        if ("Realizado".equalsIgnoreCase(m.getEstado())) {
            rbtRealizado.setSelected(true);
        } else if ("Pendiente".equalsIgnoreCase(m.getEstado())) {
            rbtPendiente.setSelected(true);
        } else {
            rbtProximo.setSelected(true);
        }

        JOptionPane.showMessageDialog(this, "Datos cargados en el formulario. Modifique los campos y presione 'Actualizar'.", "Modo Edición", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        if (idMantenimientoSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Primero seleccione un registro de la tabla y presione 'Editar'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String equipo = txtEquipo.getText().trim();
            String fecha = txtFecha.getText().trim();
            String costoTexto = txtCosto.getText().trim();
            int idxTecnico = cmbTecnico.getSelectedIndex();

            if (equipo.isEmpty() || fecha.isEmpty() || costoTexto.isEmpty() || idxTecnico < 0) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.time.LocalDate.parse(fecha);
            double costo = Double.parseDouble(costoTexto);

            if (costo <= 0) {
                JOptionPane.showMessageDialog(this, "El costo debe ser mayor que cero.", "Costo Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection con = Conexion.obtenerConexion()) {
                int idEquipo = obtenerOCrearEquipo(con, equipo);
                int idTecnico = listaTecnicosCombo.get(idxTecnico).getId();
                int idTipo = rbtPreventivo.isSelected() ? 1 : 2;
                int idEstado = rbtRealizado.isSelected() ? 1 : (rbtPendiente.isSelected() ? 2 : 3);

                try (PreparedStatement pstm = con.prepareCall("call sp_actualizar_mantenimiento(?, ?, ?, ?, ?, ?, ?, ?)")) {
                    pstm.setInt(1, idMantenimientoSeleccionado);
                    pstm.setInt(2, idEquipo);
                    pstm.setInt(3, idTecnico);
                    pstm.setString(4, fecha);
                    pstm.setInt(5, idTipo);
                    pstm.setInt(6, idEstado);
                    pstm.setDouble(7, costo);
                    pstm.setString(8, "Actualizado desde interfaz");
                    pstm.execute();
                }

                JOptionPane.showMessageDialog(this, "Mantenimiento actualizado exitosamente en MySQL.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                idMantenimientoSeleccionado = -1;
                cargarDatos();
                limpiarCampos();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar mantenimiento: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato AAAA-MM-DD.", "Fecha Inválida", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El costo debe ser un valor numérico válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        if (!esAdministrador) {
            JOptionPane.showMessageDialog(this, "Solo el Administrador tiene permisos para eliminar registros.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fila = tblMantenimientos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una fila de la tabla para eliminar.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Mantenimiento m = listaMantenimientosActual.get(fila);
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar el mantenimiento del equipo '" + m.getEquipo() + "'?\nEsta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection con = Conexion.obtenerConexion();
                 PreparedStatement pstm = con.prepareCall("call sp_eliminar_mantenimiento(?)")) {
                pstm.setInt(1, m.getIdMantenimiento());
                pstm.execute();
                JOptionPane.showMessageDialog(this, "Mantenimiento eliminado exitosamente de MySQL.", "Registro Eliminado", JOptionPane.INFORMATION_MESSAGE);
                idMantenimientoSeleccionado = -1;
                cargarDatos();
                limpiarCampos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar mantenimiento: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int obtenerOCrearEquipo(Connection con, String codigoEquipo) throws SQLException {
        try (PreparedStatement pstm = con.prepareCall("call sp_obtener_o_crear_equipo(?)")) {
            pstm.setString(1, codigoEquipo);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("No se pudo obtener el ID del equipo registrado.");
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btgEstado;
    private javax.swing.ButtonGroup btgTipo;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbTecnico;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbtCorrectivo;
    private javax.swing.JRadioButton rbtPendiente;
    private javax.swing.JRadioButton rbtPreventivo;
    private javax.swing.JRadioButton rbtProximo;
    private javax.swing.JRadioButton rbtRealizado;
    private javax.swing.JTable tblMantenimientos;
    private javax.swing.JTextField txtCosto;
    private javax.swing.JTextField txtEquipo;
    private javax.swing.JTextField txtFecha;
    // End of variables declaration//GEN-END:variables
}
