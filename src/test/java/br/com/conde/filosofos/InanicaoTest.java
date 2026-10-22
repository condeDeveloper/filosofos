package br.com.conde.filosofos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O achado que inverteu o que eu achava: a estrategia mais "limpa" e a unica que
 * deixa alguem sem comer.
 */
@DisplayName("Quem fica sem comer")
class InanicaoTest {

    private List<String> famintos(int filosofos, Estrategia estrategia) {
        Mesa mesa = Mesa.de(filosofos, estrategia);
        Explorador.Resultado r = new Explorador(mesa).explorar();
        return new Inanicao(mesa, r).procurar().stream().map(Inanicao.Achado::nome).toList();
    }

    @Test
    @DisplayName("pegar os dois garfos de uma vez evita o impasse e cria inanicao")
    void atomicaTemFome() {
        assertThat(famintos(5, Estrategia.ATOMICA)).hasSize(5);
        assertThat(famintos(4, Estrategia.ATOMICA)).hasSize(4);
        assertThat(famintos(6, Estrategia.ATOMICA)).hasSize(6);
    }

    @ParameterizedTest
    @EnumSource(value = Estrategia.class, names = {"HIERARQUIA", "PAR_IMPAR", "GARCOM"})
    @DisplayName("quem segura um garfo enquanto espera nao deixa ninguem passar fome")
    void segurarGarfoProtege(Estrategia estrategia) {
        for (int filosofos = 3; filosofos <= 5; filosofos++) {
            assertThat(famintos(filosofos, estrategia))
                    .describedAs("%s com %d filosofos", estrategia.rotulo(), filosofos)
                    .isEmpty();
        }
    }

    @Test
    @DisplayName("na mesa de TRES nem a atomica deixa alguem sem comer: nao ha como se revezar")
    void aMesaDeTresNaoTemFome() {
        assertThat(famintos(3, Estrategia.ATOMICA)).isEmpty();
        assertThat(famintos(4, Estrategia.ATOMICA)).hasSize(4);
    }

    @Test
    @DisplayName("quando ha fome, e a mesa inteira: nao sobra um privilegiado")
    void aMesaInteira() {
        for (int filosofos = 4; filosofos <= 6; filosofos++) {
            assertThat(famintos(filosofos, Estrategia.ATOMICA)).hasSize(filosofos);
        }
    }

    @Test
    @DisplayName("na atomica ninguem fica PARADO com garfo na mao: ou pega os dois ou nenhum")
    void atomicaNaoSeguraEnquantoEspera() {
        assertThat(paradoComGarfo(Estrategia.ATOMICA)).isZero();
        assertThat(famintos(5, Estrategia.ATOMICA)).isNotEmpty();
    }

    @Test
    @DisplayName("na hierarquia quem espera segura um garfo, e e isso que segura o vizinho")
    void naHierarquiaEleSegura() {
        assertThat(paradoComGarfo(Estrategia.HIERARQUIA)).isPositive();
        assertThat(famintos(5, Estrategia.HIERARQUIA)).isEmpty();
    }

    /** Estados em que o filosofo 1 nao consegue andar e tem um garfo na mao. */
    private long paradoComGarfo(Estrategia estrategia) {
        Mesa mesa = Mesa.de(5, estrategia);
        Explorador.Resultado r = new Explorador(mesa).explorar();
        return r.grafo().entrySet().stream()
                .filter(entrada -> entrada.getKey().garfosNaMao(0) == 1)
                .filter(entrada -> entrada.getValue().stream()
                        .noneMatch(movimento -> movimento.filosofo() == 0))
                .count();
    }
}
