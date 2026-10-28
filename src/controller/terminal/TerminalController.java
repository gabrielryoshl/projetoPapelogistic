package controller.terminal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import model.conexao.Conexao;
import model.dao.ClienteDAO;
import model.dao.EstoqueDAO;
import model.dao.FinanceiroDAO;
import model.dao.ItemPedidoDAO;
import model.dao.PedidoDAO;
import model.dao.ProdutoDAO;
import model.dao.UsuarioDAO;
import model.entidades.Cliente;
import model.entidades.Estoque;
import model.entidades.Financeiro;
import model.entidades.ItemPedido;
import model.entidades.Pedido;
import model.entidades.Produto;
import model.entidades.Usuario;
import view.terminal.TerminalView;

public class TerminalController {
    private final TerminalView tela;
    public TerminalController(TerminalView tela) { this.tela = tela; }

    public void executar() {
        tela.mostrar("=== PAPELOGISTIC | TESTES DE CONEXAO E CRUD ===");
        try {
            while (true) {
                tela.mostrar("\n1 - Testar conexao\n2 - Cliente\n3 - Produto\n4 - Usuario\n5 - Estoque\n6 - Pedido\n7 - ItemPedido\n8 - Financeiro\n0 - Sair");
                int opcao = tela.inteiro("Opcao");
                if (opcao == 0) { tela.mostrar("Terminal encerrado."); return; }
                try {
                    switch (opcao) {
                        case 1 -> testarConexao();
                        case 2 -> menuCliente();
                        case 3 -> menuProduto();
                        case 4 -> menuUsuario();
                        case 5 -> menuEstoque();
                        case 6 -> menuPedido();
                        case 7 -> menuItemPedido();
                        case 8 -> menuFinanceiro();
                        default -> tela.mostrar("Opcao invalida.");
                    }
                } catch (IllegalStateException e) {
                    tela.mostrar("Operacao falhou: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) { tela.mostrar("\nEntrada encerrada."); }
    }

    private void testarConexao() {
        try (Connection conexao = Conexao.conectar()) {
            if (conexao.isValid(5)) {
                tela.mostrar("Conexao OK: " + conexao.getMetaData().getDatabaseProductName());
            } else { tela.mostrar("Conexao invalida."); }
        } catch (SQLException e) { tela.mostrar("Falha na conexao: " + e.getMessage()); }
    }

    private int operacao(String entidade) {
        tela.mostrar("\n=== " + entidade + " ===\n1 - Cadastrar\n2 - Listar\n3 - Atualizar\n4 - Excluir\n0 - Voltar");
        return tela.inteiro("Operacao");
    }

    private void menuCliente() {
        ClienteDAO dao = new ClienteDAO();
        while (true) {
            int op = operacao("Cliente");
            if (op == 0) return;
            switch (op) {
                case 1, 3 -> {
                    Cliente registro = new Cliente();
                    registro.setCliente(tela.inteiro("cliente"));
                    registro.setNomeCliente(tela.texto("nomeCliente"));
                    registro.setCidade(tela.texto("cidade"));
                    if (op == 1) dao.inserir(registro); else dao.atualizar(registro);
                    tela.mostrar("Operacao concluida.");
                }
                case 2 -> {
                    var registros = dao.listar();
                    if (registros.isEmpty()) tela.mostrar("Nenhum registro cadastrado.");
                    for (Cliente r : registros) {
                        tela.mostrar("cliente=" + r.getCliente() + " | " + "nomeCliente=" + r.getNomeCliente() + " | " + "cidade=" + r.getCidade());
                    }
                }
                case 4 -> {
                    int cliente = tela.inteiro("cliente");
                    if (tela.confirmarExclusao()) {
                        dao.deletar(cliente);
                        tela.mostrar("Exclusao concluida.");
                    } else { tela.mostrar("Exclusao cancelada."); }
                }
                default -> tela.mostrar("Opcao invalida.");
            }
        }
    }

    private void menuProduto() {
        ProdutoDAO dao = new ProdutoDAO();
        while (true) {
            int op = operacao("Produto");
            if (op == 0) return;
            switch (op) {
                case 1, 3 -> {
                    Produto registro = new Produto();
                    registro.setCodProduto(tela.inteiro("codProduto"));
                    registro.setTipo(tela.texto("tipo"));
                    registro.setValor(tela.decimal("valor"));
                    registro.setQuantidade(tela.inteiro("quantidade"));
                    if (op == 1) dao.inserir(registro); else dao.atualizar(registro);
                    tela.mostrar("Operacao concluida.");
                }
                case 2 -> {
                    var registros = dao.listar();
                    if (registros.isEmpty()) tela.mostrar("Nenhum registro cadastrado.");
                    for (Produto r : registros) {
                        tela.mostrar("codProduto=" + r.getCodProduto() + " | " + "tipo=" + r.getTipo() + " | " + "valor=" + r.getValor() + " | " + "quantidade=" + r.getQuantidade());
                    }
                }
                case 4 -> {
                    int codProduto = tela.inteiro("codProduto");
                    if (tela.confirmarExclusao()) {
                        dao.deletar(codProduto);
                        tela.mostrar("Exclusao concluida.");
                    } else { tela.mostrar("Exclusao cancelada."); }
                }
                default -> tela.mostrar("Opcao invalida.");
            }
        }
    }

    private void menuUsuario() {
        UsuarioDAO dao = new UsuarioDAO();
        while (true) {
            int op = operacao("Usuario");
            if (op == 0) return;
            switch (op) {
                case 1, 3 -> {
                    Usuario registro = new Usuario();
                    registro.setLoginSimples(tela.texto("loginSimples"));
                    registro.setSenha(tela.texto("senha"));
                    registro.setNome(tela.texto("nome"));
                    registro.setTelefoneUsuario(tela.texto("telefoneUsuario"));
                    registro.setEmailUsuario(tela.texto("emailUsuario"));
                    if (op == 1) dao.inserir(registro); else dao.atualizar(registro);
                    tela.mostrar("Operacao concluida.");
                }
                case 2 -> {
                    var registros = dao.listar();
                    if (registros.isEmpty()) tela.mostrar("Nenhum registro cadastrado.");
                    for (Usuario r : registros) {
                        tela.mostrar("loginSimples=" + r.getLoginSimples() + " | " + "nome=" + r.getNome() + " | " + "telefoneUsuario=" + r.getTelefoneUsuario() + " | " + "emailUsuario=" + r.getEmailUsuario());
                    }
                }
                case 4 -> {
                    String loginSimples = tela.texto("loginSimples");
                    if (tela.confirmarExclusao()) {
                        dao.deletar(loginSimples);
                        tela.mostrar("Exclusao concluida.");
                    } else { tela.mostrar("Exclusao cancelada."); }
                }
                default -> tela.mostrar("Opcao invalida.");
            }
        }
    }

    private void menuEstoque() {
        EstoqueDAO dao = new EstoqueDAO();
        while (true) {
            int op = operacao("Estoque");
            if (op == 0) return;
            switch (op) {
                case 1, 3 -> {
                    Estoque registro = new Estoque();
                    registro.setCodProduto(tela.inteiro("codProduto"));
                    registro.setValorProduto(tela.inteiro("valorProduto"));
                    registro.setTipo(tela.texto("tipo"));
                    if (op == 1) dao.inserir(registro); else dao.atualizar(registro);
                    tela.mostrar("Operacao concluida.");
                }
                case 2 -> {
                    var registros = dao.listar();
                    if (registros.isEmpty()) tela.mostrar("Nenhum registro cadastrado.");
                    for (Estoque r : registros) {
                        tela.mostrar("codProduto=" + r.getCodProduto() + " | " + "valorProduto=" + r.getValorProduto() + " | " + "tipo=" + r.getTipo());
                    }
                }
                case 4 -> {
                    int codProduto = tela.inteiro("codProduto");
                    if (tela.confirmarExclusao()) {
                        dao.deletar(codProduto);
                        tela.mostrar("Exclusao concluida.");
                    } else { tela.mostrar("Exclusao cancelada."); }
                }
                default -> tela.mostrar("Opcao invalida.");
            }
        }
    }

    private void menuPedido() {
        PedidoDAO dao = new PedidoDAO();
        while (true) {
            int op = operacao("Pedido");
            if (op == 0) return;
            switch (op) {
                case 1, 3 -> {
                    Pedido registro = new Pedido();
                    registro.setCodPedido(tela.inteiro("codPedido"));
                    registro.setCodCliente(tela.inteiro("codCliente"));
                    if (op == 1) dao.inserir(registro); else dao.atualizar(registro);
                    tela.mostrar("Operacao concluida.");
                }
                case 2 -> {
                    var registros = dao.listar();
                    if (registros.isEmpty()) tela.mostrar("Nenhum registro cadastrado.");
                    for (Pedido r : registros) {
                        tela.mostrar("codPedido=" + r.getCodPedido() + " | " + "codCliente=" + r.getCodCliente());
                    }
                }
                case 4 -> {
                    int codPedido = tela.inteiro("codPedido");
                    if (tela.confirmarExclusao()) {
                        dao.deletar(codPedido);
                        tela.mostrar("Exclusao concluida.");
                    } else { tela.mostrar("Exclusao cancelada."); }
                }
                default -> tela.mostrar("Opcao invalida.");
            }
        }
    }

    private void menuItemPedido() {
        ItemPedidoDAO dao = new ItemPedidoDAO();
        while (true) {
            int op = operacao("ItemPedido");
            if (op == 0) return;
            switch (op) {
                case 1, 3 -> {
                    ItemPedido registro = new ItemPedido();
                    registro.setCodPedido(tela.inteiro("codPedido"));
                    registro.setCodProduto(tela.inteiro("codProduto"));
                    registro.setQuantidade(tela.inteiro("quantidade"));
                    registro.setValorUnitario(tela.decimal("valorUnitario"));
                    if (op == 1) dao.inserir(registro); else dao.atualizar(registro);
                    tela.mostrar("Operacao concluida.");
                }
                case 2 -> {
                    var registros = dao.listar();
                    if (registros.isEmpty()) tela.mostrar("Nenhum registro cadastrado.");
                    for (ItemPedido r : registros) {
                        tela.mostrar("codPedido=" + r.getCodPedido() + " | " + "codProduto=" + r.getCodProduto() + " | " + "quantidade=" + r.getQuantidade() + " | " + "valorUnitario=" + r.getValorUnitario());
                    }
                }
                case 4 -> {
                    int codPedido = tela.inteiro("codPedido");
                    int codProduto = tela.inteiro("codProduto");
                    if (tela.confirmarExclusao()) {
                        dao.deletar(codPedido, codProduto);
                        tela.mostrar("Exclusao concluida.");
                    } else { tela.mostrar("Exclusao cancelada."); }
                }
                default -> tela.mostrar("Opcao invalida.");
            }
        }
    }

    private void menuFinanceiro() {
        FinanceiroDAO dao = new FinanceiroDAO();
        while (true) {
            int op = operacao("Financeiro");
            if (op == 0) return;
            switch (op) {
                case 1, 3 -> {
                    Financeiro registro = new Financeiro();
                    registro.setCodFinanceiro(tela.inteiro("codFinanceiro"));
                    registro.setCaixa(tela.decimal("caixa"));
                    registro.setValorEntrada(tela.decimal("valorEntrada"));
                    registro.setValorSaida(tela.decimal("valorSaida"));
                    registro.setCodPedido(tela.inteiro("codPedido"));
                    if (op == 1) dao.inserir(registro); else dao.atualizar(registro);
                    tela.mostrar("Operacao concluida.");
                }
                case 2 -> {
                    var registros = dao.listar();
                    if (registros.isEmpty()) tela.mostrar("Nenhum registro cadastrado.");
                    for (Financeiro r : registros) {
                        tela.mostrar("codFinanceiro=" + r.getCodFinanceiro() + " | " + "caixa=" + r.getCaixa() + " | " + "valorEntrada=" + r.getValorEntrada() + " | " + "valorSaida=" + r.getValorSaida() + " | " + "codPedido=" + r.getCodPedido());
                    }
                }
                case 4 -> {
                    int codFinanceiro = tela.inteiro("codFinanceiro");
                    if (tela.confirmarExclusao()) {
                        dao.deletar(codFinanceiro);
                        tela.mostrar("Exclusao concluida.");
                    } else { tela.mostrar("Exclusao cancelada."); }
                }
                default -> tela.mostrar("Opcao invalida.");
            }
        }
    }
}

