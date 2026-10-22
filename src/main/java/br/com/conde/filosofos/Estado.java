package br.com.conde.filosofos;

import java.util.Arrays;

/**
 * O estado da mesa: onde cada filosofo esta no proprio laco, quem segura cada
 * garfo e quantos lugares o garcom ja deu.
 *
 * <p>Os filosofos nao param: pensam, comem e voltam a pensar para sempre. Sem o
 * laco nao existiria ciclo no grafo, e sem ciclo nao haveria como perguntar se
 * alguem fica sem comer <b>para sempre</b>, que e a pergunta que separa a
 * estrategia que so evita o impasse daquela que tambem e justa.
 */
public final class Estado {

    private final int[] pc;
    private final int[] dono;
    private final int sentados;
    private final int cacheHash;

    public Estado(int[] pc, int[] dono, int sentados) {
        this.pc = pc;
        this.dono = dono;
        this.sentados = sentados;
        this.cacheHash = 31 * (31 * Arrays.hashCode(pc) + Arrays.hashCode(dono)) + sentados;
    }

    public static Estado inicial(int filosofos) {
        int[] dono = new int[filosofos];
        Arrays.fill(dono, -1);
        return new Estado(new int[filosofos], dono, 0);
    }

    public int pc(int filosofo) {
        return pc[filosofo];
    }

    public int filosofos() {
        return pc.length;
    }

    public int dono(int garfo) {
        return dono[garfo];
    }

    public boolean livre(int garfo) {
        return dono[garfo] < 0;
    }

    public int sentados() {
        return sentados;
    }

    public Estado comPc(int filosofo, int novo) {
        int[] outro = pc.clone();
        outro[filosofo] = novo;
        return new Estado(outro, dono, sentados);
    }

    public Estado pegar(int garfo, int filosofo) {
        int[] outro = dono.clone();
        outro[garfo] = filosofo;
        return new Estado(pc, outro, sentados);
    }

    public Estado soltar(int garfo) {
        int[] outro = dono.clone();
        outro[garfo] = -1;
        return new Estado(pc, outro, sentados);
    }

    public Estado comSentados(int quantos) {
        return new Estado(pc, dono, quantos);
    }

    /** Quantos garfos este filosofo tem na mao. */
    public int garfosNaMao(int filosofo) {
        int conta = 0;
        for (int quem : dono) {
            if (quem == filosofo) {
                conta++;
            }
        }
        return conta;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof Estado e && sentados == e.sentados
                && Arrays.equals(pc, e.pc) && Arrays.equals(dono, e.dono);
    }

    @Override
    public int hashCode() {
        return cacheHash;
    }

    @Override
    public String toString() {
        return "pc=" + Arrays.toString(pc) + " garfos=" + Arrays.toString(dono)
                + (sentados > 0 ? " sentados=" + sentados : "");
    }
}
