
package model.entidades;

public class Financeiro {

    private int codFinanceiro;
    private double caixa;
    private double valorEntrada;
    private double valorSaida;
    private int codPedido;
    
    public Financeiro () {
    }

    public Financeiro(int codFinanceiro, double caixa, double valorEntrada, double valorSaida, int codPedido) {
        this.codFinanceiro = codFinanceiro;
        this.caixa = caixa;
        this.valorEntrada = valorEntrada;
        this.valorSaida = valorSaida;
        this.codPedido = codPedido;
    }

    
    
    public void setCodFinanceiro(int codFinanceiro) {
        this.codFinanceiro = codFinanceiro;
    }

    public int getCodFinanceiro() {
        return codFinanceiro;
    }

    public void setCaixa(double caixa) {
        this.caixa = caixa;
    }

    public double getCaixa() {
        return caixa;
    }

    public void setValorEntrada(double valorEntrada) {
        this.valorEntrada = valorEntrada;
    }

    public double getValorEntrada() {
        return valorEntrada;
    }

    public void setValorSaida(double valorSaida) {
        this.valorSaida = valorSaida;
    }

    public double getValorSaida() {
        return valorSaida;
    }

    public void setCodPedido(int codPedido) {
        this.codPedido = codPedido;
    }

    public int getCodPedido() {
        return codPedido;
    }
}