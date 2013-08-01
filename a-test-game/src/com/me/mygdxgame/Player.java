package com.me.mygdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class Player {
	public Vector3 position;
	public Vector3 cameraPosition;
	Vector2 velocity;
	ShapeRenderer renderer;
	TiledMapTileLayer terrain;
	TiledMapTileLayer ladder;
	boolean grounded, jumping;
	final float JUMP_ACC = 0.75f;
	final float JUMP_MAX_SPEED = 0.3f;
	final float HORIZ_MAX_SPEED = 2f;
	final float LADDER_MAX_SPEED = 0.2f;
	final float WIDTH = 12;
	final float HEIGHT = 12;
	
	public Player(Vector3 pos, MapLayer t, MapLayer l) {
		position = pos;
		renderer = new ShapeRenderer();
		terrain = (TiledMapTileLayer) t;
		ladder = (TiledMapTileLayer) l;
		velocity = new Vector2(0, 0);
	}
	
	public void updateFall(float delta) {
		Rectangle rect = new Rectangle(position.x, position.y, WIDTH-1, HEIGHT);
		grounded = false;
		Cell below = terrain.getCell((int)position.x, (int)position.y);
		Rectangle rBelow = new Rectangle(position.x, position.y, 1, 1);
		Cell left = terrain.getCell((int)position.x-1, (int)position.y);
		Rectangle rLeft = new Rectangle(position.x-1, position.y, 1, 1);	
		Cell right = terrain.getCell((int)position.x+1, (int)position.y);
		Rectangle rRight = new Rectangle((int)(position.x)+1, position.y, 1, 1);
		Cell above = terrain.getCell((int)position.x, (int)position.y+1);
		Rectangle rAbove = new Rectangle(position.x, position.y+1, 1, 1);
		if (below != null && rect.overlaps(rBelow) && !jumping){
			grounded = true;
		}
		if (right != null && rect.overlaps(rRight) && !jumping){
			Cell uRight = terrain.getCell((int)position.x+1, (int)position.y+1);
			Rectangle uRRight = new Rectangle((int)(position.x)+1, position.y+1, 1, 1);
			if (uRight == null || !rect.overlaps(uRRight)){
				grounded = true;
			}
		}
		if (left != null && rect.overlaps(rLeft) && !jumping){
			Cell uLeft = terrain.getCell((int)position.x+1, (int)position.y+1);
			Rectangle uRLeft = new Rectangle((int)(position.x)+1, position.y+1, 1, 1);
			if (uLeft == null || !rect.overlaps(uRLeft)){
				grounded = true;
			}
		}		
		if (!grounded && !jumping) {
			velocity.y -= JUMP_ACC * delta;
		} else if (grounded) {
			position.y = rBelow.y;
			velocity.y = 0;
		}
		
		if (Math.abs(velocity.y) >= JUMP_MAX_SPEED && !jumping) {
			velocity.y = -JUMP_MAX_SPEED;
		}	
	}
	
	public void updateJump(float delta) {
		velocity.y -= JUMP_ACC * delta;
		if (velocity.y <= 0) {
			jumping = false;
		}
		Rectangle rect = new Rectangle(position.x, position.y, WIDTH-1, HEIGHT);
		Cell above = terrain.getCell((int)position.x, (int)position.y+1);
		Rectangle rAbove = new Rectangle(position.x, position.y+1, 1, 1);
		if (above != null && rect.overlaps(rAbove)){
			jumping = false;
			velocity.y = 0;
			position.y = rAbove.y - 1.25f;
		}
	}
	
	public void updateSideCollisions(float delta) {
		Rectangle rect = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
		Cell left = terrain.getCell((int)position.x, (int)position.y+1);
		Rectangle rLeft = new Rectangle((int)position.x, (int)position.y+1, 1, 1);
		Cell right = terrain.getCell((int)position.x + 1, (int)position.y+1);
		Rectangle rRight = new Rectangle((int)position.x + 1, (int)position.y+1, 1, 1);
		Cell current = terrain.getCell((int)position.x, (int)position.y+1);
		Rectangle rCurrent = new Rectangle((int)position.x, (int)position.y+1, 1, 1);		
		if (left != null && rect.overlaps(rLeft)){
			position.x = rLeft.x + 1;
			velocity.x = 0;
		}
		if (right != null && rect.overlaps(rRight)){
			position.x = rRight.x-1;
			velocity.x = 0;
		}
		if (current != null && rect.overlaps(rCurrent)){
			if (velocity.x <= 0) {
				position.x = rLeft.x + 1;
				velocity.x = 0;
			} else {
				position.x = rRight.x-1;
				velocity.x = 0;				
			}
		}
	}
	
	public void update(float delta) {
		updateFall(delta);		
		if (jumping) {
			updateJump(delta);
		}			
		updateSideCollisions(delta);		
		
		if (Gdx.input.isKeyPressed(Keys.D)) {
			velocity.x = HORIZ_MAX_SPEED * delta;
		} else if (Gdx.input.isKeyPressed(Keys.A)) {
			velocity.x = -HORIZ_MAX_SPEED * delta;
		} else {
			velocity.x = 0;
		}
		
		if (Gdx.input.isKeyPressed(Keys.SPACE) && !jumping && grounded){
			jumping = true;
			velocity.y = JUMP_MAX_SPEED;
		}
		
		if (Gdx.input.isKeyPressed(Keys.S)) {
			Rectangle rect = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
			Cell below = ladder.getCell((int)position.x, (int)position.y);
			Rectangle rBelow = new Rectangle(position.x, position.y, 1, 1);
			if (below != null && rect.overlaps(rBelow)){
				if (below.getTile().getProperties().containsKey("ladder")){
					velocity.y = -LADDER_MAX_SPEED;
				}
			}
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			Rectangle rect = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
			Cell above = ladder.getCell((int)position.x, (int)position.y+1);
			Rectangle rAbove = new Rectangle(position.x, position.y+1, 1, 1);
			Cell below = ladder.getCell((int)position.x, (int)position.y);
			Rectangle rBelow = new Rectangle(position.x, position.y, 1, 1);
			boolean up = false;
			if (above != null && rect.overlaps(rAbove)){
				if (above.getTile().getProperties().containsKey("ladder")){
					up = true;
				}
			}
			if (below != null && rect.overlaps(rBelow)){
				if (below.getTile().getProperties().containsKey("ladder")){
					up = true;
				}
			}
			if (up) {
				velocity.y = LADDER_MAX_SPEED;
			}
		}
					
		position.x += velocity.x;
		position.y += velocity.y;
	}
	
	public void render(OrthographicCamera camera){
		cameraPosition = new Vector3(position.x, position.y, 0);
		camera.project(cameraPosition);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BLUE);
		renderer.rect(cameraPosition.x, cameraPosition.y, WIDTH, HEIGHT);
		renderer.end();
	}
}
