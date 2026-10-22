package br.com.conde.filosofos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("O impasse")
class ImpasseTest {

    @Test
    @DisplayName("a mesa ingenua trava, e o caminho e todo mundo pegando a esquerda")
    void ingenuaTrava() {
        Explorador.Resultado r = new Explorador(Mesa.de(5, Estrategia.INGENUA)).explorar();

        assertThat(r.temImpasse()).isTrue();
        assertThat(r.impasse()).hasSize(6);
        assertThat(r.impasse().get(5)).contains("5 filosofos com um garfo na mao");
    }

    @ParameterizedTest
    @EnumSource(value = Estrategia.class, names = {"HIERARQUIA", "PAR_IMPAR", "ATOMICA", "GARCOM"})
    @DisplayName("as outras quatro nao travam, em nenhuma mesa de 3 a 6 lugares")
    void asOutrasNaoTravam(Estrategia estrategia) {
        for (int filosofos = 3; filosofos <= 6; filosofos++) {
            assertThat(new Explorador(Mesa.de(filosofos, estrategia)).explorar().temImpasse())
                    .describedAs("%s com %d filosofos", estrategia.rotulo(), filosofos)
                    .isFalse();
        }
    }

    @Test
    @DisplayName("a mesa ingenua trava em qualquer tamanho, ate com dois filosofos")
    void travaEmQualquerTamanho() {
        for (int filosofos = 2; filosofos <= 6; filosofos++) {
            assertThat(new Explorador(Mesa.de(filosofos, Estrategia.INGENUA)).explorar().temImpasse())
                    .describedAs("mesa de %d", filosofos)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("o impasse e UM estado em 392, e e por isso que teste aleatorio nao acha")
    void umEstadoEm392() {
        Mesa mesa = Mesa.de(5, Estrategia.INGENUA);
        Explorador.Resultado r = new Explorador(mesa).explorar();
        Maquina maquina = new Maquina(mesa);

        long travados = r.grafo().keySet().stream()
                .filter(estado -> maquina.movimentos(estado).isEmpty())
                .count();

        assertThat(r.estados()).isEqualTo(392);
        assertThat(travados).isEqualTo(1);
    }

    @Test
    @DisplayName("a ingenua e a hierarquia tem o MESMO tamanho de grafo, e nao os mesmos estados")
    void mesmoTamanhoOutrosEstados() {
        Explorador.Resultado ingenua = new Explorador(Mesa.de(5, Estrategia.INGENUA)).explorar();
        Explorador.Resultado hierarquia = new Explorador(Mesa.de(5, Estrategia.HIERARQUIA)).explorar();

        assertThat(ingenua.estados()).isEqualTo(hierarquia.estados()).isEqualTo(392);
        assertThat(ingenua.transicoes()).isEqualTo(hierarquia.transicoes()).isEqualTo(1250);

        Set<Estado> soNaIngenua = new HashSet<>(ingenua.grafo().keySet());
        soNaIngenua.removeAll(hierarquia.grafo().keySet());

        assertThat(soNaIngenua).hasSize(76);
    }

    @Test
    @DisplayName("as duas tambem produzem exatamente as mesmas refeicoes")
    void mesmasRefeicoes() {
        Explorador.Resultado ingenua = new Explorador(Mesa.de(5, Estrategia.INGENUA)).explorar();
        Explorador.Resultado hierarquia = new Explorador(Mesa.de(5, Estrategia.HIERARQUIA)).explorar();

        assertThat(ingenua.refeicoes()).isEqualTo(hierarquia.refeicoes()).isEqualTo(165);
        assertThat(ingenua.refeicoesAcompanhadas())
                .isEqualTo(hierarquia.refeicoesAcompanhadas()).isEqualTo(30);
    }
}
