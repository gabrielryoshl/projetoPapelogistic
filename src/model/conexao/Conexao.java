
package model.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    private static final String URL_SERVIDOR = "jdbc:mysql://localhost:3306/";
    private static final String URL = "jdbc:mysql://localhost:3306/papelogistic";
    private static final String USUARIO = "root";
    private static final String SENHA = "";
    private static final String[] SENHAS_TENTATIVA = {SENHA, "root", "1234", "admin"};

    private static String senhaAtiva = SENHA;
    private static boolean bancoInicializado = false;

    private static final String[] SQL_ESTRUTURA = {
        "CREATE DATABASE IF NOT EXISTS papelogistic DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci",
        "USE papelogistic",
        "CREATE TABLE IF NOT EXISTS cliente ("
            + "CodCliente INT NOT NULL AUTO_INCREMENT, "
            + "IdPessoa VARCHAR(100) NOT NULL, "
            + "nomeCliente VARCHAR(150) NOT NULL, "
            + "emailCliente VARCHAR(150) DEFAULT NULL, "
            + "telefoneCliente VARCHAR(20) DEFAULT NULL, "
            + "cep CHAR(8) DEFAULT NULL, "
            + "PRIMARY KEY (CodCliente)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci",
        "CREATE TABLE IF NOT EXISTS produto ("
            + "CodProduto INT NOT NULL AUTO_INCREMENT, "
            + "nomeProduto VARCHAR(150) NOT NULL, "
            + "tipo VARCHAR(100) NOT NULL, "
            + "valor DECIMAL(10,2) NOT NULL, "
            + "PRIMARY KEY (CodProduto)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci",
        "CREATE TABLE IF NOT EXISTS usuario ("
            + "login_simples VARCHAR(50) NOT NULL, "
            + "senha VARCHAR(255) NOT NULL, "
            + "nome VARCHAR(100) NOT NULL, "
            + "telefoneUsuario VARCHAR(20) DEFAULT NULL, "
            + "emailUsuario VARCHAR(150) NOT NULL, "
            + "PRIMARY KEY (login_simples), "
            + "UNIQUE KEY emailUsuario (emailUsuario)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci",
        "CREATE TABLE IF NOT EXISTS estoque ("
            + "CodProduto INT NOT NULL, "
            + "quantidade INT NOT NULL DEFAULT 0, "
            + "PRIMARY KEY (CodProduto), "
            + "CONSTRAINT FK_Estoque_Produto FOREIGN KEY (CodProduto) "
            + "REFERENCES produto (CodProduto) ON DELETE CASCADE ON UPDATE CASCADE"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci",
        "CREATE TABLE IF NOT EXISTS pedido ("
            + "CodPedido INT NOT NULL AUTO_INCREMENT, "
            + "CodCliente INT NOT NULL, "
            + "dataPedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(), "
            + "PRIMARY KEY (CodPedido), "
            + "KEY IX_Pedido_Cliente (CodCliente), "
            + "CONSTRAINT FK_Pedido_Cliente FOREIGN KEY (CodCliente) "
            + "REFERENCES cliente (CodCliente) ON UPDATE CASCADE"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci",
        "CREATE TABLE IF NOT EXISTS item_pedido ("
            + "CodPedido INT NOT NULL, "
            + "CodProduto INT NOT NULL, "
            + "quantidade INT NOT NULL, "
            + "ValorUnitario DECIMAL(10,2) NOT NULL, "
            + "PRIMARY KEY (CodPedido, CodProduto), "
            + "KEY IX_ItemPedido_Produto (CodProduto), "
            + "CONSTRAINT FK_ItemPedido_Pedido FOREIGN KEY (CodPedido) "
            + "REFERENCES pedido (CodPedido) ON DELETE CASCADE ON UPDATE CASCADE, "
            + "CONSTRAINT FK_ItemPedido_Produto FOREIGN KEY (CodProduto) "
            + "REFERENCES produto (CodProduto) ON UPDATE CASCADE"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci",
        "CREATE TABLE IF NOT EXISTS financeiro ("
            + "CodFinanceiro INT NOT NULL AUTO_INCREMENT, "
            + "Caixa DECIMAL(10,2) NOT NULL DEFAULT 0.00, "
            + "ValorEntrada DECIMAL(10,2) NOT NULL DEFAULT 0.00, "
            + "ValorSaida DECIMAL(10,2) NOT NULL DEFAULT 0.00, "
            + "CodPedido INT DEFAULT NULL, "
            + "dataMovimentacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(), "
            + "PRIMARY KEY (CodFinanceiro), "
            + "KEY IX_Financeiro_Pedido (CodPedido), "
            + "CONSTRAINT FK_Financeiro_Pedido FOREIGN KEY (CodPedido) "
            + "REFERENCES pedido (CodPedido) ON DELETE SET NULL ON UPDATE CASCADE"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci"
    };

    public static Connection conectar() throws SQLException {
        if (!bancoInicializado) {
            inicializarBanco();
        }
        return DriverManager.getConnection(URL, USUARIO, senhaAtiva);
    }

    private static synchronized void inicializarBanco() throws SQLException {
        if (bancoInicializado) {
            return;
        }
        SQLException ultimoErro = null;
        for (String tentativa : SENHAS_TENTATIVA) {
            try (Connection conexaoServidor = DriverManager.getConnection(URL_SERVIDOR, USUARIO, tentativa);
                 Statement stmt = conexaoServidor.createStatement()) {
                for (String sql : SQL_ESTRUTURA) {
                    stmt.executeUpdate(sql);
                }
                senhaAtiva = tentativa;
                bancoInicializado = true;
                return;
            } catch (SQLException erro) {
                ultimoErro = erro;
                // Código 1045 = Access denied (tenta a próxima senha padrão).
                if (erro.getErrorCode() != 1045) {
                    break;
                }
            }
        }
        throw ultimoErro;
    }

    public static void main(String[] args) {
        try (Connection conexao = conectar()) {
            System.out.println("Conexão realizada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
    }
}
