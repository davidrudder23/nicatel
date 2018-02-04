package org.noses.nicatel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NicatelGame extends ApplicationAdapter {
    private TiledMapRenderer tiledMapRenderer;
    private TiledMap tiledMap;
    private OrthographicCamera camera;

    private Map<String, TiledMapTileLayer.Cell> highlights;

    private int tilePixelWidth;
    private int tilePixelHeight;

    @Override
    public void create() {
        tiledMap = new TmxMapLoader().load("nicatel.tmx");
        tiledMapRenderer = new HexagonalTiledMapRenderer(tiledMap);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();

        MapProperties prop = tiledMap.getProperties();
        tilePixelWidth = prop.get("tilewidth", Integer.class);
        tilePixelHeight = (prop.get("tileheight", Integer.class)/2);
        highlights = new HashMap<String, TiledMapTileLayer.Cell>();

        TiledMapTileLayer.Cell red = new TiledMapTileLayer.Cell();
        red.setTile(new StaticTiledMapTile(new TextureRegion(new Texture("red.png"))));
        highlights.put("red", red);

        TiledMapTileLayer.Cell green = new TiledMapTileLayer.Cell();
        green.setTile(new StaticTiledMapTile(new TextureRegion(new Texture("green.png"))));
        highlights.put("green", green);

        TiledMapTileLayer.Cell yellow = new TiledMapTileLayer.Cell();
        yellow.setTile(new StaticTiledMapTile(new TextureRegion(new Texture("yellow.png"))));
        highlights.put("yellow", yellow);

    }

    @Override
    public void render() {

        clearLayer(getHighlightLayer());
        highlightTheMouse();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    @Override
    public void dispose() {

    }

    private void clearLayer(TiledMapTileLayer layer) {
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                layer.setCell(x, y, null);
            }
        }
    }


    private void highlightTheMouse() {
            TiledMapTileLayer highlightLayer = getHighlightLayer();

            int tileX = getTileXFromScreenX(Gdx.input.getX());
            int tileY = getTileYFromScreenY(Gdx.input.getY());

            System.out.println("tileX="+tileX+", tileY="+tileY);


            highlightLayer.setCell(tileX, tileY, highlights.get("red"));

    }

    public TiledMapTileLayer getHighlightLayer() {
        return getLayersByName("highlight").get(0);
    }

    private List<TiledMapTileLayer> getLayersByName(String name) {
        List<TiledMapTileLayer> layers = new ArrayList<TiledMapTileLayer>();
        MapLayers mapLayers = tiledMap.getLayers();
        for (int i = 0; i < mapLayers.getCount(); i++) {
            MapLayer mapLayer = mapLayers.get(i);
            if ((mapLayer.getName().toLowerCase().contains(name.toLowerCase()))
                    && (mapLayer instanceof TiledMapTileLayer)) {
                TiledMapTileLayer tmtl = (TiledMapTileLayer) mapLayer;
                layers.add(tmtl);
            }
        }

        return layers;
    }

    public int getTileXFromScreenX(int screenX) {

        /*System.out.println("Viewport=" + camera.viewportWidth + "," +
        camera.viewportHeight);
        System.out.println("tilePixelWidth="+tilePixelWidth);
        */
        int camWidthInTiles = (int) (camera.viewportWidth / tilePixelWidth);

        int tileX = screenX / tilePixelWidth;

        int offset = 0;//(camWidthInTiles / 2);
        if (offset > 0) {
            tileX += offset;
        }

        return tileX;
    }

    public int getTileYFromScreenY(int screenY) {
        int camHeightInTiles = (int) (camera.viewportHeight / tilePixelHeight);
        int tileY = screenY / tilePixelHeight;

        int offset = 0;
        if (offset > 0) {
            tileY += offset;
        }

        tileY = camHeightInTiles - tileY;

        return tileY;
    }


}
