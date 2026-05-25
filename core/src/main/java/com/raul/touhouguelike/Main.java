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

    private final float bulletDrawWidth = 20;
    private final float bulletDrawHeight = 38;

    // inimigo
    private float enemyX = playfieldX + 150;
    private float enemyY = playfieldY + playfieldHeight;
    private final float enemySize = 40;
    private final float enemySpeed = 150;

    private boolean enemyAlive = true;

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

    //power
    private Array<PowerItem> powerItems;
    private final float powerItemFallSpeed = 90;
    private final int enemyPowerReward = 5; // +0.05 Power

    //hud
    private SpriteBatch batch;
    private BitmapFont font;

    //musica
    private Music backgroundMusic;

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

        idleTextures = new Texture[8];
        rightTextures = new Texture[8];
        leftTextures = new Texture[8];

        for (int i = 0; i < 8; i++) {
            idleTextures[i] = new Texture("parado" + (i + 1) + ".png");
            rightTextures[i] = new Texture("direita" + (i + 1) + ".png");
            leftTextures[i] = new Texture("esquerda" + (i + 1) + ".png");
        }

    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }
        if (!paused) {
            // ativa modo foco com CTRL
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
            float length = (float)Math.sqrt(moveX * moveX + moveY * moveY);
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

            if (Gdx.input.isKeyPressed(Input.Keys.Z) && shootTimer >= shootCooldown) {
                bullets.add(new Bullet(
                    playerX + hitboxOffsetX - bulletDrawWidth / 2,
                    playerY + hitboxOffsetY + 20
                ));

                shootTimer = 0;
            }

            //bomba
            if (Gdx.input.isKeyJustPressed(Input.Keys.X) && bombs > 0) {
                bombs--;

                score += 100;

                enemyY = playfieldY + playfieldHeight - enemySize;
                enemyX = playfieldX + (float)(Math.random() * (playfieldWidth - enemySize));

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
            for (int i = bullets.size - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);

                bullet.y += bulletSpeed * delta;

                // colisão tiro + inimigo
                if (enemyAlive && isCollidingRect(
                    bullet.x,
                    bullet.y,
                    bulletDrawWidth,
                    bulletDrawHeight,

                    enemyX,
                    enemyY,
                    enemySize,
                    enemySize
                ))
                    {

                    score += 100;

                    spawnPowerItems(enemyX + enemySize / 2, enemyY + enemySize / 2, enemyPowerReward);

                    enemyY = playfieldY + playfieldHeight - enemySize;
                    enemyX = playfieldX + (float)(Math.random() * (playfieldWidth - enemySize));

                    bullets.removeIndex(i);
                    continue;
                }

                // remove se sair da tela
                if (bullet.y > playfieldY + playfieldHeight) {
                    bullets.removeIndex(i);
                }
            }

            //colisoes
            float hitboxX = playerX + hitboxOffsetX - (hitboxSize / 2);
            float hitboxY = playerY + hitboxOffsetY - (hitboxSize / 2);

            if (enemyAlive && isColliding(
                hitboxX, hitboxY, hitboxSize,
                enemyX, enemyY, enemySize)) {

                lives--;

                if (bombs < 3) {
                    bombs = 3;
                }

                playerX = playfieldX + 180;
                playerY = playfieldY + 80;
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
            if (enemyAlive) {
                enemyY -= enemySpeed * delta;

                // se sair da tela, volta para o topo
                if (enemyY < playfieldY - enemySize) {
                    enemyY = playfieldY + playfieldHeight - enemySize;
                    enemyX = playfieldX + (float)(Math.random() * (playfieldWidth - enemySize));
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

        batch.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);

        // fundo preto do playfield
        shape.setColor(Color.BLACK);
        shape.rect(
            playfieldX,
            playfieldY,
            playfieldWidth,
            playfieldHeight
        );

        // player
        //shape.setColor(Color.WHITE);
        //shape.rect(playerX, playerY, playerSize, playerSize);

        // desenhar tiros
        //shape.setColor(Color.RED);
        //for (Bullet bullet : bullets) {
            //shape.rect(bullet.x, bullet.y, 6, 15);
        //}

        // inimigo
        if (enemyAlive) {
            shape.setColor(Color.YELLOW);
            shape.rect(enemyX, enemyY, enemySize, enemySize);
        }

        for (PowerItem item : powerItems) {
            shape.setColor(Color.CYAN);
            shape.rect(item.x, item.y, item.size, item.size);
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
            // futuramente: abrir tela de cartas
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

}
