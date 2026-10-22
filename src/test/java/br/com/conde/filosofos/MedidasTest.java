package br.com.conde.filosofos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Os numeros publicados no README, presos em teste. */
@DisplayName("Os numeros do README")
class MedidasTest {

    private Explorador.Resultado r(int filosofos, Estrategia estrategia) {
        return new Explorador(Mesa.de(filosofos, estrategia)).explorar();
    }

    @Test
    @DisplayName("tabela das estrategias: mesa de cinco")
    void tabelaDasEstrategias() {
        assertThat(r(5, Estrategia.INGENUA).estados()).isEqualTo(392);
        assertThat(r(5, Estrategia.INGENUA).transicoes()).isEqualTo(1250);
        assertThat(r(5, Estrategia.HIERARQUIA).estados()).isEqualTo(392);
        assertThat(r(5, Estrategia.PAR_IMPAR).estados()).isEqualTo(422);
        assertThat(r(5, Estrategia.PAR_IMPAR).transicoes()).isEqualTo(1369);
        assertThat(r(5, Estrategia.ATOMICA).estados()).isEqualTo(151);
        assertThat(r(5, Estrategia.ATOMICA).transicoes()).isEqualTo(455);
        assertThat(r(5, Estrategia.GARCOM).estados()).isEqualTo(4711);
        assertThat(r(5, Estrategia.GARCOM).transicoes()).isEqualTo(18425);
    }

    @Test
    @DisplayName("tabela do tamanho: o grafo por estrategia e por mesa")
    void tabelaDoTamanho() {
        assertThat(r(3, Estrategia.INGENUA).estados()).isEqualTo(35);
        assertThat(r(4, Estrategia.INGENUA).estados()).isEqualTo(118);
        assertThat(r(6, Estrategia.INGENUA).estados()).isEqualTo(1297);
        assertThat(r(3, Estrategia.ATOMICA).estados()).isEqualTo(19);
        assertThat(r(4, Estrategia.ATOMICA).estados()).isEqualTo(55);
        assertThat(r(6, Estrategia.ATOMICA).estados()).isEqualTo(415);
        assertThat(r(3, Estrategia.GARCOM).estados()).isEqualTo(100);
        assertThat(r(4, Estrategia.GARCOM).estados()).isEqualTo(709);
        assertThat(r(6, Estrategia.GARCOM).estados()).isEqualTo(30196);
    }

    @Test
    @DisplayName("o garcom custa 31 vezes o grafo da atomica na mesa de cinco")
    void garcomCustaCaro() {
        double garcom = r(5, Estrategia.GARCOM).estados();
        double atomica = r(5, Estrategia.ATOMICA).estados();

        assertThat(garcom / atomica).isBetween(30.0, 32.0);
    }

    @Test
    @DisplayName("tabela do paralelismo: mesas de 4, 5 e 6")
    void tabelaDoParalelismo() {
        assertThat(pct(4, Estrategia.HIERARQUIA)).isEqualTo(10.0);
        assertThat(pct(5, Estrategia.HIERARQUIA)).isEqualTo(18.2);
        assertThat(pct(6, Estrategia.HIERARQUIA)).isEqualTo(25.7);
        assertThat(pct(4, Estrategia.ATOMICA)).isEqualTo(16.7);
        assertThat(pct(5, Estrategia.ATOMICA)).isEqualTo(25.0);
        assertThat(pct(6, Estrategia.ATOMICA)).isEqualTo(34.1);
        assertThat(pct(4, Estrategia.GARCOM)).isEqualTo(9.6);
        assertThat(pct(5, Estrategia.GARCOM)).isEqualTo(16.6);
        assertThat(pct(6, Estrategia.GARCOM)).isEqualTo(23.5);
    }

    @Test
    @DisplayName("mesa de menos de dois nao disputa nada, e o construtor recusa")
    void recusaMesaSemDisputa() {
        assertThatThrownBy(() -> Mesa.de(1, Estrategia.INGENUA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dois filosofos");
    }

    @Test
    @DisplayName("o teto protege de mesa grande demais")
    void teto() {
        Explorador apertado = new Explorador(Mesa.de(6, Estrategia.GARCOM), 100);

        assertThatThrownBy(apertado::explorar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("teto de 100 estados");
    }

    @Test
    @DisplayName("o medidor roda inteiro sem lancar")
    void medidorRoda() {
        Medidor.estrategias();
        Medidor.paralelismo();
        Medidor.tamanho();
    }

    private double pct(int filosofos, Estrategia estrategia) {
        return Math.round(1000 * r(filosofos, estrategia).refeicaoAcompanhada()) / 10.0;
    }
}
