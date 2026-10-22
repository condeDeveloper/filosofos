package br.com.conde.filosofos;

import java.util.ArrayList;
import java.util.List;

/** A configuracao medida: quantos filosofos e qual estrategia. */
public record Mesa(int filosofos, Estrategia estrategia) {

    public Mesa {
        if (filosofos < 2) {
            throw new IllegalArgumentException("mesa com menos de dois filosofos nao disputa nada");
        }
    }

    public static Mesa de(int filosofos, Estrategia estrategia) {
        return new Mesa(filosofos, estrategia);
    }

    public Mesa com(Estrategia outra) {
        return new Mesa(filosofos, outra);
    }

    public List<Passo> programa(int filosofo) {
        return estrategia.programa(filosofo, filosofos);
    }

    public Passo passoAtual(Estado estado, int filosofo) {
        return programa(filosofo).get(estado.pc(filosofo));
    }

    public int limiteDeLugares() {
        return estrategia.limiteDeLugares(filosofos);
    }

    /** O filosofo esta comendo? Ele esta, enquanto o passo atual dele e o {@code come}. */
    public boolean comendo(Estado estado, int filosofo) {
        return passoAtual(estado, filosofo).tipo() == Passo.Tipo.COME;
    }

    public int quantosComendo(Estado estado) {
        int conta = 0;
        for (int filosofo = 0; filosofo < filosofos; filosofo++) {
            if (comendo(estado, filosofo)) {
                conta++;
            }
        }
        return conta;
    }

    public Estado inicial() {
        return Estado.inicial(filosofos);
    }

    public List<String> nomes() {
        List<String> nomes = new ArrayList<>();
        for (int i = 0; i < filosofos; i++) {
            nomes.add("filosofo" + (i + 1));
        }
        return nomes;
    }

    @Override
    public String toString() {
        return filosofos + " filosofos, " + estrategia.rotulo();
    }
}
