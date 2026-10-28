
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.Produto;

public class ProdutoDAO {

    public void inserir(Produto produto) {
        String sql = "INSERT INTO Produto (CodProduto, tipo, valor, quantidade) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, produto.getCodProduto());
            stmt.setString(2, produto.getTipo());
            stmt.setDouble(3, produto.getValor());
            stmt.setInt(4, produto.getQuantidade());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Produto cadastrado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir produto: " + e.getMessage(), e);
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
                produto.setQuantidade(rs.getInt("quantidade"));
                
                produtos.add(produto);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar produtos: " + e.getMessage(), e);
        }

        return produtos;
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE Produto SET tipo = ?, valor = ?, quantidade = ? WHERE CodProduto = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getTipo());
            stmt.setDouble(2, produto.getValor());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setInt(4, produto.getCodProduto());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Produto atualizado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar produto: " + e.getMessage(), e);
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