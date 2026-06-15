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
import java.util.Random;
import java.util.Stack;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Main extends ApplicationAdapter {

    private ShapeRenderer shape;
    private OrthographicCamera camera;

    //pausar
    private boolean paused = false;

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

    // fada textura
    private Texture fairyFrame1;
    private Texture fairyFrame2;
    private Texture fairyFrame3;
    private Texture fairyFrame4;

    // vidas, bombas e pontos
    private int lives = 3;
    private int bombs = 3;
    private int score = 0;

    private final float hudX = playfieldX + playfieldWidth + 30;

    private int highScore = 0;

    // carta
    private Texture cardTexture;

    // estrelas
    private Texture starFull;
    private Texture starEmpty;

    // power
    private int powerPoints = 0;
    private final int maxPowerPoints = 1500;

    // ui
    private Texture uiBackground;

    //sprite tiro
    private Texture reimuShot;

    private final float bulletDrawWidth = 20;
    private final float bulletDrawHeight = 38;

    // inimigo
    private Array<Enemy> enemies;
    private final int enemyCount = 3;
    private final float enemySize = 84;
    private final float enemySpeed = 80;

    // fada animacao
    private Animation<TextureRegion> fairyAnimation;
    private float fairyAnimationTime = 0f;

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
    private final int enemyPowerReward = 5; // +0.05 Power

    private Texture powerPequeno;

    //hud
    private SpriteBatch batch;
    private BitmapFont font;

    //background
    private Animation<TextureRegion> backgroundAnimation;
    private float backgroundTime = 0f;

    //musica
    private Music backgroundMusic;

    //cartas
    private boolean choosingCard = false;
    private Card[] currentCards = new Card[3];

    private Stack<Card> appliedCards = new Stack<>();
    private Random random = new Random();

    // buffs
    private int sideShotLevel = 0;   // 0 até 2
    private int homingShotLevel = 0; // 0 até 2
    private Texture homingShot;

    private float fireRateMultiplier = 1.0f;
    private float playerDamageMultiplier = 1.0f;

    private int maxBombs = 3;

    private void spawnEnemy() {
        Enemy enemy = new Enemy(
            playfieldX + (float)(Math.random() * (playfieldWidth - enemySize)),
            playfieldY + playfieldHeight - enemySize
        );

        enemy.shotTimer = enemyShotInterval * 0.75f;

        enemies.add(enemy);
    }

    @Override
    public void create() {
        shape = new ShapeRenderer();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 960);
        homingShot = new Texture("guiado.png");
        homingShot.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        fairyFrame1 = new Texture("fada_movendo01.png");
        fairyFrame2 = new Texture("fada_movendo02.png");
        fairyFrame3 = new Texture("fada_movendo03.png");
        fairyFrame4 = new Texture("fada_movendo04.png");

        cardTexture = new Texture("card.png");

        cardTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        batch = new SpriteBatch();

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Bauhaus.ttf")
            );

        FreeTypeFontParameter parameter =
            new FreeTypeFontParameter();

        parameter.size = 16;

        font = generator.generateFont(parameter);

        font.getData().setScale(2);

        generator.dispose();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        powerItems = new Array<>();

        uiBackground = new Texture("ui_background.png");

        reimuShot = new Texture("reimutiro.png");

        reimuShot.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
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

        starFull = new Texture("star_full.png");
        starEmpty = new Texture("star_empty.png");

        starFull.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        starEmpty.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        Array<TextureRegion> backgroundFrames = new Array<>();

        for (int i = 1; i <= 165; i++) {

            String fileName =
                String.format("background/frame_%03d.png", i);

            Texture texture = new Texture(fileName);

            texture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
            );

            backgroundFrames.add(
                new TextureRegion(texture)
            );
        }

        backgroundAnimation =
            new Animation<>(
                1f / 60f,
                backgroundFrames,
                Animation.PlayMode.LOOP
            );

        fairyAnimation = new Animation<>(
            0.12f,
            new TextureRegion(fairyFrame1),
            new TextureRegion(fairyFrame2),
            new TextureRegion(fairyFrame3),
            new TextureRegion(fairyFrame4)
        );

        fairyAnimation.setPlayMode(Animation.PlayMode.LOOP);

        powerPequeno = new Texture("power_pequeno.png");
        powerPequeno.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

    }

    @Override
    public void render() {

        if (!paused && !choosingCard) {
            backgroundTime += Gdx.graphics.getDeltaTime();
        }

        if (!paused && !choosingCard) {
            fairyAnimationTime += Gdx.graphics.getDeltaTime();
        }

        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }
        if (!paused) {

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

                    // tiro central
                    bullets.add(new Bullet(centerX, startY, 0, bulletSpeed, false));

                    // tiros laterais nível 1
                    if (sideShotLevel >= 1) {
                        bullets.add(new Bullet(centerX - 18, startY, -90, bulletSpeed, false));
                        bullets.add(new Bullet(centerX + 18, startY, 90, bulletSpeed, false));
                    }

                    // tiros laterais nível 2
                    if (sideShotLevel >= 2) {
                        bullets.add(new Bullet(centerX - 35, startY, -150, bulletSpeed * 0.95f, false));
                        bullets.add(new Bullet(centerX + 35, startY, 150, bulletSpeed * 0.95f, false));
                    }

                    // teleguiados nível 1
                    if (homingShotLevel >= 1) {
                        bullets.add(new Bullet(centerX - 50, startY, -120, bulletSpeed * 0.75f, true));
                        bullets.add(new Bullet(centerX + 50, startY, 120, bulletSpeed * 0.75f, true));
                    }

                    // teleguiados nível 2
                    if (homingShotLevel >= 2) {
                        bullets.add(new Bullet(centerX - 70, startY, -180, bulletSpeed * 0.7f, true));
                        bullets.add(new Bullet(centerX + 70, startY, 180, bulletSpeed * 0.7f, true));
                    }

                    shootTimer = 0;
                }

                if (score > highScore) {
                    highScore = score;
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

                    if (bullet.homing) {
                        Enemy target = findClosestEnemy(bullet.x, bullet.y);

                        if (target != null) {
                            float targetX = target.x + enemySize / 2;
                            float targetY = target.y + enemySize / 2;

                            float dx = targetX - bullet.x;
                            float dy = targetY - bullet.y;

                            float homingLength = (float)Math.sqrt(dx * dx + dy * dy);

                            if (homingLength != 0) {
                                dx /= homingLength;
                                dy /= homingLength;

                                float homingSpeed = bulletSpeed * 0.8f;
                                bullet.speedX = dx * homingSpeed;
                                bullet.speedY = dy * homingSpeed;
                            }
                        }
                    }

                    bullet.x += bullet.speedX * delta;
                    bullet.y += bullet.speedY * delta;

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

                    if (!bulletRemoved && (
                        bullet.y > playfieldY + playfieldHeight - bulletDrawHeight ||
                            bullet.x < playfieldX - bulletDrawWidth ||
                            bullet.x > playfieldX + playfieldWidth
                    )) {
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
                        lives--;
                        losePowerOnDeath();

                        if (bombs < 3) {
                            bombs = 3;
                        }

                        playerX = playfieldX + playfieldWidth / 2 - playerDrawWidth / 2;
                        playerY = playfieldY + 80;

                        break;
                    }
                }

                for (int i = powerItems.size - 1; i >= 0; i--) {
                    PowerItem item = powerItems.get(i);

                    item.velocityY -= 150f * delta;
                    item.y += item.velocityY * delta;

                    float maxY = playfieldY + playfieldHeight - item.size;

                    if (item.y > maxY) {
                        item.y = maxY;

                        if (item.velocityY > 0) {
                            item.velocityY = 0;
                        }
                    }

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
                        lives--;
                        losePowerOnDeath();

                        if (bombs < 3) {
                            bombs = 3;
                        }

                        playerX = playfieldX + playfieldWidth / 2 - playerDrawWidth / 2;
                        playerY = playfieldY + 80;

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
        batch.begin();

        batch.draw(uiBackground, 0, 0, 1280, 960);

        TextureRegion currentFrame =
            backgroundAnimation.getKeyFrame(backgroundTime);

        batch.draw(
            currentFrame,
            playfieldX,
            playfieldY,
            playfieldWidth,
            playfieldHeight
        );

        // fadas

        TextureRegion currentFairyFrame =
            fairyAnimation.getKeyFrame(fairyAnimationTime);

        for (Enemy enemy : enemies) {

            batch.draw(
                currentFairyFrame,
                enemy.x,
                enemy.y + 18,
                enemySize,
                enemySize
            );
        }

        for (PowerItem item : powerItems) {

            batch.draw(
                powerPequeno,
                item.x,
                item.y,
                item.size,
                item.size = 26
            );
        }

        batch.end();

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
        //  shape.setColor(Color.YELLOW);

        //  for (Enemy enemy : enemies) {
        //      shape.rect(enemy.x, enemy.y, enemySize, enemySize);
        //  }


        // for (PowerItem item : powerItems) {
        //    shape.setColor(Color.CYAN);
        //    shape.rect(item.x, item.y, item.size, item.size);
        //    }

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
            Texture shotTexture = bullet.homing ? homingShot : reimuShot;

            batch.draw(
                shotTexture,
                bullet.x,
                bullet.y,
                bulletDrawWidth,
                bulletDrawHeight
            );
        }

        batch.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        String scoreText = String.format("%09d", score);
        String highScoreText = String.format("%09d", highScore);

        font.draw(batch, "HiScore", hudX, 880);
        font.draw(batch, highScoreText, hudX + 150, 880);

        font.draw(batch, "Score", hudX, 830);
        font.draw(batch, scoreText, hudX + 150, 830);

        font.draw(batch, "Player", hudX, 730);

        for (int i = 0; i < 8; i++) {

            Texture star =
                (i < lives)
                    ? starFull
                    : starEmpty;

            batch.draw(
                star,
                hudX + 150 + i * 28,
                707,
                24,
                24
            );
        }

        font.draw(batch, "Bomb", hudX, 670);

        for (int i = 0; i < 8; i++) {

            Texture star =
                (i < bombs)
                    ? starFull
                    : starEmpty;

            batch.draw(
                star,
                hudX + 150 + i * 28,
                647,
                24,
                24
            );
        }

        String powerText =
            String.format("%.2f / 15.00", powerPoints / 100f);

        font.draw(batch, "Power", hudX, 580);
        font.draw(batch, powerText, hudX + 150, 580);

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
                "PAUSED",
                playfieldX + playfieldWidth / 2 - 80,
                playfieldY + playfieldHeight / 2 + 20
            );

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

            Gdx.gl.glEnable(GL20.GL_BLEND);

            shape.setColor(0f, 0f, 0f, 0.75f);

            shape.rect(
                playfieldX,
                playfieldY,
                playfieldWidth,
                playfieldHeight
            );

            shape.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();

            batch.draw(cardTexture, card1X, cardY, cardW, cardH);
            batch.draw(cardTexture, card2X, cardY, cardW, cardH);
            batch.draw(cardTexture, card3X, cardY, cardW, cardH);

            font.setColor(Color.WHITE);
            font.getData().setScale(2.3f);

            // título centralizado
            GlyphLayout titleLayout =
                new GlyphLayout(font, "ESCOLHA UMA CARTA");

            font.draw(
                batch,
                "ESCOLHA UMA CARTA",
                playfieldX + playfieldWidth / 2f - titleLayout.width / 2f,
                playfieldY + 800
            );

            // texto preto nas cartas
            font.setColor(Color.WHITE);

            // -------- CARTA 1 --------

            font.getData().setScale(1.2f);

            // título centralizado
            GlyphLayout layout =
                new GlyphLayout(font, currentCards[0].name);

            font.draw(
                batch,
                currentCards[0].name,
                card1X + cardW / 2f - layout.width / 2f,
                cardY + cardH - 34
            );

            font.getData().setScale(1.00f);

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

            GlyphLayout layout2 =
                new GlyphLayout(font, currentCards[1].name);

            font.draw(
                batch,
                currentCards[1].name,
                card2X + cardW / 2f - layout2.width / 2f,
                cardY + cardH - 34
            );

            font.getData().setScale(1.00f);

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

            GlyphLayout layout3 =
                new GlyphLayout(font, currentCards[2].name);

            font.draw(
                batch,
                currentCards[2].name,
                card3X + cardW / 2f - layout3.width / 2f,
                cardY + cardH - 34
            );

            font.getData().setScale(1.00f);

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
        whitePixel.dispose();
        homingShot.dispose();
        starFull.dispose();
        starEmpty.dispose();
        powerPequeno.dispose();

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
        float speedX;
        float speedY;
        boolean homing;

        public Bullet(float x, float y, float speedX, float speedY, boolean homing) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.homing = homing;
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
        float velocityY;

        public PowerItem(float x, float y, int value, float size) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.size = size;

            this.velocityY = 130f;
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
        SIDE_SHOT,
        HOMING_SHOT,
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
        int option = random.nextInt(8);

        if (option == 0 && sideShotLevel < 2) {
            return new Card(
                "Tiro Lateral",
                "Adiciona 2 disparos laterais.",
                CardType.SIDE_SHOT
            );
        }

        if (option == 1 && homingShotLevel < 2) {
            return new Card(
                "Tiro Teleguiado",
                "Adiciona 2 disparos que seguem inimigos.",
                CardType.HOMING_SHOT
            );
        }

        option = random.nextInt(3);

        switch (option) {
            case 0:
                return new Card("Cadencia +", "Aumenta a velocidade dos disparos.", CardType.FIRE_RATE);
            case 1:
                return new Card("Dano +", "Aumenta o dano dos disparos.", CardType.DAMAGE);
            default:
                return new Card("Bomba +", "Aumenta o limite de bombas.", CardType.EXTRA_BOMB);
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
            case SIDE_SHOT:
                if (sideShotLevel < 2) sideShotLevel++;
                break;

            case HOMING_SHOT:
                if (homingShotLevel < 2) homingShotLevel++;
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
            case SIDE_SHOT:
                if (sideShotLevel > 0) sideShotLevel--;
                break;

            case HOMING_SHOT:
                if (homingShotLevel > 0) homingShotLevel--;
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

    private Enemy findClosestEnemy(float x, float y) {
        Enemy closest = null;
        float closestDistance = Float.MAX_VALUE;

        for (Enemy enemy : enemies) {
            float dx = enemy.x - x;
            float dy = enemy.y - y;
            float distance = dx * dx + dy * dy;

            if (distance < closestDistance) {
                closestDistance = distance;
                closest = enemy;
            }
        }

        return closest;
    }

}
