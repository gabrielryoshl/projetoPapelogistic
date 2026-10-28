
package model.entidades;

public class Usuario {

    private String loginSimples;
    private String senha;
    private String nome;
    private String telefoneUsuario;
    private String emailUsuario;
    
    public Usuario() {
        
    }

    public Usuario(String loginSimples, String senha, String nome, String telefoneUsuario, String emailUsuario) {
        this.loginSimples = loginSimples;
        this.senha = senha;
        this.nome = nome;
        this.telefoneUsuario = telefoneUsuario;
        this.emailUsuario = emailUsuario;
    }

    
    public void setLoginSimples(String loginSimples) {
        this.loginSimples = loginSimples;
    }

    public String getLoginSimples() {
        return loginSimples;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setTelefoneUsuario(String telefoneUsuario) {
        this.telefoneUsuario = telefoneUsuario;
    }

    public String getTelefoneUsuario() {
        return telefoneUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }
}
