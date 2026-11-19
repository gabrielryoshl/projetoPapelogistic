
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.Produto;

public class ProdutoDAO {

    public void inserir(Produto produto) {
        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                int codigo = inserirProduto(conn, produto);
                String sqlEstoque = "INSERT IGNORE INTO estoque (CodProduto, quantidade) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlEstoque)) {
                    stmt.setInt(1, codigo);
                    stmt.setInt(2, Math.max(0, produto.getQuantidade()));
                    stmt.executeUpdate();
                }
                conn.commit();
                System.out.println("Produto cadastrado com sucesso!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir produto: " + e.getMessage(), e);
        }
    }

    public void inserirComEstoque(Produto produto, int quantidade) {
        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                int codigo = inserirProduto(conn, produto);
                String sql = "INSERT INTO estoque (CodProduto, quantidade) VALUES (?, ?) "
                    + "ON DUPLICATE KEY UPDATE quantidade = VALUES(quantidade)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, codigo);
                    stmt.setInt(2, quantidade);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao cadastrar produto e estoque: " + e.getMessage(), e);
        }
    }

    private int inserirProduto(Connection conn, Produto produto) throws SQLException {
        int codigo = produto.getCodProduto();
        if (codigo <= 0) {
            codigo = Conexao.proximoCodigo(conn, "produto", "CodProduto");
        }
        String sql = "INSERT INTO produto (CodProduto, nomeProduto, tipo, valor) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            stmt.setString(2, produto.getNomeProduto());
            stmt.setString(3, produto.getTipo());
            stmt.setDouble(4, produto.getValor());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            produto.setCodProduto(codigo);
            return codigo;
        }
    }

    public List<Produto> listar() {
        String sql = "SELECT * FROM produto";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setCodProduto(rs.getInt("CodProduto"));
                produto.setTipo(rs.getString("tipo"));
                produto.setValor(rs.getDouble("valor"));
                produto.setNomeProduto(rs.getString("nomeProduto"));
                
                produtos.add(produto);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar produtos: " + e.getMessage(), e);
        }

        return produtos;
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nomeProduto = ?, tipo = ?, valor = ? WHERE CodProduto = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNomeProduto());
            stmt.setString(2, produto.getTipo());
            stmt.setDouble(3, produto.getValor());
            stmt.setInt(4, produto.getCodProduto());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Produto atualizado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    
    public void atualizarComEstoque(Produto produto, int quantidade) {
        String sqlProduto = "UPDATE produto SET nomeProduto = ?, tipo = ?, valor = ? WHERE CodProduto = ?";
        String sqlEstoque = "INSERT INTO estoque (CodProduto, quantidade) VALUES (?, ?) "
            + "ON DUPLICATE KEY UPDATE quantidade = VALUES(quantidade)";
        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmtProd = conn.prepareStatement(sqlProduto)) {
                    stmtProd.setString(1, produto.getNomeProduto());
                    stmtProd.setString(2, produto.getTipo());
                    stmtProd.setDouble(3, produto.getValor());
                    stmtProd.setInt(4, produto.getCodProduto());
                    if (stmtProd.executeUpdate() == 0) {
                        throw new SQLException("O item não está mais cadastrado.");
                    }
                }
                try (PreparedStatement stmtEst = conn.prepareStatement(sqlEstoque)) {
                    stmtEst.setInt(1, produto.getCodProduto());
                    stmtEst.setInt(2, quantidade);
                    stmtEst.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao editar o item de estoque: " + e.getMessage(), e);
        }
    }

    public void deletar(int codProduto) {
        String sqlItemPedido = "DELETE FROM item_pedido WHERE CodProduto = ?";
        String sqlEstoque = "DELETE FROM estoque WHERE CodProduto = ?";
        String sqlProduto = "DELETE FROM produto WHERE CodProduto = ?";

        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmtItem = conn.prepareStatement(sqlItemPedido)) {
                    stmtItem.setInt(1, codProduto);
                    stmtItem.executeUpdate();
                }
                try (PreparedStatement stmtEstoque = conn.prepareStatement(sqlEstoque)) {
                    stmtEstoque.setInt(1, codProduto);
                    stmtEstoque.executeUpdate();
                }
                try (PreparedStatement stmtProduto = conn.prepareStatement(sqlProduto)) {
                    stmtProduto.setInt(1, codProduto);
                    if (stmtProduto.executeUpdate() == 0) {
                        throw new SQLException("Nenhum registro afetado. Confira os identificadores informados.");
                    }
                }
                conn.commit();
                System.out.println("Produto excluído com sucesso!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }
}
