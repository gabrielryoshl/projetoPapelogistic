
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
        String sql = "INSERT INTO cliente (CodCliente, IdPessoa, nomeCliente, emailCliente, telefoneCliente, cep) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int codigo = cliente.getCliente();
            if (codigo <= 0) {
                codigo = Conexao.proximoCodigo(conn, "cliente", "CodCliente");
            }
            stmt.setInt(1, codigo);
            stmt.setString(2, cliente.getIdPessoa());
            stmt.setString(3, cliente.getNomeCliente());
            stmt.setString(4, cliente.getEmailCliente());
            stmt.setString(5, cliente.getTelefoneCliente());
            stmt.setString(6, cliente.getCep());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            cliente.setCliente(codigo);
            System.out.println("Cliente cadastrado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir cliente: " + e.getMessage(), e);
        }
    }


    public List<Cliente> listar() {
        String sql = "SELECT * FROM cliente";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setCliente(rs.getInt("CodCliente"));
                c.setNomeCliente(rs.getString("nomeCliente"));
                c.setIdPessoa(rs.getString("IdPessoa"));
                c.setEmailCliente(rs.getString("emailCliente"));
                c.setTelefoneCliente(rs.getString("telefoneCliente"));
                c.setCep(rs.getString("cep"));
                
                clientes.add(c);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar clientes: " + e.getMessage(), e);
        }

        return clientes;
    }


    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET IdPessoa = ?, nomeCliente = ?, emailCliente = ?, telefoneCliente = ?, cep = ? WHERE CodCliente = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getIdPessoa());
            stmt.setString(2, cliente.getNomeCliente());
            stmt.setString(3, cliente.getEmailCliente());
            stmt.setString(4, cliente.getTelefoneCliente());
            stmt.setString(5, cliente.getCep());
            stmt.setInt(6, cliente.getCliente());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Cliente atualizado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void deletar(int codCliente) {
        String sqlFinanceiro = "UPDATE financeiro SET CodPedido = NULL WHERE CodPedido IN (SELECT CodPedido FROM pedido WHERE CodCliente = ?)";
        String sqlItens = "DELETE FROM item_pedido WHERE CodPedido IN (SELECT CodPedido FROM pedido WHERE CodCliente = ?)";
        String sqlPedidos = "DELETE FROM pedido WHERE CodCliente = ?";
        String sqlCliente = "DELETE FROM cliente WHERE CodCliente = ?";

        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmtFin = conn.prepareStatement(sqlFinanceiro)) {
                    stmtFin.setInt(1, codCliente);
                    stmtFin.executeUpdate();
                }
                try (PreparedStatement stmtItens = conn.prepareStatement(sqlItens)) {
                    stmtItens.setInt(1, codCliente);
                    stmtItens.executeUpdate();
                }
                try (PreparedStatement stmtPedidos = conn.prepareStatement(sqlPedidos)) {
                    stmtPedidos.setInt(1, codCliente);
                    stmtPedidos.executeUpdate();
                }
                try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                    stmtCliente.setInt(1, codCliente);
                    if (stmtCliente.executeUpdate() == 0) {
                        throw new SQLException("Nenhum registro afetado. Confira os identificadores informados.");
                    }
                }
                conn.commit();
                System.out.println("Cliente excluído com sucesso!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir cliente: " + e.getMessage(), e);
        }
    }
}
