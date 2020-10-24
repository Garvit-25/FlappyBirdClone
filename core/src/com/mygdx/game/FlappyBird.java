package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] birds;
	int birdState = 0;
	int pause = 0;

	float birdY = 0;
	float velocity = 0;
	int gameState = 0;
	float gravity = 0.75f;

	Texture topTube;
	Texture bottomTube;
	float gap = 400;
	float maxTubeOffset;
	Random randomgenerator;

	float tubevelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	Circle birdCircle ;
	//ShapeRenderer shapeRenderer ;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	int score  = 0;
	int scoringTube = 0;
	BitmapFont font;
	Texture gameover;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomgenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()*3/4;

		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();
		birdCircle = new Circle();
		//shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		gameover = new Texture("gameover.png");

	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;
		for(int i=0;i<numberOfTubes;i++){
			tubeOffset[i] = (randomgenerator.nextFloat()-0.5f) *maxTubeOffset;
			tubeX[i] = Gdx.graphics.getWidth()/2-bottomTube.getWidth()/2 + i*distanceBetweenTubes + Gdx.graphics.getWidth();
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if(pause<5){
			pause ++;
		}else {
			pause = 0;
			if (birdState == 0) {
				birdState = 1;
			} else
				birdState = 0;
		}

		if(gameState==1) {
			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2){
				score++;
				if(scoringTube < numberOfTubes-1) {
					scoringTube++;
				}else
					scoringTube = 0;
			}

			for(int i=0;i<numberOfTubes;i++){
				if(tubeX[i]<-topTube.getWidth()){
					tubeX[i] += numberOfTubes*distanceBetweenTubes;
					tubeOffset[i] = (randomgenerator.nextFloat()-0.5f) *maxTubeOffset;
				}else{
					tubeX[i] -= tubevelocity;
				}

				batch.draw(topTube,tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i]);
				batch.draw(bottomTube,tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
			}

			if(Gdx.input.justTouched()){
				velocity = -15;

			}

			if(birdY>0 ){
				velocity+=gravity;
				birdY -= velocity;
			}else{
				gameState = 2;
			}

		}else if(gameState==0){
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		}
		else if(gameState == 2){
			batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2);

			if(Gdx.input.justTouched()){
				gameState=1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}


		batch.draw(birds[birdState], Gdx.graphics.getWidth() / 2 - birds[birdState].getWidth() / 2, birdY);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		birdCircle.set(Gdx.graphics.getWidth()/2,birdY + birds[0].getHeight()/2,birds[0].getWidth()/2);
		font.draw(batch,String.valueOf(score),100,200);

		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
		for(int i=0;i<numberOfTubes;i++){
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle,topTubeRectangles[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangles[i])){
				gameState = 2; //gaem Over
			}
		}

		//shapeRenderer.end();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
