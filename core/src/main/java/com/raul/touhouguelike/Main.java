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

    private Texture focusModeEffect;
    private float focusEffectRotation = 0f;

    private int idleFrame = 0;
    private float idleTimer = 0;
    private final float idleFrameTime = 0.10f;

    private boolean playerDead = false;
    private boolean playerInvincible = false;

    private float respawnTimer = 0f;
    private float invincibleTimer = 0f;

    private final float respawnDelay = 1f;
    private final float invincibleDuration = 3f;

    // fada textura
    private Texture fairyFrame1;
    private Texture fairyFrame2;
    private Texture fairyFrame3;
    private Texture fairyFrame4;

    private Texture whiteFairyFrame1;
    private Texture whiteFairyFrame2;
    private Texture whiteFairyFrame3;
    private Texture whiteFairyFrame4;

    private Texture redFairyFrame1;
    private Texture redFairyFrame2;
    private Texture redFairyFrame3;
    private Texture redFairyFrame4;

    private Animation<TextureRegion> whiteFairyAnimation;
    private Animation<TextureRegion> redFairyAnimation;

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


    // pattern
    private float stageTimer = 0f;
    private boolean spawnedFirstRed = false;
    private boolean spawnedSecondRed = false;

    private final float secondRedSpawnTime = 7f;

    private final float redStopY = playfieldY + playfieldHeight - 320;

    private WavePattern currentPattern = WavePattern.RED_FAIRY_DUAL;

    private boolean pattern2Started = false;
    private float pattern2Timer = 0f;
    private int pattern2SpawnCount = 0;

    // pattern 3
    private boolean pattern3Started = false;
    private float pattern3Timer = 0f;
    private int pattern3LeftCount = 0;
    private int pattern3RightCount = 0;
    private boolean pattern3RedSpawned = false;
    private boolean pattern3BurstSpawned = false;


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
    private Texture powerGrande;

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
    private float playerDamage = 1.0f;

    private int maxBombs = 3;

    private void spawnEnemy(EnemyType type, float x, float y) {
        spawnEnemy(type, EnemyMovementType.STOP_AT_TARGET, x, y);
    }

    private void spawnEnemy(
        EnemyType type,
        EnemyMovementType movementType,
        float x,
        float y
    ) {
        Enemy enemy = new Enemy(x, y, type);

        enemy.movementType = movementType;

        enemy.startX = x;
        enemy.startY = y;

        switch (type) {

            case WHITE:

                enemy.shotTimer = 1.05f;
                break;

            case RED_BURST:

                enemy.shotTimer = enemy.shotInterval * 0.85f;
                break;

            default:
                enemy.shotTimer = enemy.shotInterval * 0.75f;
                break;
        }

        enemy.targetY = redStopY;

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

        focusModeEffect = new Texture("focusmodeeffect.png");
        focusModeEffect.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        playerX = playfieldX + playfieldWidth / 2f - playerDrawWidth / 2f;

        fairyFrame1 = new Texture("fada_movendo01.png");
        fairyFrame2 = new Texture("fada_movendo02.png");
        fairyFrame3 = new Texture("fada_movendo03.png");
        fairyFrame4 = new Texture("fada_movendo04.png");

        whiteFairyFrame1 = new Texture("fadabranca01.png");
        whiteFairyFrame2 = new Texture("fadabranca02.png");
        whiteFairyFrame3 = new Texture("fadabranca03.png");
        whiteFairyFrame4 = new Texture("fadabranca04.png");

        redFairyFrame1 = new Texture("fadavermelha01.png");
        redFairyFrame2 = new Texture("fadavermelha02.png");
        redFairyFrame3 = new Texture("fadavermelha03.png");
        redFairyFrame4 = new Texture("fadavermelha04.png");

        powerGrande = new Texture("power_grande.png");
        powerGrande.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

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

        //for (int i = 0; i < enemyCount; i++) {
        //    spawnEnemy();
        //}

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

        whiteFairyAnimation = new Animation<>(
            0.12f,
            new TextureRegion(whiteFairyFrame1),
            new TextureRegion(whiteFairyFrame2),
            new TextureRegion(whiteFairyFrame3),
            new TextureRegion(whiteFairyFrame4)
        );
        whiteFairyAnimation.setPlayMode(Animation.PlayMode.LOOP);

        redFairyAnimation = new Animation<>(
            0.12f,
            new TextureRegion(redFairyFrame1),
            new TextureRegion(redFairyFrame2),
            new TextureRegion(redFairyFrame3),
            new TextureRegion(redFairyFrame4)
        );
        redFairyAnimation.setPlayMode(Animation.PlayMode.LOOP);

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

                    float normalDamage = playerDamage;
                    float homingDamage = playerDamage / 2f;

                    // tiro central
                    bullets.add(new Bullet(centerX, startY, 0, bulletSpeed, false, normalDamage));

                    // tiros laterais nível 1
                    if (sideShotLevel >= 1) {
                        bullets.add(new Bullet(centerX - 18, startY, -90, bulletSpeed, false, normalDamage));
                        bullets.add(new Bullet(centerX + 18, startY, 90, bulletSpeed, false, normalDamage));
                    }

                    // tiros laterais nível 2
                    if (sideShotLevel >= 2) {
                        bullets.add(new Bullet(centerX - 35, startY, -150, bulletSpeed * 0.95f, false, normalDamage));
                        bullets.add(new Bullet(centerX + 35, startY, 150, bulletSpeed * 0.95f, false, normalDamage));
                    }

                    // teleguiados nível 1
                    if (homingShotLevel >= 1) {
                        bullets.add(new Bullet(centerX - 50, startY, -120, bulletSpeed * 0.75f, true, homingDamage));
                        bullets.add(new Bullet(centerX + 50, startY, 120, bulletSpeed * 0.75f, true, homingDamage));
                    }

                    // teleguiados nível 2
                    if (homingShotLevel >= 2) {
                        bullets.add(new Bullet(centerX - 70, startY, -180, bulletSpeed * 0.7f, true, homingDamage));
                        bullets.add(new Bullet(centerX + 70, startY, 180, bulletSpeed * 0.7f, true, homingDamage));
                    }

                    shootTimer = 0;
                }

                if (score > highScore) {
                    highScore = score;
                }

                if (focusMode) {
                    focusEffectRotation -= 180f * delta;
                }

                stageTimer += delta;

                if (playerDead) {
                    respawnTimer -= delta;

                    if (respawnTimer <= 0f) {
                        playerDead = false;
                        playerInvincible = true;
                        invincibleTimer = invincibleDuration;

                        playerX = playfieldX + playfieldWidth / 2f - playerDrawWidth / 2f;
                        playerY = playfieldY + 80;
                    }
                }

                if (playerInvincible) {
                    invincibleTimer -= delta;

                    if (invincibleTimer <= 0f) {
                        playerInvincible = false;
                    }
                }

                updatePattern(delta);
                updatePattern2(delta);
                updatePattern3(delta);

                //bomba
                if (Gdx.input.isKeyJustPressed(Input.Keys.X) && bombs > 0) {
                    bombs--;

                    score += 100;

                    for (Enemy enemy : enemies) {
                        score += 100;

                        dropEnemyPower(enemy);
                    }

                    enemies.clear();

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
                            enemy.hp -= bullet.damage;

                            bullets.removeIndex(b);
                            bulletRemoved = true;

                            if (enemy.hp <= 0) {
                                score += 100;

                                dropEnemyPower(enemy);

                                enemies.removeIndex(e);
                                //spawnEnemy();
                            }

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

                if (!playerDead && !playerInvincible) {
                    for (Enemy enemy : enemies) {
                        if (isColliding(
                            hitboxX, hitboxY, hitboxSize,
                            enemy.x, enemy.y, enemySize
                        )) {
                            killPlayer();
                            break;
                        }
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

                    if (!playerDead && isCollidingRect(
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

                    updateEnemyMovement(enemy, delta);

                    if (enemy.y <= playfieldY + playfieldHeight - enemySize) {
                        enemy.shotTimer += delta;

                        if (enemy.shotTimer >= enemy.shotInterval) {
                            enemyShoot(enemy);
                            enemy.shotTimer = 0;
                        }
                    }

                    if (enemy.y < playfieldY - enemySize) {
                        enemies.removeIndex(i);
                        //spawnEnemy();
                    }
                }

                for (int i = enemyBullets.size - 1; i >= 0; i--) {

                    EnemyBullet bullet = enemyBullets.get(i);

                    bullet.x += bullet.speedX * delta;
                    bullet.y += bullet.speedY * delta;

                    if (!playerDead && !playerInvincible) {
                        if (isColliding(
                            hitboxX,
                            hitboxY,
                            hitboxSize,
                            bullet.x - bullet.size / 2,
                            bullet.y - bullet.size / 2,
                            bullet.size
                        )) {
                            killPlayer();
                            break;
                        }
                    }

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
        for (Enemy enemy : enemies) {
            TextureRegion enemyFrame;

            if (enemy.type == EnemyType.WHITE || enemy.type == EnemyType.WHITE_PASSIVE) {
                enemyFrame = whiteFairyAnimation.getKeyFrame(fairyAnimationTime);
            } else {
                enemyFrame = redFairyAnimation.getKeyFrame(fairyAnimationTime);
            }

            batch.draw(
                enemyFrame,
                enemy.x,
                enemy.y + 18,
                enemySize,
                enemySize
            );
        }

        for (PowerItem item : powerItems) {

            Texture powerTexture = item.big ? powerGrande : powerPequeno;

            batch.draw(
                powerTexture,
                item.x,
                item.y,
                item.size,
                item.size
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

        if (focusMode) {
            float effectSize = 106f;

            float hx = playerX + hitboxOffsetX;
            float hy = playerY + hitboxOffsetY;

            batch.draw(
                focusModeEffect,
                hx - effectSize / 2f,
                hy - effectSize / 2f,
                effectSize / 2f,
                effectSize / 2f,
                effectSize,
                effectSize,
                1f,
                1f,
                focusEffectRotation,
                0,
                0,
                60,
                60,
                false,
                false
            );
        }

        boolean drawPlayer = !playerDead;

        if (playerInvincible) {
            drawPlayer = ((int)(invincibleTimer * 10) % 2 == 0);
        }

        if (drawPlayer) {
            batch.draw(
                currentSprite,
                playerX,
                playerY,
                51,
                75
            );
        }

        for (Bullet bullet : bullets) {
            Texture shotTexture = bullet.homing ? homingShot : reimuShot;

            float trailAlpha = bullet.homing ? 0.18f : 0.25f;

            batch.setColor(1f, 1f, 1f, trailAlpha);
            batch.draw(
                shotTexture,
                bullet.x - bullet.speedX * 0.045f,
                bullet.y - bullet.speedY * 0.045f,
                bulletDrawWidth,
                bulletDrawHeight
            );

            batch.setColor(1f, 1f, 1f, trailAlpha * 0.6f);
            batch.draw(
                shotTexture,
                bullet.x - bullet.speedX * 0.030f,
                bullet.y - bullet.speedY * 0.030f,
                bulletDrawWidth,
                bulletDrawHeight
            );

            batch.setColor(Color.WHITE);
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
        powerGrande.dispose();
        focusModeEffect.dispose();

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
        float damage;

        public Bullet(float x, float y, float speedX, float speedY, boolean homing, float damage) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.homing = homing;
            this.damage = damage;
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
        float targetY;

        float hp;
        float maxHp;

        float shotInterval;

        EnemyType type;
        EnemyShotType shotType;

        EnemyMovementType movementType;
        float moveTimer = 0f;
        float startX;
        float startY;

        Enemy(float x, float y, EnemyType type) {
            this.x = x;
            this.y = y;
            this.type = type;

            switch (type) {
                case WHITE:
                    this.hp = 1f;
                    this.maxHp = 1f;
                    this.shotType = EnemyShotType.AIMED;
                    this.shotInterval = 2.5f;
                    break;

                case WHITE_PASSIVE:
                    this.hp = 1f;
                    this.maxHp = 1f;
                    this.shotType = EnemyShotType.NONE;
                    this.shotInterval = 999f;
                    break;

                case RED:
                    this.hp = 15f;
                    this.maxHp = 15f;
                    this.shotType = EnemyShotType.RADIAL;
                    this.shotInterval = 2.0f;
                    break;

                case RED_BURST:
                    this.hp = 35f;
                    this.maxHp = 35f;
                    this.shotType = EnemyShotType.RADIAL_BURST;
                    this.shotInterval = 4.0f;
            }
        }
    }

    class PowerItem {
        float x;
        float y;
        int value;
        float size;
        float velocityY;
        boolean big;

        public PowerItem(float x, float y, int value, float size, boolean big) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.size = size;
            this.big = big;

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
        while (totalPower >= 25) {
            powerItems.add(new PowerItem(
                x + (float)(Math.random() * 30 - 15),
                y + (float)(Math.random() * 30 - 15),
                25,
                28,
                true
            ));
            totalPower -= 25;
        }

        // pequeno
        while (totalPower >= 5) {
            powerItems.add(new PowerItem(
                x + (float)(Math.random() * 30 - 15),
                y + (float)(Math.random() * 30 - 15),
                5,
                20,
                false
            ));
            totalPower -= 5;
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

    enum EnemyType {
        WHITE,
        WHITE_PASSIVE,
        RED,
        RED_BURST
    }

    enum WavePattern {
        RED_FAIRY_DUAL
    }

    enum EnemyMovementType {
        STOP_AT_TARGET,
        WHITE_LOOP_LEFT,
        WHITE_LOOP_RIGHT
    }

    enum EnemyShotType {
        NONE,
        AIMED,
        RADIAL,
        RADIAL_BURST
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
                return new Card("Bomba +", "Ganha uma bomba.", CardType.EXTRA_BOMB);
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
                playerDamage += 0.20f;
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
                playerDamage -= 0.20f;
                if (playerDamage < 1.0f) {
                    playerDamage = 1.0f;
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

    private void killPlayer() {
        if (playerDead || playerInvincible) {
            return;
        }

        lives--;

        dropPlayerPower();
        losePowerOnDeath();

        if (bombs < 3) {
            bombs = 3;
        }

        playerDead = true;
        respawnTimer = respawnDelay;
    }

    private void dropPlayerPower() {
        int availablePower = powerPoints;

        if (availablePower <= 0) {
            return;
        }

        float centerX = playerX + hitboxOffsetX;
        float centerY = playerY + hitboxOffsetY + 70f;

        if (availablePower >= 35) {
            PowerItem big = new PowerItem(centerX, centerY, 25, 28, true);
            big.velocityY = 260f;
            powerItems.add(big);

            PowerItem smallLeft = new PowerItem(centerX - 28, centerY, 5, 20, false);
            smallLeft.velocityY = 220f;
            powerItems.add(smallLeft);

            PowerItem smallRight = new PowerItem(centerX + 28, centerY, 5, 20, false);
            smallRight.velocityY = 220f;
            powerItems.add(smallRight);
        } else {
            int smallCount = availablePower / 5;

            for (int i = 0; i < smallCount; i++) {
                float offsetX = (i - (smallCount - 1) / 2f) * 18f;

                PowerItem small = new PowerItem(
                    centerX + offsetX,
                    centerY,
                    5,
                    20,
                    false
                );

                small.velocityY = 180f + i * 10f;
                powerItems.add(small);
            }
        }
    }

    private void losePowerOnDeath() {
        for (int i = 0; i < 1; i++) {
            if (!appliedCards.empty()) {
                Card lastCard = appliedCards.pop();
                removeCard(lastCard);
            }
        }

        if (powerPoints >= 100) {
            powerPoints -= 100;
        } else {
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

    private void enemyShoot(Enemy enemy) {
        float centerX = enemy.x + enemySize / 2;
        float centerY = enemy.y + enemySize / 2;

        switch (enemy.shotType) {
            case NONE:
                break;

            case AIMED:
                shootAimedPattern(centerX, centerY);
                break;

            case RADIAL:
                shootRedFairyCirclePattern(centerX, centerY, 140f, 0f, 32);
                break;

            case RADIAL_BURST:
                shootRedFairyCirclePattern(centerX, centerY, 60f, 0f, 16);
                shootRedFairyCirclePattern(centerX, centerY, 95f, 11.25f, 16);
                shootRedFairyCirclePattern(centerX, centerY, 130f, 0f, 16);
                shootRedFairyCirclePattern(centerX, centerY, 165f, 11.25f, 16);
                break;
        }
    }

    private void shootAimedPattern(float centerX, float centerY) {
        float speed = 180f;

        float targetX = playerX + hitboxOffsetX;
        float targetY = playerY + hitboxOffsetY;

        float dx = targetX - centerX;
        float dy = targetY - centerY;

        float length = (float)Math.sqrt(dx * dx + dy * dy);

        if (length != 0) {
            dx /= length;
            dy /= length;
        }

        enemyBullets.add(new EnemyBullet(
            centerX,
            centerY,
            dx * speed,
            dy * speed,
            14
        ));
    }

    private void shootRedFairyCirclePattern(
        float centerX,
        float centerY,
        float speed,
        float angleOffset,
        int bulletCount
    ) {
        for (int i = 0; i < bulletCount; i++) {
            float angle = (float)Math.toRadians((360f / bulletCount) * i + angleOffset);

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

    private void updatePattern(float delta) {

        switch (currentPattern) {

            case RED_FAIRY_DUAL:

                if (!spawnedFirstRed && stageTimer >= 5f) {
                    spawnEnemy(
                        EnemyType.RED,
                        playfieldX + playfieldWidth - enemySize - 120,
                        playfieldY + playfieldHeight - enemySize
                    );

                    spawnedFirstRed = true;
                }

                if (!spawnedSecondRed && stageTimer >= 10f) {
                    spawnEnemy(
                        EnemyType.RED,
                        playfieldX + 120,
                        playfieldY + playfieldHeight - enemySize
                    );

                    spawnedSecondRed = true;
                }

                break;
        }
    }

    private void updatePattern2(float delta) {
        if (!pattern2Started && stageTimer >= 17f) {
            pattern2Started = true;
            pattern2Timer = 0f;
            pattern2SpawnCount = 0;
        }

        if (!pattern2Started) {
            return;
        }

        pattern2Timer += delta;

        if (pattern2SpawnCount < 10 && pattern2Timer >= 0.35f) {
            pattern2Timer = 0f;

            float leftX = playfieldX;
            float rightX = playfieldX + playfieldWidth - enemySize;

            float spawnY = playfieldY + playfieldHeight - enemySize - 40;

            spawnEnemy(
                EnemyType.WHITE,
                EnemyMovementType.WHITE_LOOP_LEFT,
                leftX,
                spawnY
            );

            spawnEnemy(
                EnemyType.WHITE,
                EnemyMovementType.WHITE_LOOP_RIGHT,
                rightX,
                spawnY
            );

            pattern2SpawnCount++;
        }
    }

    private void updateEnemyMovement(Enemy enemy, float delta) {
        enemy.moveTimer += delta;

        switch (enemy.movementType) {
            case STOP_AT_TARGET:
                if (enemy.y > enemy.targetY) {
                    float distance = enemy.y - enemy.targetY;

                    float speed = distance * 4f;

                    if (speed < 60f) {
                        speed = 60f;
                    }

                    if (speed > 420f) {
                        speed = 420f;
                    }

                    enemy.y -= speed * delta;

                    if (enemy.y < enemy.targetY) {
                        enemy.y = enemy.targetY;
                    }
                }
                break;

            case WHITE_LOOP_LEFT:
                updateWhiteLoop(enemy, false);
                break;

            case WHITE_LOOP_RIGHT:
                updateWhiteLoop(enemy, true);
                break;
        }
    }

    private void updateWhiteLoop(Enemy enemy, boolean fromRight) {
        float duration = 3.2f;
        float t = enemy.moveTimer / duration;

        if (t > 1f) {
            enemy.y = playfieldY - enemySize - 20;
            return;
        }

        float startX = fromRight
            ? playfieldX + playfieldWidth - enemySize
            : playfieldX;

        float endX = fromRight
            ? playfieldX
            : playfieldX + playfieldWidth - enemySize;

        float centerX = playfieldX + playfieldWidth / 2f - enemySize / 2f;

        float baseY = playfieldY + playfieldHeight - enemySize - 120;
        float loopHeight = 260f;

        float x;

        if (t < 0.5f) {
            float localT = t / 0.5f;
            x = lerp(startX, centerX, localT);
        } else {
            float localT = (t - 0.5f) / 0.5f;
            x = lerp(centerX, endX, localT);
        }

        float y = baseY - (float)Math.sin(t * Math.PI) * loopHeight;

        enemy.x = x;
        enemy.y = y;
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private void updatePattern3(float delta) {
        if (!pattern3Started && stageTimer >= 26f) {
            pattern3Started = true;
            pattern3Timer = 0f;
        }

        if (!pattern3Started) {
            return;
        }

        pattern3Timer += delta;

        // enxame branco passivo pela esquerda
        if (pattern3LeftCount < 10 && pattern3Timer >= pattern3LeftCount * 0.30f) {
            spawnEnemy(
                EnemyType.WHITE_PASSIVE,
                EnemyMovementType.WHITE_LOOP_LEFT,
                playfieldX,
                playfieldY + playfieldHeight - enemySize - 40
            );

            pattern3LeftCount++;
        }

        // enxame branco passivo pela direita, depois do esquerdo
        if (pattern3Timer >= 4f && pattern3RightCount < 10 &&
            pattern3Timer >= 4f + pattern3RightCount * 0.30f) {

            spawnEnemy(
                EnemyType.WHITE_PASSIVE,
                EnemyMovementType.WHITE_LOOP_RIGHT,
                playfieldX + playfieldWidth - enemySize,
                playfieldY + playfieldHeight - enemySize - 40
            );

            pattern3RightCount++;
        }

        // vermelhas padrão depois do segundo enxame
        if (!pattern3RedSpawned && pattern3Timer >= 8f) {
            spawnEnemy(
                EnemyType.RED,
                playfieldX + playfieldWidth - enemySize - 120,
                playfieldY + playfieldHeight - enemySize
            );

            spawnEnemy(
                EnemyType.RED,
                playfieldX + 120,
                playfieldY + playfieldHeight - enemySize
            );

            pattern3RedSpawned = true;
        }

        // red burst no meio, um pouco mais alto
        if (!pattern3BurstSpawned && pattern3Timer >= 14f) {
            spawnEnemy(
                EnemyType.RED_BURST,
                playfieldX + playfieldWidth / 2f - enemySize / 2f,
                playfieldY + playfieldHeight - enemySize
            );

            pattern3BurstSpawned = true;
        }
    }

    private void dropEnemyPower(Enemy enemy) {
        float x = enemy.x + enemySize / 2;
        float y = enemy.y + enemySize / 2;

        switch (enemy.type) {
            case RED_BURST:
                spawnPowerItems(x, y, 75); // 3 power grandes
                break;

            case RED:
                spawnPowerItems(x, y, 25); // 1 power grande
                break;

            default:
                spawnPowerItems(x, y, enemyPowerReward); // power pequeno
                break;
        }
    }

}
