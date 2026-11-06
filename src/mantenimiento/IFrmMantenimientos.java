/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package mantenimiento;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author danie
 */
public class IFrmMantenimientos extends javax.swing.JInternalFrame {
    
    private static final long serialVersionUID = 1L;
    
    private DefaultTableModel modeloTabla;
    private java.util.List<ItemCombo> listaTecnicosCombo;
    private java.util.List<Mantenimiento> listaMantenimientosActual;
    private int idMantenimientoSeleccionado = -1;
    private usuario.Usuario usuarioLogueado;

    
    public IFrmMantenimientos() {
        this(null);
    }

    public IFrmMantenimientos(usuario.Usuario usuario) {
        initComponents();
        this.usuarioLogueado = usuario;
        
        agruparRadioButtons();

     
        this.setClosable(true);
        this.setIconifiable(true);
        this.setMaximizable(true);
        this.setResizable(true);
        this.setTitle("Registro e Informe de Mantenimientos Realizados");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        configurarTabla();
        cargarTecnicos();
        cargarDatos();
        aplicarPermisos();
    }
    
    private void aplicarPermisos() {
        
        boolean esAdmin = usuarioLogueado != null && usuarioLogueado.esAdministrador();
        btnEditar.setVisible(esAdmin);
        btnActualizar.setVisible(esAdmin);
        btnEliminar.setVisible(esAdmin);
    }
    
    
    private void cargarTecnicos() {
        listaTecnicosCombo = Mantenimiento.obtenerTecnicosCombo();
        cmbTecnico.removeAllItems();
        for (ItemCombo item : listaTecnicosCombo) {
            cmbTecnico.addItem(item.getTexto());
        }
    }
    
    private void agruparRadioButtons() {
        rbtPreventivo.setSelected(true);
        rbtRealizado.setSelected(true);
    }
    
    private void cargarDatos() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        listaMantenimientosActual = Mantenimiento.obtenerMantenimientos();
        for (Mantenimiento m : listaMantenimientosActual) {
            modeloTabla.addRow(new Object[]{
                m.getIdMantenimiento(),
                m.getEquipo(),
                m.getTecnico(),
                m.getFecha(),
                m.getTipoMantenimiento(),
                m.getEstado(),
                "$" + String.format("%,.2f", m.getCosto())
            });
        }
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

            int idEquipo = Mantenimiento.obtenerOCrearEquipo(equipo);
            int idTecnico = listaTecnicosCombo.get(idxTecnico).getId();
            int idTipo = rbtPreventivo.isSelected() ? 1 : 2;
            int idEstado = rbtRealizado.isSelected() ? 1 : (rbtPendiente.isSelected() ? 2 : 3);

            boolean exito = Mantenimiento.registrarMantenimiento(idEquipo, idTecnico, fecha, idTipo, idEstado, costo, "Registro desde interfaz");
            if (exito) {
                cargarDatos();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Mantenimiento registrado correctamente en MySQL.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

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
    
    private void configurarTabla() {
        
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Equipo");
        modeloTabla.addColumn("Técnico");
        modeloTabla.addColumn("Fecha");
        modeloTabla.addColumn("Tipo");
        modeloTabla.addColumn("Estado");
        modeloTabla.addColumn("Costo");
        tblMantenimientos.setModel(modeloTabla);
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

            int idEquipo = Mantenimiento.obtenerOCrearEquipo(equipo);
            int idTecnico = listaTecnicosCombo.get(idxTecnico).getId();
            int idTipo = rbtPreventivo.isSelected() ? 1 : 2;
            int idEstado = rbtRealizado.isSelected() ? 1 : (rbtPendiente.isSelected() ? 2 : 3);

            boolean exito = Mantenimiento.actualizarMantenimiento(idMantenimientoSeleccionado, idEquipo, idTecnico, fecha, idTipo, idEstado, costo, "Actualizado desde interfaz");
            if (exito) {
                JOptionPane.showMessageDialog(this, "Mantenimiento actualizado exitosamente en MySQL.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                idMantenimientoSeleccionado = -1;
                cargarDatos();
                limpiarCampos();
            }
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato AAAA-MM-DD.", "Fecha Inválida", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El costo debe ser un valor numérico válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        if (usuarioLogueado != null && !usuarioLogueado.esAdministrador()) {
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
            boolean exito = Mantenimiento.eliminarMantenimiento(m.getIdMantenimiento());
            if (exito) {
                JOptionPane.showMessageDialog(this, "Mantenimiento eliminado exitosamente de MySQL.", "Registro Eliminado", JOptionPane.INFORMATION_MESSAGE);
                idMantenimientoSeleccionado = -1;
                cargarDatos();
                limpiarCampos();
            }
        }
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
