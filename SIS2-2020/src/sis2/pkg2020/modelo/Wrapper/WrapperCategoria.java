package sis2.pkg2020.modelo.Wrapper;

/**
 *
 * Clase creada para mantener los datos cargados de la hoja 2.
 * 
 * Pertenecientes a las categorias correspodientes.
 * 
 * 
 * @author Marco Speranza López
 */
public class WrapperCategoria {
    
    private Integer salarioBase;
    private Integer complementos;

    public WrapperCategoria(Integer salarioBase, Integer complementos) {
        this.salarioBase = salarioBase;
        this.complementos = complementos;
    }

    public Integer getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(Integer salarioBase) {
        this.salarioBase = salarioBase;
    }

    public Integer getComplementos() {
        return complementos;
    }

    public void setComplementos(Integer complementos) {
        this.complementos = complementos;
    }

    @Override
    public String toString() {
        return  "salarioBase= " + salarioBase + ", complementos= " + complementos;
    }
    
}
