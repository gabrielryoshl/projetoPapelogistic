package papelogistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.dao.ClienteDAO;
import model.dao.EstoqueDAO;
import model.dao.FinanceiroDAO;
import model.dao.PedidoDAO;
import model.dao.ProdutoDAO;
import model.entidades.Cliente;
import model.entidades.Estoque;
import model.entidades.Financeiro;
import model.entidades.Pedido;
import model.entidades.Produto;

final class TabelasCadastro {
    private TabelasCadastro() {
    }

    static Runnable preparar(JTable tabela, Supplier<List<Object[]>> consultar, String... colunas) {
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela.setModel(modelo);
        return () -> {
            try {
                List<Object[]> linhas = consultar.get();
                modelo.setRowCount(0);
                for (Object[] linha : linhas) {
                    modelo.addRow(linha);
                }
            } catch (IllegalStateException erro) {
                JOptionPane.showMessageDialog(tabela,
                    "Não foi possível atualizar a tabela.\n" + erro.getMessage(),
                    "Consulta ao banco", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    static List<Object[]> produtos() {
        Map<Integer, Integer> quantidades = new HashMap<>();
        for (Estoque estoque : new EstoqueDAO().listar()) {
            quantidades.put(estoque.getCodProduto(), estoque.getQuantidade());
        }
        List<Object[]> linhas = new ArrayList<>();
        for (Produto produto : new ProdutoDAO().listar()) {
            int codigo = produto.getCodProduto();
            linhas.add(new Object[]{codigo, produto.getNomeProduto(), produto.getTipo(),
                quantidades.getOrDefault(codigo, 0), produto.getValor()});
        }
        return linhas;
    }

    static List<Object[]> clientes() {
        List<Object[]> linhas = new ArrayList<>();
        for (Cliente cliente : new ClienteDAO().listar()) {
            linhas.add(new Object[]{cliente.getCliente(), cliente.getNomeCliente(), cliente.getIdPessoa(),
                cliente.getTelefoneCliente(), cliente.getEmailCliente(), cliente.getCep()});
        }
        return linhas;
    }

    static List<Object[]> pedidos() {
        List<Object[]> linhas = new ArrayList<>();
        for (Pedido pedido : new PedidoDAO().listar()) {
            linhas.add(new Object[]{pedido.getCodPedido(), pedido.getCodCliente()});
        }
        return linhas;
    }

    static List<Object[]> financeiro() {
        List<Object[]> linhas = new ArrayList<>();
        for (Financeiro registro : new FinanceiroDAO().listar()) {
            linhas.add(new Object[]{registro.getCodFinanceiro(),
                registro.getCodPedido() == 0 ? null : registro.getCodPedido(),
                registro.getValorEntrada(), registro.getValorSaida(), registro.getCaixa()});
        }
        return linhas;
    }
}
