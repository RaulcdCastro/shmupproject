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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import java.util.Random;
import java.util.Stack;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

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

    private Animation<TextureRegion> subBossAnimation;

    // boss
    private Animation<TextureRegion> marisaIdleAnimation;
    private Animation<TextureRegion> marisaLeftAnimation;
    private Animation<TextureRegion> marisaRightAnimation;

    private boolean finalBossSpawnDelayStarted = false;
    private float finalBossSpawnDelay = 6f;

    private boolean finalBossSpawned = false;

    private Enemy currentBossEnemy = null;

    // teste boss
    private final boolean debugFinalBossOnly = false; //debug

    private boolean gameFinished = false;
    private float gameFinishedTimer = 0f;
    private boolean showEndingScreen = false;

    private boolean gameOver = false;

    // vidas, bombas e pontos
    private int lives = 6;  //valor normal = 2
    private int bombs = 3;
    private int score = 0;

    private final float hudX = playfieldX + playfieldWidth + 30;

    private int highScore = 0;

    // carta
    private Texture cardTexture;

    private float cardInvulnerabilityTimer = 0f;

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

    // pattern 2

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

    // patter 4
    private boolean pattern4Started = false;
    private boolean pattern4PurpleSpawned = false;
    private float pattern4Timer = 0f;

    // pattern 5
    private boolean pattern5Started = false;
    private float pattern5Timer = 0f;

    private int pattern5SideIndex = 0;
    private int pattern5SpawnCount = 0;

    private boolean pattern5BurstSpawned = false;

    // pattern 6
    private boolean pattern6Started = false;
    private float pattern6Timer = 0f;

    private boolean pattern6OuterRedsSpawned = false;
    private boolean pattern6InnerRedsSpawned = false;
    private boolean pattern6PurpleSpawned = false;

    // sub chefe
    private boolean subBossSpawned = false;

    private boolean subBossSpawnDelayStarted = false;
    private float subBossSpawnDelay = 6f;

    private Enemy subBossEnemy = null;

    // pattern 7
    private boolean postBossPatternStarted = false;
    private float postBossPatternTimer = 0f;
    private int postBossWave = 0;
    private int postBossSpawnCount = 0;

    // pattern 8
    private boolean pattern8Started = false;
    private boolean pattern8Spawned = false;
    private float pattern8Timer = 0f;

    private boolean pattern8Waiting = false;

    // pattern 9
    private boolean pattern9Started = false;
    private boolean pattern9PurpleSpawned = false;
    private float pattern9Timer = 0f;
    private int pattern9SpawnCount = 0;

    // pattern 10
    private boolean pattern10Started = false;
    private float pattern10Timer = 0f;
    private int pattern10Side = 0;
    private int pattern10Spawned = 0;

    // fada animacao
    private Animation<TextureRegion> fairyAnimation;
    private float fairyAnimationTime = 0f;

    private boolean bombEffect = false;
    private float bombTimer = 0;
    private final float bombDuration = 0.50f;

    private boolean masterSparkFlashEffect = false;
    private float masterSparkFlashTimer = 0f;
    private final float masterSparkFlashDuration = 8.50f;

    private float screenShakeTimer = 0f;
    private float screenShakeIntensity = 0f;

    private Texture whitePixel;

    // modo foco
    private boolean focusMode = false;

    // tiros
    private Array<Bullet> bullets;
    private final float bulletSpeed = 500;
    private float shootTimer = 0;
    private final float shootCooldown = 0.12f;

    private float homingShootTimer = 0;

    //disparos inimigos
    private Array<EnemyBullet> enemyBullets;

    private Array<EnemyLaser> enemyLasers;
    private Array<MarisaSideLaser> marisaSideLasers;

    private Array<BossHorizontalLaser> bossHorizontalLasers;
    private Array<MasterSparkLaser> masterSparkLasers;

    private final float enemyShotInterval = 1f;

    //power
    private Array<PowerItem> powerItems;
    private final float powerItemFallSpeed = 90;
    private final int enemyPowerReward = 5; // +0.05 Power

    private Texture powerPequeno;
    private Texture powerGrande;

    private Texture lifeTexture;

    private Array<LifeItem> lifeItems;

    //hud
    private SpriteBatch batch;
    private BitmapFont font;

    //background
    private Animation<TextureRegion> backgroundAnimation;
    private float backgroundTime = 0f;

    //sons
    private Music backgroundMusic;

    private Music lowLifeMusic;

    private Sound shotSound;

    private Sound bombSound;

    private Sound redShot00;
    private Sound redShot02;
    private Sound redShot04;

    private Sound whiteShot01;
    private Sound whiteShot02;
    private Sound whiteShot03;

    private Sound deathSound;

    private Sound laserSound;
    private boolean laserSoundPlayedThisFrame = false;

    private Sound pointsSound;
    private Sound enemyDefeatedSound;

    private Sound powerUpSound;

    private Sound marisaLaserHitSound;

    private Sound defeat01Sound;
    private Sound defeat02Sound;

    private Sound masterSparkSound;

    private Sound warningSound;

    private Sound lifeSound;

    private boolean lowLifePlaying = false;

    private float bossShotSoundTimer = 0f;
    private final float bossShotSoundCooldown = 0.08f;

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
    private float playerDamage = 1.0f; // dano padrao = 1.0f

    private float criticalChance = 0f;

    private float hitboxMultiplier = 1.0f;

    private float moveSpeedMultiplier = 1.0f;
    private float focusSpeedMultiplier = 1.0f;

    private int witchStyleLevel = 0;
    private int priestessStyleLevel = 0;

    private int maxBombs = 3;

    private Enemy spawnEnemy(EnemyType type, float x, float y) {
        return spawnEnemy(type, EnemyMovementType.STOP_AT_TARGET, x, y);

    }

    private Enemy spawnEnemy(
        EnemyType type,
        EnemyMovementType movementType,
        float x,
        float y
    ) {
        Enemy enemy = new Enemy(x, y, type);

        enemy.movementType = movementType;

        enemy.startX = x;
        enemy.startY = y;

        enemy.initialX = x;
        enemy.initialY = y;

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

        if (type == EnemyType.FINAL_BOSS) {
            enemy.targetY = playfieldY + playfieldHeight - 260f;
        } else if (type == EnemyType.SUBBOSS) {
            enemy.targetY = playfieldY + playfieldHeight - 300f;
        } else if (type == EnemyType.PURPLE || type == EnemyType.PURPLE_TANK) {
            enemy.targetY = playfieldY + playfieldHeight - 190f;
        } else {
            enemy.targetY = redStopY;
        }

        enemies.add(enemy);
        return enemy;
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
        lifeTexture = new Texture("vida.png");
        lifeItems = new Array<>();

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

        subBossAnimation = new Animation<>(
            0.12f,
            new TextureRegion(new Texture("subboss01.png")),
            new TextureRegion(new Texture("subboss02.png")),
            new TextureRegion(new Texture("subboss03.png")),
            new TextureRegion(new Texture("subboss04.png"))
        );
        subBossAnimation.setPlayMode(Animation.PlayMode.LOOP);

        marisaIdleAnimation = new Animation<>(
            0.12f,
            new TextureRegion(new Texture("marisaparada01.png")),
            new TextureRegion(new Texture("marisaparada02.png")),
            new TextureRegion(new Texture("marisaparada03.png")),
            new TextureRegion(new Texture("marisaparada04.png"))
        );
        marisaIdleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        marisaLeftAnimation = new Animation<>(
            0.12f,
            new TextureRegion(new Texture("marisaesquerda01.png")),
            new TextureRegion(new Texture("marisaesquerda02.png"))
        );
        marisaLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);

        marisaRightAnimation = new Animation<>(
            0.12f,
            new TextureRegion(new Texture("marisadireita01.png")),
            new TextureRegion(new Texture("marisadireita02.png"))
        );
        marisaRightAnimation.setPlayMode(Animation.PlayMode.LOOP);

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
        marisaSideLasers = new Array<>();

        uiBackground = new Texture("ui_background.png");

        reimuShot = new Texture("reimutiro.png");

        reimuShot.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("musica1.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f); //0.5
        backgroundMusic.play();

        shotSound = Gdx.audio.newSound(Gdx.files.internal("shot.wav"));
        redShot00 = Gdx.audio.newSound(Gdx.files.internal("red00.wav"));
        redShot02 = Gdx.audio.newSound(Gdx.files.internal("red02.wav"));
        redShot04 = Gdx.audio.newSound(Gdx.files.internal("red04.wav"));

        whiteShot01 = Gdx.audio.newSound(
            Gdx.files.internal("white01.wav")
        );

        whiteShot02 = Gdx.audio.newSound(
            Gdx.files.internal("white02.wav")
        );

        whiteShot03 = Gdx.audio.newSound(
            Gdx.files.internal("white03.wav")
        );

        bombSound = Gdx.audio.newSound(
            Gdx.files.internal("bomb.wav")
        );

        deathSound = Gdx.audio.newSound(
            Gdx.files.internal("death.wav")
        );

        laserSound = Gdx.audio.newSound(
            Gdx.files.internal("laser.wav")
        );

        marisaLaserHitSound = Gdx.audio.newSound(
            Gdx.files.internal("marisalaserhit.wav")
        );

        pointsSound = Gdx.audio.newSound(
            Gdx.files.internal("points.wav")
        );

        enemyDefeatedSound = Gdx.audio.newSound(
            Gdx.files.internal("enemydefeated.wav")
        );

        defeat01Sound = Gdx.audio.newSound(
            Gdx.files.internal("defeat01.wav")
        );

        defeat02Sound = Gdx.audio.newSound(
            Gdx.files.internal("defeat02.wav")
        );

        powerUpSound = Gdx.audio.newSound(
            Gdx.files.internal("powerup.wav")
        );

        lowLifeMusic = Gdx.audio.newMusic(
            Gdx.files.internal("lowlife.wav")
        );

        lowLifeMusic.setLooping(true);
        lowLifeMusic.setVolume(0.30f);

        masterSparkSound = Gdx.audio.newSound(
            Gdx.files.internal("masterspark.wav")
        );

        warningSound = Gdx.audio.newSound(
            Gdx.files.internal("warning.wav")
        );

        lifeSound = Gdx.audio.newSound(
            Gdx.files.internal("vida.wav")
        );

        bullets = new Array<>();
        enemyBullets = new Array<>();
        enemyLasers = new Array<>();
        bossHorizontalLasers = new Array<>();
        masterSparkLasers = new Array<>();

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
        laserSoundPlayedThisFrame = false;
        bossShotSoundTimer += delta;

        if (cardInvulnerabilityTimer > 0f) {
            cardInvulnerabilityTimer -= delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }

        if ((paused || gameOver || showEndingScreen)
            && Gdx.input.isKeyJustPressed(Input.Keys.R)) {

            restartGame();
        }

        if (!paused) {

            if (choosingCard) {
                handleCardMouseInput();
            } else {

                // ativa modo foco com SHIFT
                focusMode =
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                        Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

                float currentSpeed =
                    focusMode
                        ? focusSpeed * focusSpeedMultiplier
                        : normalSpeed * moveSpeedMultiplier;

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
                homingShootTimer += delta;

                if (!playerDead &&
                    Gdx.input.isKeyPressed(Input.Keys.Z) &&
                    shootTimer >= shootCooldown / fireRateMultiplier) {

                    float centerX = playerX + hitboxOffsetX - bulletDrawWidth / 2;
                    float startY = playerY + hitboxOffsetY + 20;

                    float normalDamage = playerDamage;
                    float homingDamage = playerDamage / 2f;


                    float witchSpreadMultiplier = 1f - witchStyleLevel * 0.25f;
                    float priestessSpreadMultiplier = 1f + priestessStyleLevel * 0.25f;

                    // tiro central
                    bullets.add(new Bullet(centerX, startY, 0, bulletSpeed, false, normalDamage));
                    shotSound.play(0.17f);

                    // tiros laterais nível 1
                    if (sideShotLevel >= 1) {
                        if (focusMode) {
                            bullets.add(new Bullet(centerX - 5, startY, -20f * witchSpreadMultiplier, bulletSpeed, false, normalDamage));
                            bullets.add(new Bullet(centerX + 5, startY, 20f * witchSpreadMultiplier, bulletSpeed, false, normalDamage));
                        } else {
                            bullets.add(new Bullet(centerX - 18, startY, -90f * priestessSpreadMultiplier, bulletSpeed, false, normalDamage));
                            bullets.add(new Bullet(centerX + 18, startY, 90f * priestessSpreadMultiplier, bulletSpeed, false, normalDamage));
                        }
                    }

                    // tiros laterais nível 2
                    if (sideShotLevel >= 2) {
                        if (focusMode) {
                            bullets.add(new Bullet(centerX - 10, startY, -40f * witchSpreadMultiplier, bulletSpeed * 0.95f, false, normalDamage));
                            bullets.add(new Bullet(centerX + 10, startY, 40f * witchSpreadMultiplier, bulletSpeed * 0.95f, false, normalDamage));
                        } else {
                            bullets.add(new Bullet(centerX - 35, startY, -150f * priestessSpreadMultiplier, bulletSpeed * 0.95f, false, normalDamage));
                            bullets.add(new Bullet(centerX + 35, startY, 150f * priestessSpreadMultiplier, bulletSpeed * 0.95f, false, normalDamage));
                        }
                    }

                    shootTimer = 0;
                }

                float homingCooldown =
                    (shootCooldown * 2f) / (1f + (fireRateMultiplier - 1f) * 0.5f);

                if (!playerDead &&
                    Gdx.input.isKeyPressed(Input.Keys.Z) &&
                    homingShootTimer >= homingCooldown) {

                    float centerX = playerX + hitboxOffsetX - bulletDrawWidth / 2;
                    float startY = playerY + hitboxOffsetY + 20;

                    float homingDamage = playerDamage / 2f;

                    if (homingShotLevel >= 1) {
                        bullets.add(new Bullet(centerX - 50, startY, -120, bulletSpeed * 0.75f, true, homingDamage));
                        bullets.add(new Bullet(centerX + 50, startY, 120, bulletSpeed * 0.75f, true, homingDamage));
                    }

                    if (homingShotLevel >= 2) {
                        bullets.add(new Bullet(centerX - 70, startY, -180, bulletSpeed * 0.7f, true, homingDamage));
                        bullets.add(new Bullet(centerX + 70, startY, 180, bulletSpeed * 0.7f, true, homingDamage));
                    }

                    homingShootTimer = 0;
                }

                if (score > highScore) {
                    highScore = score;
                }

                if (focusMode && !playerDead) {
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

                if (debugFinalBossOnly) {
                    spawnDebugFinalBoss();
                } else {
                    updatePattern(delta);
                    updatePattern2(delta);
                    updatePattern3(delta);
                    updatePattern4(delta);
                    updatePattern5(delta);
                    updatePattern6(delta);
                    updateSubBossSpawn(delta);
                    updatePostBossPattern(delta);
                    updatePattern8(delta);
                    updatePattern9(delta);
                    updatePattern10(delta);
                }

                //bomba
                if (Gdx.input.isKeyJustPressed(Input.Keys.X) &&
                    bombs > 0 &&
                    !playerDead) {
                    bombs--;

                    bombSound.play(0.45f);

                    score += 100;

                    for (int i = enemies.size - 1; i >= 0; i--) {
                        Enemy enemy = enemies.get(i);

                        score += 100;

                        if (enemy.bossLike) {
                            float bombDamagePercent = 0.10f;

                            float hpPercent = enemy.hp / enemy.maxHp;

                            if ((enemy.type == EnemyType.FINAL_BOSS ||
                                enemy.type == EnemyType.SUBBOSS) &&
                                hpPercent <= 0.30f) {

                                bombDamagePercent = 0.05f;
                            }

                            if (enemy.type == EnemyType.FINAL_BOSS &&
                                enemy.postMasterSparkStarted &&
                                enemy.postMasterSparkDelay <= 0f) {

                                bombDamagePercent = 0.025f;
                            }

                            enemy.hp -= enemy.maxHp * bombDamagePercent;

                            if (enemy.hp <= 0) {
                                if (enemy.healthBar < enemy.maxHealthBars - 1) {
                                    breakBossHealthBar(enemy);
                                } else {
                                    dropEnemyPower(enemy);

                                    if (enemy == subBossEnemy) {

                                        dropLifeItem(enemy);
                                        defeat02Sound.play(0.18f);

                                        subBossEnemy = null;
                                        currentBossEnemy = null;

                                        lowLifeMusic.stop();
                                        lowLifePlaying = false;

                                        postBossPatternStarted = true;
                                        postBossPatternTimer = 0f;
                                        postBossWave = 0;
                                        postBossSpawnCount = 0;

                                        bombEffect = true;
                                        bombTimer = bombDuration;
                                    }

                                    if (enemy.type == EnemyType.FINAL_BOSS) {

                                        defeat02Sound.play(0.25f);

                                        enemyBullets.clear();
                                        enemyLasers.clear();
                                        bossHorizontalLasers.clear();
                                        masterSparkLasers.clear();

                                        currentBossEnemy = null;

                                        lowLifeMusic.stop();
                                        lowLifePlaying = false;

                                        gameFinished = true;
                                        gameFinishedTimer = 10f;

                                        bombEffect = true;
                                        bombTimer = 1.0f;
                                    }

                                    if (!enemy.bossLike) {
                                        enemyDefeatedSound.play(0.35f);
                                    }

                                    enemies.removeIndex(i);

                                    bombEffect = true;
                                    bombTimer = bombDuration;
                                }
                            }
                        } else {
                            dropEnemyPower(enemy);

                            if (!enemy.bossLike) {
                                enemyDefeatedSound.play(0.35f);
                            }

                            enemies.removeIndex(i);
                        }
                    }

                    enemyBullets.clear();
                    enemyLasers.clear();

                    bombEffect = true;
                    bombTimer = bombDuration;
                }

                if (bombEffect) {
                    bombTimer -= delta;

                    if (bombTimer <= 0) {
                        bombEffect = false;
                    }
                }

                if (masterSparkFlashEffect) {
                    masterSparkFlashTimer -= delta;

                    if (masterSparkFlashTimer <= 0f) {
                        masterSparkFlashEffect = false;
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

                        if (enemy.phaseTransition ||
                            enemy.damageLocked) {
                            continue;
                        }

                        if (isCollidingRect(
                            bullet.x, bullet.y, bulletDrawWidth, bulletDrawHeight,
                            enemy.x, enemy.y, enemySize, enemySize
                        )) {
                            float damage = bullet.damage;

                            if (random.nextFloat() < criticalChance) {
                                damage *= 2f;
                            }

                            if (enemy.type == EnemyType.FINAL_BOSS &&
                                enemy.postMasterSparkStarted &&
                                enemy.postMasterSparkDelay <= 0f) {

                                damage *= 0.70f;
                            }

                            if (enemy.bossLike) {
                                float hpPercent = enemy.hp / enemy.maxHp;

                                if (hpPercent <= 0.30f) {
                                    damage *= 0.50f;
                                }
                            }

                            enemy.hp -= damage;

                            bullets.removeIndex(b);
                            bulletRemoved = true;

                            if (enemy.hp <= 0) {
                                score += 100;

                                if (enemy.bossLike && enemy.healthBar < enemy.maxHealthBars - 1) {
                                    breakBossHealthBar(enemy);
                                } else {
                                    dropEnemyPower(enemy);

                                    if (enemy == subBossEnemy) {

                                        dropLifeItem(enemy);
                                        defeat02Sound.play(0.18f);

                                        subBossEnemy = null;
                                        currentBossEnemy = null;

                                        lowLifeMusic.stop();
                                        lowLifePlaying = false;

                                        enemyBullets.clear();
                                        enemyLasers.clear();

                                        postBossPatternStarted = true;
                                        postBossPatternTimer = 0f;
                                        postBossWave = 0;
                                        postBossSpawnCount = 0;

                                        bombEffect = true;
                                        bombTimer = bombDuration;
                                    }

                                    if (enemy.type == EnemyType.FINAL_BOSS) {

                                        defeat02Sound.play(0.25f);

                                        enemyBullets.clear();
                                        enemyLasers.clear();
                                        bossHorizontalLasers.clear();
                                        masterSparkLasers.clear();
                                        marisaSideLasers.clear();

                                        currentBossEnemy = null;

                                        bombEffect = true;
                                        bombTimer = 1.0f;

                                        lowLifeMusic.stop();
                                        lowLifePlaying = false;

                                        gameFinished = true;
                                        gameFinishedTimer = 10f;
                                    }

                                    if (!enemy.bossLike) {
                                        enemyDefeatedSound.play(0.35f);
                                    }

                                    enemies.removeIndex(e);
                                }
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
                float currentHitboxSize = hitboxSize * hitboxMultiplier;

                float hitboxX =
                    playerX + hitboxOffsetX - (currentHitboxSize / 2f);

                float hitboxY =
                    playerY + hitboxOffsetY - (currentHitboxSize / 2f);

                if (!playerDead &&
                    !playerInvincible &&
                    cardInvulnerabilityTimer <= 0f) {
                    for (Enemy enemy : enemies) {
                        if (isColliding(
                            hitboxX, hitboxY, currentHitboxSize,
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

                        pointsSound.play(0.25f);

                        powerItems.removeIndex(i);
                        continue;
                    }

                    if (item.y < playfieldY - item.size) {
                        powerItems.removeIndex(i);
                    }
                }

                for (int i = lifeItems.size - 1; i >= 0; i--) {
                    LifeItem item = lifeItems.get(i);

                    item.velocityY -= 150f * delta;
                    item.y += item.velocityY * delta;

                    float maxY = playfieldY + playfieldHeight - item.size;

                    if (item.y > maxY) {
                        item.y = maxY;

                        if (item.velocityY > 0) {
                            item.velocityY = 0;
                        }
                    }

                    if (!playerDead && isCollidingRect(
                        playerX,
                        playerY,
                        collectHitboxWidth,
                        collectHitboxHeight,
                        item.x,
                        item.y,
                        item.size,
                        item.size
                    )) {
                        lives++;
                        lifeItems.removeIndex(i);
                        pointsSound.play(0.25f);

                        lifeSound.play(0.35f);

                        continue;
                    }

                    if (item.y < playfieldY - item.size) {
                        lifeItems.removeIndex(i);
                    }
                }


                // inimigo descendo
                for (int i = enemies.size - 1; i >= 0; i--) {
                    Enemy enemy = enemies.get(i);

                    updateEnemyMovement(enemy, delta);

                    if (enemy.type == EnemyType.FINAL_BOSS &&
                        enemy.phase2Intro) {

                        updateMarisaPhase2Intro(enemy, delta);
                    }

                    if (enemy.type == EnemyType.FINAL_BOSS &&
                        enemy.postMasterSparkStarted) {

                        updateMarisaPostMasterSpark(enemy, delta);
                    }

                    if (enemy.type == EnemyType.FINAL_BOSS &&
                        enemy.lastStand &&
                        !enemy.finalSpellStarted &&
                        !enemy.finalSpellFinished) {

                        updateMarisaLastStand(enemy);
                    }

                    if (enemy.type == EnemyType.FINAL_BOSS &&
                        enemy.lastStand &&
                        !enemy.finalSpellStarted &&
                        !enemy.finalSpellFinished) {

                        float hpPercent = enemy.hp / enemy.maxHp;

                        if (hpPercent <= 0.05f) {
                            startMarisaMasterSpark(enemy);
                        }
                    }

                    if (enemy.y <= playfieldY + playfieldHeight - enemySize) {
                        enemy.shotTimer += delta;

                        if (enemy.attackDelayTimer > 0f) {
                            enemy.attackDelayTimer -= delta;
                        } else {
                            if (!enemy.phaseTransition &&
                                !enemy.lastStand &&
                                !enemy.phase2Intro &&
                                enemy.shotTimer >= enemy.shotInterval) {

                                enemyShoot(enemy);
                                enemy.shotTimer = 0;
                            }
                        }
                    }

                    if (enemy.y < playfieldY - enemySize) {
                        enemies.removeIndex(i);
                        //spawnEnemy();
                    }

                    if (enemy.bossLike) {
                        updateBossTransition(enemy, delta);
                        updateBossPhase(enemy);
                    }

                }

                for (int i = enemyBullets.size - 1; i >= 0; i--) {

                    EnemyBullet bullet = enemyBullets.get(i);

                    bullet.x += bullet.speedX * delta;
                    bullet.y += bullet.speedY * delta;

                    if (!playerDead &&
                        !playerInvincible &&
                        cardInvulnerabilityTimer <= 0f) {
                        if (isColliding(
                            hitboxX,
                            hitboxY,
                            currentHitboxSize,
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

                for (int i = enemyLasers.size - 1; i >= 0; i--) {
                    EnemyLaser laser = enemyLasers.get(i);

                    laser.timer += delta;

                    if (laser.timer >= laser.getTotalTime()) {
                        enemyLasers.removeIndex(i);
                    }
                }

                for (int i = bossHorizontalLasers.size - 1; i >= 0; i--) {
                    BossHorizontalLaser laser = bossHorizontalLasers.get(i);

                    laser.timer += delta;

                    if (laser.timer < laser.followTime) {
                        laser.y = playerY + hitboxOffsetY;
                    }

                    if (!laser.hitSoundPlayed &&
                        laser.timer >= laser.followTime + laser.warningTime) {

                        marisaLaserHitSound.play(0.50f);
                        laser.hitSoundPlayed = true;
                    }

                    if (laser.timer >= laser.getTotalTime()) {
                        bossHorizontalLasers.removeIndex(i);
                    }
                }

                for (MarisaSideLaser laser : marisaSideLasers) {

                    if (isCollidingRect(
                        hitboxX,
                        hitboxY,
                        currentHitboxSize,
                        currentHitboxSize,

                        laser.x,
                        playfieldY,
                        laser.width,
                        playfieldHeight
                    )) {
                        killPlayer();
                        break;
                    }
                }

                for (int i = masterSparkLasers.size - 1; i >= 0; i--) {
                    MasterSparkLaser laser = masterSparkLasers.get(i);

                    laser.timer += delta;

                    if (laser.timer >= laser.getTotalTime()) {
                        masterSparkLasers.removeIndex(i);

                        if (currentBossEnemy != null &&
                            currentBossEnemy.type == EnemyType.FINAL_BOSS) {

                            currentBossEnemy.damageLocked = false;
                            currentBossEnemy.finalSpellFinished = true;

                            currentBossEnemy.postMasterSparkStarted = true;
                            currentBossEnemy.postMasterSparkDelay = 1.0f;
                            currentBossEnemy.postMasterSparkTimer = 0f;
                            currentBossEnemy.postMasterSparkShotTimer = 0f;
                        }
                    }
                }

                if (!playerDead &&
                    !playerInvincible &&
                    cardInvulnerabilityTimer <= 0f) {
                    for (EnemyLaser laser : enemyLasers) {
                        if (laser.timer >= laser.fadeInTime &&
                            laser.timer <= laser.fadeInTime + laser.activeTime) {
                            if (isCollidingRect(
                                hitboxX,
                                hitboxY,
                                currentHitboxSize,
                                currentHitboxSize,

                                laser.x - laser.width / 2f,
                                playfieldY,
                                laser.width,
                                laser.length
                            )) {
                                killPlayer();
                                break;
                            }
                        }
                    }

                    for (BossHorizontalLaser laser : bossHorizontalLasers) {

                        if (!laser.isDangerous()) {
                            continue;
                        }

                        if (isCollidingRect(
                            hitboxX,
                            hitboxY,
                            currentHitboxSize,
                            currentHitboxSize,

                            playfieldX,
                            laser.y - laser.width / 2f,
                            playfieldWidth,
                            laser.width
                        )) {

                            killPlayer();
                            break;
                        }
                    }


                    for (MasterSparkLaser laser : masterSparkLasers) {
                        if (!laser.isDangerous()) {
                            continue;
                        }

                        if (isCollidingRect(
                            hitboxX,
                            hitboxY,
                            currentHitboxSize,
                            currentHitboxSize,

                            laser.x - laser.width / 2f,
                            playfieldY,
                            laser.width,
                            playfieldHeight
                        )) {
                            killPlayer();
                            break;
                        }
                    }
                }

                if (gameFinished && !showEndingScreen) {
                    gameFinishedTimer -= delta;

                    if (gameFinishedTimer <= 0f) {
                        showEndingScreen = true;
                    }
                }

            }
        }

        // limpar tela
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(640, 480, 0);

        if (screenShakeTimer > 0f) {
            screenShakeTimer -= delta;

            float shakeX = (random.nextFloat() * 2f - 1f) * screenShakeIntensity;
            float shakeY = (random.nextFloat() * 2f - 1f) * screenShakeIntensity;

            camera.position.x += shakeX;
            camera.position.y += shakeY;
        }

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


        // fadas e boss
        for (Enemy enemy : enemies) {
            TextureRegion enemyFrame;

            if (enemy.type == EnemyType.FINAL_BOSS) {
                TextureRegion marisaFrame;

                if (enemy.bossMoveDirection < 0) {
                    marisaFrame = marisaLeftAnimation.getKeyFrame(fairyAnimationTime);
                } else if (enemy.bossMoveDirection > 0) {
                    marisaFrame = marisaRightAnimation.getKeyFrame(fairyAnimationTime);
                } else {
                    marisaFrame = marisaIdleAnimation.getKeyFrame(fairyAnimationTime);
                }

                if (enemy.phaseTransition) {
                    boolean drawEnemy =
                        ((int)(enemy.phaseTransitionTimer * 12f) % 2 == 0);

                    if (!drawEnemy) {
                        continue;
                    }
                }

                float marisaW = 120f;
                float marisaH = 117f;

                batch.draw(
                    marisaFrame,
                    enemy.x - (marisaW - 80f) / 2f,
                    enemy.y - (marisaH - 78f) / 2f,
                    marisaW,
                    marisaH
                );

                continue;
            }

            if (enemy.type == EnemyType.WHITE || enemy.type == EnemyType.WHITE_PASSIVE) {
                enemyFrame = whiteFairyAnimation.getKeyFrame(fairyAnimationTime);
            } else if (enemy.type == EnemyType.PURPLE || enemy.type == EnemyType.PURPLE_TANK) {
                enemyFrame = fairyAnimation.getKeyFrame(fairyAnimationTime);
            } else if (enemy.type == EnemyType.SUBBOSS) {
                enemyFrame = subBossAnimation.getKeyFrame(fairyAnimationTime);
            } else {
                enemyFrame = redFairyAnimation.getKeyFrame(fairyAnimationTime);
            }


            float drawSize = enemySize;

            if (enemy.type == EnemyType.SUBBOSS) {
                drawSize = enemySize * 2.0f;
            }

            float drawX = enemy.x - (drawSize - enemySize) / 2f;
            float drawY = enemy.y + 18 - (drawSize - enemySize) / 2f;

            if (enemy.phaseTransition) {
                boolean drawEnemy = ((int)(enemy.phaseTransitionTimer * 12f) % 2 == 0);

                if (!drawEnemy) {
                    continue;
                }
            }

            batch.draw(
                enemyFrame,
                drawX,
                drawY,
                drawSize,
                drawSize
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

        for (LifeItem item : lifeItems) {
            batch.draw(
                lifeTexture,
                item.x,
                item.y,
                item.size,
                item.size
            );
        }

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.begin(ShapeRenderer.ShapeType.Filled);

        if (currentBossEnemy != null &&
            currentBossEnemy.type == EnemyType.FINAL_BOSS &&
            currentBossEnemy.phase2Intro) {

            float sideWidth = 230f;

            float pulse =
                0.18f +
                    0.22f *
                        Math.abs(
                            (float)Math.sin(
                                currentBossEnemy.phase2IntroTimer * 8f
                            )
                        );

            shape.setColor(1f, 0f, 0f, pulse);

            shape.rect(
                playfieldX,
                playfieldY,
                sideWidth,
                playfieldHeight
            );

            shape.rect(
                playfieldX + playfieldWidth - sideWidth,
                playfieldY,
                sideWidth,
                playfieldHeight
            );
        }

        for (MarisaSideLaser laser : marisaSideLasers) {

            float outerBorder = 10f;
            float middleBorder = 8f;

            // borda azul externa, mais transparente
            shape.setColor(0.15f, 0.25f, 1f, 0.35f);
            shape.rect(
                laser.x,
                playfieldY,
                laser.width,
                playfieldHeight
            );

            // borda azul intermediária
            shape.setColor(0.45f, 0.65f, 1f, 0.55f);
            shape.rect(
                laser.x + outerBorder,
                playfieldY,
                laser.width - outerBorder * 2f,
                playfieldHeight
            );

            // centro branco maior
            shape.setColor(1f, 1f, 1f, 0.90f);
            shape.rect(
                laser.x + outerBorder + middleBorder,
                playfieldY,
                laser.width - (outerBorder + middleBorder) * 2f,
                playfieldHeight
            );
        }

        // barra de hp sub chefe
        if (currentBossEnemy != null) {
            float hpPercent = currentBossEnemy.hp / currentBossEnemy.maxHp;

            if (hpPercent < 0f) hpPercent = 0f;
            if (hpPercent > 1f) hpPercent = 1f;

            float barX = playfieldX + 18f;
            float barY = playfieldY + playfieldHeight - 28f;
            float barW = playfieldWidth - 36f;
            float barH = 10f;

            shape.setColor(0.15f, 0f, 0f, 0.75f);
            shape.rect(barX, barY, barW, barH);

            shape.setColor(1f, 0f, 0f, 1f);
            shape.rect(barX, barY, barW * hpPercent, barH);
        }

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
        for (EnemyBullet bullet : enemyBullets) {
            if (bullet.bossAimedShot) {
                shape.setColor(0.2f, 1f, 0.1f, 1f); // verde choque
            } else {
                shape.setColor(1f, 0f, 0.8f, 1f); // rosa normal
            }

            shape.circle(
                bullet.x,
                bullet.y,
                bullet.size / 2
            );
        }

        for (EnemyLaser laser : enemyLasers) {
            float alpha = laser.getAlpha();

            float coreWidth = laser.width;
            float glowWidth = laser.width * 2.8f;

            float topY = playfieldY + laser.length;

            // borda/aura transparente
            shape.setColor(1f, 1f, 1f, alpha * 0.22f);
            shape.rect(
                laser.x - glowWidth / 2f,
                playfieldY,
                glowWidth,
                laser.length
            );
            shape.circle(
                laser.x,
                topY,
                glowWidth / 2f
            );

            // núcleo branco forte
            shape.setColor(1f, 1f, 1f, alpha);
            shape.rect(
                laser.x - coreWidth / 2f,
                playfieldY,
                coreWidth,
                laser.length
            );
            shape.circle(
                laser.x,
                topY,
                coreWidth / 2f
            );
        }

        for (BossHorizontalLaser laser : bossHorizontalLasers) {

            float alpha = laser.getAlpha();

            float outerBorder = laser.width * 0.18f;
            float middleBorder = laser.width * 0.14f;

            // brilho externo (laranja escuro)
            //shape.setColor(1.0f, 0.35f, 0.0f, alpha * 0.30f);
            //shape.rect(
                //playfieldX,
               // laser.y - laser.width * 1.35f,
                //playfieldWidth,
               // laser.width * 2.70f
            //);

            // borda externa
            shape.setColor(1.0f, 0.45f, 0.05f, alpha * 0.40f);
            shape.rect(
                playfieldX,
                laser.y - laser.width / 2f,
                playfieldWidth,
                laser.width
            );

            // borda intermediária
            shape.setColor(1.0f, 0.72f, 0.18f, alpha * 0.65f);
            shape.rect(
                playfieldX,
                laser.y - laser.width / 2f + outerBorder,
                playfieldWidth,
                laser.width - outerBorder * 2f
            );

            // núcleo branco
            shape.setColor(1f, 1f, 1f, alpha);
            shape.rect(
                playfieldX,
                laser.y - laser.width / 2f + outerBorder + middleBorder,
                playfieldWidth,
                laser.width - (outerBorder + middleBorder) * 2f
            );
        }

        for (MasterSparkLaser laser : masterSparkLasers) {
            float alpha = laser.getAlpha();

            float baseY = playfieldY;
            float topY = laser.y;

            float centerX = laser.x;

            float outerWidth = laser.width * 1.18f;
            float middleWidth = laser.width * 1.08f;
            float innerWidth = laser.width;

            // borda azul escuro
            shape.setColor(0.05f, 0.15f, 1f, alpha);
            shape.rect(
                centerX - outerWidth / 2f,
                baseY,
                outerWidth,
                topY - baseY
            );
            shape.circle(
                centerX,
                topY,
                outerWidth / 2f
            );

            // borda azul claro
            shape.setColor(0.45f, 0.65f, 1f, alpha);
            shape.rect(
                centerX - middleWidth / 2f,
                baseY,
                middleWidth,
                topY - baseY
            );
            shape.circle(
                centerX,
                topY,
                middleWidth / 2f
            );

            // centro branco
            shape.setColor(1f, 1f, 1f, alpha);
            shape.rect(
                centerX - innerWidth / 2f,
                baseY,
                innerWidth,
                topY - baseY
            );
            shape.circle(
                centerX,
                topY,
                innerWidth / 2f
            );
        }

        shape.end();

        if (masterSparkFlashEffect) {

            shape.begin(ShapeRenderer.ShapeType.Filled);

            float progress = masterSparkFlashTimer / masterSparkFlashDuration;

            if (progress < 0f) progress = 0f;
            if (progress > 1f) progress = 1f;

            float alpha;

            if (progress > 0.92f) {
                // clarão inicial
                alpha = 0.80f;
            } else {

                float t = progress / 0.92f;

                // fade bem suave
                alpha = (float)Math.pow(t, 0.35f);

                alpha *= 0.80f;
            }

            shape.setColor(1f, 1f, 1f, alpha);
            shape.rect(playfieldX, playfieldY, playfieldWidth, playfieldHeight);

            shape.end();
        }

        if (currentBossEnemy != null) {
            batch.begin();

            float barX = playfieldX + 18f;
            float barY = playfieldY + playfieldHeight - 28f;
            float barH = 10f;

            String barNumber =
                "" + (currentBossEnemy.maxHealthBars - 1 - currentBossEnemy.healthBar);

            font.getData().setScale(0.8f);
            font.setColor(Color.WHITE);

            GlyphLayout layout = new GlyphLayout(font, barNumber);

            float textX = barX + 4f;
            float textY =
                barY + (barH / 2f) + (layout.height / 2f) - 1f;

            font.draw(batch, barNumber, textX, textY);

            font.getData().setScale(1.2f);

            String bossName;

            if (currentBossEnemy.type == EnemyType.FINAL_BOSS) {
                bossName = "Bruxa negra";
            } else {
                bossName = "Grande fada do bosque";
            }

            font.draw(
                batch,
                bossName,
                barX,
                barY - 8f
            );

            // Restaura escala padrão
            font.getData().setScale(2f);

            batch.end();
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);

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

        if (focusMode && !playerDead) {
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
        if (focusMode && !playerDead) {
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

        if (gameOver) {
            shape.begin(ShapeRenderer.ShapeType.Filled);
            Gdx.gl.glEnable(GL20.GL_BLEND);

            shape.setColor(0f, 0f, 0f, 0.75f);
            shape.rect(playfieldX, playfieldY, playfieldWidth, playfieldHeight);

            shape.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();

            font.setColor(Color.WHITE);

            font.getData().setScale(2.0f);
            String title = "VOCE MORREU";
            GlyphLayout titleLayout = new GlyphLayout(font, title);

            font.draw(
                batch,
                title,
                playfieldX + playfieldWidth / 2f - titleLayout.width / 2f,
                playfieldY + playfieldHeight / 2f + 50f
            );

            font.getData().setScale(1.25f);
            String restartText = "Aperte R para reiniciar o jogo";
            GlyphLayout restartLayout = new GlyphLayout(font, restartText);

            font.draw(
                batch,
                restartText,
                playfieldX + playfieldWidth / 2f - restartLayout.width / 2f,
                playfieldY + playfieldHeight / 2f - 10f
            );

            font.getData().setScale(2f);

            batch.end();
        }

        if (paused && !gameOver) {

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

            font.draw(
                batch,
                "PAUSADO",
                playfieldX + playfieldWidth / 2 - 80,
                playfieldY + playfieldHeight / 2 + 20
            );

            font.draw(
                batch,
                "APERTE R PARA REINICIAR",
                playfieldX + playfieldWidth / 2f - 170,
                playfieldY + playfieldHeight / 2f - 30
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

        if (showEndingScreen) {
            batch.begin();

            font.setColor(Color.WHITE);

            font.getData().setScale(2.0f);
            GlyphLayout titleLayout = new GlyphLayout(font, "FIM DE JOGO");

            font.draw(
                batch,
                "FIM DE JOGO",
                playfieldX + playfieldWidth / 2f - titleLayout.width / 2f,
                playfieldY + playfieldHeight / 2f + 80f
            );

            font.getData().setScale(1.35f);
            String endingText =
                "O incidente da bruxa negra e suas fadas foi resolvido!";

            GlyphLayout endingLayout = new GlyphLayout(font, endingText);

            font.draw(
                batch,
                endingText,
                playfieldX + playfieldWidth / 2f - endingLayout.width / 2f,
                playfieldY + playfieldHeight / 2f + 20f
            );

            font.getData().setScale(2f);
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
        redShot00.dispose();
        redShot02.dispose();
        redShot04.dispose();
        bombSound.dispose();
        whiteShot01.dispose();
        whiteShot02.dispose();
        whiteShot03.dispose();
        deathSound.dispose();
        laserSound.dispose();
        pointsSound.dispose();
        enemyDefeatedSound.dispose();
        powerUpSound.dispose();
        marisaLaserHitSound.dispose();
        lowLifeMusic.dispose();
        defeat01Sound.dispose();
        defeat02Sound.dispose();
        masterSparkSound.dispose();
        warningSound.dispose();
        lifeTexture.dispose();
        lifeSound.dispose();

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

        boolean bossAimedShot = false;

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

    class EnemyLaser {
        float x;
        float y;
        float width;
        float length;

        float timer = 0f;

        float fadeInTime = 0f;
        float activeTime = 0.60f;
        float fadeOutTime = 0.90f;

        EnemyLaser(float x, float y, float width, float length) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.length = length;
        }

        float getTotalTime() {
            return fadeInTime + activeTime + fadeOutTime;
        }

        float getAlpha() {
            if (fadeInTime > 0f && timer < fadeInTime) {
                return timer / fadeInTime;
            }

            if (timer < fadeInTime + activeTime) {
                return 1f;
            }

            float fadeOutTimer = timer - fadeInTime - activeTime;
            return 1f - fadeOutTimer / fadeOutTime;
        }
    }

    class MarisaSideLaser {
        float x;
        float width;

        MarisaSideLaser(float x, float width) {
            this.x = x;
            this.width = width;
        }
    }

    class BossHorizontalLaser {
        float y;
        float width = 32f;
        float timer = 0f;

        float followTime = 2.0f;
        float warningTime = 0.5f;
        float activeTime = 0.8f;
        float fadeOutTime = 0.7f;

        boolean locked = false;
        boolean hitSoundPlayed = false;

        float getTotalTime() {
            return followTime + warningTime + activeTime + fadeOutTime;
        }

        float getAlpha() {
            if (timer < followTime + warningTime) {
                return 0.35f;
            }

            if (timer < followTime + warningTime + activeTime) {
                return 1f;
            }

            float fadeTimer = timer - followTime - warningTime - activeTime;
            return 1f - fadeTimer / fadeOutTime;
        }

        boolean isDangerous() {
            return timer >= followTime + warningTime &&
                timer <= followTime + warningTime + activeTime;
        }
    }

    class MasterSparkLaser {
        float x;
        float y;
        float width = 650f; // tamanho master
        float length;

        float timer = 0f;

        float warningTime = 1.20f; // aviso master
        float activeTime = 4.0f;
        float fadeOutTime = 2.0f;

        boolean isDangerous() {
            return timer >= warningTime &&
                timer <= warningTime + activeTime;
        }

        float getTotalTime() {
            return warningTime + activeTime + fadeOutTime;
        }

        float getAlpha() {
            if (timer < warningTime) return 0.30f;
            if (timer < warningTime + activeTime) return 1f;

            float fadeTimer = timer - warningTime - activeTime;
            return 1f - fadeTimer / fadeOutTime;
        }
    }

    class Enemy {
        float x, y;
        float shotTimer = 0;
        float targetY;

        boolean lastStand = false;
        float lastStandTimer = 0f;

        boolean phase2Intro = false;
        float phase2IntroTimer = 0f;
        float phase2WarningSoundTimer = 0f;

        boolean finalSpellStarted = false;
        boolean finalSpellFinished = false;
        boolean damageLocked = false;

        float finalSpellTimer = 0f;

        boolean postMasterSparkStarted = false;
        float postMasterSparkDelay = 1.0f;
        float postMasterSparkTimer = 0f;
        float postMasterSparkShotTimer = 0f;

        float lockedX;
        float lockedY;

        float hp;
        float maxHp;

        float shotInterval;

        boolean bossLike = false;

        int patternShotCount = 0;

        int bossMoveDirection = 0;

        int healthBar = 0;
        int maxHealthBars = 1;

        float attackDelayTimer = 0f;

        float[] barMaxHp;
        int attackPhase = 0;

        boolean phaseTransition = false;
        float phaseTransitionTimer = 0f;

        float initialX;
        float initialY;

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
                    this.hp = 25f;
                    this.maxHp = 25f;
                    this.shotType = EnemyShotType.RADIAL;
                    this.shotInterval = 2.0f;
                    break;

                case RED_BURST:
                    this.hp = 55f;
                    this.maxHp = 55f;
                    this.shotType = EnemyShotType.RADIAL_BURST;
                    this.shotInterval = 4.0f;
                    break;

                case PURPLE:
                    this.hp = 20f;
                    this.maxHp = 20f;
                    this.shotType = EnemyShotType.LASER;
                    this.shotInterval = 3.5f;
                    break;

                case PURPLE_TANK:
                    this.hp = 70f;
                    this.maxHp = 70f;
                    this.shotType = EnemyShotType.LASER;
                    this.shotInterval = 4.5f;
                    break;

                case SUBBOSS:
                    this.bossLike = true;

                    this.maxHealthBars = 2;
                    this.healthBar = 0;

                    this.shotInterval = 2.2f;

                    this.barMaxHp = new float[] {
                        600f,
                        600f
                    };

                    this.hp = this.barMaxHp[0];
                    this.maxHp = this.barMaxHp[0];

                    this.attackPhase = 0;
                    this.shotType = EnemyShotType.SUBBOSS_BURST;
                    this.shotInterval = 2.8f;
                    break;

                case FINAL_BOSS:
                    this.bossLike = true;

                    this.maxHealthBars = 3;
                    this.healthBar = 0;

                    this.barMaxHp = new float[] {
                        1000f,
                        2500f,
                        4000f
                    };

                    this.hp = this.barMaxHp[0];
                    this.maxHp = this.barMaxHp[0];

                    this.attackPhase = 0;
                    this.shotType = EnemyShotType.FINAL_BOSS_SPIRAL;
                    this.shotInterval = 0.08f;

                    this.attackDelayTimer = 1.5f;

                    break;

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

    class LifeItem {
        float x;
        float y;
        float size;
        float velocityY;

        LifeItem(float x, float y) {
            this.x = x;
            this.y = y;
            this.size = 28f;
            this.velocityY = 260f;
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
            powerUpSound.play(0.50f);
            openCardSelection();

        }

        updatePowerShotUnlocks();

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
        RED_BURST,
        PURPLE,
        PURPLE_TANK,
        SUBBOSS,
        FINAL_BOSS
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
        RADIAL_BURST,
        LASER,
        SUBBOSS_BURST,
        FINAL_BOSS_SPIRAL
    }

    enum CardType {
        DAMAGE,
        FIRE_RATE,
        EXTRA_BOMB,
        SMALL_HITBOX,
        CRITICAL,
        SPEED_UP,
        FOCUS_SLOW,
        WITCH_STYLE,
        PRIESTESS_STYLE
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

        Array<CardType> pool = new Array<>();

        for (CardType type : CardType.values()) {

            if (isCardAvailable(type)) {
                pool.add(type);
            }
        }

        for (int i = 0; i < 3; i++) {

            if (pool.size > 0) {
                currentCards[i] = generateRandomCard(pool);
            }
            else {
                currentCards[i] = createCardByType(CardType.EXTRA_BOMB);
            }
        }
    }

    private Card generateRandomCard(Array<CardType> pool) {
        int index = random.nextInt(pool.size);
        CardType type = pool.get(index);
        pool.removeIndex(index);

        return createCardByType(type);
    }

    private boolean isCardAvailable(CardType type) {
        switch (type) {
            case DAMAGE:
                return playerDamage < 36.0f;

            case FIRE_RATE:
                return fireRateMultiplier < 1.75f;

            case SMALL_HITBOX:
                return hitboxMultiplier > 0.75f;

            case CRITICAL:
                return criticalChance < 0.25f;

            case SPEED_UP:
                return moveSpeedMultiplier < 1.30f;

            case FOCUS_SLOW:
                return focusSpeedMultiplier > 0.70f;

            case WITCH_STYLE:
                return witchStyleLevel < 2;

            case PRIESTESS_STYLE:
                return priestessStyleLevel < 2;

            case EXTRA_BOMB:
                return bombs < maxBombs;
        }

        return true;
    }

    private Card createCardByType(CardType type) {
        switch (type) {
            case FIRE_RATE:
                return new Card(
                    "Cadencia +",
                    "Aumenta a velocidade dos disparos.",
                    CardType.FIRE_RATE
                );

            case DAMAGE:
                return new Card(
                    "Dano +",
                    "Aumenta o dano dos disparos.",
                    CardType.DAMAGE
                );

            case EXTRA_BOMB:
                return new Card(
                    "Bomba +",
                    "Ganha uma bomba.",
                    CardType.EXTRA_BOMB
                );

            case SMALL_HITBOX:
                return new Card(
                    "Hitbox menor",
                    "Reduz sua hitbox em 10%.",
                    CardType.SMALL_HITBOX
                );

            case CRITICAL:
                return new Card(
                    "Critico +",
                    "Aumenta em 5% a chance de dano dobrado.",
                    CardType.CRITICAL
                );

            case SPEED_UP:
                return new Card(
                    "Velocidade +",
                    "Aumenta em 10% a velocidade normal.",
                    CardType.SPEED_UP
                );

            case FOCUS_SLOW:
                return new Card(
                    "Foco +",
                    "Reduz em 10% a velocidade no modo foco.",
                    CardType.FOCUS_SLOW
                );

            case WITCH_STYLE:
                return new Card(
                    "Bruxa Style",
                    "No modo foco, os disparos laterais ficam mais fechados.",
                    CardType.WITCH_STYLE
                );

            case PRIESTESS_STYLE:
                return new Card(
                    "Sacerdotisa Style",
                    "Sem modo foco, os disparos laterais ficam mais abertos.",
                    CardType.PRIESTESS_STYLE
                );
        }

        return new Card("Carta", "Sem efeito.", CardType.DAMAGE);
    }

    private void chooseCard(int index) {
        Card chosen = currentCards[index];

        applyCard(chosen);
        appliedCards.push(chosen);

        cardInvulnerabilityTimer = 0.5f;

        choosingCard = false;
    }

    private void applyCard(Card card) {
        switch (card.type) {
            case FIRE_RATE:
                fireRateMultiplier += 0.15f;
                break;

            case DAMAGE:
                playerDamage += 0.20f;
                break;

            case EXTRA_BOMB:
                bombs = Math.min(bombs + 1, maxBombs);
                break;

            case SMALL_HITBOX:
                hitboxMultiplier = Math.max(0.75f, hitboxMultiplier * 0.90f);
                break;

            case CRITICAL:
                criticalChance = Math.min(criticalChance + 0.05f, 0.25f);
                break;

            case SPEED_UP:
                moveSpeedMultiplier = Math.min(
                    moveSpeedMultiplier + 0.10f,
                    1.30f
                );
                break;

            case FOCUS_SLOW:
                focusSpeedMultiplier = Math.max(
                    focusSpeedMultiplier * 0.90f,
                    0.70f
                );
                break;

            case WITCH_STYLE:
                witchStyleLevel = Math.min(witchStyleLevel + 1, 2);
                break;

            case PRIESTESS_STYLE:
                priestessStyleLevel = Math.min(priestessStyleLevel + 1, 2);
                break;

        }
    }

    private void removeCard(Card card) {
        switch (card.type) {

            case DAMAGE:
                playerDamage -= 0.20f;
                break;

            case FIRE_RATE:
                fireRateMultiplier -= 0.15f;
                break;

            case SMALL_HITBOX:
                hitboxMultiplier = Math.min(
                    1.0f,
                    hitboxMultiplier / 0.90f
                );
                break;

            case CRITICAL:
                criticalChance = Math.max(
                    0f,
                    criticalChance - 0.05f
                );
                break;

            case SPEED_UP:
                moveSpeedMultiplier = Math.max(
                    1.0f,
                    moveSpeedMultiplier - 0.10f
                );
                break;

            case FOCUS_SLOW:
                focusSpeedMultiplier = Math.min(
                    1.0f,
                    focusSpeedMultiplier / 0.90f
                );
                break;

            case WITCH_STYLE:
                witchStyleLevel = Math.max(witchStyleLevel - 1, 0);
                break;

            case PRIESTESS_STYLE:
                priestessStyleLevel = Math.max(priestessStyleLevel - 1, 0);
                break;
        }
    }

    private void killPlayer() {
        if (playerDead || playerInvincible) {
            return;
        }

        lives--;

        if (lives < 0) {

            deathSound.play(0.42f);

            gameOver = true;
            paused = true;
            return;
        }

        dropPlayerPower();
        losePowerOnDeath();

        if (bombs < 3) {
            bombs = 3;
        }

        deathSound.play(0.42f);

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
            if (enemy.phaseTransition || enemy.damageLocked) {
                continue;
            }

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
                playWhiteShotSound();
                break;

            case RADIAL:
                shootRedFairyCirclePattern(centerX, centerY, 140f, 0f, 32);
                playRedShotSound();
                break;

            case RADIAL_BURST:
                shootRedFairyCirclePattern(centerX, centerY, 60f, 0f, 16);
                shootRedFairyCirclePattern(centerX, centerY, 95f, 11.25f, 16);
                shootRedFairyCirclePattern(centerX, centerY, 130f, 0f, 16);
                shootRedFairyCirclePattern(centerX, centerY, 165f, 11.25f, 16);
                playRedShotSound();
                break;

            case LASER:
                shootPurpleLaser(enemy);
                playLaserSoundOncePerFrame();
                break;

            case SUBBOSS_BURST:
                shootSubBossBurst(enemy);
                break;

            case FINAL_BOSS_SPIRAL:
                shootFinalBossSpiral(enemy);
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

    private void shootBossAimed(float centerX, float centerY) {
        float speed = 260f;

        float targetX = playerX + hitboxOffsetX;
        float targetY = playerY + hitboxOffsetY;

        float dx = targetX - centerX;
        float dy = targetY - centerY;

        float length = (float)Math.sqrt(dx * dx + dy * dy);

        if (length != 0) {
            dx /= length;
            dy /= length;
        }

        EnemyBullet bullet = new EnemyBullet(
            centerX,
            centerY,
            dx * speed,
            dy * speed,
            14
        );

        bullet.bossAimedShot = true;

        enemyBullets.add(bullet);
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

    private void shootDoubleRotatingRing(
        float centerX,
        float centerY,
        float speed,
        float angleOffset,
        int bulletCount
    ) {
        shootRedFairyCirclePattern(
            centerX,
            centerY,
            speed,
            angleOffset,
            bulletCount
        );

        shootRedFairyCirclePattern(
            centerX,
            centerY,
            speed,
            -angleOffset,
            bulletCount
        );
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

        if (enemy.type == EnemyType.FINAL_BOSS &&
            enemy.finalSpellStarted &&
            !enemy.finalSpellFinished) {

            enemy.x = enemy.lockedX;
            enemy.y = enemy.lockedY;
            enemy.bossMoveDirection = 0;
            return;
        }

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

    private void updatePattern4(float delta) {
        if (!pattern4Started && stageTimer >= 45f) {
            pattern4Started = true;
            pattern4Timer = 0f;
        }

        if (!pattern4Started) {
            return;
        }

        pattern4Timer += delta;

        if (!pattern4PurpleSpawned && pattern4Timer >= 0f) {
            float spawnY = playfieldY + playfieldHeight - enemySize;

            float x1 = playfieldX + playfieldWidth * 0.15f - enemySize / 2f;
            float x2 = playfieldX + playfieldWidth * 0.35f - enemySize / 2f;
            float x3 = playfieldX + playfieldWidth * 0.65f - enemySize / 2f;
            float x4 = playfieldX + playfieldWidth * 0.85f - enemySize / 2f;

            spawnEnemy(EnemyType.PURPLE, x1, spawnY);
            spawnEnemy(EnemyType.PURPLE, x2, spawnY);
            spawnEnemy(EnemyType.PURPLE, x3, spawnY);
            spawnEnemy(EnemyType.PURPLE, x4, spawnY);

            pattern4PurpleSpawned = true;
        }
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

            case PURPLE:
                spawnPowerItems(x, y, 20);
                break;

            case PURPLE_TANK:
                spawnPowerItems(x, y, 50);
                break;

            case RED_BURST:
                spawnPowerItems(x, y, 75);
                break;

            case RED:
                spawnPowerItems(x, y, 25);
                break;

            case SUBBOSS:
                spawnPowerItems(x, y, 150);

                for (int i = 0; i < 10; i++) {
                    PowerItem small = new PowerItem(
                        x + (float)(Math.random() * 80f - 40f),
                        y + (float)(Math.random() * 60f - 30f),
                        5,
                        20,
                        false
                    );

                    small.velocityY = 160f + i * 8f;
                    powerItems.add(small);
                }

                break;

            default:
                spawnPowerItems(x, y, enemyPowerReward);
                break;
        }
    }

    private void shootPurpleLaser(Enemy enemy) {
        float laserX = enemy.x + enemySize / 2f;
        float laserY = enemy.y + enemySize / 2f;

        enemyLasers.add(new EnemyLaser(
            laserX,
            laserY,
            26f,
            laserY - playfieldY
        ));
    }

    private void updatePattern5(float delta) {
        if (!pattern5Started && stageTimer >= 58f) {
            pattern5Started = true;
            pattern5Timer = 0f;
            pattern5SideIndex = 0;
            pattern5SpawnCount = 0;
            pattern5BurstSpawned = false;
        }

        if (!pattern5Started) {
            return;
        }

        pattern5Timer += delta;

        float sideDuration = 3.6f;
        float spawnInterval = 0.30f;

        if (pattern5SideIndex < 6) {
            boolean fromLeft = pattern5SideIndex % 2 == 0;

            if (pattern5SpawnCount < 10 &&
                pattern5Timer >= pattern5SpawnCount * spawnInterval) {

                float x = fromLeft
                    ? playfieldX
                    : playfieldX + playfieldWidth - enemySize;

                float y = playfieldY + playfieldHeight - enemySize;

                spawnEnemy(
                    EnemyType.WHITE_PASSIVE,
                    fromLeft
                        ? EnemyMovementType.WHITE_LOOP_LEFT
                        : EnemyMovementType.WHITE_LOOP_RIGHT,
                    x,
                    y
                );

                pattern5SpawnCount++;
            }

            if (pattern5Timer >= sideDuration) {
                pattern5SideIndex++;
                pattern5SpawnCount = 0;
                pattern5Timer = 0f;
            }
        }

        if (!pattern5BurstSpawned && pattern5SideIndex >= 5) {
            spawnEnemy(
                EnemyType.RED_BURST,
                playfieldX + 160,
                playfieldY + playfieldHeight - enemySize
            );

            spawnEnemy(
                EnemyType.RED_BURST,
                playfieldX + playfieldWidth - enemySize - 160,
                playfieldY + playfieldHeight - enemySize
            );

            pattern5BurstSpawned = true;
        }
    }

    private void updatePattern6(float delta) {
        if (!pattern6Started && stageTimer >= 84f) {
            pattern6Started = true;
            pattern6Timer = 0f;
        }

        if (!pattern6Started) {
            return;
        }

        pattern6Timer += delta;


        if (!pattern6OuterRedsSpawned && pattern6Timer >= 0f) {
            spawnEnemy(
                EnemyType.RED,
                playfieldX + 60,
                playfieldY + playfieldHeight - enemySize
            );

            spawnEnemy(
                EnemyType.RED,
                playfieldX + playfieldWidth - enemySize - 60,
                playfieldY + playfieldHeight - enemySize
            );

            pattern6OuterRedsSpawned = true;
        }

        if (!pattern6InnerRedsSpawned && pattern6Timer >= 3f) {
            spawnEnemy(
                EnemyType.RED,
                playfieldX + 210,
                playfieldY + playfieldHeight - enemySize
            );

            spawnEnemy(
                EnemyType.RED,
                playfieldX + playfieldWidth - enemySize - 210,
                playfieldY + playfieldHeight - enemySize
            );

            pattern6InnerRedsSpawned = true;
        }

        if (!pattern6PurpleSpawned && pattern6Timer >= 4f) {
            spawnEnemy(
                EnemyType.PURPLE_TANK,
                playfieldX + playfieldWidth / 2f - enemySize / 2f,
                playfieldY + playfieldHeight - enemySize
            );

            pattern6PurpleSpawned = true;
        }
    }

    private void updateSubBossSpawn(float delta) {

        if (!subBossSpawned &&
            pattern6PurpleSpawned &&
            enemies.size == 0) {

            if (!subBossSpawnDelayStarted) {
                subBossSpawnDelayStarted = true;
                subBossSpawnDelay = 4f;
            }

            subBossSpawnDelay -= delta;

            if (subBossSpawnDelay <= 0f) {
                subBossEnemy = spawnEnemy(
                    EnemyType.SUBBOSS,
                    playfieldX + playfieldWidth / 2f - enemySize / 2f,
                    playfieldY + playfieldHeight - enemySize
                );

                currentBossEnemy = subBossEnemy;
                subBossSpawned = true;
            }
        }
    }

    private void updateBossPhase(Enemy enemy) {
        if (!enemy.bossLike || enemy.phaseTransition) {
            return;
        }

        float hpPercent = enemy.hp / enemy.maxHp;

        if (hpPercent <= 0.30f && enemy.attackPhase == 0) {

            if (!lowLifePlaying) {
                lowLifeMusic.play();
                lowLifePlaying = true;
            }

            enemy.attackPhase = 1;

            if (enemy.type == EnemyType.FINAL_BOSS) {
                return;
            }

            if (enemy.healthBar == 0) {
                enemy.shotInterval = 1.5f;
            } else {
                enemy.shotInterval = 1.2f;
            }
        }
    }

    private void breakBossHealthBar(Enemy enemy) {
        if (enemy.phaseTransition) {
            return;
        }

        dropBossBarPower(enemy);

        bombEffect = true;
        bombTimer = bombDuration;

        enemyDefeatedSound.play(0.45f);
        defeat01Sound.play(0.60f);

        enemyBullets.clear();
        enemyLasers.clear();
        bossHorizontalLasers.clear();
        marisaSideLasers.clear();

        enemy.phaseTransition = true;

        if (lowLifePlaying) {
            lowLifeMusic.stop();
            lowLifePlaying = false;
        }

        enemy.phaseTransitionTimer = 3f;

        enemy.x = playfieldX + playfieldWidth / 2f - enemySize / 2f;
        enemy.y = playfieldY + playfieldHeight - 300f;

        enemy.shotTimer = 0f;
    }

    private void dropBossBarPower(Enemy enemy) {
        float centerX = enemy.x + enemySize / 2f;
        float centerY = enemy.y + enemySize / 2f;

        for (int i = 0; i < 3; i++) {
            PowerItem big = new PowerItem(
                centerX + (i - 1) * 24f,
                centerY,
                25,
                28,
                true
            );

            big.velocityY = 260f + i * 20f;
            powerItems.add(big);
        }

        PowerItem smallLeft = new PowerItem(centerX - 42f, centerY, 5, 20, false);
        smallLeft.velocityY = 220f;
        powerItems.add(smallLeft);

        PowerItem smallRight = new PowerItem(centerX + 42f, centerY, 5, 20, false);
        smallRight.velocityY = 220f;
        powerItems.add(smallRight);
    }

    private void updateBossTransition(Enemy enemy, float delta) {
        if (!enemy.phaseTransition) {
            return;
        }

        enemy.phaseTransitionTimer -= delta;

        if (enemy.phaseTransitionTimer <= 0f) {
            enemy.phaseTransition = false;

            enemy.healthBar++;

            enemy.hp = enemy.barMaxHp[enemy.healthBar];
            enemy.maxHp = enemy.barMaxHp[enemy.healthBar];

            enemy.attackPhase = 0;
            enemy.shotTimer = 0f;

            enemy.patternShotCount = 0;

            if (enemy.type == EnemyType.FINAL_BOSS) {

                if (enemy.healthBar == 2) {
                    enemy.shotInterval = 0.30f;
                } else {
                    enemy.shotInterval = 0.08f;
                }

                if (enemy.type == EnemyType.FINAL_BOSS &&
                    enemy.healthBar == 1) {

                    startMarisaPhase2Intro(enemy);
                }

            } else {
                enemy.shotInterval = 2.4f;
            }

        }
    }

    private void shootSubBossBurst(Enemy enemy) {
        float centerX = enemy.x + enemySize / 2f;
        float centerY = enemy.y + enemySize / 2f;

        playBossShotSound();

        if (enemy.healthBar == 0) {
            // PRIMEIRA BARRA
            if (enemy.attackPhase == 0) {
                shootRedFairyCirclePattern(centerX, centerY, 65f, 0f, 18);
                shootRedFairyCirclePattern(centerX, centerY, 90f, 10f, 18);
                shootRedFairyCirclePattern(centerX, centerY, 115f, 0f, 18);
                shootRedFairyCirclePattern(centerX, centerY, 140f, 10f, 18);
                shootRedFairyCirclePattern(centerX, centerY, 165f, 0f, 18);
                shootRedFairyCirclePattern(centerX, centerY, 190f, 10f, 18);

                shootBossAimed(centerX - 24f, centerY);
                shootBossAimed(centerX + 24f, centerY);
            } else {
                shootRedFairyCirclePattern(centerX, centerY, 65f, 0f, 22);
                shootRedFairyCirclePattern(centerX, centerY, 90f, 8f, 22);
                shootRedFairyCirclePattern(centerX, centerY, 115f, 16f, 22);
                shootRedFairyCirclePattern(centerX, centerY, 140f, 24f, 22);
                shootRedFairyCirclePattern(centerX, centerY, 165f, 32f, 22);
                shootRedFairyCirclePattern(centerX, centerY, 190f, 40f, 22);

                shootBossAimed(centerX - 36f, centerY);
                shootBossAimed(centerX, centerY);
                shootBossAimed(centerX + 36f, centerY);
            }

            enemy.patternShotCount++;
            return;
        }

        // SEGUNDA BARRA
        float rotation = enemy.patternShotCount * 18f;

        if (enemy.attackPhase == 0) {

            // 4 ondas
            shootDoubleRotatingRing(centerX, centerY, 70f, rotation, 16);
            shootBossAimed(centerX, centerY);

            shootDoubleRotatingRing(centerX, centerY, 100f, rotation + 8f, 24);
            shootBossAimed(centerX - 28f, centerY);

            shootDoubleRotatingRing(centerX, centerY, 130f, rotation + 16f, 24);
            shootBossAimed(centerX + 28f, centerY);

            shootDoubleRotatingRing(centerX, centerY, 160f, rotation + 24f, 24);
            shootBossAimed(centerX, centerY);

        } else {

            // 6 ondas nos 30%
            shootDoubleRotatingRing(centerX, centerY, 60f, rotation, 14);
            shootDoubleRotatingRing(centerX, centerY, 85f, rotation + 6f, 20);
            shootDoubleRotatingRing(centerX, centerY, 110f, rotation + 12f, 20);
            shootDoubleRotatingRing(centerX, centerY, 135f, rotation + 18f, 20);
            shootDoubleRotatingRing(centerX, centerY, 160f, rotation + 24f, 20);
            shootDoubleRotatingRing(centerX, centerY, 185f, rotation + 30f, 20);

            shootBossAimed(centerX - 42f, centerY);
            shootBossAimed(centerX - 18f, centerY);
            shootBossAimed(centerX + 18f, centerY);
            shootBossAimed(centerX + 42f, centerY);
        }

        enemy.patternShotCount++;
    }

    private void updatePostBossPattern(float delta) {

        if (!postBossPatternStarted) {
            return;
        }

        if (postBossWave >= 2) {
            if (!pattern8Waiting && enemies.size == 0) {
                pattern8Waiting = true;
                pattern8Timer = 0f;
            }

            return;
        }

        postBossPatternTimer += delta;

        if (postBossSpawnCount < 10 &&
            postBossPatternTimer >= 0.35f) {

            postBossPatternTimer = 0f;

            float leftX = playfieldX;
            float rightX = playfieldX + playfieldWidth - enemySize;

            float spawnY =
                playfieldY + playfieldHeight - enemySize - 40;

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

            postBossSpawnCount++;
        }

        if (postBossSpawnCount >= 10 &&
            postBossPatternTimer >= 4f) {

            postBossWave++;
            postBossSpawnCount = 0;
            postBossPatternTimer = 0f;
        }
    }

    private void updatePattern8(float delta) {

        if (!pattern8Waiting) {
            return;
        }

        pattern8Timer += delta;

        if (!pattern8Started && pattern8Timer >= 5f) {
            pattern8Started = true;
            pattern8Timer = 0f;
        }

        if (!pattern8Started) {
            return;
        }

        if (!pattern8Spawned) {
            float spawnY = playfieldY + playfieldHeight - enemySize;

            spawnEnemy(
                EnemyType.RED,
                playfieldX + 20f,
                spawnY
            );

            spawnEnemy(
                EnemyType.RED,
                playfieldX + playfieldWidth - enemySize - 20f,
                spawnY
            );

            spawnEnemy(
                EnemyType.PURPLE_TANK,
                playfieldX + playfieldWidth * 0.35f,
                spawnY
            );

            spawnEnemy(
                EnemyType.PURPLE_TANK,
                playfieldX + playfieldWidth * 0.65f - enemySize,
                spawnY
            );

            pattern8Spawned = true;
        }

        pattern8Spawned = true;

    }

    private void updatePattern9(float delta) {

        if (!pattern8Spawned) {
            return;
        }

        if (!pattern9Started && enemies.size == 0) {
            pattern9Started = true;
            pattern9Timer = 0f;
            pattern9SpawnCount = 0;
            pattern9PurpleSpawned = false;
        }

        if (!pattern9Started) {
            return;
        }

        pattern9Timer += delta;

        if (!pattern9PurpleSpawned && pattern9Timer >= 2f) {
            float spawnY = playfieldY + playfieldHeight - enemySize;

            for (int i = 0; i < 5; i++) {
                float x = playfieldX + playfieldWidth * (0.10f + i * 0.20f) - enemySize / 2f;

                spawnEnemy(
                    EnemyType.PURPLE_TANK,
                    x,
                    spawnY
                );
            }

            pattern9PurpleSpawned = true;
        }

        if (pattern9SpawnCount < 15 &&
            pattern9Timer >= 3f + pattern9SpawnCount * 0.28f) {

            float spawnY = playfieldY + playfieldHeight - enemySize - 40;

            spawnEnemy(
                EnemyType.WHITE,
                EnemyMovementType.WHITE_LOOP_LEFT,
                playfieldX,
                spawnY
            );

            spawnEnemy(
                EnemyType.WHITE,
                EnemyMovementType.WHITE_LOOP_RIGHT,
                playfieldX + playfieldWidth - enemySize,
                spawnY
            );

            pattern9SpawnCount++;

        }

        if (pattern9Started &&
            pattern9PurpleSpawned &&
            pattern9SpawnCount >= 15 &&
            enemies.size == 0 &&
            !pattern10Started) {

            pattern10Started = true;
            pattern10Timer = 0f;
        }

    }

    private void updatePattern10(float delta) {

        if (!pattern10Started) {
            return;
        }

        pattern10Timer += delta;

        if (pattern10Side == 0) {

            if (pattern10Spawned < 15 &&
                pattern10Timer >= 0.12f) {

                pattern10Timer = 0f;

                float topY =
                    playfieldY + playfieldHeight - enemySize - 40f;

                float bottomY =
                    playfieldY + playfieldHeight - enemySize - 180f;

                spawnEnemy(
                    EnemyType.WHITE_PASSIVE,
                    EnemyMovementType.WHITE_LOOP_LEFT,
                    playfieldX,
                    topY
                );

                spawnEnemy(
                    EnemyType.WHITE_PASSIVE,
                    EnemyMovementType.WHITE_LOOP_LEFT,
                    playfieldX,
                    bottomY
                );

                pattern10Spawned++;
            }

            if (pattern10Spawned >= 15 &&
                enemies.size == 0) {

                pattern10Side = 1;
                pattern10Spawned = 0;
                pattern10Timer = 0f;
            }
        }

        else {

            if (pattern10Spawned < 15 &&
                pattern10Timer >= 0.12f) {

                pattern10Timer = 0f;

                float topY =
                    playfieldY + playfieldHeight - enemySize - 40f;

                float bottomY =
                    playfieldY + playfieldHeight - enemySize - 180f;

                spawnEnemy(
                    EnemyType.WHITE_PASSIVE,
                    EnemyMovementType.WHITE_LOOP_RIGHT,
                    playfieldX + playfieldWidth - enemySize,
                    topY
                );

                spawnEnemy(
                    EnemyType.WHITE_PASSIVE,
                    EnemyMovementType.WHITE_LOOP_RIGHT,
                    playfieldX + playfieldWidth - enemySize,
                    bottomY
                );

                pattern10Spawned++;
            }

            if (pattern10Spawned >= 15 &&
                enemies.size == 0 &&
                !finalBossSpawned) {

                if (!finalBossSpawnDelayStarted) {
                    finalBossSpawnDelayStarted = true;
                    finalBossSpawnDelay = 5f;
                }

                finalBossSpawnDelay -= delta;

                if (finalBossSpawnDelay <= 0f) {

                    pattern10Started = false;
                    finalBossSpawned = true;

                    currentBossEnemy = spawnEnemy(
                        EnemyType.FINAL_BOSS,
                        playfieldX + playfieldWidth / 2f - 40f,
                        playfieldY + playfieldHeight - 78f
                    );
                }
            }
        }
    }

    private void spawnDebugFinalBoss() {
        if (finalBossSpawned) {
            return;
        }

        enemies.clear();
        enemyBullets.clear();
        enemyLasers.clear();

        finalBossSpawned = true;

        currentBossEnemy = spawnEnemy(
            EnemyType.FINAL_BOSS,
            playfieldX + playfieldWidth / 2f - 40f,
            playfieldY + playfieldHeight - 78f
        );
    }

    private void shootFinalBossSpiral(Enemy enemy) {
        float centerX = enemy.x + 40f;
        float centerY = enemy.y + 39f;

        playBossShotSound();

        // TERCEIRA BARRA
        if (enemy.healthBar == 2) {

            float hpPercent = enemy.hp / enemy.maxHp;

            if (hpPercent <= 0.30f && !lowLifePlaying) {
                lowLifeMusic.play();
                lowLifePlaying = true;
            }

            if (enemy.lastStand) {
                enemy.patternShotCount++;
                return;
            }

            if (hpPercent <= 0.15f && !enemy.lastStand) {

                enemy.lastStand = true;
                enemy.patternShotCount = 0;
                enemy.shotTimer = 0f;

                enemyBullets.clear();
                enemyLasers.clear();
                bossHorizontalLasers.clear();

                bombEffect = true;
                bombTimer = bombDuration;

                enemyDefeatedSound.play(0.45f);

                return;
            }

            if (hpPercent <= 0.50f) {

                if (enemy.patternShotCount % 15 == 0) {
                    shootBossAimed(centerX, centerY);
                }
            }

            float baseAngle = enemy.patternShotCount * 8f;

            int arms = 24;
            float speed = 85f;

            for (int i = 0; i < arms; i++) {

                float angle =
                    (float)Math.toRadians(
                        baseAngle + i * (360f / arms)
                    );

                enemyBullets.add(
                    new EnemyBullet(
                        centerX,
                        centerY,
                        (float)Math.cos(angle) * speed,
                        (float)Math.sin(angle) * speed,
                        14
                    )
                );
            }

            enemy.patternShotCount++;
            return;
        }

        // SEGUNDA BARRA
        if (enemy.healthBar == 1) {

            if (enemy.patternShotCount % 45 == 0) {
                shootMarisaHorizontalLaser(enemy);
            }

            float hpPercent = enemy.hp / enemy.maxHp;

            int shotgunCount = 3;
            int shotgunInterval = 14;

            if (hpPercent <= 0.30f) {

                shotgunCount = 5;
                shotgunInterval = 8;

                if (!lowLifePlaying) {
                    lowLifeMusic.play();
                    lowLifePlaying = true;
                }

            }

            if (enemy.patternShotCount % shotgunInterval == 0) {
                shootBossShotgun(
                    centerX,
                    centerY,
                    shotgunCount
                );
            }

            enemy.patternShotCount++;
            return;
        }

        // PRIMEIRA BARRA
        float baseAngle = enemy.patternShotCount * 26f;

        int arms = 10;
        float speed = 250f;

        for (int i = 0; i < arms; i++) {

            float angle =
                (float)Math.toRadians(
                    baseAngle + i * (360f / arms)
                );

            float speedX =
                (float)Math.cos(angle) * speed;

            float speedY =
                (float)Math.sin(angle) * speed;

            enemyBullets.add(
                new EnemyBullet(
                    centerX,
                    centerY,
                    speedX,
                    speedY,
                    14
                )
            );
        }

        float hpPercent = enemy.hp / enemy.maxHp;

        if (hpPercent <= 0.30f) {

            if (!lowLifePlaying) {
                lowLifeMusic.play();
                lowLifePlaying = true;
            }

            if (enemy.patternShotCount % 10 == 0) {
                shootBossAimed(centerX - 25f, centerY);
                shootBossAimed(centerX, centerY);
                shootBossAimed(centerX + 25f, centerY);
            }
        }

        enemy.patternShotCount++;
    }

    private void shootMarisaHorizontalLaser(Enemy enemy) {
        BossHorizontalLaser laser = new BossHorizontalLaser();
        laser.y = playerY + hitboxOffsetY;
        bossHorizontalLasers.add(laser);

        playLaserSoundOncePerFrame();
    }

    private void shootBossShotgun(
        float centerX,
        float centerY,
        int count
    ) {
        float speed = 270f;

        float targetX = playerX + hitboxOffsetX;
        float targetY = playerY + hitboxOffsetY;

        float baseAngle =
            (float)Math.atan2(
                targetY - centerY,
                targetX - centerX
            );

        float spread = 12f;

        for (int i = 0; i < count; i++) {

            float offset =
                i - (count - 1) / 2f;

            float angle =
                baseAngle +
                    (float)Math.toRadians(
                        offset * spread
                    );

            EnemyBullet bullet =
                new EnemyBullet(
                    centerX,
                    centerY,
                    (float)Math.cos(angle) * speed,
                    (float)Math.sin(angle) * speed,
                    14
                );

            bullet.bossAimedShot = true;

            enemyBullets.add(bullet);
        }
    }

    private void updateMarisaLastStand(Enemy enemy) {

        float targetY =
            playfieldY +
                playfieldHeight -
                75f;

        float oldX = enemy.x;

        enemy.y += (targetY - enemy.y) * 0.08f;

        float targetX =
            playerX +
                hitboxOffsetX -
                40f;

        enemy.x += (targetX - enemy.x) * 0.06f;

        float moveAmount = enemy.x - oldX;

        if (moveAmount > 0.3f) {
            enemy.bossMoveDirection = 1;
        }
        else if (moveAmount < -0.3f) {
            enemy.bossMoveDirection = -1;
        }
        else {
            enemy.bossMoveDirection = 0;
        }

        enemy.x = Math.max(
            playfieldX,
            Math.min(
                enemy.x,
                playfieldX + playfieldWidth - enemySize
            )
        );
    }

    private void startMarisaMasterSpark(Enemy enemy) {
        enemy.finalSpellStarted = true;
        enemy.damageLocked = true;

        enemy.lockedX = enemy.x;
        enemy.lockedY = enemy.y;

        enemy.bossMoveDirection = 0;

        enemyBullets.clear();
        enemyLasers.clear();
        bossHorizontalLasers.clear();

        masterSparkFlashEffect = true;
        masterSparkFlashTimer = masterSparkFlashDuration;

        masterSparkSound.play(0.55f);

        startScreenShake(7.20f, 10f);

        MasterSparkLaser laser = new MasterSparkLaser();

        laser.x = enemy.x + 40f;
        laser.y = enemy.y - 350f; // altura master
        laser.length = laser.y - playfieldY;

        masterSparkLasers.add(laser);
    }

    private void playRedShotSound() {

        float volume = 0.30f + random.nextFloat() * 0.08f;

        switch (random.nextInt(3)) {

            case 0:
                redShot00.play(volume);
                break;

            case 1:
                redShot02.play(volume);
                break;

            default:
                redShot04.play(volume);
                break;
        }
    }

    private void playWhiteShotSound() {

        float volume = 0.22f + random.nextFloat() * 0.06f;

        switch (random.nextInt(3)) {

            case 0:
                whiteShot01.play(volume);
                break;

            case 1:
                whiteShot02.play(volume);
                break;

            default:
                whiteShot03.play(volume);
                break;
        }
    }

    private void playBossShotSound() {

        if (bossShotSoundTimer < bossShotSoundCooldown) {
            return;
        }

        bossShotSoundTimer = 0f;

        float volume = 0.05f + random.nextFloat() * 0.02f;

        switch (random.nextInt(3)) {

            case 0:
                whiteShot01.play(volume);
                break;

            case 1:
                whiteShot02.play(volume);
                break;

            default:
                whiteShot03.play(volume);
                break;
        }
    }

    private void playLaserSoundOncePerFrame() {
        if (laserSoundPlayedThisFrame) {
            return;
        }

        laserSound.play(0.38f);
        laserSoundPlayedThisFrame = true;
    }

    private void startScreenShake(float duration, float intensity) {
        screenShakeTimer = duration;
        screenShakeIntensity = intensity;
    }

    private void updateMarisaPostMasterSpark(Enemy enemy, float delta) {

        if (enemy.postMasterSparkDelay > 0f) {
            enemy.postMasterSparkDelay -= delta;
            enemy.bossMoveDirection = 0;
            return;
        }

        enemy.postMasterSparkTimer += delta;
        enemy.postMasterSparkShotTimer += delta;

        float centerX =
            playfieldX +
                playfieldWidth / 2f -
                enemySize / 2f;

        float centerY =
            playfieldY +
                playfieldHeight -
                220f;

        float horizontalRadius =
            playfieldWidth / 2f -
                enemySize / 2f -
                20f;

        float verticalRadius = 45f;

        float speed = 2.6f;

        float oldX = enemy.x;

        float t = enemy.postMasterSparkTimer * speed;

        enemy.x =
            centerX +
                (float)Math.sin(t) *
                    horizontalRadius;

        enemy.y =
            centerY +
                (float)Math.sin(t * 2f) *
                    verticalRadius;

        float moveAmount = enemy.x - oldX;

        if (moveAmount > 0.3f) {
            enemy.bossMoveDirection = 1;
        } else if (moveAmount < -0.3f) {
            enemy.bossMoveDirection = -1;
        } else {
            enemy.bossMoveDirection = 0;
        }

        if (enemy.postMasterSparkShotTimer >= 0.18f) {
            shootBossAimed(enemy.x + 40f, enemy.y + 39f);
            enemy.postMasterSparkShotTimer = 0f;
        }
    }

    private void startMarisaPhase2Intro(Enemy enemy) {
        enemy.phase2Intro = true;
        enemy.phase2IntroTimer = 4f;
        enemy.phase2WarningSoundTimer = 0f;

        enemy.damageLocked = true;
        enemy.shotTimer = 0f;
        enemy.patternShotCount = 0;

        enemyBullets.clear();
        enemyLasers.clear();
        bossHorizontalLasers.clear();
        marisaSideLasers.clear();
    }

    private void updateMarisaPhase2Intro(Enemy enemy, float delta) {
        if (!enemy.phase2Intro) {
            return;
        }

        enemy.damageLocked = true;
        enemy.shotTimer = 0f;
        enemy.bossMoveDirection = 0;

        enemy.phase2IntroTimer -= delta;
        enemy.phase2WarningSoundTimer -= delta;

        if (enemy.phase2WarningSoundTimer <= 0f) {
            warningSound.play(0.55f);
            enemy.phase2WarningSoundTimer = 1f;
        }

        if (enemy.phase2IntroTimer <= 0f) {
            enemy.phase2Intro = false;
            enemy.damageLocked = false;

            spawnMarisaSideLasers();

            enemy.shotTimer = 0f;
            enemy.patternShotCount = 0;
        }
    }

    private void spawnMarisaSideLasers() {
        marisaSideLasers.clear();

        float sideWidth = 230f;

        marisaSideLasers.add(
            new MarisaSideLaser(
                playfieldX,
                sideWidth
            )
        );

        marisaSideLasers.add(
            new MarisaSideLaser(
                playfieldX + playfieldWidth - sideWidth,
                sideWidth
            )
        );

        playLaserSoundOncePerFrame();
    }

    private void dropLifeItem(Enemy enemy) {
        lifeItems.add(
            new LifeItem(
                enemy.x + enemySize / 2f - 14f,
                enemy.y + enemySize / 2f
            )
        );
    }

    private void updatePowerShotUnlocks() {
        float power = powerPoints / 100f;

        homingShotLevel = 0;
        sideShotLevel = 0;

        if (power >= 3f) {
            homingShotLevel = 1;
        }

        if (power >= 6f) {
            sideShotLevel = 1;
        }

        if (power >= 9f) {
            homingShotLevel = 2;
        }

        if (power >= 12f) {
            sideShotLevel = 2;
        }
    }


    private void restartGame() {
        enemies.clear();
        bullets.clear();
        enemyBullets.clear();
        enemyLasers.clear();
        bossHorizontalLasers.clear();
        masterSparkLasers.clear();
        powerItems.clear();

        currentBossEnemy = null;
        subBossEnemy = null;

        finalBossSpawned = false;
        subBossSpawned = false;

        stageTimer = 0f;

        spawnedFirstRed = false;
        spawnedSecondRed = false;

        pattern2Started = false;
        pattern3Started = false;
        pattern4Started = false;
        pattern5Started = false;
        pattern6Started = false;
        postBossPatternStarted = false;
        pattern8Started = false;
        pattern9Started = false;
        pattern10Started = false;

        lives = 2;
        bombs = 3;
        score = 0;
        powerPoints = 0;

        sideShotLevel = 0;
        homingShotLevel = 0;
        fireRateMultiplier = 1.0f;
        playerDamage = 1.0f;

        playerDead = false;
        playerInvincible = false;
        choosingCard = false;
        paused = false;

        playerX = playfieldX + playfieldWidth / 2f - playerDrawWidth / 2f;
        playerY = playfieldY + 80;

        gameFinished = false;
        gameFinishedTimer = 0f;
        showEndingScreen = false;

        pattern2Timer = 0f;
        pattern2SpawnCount = 0;

        pattern3Timer = 0f;
        pattern3LeftCount = 0;
        pattern3RightCount = 0;
        pattern3RedSpawned = false;
        pattern3BurstSpawned = false;

        pattern4Timer = 0f;
        pattern4PurpleSpawned = false;

        pattern5Timer = 0f;
        pattern5SideIndex = 0;
        pattern5SpawnCount = 0;
        pattern5BurstSpawned = false;

        pattern6Timer = 0f;
        pattern6OuterRedsSpawned = false;
        pattern6InnerRedsSpawned = false;
        pattern6PurpleSpawned = false;

        postBossPatternTimer = 0f;
        postBossWave = 0;
        postBossSpawnCount = 0;

        pattern8Timer = 0f;
        pattern8Spawned = false;
        pattern8Waiting = false;

        pattern9Timer = 0f;
        pattern9SpawnCount = 0;
        pattern9PurpleSpawned = false;

        pattern10Timer = 0f;
        pattern10Side = 0;
        pattern10Spawned = 0;

        gameOver = false;

        gameFinished = false;
        gameFinishedTimer = 0f;
        showEndingScreen = false;

        currentBossEnemy = null;
        subBossEnemy = null;

        bombEffect = false;
        bombTimer = 0f;

        cardInvulnerabilityTimer = 0f;

        masterSparkLasers.clear();
        bossHorizontalLasers.clear();
        enemyLasers.clear();
        enemyBullets.clear();
        lowLifeMusic.stop();
        lowLifePlaying = false;

        finalBossSpawnDelayStarted = false;
        finalBossSpawnDelay = 6f;

        subBossSpawnDelayStarted = false;
        subBossSpawnDelay = 6f;

        hitboxMultiplier = 1.0f;

        criticalChance = 0f;

        moveSpeedMultiplier = 1.0f;

        focusSpeedMultiplier = 1.0f;

        witchStyleLevel = 0;

        priestessStyleLevel = 0;

    }

}
