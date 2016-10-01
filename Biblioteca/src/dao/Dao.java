/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dominio.Glosario;
import dominio.Termino;
import java.util.List;
import util.TerminoUtil;

/**
 *
 * @author Eduardo
 */
public interface Dao {
    
    /**
     * Método que obtiene todos los terminos de un determinado glosario. 
     * @param glosa variable de tipo Glosario que almacena el nombre del glosario. 
     * @return List<Termino>
     */
    public List<TerminoUtil> obtenerTerminosUnGlosario(Glosario glosa) throws Exception;
    
    /**
     * Método que carga todos los datos de un termino dado su nombre
     * @return Termino
     * @throws Exception 
     */
    public Termino obtenerTerminoDadoSuNombre(Termino term) throws Exception;
    
    
    /**
     * Método que añade una definición al termino
     * @param term 
     * @throws Exception
     */
    public void annadirDifinicion(Termino term) throws Exception;
   
}
