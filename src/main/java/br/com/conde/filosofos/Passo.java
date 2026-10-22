package br.com.conde.filosofos;

/**
 * Uma instrucao atomica de um filosofo.
 *
 * <p>O tamanho do passo e o que decide o resultado inteiro. Pegar dois garfos "de
 * uma vez" e uma estrategia diferente de pegar um e depois o outro, e a diferenca
 * entre elas nao e de estilo: e a diferenca entre travar e nao travar. Por isso
 * {@link Tipo#PEGA} e {@link Tipo#PEGA_DOIS} sao instrucoes distintas, e nao a
 * mesma coisa escrita de dois jeitos.
 */
public record Passo(Tipo tipo, int garfo, int outroGarfo, String rotulo) {

    public enum Tipo {
        /** Pega um garfo. Se estiver na mao de outro, o filosofo nao progride. */
        PEGA,
        /** Pega os dois garfos num golpe so, ou nao faz nada. */
        PEGA_DOIS,
        /** Pede lugar ao garcom. Se a sala esta cheia, espera. */
        SENTA,
        /** Come. E neste passo que o paralelismo e contado. */
        COME,
        /** Devolve um garfo. */
        SOLTA,
        /** Devolve o lugar ao garcom. */
        LEVANTA
    }

    public static Passo pega(int garfo) {
        return new Passo(Tipo.PEGA, garfo, -1, "pega o garfo " + garfo);
    }

    public static Passo pegaDois(int um, int outro) {
        return new Passo(Tipo.PEGA_DOIS, um, outro, "pega os garfos " + um + " e " + outro);
    }

    public static Passo senta() {
        return new Passo(Tipo.SENTA, -1, -1, "pede lugar ao garcom");
    }

    public static Passo come() {
        return new Passo(Tipo.COME, -1, -1, "come");
    }

    public static Passo solta(int garfo) {
        return new Passo(Tipo.SOLTA, garfo, -1, "solta o garfo " + garfo);
    }

    public static Passo levanta() {
        return new Passo(Tipo.LEVANTA, -1, -1, "devolve o lugar");
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
