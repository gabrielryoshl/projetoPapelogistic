
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
            inserirProduto(conn, produto);
            System.out.println("Produto cadastrado com sucesso!");
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir produto: " + e.getMessage(), e);
        }
    }

    public void inserirComEstoque(Produto produto, int quantidade) {
        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                int codigo = inserirProduto(conn, produto);
                String sql = "INSERT INTO Estoque (CodProduto, quantidade) VALUES (?, ?)";
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
        String sql = "INSERT INTO Produto (CodProduto, nomeProduto, tipo, valor) VALUES (NULLIF(?, 0), ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, produto.getCodProduto());
            stmt.setString(2, produto.getNomeProduto());
            stmt.setString(3, produto.getTipo());
            stmt.setDouble(4, produto.getValor());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            if (produto.getCodProduto() != 0) {
                return produto.getCodProduto();
            }
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getInt(1);
                }
                throw new SQLException("O banco não retornou o código do produto.");
            }
        }
    }

    public List<Produto> listar() {
        String sql = "SELECT * FROM Produto";
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
        String sql = "UPDATE Produto SET nomeProduto = ?, tipo = ?, valor = ? WHERE CodProduto = ?";

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
        String sql = "UPDATE Produto p JOIN Estoque e ON e.CodProduto = p.CodProduto "
            + "SET p.nomeProduto = ?, p.tipo = ?, p.valor = ?, e.quantidade = ? WHERE p.CodProduto = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNomeProduto());
            stmt.setString(2, produto.getTipo());
            stmt.setDouble(3, produto.getValor());
            stmt.setInt(4, quantidade);
            stmt.setInt(5, produto.getCodProduto());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("O item não está mais no estoque.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao editar o item de estoque: " + e.getMessage(), e);
        }
    }

    public void deletar(int codProduto) {
        String sql = "DELETE FROM Produto WHERE CodProduto = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codProduto);
            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Produto excluído com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }
}
