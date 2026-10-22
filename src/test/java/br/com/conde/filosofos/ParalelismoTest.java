package br.com.conde.filosofos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Quantos comem ao mesmo tempo")
class ParalelismoTest {

    private Explorador.Resultado r(int filosofos, Estrategia estrategia) {
        return new Explorador(Mesa.de(filosofos, estrategia)).explorar();
    }

    @ParameterizedTest
    @EnumSource(Estrategia.class)
    @DisplayName("o maximo comendo junto e a metade da mesa, arredondada para baixo")
    void metadeDaMesa(Estrategia estrategia) {
        for (int filosofos = 3; filosofos <= 6; filosofos++) {
            assertThat(r(filosofos, estrategia).maximoComendo())
                    .describedAs("%s com %d filosofos", estrategia.rotulo(), filosofos)
                    .isEqualTo(filosofos / 2);
        }
    }

    @Test
    @DisplayName("a atomica e a que mais produz refeicao acompanhada")
    void atomicaLidera() {
        for (int filosofos = 4; filosofos <= 6; filosofos++) {
            double atomica = r(filosofos, Estrategia.ATOMICA).refeicaoAcompanhada();

            assertThat(atomica).isGreaterThan(r(filosofos, Estrategia.HIERARQUIA).refeicaoAcompanhada());
            assertThat(atomica).isGreaterThan(r(filosofos, Estrategia.PAR_IMPAR).refeicaoAcompanhada());
            assertThat(atomica).isGreaterThan(r(filosofos, Estrategia.GARCOM).refeicaoAcompanhada());
        }
    }

    @Test
    @DisplayName("o garcom e o que menos produz, e ainda tem o maior grafo")
    void garcomCustaEEntregaMenos() {
        double garcom = r(5, Estrategia.GARCOM).refeicaoAcompanhada();

        assertThat(garcom).isLessThan(r(5, Estrategia.HIERARQUIA).refeicaoAcompanhada());
        assertThat(garcom).isLessThan(r(5, Estrategia.PAR_IMPAR).refeicaoAcompanhada());
        assertThat(r(5, Estrategia.GARCOM).estados()).isGreaterThan(10 * r(5, Estrategia.ATOMICA).estados());
    }

    @Test
    @DisplayName("mesa maior, mais refeicao acompanhada, em todas as estrategias")
    void mesaMaiorMaisJunta() {
        for (Estrategia estrategia : Estrategia.values()) {
            assertThat(r(4, estrategia).refeicaoAcompanhada())
                    .describedAs("%s", estrategia.rotulo())
                    .isLessThan(r(6, estrategia).refeicaoAcompanhada());
        }
    }

    @Test
    @DisplayName("os numeros publicados da mesa de cinco")
    void numerosPublicados() {
        assertThat(r(5, Estrategia.HIERARQUIA).refeicaoAcompanhada()).isCloseTo(0.182, within(0.001));
        assertThat(r(5, Estrategia.PAR_IMPAR).refeicaoAcompanhada()).isCloseTo(0.179, within(0.001));
        assertThat(r(5, Estrategia.ATOMICA).refeicaoAcompanhada()).isCloseTo(0.250, within(0.001));
        assertThat(r(5, Estrategia.GARCOM).refeicaoAcompanhada()).isCloseTo(0.166, within(0.001));
    }

    @Test
    @DisplayName("na mesa de tres ninguem come acompanhado: nao cabe")
    void mesaDeTresNaoCabe() {
        for (Estrategia estrategia : Estrategia.values()) {
            assertThat(r(3, estrategia).refeicaoAcompanhada()).isZero();
            assertThat(r(3, estrategia).maximoComendo()).isEqualTo(1);
        }
    }
}
