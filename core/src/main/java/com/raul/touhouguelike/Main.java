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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

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

    // velocidade normal
    private final float normalSpeed = 420;

    // velocidade no foco
    private final float focusSpeed = 210;

    //sprite
    private Texture[] reimuIdleTextures;
    private Animation<TextureRegion> reimuIdleAnimation;
    private float stateTime = 0;

    // vidas, bombas e pontos
    private int lives = 3;
    private int bombs = 3;
    private int score = 0;

    //ui
    private Texture uiBackground;

    // inimigo
    private float enemyX = playfieldX + 150;
    private float enemyY = playfieldY + playfieldHeight;
    private final float enemySize = 40;
    private final float enemySpeed = 150;

    private boolean enemyAlive = true;

    private boolean bombEffect = false;
    private float bombTimer = 0;
    private final float bombDuration = 0.15f;

    // modo foco
    private boolean focusMode = false;

    // tiros
    private Array<Bullet> bullets;
    private final float bulletSpeed = 500;
    private float shootTimer = 0;
    private final float shootCooldown = 0.12f;

    //hud
    private SpriteBatch batch;
    private BitmapFont font;

    //musica
    private Music backgroundMusic;

    @Override
    public void create() {
        shape = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 960);
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);

        uiBackground = new Texture("ui_background.png");

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("musica1.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();

        bullets = new Array<>();

        reimuIdleTextures = new Texture[8];
        TextureRegion[] idleFrames = new TextureRegion[8];

        for (int i = 0; i < 8; i++) {
            reimuIdleTextures[i] = new Texture("parado" + (i + 1) + ".png");
            idleFrames[i] = new TextureRegion(reimuIdleTextures[i]);
        }

        reimuIdleAnimation = new Animation<>(0.10f, idleFrames);

    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        stateTime += delta;

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

            // limitar ao playfield
            playerX = Math.max(
                playfieldX,
                Math.min(playerX, playfieldX + playfieldWidth - playerSize)
            );

            playerY = Math.max(
                playfieldY,
                Math.min(playerY, playfieldY + playfieldHeight - playerSize)
            );

            // grid invisível
            playerX = Math.round(playerX);
            playerY = Math.round(playerY);

            // tiro com Z
            shootTimer += delta;

            if (Gdx.input.isKeyPressed(Input.Keys.Z) && shootTimer >= shootCooldown) {
                bullets.add(new Bullet(
                    playerX + playerSize / 2 - 3,
                    playerY + playerSize
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
                if (enemyAlive && isColliding(
                    bullet.x, bullet.y, 6,
                    enemyX, enemyY, enemySize)) {

                    score += 100;

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

            // inimigo descendo
            if (enemyAlive) {
                enemyY -= enemySpeed * delta;

                // se sair da tela, volta para o topo
                if (enemyY < playfieldY - enemySize) {
                    enemyY = playfieldY + playfieldHeight - enemySize;
                    enemyX = playfieldX + (float)(Math.random() * (playfieldWidth - enemySize));
                }
            }

            //colisoes
            float hitboxX = playerX + (playerSize / 2) - (hitboxSize / 2);
            float hitboxY = playerY + (playerSize / 2) - (hitboxSize / 2);

            if (enemyAlive && isColliding(
                hitboxX, hitboxY, hitboxSize,
                enemyX, enemyY, enemySize)) {

                lives--;

                playerX = playfieldX + 180;
                playerY = playfieldY + 80;
            }

        }

        // limpar tela
        if (bombEffect) {
            Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        } else {
            Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        }
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

        // hitbox no foco
        if (focusMode) {
            shape.setColor(Color.BLUE);
            shape.circle(
                playerX + playerSize / 2,
                playerY + playerSize / 2,
                4
            );
        }

        // desenhar tiros
        shape.setColor(Color.RED);
        for (Bullet bullet : bullets) {
            shape.rect(bullet.x, bullet.y, 6, 15);
        }

        // inimigo
        if (enemyAlive) {
            shape.setColor(Color.YELLOW);
            shape.rect(enemyX, enemyY, enemySize, enemySize);
        }

        shape.end();

        TextureRegion currentFrame = reimuIdleAnimation.getKeyFrame(stateTime, true);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(
            currentFrame,
            playerX,
            playerY,
            51,
            75
        );

        batch.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.draw(batch, "Score: " + score, 900, 850);
        font.draw(batch, "Vidas: " + lives, 900, 780);
        font.draw(batch, "Bombas: " + bombs, 900, 710);

        batch.end();

        if (paused) {
            batch.begin();

            font.draw(batch, "PAUSADO", 320, 300);

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

        for (Texture texture : reimuIdleTextures) {
            texture.dispose();
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

    private boolean isColliding(
        float x1, float y1, float size1,
        float x2, float y2, float size2) {

        return x1 < x2 + size2 &&
            x1 + size1 > x2 &&
            y1 < y2 + size2 &&
            y1 + size1 > y2;
    }
}
