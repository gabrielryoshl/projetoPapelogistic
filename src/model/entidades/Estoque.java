
package model.entidades;

public class Estoque {

    private int codProduto;
    private int valorProduto;
    private String tipo;
    
    public Estoque() {
    }

    public Estoque(int codProduto, int valorProduto, String tipo) {
        this.codProduto = codProduto;
        this.valorProduto = valorProduto;
        this.tipo = tipo;
    }
    
    

    public void setCodProduto(int codProduto) {
        this.codProduto = codProduto;
    }

    public int getCodProduto() {
        return codProduto;
    }

    public void setValorProduto(int valorProduto) {
        this.valorProduto = valorProduto;
    }

    public int getValorProduto() {
        return valorProduto;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
