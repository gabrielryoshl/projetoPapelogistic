
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.Cliente;

public class ClienteDAO {

    public void inserir(Cliente cliente) {
        String sql = "INSERT INTO Cliente (cliente, nomeCliente, cidade) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cliente.getCliente());
            stmt.setString(2, cliente.getNomeCliente());
            stmt.setString(3, cliente.getCidade());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Cliente cadastrado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir cliente: " + e.getMessage(), e);
        }
    }


    public List<Cliente> listar() {
        String sql = "SELECT * FROM Cliente";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setCliente(rs.getInt("cliente"));
                c.setNomeCliente(rs.getString("nomeCliente"));
                c.setCidade(rs.getString("cidade"));
                
                clientes.add(c);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar clientes: " + e.getMessage(), e);
        }

        return clientes;
    }


    public void atualizar(Cliente cliente) {
        String sql = "UPDATE Cliente SET nomeCliente = ?, cidade = ? WHERE cliente = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNomeCliente());
            stmt.setString(2, cliente.getCidade());
            stmt.setInt(3, cliente.getCliente()); // Identifica qual registro atualizar pelo ID (cliente)

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Cliente atualizado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void deletar(int codCliente) {
        String sql = "DELETE FROM Cliente WHERE cliente = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codCliente);
            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Cliente excluído com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir cliente: " + e.getMessage(), e);
        }
    }
}