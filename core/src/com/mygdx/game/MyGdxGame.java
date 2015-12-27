package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input;

public class MyGdxGame extends ApplicationAdapter {
	static final int LOGICAL_WIDTH = 640;       // ゲーム内の論理座標 幅
	static final int LOGICAL_HEIGHT = 480;  // ゲーム内の論理座標 高さ

	static final int BLOCKS_W = 10;
	static final int BLOCKS_H = 4;

	SpriteBatch batch;
	Texture img;
	Sprite sprite;
	BitmapFont font;
	Vector2 pos;
	Music music;
	float angle;
	ShapeRenderer renderer;
	Block[] blocks;
	Block ball;
	float dx, dy;

	@Override
	public void create () {
		// カメラ設定
		OrthographicCamera camera = new OrthographicCamera();
		camera.setToOrtho(false, LOGICAL_WIDTH,LOGICAL_HEIGHT);   // ★第１引数をtrueにすることで上下反転する

		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);    // カメラを設定する

		renderer = new ShapeRenderer();
		renderer.setProjectionMatrix(camera.combined); // カメラを設定する

		img = new Texture("badlogic.jpg");
		sprite = new Sprite(img);
		sprite.setSize(120, 20);
		font = new BitmapFont();
		pos = new Vector2();
		/*
		music = Gdx.audio.newMusic(Gdx.files.floaternal("mixdown.mp3"));
		music.setLooping(true);
		music.setVolume(0.5f);
		music.play();
		*/
		blocks = new Block[BLOCKS_H * BLOCKS_W];
		for(int j=0; j < BLOCKS_H; j++)
			for(int i=0; i < BLOCKS_W; i++)
				blocks[i + j * BLOCKS_W] = new Block(2 + i * 64, 300 + j * 32, 60, 30);
		ball = new Block(50, 50, 16, 16);
		dx = 1.2f; dy = 3.3f;
	}

	@Override
	public void render () {
		/*
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			pos.x -= 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			pos.x += 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			pos.y += 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			pos.y -= 1;
		}
		*/
		String info = String.format("x,y(%f,%f) d(%f,%f)",ball.r.x, ball.r.y, dx, dy);
		//sprite.setPosition(pos.x, pos.y);
		//sprite.setPosition(Gdx.input.getX() - img.getWidth() / 2, Gdx.graphics.getHeight() - img.getHeight() / 2 - Gdx.input.getY());
		sprite.setPosition(Gdx.input.getX() * (float)LOGICAL_WIDTH / (float) Gdx.graphics.getWidth(), 20) ;

		boolean cond;
		Block b1;

		ball.r.y += dy;

		if (dy == 0f) {
			ball.r.x = sprite.getX() + sprite.getWidth()/ 2;
			if (Gdx.input.isTouched()) dy = 3.3f;
		} else if ((dy < 0) && (ball.r.y < 0)) {
			ball.r.x = sprite.getX() + sprite.getWidth()/ 2;
			ball.r.y = sprite.getY() + sprite.getHeight();
			dx = 1.2f; dy = 0f;
		} else {
			b1 = ball.hitAll(blocks);
			if (b1 != null) {
				cond = true;
				b1.live = false;
			} else {
				cond = false;
			}
			cond |= (dy > 0) && (ball.r.y > (LOGICAL_HEIGHT - ball.r.height));
			if (cond) {
				dy = -dy;
				ball.r.y += dy;
			}
			if ((ball.r.y <= (sprite.getY() + sprite.getHeight())) && ((ball.r.y - dy) > (sprite.getY() + sprite.getHeight()))) {
				if ((ball.r.x > sprite.getX()) && ((ball.r.x + ball.r.width) < sprite.getX() + sprite.getWidth())) {
					dx = ((ball.r.x - sprite.getX()) / sprite.getWidth() - 0.5f) * 10.0f;
					dy = -dy;
					dy += 0.2f;
				} else {
					if (Gdx.input.isTouched()) dy = -dy;
				}
			}

			ball.r.x += dx;
			b1 = ball.hitAll(blocks);
			if (b1 != null) {
				cond = true;
				b1.live = false;
			} else {
				cond = false;
			}
			cond |= (dx > 0) && (ball.r.x > (640 - ball.r.width));
			cond |= (dx < 0) && (ball.r.x < 0);
			if (cond) {
				dx = -dx;
				ball.r.x += dx;
			}
		}


		//sprite.setScale((float) Math.sin(angle));

		/*
		sprite.setRotation(angle);
		if (Gdx.input.isTouched() || angle > 0.0) {
			angle += 1.0;
			if (angle >= 360.0) angle = (float) 0.0;
		}
		*/

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.begin(ShapeRenderer.ShapeType.Filled);
		/*
		renderer.setColor(Color.GREEN);
		renderer.circle(320, 240, 240);
		*/
			renderer.setColor(Color.BLUE);
			for (Block b: blocks) {
				b.draw(renderer);
			}
			renderer.setColor(Color.YELLOW);
			ball.draw(renderer);

		renderer.end();

		batch.begin();
		//batch.draw(img, 50, 50);
			sprite.draw(batch);
			font.draw(batch, "Hello libGDX", 200, 400);
			font.draw(batch, info, 200, 300);
		batch.end();
	}
}

class Block {
	//float _x, _y, _w, _h;
	public Rectangle r;
	public boolean live = true;

	public Block(float x, float y, float w, float h) {
		r = new Rectangle(x, y, w, h);
	}
	public void draw(ShapeRenderer renderer) {
		if(this.live) renderer.rect(r.x, r.y, r.width, r.height);
	}
	public boolean hit (Block b) {
		return b.live && Intersector.overlaps(this.r, b.r);
	}
	public Block hitAll(Block[] blocks) {
		for(Block b: blocks) {
			if (b.live) if (hit(b)) return b;
		}
		return null;
	}

}