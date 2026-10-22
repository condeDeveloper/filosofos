# filosofos

O jantar dos filósofos do zero em Java 21, com as cinco estratégias julgadas pela
varredura de **todos os entrelaçamentos possíveis** — impasse, inanição e
paralelismo medidos no mesmo grafo.

Não há uma `Thread` aqui. Cada ação vira um passo atômico, a mesa inteira cabe
num objeto imutável e rodar o programa vira caminhar num grafo. O que ele
responde não é "não travou nas mil vezes que rodei": é "existe exatamente **um**
estado travado em 392, e aqui está o caminho até ele".

```
mvn -B -ntp verify
mvn -B -ntp exec:java -Dexec.args="estrategias"   # (ou paralelismo, tamanho)
```

## As cinco estratégias

Mesa de 5 filósofos e 5 garfos:

| estratégia | estados | arestas | trava | fica sem comer | máx. juntos |
|---|---:|---:|---|---|---:|
| ingênua | 392 | 1.250 | **SIM** | - | 2 |
| hierarquia | 392 | 1.250 | não | - | 2 |
| par e ímpar | 422 | 1.369 | não | - | 2 |
| pega os dois | 151 | 455 | não | **os 5** | 2 |
| garçom | 4.711 | 18.425 | não | - | 2 |

O travamento da mesa ingênua sai em cinco passos, um por filósofo:

```
filosofo1: pega o garfo 0
filosofo2: pega o garfo 1
filosofo3: pega o garfo 2
filosofo4: pega o garfo 3
filosofo5: pega o garfo 4
=> mesa travada: 5 filosofos com um garfo na mao, esperando o vizinho
```

## A ingênua e a hierarquia são gêmeas, menos num ponto

Esta linha da tabela é a que vale o repositório:

| | ingênua | hierarquia |
|---|---:|---:|
| estados | 392 | 392 |
| arestas | 1.250 | 1.250 |
| refeições possíveis | 165 | 165 |
| refeições acompanhadas | 30 | 30 |
| **estados travados** | **1** | **0** |

Mesmo tamanho de grafo, mesmo número de caminhos, mesma quantidade de refeições.
**76 dos 392 estados são diferentes**, e a única diferença que importa é que um
dos conjuntos contém o estado em que ninguém pode andar e o outro não.

Numerar os garfos e mandar pegar o menor primeiro não torna a mesa mais rápida,
não a torna mais justa e não muda o quanto se come. Só tira um estado de 392 do
mapa. E é esse 0,26% que separa um sistema que funciona de um que para no meio da
madrugada: é pouco demais para um teste aleatório encontrar, e grande demais para
conviver com ele.

## O que me pegou: a estratégia mais limpa é a única injusta

"Pega os dois garfos de uma vez, ou não pega nenhum" parece a solução óbvia: não
trava, tem o menor grafo (151 estados contra 392) e produz a maior quantidade de
refeição acompanhada. **E é a única das quatro corretas em que um filósofo pode
nunca comer.**

O motivo é exatamente a qualidade dela. Quem pega um garfo e espera pelo outro
está, sem querer, **bloqueando um vizinho** — e esse bloqueio é o que força o
revezamento a chegar em todo mundo. Quem não segura nada não atrapalha ninguém, e
os dois vizinhos podem se revezar para sempre sem nunca deixar os dois garfos
livres ao mesmo tempo.

Na hierarquia há **43 estados** em que um filósofo está parado com um garfo na
mão. Na atômica, **nenhum**. Essa diferença é a inanição inteira.

Com três filósofos a atômica também não tem fome: não há como dois vizinhos se
revezarem sem passar pelo terceiro. O defeito nasce na mesa de quatro.

## Quantos comem ao mesmo tempo

Das vezes em que alguém senta para comer, em quantas já havia outro comendo:

| estratégia | 4 lugares | 5 lugares | 6 lugares |
|---|---:|---:|---:|
| hierarquia | 10,0% | 18,2% | 25,7% |
| par e ímpar | 10,5% | 17,9% | 25,4% |
| **pega os dois** | **16,7%** | **25,0%** | **34,1%** |
| garçom | 9,6% | 16,6% | 23,5% |

O máximo comendo junto é sempre **metade da mesa, arredondada para baixo** — 1
com três filósofos, 2 com quatro e cinco, 3 com seis e sete. Isso vem da forma da
mesa, não da estratégia: vizinhos nunca comem juntos.

O garçom é o pior dos dois lados: produz a **menor** quantidade de refeição
acompanhada e tem o **maior** grafo, 31 vezes o da atômica. O lugar a menos que
ele impõe resolve o impasse por construção, mas custa paralelismo e custa estado.

## Tamanho do grafo

| estratégia | 3 | 4 | 5 | 6 |
|---|---:|---:|---:|---:|
| ingênua | 35 | 118 | 392 | 1.297 |
| hierarquia | 35 | 118 | 392 | 1.297 |
| par e ímpar | 38 | 116 | 422 | 1.264 |
| pega os dois | 19 | 55 | 151 | 415 |
| garçom | 100 | 709 | 4.711 | 30.196 |

## O que as medidas me corrigiram

1. **"Pegar os dois garfos de uma vez é a melhor solução."** É a única injusta. A
   propriedade que a torna elegante, não segurar nada enquanto espera, é a mesma
   que permite que os vizinhos revezem para sempre sem ela.
2. **"A hierarquia reduz o espaço de estados, por isso evita o impasse."** Não
   reduz nada: 392 estados nas duas, 1.250 arestas nas duas, 165 refeições nas
   duas. Ela troca 76 estados por outros 76, e só um deles importa.
3. **"O garçom é a solução prática."** É a mais cara em estado (31 vezes a
   atômica) e a que menos produz leitura simultânea. Resolver por construção tem
   preço.
4. **"Com três filósofos a atômica também deixa alguém sem comer."** Não deixa. A
   fome precisa de quatro: com três, qualquer revezamento passa por todo mundo.

## Como está organizado

- `Passo`: a instrução atômica. `PEGA` e `PEGA_DOIS` são instruções diferentes de
  propósito; é essa diferença que decide se a mesa trava.
- `Estrategia`: os cinco laços, como listas de passos. Os garfos de cada filósofo
  são sempre os mesmos dois; o que muda é a **ordem**.
- `Estado`: a mesa inteira, imutável, com igualdade por valor.
- `Maquina`: a transição. Quem tenta pegar um garfo ocupado não gera transição
  nenhuma: não há fila de garfo, e é dessa falta de fila que a inanição nasce.
- `Explorador`: a varredura, o caminho até o impasse e a conta do paralelismo.
- `Inanicao`: componentes fortemente conexos (Tarjan iterativo) no pedaço do
  grafo em que o faminto está parado, mais o teste de justiça.
- `Medidor`: as três tabelas acima, rodando no CI nos três sistemas.

49 testes.
