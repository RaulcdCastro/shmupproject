package com.raul.touhouguelike;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import java.util.Random;
import java.util.Stack;

public class Main extends ApplicationAdapter {

    private ShapeRenderer shape;
    private OrthographicCamera camera;

    //pausar
    private boolean paused = false;
    private boolean gameOver = false;
    private final float gameOverPanelX = 1280 / 2f - 300;
    private final float gameOverPanelY = 960 / 2f - 190;
    private final float gameOverPanelW = 600;
    private final float gameOverPanelH = 380;
    private final float gameOverButtonX = 1280 / 2f - 150;
    private final float gameOverButtonY = 960 / 2f - 92;
    private final float gameOverButtonW = 300;
    private final float gameOverButtonH = 76;

    //play field
    private final float playfieldX = 64;
    private final float playfieldY = 32;

    private final float playfieldWidth = 768;
    private final float playfieldHeight = 896;

    // posição do player
    private float playerX = playfieldX + 180;
    private float playerY = playfieldY + 80;

    // tamanho do player
    private final float playerSize = 30;
    private final float hitboxSize = 8;

    private final float playerDrawWidth = 51;
    private final float playerDrawHeight = 75;

    //hitbox
    private final float hitboxOffsetX = 25;
    private final float hitboxOffsetY = 40;

    //hitbox de coleta
    private final float collectHitboxWidth = 51;
    private final float collectHitboxHeight = 75;

    // velocidade normal
    private final float normalSpeed = 420;

    // velocidade no foco
    private final float focusSpeed = 210;

    // sprite
    private Texture[] idleTextures;
    private Texture[] rightTextures;
    private Texture[] leftTextures;

    private int spriteFrame = 0;
    private float spriteTimer = 0;
    private final float spriteFrameTime = 0.01f;

    private int currentDirection = 0;
    // 0 = parado, 1 = direita, -1 = esquerda

    private int idleFrame = 0;
    private float idleTimer = 0;
    private final float idleFrameTime = 0.10f;

    // vidas, bombas e pontos
    private int lives = 3;
    private int bombs = 3;
    private int score = 0;

    // power
    private int powerPoints = 0;
    private final int maxPowerPoints = 1500;

    // ui
    private Texture uiBackground;

    //sprite tiro
    private Texture reimuShot;
    private Texture enemyTexture;

    private final float bulletDrawWidth = 20;
    private final float bulletDrawHeight = 38;

    // inimigo
    private Array<Enemy> enemies;
    private final int enemyCount = 3;
    private final float enemySize = 56;
    private final float enemyDrawWidth = 120;
    private final float enemyDrawHeight = 56;
    private final float enemySpeed = 80;

    private boolean bombEffect = false;
    private float bombTimer = 0;
    private final float bombDuration = 0.50f;

    private Texture whitePixel;

    // modo foco
    private boolean focusMode = false;

    // tiros
    private Array<Bullet> bullets;
    private final float bulletSpeed = 500;
    private float shootTimer = 0;
    private final float shootCooldown = 0.12f;

    //disparos inimigos
    private Array<EnemyBullet> enemyBullets;

    private final float enemyShotInterval = 1f;

    //power
    private Array<PowerItem> powerItems;
    private final float powerItemFallSpeed = 90;
    private final int enemyPowerReward = 15; // +0.05 Power

    //hud
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    //musica
    private Music backgroundMusic;

    //cartas
    private boolean choosingCard = false;
    private Card[] currentCards = new Card[3];

    private Stack<Card> appliedCards = new Stack<>();
    private Random random = new Random();

    // buffs
    private int shotLines = 1;
    private final int maxShotLines = 3;

    private float fireRateMultiplier = 1.0f;
    private float playerDamageMultiplier = 1.0f;

    private int maxBombs = 3;
    private final float enemyMinHorizontalGap = 135f;

    private void spawnEnemy() {
        float enemyX = findEnemySpawnX();
        Enemy enemy = new Enemy(
            enemyX,
            playfieldY + playfieldHeight - enemySize
        );

        enemy.shotTimer = enemyShotInterval * 0.75f;

        enemies.add(enemy);
    }

    private float findEnemySpawnX() {
        for (int attempt = 0; attempt < 24; attempt++) {
            float candidateX = playfieldX + random.nextFloat() * (playfieldWidth - enemySize);
            boolean tooClose = false;

            for (Enemy enemy : enemies) {
                if (Math.abs(candidateX - enemy.x) < enemyMinHorizontalGap) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                return candidateX;
            }
        }

        return playfieldX + random.nextFloat() * (playfieldWidth - enemySize);
    }

    private void drawFullscreenBackground() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        float textureWidth = uiBackground.getWidth();
        float textureHeight = uiBackground.getHeight();

        float scale = Math.max(
            screenWidth / textureWidth,
            screenHeight / textureHeight
        );

        float drawWidth = textureWidth * scale;
        float drawHeight = textureHeight * scale;

        float drawX = (screenWidth - drawWidth) / 2f;
        float drawY = (screenHeight - drawHeight) / 2f;

        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight));
        batch.begin();
        batch.draw(uiBackground, drawX, drawY, drawWidth, drawHeight);
        batch.end();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void create() {
        shape = new ShapeRenderer();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 960);
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        layout = new GlyphLayout();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        powerItems = new Array<>();

        uiBackground = new Texture("ui_background.png");
        uiBackground.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        reimuShot = new Texture("reimutiro.png");
        enemyTexture = new Texture("inimigo.png");

        reimuShot.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
        enemyTexture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("musica1.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();

        bullets = new Array<>();
        enemyBullets = new Array<>();

        idleTextures = new Texture[8];
        rightTextures = new Texture[8];
        leftTextures = new Texture[8];

        for (int i = 0; i < 8; i++) {
            idleTextures[i] = new Texture("parado" + (i + 1) + ".png");
            rightTextures[i] = new Texture("direita" + (i + 1) + ".png");
            leftTextures[i] = new Texture("esquerda" + (i + 1) + ".png");
        }

        enemies = new Array<>();

        for (int i = 0; i < enemyCount; i++) {
            spawnEnemy();
        }

    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!gameOver) {
                paused = !paused;
            }
        }
        if (!paused && !gameOver) {

            if (choosingCard) {
                handleCardMouseInput();
            } else {

                // ativa modo foco com SHIFT
                focusMode =
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                        Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

                float currentSpeed = focusMode ? focusSpeed : normalSpeed;

                // movimentação
                float moveX = 0;
                float moveY = 0;

                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveX -= 1;
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1;
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) moveY += 1;
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveY -= 1;

                // normalizar diagonal
                float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
                if (length != 0) {
                    moveX /= length;
                    moveY /= length;
                }

                // aplicar velocidade
                playerX += moveX * currentSpeed * delta;
                playerY += moveY * currentSpeed * delta;

                int targetDirection = 0;

                if (moveX > 0) {
                    targetDirection = 1;
                } else if (moveX < 0) {
                    targetDirection = -1;
                }

                //logica movimentacao
                spriteTimer += delta;

                if (spriteTimer >= spriteFrameTime) {
                    spriteTimer = 0;

                    if (targetDirection == 0) {
                        // voltando para parado
                        if (spriteFrame > 0) {
                            spriteFrame--;
                        } else {
                            currentDirection = 0;
                        }
                    } else {
                        if (currentDirection == 0) {
                            currentDirection = targetDirection;
                            spriteFrame = 0;
                        } else if (currentDirection != targetDirection) {
                            // mudando de direita para esquerda ou esquerda para direita
                            if (spriteFrame > 0) {
                                spriteFrame--;
                            } else {
                                currentDirection = targetDirection;
                                spriteFrame = 0;
                            }
                        } else {
                            // indo até o frame 8 e parando nele
                            if (spriteFrame < 7) {
                                spriteFrame++;
                            }
                        }
                    }
                }

                if (currentDirection == 0) {
                    idleTimer += delta;

                    if (idleTimer >= idleFrameTime) {
                        idleTimer = 0;
                        idleFrame++;

                        if (idleFrame >= 8) {
                            idleFrame = 0;
                        }
                    }
                } else {
                    idleFrame = 0;
                    idleTimer = 0;
                }

                // limitar ao playfield
                playerX = Math.max(
                    playfieldX,
                    Math.min(playerX, playfieldX + playfieldWidth - playerDrawWidth)
                );

                playerY = Math.max(
                    playfieldY,
                    Math.min(playerY, playfieldY + playfieldHeight - playerDrawHeight)
                );

                // grid invisível
                playerX = Math.round(playerX);
                playerY = Math.round(playerY);

                // tiro com Z
                shootTimer += delta;

                if (Gdx.input.isKeyPressed(Input.Keys.Z) && shootTimer >= shootCooldown / fireRateMultiplier) {

                    float centerX = playerX + hitboxOffsetX - bulletDrawWidth / 2;
                    float startY = playerY + hitboxOffsetY + 20;

                    if (shotLines == 1) {
                        bullets.add(new Bullet(centerX, startY));
                    } else if (shotLines == 2) {
                        bullets.add(new Bullet(centerX - 12, startY));
                        bullets.add(new Bullet(centerX + 12, startY));
                    } else {
                        bullets.add(new Bullet(centerX, startY));
                        bullets.add(new Bullet(centerX - 20, startY));
                        bullets.add(new Bullet(centerX + 20, startY));
                    }

                    shootTimer = 0;
                }

                //bomba
                if (Gdx.input.isKeyJustPressed(Input.Keys.X) && bombs > 0) {
                    bombs--;

                    score += 100;

                    for (Enemy enemy : enemies) {
                        score += 100;

                        spawnPowerItems(
                            enemy.x + enemySize / 2,
                            enemy.y + enemySize / 2,
                            enemyPowerReward
                        );
                    }

                    enemies.clear();

                    for (int i = 0; i < enemyCount; i++) {
                        spawnEnemy();
                    }

                    enemyBullets.clear();

                    bombEffect = true;
                    bombTimer = bombDuration;
                }

                if (bombEffect) {
                    bombTimer -= delta;

                    if (bombTimer <= 0) {
                        bombEffect = false;
                    }
                }

                // atualizar tiros
                for (int b = bullets.size - 1; b >= 0; b--) {
                    Bullet bullet = bullets.get(b);

                    bullet.y += bulletSpeed * delta;

                    boolean bulletRemoved = false;

                    for (int e = enemies.size - 1; e >= 0; e--) {
                        Enemy enemy = enemies.get(e);

                        if (isCollidingRect(
                            bullet.x, bullet.y, bulletDrawWidth, bulletDrawHeight,
                            enemy.x, enemy.y, enemySize, enemySize
                        )) {
                            score += 100;

                            spawnPowerItems(
                                enemy.x + enemySize / 2,
                                enemy.y + enemySize / 2,
                                enemyPowerReward
                            );

                            enemies.removeIndex(e);
                            spawnEnemy();

                            bullets.removeIndex(b);
                            bulletRemoved = true;
                            break;
                        }
                    }

                    if (!bulletRemoved && bullet.y > playfieldY + playfieldHeight - bulletDrawHeight) {
                        bullets.removeIndex(b);
                    }
                }

                //colisoes
                float hitboxX = playerX + hitboxOffsetX - (hitboxSize / 2);
                float hitboxY = playerY + hitboxOffsetY - (hitboxSize / 2);

                for (Enemy enemy : enemies) {
                    if (isColliding(
                        hitboxX, hitboxY, hitboxSize,
                        enemy.x, enemy.y, enemySize
                    )) {
                        takePlayerHit();

                        break;
                    }
                }

                for (int i = powerItems.size - 1; i >= 0; i--) {
                    PowerItem item = powerItems.get(i);

                    item.y -= powerItemFallSpeed * delta;

                    float collectHitboxX = playerX;
                    float collectHitboxY = playerY;

                    if (isCollidingRect(
                        collectHitboxX, collectHitboxY, collectHitboxWidth, collectHitboxHeight,
                        item.x, item.y, item.size, item.size
                    )) {
                        gainPower(item.value);
                        powerItems.removeIndex(i);
                        continue;
                    }

                    if (item.y < playfieldY - item.size) {
                        powerItems.removeIndex(i);
                    }
                }


                // inimigo descendo
                for (int i = enemies.size - 1; i >= 0; i--) {
                    Enemy enemy = enemies.get(i);

                    enemy.y -= enemySpeed * delta;

                    if (enemy.y <= playfieldY + playfieldHeight - enemySize) {
                        enemy.shotTimer += delta;

                        if (enemy.shotTimer >= enemyShotInterval) {
                            shootRadialPattern(
                                enemy.x + enemySize / 2,
                                enemy.y + enemySize / 2
                            );

                            enemy.shotTimer = 0;
                        }
                    }

                    if (enemy.y < playfieldY - enemySize) {
                        enemies.removeIndex(i);
                        spawnEnemy();
                    }
                }

                for (int i = enemyBullets.size - 1; i >= 0; i--) {

                    EnemyBullet bullet = enemyBullets.get(i);

                    bullet.x += bullet.speedX * delta;
                    bullet.y += bullet.speedY * delta;

                    // colisão bala inimiga + hitbox pequena do player
                    if (isColliding(
                        hitboxX,
                        hitboxY,
                        hitboxSize,
                        bullet.x - bullet.size / 2,
                        bullet.y - bullet.size / 2,
                        bullet.size
                    )) {
                        takePlayerHit();
                        enemyBullets.removeIndex(i);
                        continue;
                    }

                    // remover quando sair do playfield
                    if (
                        bullet.x < playfieldX ||
                            bullet.x > playfieldX + playfieldWidth ||
                            bullet.y < playfieldY ||
                            bullet.y > playfieldY + playfieldHeight
                    ) {
                        enemyBullets.removeIndex(i);
                    }
                }

            }
        }

        // limpar tela
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shape.setProjectionMatrix(camera.combined);

        batch.setProjectionMatrix(camera.combined);
        drawFullscreenBackground();

        shape.begin(ShapeRenderer.ShapeType.Filled);

        // player
        //shape.setColor(Color.WHITE);
        //shape.rect(playerX, playerY, playerSize, playerSize);

        // desenhar tiros
        //shape.setColor(Color.RED);
        //for (Bullet bullet : bullets) {
            //shape.rect(bullet.x, bullet.y, 6, 15);
        //}

        // inimigo
        for (PowerItem item : powerItems) {
            shape.setColor(Color.CYAN);
            shape.rect(item.x, item.y, item.size, item.size);
        }

        //disparos inimigos
        shape.setColor(1f, 0f, 0.8f, 1f);

        for (EnemyBullet bullet : enemyBullets) {
            shape.circle(
                bullet.x,
                bullet.y,
                bullet.size / 2
            );
        }

        shape.end();

        Texture currentSprite;

        if (currentDirection == 1) {
            currentSprite = rightTextures[spriteFrame];
        } else if (currentDirection == -1) {
            currentSprite = leftTextures[spriteFrame];
        } else {
            currentSprite = idleTextures[idleFrame];
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(
            currentSprite,
            playerX,
            playerY,
            51,
            75
        );

        for (Bullet bullet : bullets) {
            batch.draw(
                reimuShot,
                bullet.x,
                bullet.y,
                bulletDrawWidth,
                bulletDrawHeight
            );
        }

        for (Enemy enemy : enemies) {
            batch.draw(
                enemyTexture,
                enemy.x - (enemyDrawWidth - enemySize) / 2f,
                enemy.y - (enemyDrawHeight - enemySize) / 2f,
                enemyDrawWidth,
                enemyDrawHeight
            );
        }

        batch.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.draw(batch, "Score: " + score, 900, 850);
        font.draw(batch, "Vidas: " + lives, 900, 780);
        font.draw(batch, "Bombas: " + bombs, 900, 710);
        font.draw(batch, String.format("Power: %.2f/15.00", powerPoints / 100f), 900, 640);

        batch.end();

        // hitbox no foco
        if (focusMode) {
            float hx = playerX + hitboxOffsetX;
            float hy = playerY + hitboxOffsetY;

            shape.begin(ShapeRenderer.ShapeType.Filled);

            // borda vermelha
            shape.setColor(Color.RED);
            shape.circle(hx, hy, 6);

            // centro branco
            shape.setColor(Color.WHITE);
            shape.circle(hx, hy, 4);

            shape.end();
        }

        if (paused) {
            batch.begin();

            font.draw(
                batch,
                "PAUSADO",
                playfieldX + playfieldWidth / 2 - 80,
                playfieldY + playfieldHeight / 2 + 20
            );

            batch.end();
        }

        if (gameOver) {
            handleGameOverMouseInput();

            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(0f, 0f, 0f, 0.72f);
            shape.rect(0, 0, 1280, 960);

            shape.setColor(0.03f, 0.03f, 0.04f, 1f);
            shape.rect(gameOverPanelX + 10, gameOverPanelY - 8, gameOverPanelW, gameOverPanelH);

            shape.setColor(0.11f, 0.11f, 0.14f, 1f);
            shape.rect(gameOverPanelX, gameOverPanelY, gameOverPanelW, gameOverPanelH);

            shape.setColor(0.82f, 0.14f, 0.18f, 1f);
            shape.rect(gameOverPanelX, gameOverPanelY + gameOverPanelH - 10, gameOverPanelW, 10);

            shape.setColor(0.06f, 0.06f, 0.08f, 1f);
            shape.rect(gameOverPanelX + 16, gameOverPanelY + 16, gameOverPanelW - 32, gameOverPanelH - 32);

            shape.setColor(0.24f, 0.24f, 0.28f, 1f);
            shape.rect(gameOverButtonX + 7, gameOverButtonY - 7, gameOverButtonW, gameOverButtonH);

            if (isMouseOverGameOverButton()) {
                shape.setColor(0.92f, 0.22f, 0.25f, 1f);
            } else {
                shape.setColor(0.76f, 0.12f, 0.16f, 1f);
            }
            shape.rect(gameOverButtonX, gameOverButtonY, gameOverButtonW, gameOverButtonH);

            shape.setColor(1f, 1f, 1f, 0.12f);
            shape.rect(gameOverButtonX + 4, gameOverButtonY + gameOverButtonH - 12, gameOverButtonW - 8, 8);

            shape.setColor(1f, 1f, 1f, 0.88f);
            shape.rectLine(gameOverPanelX, gameOverPanelY, gameOverPanelX + gameOverPanelW, gameOverPanelY, 2.5f);
            shape.rectLine(gameOverPanelX, gameOverPanelY + gameOverPanelH, gameOverPanelX + gameOverPanelW, gameOverPanelY + gameOverPanelH, 2.5f);
            shape.rectLine(gameOverPanelX, gameOverPanelY, gameOverPanelX, gameOverPanelY + gameOverPanelH, 2.5f);
            shape.rectLine(gameOverPanelX + gameOverPanelW, gameOverPanelY, gameOverPanelX + gameOverPanelW, gameOverPanelY + gameOverPanelH, 2.5f);
            shape.rectLine(gameOverButtonX, gameOverButtonY, gameOverButtonX + gameOverButtonW, gameOverButtonY, 2f);
            shape.rectLine(gameOverButtonX, gameOverButtonY + gameOverButtonH, gameOverButtonX + gameOverButtonW, gameOverButtonY + gameOverButtonH, 2f);
            shape.rectLine(gameOverButtonX, gameOverButtonY, gameOverButtonX, gameOverButtonY + gameOverButtonH, 2f);
            shape.rectLine(gameOverButtonX + gameOverButtonW, gameOverButtonY, gameOverButtonX + gameOverButtonW, gameOverButtonY + gameOverButtonH, 2f);
            shape.end();

            batch.begin();

            font.setColor(0.95f, 0.2f, 0.24f, 1f);
            font.getData().setScale(3.4f);
            layout.setText(font, "GAME OVER");
            font.draw(
                batch,
                layout,
                1280 / 2f - layout.width / 2f,
                960 / 2f + 110
            );

            font.setColor(Color.WHITE);
            font.getData().setScale(1.25f);
            layout.setText(font, "REINICIAR");
            font.draw(
                batch,
                layout,
                gameOverButtonX + gameOverButtonW / 2f - layout.width / 2f,
                gameOverButtonY + 50
            );

            font.getData().setScale(2f);
            font.setColor(Color.WHITE);
            batch.end();
        }

        if (choosingCard) {
            float panelX = playfieldX + 35;
            float panelY = playfieldY + 180;
            float panelW = playfieldWidth - 70;
            float panelH = 620;

            float cardY = playfieldY + 260;
            float cardW = 190;
            float cardH = 330;

            float card1X = playfieldX + 75;
            float card2X = playfieldX + 290;
            float card3X = playfieldX + 505;

            shape.begin(ShapeRenderer.ShapeType.Filled);

            shape.setColor(Color.DARK_GRAY);
            shape.rect(panelX, panelY, panelW, panelH);

            shape.setColor(Color.WHITE);
            shape.rect(card1X, cardY, cardW, cardH);
            shape.rect(card2X, cardY, cardW, cardH);
            shape.rect(card3X, cardY, cardW, cardH);

            shape.end();

            batch.begin();

            font.setColor(Color.WHITE);
            font.getData().setScale(1.4f);

// título centralizado
            font.draw(
                batch,
                "ESCOLHA UMA CARTA",
                playfieldX + playfieldWidth / 2 - 170,
                playfieldY + 760
            );

            // texto preto nas cartas
            font.setColor(Color.BLACK);

            // -------- CARTA 1 --------

            font.getData().setScale(1.2f);

            // título centralizado
            font.draw(
                batch,
                currentCards[0].name,
                card1X + cardW / 2 - currentCards[0].name.length() * 7,
                cardY + 270
            );

            font.getData().setScale(0.75f);

            // descrição
            font.draw(
                batch,
                currentCards[0].description,
                card1X + 12,
                cardY + 190,
                cardW - 24,
                1,
                true
            );

            // -------- CARTA 2 --------

            font.getData().setScale(1.2f);

            font.draw(
                batch,
                currentCards[1].name,
                card2X + cardW / 2 - currentCards[1].name.length() * 7,
                cardY + 270
            );

            font.getData().setScale(0.75f);

            font.draw(
                batch,
                currentCards[1].description,
                card2X + 12,
                cardY + 190,
                cardW - 24,
                1,
                true
            );

            // -------- CARTA 3 --------

            font.getData().setScale(1.2f);

            font.draw(
                batch,
                currentCards[2].name,
                card3X + cardW / 2 - currentCards[2].name.length() * 7,
                cardY + 270
            );

            font.getData().setScale(0.75f);

            font.draw(
                batch,
                currentCards[2].description,
                card3X + 12,
                cardY + 190,
                cardW - 24,
                1,
                true
            );

            // reset
            font.setColor(Color.WHITE);
            font.getData().setScale(2f);

            batch.end();
        }

        if (bombEffect) {
            float progress = bombTimer / bombDuration;
            float alpha = progress * progress;

            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(
                whitePixel,
                playfieldX,
                playfieldY,
                playfieldWidth,
                playfieldHeight
            );
            batch.setColor(Color.WHITE);

            batch.end();
        }

    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();
        backgroundMusic.dispose();
        uiBackground.dispose();
        reimuShot.dispose();
        enemyTexture.dispose();
        whitePixel.dispose();

        for (int i = 0; i < 8; i++) {
            idleTextures[i].dispose();
            rightTextures[i].dispose();
            leftTextures[i].dispose();
        }

    }

    // classe do tiro
    class Bullet {
        float x;
        float y;

        public Bullet(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    class EnemyBullet {
        float x;
        float y;

        float speedX;
        float speedY;

        float size;

        public EnemyBullet(
            float x,
            float y,
            float speedX,
            float speedY,
            float size
        ) {
            this.x = x;
            this.y = y;

            this.speedX = speedX;
            this.speedY = speedY;

            this.size = size;
        }
    }

    class Enemy {
        float x, y;
        float shotTimer = 0;

        Enemy(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    class PowerItem {
        float x;
        float y;
        int value;
        float size;

        public PowerItem(float x, float y, int value, float size) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.size = size;
        }
    }


    private boolean isColliding(
        float x1, float y1, float size1,
        float x2, float y2, float size2) {

        return x1 < x2 + size2 &&
            x1 + size1 > x2 &&
            y1 < y2 + size2 &&
            y1 + size1 > y2;
    }

    private void gainPower(int amount) {
        int oldPowerLevel = powerPoints / 100;

        powerPoints += amount;

        if (powerPoints > maxPowerPoints) {
            powerPoints = maxPowerPoints;
        }

        int newPowerLevel = powerPoints / 100;

        if (newPowerLevel > oldPowerLevel) {
            openCardSelection();
            // Exemplo:
            // choosingCard = true;
            // paused = true;
        }
    }

    private void spawnPowerItems(float x, float y, int totalPower) {
        while (totalPower >= 5) {
            powerItems.add(new PowerItem(
                x + (float)(Math.random() * 30 - 15),
                y + (float)(Math.random() * 30 - 15),
                5,
                16
            ));
            totalPower -= 5;
        }

        while (totalPower > 0) {
            powerItems.add(new PowerItem(
                x + (float)(Math.random() * 30 - 15),
                y + (float)(Math.random() * 30 - 15),
                1,
                10
            ));
            totalPower--;
        }
    }

    private boolean isCollidingRect(
        float x1, float y1, float width1, float height1,
        float x2, float y2, float width2, float height2) {

        return x1 < x2 + width2 &&
            x1 + width1 > x2 &&
            y1 < y2 + height2 &&
            y1 + height1 > y2;
    }

    private void shootRadialPattern(float centerX, float centerY) {

        float speed = 100f;

        for (int i = 0; i < 8; i++) {

            float angle = (float)Math.toRadians(i * 45);

            float speedX = (float)Math.cos(angle) * speed;
            float speedY = (float)Math.sin(angle) * speed;

            enemyBullets.add(new EnemyBullet(
                centerX,
                centerY,
                speedX,
                speedY,
                14
            ));
        }
    }

    enum CardType {
        EXTRA_SHOT,
        FIRE_RATE,
        DAMAGE,
        EXTRA_BOMB
    }

    class Card {
        String name;
        String description;
        CardType type;

        public Card(String name, String description, CardType type) {
            this.name = name;
            this.description = description;
            this.type = type;
        }
    }

    private void openCardSelection() {
        choosingCard = true;

        currentCards[0] = generateRandomCard();
        currentCards[1] = generateRandomCard();
        currentCards[2] = generateRandomCard();
    }

    private Card generateRandomCard() {
        int option = random.nextInt(4);

        switch (option) {
            case 0:
                return new Card(
                    "Tiro Extra",
                    "Adiciona mais uma linha de tiro. Max: 3",
                    CardType.EXTRA_SHOT
                );

            case 1:
                return new Card(
                    "Cadencia +",
                    "Aumenta a velocidade dos disparos",
                    CardType.FIRE_RATE
                );

            case 2:
                return new Card(
                    "Dano +",
                    "Aumenta o dano dos disparos",
                    CardType.DAMAGE
                );

            default:
                return new Card(
                    "Bomba +",
                    "Aumenta o limite de bombas",
                    CardType.EXTRA_BOMB
                );
        }
    }

    private void chooseCard(int index) {
        Card chosen = currentCards[index];

        applyCard(chosen);
        appliedCards.push(chosen);

        choosingCard = false;
    }

    private void applyCard(Card card) {
        switch (card.type) {
            case EXTRA_SHOT:
                if (shotLines < maxShotLines) {
                    shotLines++;
                }
                break;

            case FIRE_RATE:
                fireRateMultiplier += 0.15f;
                break;

            case DAMAGE:
                playerDamageMultiplier += 0.20f;
                break;

            case EXTRA_BOMB:
                maxBombs++;
                bombs++;
                break;
        }
    }

    private void removeCard(Card card) {
        switch (card.type) {
            case EXTRA_SHOT:
                if (shotLines > 1) {
                    shotLines--;
                }
                break;

            case FIRE_RATE:
                fireRateMultiplier -= 0.15f;
                if (fireRateMultiplier < 1.0f) {
                    fireRateMultiplier = 1.0f;
                }
                break;

            case DAMAGE:
                playerDamageMultiplier -= 0.20f;
                if (playerDamageMultiplier < 1.0f) {
                    playerDamageMultiplier = 1.0f;
                }
                break;

            case EXTRA_BOMB:
                maxBombs--;
                if (maxBombs < 3) {
                    maxBombs = 3;
                }

                if (bombs > maxBombs) {
                    bombs = maxBombs;
                }
                break;
        }
    }

    private void losePowerOnDeath() {
        for (int i = 0; i < 2; i++) {
            if (!appliedCards.empty()) {
                Card lastCard = appliedCards.pop();
                removeCard(lastCard);
            }
        }

        powerPoints -= 200;

        if (powerPoints < 0) {
            powerPoints = 0;
        }
    }

    private void takePlayerHit() {
        if (gameOver) {
            return;
        }

        lives--;
        losePowerOnDeath();

        if (lives <= 0) {
            lives = 0;
            gameOver = true;
            paused = true;
            return;
        }

        if (bombs < 3) {
            bombs = 3;
        }

        playerX = playfieldX + playfieldWidth / 2 - playerDrawWidth / 2;
        playerY = playfieldY + 80;
    }

    private void resetGame() {
        paused = false;
        gameOver = false;
        choosingCard = false;
        bombEffect = false;
        focusMode = false;

        lives = 3;
        bombs = 3;
        score = 0;
        powerPoints = 0;

        shotLines = 1;
        fireRateMultiplier = 1.0f;
        playerDamageMultiplier = 1.0f;
        maxBombs = 3;

        spriteFrame = 0;
        spriteTimer = 0;
        currentDirection = 0;
        idleFrame = 0;
        idleTimer = 0;
        shootTimer = 0;
        bombTimer = 0;

        playerX = playfieldX + 180;
        playerY = playfieldY + 80;

        bullets.clear();
        enemyBullets.clear();
        powerItems.clear();
        enemies.clear();
        appliedCards.clear();

        for (int i = 0; i < enemyCount; i++) {
            spawnEnemy();
        }
    }

    private void handleGameOverMouseInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            resetGame();
            return;
        }

        if (!Gdx.input.justTouched()) {
            return;
        }

        if (isMouseOverGameOverButton()) {
            resetGame();
        }
    }

    private boolean isMouseOverGameOverButton() {
        float mouseX = Gdx.input.getX();
        float mouseY = 960 - Gdx.input.getY();

        return isCollidingRect(
            mouseX,
            mouseY,
            1,
            1,
            gameOverButtonX,
            gameOverButtonY,
            gameOverButtonW,
            gameOverButtonH
        );
    }

    private void handleCardMouseInput() {
        if (!Gdx.input.justTouched()) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = 960 - Gdx.input.getY();

        float cardY = playfieldY + 260;
        float cardW = 190;
        float cardH = 330;

        float card1X = playfieldX + 75;
        float card2X = playfieldX + 290;
        float card3X = playfieldX + 505;

        if (isCollidingRect(mouseX, mouseY, 1, 1, card1X, cardY, cardW, cardH)) {
            chooseCard(0);
        } else if (isCollidingRect(mouseX, mouseY, 1, 1, card2X, cardY, cardW, cardH)) {
            chooseCard(1);
        } else if (isCollidingRect(mouseX, mouseY, 1, 1, card3X, cardY, cardW, cardH)) {
            chooseCard(2);
        }
    }

}
