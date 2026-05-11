TouhouGuelike

Projeto desenvolvido com Java e LibGDX inspirado no estilo *Bullet Hell / Shoot 'em Up (SHMUP)* da franquia Touhou. O jogo apresenta movimentação fluida, sistema de tiros, bombas, colisões, HUD, música de fundo e animações do personagem.

📖 Sobre o Projeto

O objetivo do projeto é criar um jogo 2D no estilo arcade utilizando a framework LibGDX. O jogador controla uma personagem dentro de uma área limitada do mapa, desviando de inimigos enquanto dispara projéteis para aumentar a pontuação.

O projeto foi desenvolvido com foco em:

* Programação de jogos em Java
* Utilização da framework LibGDX
* Renderização gráfica 2D
* Sistema de colisão
* Controle de movimentação e animações
* Gerenciamento de assets e áudio

🎮 Funcionalidades

* Movimentação do personagem
* Sistema de tiro contínuo
* Sistema de bombas
* Sistema de vidas
* Pontuação (Score)
* Pausa do jogo
* Música de fundo
* Animação do personagem
* Colisão entre jogador, tiros e inimigos
* HUD com informações do jogador

🕹️ Controles

| Tecla    | Ação         |
| -------- | ------------ |
| ⬅️➡️⬆️⬇️ | Movimentação |
| Z        | Atirar       |
| X        | Usar bomba   |
| SHIFT    | Modo foco    |
| ESC      | Pausar jogo  |

🛠️ Tecnologias Utilizadas

* Java
* LibGDX
* Gradle
* LWJGL3

📂 Estrutura do Projeto

```bash
shmupproject/
│
├── assets/                # Imagens, sprites e músicas
├── core/                  # Lógica principal do jogo
├── lwjgl3/                # Inicialização da aplicação desktop
├── gradle/                # Configurações do Gradle
├── build.gradle
├── settings.gradle
└── README.md
```

▶️ Como Executar

Pré-requisitos

Antes de executar o projeto, instale:

* Java JDK 17 ou superior
* Gradle (opcional, pois o wrapper já está incluído)
* IntelliJ IDEA ou outra IDE Java

Executando pelo terminal

Windows

```bash
./gradlew lwjgl3:run
```

ou

```bash
gradlew.bat lwjgl3:run
```

Linux / macOS

```bash
chmod +x gradlew
./gradlew lwjgl3:run
```

---

📸 Recursos do Jogo

 Player

O jogador utiliza sprites animados armazenados na pasta `assets/`:

* parado1.png
* parado2.png
* parado3.png
* ...
* parado8.png

As animações são reproduzidas utilizando `Animation<TextureRegion>` da LibGDX.

 Sistema de Tiros

Os projéteis são armazenados em um `Array<Bullet>` e atualizados continuamente durante o loop do jogo.

 Sistema de Bombas

Ao pressionar `X`, o jogador:

* Consome uma bomba
* Recebe pontos extras
* Reinicia a posição do inimigo
* Ativa um efeito visual na tela

 Sistema de Colisão

O projeto utiliza colisão baseada em retângulos (*AABB Collision Detection*).

🎵 Áudio

A música de fundo utilizada é:

```bash
assets/musica1.mp3
```

Gerenciada pela classe:

```java
Music backgroundMusic;
```

🧠 Conceitos Utilizados

* Game Loop
* Renderização 2D
* Delta Time
* Animações
* Programação Orientada a Objetos
* Gerenciamento de memória
* Entrada do teclado
* Sistema de HUD

 🚀 Melhorias Futuras

* Adicionar múltiplos inimigos
* Implementar chefes (Bosses)
* Sistema de fases
* Menu inicial
* Tela de Game Over
* Efeitos sonoros
* Power-ups
* Diferentes tipos de tiros
* Sistema de ranking
* Suporte a joystick

 👨‍💻 Autor:
 Charles M Garay, Erick Oliveira, Raul Castro

Projeto desenvolvido para estudos e prática de desenvolvimento de jogos utilizando Java e LibGDX.

📄 Licença

Este projeto é destinado para fins educacionais.
