/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import java.util.List;

/**
 *
 * @author Eduardo
 */
public class Glosario {
    private String nombre; // nombre del glosario
    private String descip; // descripción del glosario
    private List<Termino> listTerminos;
   
    
    
    private int entradasDuplicadas; //0 no permite entradas de terminos duplicados, 1 permite entrada de datos duplicados
    private String formatoMuestra; // los tipos de estilos de muestra del diccionario, dictionary:es el más basico
    private int mostrarEnlaceEspecial;//0 la busqueda es mas restringida, 1 abilita la busqueda más abierta
    private int mostrarAlfabeto;//0 la busqueda es mas restringida, 1 abilita la busqueda más abierta
    private int mostrarEnlace;//0 la busqueda es mas restringida, 1 abilita la busqueda más abierta
    private int permitirComentario;
    private int hiperenlaceAutomatico;//0 enlaza automaticamente las palabras donde quiera que se encuentren en el curso, 1 no las enlaza
    private int entradasAprobadasPorDefecto; //0 las entradas deben ser aprobadas, 1 el profesor aprueba las entradas.
    private int tipoGlosario; //0 glosario secundario, 1 glosario principal
    private int cantTerminosPagina; //cantidad de terminos que se mostraran en una pagina

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
     * @return the descip
     */
    public String getDescip() {
        return descip;
    }

    /**
     * @param descip the descip to set
     */
    public void setDescip(String descip) {
        this.descip = descip;
    }

    /**
     * @return the listTerminos
     */
    public List<Termino> getListTerminos() {
        return listTerminos;
    }

    /**
     * @param listTerminos the listTerminos to set
     */
    public void setListTerminos(List<Termino> listTerminos) {
        this.listTerminos = listTerminos;
    }





}