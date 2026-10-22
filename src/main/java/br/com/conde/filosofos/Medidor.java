package br.com.conde.filosofos;

import java.util.List;
import java.util.Locale;

/**
 * As tabelas do README. Uso:
 * {@code mvn exec:java -Dexec.args="estrategias"} (ou {@code paralelismo}, {@code tamanho}).
 */
public final class Medidor {

    private Medidor() {
    }

    public static void main(String[] args) {
        String tabela = args.length > 0 ? args[0] : "tudo";
        boolean tudo = tabela.equals("tudo");
        if (tudo || tabela.equals("estrategias")) {
            estrategias();
        }
        if (tudo || tabela.equals("paralelismo")) {
            paralelismo();
        }
        if (tudo || tabela.equals("tamanho")) {
            tamanho();
        }
    }

    /** As cinco estrategias na mesa de cinco. */
    public static void estrategias() {
        System.out.println();
        System.out.println("ESTRATEGIAS  (5 filosofos, 5 garfos)");
        System.out.printf(Locale.ROOT, "%-14s %8s %8s %9s %12s %10s%n",
                "estrategia", "estados", "arestas", "trava", "sem comer", "max juntos");
        for (Estrategia estrategia : Estrategia.values()) {
            Mesa mesa = Mesa.de(5, estrategia);
            Explorador.Resultado r = new Explorador(mesa).explorar();
            List<Inanicao.Achado> fome = new Inanicao(mesa, r).procurar();
            System.out.printf(Locale.ROOT, "%-14s %8d %8d %9s %12s %10d%n",
                    estrategia.rotulo(), r.estados(), r.transicoes(),
                    r.temImpasse() ? "SIM" : "nao",
                    fome.isEmpty() ? "-" : fome.size() + " filosofos",
                    r.maximoComendo());
        }
        Explorador.Resultado ingenua = new Explorador(Mesa.de(5, Estrategia.INGENUA)).explorar();
        System.out.println();
        System.out.println("como a mesa ingenua trava:");
        for (String linha : ingenua.impasse()) {
            System.out.println("  " + linha);
        }
        System.out.println();
    }

    /** Quanto de refeicao acompanhada cada estrategia produz. */
    public static void paralelismo() {
        System.out.println();
        System.out.println("PARALELISMO  (das vezes que alguem come, em quantas ja havia outro comendo)");
        System.out.printf(Locale.ROOT, "%-14s %10s %10s %10s%n",
                "estrategia", "4 lugares", "5 lugares", "6 lugares");
        for (Estrategia estrategia : Estrategia.values()) {
            if (estrategia == Estrategia.INGENUA) {
                continue;
            }
            StringBuilder linha = new StringBuilder(String.format(Locale.ROOT, "%-14s", estrategia.rotulo()));
            for (int filosofos = 4; filosofos <= 6; filosofos++) {
                Explorador.Resultado r = new Explorador(Mesa.de(filosofos, estrategia)).explorar();
                linha.append(String.format(Locale.ROOT, " %9.1f%%", 100 * r.refeicaoAcompanhada()));
            }
            System.out.println(linha);
        }
        System.out.println();
        System.out.println("maximo comendo ao mesmo tempo:");
        for (int filosofos = 3; filosofos <= 7; filosofos++) {
            Explorador.Resultado r = new Explorador(Mesa.de(filosofos, Estrategia.ATOMICA)).explorar();
            System.out.printf(Locale.ROOT, "  %d filosofos: %d%n", filosofos, r.maximoComendo());
        }
        System.out.println();
    }

    /** O tamanho do grafo por estrategia e por mesa. */
    public static void tamanho() {
        System.out.println();
        System.out.println("TAMANHO  (estados do grafo)");
        System.out.printf(Locale.ROOT, "%-14s %9s %9s %9s %9s%n",
                "estrategia", "3", "4", "5", "6");
        for (Estrategia estrategia : Estrategia.values()) {
            StringBuilder linha = new StringBuilder(String.format(Locale.ROOT, "%-14s", estrategia.rotulo()));
            for (int filosofos = 3; filosofos <= 6; filosofos++) {
                Explorador.Resultado r = new Explorador(Mesa.de(filosofos, estrategia)).explorar();
                linha.append(String.format(Locale.ROOT, " %9d", r.estados()));
            }
            System.out.println(linha);
        }
        System.out.println();
    }
}
