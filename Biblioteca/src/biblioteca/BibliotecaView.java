/*
 * BibliotecaView.java
 */
package biblioteca;

import dao.TrabajoXML;
import dominio.Categoria;
import dominio.DifinicionAutor;
import dominio.Glosario;
import dominio.Termino;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import util.TerminoUtil;

/**
 * The application's main frame.
 */
public class BibliotecaView extends FrameView {

    private List<DifinicionAutor> lisDifi = null;

    public BibliotecaView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        
        activarPanel(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        
        
        

        actualizatGlosarioIncluir();
        actualizatCategorias();
        actualizatAutores();
        actualizatArchivos();
    }
    
    private void activarPanel(Boolean panel1, Boolean panel2, Boolean panel3) {
        jPanel1.setVisible(panel1);
        jPanel2.setVisible(panel2);
        jPanel3.setVisible(panel3);
    }
   
    private void actualizarTableTerminos(String glosario){
        TrabajoXML xml = new TrabajoXML();
        Glosario nuvo = new Glosario();
        nuvo.setNombre(glosario);
        List<TerminoUtil> listTer = null;
        try {
            listTer = xml.obtenerTerminosUnGlosario(nuvo);
            
            DefaultTableModel datosTabla = new DefaultTableModel();
        
            //selecciono el modelo de datos de la tabla para modificarlo
            datosTabla = (DefaultTableModel) jTable1.getModel();
            datosTabla.setRowCount(listTer.size());
            
            
            //Paso la información a la tabla
            for(int i = 0; i < listTer.size(); i++){
                datosTabla.setValueAt(listTer.get(i).getTermino().getNombre(),i,0);
                datosTabla.setValueAt(listTer.get(i).getTermino().getPalabrasClaves().toString(),i,1);
                datosTabla.setValueAt(listTer.get(i).getTermino().getCategoria().toString(),i,2);
                datosTabla.setValueAt(listTer.get(i).getNumeroDefiniciones().toString(),i,3);
            }
        } catch (Exception ex) {
            Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }
    private void actualizatGlosarioConsultar(JComboBox jcomboParaConsultar) {
        TrabajoXML xml = new TrabajoXML();
        jcomboParaConsultar.removeAllItems();
        List<String> lis = new ArrayList<String>();
        try {
            lis = xml.obtenerGlosarios();
        } catch (Exception ex) {
            Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < lis.size(); i++) {
            jcomboParaConsultar.addItem(lis.get(i));
        }
        //jComboBox6.setSelectedIndex(0);
    }

    private void actualizatGlosarioIncluir() {
        TrabajoXML xml = new TrabajoXML();
        jComboBox2.removeAllItems();
        List<String> lis = new ArrayList<String>();
        try {
            lis = xml.obtenerGlosarios();
        } catch (Exception ex) {
            Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < lis.size(); i++) {
            jComboBox2.addItem(lis.get(i));
        }
        jComboBox2.setSelectedIndex(-1);
    }
    
    private void actualizatAutores() {
        TrabajoXML xml = new TrabajoXML();
        jComboBox3.removeAllItems();
        List<String> lis = new ArrayList<String>();
        try {
            lis = xml.obtenerAutores();
        } catch (Exception ex) {
            Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < lis.size(); i++) {
            jComboBox3.addItem(lis.get(i));
        }
        jComboBox3.setSelectedIndex(-1);
    }
    private void actualizatArchivos() {
        TrabajoXML xml = new TrabajoXML();
        jComboBox4.removeAllItems();
        List<String> lis = new ArrayList<String>();
        try {
            lis = xml.obtenerArchivos();
        } catch (Exception ex) {
            Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < lis.size(); i++) {
            jComboBox4.addItem(lis.get(i));
        }
        jComboBox4.setSelectedIndex(-1);
        jTextField7.setText("");
        jTextField9.setText("");
    }
    private void actualizatCategorias() {
        TrabajoXML xml = new TrabajoXML();
        jComboBox1.removeAllItems();
        List<String> lis = new ArrayList<String>();
        try {
            lis = xml.obtenerCategorias();
        } catch (Exception ex) {
            Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < lis.size(); i++) {
            jComboBox1.addItem(lis.get(i));
        }
        jComboBox1.setSelectedIndex(-1);
    }
    private void actualizatTerminos() {
        TrabajoXML xml = new TrabajoXML();
        jComboBox5.removeAllItems();
        List<String> lis = new ArrayList<String>();
        try {
            lis = xml.obtenerTerminos();
        } catch (Exception ex) {
            Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < lis.size(); i++) {
            jComboBox5.addItem(lis.get(i));
        }
        jComboBox5.setSelectedIndex(-1);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = BibliotecaApp.getApplication().getMainFrame();
            aboutBox = new BibliotecaAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        BibliotecaApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jButton61 = new javax.swing.JButton();
        jButton62 = new javax.swing.JButton();
        jButton63 = new javax.swing.JButton();
        jButton64 = new javax.swing.JButton();
        jButton65 = new javax.swing.JButton();
        jButton66 = new javax.swing.JButton();
        jButton67 = new javax.swing.JButton();
        jButton68 = new javax.swing.JButton();
        jButton69 = new javax.swing.JButton();
        jButton70 = new javax.swing.JButton();
        jButton71 = new javax.swing.JButton();
        jButton72 = new javax.swing.JButton();
        jButton73 = new javax.swing.JButton();
        jButton74 = new javax.swing.JButton();
        jButton75 = new javax.swing.JButton();
        jButton76 = new javax.swing.JButton();
        jButton77 = new javax.swing.JButton();
        jButton78 = new javax.swing.JButton();
        jButton79 = new javax.swing.JButton();
        jButton80 = new javax.swing.JButton();
        jButton81 = new javax.swing.JButton();
        jButton82 = new javax.swing.JButton();
        jButton83 = new javax.swing.JButton();
        jButton84 = new javax.swing.JButton();
        jButton85 = new javax.swing.JButton();
        jButton86 = new javax.swing.JButton();
        jButton87 = new javax.swing.JButton();
        jButton88 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jComboBox7 = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jButton54 = new javax.swing.JButton();
        jButton55 = new javax.swing.JButton();
        jButton56 = new javax.swing.JButton();
        jButton57 = new javax.swing.JButton();
        jButton58 = new javax.swing.JButton();
        jButton59 = new javax.swing.JButton();
        jButton60 = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jComboBox6 = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jTextField5 = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox();
        jTextField7 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setMaximumSize(new java.awt.Dimension(2147483647, 50000));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(null);

        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(null);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(biblioteca.BibliotecaApp.class).getContext().getResourceMap(BibliotecaView.class);
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(440, 370, 170, 23);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setName("jTextArea2"); // NOI18N
        jScrollPane2.setViewportView(jTextArea2);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(90, 100, 520, 220);

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);
        jButton3.setBounds(90, 370, 160, 23);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jPanel1.add(jLabel11);
        jLabel11.setBounds(20, 100, 54, 14);

        jTextField8.setText(resourceMap.getString("jTextField8.text")); // NOI18N
        jTextField8.setName("jTextField8"); // NOI18N
        jPanel1.add(jTextField8);
        jTextField8.setBounds(90, 50, 520, 20);

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel1.add(jLabel10);
        jLabel10.setBounds(40, 50, 38, 14);

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel1.add(jLabel9);
        jLabel9.setBounds(10, 10, 100, 17);

        mainPanel.add(jPanel1);
        jPanel1.setBounds(10, 20, 650, 410);

        jPanel3.setName("jPanel3"); // NOI18N

        jTabbedPane1.setMaximumSize(new java.awt.Dimension(2147483647, 32767));
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jTabbedPane3.setName("jTabbedPane3"); // NOI18N

        jPanel6.setForeground(resourceMap.getColor("jPanel6.foreground")); // NOI18N
        jPanel6.setMaximumSize(new java.awt.Dimension(2147483647, 32767));
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(null);

        jButton61.setFont(resourceMap.getFont("jButton61.font")); // NOI18N
        jButton61.setForeground(resourceMap.getColor("jButton61.foreground")); // NOI18N
        jButton61.setText(resourceMap.getString("jButton61.text")); // NOI18N
        jButton61.setName("jButton61"); // NOI18N
        jPanel6.add(jButton61);
        jButton61.setBounds(280, 20, 40, 40);

        jButton62.setFont(resourceMap.getFont("jButton62.font")); // NOI18N
        jButton62.setForeground(resourceMap.getColor("jButton62.foreground")); // NOI18N
        jButton62.setText(resourceMap.getString("jButton62.text")); // NOI18N
        jButton62.setName("jButton62"); // NOI18N
        jPanel6.add(jButton62);
        jButton62.setBounds(320, 20, 40, 40);

        jButton63.setFont(resourceMap.getFont("jButton63.font")); // NOI18N
        jButton63.setForeground(resourceMap.getColor("jButton63.foreground")); // NOI18N
        jButton63.setText(resourceMap.getString("jButton63.text")); // NOI18N
        jButton63.setName("jButton63"); // NOI18N
        jPanel6.add(jButton63);
        jButton63.setBounds(360, 20, 41, 40);

        jButton64.setFont(resourceMap.getFont("jButton64.font")); // NOI18N
        jButton64.setForeground(resourceMap.getColor("jButton64.foreground")); // NOI18N
        jButton64.setText(resourceMap.getString("jButton64.text")); // NOI18N
        jButton64.setName("jButton64"); // NOI18N
        jPanel6.add(jButton64);
        jButton64.setBounds(400, 20, 42, 40);

        jButton65.setFont(resourceMap.getFont("jButton65.font")); // NOI18N
        jButton65.setForeground(resourceMap.getColor("jButton65.foreground")); // NOI18N
        jButton65.setText(resourceMap.getString("jButton65.text")); // NOI18N
        jButton65.setName("jButton65"); // NOI18N
        jPanel6.add(jButton65);
        jButton65.setBounds(440, 20, 40, 40);

        jButton66.setFont(resourceMap.getFont("jButton66.font")); // NOI18N
        jButton66.setForeground(resourceMap.getColor("jButton66.foreground")); // NOI18N
        jButton66.setText(resourceMap.getString("jButton66.text")); // NOI18N
        jButton66.setName("jButton66"); // NOI18N
        jPanel6.add(jButton66);
        jButton66.setBounds(480, 20, 40, 40);

        jButton67.setFont(resourceMap.getFont("jButton67.font")); // NOI18N
        jButton67.setForeground(resourceMap.getColor("jButton67.foreground")); // NOI18N
        jButton67.setText(resourceMap.getString("jButton67.text")); // NOI18N
        jButton67.setName("jButton67"); // NOI18N
        jPanel6.add(jButton67);
        jButton67.setBounds(520, 20, 41, 40);

        jButton68.setFont(resourceMap.getFont("jButton68.font")); // NOI18N
        jButton68.setForeground(resourceMap.getColor("jButton68.foreground")); // NOI18N
        jButton68.setText(resourceMap.getString("jButton68.text")); // NOI18N
        jButton68.setName("jButton68"); // NOI18N
        jPanel6.add(jButton68);
        jButton68.setBounds(560, 20, 41, 40);

        jButton69.setFont(resourceMap.getFont("jButton69.font")); // NOI18N
        jButton69.setForeground(resourceMap.getColor("jButton69.foreground")); // NOI18N
        jButton69.setText(resourceMap.getString("jButton69.text")); // NOI18N
        jButton69.setName("jButton69"); // NOI18N
        jPanel6.add(jButton69);
        jButton69.setBounds(600, 20, 40, 40);

        jButton70.setFont(resourceMap.getFont("jButton70.font")); // NOI18N
        jButton70.setForeground(resourceMap.getColor("jButton70.foreground")); // NOI18N
        jButton70.setText(resourceMap.getString("jButton70.text")); // NOI18N
        jButton70.setName("jButton70"); // NOI18N
        jPanel6.add(jButton70);
        jButton70.setBounds(640, 20, 40, 40);

        jButton71.setFont(resourceMap.getFont("jButton71.font")); // NOI18N
        jButton71.setForeground(resourceMap.getColor("jButton71.foreground")); // NOI18N
        jButton71.setText(resourceMap.getString("jButton71.text")); // NOI18N
        jButton71.setName("jButton71"); // NOI18N
        jPanel6.add(jButton71);
        jButton71.setBounds(680, 20, 40, 40);

        jButton72.setFont(resourceMap.getFont("jButton72.font")); // NOI18N
        jButton72.setForeground(resourceMap.getColor("jButton72.foreground")); // NOI18N
        jButton72.setText(resourceMap.getString("jButton72.text")); // NOI18N
        jButton72.setName("jButton72"); // NOI18N
        jPanel6.add(jButton72);
        jButton72.setBounds(720, 20, 40, 40);

        jButton73.setFont(resourceMap.getFont("jButton73.font")); // NOI18N
        jButton73.setForeground(resourceMap.getColor("jButton73.foreground")); // NOI18N
        jButton73.setText(resourceMap.getString("jButton73.text")); // NOI18N
        jButton73.setName("jButton73"); // NOI18N
        jPanel6.add(jButton73);
        jButton73.setBounds(760, 20, 42, 40);

        jButton74.setFont(resourceMap.getFont("jButton74.font")); // NOI18N
        jButton74.setForeground(resourceMap.getColor("jButton74.foreground")); // NOI18N
        jButton74.setText(resourceMap.getString("jButton74.text")); // NOI18N
        jButton74.setName("jButton74"); // NOI18N
        jPanel6.add(jButton74);
        jButton74.setBounds(800, 20, 41, 40);

        jButton75.setFont(resourceMap.getFont("jButton75.font")); // NOI18N
        jButton75.setForeground(resourceMap.getColor("jButton75.foreground")); // NOI18N
        jButton75.setText(resourceMap.getString("jButton75.text")); // NOI18N
        jButton75.setName("jButton75"); // NOI18N
        jPanel6.add(jButton75);
        jButton75.setBounds(840, 20, 41, 40);

        jButton76.setFont(resourceMap.getFont("jButton76.font")); // NOI18N
        jButton76.setForeground(resourceMap.getColor("jButton76.foreground")); // NOI18N
        jButton76.setText(resourceMap.getString("jButton76.text")); // NOI18N
        jButton76.setName("jButton76"); // NOI18N
        jPanel6.add(jButton76);
        jButton76.setBounds(280, 60, 42, 40);

        jButton77.setFont(resourceMap.getFont("jButton77.font")); // NOI18N
        jButton77.setForeground(resourceMap.getColor("jButton77.foreground")); // NOI18N
        jButton77.setText(resourceMap.getString("jButton77.text")); // NOI18N
        jButton77.setName("jButton77"); // NOI18N
        jPanel6.add(jButton77);
        jButton77.setBounds(320, 60, 40, 40);

        jButton78.setFont(resourceMap.getFont("jButton78.font")); // NOI18N
        jButton78.setForeground(resourceMap.getColor("jButton78.foreground")); // NOI18N
        jButton78.setText(resourceMap.getString("jButton78.text")); // NOI18N
        jButton78.setName("jButton78"); // NOI18N
        jPanel6.add(jButton78);
        jButton78.setBounds(360, 60, 42, 40);

        jButton79.setFont(resourceMap.getFont("jButton79.font")); // NOI18N
        jButton79.setForeground(resourceMap.getColor("jButton79.foreground")); // NOI18N
        jButton79.setText(resourceMap.getString("jButton79.text")); // NOI18N
        jButton79.setName("jButton79"); // NOI18N
        jPanel6.add(jButton79);
        jButton79.setBounds(400, 60, 41, 40);

        jButton80.setFont(resourceMap.getFont("jButton80.font")); // NOI18N
        jButton80.setForeground(resourceMap.getColor("jButton80.foreground")); // NOI18N
        jButton80.setText(resourceMap.getString("jButton80.text")); // NOI18N
        jButton80.setName("jButton80"); // NOI18N
        jPanel6.add(jButton80);
        jButton80.setBounds(440, 60, 40, 40);

        jButton81.setFont(resourceMap.getFont("jButton81.font")); // NOI18N
        jButton81.setForeground(resourceMap.getColor("jButton81.foreground")); // NOI18N
        jButton81.setText(resourceMap.getString("jButton81.text")); // NOI18N
        jButton81.setName("jButton81"); // NOI18N
        jPanel6.add(jButton81);
        jButton81.setBounds(480, 60, 40, 40);

        jButton82.setFont(resourceMap.getFont("jButton82.font")); // NOI18N
        jButton82.setForeground(resourceMap.getColor("jButton82.foreground")); // NOI18N
        jButton82.setText(resourceMap.getString("jButton82.text")); // NOI18N
        jButton82.setName("jButton82"); // NOI18N
        jPanel6.add(jButton82);
        jButton82.setBounds(520, 60, 41, 40);

        jButton83.setFont(resourceMap.getFont("jButton83.font")); // NOI18N
        jButton83.setForeground(resourceMap.getColor("jButton83.foreground")); // NOI18N
        jButton83.setText(resourceMap.getString("jButton83.text")); // NOI18N
        jButton83.setName("jButton83"); // NOI18N
        jPanel6.add(jButton83);
        jButton83.setBounds(560, 60, 40, 40);

        jButton84.setFont(resourceMap.getFont("jButton84.font")); // NOI18N
        jButton84.setForeground(resourceMap.getColor("jButton84.foreground")); // NOI18N
        jButton84.setText(resourceMap.getString("jButton84.text")); // NOI18N
        jButton84.setName("jButton84"); // NOI18N
        jPanel6.add(jButton84);
        jButton84.setBounds(600, 60, 46, 40);

        jButton85.setFont(resourceMap.getFont("jButton85.font")); // NOI18N
        jButton85.setForeground(resourceMap.getColor("jButton85.foreground")); // NOI18N
        jButton85.setText(resourceMap.getString("jButton85.text")); // NOI18N
        jButton85.setName("jButton85"); // NOI18N
        jPanel6.add(jButton85);
        jButton85.setBounds(650, 60, 40, 40);

        jButton86.setFont(resourceMap.getFont("jButton86.font")); // NOI18N
        jButton86.setForeground(resourceMap.getColor("jButton86.foreground")); // NOI18N
        jButton86.setText(resourceMap.getString("jButton86.text")); // NOI18N
        jButton86.setName("jButton86"); // NOI18N
        jPanel6.add(jButton86);
        jButton86.setBounds(680, 60, 40, 40);

        jButton87.setFont(resourceMap.getFont("jButton87.font")); // NOI18N
        jButton87.setForeground(resourceMap.getColor("jButton87.foreground")); // NOI18N
        jButton87.setText(resourceMap.getString("jButton87.text")); // NOI18N
        jButton87.setName("jButton87"); // NOI18N
        jPanel6.add(jButton87);
        jButton87.setBounds(720, 60, 40, 40);

        jButton88.setFont(resourceMap.getFont("jButton88.font")); // NOI18N
        jButton88.setForeground(resourceMap.getColor("jButton88.foreground")); // NOI18N
        jButton88.setText(resourceMap.getString("jButton88.text")); // NOI18N
        jButton88.setName("jButton88"); // NOI18N
        jPanel6.add(jButton88);
        jButton88.setBounds(760, 60, 120, 40);

        jScrollPane4.setMaximumSize(new java.awt.Dimension(2147483647, 32767));
        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Término", "Palabras Claves", "Categoría", "Definiciones"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setName("jTable2"); // NOI18N
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable2);

        jPanel6.add(jScrollPane4);
        jScrollPane4.setBounds(30, 110, 1130, 370);

        jComboBox7.setName("jComboBox7"); // NOI18N
        jComboBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox7ActionPerformed(evt);
            }
        });
        jPanel6.add(jComboBox7);
        jComboBox7.setBounds(40, 80, 200, 20);

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        jPanel6.add(jLabel14);
        jLabel14.setBounds(40, 60, 70, 14);

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        jPanel6.add(jLabel16);
        jLabel16.setBounds(40, 10, 70, 14);

        jComboBox8.setName("jComboBox8"); // NOI18N
        jComboBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox8ActionPerformed(evt);
            }
        });
        jPanel6.add(jComboBox8);
        jComboBox8.setBounds(40, 30, 200, 20);

        jTabbedPane3.addTab("Navege por el glosario usando este índice", jPanel6);

        jTabbedPane1.addTab(resourceMap.getString("jTabbedPane3.TabConstraints.tabTitle"), jTabbedPane3); // NOI18N

        jTabbedPane4.setName("jTabbedPane4"); // NOI18N
        jTabbedPane1.addTab(resourceMap.getString("jTabbedPane4.TabConstraints.tabTitle"), jTabbedPane4); // NOI18N

        jTabbedPane5.setName("jTabbedPane5"); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N

        jButton33.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton33.setForeground(resourceMap.getColor("jButton33.foreground")); // NOI18N
        jButton33.setText(resourceMap.getString("jButton33.text")); // NOI18N
        jButton33.setName("jButton33"); // NOI18N

        jButton34.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton34.setForeground(resourceMap.getColor("jButton34.foreground")); // NOI18N
        jButton34.setText(resourceMap.getString("jButton34.text")); // NOI18N
        jButton34.setName("jButton34"); // NOI18N

        jButton35.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton35.setForeground(resourceMap.getColor("jButton35.foreground")); // NOI18N
        jButton35.setText(resourceMap.getString("jButton35.text")); // NOI18N
        jButton35.setName("jButton35"); // NOI18N

        jButton36.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton36.setForeground(resourceMap.getColor("jButton36.foreground")); // NOI18N
        jButton36.setText(resourceMap.getString("jButton36.text")); // NOI18N
        jButton36.setName("jButton36"); // NOI18N

        jButton37.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton37.setForeground(resourceMap.getColor("jButton37.foreground")); // NOI18N
        jButton37.setText(resourceMap.getString("jButton37.text")); // NOI18N
        jButton37.setName("jButton37"); // NOI18N

        jButton38.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton38.setForeground(resourceMap.getColor("jButton38.foreground")); // NOI18N
        jButton38.setText(resourceMap.getString("jButton38.text")); // NOI18N
        jButton38.setName("jButton38"); // NOI18N

        jButton39.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton39.setForeground(resourceMap.getColor("jButton39.foreground")); // NOI18N
        jButton39.setText(resourceMap.getString("jButton39.text")); // NOI18N
        jButton39.setName("jButton39"); // NOI18N

        jButton40.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton40.setForeground(resourceMap.getColor("jButton40.foreground")); // NOI18N
        jButton40.setText(resourceMap.getString("jButton40.text")); // NOI18N
        jButton40.setName("jButton40"); // NOI18N

        jButton41.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton41.setForeground(resourceMap.getColor("jButton41.foreground")); // NOI18N
        jButton41.setText(resourceMap.getString("jButton41.text")); // NOI18N
        jButton41.setName("jButton41"); // NOI18N

        jButton42.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton42.setForeground(resourceMap.getColor("jButton42.foreground")); // NOI18N
        jButton42.setText(resourceMap.getString("jButton42.text")); // NOI18N
        jButton42.setName("jButton42"); // NOI18N

        jButton43.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton43.setForeground(resourceMap.getColor("jButton43.foreground")); // NOI18N
        jButton43.setText(resourceMap.getString("jButton43.text")); // NOI18N
        jButton43.setName("jButton43"); // NOI18N

        jButton44.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton44.setForeground(resourceMap.getColor("jButton44.foreground")); // NOI18N
        jButton44.setText(resourceMap.getString("jButton44.text")); // NOI18N
        jButton44.setName("jButton44"); // NOI18N

        jButton45.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton45.setForeground(resourceMap.getColor("jButton45.foreground")); // NOI18N
        jButton45.setText(resourceMap.getString("jButton45.text")); // NOI18N
        jButton45.setName("jButton45"); // NOI18N

        jButton46.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton46.setForeground(resourceMap.getColor("jButton46.foreground")); // NOI18N
        jButton46.setText(resourceMap.getString("jButton46.text")); // NOI18N
        jButton46.setName("jButton46"); // NOI18N

        jButton47.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton47.setForeground(resourceMap.getColor("jButton47.foreground")); // NOI18N
        jButton47.setText(resourceMap.getString("jButton47.text")); // NOI18N
        jButton47.setName("jButton47"); // NOI18N

        jButton48.setFont(resourceMap.getFont("jButton48.font")); // NOI18N
        jButton48.setForeground(resourceMap.getColor("jButton48.foreground")); // NOI18N
        jButton48.setText(resourceMap.getString("jButton48.text")); // NOI18N
        jButton48.setName("jButton48"); // NOI18N

        jButton49.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton49.setForeground(resourceMap.getColor("jButton49.foreground")); // NOI18N
        jButton49.setText(resourceMap.getString("jButton49.text")); // NOI18N
        jButton49.setName("jButton49"); // NOI18N

        jButton50.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton50.setForeground(resourceMap.getColor("jButton50.foreground")); // NOI18N
        jButton50.setText(resourceMap.getString("jButton50.text")); // NOI18N
        jButton50.setName("jButton50"); // NOI18N

        jButton51.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton51.setForeground(resourceMap.getColor("jButton51.foreground")); // NOI18N
        jButton51.setText(resourceMap.getString("jButton51.text")); // NOI18N
        jButton51.setName("jButton51"); // NOI18N

        jButton52.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton52.setForeground(resourceMap.getColor("jButton52.foreground")); // NOI18N
        jButton52.setText(resourceMap.getString("jButton52.text")); // NOI18N
        jButton52.setName("jButton52"); // NOI18N

        jButton53.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton53.setForeground(resourceMap.getColor("jButton53.foreground")); // NOI18N
        jButton53.setText(resourceMap.getString("jButton53.text")); // NOI18N
        jButton53.setName("jButton53"); // NOI18N

        jButton54.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton54.setForeground(resourceMap.getColor("jButton54.foreground")); // NOI18N
        jButton54.setText(resourceMap.getString("jButton54.text")); // NOI18N
        jButton54.setName("jButton54"); // NOI18N

        jButton55.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton55.setForeground(resourceMap.getColor("jButton55.foreground")); // NOI18N
        jButton55.setText(resourceMap.getString("jButton55.text")); // NOI18N
        jButton55.setName("jButton55"); // NOI18N

        jButton56.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton56.setForeground(resourceMap.getColor("jButton56.foreground")); // NOI18N
        jButton56.setText(resourceMap.getString("jButton56.text")); // NOI18N
        jButton56.setName("jButton56"); // NOI18N

        jButton57.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton57.setForeground(resourceMap.getColor("jButton57.foreground")); // NOI18N
        jButton57.setText(resourceMap.getString("jButton57.text")); // NOI18N
        jButton57.setName("jButton57"); // NOI18N

        jButton58.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton58.setForeground(resourceMap.getColor("jButton58.foreground")); // NOI18N
        jButton58.setText(resourceMap.getString("jButton58.text")); // NOI18N
        jButton58.setName("jButton58"); // NOI18N

        jButton59.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton59.setForeground(resourceMap.getColor("jButton59.foreground")); // NOI18N
        jButton59.setText(resourceMap.getString("jButton59.text")); // NOI18N
        jButton59.setName("jButton59"); // NOI18N

        jButton60.setFont(new java.awt.Font("Tahoma", 0, 14));
        jButton60.setForeground(resourceMap.getColor("jButton60.foreground")); // NOI18N
        jButton60.setText(resourceMap.getString("jButton60.text")); // NOI18N
        jButton60.setName("jButton60"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(466, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton35))
                        .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton39)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jButton40)))
                        .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(81, 81, 81)
                                .addComponent(jButton47))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jButton46))
                            .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(jButton58, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jButton59, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton60, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(161, 161, 161)
                                .addComponent(jButton56, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(200, 200, 200)
                                .addComponent(jButton55, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(121, 121, 121)
                                .addComponent(jButton57)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jButton53, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton54))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(123, 123, 123)
                                .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(162, 162, 162)
                                .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton52, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(84, 84, 84)
                                .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(122, 122, 122))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton58, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton59, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton60, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton56, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton55, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton57, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton53, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton54, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton52, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(396, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        jTabbedPane1.addTab(resourceMap.getString("jTabbedPane5.TabConstraints.tabTitle"), jTabbedPane5); // NOI18N

        jTabbedPane2.setMaximumSize(new java.awt.Dimension(2147483647, 32767));
        jTabbedPane2.setName("jTabbedPane2"); // NOI18N

        jPanel4.setForeground(resourceMap.getColor("jPanel4.foreground")); // NOI18N
        jPanel4.setMaximumSize(new java.awt.Dimension(2147483647, 32767));
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(null);

        jButton5.setFont(resourceMap.getFont("jButton5.font")); // NOI18N
        jButton5.setForeground(resourceMap.getColor("jButton5.foreground")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jPanel4.add(jButton5);
        jButton5.setBounds(280, 20, 40, 40);

        jButton6.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton6.setForeground(resourceMap.getColor("jButton6.foreground")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jPanel4.add(jButton6);
        jButton6.setBounds(320, 20, 40, 40);

        jButton7.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton7.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jPanel4.add(jButton7);
        jButton7.setBounds(360, 20, 41, 40);

        jButton8.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton8.setForeground(resourceMap.getColor("jButton8.foreground")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        jPanel4.add(jButton8);
        jButton8.setBounds(400, 20, 42, 40);

        jButton9.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton9.setForeground(resourceMap.getColor("jButton9.foreground")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N
        jPanel4.add(jButton9);
        jButton9.setBounds(440, 20, 40, 40);

        jButton10.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton10.setForeground(resourceMap.getColor("jButton10.foreground")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setName("jButton10"); // NOI18N
        jPanel4.add(jButton10);
        jButton10.setBounds(480, 20, 40, 40);

        jButton11.setFont(resourceMap.getFont("jButton11.font")); // NOI18N
        jButton11.setForeground(resourceMap.getColor("jButton11.foreground")); // NOI18N
        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setName("jButton11"); // NOI18N
        jPanel4.add(jButton11);
        jButton11.setBounds(520, 20, 41, 40);

        jButton12.setFont(resourceMap.getFont("jButton12.font")); // NOI18N
        jButton12.setForeground(resourceMap.getColor("jButton12.foreground")); // NOI18N
        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setName("jButton12"); // NOI18N
        jPanel4.add(jButton12);
        jButton12.setBounds(560, 20, 41, 40);

        jButton13.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton13.setForeground(resourceMap.getColor("jButton13.foreground")); // NOI18N
        jButton13.setText(resourceMap.getString("jButton13.text")); // NOI18N
        jButton13.setName("jButton13"); // NOI18N
        jPanel4.add(jButton13);
        jButton13.setBounds(600, 20, 40, 40);

        jButton14.setFont(resourceMap.getFont("jButton14.font")); // NOI18N
        jButton14.setForeground(resourceMap.getColor("jButton14.foreground")); // NOI18N
        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton14.setName("jButton14"); // NOI18N
        jPanel4.add(jButton14);
        jButton14.setBounds(640, 20, 40, 40);

        jButton15.setFont(resourceMap.getFont("jButton15.font")); // NOI18N
        jButton15.setForeground(resourceMap.getColor("jButton15.foreground")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setName("jButton15"); // NOI18N
        jPanel4.add(jButton15);
        jButton15.setBounds(680, 20, 40, 40);

        jButton16.setFont(resourceMap.getFont("jButton16.font")); // NOI18N
        jButton16.setForeground(resourceMap.getColor("jButton16.foreground")); // NOI18N
        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setName("jButton16"); // NOI18N
        jPanel4.add(jButton16);
        jButton16.setBounds(720, 20, 40, 40);

        jButton17.setFont(resourceMap.getFont("jButton17.font")); // NOI18N
        jButton17.setForeground(resourceMap.getColor("jButton17.foreground")); // NOI18N
        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setName("jButton17"); // NOI18N
        jPanel4.add(jButton17);
        jButton17.setBounds(760, 20, 42, 40);

        jButton18.setFont(resourceMap.getFont("jButton18.font")); // NOI18N
        jButton18.setForeground(resourceMap.getColor("jButton18.foreground")); // NOI18N
        jButton18.setText(resourceMap.getString("jButton18.text")); // NOI18N
        jButton18.setName("jButton18"); // NOI18N
        jPanel4.add(jButton18);
        jButton18.setBounds(800, 20, 41, 40);

        jButton19.setFont(resourceMap.getFont("jButton19.font")); // NOI18N
        jButton19.setForeground(resourceMap.getColor("jButton19.foreground")); // NOI18N
        jButton19.setText(resourceMap.getString("jButton19.text")); // NOI18N
        jButton19.setName("jButton19"); // NOI18N
        jPanel4.add(jButton19);
        jButton19.setBounds(840, 20, 41, 40);

        jButton20.setFont(resourceMap.getFont("jButton20.font")); // NOI18N
        jButton20.setForeground(resourceMap.getColor("jButton20.foreground")); // NOI18N
        jButton20.setText(resourceMap.getString("jButton20.text")); // NOI18N
        jButton20.setName("jButton20"); // NOI18N
        jPanel4.add(jButton20);
        jButton20.setBounds(280, 60, 42, 40);

        jButton21.setFont(resourceMap.getFont("jButton21.font")); // NOI18N
        jButton21.setForeground(resourceMap.getColor("jButton21.foreground")); // NOI18N
        jButton21.setText(resourceMap.getString("jButton21.text")); // NOI18N
        jButton21.setName("jButton21"); // NOI18N
        jPanel4.add(jButton21);
        jButton21.setBounds(320, 60, 40, 40);

        jButton22.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton22.setForeground(resourceMap.getColor("jButton22.foreground")); // NOI18N
        jButton22.setText(resourceMap.getString("jButton22.text")); // NOI18N
        jButton22.setName("jButton22"); // NOI18N
        jPanel4.add(jButton22);
        jButton22.setBounds(360, 60, 42, 40);

        jButton23.setFont(resourceMap.getFont("jButton23.font")); // NOI18N
        jButton23.setForeground(resourceMap.getColor("jButton23.foreground")); // NOI18N
        jButton23.setText(resourceMap.getString("jButton23.text")); // NOI18N
        jButton23.setName("jButton23"); // NOI18N
        jPanel4.add(jButton23);
        jButton23.setBounds(400, 60, 41, 40);

        jButton24.setFont(resourceMap.getFont("jButton24.font")); // NOI18N
        jButton24.setForeground(resourceMap.getColor("jButton24.foreground")); // NOI18N
        jButton24.setText(resourceMap.getString("jButton24.text")); // NOI18N
        jButton24.setName("jButton24"); // NOI18N
        jPanel4.add(jButton24);
        jButton24.setBounds(440, 60, 40, 40);

        jButton25.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton25.setForeground(resourceMap.getColor("jButton25.foreground")); // NOI18N
        jButton25.setText(resourceMap.getString("jButton25.text")); // NOI18N
        jButton25.setName("jButton25"); // NOI18N
        jPanel4.add(jButton25);
        jButton25.setBounds(480, 60, 40, 40);

        jButton26.setFont(resourceMap.getFont("jButton26.font")); // NOI18N
        jButton26.setForeground(resourceMap.getColor("jButton26.foreground")); // NOI18N
        jButton26.setText(resourceMap.getString("jButton26.text")); // NOI18N
        jButton26.setName("jButton26"); // NOI18N
        jPanel4.add(jButton26);
        jButton26.setBounds(520, 60, 41, 40);

        jButton27.setFont(resourceMap.getFont("jButton27.font")); // NOI18N
        jButton27.setForeground(resourceMap.getColor("jButton27.foreground")); // NOI18N
        jButton27.setText(resourceMap.getString("jButton27.text")); // NOI18N
        jButton27.setName("jButton27"); // NOI18N
        jPanel4.add(jButton27);
        jButton27.setBounds(560, 60, 40, 40);

        jButton28.setFont(resourceMap.getFont("jButton28.font")); // NOI18N
        jButton28.setForeground(resourceMap.getColor("jButton28.foreground")); // NOI18N
        jButton28.setText(resourceMap.getString("jButton28.text")); // NOI18N
        jButton28.setName("jButton28"); // NOI18N
        jPanel4.add(jButton28);
        jButton28.setBounds(600, 60, 46, 40);

        jButton29.setFont(resourceMap.getFont("jButton29.font")); // NOI18N
        jButton29.setForeground(resourceMap.getColor("jButton29.foreground")); // NOI18N
        jButton29.setText(resourceMap.getString("jButton29.text")); // NOI18N
        jButton29.setName("jButton29"); // NOI18N
        jPanel4.add(jButton29);
        jButton29.setBounds(650, 60, 40, 40);

        jButton30.setFont(resourceMap.getFont("jButton30.font")); // NOI18N
        jButton30.setForeground(resourceMap.getColor("jButton30.foreground")); // NOI18N
        jButton30.setText(resourceMap.getString("jButton30.text")); // NOI18N
        jButton30.setName("jButton30"); // NOI18N
        jPanel4.add(jButton30);
        jButton30.setBounds(680, 60, 40, 40);

        jButton31.setFont(resourceMap.getFont("jButton31.font")); // NOI18N
        jButton31.setForeground(resourceMap.getColor("jButton31.foreground")); // NOI18N
        jButton31.setText(resourceMap.getString("jButton31.text")); // NOI18N
        jButton31.setName("jButton31"); // NOI18N
        jPanel4.add(jButton31);
        jButton31.setBounds(720, 60, 40, 40);

        jButton32.setFont(resourceMap.getFont("jButton32.font")); // NOI18N
        jButton32.setForeground(resourceMap.getColor("jButton32.foreground")); // NOI18N
        jButton32.setText(resourceMap.getString("jButton32.text")); // NOI18N
        jButton32.setName("jButton32"); // NOI18N
        jPanel4.add(jButton32);
        jButton32.setBounds(760, 60, 120, 40);

        jScrollPane3.setMaximumSize(new java.awt.Dimension(2147483647, 32767));
        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Término", "Palabras Claves", "Categoría", "Definiciones"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable1);

        jPanel4.add(jScrollPane3);
        jScrollPane3.setBounds(30, 110, 1130, 370);

        jComboBox6.setName("jComboBox6"); // NOI18N
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });
        jPanel4.add(jComboBox6);
        jComboBox6.setBounds(40, 50, 200, 20);

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        jPanel4.add(jLabel15);
        jLabel15.setBounds(40, 30, 60, 14);

        jTabbedPane2.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jTabbedPane1.addTab(resourceMap.getString("jTabbedPane2.TabConstraints.tabTitle"), jTabbedPane2); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        mainPanel.add(jPanel3);
        jPanel3.setBounds(10, 20, 1220, 580);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(null);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel2.add(jLabel1);
        jLabel1.setBounds(60, 40, 54, 14);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel2.add(jLabel2);
        jLabel2.setBounds(60, 66, 73, 14);

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        jPanel2.add(jLabel12);
        jLabel12.setBounds(60, 96, 64, 14);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3);
        jLabel3.setBounds(30, 126, 102, 14);

        jComboBox5.setEditable(true);
        jComboBox5.setName("jComboBox5"); // NOI18N
        jPanel2.add(jComboBox5);
        jComboBox5.setBounds(140, 30, 520, 20);

        jComboBox1.setEditable(true);
        jComboBox1.setMaximumRowCount(10000);
        jComboBox1.setName("jComboBox1"); // NOI18N
        jPanel2.add(jComboBox1);
        jComboBox1.setBounds(140, 60, 520, 20);

        jComboBox2.setMaximumRowCount(100);
        jComboBox2.setName("jComboBox2"); // NOI18N
        jPanel2.add(jComboBox2);
        jComboBox2.setBounds(140, 90, 520, 20);

        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setName("jTextField3"); // NOI18N
        jPanel2.add(jTextField3);
        jTextField3.setBounds(140, 120, 520, 20);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4);
        jLabel4.setBounds(50, 150, 72, 14);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(140, 150, 520, 130);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel2.add(jLabel5);
        jLabel5.setBounds(50, 296, 54, 14);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel2.add(jLabel6);
        jLabel6.setBounds(50, 336, 58, 14);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel2.add(jLabel7);
        jLabel7.setBounds(50, 366, 38, 14);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jPanel2.add(jLabel8);
        jLabel8.setBounds(30, 396, 82, 14);

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        jPanel2.add(jLabel13);
        jLabel13.setBounds(20, 426, 102, 14);

        jComboBox3.setEditable(true);
        jComboBox3.setName("jComboBox3"); // NOI18N
        jPanel2.add(jComboBox3);
        jComboBox3.setBounds(140, 290, 520, 20);

        jTextField5.setText(resourceMap.getString("jTextField5.text")); // NOI18N
        jTextField5.setName("jTextField5"); // NOI18N
        jPanel2.add(jTextField5);
        jTextField5.setBounds(140, 330, 520, 20);

        jComboBox4.setEditable(true);
        jComboBox4.setName("jComboBox4"); // NOI18N
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox4);
        jComboBox4.setBounds(140, 360, 520, 20);

        jTextField7.setText(resourceMap.getString("jTextField7.text")); // NOI18N
        jTextField7.setName("jTextField7"); // NOI18N
        jPanel2.add(jTextField7);
        jTextField7.setBounds(140, 390, 520, 20);

        jTextField9.setText(resourceMap.getString("jTextField9.text")); // NOI18N
        jTextField9.setName("jTextField9"); // NOI18N
        jPanel2.add(jTextField9);
        jTextField9.setBounds(140, 420, 520, 20);

        jButton4.setLabel(resourceMap.getString("jButton4.label")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4);
        jButton4.setBounds(500, 460, 160, 23);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);
        jButton1.setBounds(140, 460, 160, 23);

        mainPanel.add(jPanel2);
        jPanel2.setBounds(10, 20, 690, 500);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(biblioteca.BibliotecaApp.class).getContext().getActionMap(BibliotecaView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        menuBar.add(jMenu1);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1262, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1092, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        TrabajoXML xml = new TrabajoXML();
        Glosario glosa = new Glosario();
        if (!jTextField8.getText().isEmpty() && !jTextArea2.getText().isEmpty()) {
            glosa.setNombre(jTextField8.getText());
            glosa.setDescip(jTextArea2.getText());
            try {
                xml.annadirGlosario(glosa);
                actualizatGlosarioIncluir();
                jTextArea2.setText("");
                jTextField8.setText("");
            } catch (Exception ex) {
                Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Los campos estan vacios", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_jButton3ActionPerformed
    private List<String> obtenerPalabrasClaves(String todasSeparasComas) {
        List<String> palabrasClaves = new ArrayList<String>();

        String[] pe = todasSeparasComas.split(",");

        palabrasClaves.addAll(Arrays.asList(pe));

        return palabrasClaves;
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         TrabajoXML xml = new TrabajoXML();
        if (jComboBox5.getSelectedItem()!= null && !jTextField3.getText().isEmpty() && jComboBox2.getSelectedIndex() != -1 && jComboBox1.getSelectedItem() != null) {
            if (this.lisDifi != null) {
                Termino ter = new Termino();
                ter.setNombre(jComboBox5.getSelectedItem().toString());
                ter.setPalabrasClaves(obtenerPalabrasClaves(jTextField3.getText()));
                Categoria nue = new Categoria();
                nue.setNombre(jComboBox1.getSelectedItem().toString());
                Glosario gl = new Glosario();
                gl.setNombre(jComboBox2.getSelectedItem().toString());
                ter.setGlosa(gl);
                ter.setCategoria(nue);
                ter.setDefiniciones(lisDifi);
                try {
                    xml.annadirTermino(ter);
                    actualizatCategorias();
                    actualizatAutores();
                    actualizatArchivos();
                    this.lisDifi = null;
                    jComboBox5.setSelectedIndex(-1);
                    jTextField3.setText("");
                    jComboBox1.setSelectedIndex(-1);
                    jComboBox2.setSelectedIndex(-1);
                    
                } catch (Exception ex) {
                    Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
                }
                

            } else {
                JOptionPane.showMessageDialog(null, "Debe incluir al menos una definición", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

            try {
            } catch (Exception ex) {
                Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Algunos campos estan vacios", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if (this.lisDifi == null);
        this.lisDifi = new ArrayList<DifinicionAutor>();
       
        if (jComboBox3.getSelectedItem()!= null && !jTextField5.getText().isEmpty() && jComboBox4.getSelectedItem()!= null && !jTextField7.getText().isEmpty() && !jTextArea1.getText().isEmpty() && !jTextField9.getText().isEmpty()) {
            DifinicionAutor nueva = new DifinicionAutor();
            nueva.setDefinición(jTextArea1.getText());
            nueva.setAutor(jComboBox3.getSelectedItem().toString());
            nueva.setNombreArchivo(jComboBox4.getSelectedItem().toString());
            nueva.setLocalizacionPC(jTextField7.getText());
            nueva.setNombreArticulo(jTextField9.getText());
            
            Calendar c = Calendar.getInstance();
             c.setTime(new Date());
        
             String   dia = Integer.toString(c.get(Calendar.DATE));
             int mes = (c.get(Calendar.MONTH));
             String   annio = Integer.toString(c.get(Calendar.YEAR));
             mes++;
           
             try {
                
                nueva.setPagDefinicion(Integer.parseInt(jTextField5.getText()));
                nueva.setAnno(annio);
                nueva.setMes(String.valueOf(mes));
                nueva.setDia(dia);
                lisDifi.add(nueva);
                actualizatAutores();
                actualizatArchivos();
                jTextArea1.setText("");
                jComboBox3.setSelectedIndex(-1);
                jComboBox4.setSelectedIndex(-1);
                jTextField7.setText("");
                jTextField9.setText("");
                jTextField5.setText("");
                
                

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El número de la página está incorrecto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Algunos campos estan vacios", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jPanel1.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
       if(jComboBox4.getSelectedIndex() != -1){
        TrabajoXML xml = new TrabajoXML();
        try {
            jTextField9.setText(xml.obtenerNombreDocumento(jComboBox4.getSelectedItem().toString()));
            jTextField7.setText(xml.obtenerLocalizacionDocumento(jComboBox4.getSelectedItem().toString()));
            
        } catch (Exception ex) {
            Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
        }
       }
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        activarPanel(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        activarPanel(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
       activarPanel(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        // TODO add your handling code here:
        if(jComboBox6.getSelectedIndex() != -1){
            actualizarTableTerminos(jComboBox6.getSelectedItem().toString());
        }
        
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 2 && jTable1.getSelectedRow()!=-1){
         FrameDefini nue = new FrameDefini();
         Termino termiNuevo = new Termino();
         termiNuevo.setNombre(jTable1.getModel().getValueAt(jTable1.getSelectedRow(),0).toString());
            
         TrabajoXML xml = new TrabajoXML();
            try {
                termiNuevo = xml.obtenerTerminoDadoSuNombre(termiNuevo);
                nue.setTermi(termiNuevo);
                nue.mostrarDefiniciones();
                nue.setVisible(true);
            } catch (Exception ex) {
                Logger.getLogger(BibliotecaView.class.getName()).log(Level.SEVERE, null, ex);
            }
         
        }
        
        
        
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable2MouseClicked

    private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox7ActionPerformed

    private void jComboBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox8ActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        
        
        switch (jTabbedPane1.getSelectedIndex()){
            case 0: {
                actualizatGlosarioConsultar(jComboBox8);
            };
                break;
           case 1: {
                
            };
                break;
           case 2: {
                
            };
                break;
           case 3: {
                actualizatGlosarioConsultar(jComboBox6);
            };
                break;    
            
         }
        
        
         
         
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JButton jButton61;
    private javax.swing.JButton jButton62;
    private javax.swing.JButton jButton63;
    private javax.swing.JButton jButton64;
    private javax.swing.JButton jButton65;
    private javax.swing.JButton jButton66;
    private javax.swing.JButton jButton67;
    private javax.swing.JButton jButton68;
    private javax.swing.JButton jButton69;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton70;
    private javax.swing.JButton jButton71;
    private javax.swing.JButton jButton72;
    private javax.swing.JButton jButton73;
    private javax.swing.JButton jButton74;
    private javax.swing.JButton jButton75;
    private javax.swing.JButton jButton76;
    private javax.swing.JButton jButton77;
    private javax.swing.JButton jButton78;
    private javax.swing.JButton jButton79;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton80;
    private javax.swing.JButton jButton81;
    private javax.swing.JButton jButton82;
    private javax.swing.JButton jButton83;
    private javax.swing.JButton jButton84;
    private javax.swing.JButton jButton85;
    private javax.swing.JButton jButton86;
    private javax.swing.JButton jButton87;
    private javax.swing.JButton jButton88;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
