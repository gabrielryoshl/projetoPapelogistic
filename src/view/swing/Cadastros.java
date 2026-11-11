package papelogistic;

import java.awt.Component;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import model.dao.ClienteDAO;
import model.dao.FinanceiroDAO;
import model.dao.PedidoDAO;
import model.dao.ProdutoDAO;
import model.entidades.Cliente;
import model.entidades.Financeiro;
import model.entidades.Pedido;
import model.entidades.Produto;

final class Cadastros {
    private Cadastros() {
    }

    static void produto(Component tela, Runnable atualizarTabela) {
        abrir(tela, "Novo Produto", atualizarTabela, campos -> {
            Produto produto = new Produto();
            produto.setNomeProduto(obrigatorio(campos[0], "Nome"));
            produto.setTipo(obrigatorio(campos[1], "Categoria"));
            double preco = decimal(campos[2], "Preço");
            if (preco < 0) {
                throw new IllegalArgumentException("O preço não pode ser negativo.");
            }
            produto.setValor(preco);
            int quantidade = inteiro(campos[3], "Quantidade inicial", 0);
            new ProdutoDAO().inserirComEstoque(produto, quantidade);
        }, "Nome", "Categoria", "Preço", "Quantidade inicial");
    }

    static void cliente(Component tela, Runnable atualizarTabela) {
        abrir(tela, "Novo Cliente", atualizarTabela, campos -> {
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
            new ClienteDAO().inserir(cliente);
        }, "Nome", "ID da pessoa", "E-mail (opcional)", "Telefone (opcional)", "CEP (opcional)");
    }

    static void pedido(Component tela, Runnable atualizarTabela) {
        abrir(tela, "Novo Pedido", atualizarTabela, campos -> {
            Pedido pedido = new Pedido();
            pedido.setCodCliente(inteiro(campos[0], "Código do cliente", 1));
            new PedidoDAO().inserir(pedido);
        }, "Código do cliente");
    }

    static void despesa(Component tela, Runnable atualizarTabela) {
        abrir(tela, "Nova Despesa", atualizarTabela, campos -> {
            double valor = decimal(campos[0], "Valor da despesa");
            if (valor <= 0) {
                throw new IllegalArgumentException("O valor da despesa deve ser maior que zero.");
            }
            Financeiro despesa = new Financeiro();
            despesa.setValorSaida(valor);
            despesa.setCaixa(decimal(campos[1], "Caixa após a despesa"));
            if (opcional(campos[2]) != null) {
                despesa.setCodPedido(inteiro(campos[2], "Código do pedido", 1));
            }
            new FinanceiroDAO().inserir(despesa);
        }, "Valor da despesa", "Caixa após a despesa", "Código do pedido (opcional)");
    }

    private static void abrir(Component tela, String titulo, Runnable atualizarTabela,
            Consumer<JTextField[]> salvar, String... rotulos) {
        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField[] campos = new JTextField[rotulos.length];
        for (int i = 0; i < rotulos.length; i++) {
            campos[i] = new JTextField(18);
            JLabel rotulo = new JLabel(rotulos[i]);
            rotulo.setLabelFor(campos[i]);
            painel.add(rotulo);
            painel.add(campos[i]);
        }

        Object[] opcoes = {"Cadastrar", "Cancelar"};
        while (JOptionPane.showOptionDialog(tela, painel, titulo, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]) == JOptionPane.OK_OPTION) {
            try {
                salvar.accept(campos);
            } catch (IllegalArgumentException | IllegalStateException erro) {
                JOptionPane.showMessageDialog(tela, erro.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
                continue;
            }
            atualizarTabela.run();
            JOptionPane.showMessageDialog(tela, "Cadastro concluído.");
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
