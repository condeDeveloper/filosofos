package br.com.conde.filosofos;

import java.util.ArrayList;
import java.util.List;

/**
 * As cinco maneiras de sentar a mesa.
 *
 * <p>Todas resolvem o mesmo problema fisico: cinco filosofos, cinco garfos, e
 * cada um precisa dos dois vizinhos para comer. O que muda e uma linha de regra,
 * e essa linha decide se a mesa trava, se alguem fica sem comer e quantos comem
 * ao mesmo tempo.
 */
public enum Estrategia {

    /** Todo mundo pega o garfo da esquerda e depois o da direita. O impasse do livro. */
    INGENUA("ingenua"),

    /**
     * Dijkstra: numere os garfos e pegue sempre o de menor numero primeiro. So um
     * filosofo faz diferente do resto, e e isso que quebra o ciclo de espera.
     */
    HIERARQUIA("hierarquia"),

    /**
     * Os de indice par comecam pela direita, os impares pela esquerda. Tambem
     * quebra o ciclo, e de um jeito mais simetrico que a hierarquia.
     */
    PAR_IMPAR("par e impar"),

    /** Pega os dois garfos num golpe so, ou nao pega nenhum. */
    ATOMICA("pega os dois"),

    /**
     * Um garcom deixa no maximo N-1 filosofos tentarem ao mesmo tempo. Com um
     * lugar a menos que o numero de garfos, sempre sobra garfo para alguem.
     */
    GARCOM("garcom");

    private final String rotulo;

    Estrategia(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    /**
     * O laco do filosofo {@code i} numa mesa de {@code n} lugares.
     *
     * <p>O garfo da esquerda do filosofo {@code i} e o garfo {@code i}; o da
     * direita e o {@code (i + 1) % n}. Sao sempre esses dois, em toda estrategia:
     * o que muda e a ORDEM de pegar, nunca quais garfos.
     */
    public List<Passo> programa(int filosofo, int n) {
        int esquerdo = filosofo;
        int direito = (filosofo + 1) % n;
        List<Passo> codigo = new ArrayList<>();

        if (this == GARCOM) {
            codigo.add(Passo.senta());
        }
        switch (this) {
            case ATOMICA -> codigo.add(Passo.pegaDois(esquerdo, direito));
            case HIERARQUIA -> {
                codigo.add(Passo.pega(Math.min(esquerdo, direito)));
                codigo.add(Passo.pega(Math.max(esquerdo, direito)));
            }
            case PAR_IMPAR -> {
                boolean comecaPelaDireita = filosofo % 2 == 0;
                codigo.add(Passo.pega(comecaPelaDireita ? direito : esquerdo));
                codigo.add(Passo.pega(comecaPelaDireita ? esquerdo : direito));
            }
            default -> {
                codigo.add(Passo.pega(esquerdo));
                codigo.add(Passo.pega(direito));
            }
        }
        codigo.add(Passo.come());
        codigo.add(Passo.solta(esquerdo));
        codigo.add(Passo.solta(direito));
        if (this == GARCOM) {
            codigo.add(Passo.levanta());
        }
        return List.copyOf(codigo);
    }

    /** Quantos podem tentar ao mesmo tempo. So o garcom limita. */
    public int limiteDeLugares(int n) {
        return this == GARCOM ? n - 1 : n;
    }
}
