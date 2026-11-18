
package model.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.Pedido;

public class PedidoDAO {

    public void inserir(Pedido pedido) {
        String sql = "INSERT INTO pedido (CodPedido, CodCliente) VALUES (NULLIF(?, 0), ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pedido.getCodPedido());
            stmt.setInt(2, pedido.getCodCliente());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            if (pedido.getCodPedido() == 0) {
                try (ResultSet chaves = stmt.getGeneratedKeys()) {
                    if (chaves.next()) {
                        pedido.setCodPedido(chaves.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir pedido: " + e.getMessage(), e);
        }
    }

    public List<Pedido> listar() {
        String sql = "SELECT * FROM pedido";
        List<Pedido> listaPedido = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pedido p = new Pedido();
                p.setCodPedido(rs.getInt("CodPedido"));
                p.setCodCliente(rs.getInt("CodCliente"));
                
                listaPedido.add(p);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar pedido: " + e.getMessage(), e);
        }

        return listaPedido;
    }

    public void atualizar(Pedido pedido) {
        String sql = "UPDATE pedido SET CodCliente = ? WHERE CodPedido = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedido.getCodCliente());
            stmt.setInt(2, pedido.getCodPedido());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar pedido: " + e.getMessage(), e);
        }
    }

    public void deletar(int codPedido) {
        String sql = "DELETE FROM pedido WHERE CodPedido = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codPedido);
            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir pedido: " + e.getMessage(), e);
        }
    }
}
