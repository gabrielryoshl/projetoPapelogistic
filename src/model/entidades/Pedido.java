
package model.entidades;

public class Pedido {

    private int codPedido;
    private int codCliente;

    public Pedido() {
        
    }
    public Pedido(int codPedido, int codCliente) {
        this.codPedido = codPedido;
        this.codCliente = codCliente;
    }
    
    

    public void setCodPedido(int codPedido) {
        this.codPedido = codPedido;
    }

    public int getCodPedido() {
        return codPedido;
    }

    public void setCodCliente(int codCliente) {
        this.codCliente = codCliente;
    }

    public int getCodCliente() {
        return codCliente;
    }
}
