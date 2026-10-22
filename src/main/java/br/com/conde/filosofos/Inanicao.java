package br.com.conde.filosofos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Procura o filosofo que nunca come.
 *
 * <p>Impasse e a mesa inteira parada, e e facil de ver. Inanicao e o contrario:
 * a mesa anda, todo mundo come, e um filosofo especifico fica de maos vazias para
 * sempre porque os dois vizinhos se revezam e nunca soltam os garfos ao mesmo
 * tempo. A pergunta nao e sobre estado nenhum, e sobre <b>ciclo</b>.
 *
 * <p>A volta so conta se for <b>justa</b>: todo filosofo que em algum ponto dela
 * podia andar precisa ter andado. Sem essa exigencia toda estrategia "tem
 * inanicao", porque sempre da para imaginar um escalonador que ignora alguem de
 * proposito. O que interessa e o caso em que ninguem e ignorado e mesmo assim
 * alguem fica sem comer.
 */
public final class Inanicao {

    private final Mesa mesa;
    private final Explorador.Resultado resultado;

    public Inanicao(Mesa mesa, Explorador.Resultado resultado) {
        this.mesa = mesa;
        this.resultado = resultado;
    }

    public record Achado(int filosofo, String nome, int tamanhoDoCiclo) {
    }

    public List<Achado> procurar() {
        List<Achado> achados = new ArrayList<>();
        for (int filosofo = 0; filosofo < mesa.filosofos(); filosofo++) {
            Achado achado = procurarPara(filosofo);
            if (achado != null) {
                achados.add(achado);
            }
        }
        return achados;
    }

    private Achado procurarPara(int faminto) {
        // So interessa o pedaco do grafo em que o faminto esta de maos vazias e
        // parado: nos estados em que ele PODE andar, ficar de fora seria escolha do
        // escalonador, nao consequencia da estrategia.
        Set<Estado> permitidos = new HashSet<>();
        for (Estado estado : resultado.grafo().keySet()) {
            if (parado(estado, faminto)) {
                permitidos.add(estado);
            }
        }
        if (permitidos.isEmpty()) {
            return null;
        }
        for (List<Estado> componente : componentes(permitidos)) {
            if (temCiclo(componente, permitidos) && justo(componente, faminto)) {
                return new Achado(faminto, mesa.nomes().get(faminto), componente.size());
            }
        }
        return null;
    }

    /** O filosofo esta sem conseguir andar, e ainda nao chegou a comer. */
    private boolean parado(Estado estado, int filosofo) {
        if (mesa.comendo(estado, filosofo)) {
            return false;
        }
        for (Maquina.Movimento movimento : resultado.grafo().get(estado)) {
            if (movimento.filosofo() == filosofo) {
                return false;
            }
        }
        return true;
    }

    private List<List<Estado>> componentes(Set<Estado> permitidos) {
        Map<Estado, Integer> indice = new HashMap<>();
        Map<Estado, Integer> menor = new HashMap<>();
        Set<Estado> naPilha = new HashSet<>();
        Deque<Estado> pilha = new ArrayDeque<>();
        List<List<Estado>> saida = new ArrayList<>();
        int[] contador = {0};

        for (Estado raiz : permitidos) {
            if (indice.containsKey(raiz)) {
                continue;
            }
            Deque<Estado> caminho = new ArrayDeque<>();
            Deque<Integer> posicao = new ArrayDeque<>();
            caminho.push(raiz);
            posicao.push(0);
            indice.put(raiz, contador[0]);
            menor.put(raiz, contador[0]++);
            pilha.push(raiz);
            naPilha.add(raiz);

            while (!caminho.isEmpty()) {
                Estado atual = caminho.peek();
                List<Estado> vizinhos = vizinhos(atual, permitidos);
                int i = posicao.pop();
                if (i < vizinhos.size()) {
                    posicao.push(i + 1);
                    Estado proximo = vizinhos.get(i);
                    if (!indice.containsKey(proximo)) {
                        indice.put(proximo, contador[0]);
                        menor.put(proximo, contador[0]++);
                        pilha.push(proximo);
                        naPilha.add(proximo);
                        caminho.push(proximo);
                        posicao.push(0);
                    } else if (naPilha.contains(proximo)) {
                        menor.put(atual, Math.min(menor.get(atual), indice.get(proximo)));
                    }
                } else {
                    caminho.pop();
                    if (!caminho.isEmpty()) {
                        Estado anterior = caminho.peek();
                        menor.put(anterior, Math.min(menor.get(anterior), menor.get(atual)));
                    }
                    if (menor.get(atual).equals(indice.get(atual))) {
                        List<Estado> componente = new ArrayList<>();
                        Estado quem;
                        do {
                            quem = pilha.pop();
                            naPilha.remove(quem);
                            componente.add(quem);
                        } while (!quem.equals(atual));
                        saida.add(componente);
                    }
                }
            }
        }
        return saida;
    }

    private List<Estado> vizinhos(Estado estado, Set<Estado> permitidos) {
        List<Estado> saida = new ArrayList<>();
        for (Maquina.Movimento movimento : resultado.grafo().get(estado)) {
            if (permitidos.contains(movimento.destino())) {
                saida.add(movimento.destino());
            }
        }
        return saida;
    }

    private boolean temCiclo(List<Estado> componente, Set<Estado> permitidos) {
        if (componente.size() > 1) {
            return true;
        }
        Estado unico = componente.get(0);
        return vizinhos(unico, permitidos).contains(unico);
    }

    private boolean justo(List<Estado> componente, int faminto) {
        Set<Estado> dentro = new HashSet<>(componente);
        Set<Integer> podiaAndar = new HashSet<>();
        Set<Integer> andou = new HashSet<>();
        for (Estado estado : componente) {
            for (Maquina.Movimento movimento : resultado.grafo().get(estado)) {
                podiaAndar.add(movimento.filosofo());
                if (dentro.contains(movimento.destino())) {
                    andou.add(movimento.filosofo());
                }
            }
        }
        if (podiaAndar.contains(faminto)) {
            return false;
        }
        podiaAndar.removeAll(andou);
        return podiaAndar.isEmpty() && !andou.isEmpty();
    }
}
