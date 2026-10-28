
package model.entidades;

public class Produto {

    private int codProduto;
    private String tipo;
    private double valor;
    private int quantidade;
    
    public Produto() {
    }

    public Produto(int codProduto, String tipo, double valor, int quantidade) {
        this.codProduto = codProduto;
        this.tipo = tipo;
        this.valor = valor;
        this.quantidade = quantidade;
    }
    
    

    public void setCodProduto(int codProduto) {
        this.codProduto = codProduto;
    }

    public int getCodProduto() {
        return codProduto;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
