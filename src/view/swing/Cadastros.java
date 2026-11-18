package view.swing;

import java.awt.Component;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import model.dao.ClienteDAO;
import model.dao.FinanceiroDAO;
import model.dao.ItemPedidoDAO;
import model.dao.PedidoDAO;
import model.dao.ProdutoDAO;
import model.entidades.Cliente;
import model.entidades.Financeiro;
import model.entidades.ItemPedido;
import model.entidades.Pedido;
import model.entidades.Produto;

final class Cadastros {
    private Cadastros() {
    }

    static void produto(Component tela, Runnable atualizarTabela, Object[] linha) {
        boolean editando = linha != null;
        Object[] valores = editando ? new Object[]{linha[1], linha[2], linha[4], linha[3]} : null;
        abrir(tela, editando ? "Editar Produto" : "Novo Produto", atualizarTabela, valores, campos -> {
            Produto produto = new Produto();
            produto.setNomeProduto(obrigatorio(campos[0], "Nome"));
            produto.setTipo(obrigatorio(campos[1], "Categoria"));
            double preco = decimal(campos[2], "Preço");
            if (preco < 0) {
                throw new IllegalArgumentException("O preço não pode ser negativo.");
            }
            produto.setValor(preco);
            int quantidade = inteiro(campos[3], "Quantidade", 0);
            ProdutoDAO dao = new ProdutoDAO();
            if (editando) {
                produto.setCodProduto((Integer) linha[0]);
                dao.atualizarComEstoque(produto, quantidade);
            } else {
                dao.inserirComEstoque(produto, quantidade);
            }
        }, "Nome", "Categoria", "Preço", editando ? "Quantidade" : "Quantidade inicial");
    }

    static void cliente(Component tela, Runnable atualizarTabela, Object[] linha) {
        boolean editando = linha != null;
        Object[] valores = editando ? new Object[]{linha[1], linha[2], linha[4], linha[3], linha[5]} : null;
        abrir(tela, editando ? "Editar Cliente" : "Novo Cliente", atualizarTabela, valores, campos -> {
            Cliente cliente = new Cliente();
            cliente.setNomeCliente(obrigatorio(campos[0], "Nome"));
            cliente.setIdPessoa(obrigatorio(campos[1], "ID da pessoa"));
            cliente.setEmailCliente(opcional(campos[2]));
            cliente.setTelefoneCliente(opcional(campos[3]));
            String cep = opcional(campos[4]);
            if (cep != null) {
                cep = cep.replace("-", "");
                if (!cep.matches("[0-9]{8}")) {
                    throw new IllegalArgumentException("O CEP deve ter 8 dígitos.");
                }
            }
            cliente.setCep(cep);
            ClienteDAO dao = new ClienteDAO();
            if (editando) {
                cliente.setCliente((Integer) linha[0]);
                dao.atualizar(cliente);
            } else {
                dao.inserir(cliente);
            }
        }, "Nome", "ID da pessoa", "E-mail (opcional)", "Telefone (opcional)", "CEP (opcional)");
    }

    static void pedido(Component tela, Runnable atualizarTabela, Object[] linha) {
        boolean editando = linha != null;
        Object[] valores = editando
            ? (linha.length >= 5 ? new Object[]{linha[1], linha[2], linha[3], linha[4]} : new Object[]{linha[1], null, null, null})
            : null;
        abrir(tela, editando ? "Editar Pedido" : "Novo Pedido", atualizarTabela, valores, campos -> {
            Pedido pedido = new Pedido();
            pedido.setCodCliente(inteiro(campos[0], "Código do cliente", 1));
            boolean temProduto = opcional(campos[1]) != null;
            boolean temQuantidade = opcional(campos[2]) != null;
            boolean temValor = opcional(campos[3]) != null;
            ItemPedido item = null;
            if (temProduto || temQuantidade || temValor) {
                item = new ItemPedido();
                item.setCodProduto(inteiro(campos[1], "Código do produto", 1));
                item.setQuantidade(inteiro(campos[2], "Quantidade", 1));
                double valorUnitario = decimal(campos[3], "Valor unitário");
                if (valorUnitario < 0) {
                    throw new IllegalArgumentException("O valor unitário não pode ser negativo.");
                }
                item.setValorUnitario(valorUnitario);
            }
            PedidoDAO dao = new PedidoDAO();
            ItemPedidoDAO itemDao = new ItemPedidoDAO();
            if (editando) {
                int codPedido = (Integer) linha[0];
                pedido.setCodPedido(codPedido);
                dao.atualizar(pedido);
                Integer produtoAnterior = linha.length >= 3 ? (Integer) linha[2] : null;
                if (item == null) {
                    if (produtoAnterior != null) {
                        itemDao.deletar(codPedido, produtoAnterior);
                    }
                } else {
                    item.setCodPedido(codPedido);
                    if (produtoAnterior == null) {
                        itemDao.inserir(item);
                    } else if (!produtoAnterior.equals(item.getCodProduto())) {
                        itemDao.deletar(codPedido, produtoAnterior);
                        itemDao.inserir(item);
                    } else {
                        itemDao.atualizar(item);
                    }
                }
            } else {
                dao.inserir(pedido);
                if (item != null) {
                    item.setCodPedido(pedido.getCodPedido());
                    itemDao.inserir(item);
                }
            }
        }, "Código do cliente", "Código do produto (opcional)", "Quantidade (opcional)", "Valor unitário (opcional)");
    }

    static void financeiro(Component tela, Runnable atualizarTabela, Object[] linha) {
        boolean editando = linha != null;
        Object[] valores = editando ? new Object[]{linha[2], linha[3], linha[4], linha[1]} : null;
        abrir(tela, editando ? "Editar Lançamento" : "Novo Lançamento", atualizarTabela, valores, campos -> {
            Financeiro registro = new Financeiro();
            double entrada = opcional(campos[0]) == null ? 0.0 : decimal(campos[0], "Entrada");
            double saida = opcional(campos[1]) == null ? 0.0 : decimal(campos[1], "Saída");
            if (entrada < 0 || saida < 0) {
                throw new IllegalArgumentException("Entrada e saída não podem ser negativas.");
            }
            if (!editando && entrada == 0 && saida == 0) {
                throw new IllegalArgumentException("Informe um valor de entrada ou de saída maior que zero.");
            }
            registro.setValorEntrada(entrada);
            registro.setValorSaida(saida);
            registro.setCaixa(decimal(campos[2], "Caixa"));
            if (opcional(campos[3]) != null) {
                registro.setCodPedido(inteiro(campos[3], "Código do pedido", 1));
            }
            FinanceiroDAO dao = new FinanceiroDAO();
            if (editando) {
                registro.setCodFinanceiro((Integer) linha[0]);
                dao.atualizar(registro);
            } else {
                dao.inserir(registro);
            }
        }, "Entrada", "Saída", "Caixa", "Código do pedido (opcional)");
    }

    static void despesa(Component tela, Runnable atualizarTabela) {
        financeiro(tela, atualizarTabela, null);
    }

    static void editarFinanceiro(Component tela, JTable tabela, Runnable atualizarTabela) {
        editar(tela, tabela, linha -> financeiro(tela, atualizarTabela, linha));
    }

    static void editar(Component tela, JTable tabela, Consumer<Object[]> abrirEdicao) {
        int selecionada = tabela.getSelectedRow();
        if (selecionada < 0) {
            JOptionPane.showMessageDialog(tela, "Selecione um registro na tabela.");
            return;
        }
        int indice = tabela.convertRowIndexToModel(selecionada);
        Object[] linha = new Object[tabela.getModel().getColumnCount()];
        for (int coluna = 0; coluna < linha.length; coluna++) {
            linha[coluna] = tabela.getModel().getValueAt(indice, coluna);
        }
        abrirEdicao.accept(linha);
    }

    static void excluir(Component tela, JTable tabela, Runnable atualizarTabela, String mensagem, IntConsumer apagar) {
        editar(tela, tabela, linha -> {
            int codigo = (Integer) linha[0];
            int resposta = JOptionPane.showConfirmDialog(tela, mensagem + "\nCódigo: " + codigo,
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                apagar.accept(codigo);
            } catch (IllegalStateException erro) {
                JOptionPane.showMessageDialog(tela, erro.getMessage(), "Exclusão", JOptionPane.ERROR_MESSAGE);
            }
            atualizarTabela.run();
        });
    }

    private static void abrir(Component tela, String titulo, Runnable atualizarTabela, Object[] valores,
            Consumer<JTextField[]> salvar, String... rotulos) {
        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField[] campos = new JTextField[rotulos.length];
        for (int i = 0; i < rotulos.length; i++) {
            campos[i] = new JTextField(18);
            if (valores != null && valores[i] != null) {
                Object valor = valores[i];
                campos[i].setText(valor instanceof Double decimal
                    ? BigDecimal.valueOf(decimal).stripTrailingZeros().toPlainString() : valor.toString());
            }
            JLabel rotulo = new JLabel(rotulos[i]);
            rotulo.setLabelFor(campos[i]);
            painel.add(rotulo);
            painel.add(campos[i]);
        }

        Object[] opcoes = {valores == null ? "Cadastrar" : "Salvar", "Cancelar"};
        while (JOptionPane.showOptionDialog(tela, painel, titulo, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]) == JOptionPane.OK_OPTION) {
            try {
                salvar.accept(campos);
            } catch (IllegalArgumentException | IllegalStateException erro) {
                JOptionPane.showMessageDialog(tela, erro.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
                continue;
            }
            atualizarTabela.run();
            JOptionPane.showMessageDialog(tela, valores == null ? "Cadastro concluído." : "Alterações salvas.");
            return;
        }
    }

    private static String opcional(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    private static String obrigatorio(JTextField campo, String rotulo) {
        String texto = opcional(campo);
        if (texto == null) {
            throw new IllegalArgumentException("Preencha o campo " + rotulo + ".");
        }
        return texto;
    }

    private static int inteiro(JTextField campo, String rotulo, int minimo) {
        String texto = obrigatorio(campo, rotulo);
        try {
            int valor = Integer.parseInt(texto);
            if (valor >= minimo) {
                return valor;
            }
        } catch (NumberFormatException erro) {
            // A mensagem abaixo também cobre números fora da faixa de int.
        }
        throw new IllegalArgumentException(rotulo + " deve ser um número inteiro a partir de " + minimo + ".");
    }

    private static double decimal(JTextField campo, String rotulo) {
        String texto = obrigatorio(campo, rotulo).replace(',', '.');
        if (!texto.matches("-?[0-9]+(\\.[0-9]{1,2})?")) {
            throw new IllegalArgumentException(rotulo + " deve ter até duas casas decimais.");
        }
        BigDecimal valor = new BigDecimal(texto);
        if (valor.abs().compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException(rotulo + " está fora do limite aceito pelo banco.");
        }
        return valor.doubleValue();
    }
}
