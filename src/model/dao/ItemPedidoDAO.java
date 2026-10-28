
package model.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.ItemPedido;

public class ItemPedidoDAO {

    public void inserir(ItemPedido itemPedido) {
        String sql = "INSERT INTO item_pedido (CodPedido, CodProduto, quantidade, ValorUnitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemPedido.getCodPedido());
            stmt.setInt(2, itemPedido.getCodProduto());
            stmt.setInt(3, itemPedido.getQuantidade());
            stmt.setDouble(4, itemPedido.getValorUnitario());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir item_pedido: " + e.getMessage(), e);
        }
    }

    public List<ItemPedido> listar() {
        String sql = "SELECT * FROM item_pedido";
        List<ItemPedido> listaItemPedido = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ItemPedido ip = new ItemPedido();
                ip.setCodPedido(rs.getInt("CodPedido"));
                ip.setCodProduto(rs.getInt("CodProduto"));
                ip.setQuantidade(rs.getInt("quantidade"));
                ip.setValorUnitario(rs.getDouble("ValorUnitario"));
                
                listaItemPedido.add(ip);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar item_pedido: " + e.getMessage(), e);
        }

        return listaItemPedido;
    }

    public void atualizar(ItemPedido itemPedido) {
        String sql = "UPDATE item_pedido SET quantidade = ?, ValorUnitario = ? WHERE CodPedido = ? AND CodProduto = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemPedido.getQuantidade());
            stmt.setDouble(2, itemPedido.getValorUnitario());
            stmt.setInt(3, itemPedido.getCodPedido());
            stmt.setInt(4, itemPedido.getCodProduto());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar item_pedido: " + e.getMessage(), e);
        }
    }

    public void deletar(int codPedido, int codProduto) {
        String sql = "DELETE FROM item_pedido WHERE CodPedido = ? AND CodProduto = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codPedido);
            stmt.setInt(2, codProduto);
            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir item_pedido: " + e.getMessage(), e);
        }
    }
}
