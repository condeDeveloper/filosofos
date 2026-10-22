package br.com.conde.filosofos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Varre todos os entrelacamentos e mede tres coisas no mesmo grafo: se a mesa
 * trava, quantos comem ao mesmo tempo e quanto de cada um.
 *
 * <p>Impasse aqui e um estado sem movimento nenhum. Nos filosofos ele tem uma
 * forma reconhecivel: todo mundo com exatamente um garfo na mao, esperando o
 * vizinho soltar o outro.
 */
public final class Explorador {

    private final Mesa mesa;
    private final Maquina maquina;
    private final int teto;

    public Explorador(Mesa mesa) {
        this(mesa, 400_000);
    }

    public Explorador(Mesa mesa, int teto) {
        this.mesa = mesa;
        this.maquina = new Maquina(mesa);
        this.teto = teto;
    }

    public record Resultado(Mesa mesa, int estados, int transicoes, int maximoComendo,
                            int refeicoes, int refeicoesAcompanhadas,
                            List<String> impasse, Map<Estado, List<Maquina.Movimento>> grafo) {

        public boolean temImpasse() {
            return impasse != null;
        }

        /** Das vezes em que alguem senta para comer, em quantas ja havia outro comendo. */
        public double refeicaoAcompanhada() {
            return refeicoes == 0 ? 0 : (double) refeicoesAcompanhadas / refeicoes;
        }
    }

    public Resultado explorar() {
        Estado inicial = mesa.inicial();
        Map<Estado, List<Maquina.Movimento>> grafo = new HashMap<>();
        Map<Estado, Estado> pai = new HashMap<>();
        Map<Estado, String> comoCheguei = new HashMap<>();
        Deque<Estado> aFazer = new ArrayDeque<>();
        grafo.put(inicial, null);
        pai.put(inicial, null);
        aFazer.add(inicial);

        int transicoes = 0;
        int maximoComendo = 0;
        int refeicoes = 0;
        int acompanhadas = 0;
        List<String> impasse = null;

        while (!aFazer.isEmpty()) {
            Estado atual = aFazer.poll();
            List<Maquina.Movimento> daqui = maquina.movimentos(atual);
            grafo.put(atual, daqui);
            transicoes += daqui.size();
            maximoComendo = Math.max(maximoComendo, mesa.quantosComendo(atual));

            if (daqui.isEmpty() && impasse == null) {
                impasse = traco(atual, pai, comoCheguei, descreverTravamento(atual));
            }

            for (Maquina.Movimento movimento : daqui) {
                if (movimento.passo().tipo() == Passo.Tipo.COME) {
                    refeicoes++;
                    if (mesa.quantosComendo(atual) > 1) {
                        acompanhadas++;
                    }
                }
                Estado destino = movimento.destino();
                if (!grafo.containsKey(destino)) {
                    if (grafo.size() >= teto) {
                        throw new IllegalStateException("passou do teto de " + teto + " estados");
                    }
                    grafo.put(destino, null);
                    pai.put(destino, atual);
                    comoCheguei.put(destino, mesa.nomes().get(movimento.filosofo())
                            + ": " + movimento.passo().rotulo());
                    aFazer.add(destino);
                }
            }
        }

        return new Resultado(mesa, grafo.size(), transicoes, maximoComendo,
                refeicoes, acompanhadas, impasse, grafo);
    }

    private String descreverTravamento(Estado estado) {
        int comUm = 0;
        for (int filosofo = 0; filosofo < mesa.filosofos(); filosofo++) {
            if (estado.garfosNaMao(filosofo) == 1) {
                comUm++;
            }
        }
        return "mesa travada: " + comUm + " filosofos com um garfo na mao, esperando o vizinho";
    }

    private List<String> traco(Estado alvo, Map<Estado, Estado> pai,
                               Map<Estado, String> comoCheguei, String veredito) {
        List<String> caminho = new ArrayList<>();
        Estado passo = alvo;
        while (pai.get(passo) != null) {
            caminho.add(0, comoCheguei.get(passo));
            passo = pai.get(passo);
        }
        caminho.add("=> " + veredito);
        return caminho;
    }
}
