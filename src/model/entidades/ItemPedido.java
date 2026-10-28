
package model.entidades;

public class ItemPedido {

    private int codPedido;
    private int codProduto;
    private int quantidade;
    private double valorUnitario;
    
    public ItemPedido (){
        
    }

    public ItemPedido(int codPedido, int codProduto, int quantidade, double valorUnitario) {
        this.codPedido = codPedido;
        this.codProduto = codProduto;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    
    public void setCodPedido(int codPedido) {
        this.codPedido = codPedido;
    }

    public int getCodPedido() {
        return codPedido;
    }

    public void setCodProduto(int codProduto) {
        this.codProduto = codProduto;
    }

    public int getCodProduto() {
        return codProduto;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }
}