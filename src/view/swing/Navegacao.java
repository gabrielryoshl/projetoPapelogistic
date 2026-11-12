package view.swing;

import javax.swing.JFrame;

final class Navegacao {
    private Navegacao() {
    }

    static void abrir(JFrame atual, JFrame destino) {
        destino.setLocationRelativeTo(atual);
        destino.setVisible(true);
        atual.dispose();
    }
}
