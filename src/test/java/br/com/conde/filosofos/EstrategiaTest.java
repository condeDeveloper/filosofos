package br.com.conde.filosofos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Os programas das cinco estrategias")
class EstrategiaTest {

    @ParameterizedTest
    @EnumSource(Estrategia.class)
    @DisplayName("todo filosofo usa os dois garfos vizinhos dele, e so eles")
    void sempreOsMesmosDoisGarfos(Estrategia estrategia) {
        for (int filosofo = 0; filosofo < 5; filosofo++) {
            List<Passo> codigo = estrategia.programa(filosofo, 5);
            int esquerdo = filosofo;
            int direito = (filosofo + 1) % 5;

            assertThat(codigo).filteredOn(p -> p.tipo() == Passo.Tipo.SOLTA)
                    .extracting(Passo::garfo)
                    .containsExactlyInAnyOrder(esquerdo, direito);
        }
    }

    @ParameterizedTest
    @EnumSource(Estrategia.class)
    @DisplayName("todo laco come uma vez e devolve os dois garfos")
    void comeUmaVezEDevolve(Estrategia estrategia) {
        List<Passo> codigo = estrategia.programa(0, 5);

        assertThat(codigo).filteredOn(p -> p.tipo() == Passo.Tipo.COME).hasSize(1);
        assertThat(codigo).filteredOn(p -> p.tipo() == Passo.Tipo.SOLTA).hasSize(2);
    }

    @Test
    @DisplayName("na ingenua todo mundo pega a esquerda primeiro, e e esse o ciclo")
    void ingenuaEUnanime() {
        for (int filosofo = 0; filosofo < 5; filosofo++) {
            assertThat(Estrategia.INGENUA.programa(filosofo, 5).get(0).garfo())
                    .isEqualTo(filosofo);
        }
    }

    @Test
    @DisplayName("na hierarquia so um filosofo faz diferente do resto")
    void hierarquiaQuebraOCiclo() {
        int diferentes = 0;
        for (int filosofo = 0; filosofo < 5; filosofo++) {
            if (Estrategia.HIERARQUIA.programa(filosofo, 5).get(0).garfo() != filosofo) {
                diferentes++;
            }
        }

        assertThat(diferentes).isEqualTo(1);
    }

    @Test
    @DisplayName("no par e impar, metade da mesa faz diferente")
    void parImparDivideAMesa() {
        int diferentes = 0;
        for (int filosofo = 0; filosofo < 6; filosofo++) {
            if (Estrategia.PAR_IMPAR.programa(filosofo, 6).get(0).garfo() != filosofo) {
                diferentes++;
            }
        }

        assertThat(diferentes).isEqualTo(3);
    }

    @Test
    @DisplayName("a atomica tem um passo a menos: os dois garfos saem juntos")
    void atomicaEMaisCurta() {
        List<Passo> atomica = Estrategia.ATOMICA.programa(0, 5);

        assertThat(atomica).hasSize(4);
        assertThat(atomica.get(0).tipo()).isEqualTo(Passo.Tipo.PEGA_DOIS);
        assertThat(Estrategia.HIERARQUIA.programa(0, 5)).hasSize(5);
    }

    @Test
    @DisplayName("so o garcom limita lugares, e o limite e um a menos que a mesa")
    void garcomLimita() {
        assertThat(Estrategia.GARCOM.limiteDeLugares(5)).isEqualTo(4);
        assertThat(Estrategia.GARCOM.programa(0, 5).get(0).tipo()).isEqualTo(Passo.Tipo.SENTA);

        for (Estrategia outra : List.of(Estrategia.INGENUA, Estrategia.HIERARQUIA,
                Estrategia.PAR_IMPAR, Estrategia.ATOMICA)) {
            assertThat(outra.limiteDeLugares(5)).isEqualTo(5);
        }
    }
}
