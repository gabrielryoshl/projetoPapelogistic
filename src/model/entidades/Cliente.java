
package model.entidades;


    
    public class Cliente {

    private int cliente;
    private String nomeCliente;
    private String cidade;
    private String idPessoa;
    private String emailCliente;
    private String telefoneCliente;
    private String cep;

    public String getIdPessoa() { return idPessoa; }
    public void setIdPessoa(String idPessoa) { this.idPessoa = idPessoa; }
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public String getTelefoneCliente() { return telefoneCliente; }
    public void setTelefoneCliente(String telefoneCliente) { this.telefoneCliente = telefoneCliente; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

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
    

