
package model.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.ItemPedido;
import model.entidades.Pedido;

public class PedidoDAO {

    public void inserir(Pedido pedido) {
        String sql = "INSERT INTO pedido (CodPedido, CodCliente) VALUES (?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int codigo = pedido.getCodPedido();
            if (codigo <= 0) {
                codigo = Conexao.proximoCodigo(conn, "pedido", "CodPedido");
            }
            stmt.setInt(1, codigo);
            stmt.setInt(2, pedido.getCodCliente());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            pedido.setCodPedido(codigo);

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir pedido: " + e.getMessage(), e);
        }
    }

    public void inserirComItem(Pedido pedido, ItemPedido item) {
        String sqlPedido = "INSERT INTO pedido (CodPedido, CodCliente) VALUES (?, ?)";
        String sqlItem = "INSERT INTO item_pedido (CodPedido, CodProduto, quantidade, ValorUnitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                int codigo = pedido.getCodPedido();
                if (codigo <= 0) {
                    codigo = Conexao.proximoCodigo(conn, "pedido", "CodPedido");
                }
                try (PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido)) {
                    stmtPedido.setInt(1, codigo);
                    stmtPedido.setInt(2, pedido.getCodCliente());
                    if (stmtPedido.executeUpdate() == 0) {
                        throw new SQLException("Nenhum registro afetado. Confira os identificadores informados.");
                    }
                }
                if (item != null) {
                    try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                        stmtItem.setInt(1, codigo);
                        stmtItem.setInt(2, item.getCodProduto());
                        stmtItem.setInt(3, item.getQuantidade());
                        stmtItem.setDouble(4, item.getValorUnitario());
                        if (stmtItem.executeUpdate() == 0) {
                            throw new SQLException("Nenhum registro afetado ao inserir o item do pedido.");
                        }
                    }
                    item.setCodPedido(codigo);
                }
                conn.commit();
                pedido.setCodPedido(codigo);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao cadastrar pedido: " + e.getMessage(), e);
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
        String sqlFinanceiro = "UPDATE financeiro SET CodPedido = NULL WHERE CodPedido = ?";
        String sqlItens = "DELETE FROM item_pedido WHERE CodPedido = ?";
        String sqlPedido = "DELETE FROM pedido WHERE CodPedido = ?";

        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmtFin = conn.prepareStatement(sqlFinanceiro)) {
                    stmtFin.setInt(1, codPedido);
                    stmtFin.executeUpdate();
                }
                try (PreparedStatement stmtItens = conn.prepareStatement(sqlItens)) {
                    stmtItens.setInt(1, codPedido);
                    stmtItens.executeUpdate();
                }
                try (PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido)) {
                    stmtPedido.setInt(1, codPedido);
                    if (stmtPedido.executeUpdate() == 0) {
                        throw new SQLException("Nenhum registro afetado. Confira os identificadores informados.");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir pedido: " + e.getMessage(), e);
        }
    }
}
