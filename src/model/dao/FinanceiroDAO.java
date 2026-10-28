
package model.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.conexao.Conexao;
import model.entidades.Financeiro;

public class FinanceiroDAO {

    
    public void inserir(Financeiro financeiro) {
        String sql = "INSERT INTO Financeiro (CodFinanceiro, Caixa, ValorEntrada, ValorSaida, CodPedido) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, financeiro.getCodFinanceiro());
            stmt.setDouble(2, financeiro.getCaixa());
            stmt.setDouble(3, financeiro.getValorEntrada());
            stmt.setDouble(4, financeiro.getValorSaida());
            stmt.setInt(5, financeiro.getCodPedido());

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Registro financeiro cadastrado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir financeiro: " + e.getMessage(), e);
        }
    }

    public List<Financeiro> listar() {
        String sql = "SELECT * FROM Financeiro";
        List<Financeiro> listaFinanceiro = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Financeiro f = new Financeiro();
                f.setCodFinanceiro(rs.getInt("CodFinanceiro"));
                f.setCaixa(rs.getDouble("Caixa"));
                f.setValorEntrada(rs.getDouble("ValorEntrada"));
                f.setValorSaida(rs.getDouble("ValorSaida"));
                f.setCodPedido(rs.getInt("CodPedido"));
                
                listaFinanceiro.add(f);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar financeiro: " + e.getMessage(), e);
        }

        return listaFinanceiro;
    }

  
    public void atualizar(Financeiro financeiro) {
        String sql = "UPDATE Financeiro SET Caixa = ?, ValorEntrada = ?, ValorSaida = ?, CodPedido = ? WHERE CodFinanceiro = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, financeiro.getCaixa());
            stmt.setDouble(2, financeiro.getValorEntrada());
            stmt.setDouble(3, financeiro.getValorSaida());
            stmt.setInt(4, financeiro.getCodPedido());
            stmt.setInt(5, financeiro.getCodFinanceiro()); // Identifica pelo CodFinanceiro

            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Registro financeiro atualizado com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar financeiro: " + e.getMessage(), e);
        }
    }

  
    public void deletar(int codFinanceiro) {
        String sql = "DELETE FROM Financeiro WHERE CodFinanceiro = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codFinanceiro);
            if (stmt.executeUpdate() == 0) { throw new SQLException("Nenhum registro afetado. Confira os identificadores informados."); }
            System.out.println("Registro financeiro excluído com sucesso!");

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir financeiro: " + e.getMessage(), e);
        }
    }
}
