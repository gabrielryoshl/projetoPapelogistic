package view.terminal;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class TerminalView {
    private final Scanner entrada;

    public TerminalView(Scanner entrada) { this.entrada = entrada; }

    public void mostrar(String texto) { System.out.println(texto); }

    public String texto(String rotulo) {
        while (true) {
            System.out.print(rotulo + ": ");
            if (!entrada.hasNextLine()) throw new NoSuchElementException();
            String valor = entrada.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            mostrar("Preencha o campo.");
        }
    }

    public int inteiro(String rotulo) {
        while (true) {
            try { return Integer.parseInt(texto(rotulo)); }
            catch (NumberFormatException e) { mostrar("Digite um numero inteiro valido."); }
        }
    }

    public double decimal(String rotulo) {
        while (true) {
            try {
                double valor = Double.parseDouble(texto(rotulo).replace(',', '.'));
                if (Double.isFinite(valor)) return valor;
            } catch (NumberFormatException e) { /* Solicita novamente. */ }
            mostrar("Digite um numero decimal valido.");
        }
    }

    public boolean confirmarExclusao() {
        return texto("Confirma a exclusao? Digite SIM para confirmar").equalsIgnoreCase("SIM");
    }
}
