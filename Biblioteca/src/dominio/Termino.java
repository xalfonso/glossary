/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eduardo
 */
public class Termino {
    private String nombre;
    private List<String> palabrasClaves;
    private Categoria categoria;
    private List<DifinicionAutor> definiciones;
    private Glosario glosa;
    
    
    
    
    private int formato = 1;
    private int vinculo = 0;
    private int sencibilidadMayuscula = 0;
    private int map = 0;
    private int terminoProfesor = 1;

    public Termino(){
        this.nombre = "";
        this.palabrasClaves = new ArrayList<String>();
        this.categoria = null;
        this.definiciones = new ArrayList<DifinicionAutor>();
        glosa = null;
    }
    
    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the palabrasClaves
     */
    public List<String> getPalabrasClaves() {
        return palabrasClaves;
    }

    /**
     * @param palabrasClaves the palabrasClaves to set
     */
    public void setPalabrasClaves(List<String> palabrasClaves) {
        this.palabrasClaves = palabrasClaves;
    }

    /**
     * @return the categoria
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * @return the definiciones
     */
    public List<DifinicionAutor> getDefiniciones() {
        return definiciones;
    }

    /**
     * @param definiciones the definiciones to set
     */
    public void setDefiniciones(List<DifinicionAutor> definiciones) {
        this.definiciones = definiciones;
    }

    /**
     * @return the glosa
     */
    public Glosario getGlosa() {
        return glosa;
    }

    /**
     * @param glosa the glosa to set
     */
    public void setGlosa(Glosario glosa) {
        this.glosa = glosa;
    }

    /**
     * @param categoria the categoria to set
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
}
