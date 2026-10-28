
package model.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.Usuario;

public class UsuarioDAO {

    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO Usuario (login_simples, senha, nome, telefoneUsuario, emailUsuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getLoginSimples());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getNome());
            stmt.setString(4, usuario.getTelefoneUsuario());
            stmt.setString(5, usuario.getEmailUsuario());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir usuario: " + e.getMessage(), e);
        }
    }

    public List<Usuario> listar() {
        String sql = "SELECT * FROM Usuario";
        List<Usuario> listaUsuario = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setLoginSimples(rs.getString("login_simples"));
                u.setSenha(rs.getString("senha"));
                u.setNome(rs.getString("nome"));
                u.setTelefoneUsuario(rs.getString("telefoneUsuario"));
                u.setEmailUsuario(rs.getString("emailUsuario"));
                
                listaUsuario.add(u);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar usuario: " + e.getMessage(), e);
        }

        return listaUsuario;
    }

    public void atualizar(Usuario usuario) {
        String sql = "UPDATE Usuario SET senha = ?, nome = ?, telefoneUsuario = ?, emailUsuario = ? WHERE login_simples = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getSenha());
            stmt.setString(2, usuario.getNome());
            stmt.setString(3, usuario.getTelefoneUsuario());
            stmt.setString(4, usuario.getEmailUsuario());
            stmt.setString(5, usuario.getLoginSimples());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar usuario: " + e.getMessage(), e);
        }
    }

    public void deletar(String loginSimples) {
        String sql = "DELETE FROM Usuario WHERE login_simples = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loginSimples);
            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir usuario: " + e.getMessage(), e);
        }
    }
}

