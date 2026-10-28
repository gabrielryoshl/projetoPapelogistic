package view.terminal;

import controller.terminal.TerminalController;
import java.util.Scanner;

public class TerminalPrincipal {
    public static void main(String[] args) {
        new TerminalController(new TerminalView(new Scanner(System.in))).executar();
    }
}
