package sis2.pkg2020.modelo.Wrapper;

/**
 * Esta al igual que la clase Categoria, será utilizada como la base de la 
 * estructura de datos de la hoja 3.
 * @author Marco Speranza López
 */
public class WrapperBrutoRetencion {
   private double brutoAnual;
   private double retencion;

    public WrapperBrutoRetencion(double brutoAnual, double retencion) {
        this.brutoAnual = brutoAnual;
        this.retencion = retencion;
    }

    public double getBrutoAnual() {
        return brutoAnual;
    }

    public void setBrutoAnual(double brutoAnual) {
        this.brutoAnual = brutoAnual;
    }

    public double getRetencion() {
        return retencion;
    }

    public void setRetencion(double retencion) {
        this.retencion = retencion;
    }

    @Override
    public String toString() {
        return "  brutoAnual= " + brutoAnual + ", retencion= " + retencion +"\n";
    }
   
}
