package sis2.pkg2020.modelo.Wrapper;

/**
 * Esta al igual que la clase Categoria, será utilizada como la base de la 
 * estructura de datos de la hoja 3.
 * @author Marco Speranza López
 */
public class WrapperBrutoRetencion {
   private Integer brutoAnual;
   private Integer retencion;

    public WrapperBrutoRetencion(Integer brutoAnual, Integer retencion) {
        this.brutoAnual = brutoAnual;
        this.retencion = retencion;
    }

    public Integer getBrutoAnual() {
        return brutoAnual;
    }

    public void setBrutoAnual(Integer brutoAnual) {
        this.brutoAnual = brutoAnual;
    }

    public Integer getRetencion() {
        return retencion;
    }

    public void setRetencion(Integer retencion) {
        this.retencion = retencion;
    }

    @Override
    public String toString() {
        return "  brutoAnual= " + brutoAnual + ", retencion= " + retencion +"\n";
    }
   
}
