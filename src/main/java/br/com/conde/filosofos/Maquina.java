package br.com.conde.filosofos;

import java.util.ArrayList;
import java.util.List;

/**
 * A transicao: um passo de um filosofo por vez, em qualquer ordem.
 *
 * <p>Quem tenta pegar um garfo que esta na mao de outro simplesmente nao gera
 * transicao. Nao ha espera ocupada, nao ha fila e nao ha preferencia: o garfo
 * fica la, e quando ele voltar para a mesa qualquer um dos dois vizinhos pode
 * pegar. E dessa falta de fila que a inanicao nasce.
 */
public final class Maquina {

    private final Mesa mesa;

    public Maquina(Mesa mesa) {
        this.mesa = mesa;
    }

    public record Movimento(int filosofo, Passo passo, Estado destino) {
    }

    public List<Movimento> movimentos(Estado estado) {
        List<Movimento> saida = new ArrayList<>();
        for (int filosofo = 0; filosofo < mesa.filosofos(); filosofo++) {
            Passo passo = mesa.passoAtual(estado, filosofo);
            Estado destino = aplicar(estado, filosofo, passo);
            if (destino != null) {
                saida.add(new Movimento(filosofo, passo, destino));
            }
        }
        return saida;
    }

    private Estado aplicar(Estado estado, int filosofo, Passo passo) {
        return switch (passo.tipo()) {
            case PEGA -> estado.livre(passo.garfo())
                    ? avancar(estado.pegar(passo.garfo(), filosofo), filosofo) : null;
            case PEGA_DOIS -> estado.livre(passo.garfo()) && estado.livre(passo.outroGarfo())
                    ? avancar(estado.pegar(passo.garfo(), filosofo)
                    .pegar(passo.outroGarfo(), filosofo), filosofo) : null;
            case SENTA -> estado.sentados() < mesa.limiteDeLugares()
                    ? avancar(estado.comSentados(estado.sentados() + 1), filosofo) : null;
            case COME -> avancar(estado, filosofo);
            case SOLTA -> avancar(estado.soltar(passo.garfo()), filosofo);
            case LEVANTA -> avancar(estado.comSentados(estado.sentados() - 1), filosofo);
        };
    }

    public Estado aplicar(Estado estado, int filosofo) {
        for (Movimento movimento : movimentos(estado)) {
            if (movimento.filosofo() == filosofo) {
                return movimento.destino();
            }
        }
        throw new IllegalStateException("filosofo " + filosofo + " nao pode andar em " + estado);
    }

    private Estado avancar(Estado estado, int filosofo) {
        int tamanho = mesa.programa(filosofo).size();
        return estado.comPc(filosofo, (estado.pc(filosofo) + 1) % tamanho);
    }
}
