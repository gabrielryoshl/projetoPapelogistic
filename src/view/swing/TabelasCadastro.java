package view.swing;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import model.dao.ClienteDAO;
import model.dao.EstoqueDAO;
import model.dao.FinanceiroDAO;
import model.dao.ItemPedidoDAO;
import model.dao.PedidoDAO;
import model.dao.ProdutoDAO;
import model.entidades.Cliente;
import model.entidades.Estoque;
import model.entidades.Financeiro;
import model.entidades.ItemPedido;
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

    static void configurarBusca(JTable tabela, JButton botaoBuscar, JTextComponent... camposBusca) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tabela.getModel());
        tabela.setRowSorter(sorter);
        Runnable filtrar = () -> {
            List<RowFilter<Object, Object>> filtros = new ArrayList<>();
            for (JTextComponent campo : camposBusca) {
                String termo = campo.getText().trim();
                if (!termo.isEmpty()) {
                    filtros.add(RowFilter.regexFilter("(?i)" + Pattern.quote(termo)));
                }
            }
            sorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
        };
        botaoBuscar.addActionListener(evt -> filtrar.run());
        for (JTextComponent campo : camposBusca) {
            campo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "buscarTabela");
            campo.getActionMap().put("buscarTabela", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filtrar.run();
                }
            });
        }
    }

    static List<Object[]> produtos() {
        Map<Integer, Integer> quantidades = new HashMap<>();
        for (Estoque estoque : new EstoqueDAO().listar()) {
            quantidades.put(estoque.getCodProduto(), estoque.getQuantidade());
        }
        List<Object[]> linhas = new ArrayList<>();
        for (Produto produto : new ProdutoDAO().listar()) {
            int codigo = produto.getCodProduto();
            int quantidade = quantidades.getOrDefault(codigo, 0);
            linhas.add(new Object[]{codigo, produto.getNomeProduto(), produto.getTipo(),
                quantidade, produto.getValor()});
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
        Map<Integer, List<ItemPedido>> itensPorPedido = new HashMap<>();
        for (ItemPedido item : new ItemPedidoDAO().listar()) {
            itensPorPedido.computeIfAbsent(item.getCodPedido(), k -> new ArrayList<>()).add(item);
        }
        List<Object[]> linhas = new ArrayList<>();
        for (Pedido pedido : new PedidoDAO().listar()) {
            List<ItemPedido> itens = itensPorPedido.get(pedido.getCodPedido());
            if (itens == null || itens.isEmpty()) {
                linhas.add(new Object[]{pedido.getCodPedido(), pedido.getCodCliente(), null, null, null});
            } else {
                for (ItemPedido item : itens) {
                    linhas.add(new Object[]{pedido.getCodPedido(), pedido.getCodCliente(),
                        item.getCodProduto(), item.getQuantidade(), item.getValorUnitario()});
                }
            }
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
