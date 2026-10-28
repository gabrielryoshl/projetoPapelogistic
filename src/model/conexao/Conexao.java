
package model.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    
    private static final String URL = "jdbc:mysql://localhost:3306/Papelogistic";
    private static final String USUARIO = "root";            
    private static final String SENHA = "";         

    
    public static Connection conectar() throws SQLException {
       
       return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
    
    public static void main(String[] args) {
        
    
        try (Connection conexao = conectar()) {
            System.out.println("Conexão realizada com sucesso!");
            
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
    }
}
