
package model.entidades;


    
    public class Cliente {

    private int cliente;
    private String nomeCliente;
    private String cidade;

    public Cliente() {
    }
    
    public Cliente(int cliente, String nomeCliente, String cidade) {
        this.cliente = cliente;
        this.nomeCliente = nomeCliente;
        this.cidade = cidade;
    }

    public int getCliente() {
        return cliente;
    }

    public void setCliente(int cliente) {
        this.cliente = cliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    

    
}
    

