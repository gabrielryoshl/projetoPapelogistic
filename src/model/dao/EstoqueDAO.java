
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.Estoque;

public class EstoqueDAO {

    
    public void inserir(Estoque estoque) {
       
        String sql = "INSERT INTO Estoque (CodProduto, ValorProduto, Tipo) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estoque.getCodProduto());
            stmt.setInt(2, estoque.getValorProduto());
            stmt.setString(3, estoque.getTipo());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Item de estoque cadastrado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir estoque: " + e.getMessage(), e);
        }
    }

    // 2. LISTAR TODOS (SELECT)
    public List<Estoque> listar() {
        String sql = "SELECT * FROM Estoque";
        List<Estoque> listaEstoque = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Estoque e = new Estoque();
                e.setCodProduto(rs.getInt("CodProduto"));
                e.setValorProduto(rs.getInt("ValorProduto"));
                e.setTipo(rs.getString("Tipo"));
                
                listaEstoque.add(e);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar estoque: " + e.getMessage(), e);
        }

        return listaEstoque;
    }

    // 3. ATUALIZAR (UPDATE)
    public void atualizar(Estoque estoque) {
        String sql = "UPDATE Estoque SET ValorProduto = ?, Tipo = ? WHERE CodProduto = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estoque.getValorProduto());
            stmt.setString(2, estoque.getTipo());
            stmt.setInt(3, estoque.getCodProduto()); 

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Estoque atualizado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar estoque: " + e.getMessage(), e);
        }
    }

  
    public void deletar(int codProduto) {
        String sql = "DELETE FROM Estoque WHERE CodProduto = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codProduto);
            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Item de estoque excluído com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir estoque: " + e.getMessage(), e);
        }
    }
    
}
