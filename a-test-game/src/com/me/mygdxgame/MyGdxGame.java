package com.me.mygdxgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MyGdxGame implements ApplicationListener {
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	Player player;
	FPSLogger fps;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		fps = new FPSLogger();
		
		camera = new OrthographicCamera(1, h/w);
		TiledMap map = new TmxMapLoader().load("data/testlevel.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1/12f);
		camera.setToOrtho(false, 40, 30);
		camera.update();
		
		MapLayer layer = map.getLayers().get("Object layer");
		RectangleMapObject object = (RectangleMapObject) layer.getObjects().get("Player");
		Rectangle r = object.getRectangle();
		Vector3 pos = new Vector3(r.x, r.y, 0);
		//pos.y = map.getProperties().get("height", int.class) - 12 * pos.y;
		Gdx.app.log("", Float.toString(pos.y));
		Gdx.app.log("", Float.toString(h - pos.y));
		pos.y = h - pos.y;
		camera.unproject(pos);
		player = new Player(pos, map.getLayers().get("Terrain"), map.getLayers().get("Ladder"));
	}

	@Override
	public void dispose() {
	}

	@Override
	public void render() {	
		//fps.log();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.setView(camera);		
		renderer.render();
		player.render(camera);
		
		camera.position.x = player.position.x;
		//camera.position.y = player.position.y;
		camera.update();
		float delta = Gdx.graphics.getDeltaTime();
		player.update(delta);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
