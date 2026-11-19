
package model.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Conexao {

    private static final int[] PORTAS_TENTATIVA = {3306, 3307};
    private static final String USUARIO = "root";
    private static final String SENHA = "";
    private static final String[] SENHAS_TENTATIVA = {SENHA, "root", "1234", "admin", "123456", "mysql"};

    private static int portaAtiva = 3306;
    private static String senhaAtiva = SENHA;
    private static boolean bancoInicializado = false;

    private static final String PARAMETROS_JDBC = "?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=3000";

    private static String urlServidor(int porta) {
        return "jdbc:mysql://localhost:" + porta + "/" + PARAMETROS_JDBC;
    }

    private static String urlBanco(int porta) {
        return "jdbc:mysql://localhost:" + porta + "/papelogistic" + PARAMETROS_JDBC;
    }

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

    private static final String[][] COLUNAS_OBRIGATORIAS = {
        {"cliente", "CodCliente", "INT NOT NULL"},
        {"cliente", "IdPessoa", "VARCHAR(100) NOT NULL DEFAULT ''"},
        {"cliente", "nomeCliente", "VARCHAR(150) NOT NULL DEFAULT ''"},
        {"cliente", "emailCliente", "VARCHAR(150) DEFAULT NULL"},
        {"cliente", "telefoneCliente", "VARCHAR(20) DEFAULT NULL"},
        {"cliente", "cep", "CHAR(8) DEFAULT NULL"},
        {"produto", "CodProduto", "INT NOT NULL"},
        {"produto", "nomeProduto", "VARCHAR(150) NOT NULL DEFAULT '' AFTER CodProduto"},
        {"produto", "tipo", "VARCHAR(100) NOT NULL DEFAULT ''"},
        {"produto", "valor", "DECIMAL(10,2) NOT NULL DEFAULT 0.00"},
        {"usuario", "login_simples", "VARCHAR(50) NOT NULL"},
        {"usuario", "senha", "VARCHAR(255) NOT NULL DEFAULT ''"},
        {"usuario", "nome", "VARCHAR(100) NOT NULL DEFAULT ''"},
        {"usuario", "telefoneUsuario", "VARCHAR(20) DEFAULT NULL"},
        {"usuario", "emailUsuario", "VARCHAR(150) NOT NULL DEFAULT ''"},
        {"estoque", "CodProduto", "INT NOT NULL"},
        {"estoque", "quantidade", "INT NOT NULL DEFAULT 0"},
        {"pedido", "CodPedido", "INT NOT NULL"},
        {"pedido", "CodCliente", "INT NOT NULL"},
        {"pedido", "dataPedido", "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP()"},
        {"item_pedido", "CodPedido", "INT NOT NULL"},
        {"item_pedido", "CodProduto", "INT NOT NULL"},
        {"item_pedido", "quantidade", "INT NOT NULL DEFAULT 0"},
        {"item_pedido", "ValorUnitario", "DECIMAL(10,2) NOT NULL DEFAULT 0.00"},
        {"financeiro", "CodFinanceiro", "INT NOT NULL"},
        {"financeiro", "Caixa", "DECIMAL(10,2) NOT NULL DEFAULT 0.00"},
        {"financeiro", "ValorEntrada", "DECIMAL(10,2) NOT NULL DEFAULT 0.00"},
        {"financeiro", "ValorSaida", "DECIMAL(10,2) NOT NULL DEFAULT 0.00"},
        {"financeiro", "CodPedido", "INT DEFAULT NULL"},
        {"financeiro", "dataMovimentacao", "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP()"}
    };

    public static Connection conectar() throws SQLException {
        if (!bancoInicializado) {
            inicializarBanco();
        }
        return DriverManager.getConnection(urlBanco(portaAtiva), USUARIO, senhaAtiva);
    }

    private static synchronized void inicializarBanco() throws SQLException {
        if (bancoInicializado) {
            return;
        }
        SQLException ultimoErro = null;
        SQLException erroAcessoNegado = null;
        Connection conexaoServidor = null;
        int portaEncontrada = 3306;
        String senhaEncontrada = SENHA;

        busca:
        for (int porta : PORTAS_TENTATIVA) {
            for (String tentativa : SENHAS_TENTATIVA) {
                try {
                    conexaoServidor = DriverManager.getConnection(urlServidor(porta), USUARIO, tentativa);
                    portaEncontrada = porta;
                    senhaEncontrada = tentativa;
                    break busca;
                } catch (SQLException erro) {
                    ultimoErro = erro;
                    if (erro.getErrorCode() == 1045) {
                        erroAcessoNegado = erro;
                    } else {
                        break;
                    }
                }
            }
        }

        if (conexaoServidor == null) {
            throw erroAcessoNegado != null ? erroAcessoNegado : ultimoErro;
        }

        try (Connection conn = conexaoServidor;
             Statement stmt = conn.createStatement()) {
            try {
                criarEstrutura(stmt);
            } catch (SQLException erro) {
                if (!tablespaceOrfao(erro)) {
                    throw erro;
                }
                // Arquivos .ibd orfaos (pasta de dados copiada de outro PC): recria o banco do zero.
                stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
                stmt.executeUpdate("DROP DATABASE IF EXISTS papelogistic");
                stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=1");
                criarEstrutura(stmt);
            }
            portaAtiva = portaEncontrada;
            senhaAtiva = senhaEncontrada;
            bancoInicializado = true;
        }
    }

    private static void criarEstrutura(Statement stmt) throws SQLException {
        for (String sql : SQL_ESTRUTURA) {
            stmt.executeUpdate(sql);
        }
        atualizarEstrutura(stmt);
    }

    private static boolean tablespaceOrfao(SQLException erro) {
        for (Throwable t = erro; t != null; t = t.getCause()) {
            if (t instanceof SQLException && ((SQLException) t).getErrorCode() == 1813) {
                return true;
            }
            String m = t.getMessage();
            if (m != null && m.contains("DISCARD the tablespace")) {
                return true;
            }
        }
        return false;
    }

    private static void atualizarEstrutura(Statement stmt) throws SQLException {
        Map<String, Set<String>> colunasExistentes = new HashMap<>();
        try (ResultSet rs = stmt.executeQuery(
                "SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'papelogistic'")) {
            while (rs.next()) {
                String tabela = rs.getString("TABLE_NAME").toLowerCase(Locale.ROOT);
                String coluna = rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT);
                colunasExistentes.computeIfAbsent(tabela, k -> new HashSet<>()).add(coluna);
            }
        }

        Map<String, Set<String>> colunasEsperadas = new HashMap<>();
        boolean adicionouNomeProduto = false;
        for (String[] definicao : COLUNAS_OBRIGATORIAS) {
            String tabela = definicao[0].toLowerCase(Locale.ROOT);
            String coluna = definicao[1];
            String colunaNormalizada = coluna.toLowerCase(Locale.ROOT);
            colunasEsperadas.computeIfAbsent(tabela, k -> new HashSet<>()).add(colunaNormalizada);

            Set<String> existentes = colunasExistentes.get(tabela);
            if (existentes != null && !existentes.contains(colunaNormalizada)) {
                stmt.executeUpdate("ALTER TABLE " + definicao[0] + " ADD COLUMN " + coluna + " " + definicao[2]);
                existentes.add(colunaNormalizada);
                if ("produto".equals(tabela) && "nomeproduto".equals(colunaNormalizada)) {
                    adicionouNomeProduto = true;
                }
            }
        }

        Set<String> colsProduto = colunasExistentes.get("produto");
        if (adicionouNomeProduto) {
            if (colsProduto != null && colsProduto.contains("nome")) {
                stmt.executeUpdate("UPDATE produto SET nomeProduto = nome WHERE nomeProduto = '' AND nome IS NOT NULL AND nome <> ''");
            }
            stmt.executeUpdate("UPDATE produto SET nomeProduto = tipo WHERE nomeProduto = '' AND tipo <> ''");
        }
        if (colsProduto != null && colsProduto.contains("quantidade")) {
            stmt.executeUpdate("INSERT IGNORE INTO estoque (CodProduto, quantidade) SELECT CodProduto, COALESCE(quantidade, 0) FROM produto");
        }

        List<String> alteracoesLegadas = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery(
                "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, EXTRA, COLUMN_KEY "
                + "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'papelogistic'")) {
            while (rs.next()) {
                String tabela = rs.getString("TABLE_NAME");
                String tabelaNorm = tabela.toLowerCase(Locale.ROOT);
                String coluna = rs.getString("COLUMN_NAME");
                String colunaNorm = coluna.toLowerCase(Locale.ROOT);
                Set<String> esperadas = colunasEsperadas.get(tabelaNorm);
                if (esperadas == null || esperadas.contains(colunaNorm)) {
                    continue;
                }
                String isNullable = rs.getString("IS_NULLABLE");
                String padrao = rs.getString("COLUMN_DEFAULT");
                String extra = rs.getString("EXTRA");
                String chave = rs.getString("COLUMN_KEY");
                boolean autoInc = extra != null && extra.toLowerCase(Locale.ROOT).contains("auto_increment");
                boolean primaria = chave != null && "PRI".equalsIgnoreCase(chave);
                if ("NO".equalsIgnoreCase(isNullable) && padrao == null && !autoInc && !primaria) {
                    String tipoColuna = rs.getString("COLUMN_TYPE");
                    alteracoesLegadas.add("ALTER TABLE `" + tabela + "` MODIFY COLUMN `" + coluna + "` " + tipoColuna + " NULL DEFAULT NULL");
                }
            }
        }
        for (String sql : alteracoesLegadas) {
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException ignorado) {
                // Ignora coluna legada com restrição específica.
            }
        }

        String[][] ajustesColunas = {
            {"cliente", "emailCliente", "ALTER TABLE cliente MODIFY COLUMN emailCliente VARCHAR(150) NULL DEFAULT NULL"},
            {"cliente", "telefoneCliente", "ALTER TABLE cliente MODIFY COLUMN telefoneCliente VARCHAR(20) NULL DEFAULT NULL"},
            {"cliente", "cep", "ALTER TABLE cliente MODIFY COLUMN cep CHAR(8) NULL DEFAULT NULL"},
            {"usuario", "telefoneUsuario", "ALTER TABLE usuario MODIFY COLUMN telefoneUsuario VARCHAR(20) NULL DEFAULT NULL"},
            {"pedido", "dataPedido", "ALTER TABLE pedido MODIFY COLUMN dataPedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP()"},
            {"financeiro", "CodPedido", "ALTER TABLE financeiro MODIFY COLUMN CodPedido INT NULL DEFAULT NULL"},
            {"financeiro", "dataMovimentacao", "ALTER TABLE financeiro MODIFY COLUMN dataMovimentacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP()"}
        };
        stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
        try {
            for (String[] ajuste : ajustesColunas) {
                Set<String> cols = colunasExistentes.get(ajuste[0].toLowerCase(Locale.ROOT));
                if (cols != null && cols.contains(ajuste[1].toLowerCase(Locale.ROOT))) {
                    try {
                        stmt.executeUpdate(ajuste[2]);
                    } catch (SQLException erro) {
                        if ("financeiro".equalsIgnoreCase(ajuste[0]) && "CodPedido".equalsIgnoreCase(ajuste[1])) {
                            permitirCodPedidoNuloNoFinanceiro(stmt);
                        }
                    }
                }
            }
        } finally {
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        }
        try {
            stmt.executeUpdate("INSERT IGNORE INTO usuario (login_simples, senha, nome, telefoneUsuario, emailUsuario) "
                + "VALUES ('biel', '1234', 'Administrador', NULL, 'biel@papelogistic.com'), "
                + "('admin', 'admin', 'Administrador', NULL, 'admin@papelogistic.com')");
        } catch (SQLException ignorado) {
            // Ignora caso os usuários padrão já existam.
        }
    }

    private static void permitirCodPedidoNuloNoFinanceiro(Statement stmt) {
        List<String> fks = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery(
                "SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE "
                + "WHERE TABLE_SCHEMA = 'papelogistic' AND TABLE_NAME = 'financeiro' "
                + "AND COLUMN_NAME = 'CodPedido' AND REFERENCED_TABLE_NAME IS NOT NULL")) {
            while (rs.next()) {
                fks.add(rs.getString("CONSTRAINT_NAME"));
            }
        } catch (SQLException ignorado) {
            return;
        }
        try {
            for (String fk : fks) {
                stmt.executeUpdate("ALTER TABLE financeiro DROP FOREIGN KEY `" + fk + "`");
            }
            stmt.executeUpdate("ALTER TABLE financeiro MODIFY COLUMN CodPedido INT NULL DEFAULT NULL");
            stmt.executeUpdate("ALTER TABLE financeiro ADD CONSTRAINT FK_Financeiro_Pedido "
                + "FOREIGN KEY (CodPedido) REFERENCES pedido (CodPedido) ON DELETE SET NULL ON UPDATE CASCADE");
        } catch (SQLException ignorado) {
            // Mantém execução caso o banco não permita recriar a constraint.
        }
    }

    public static int proximoCodigo(Connection conn, String tabela, String coluna) throws SQLException {
        String sql = "SELECT COALESCE(MAX(" + coluna + "), 0) + 1 FROM " + tabela;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 1;
        }
    }

    public static void main(String[] args) {
        try (Connection conexao = conectar()) {
            System.out.println("Conexão realizada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
    }
}
