package Build;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;

public class BuildServer extends GameApplication {
    private Entity player1OnScreen=new Entity(), turret=new Entity();
    private Player mainPlayer;
    private ArrayList<Resource> resources=new ArrayList<>();
    private ArrayList<Entity> resourcesOnScreen=new ArrayList<>(),lives=new ArrayList<>();
    private ArrayList<Asteroid> asteroids=new ArrayList<>();
    private ArrayList<Planet> planets=new ArrayList<>();
    private ArrayList<ResourceProbe> probes=new ArrayList<>();
    private ArrayList<Satellite> satellites=new ArrayList<>();
    private ArrayList<Bullet> bulletsOnScreen=new ArrayList<>();
    private ArrayList<BlackHole> blackHoles=new ArrayList<>();
    private Button factory,resourceCollect, portal, start,probe,satellite;
    private ListView messages;
    private Label lbl=new Label();
    private int level=1;
    private double life=10,moves=0;
    private Label showFood=new Label(),showFuel=new Label(),showBuildingSupplies=new Label();

    public static void main(String[] args) {
        launch(args);
    }

    public enum EntityType {
        PLAYER, BULLET, RESOURCE, ASTEROID, PLANETS, PROBE, SATELLITE,BEAM, ALIENSHIP,BLACKHOLE,FACTORY,RESOURCECOLLECT,PORTAL
    }

    /**
     * pre:resources exist
     * post:adds points based on resource
     */
    public void addResourceBenefit(int index){
        if (resources.get(index).getType()==0) mainPlayer.addFood(1);
        else if (resources.get(index).getType()==1) mainPlayer.addFuel(1);
        else if (resources.get(index).getType()==2) mainPlayer.addBuildingSupplies(1);
    }

    /**
     * pre:player has enough resources
     * post:resource collector created
     */
    public void createNewResourceCollector(){
        if(ResourceCollector.getPlaceTimer()==0) {
            int amount = 0;
            for (ResourceCollector p : mainPlayer.getCollectors()) {
                if (p.isCanBeUsed()) amount++;
            }
            if (amount < 1 && mainPlayer.getBuildingSupplies() > 1 && level > 1) {
                mainPlayer.setCollectors(new ResourceCollector((int) player1OnScreen.getX(), (int) player1OnScreen.getY(), 55));
                mainPlayer.addBuildingSupplies(-2);
            }
            else if (!(level > 1)) messages.getItems().add("Need level 2");
            else if (!(mainPlayer.getCollectors().size() < 1)) messages.getItems().add("Can't build more.");
            else if (!(mainPlayer.getBuildingSupplies() > 1)) messages.getItems().add("Not enough supplies.");
            ResourceCollector.setPlaceTimer(1);
        }
    }

    /**
     * pre:player has enough resources
     * post:portal created
     */
    public void createNewPortal() {
        if (Portal.getPlaceTimer()==0) {
            int amount = 0;
            for (Portal p : mainPlayer.getPortals()) {
                if (p.isCanBeUsed()) amount++;
            }
            if (amount < 2 && mainPlayer.getBuildingSupplies() > 4 && level > 1) {
                mainPlayer.setPortals(new Portal((int) player1OnScreen.getX(), (int) player1OnScreen.getY(), 40));
                mainPlayer.addBuildingSupplies(-5);
            }
            else if (!(level > 1)) messages.getItems().add("Need level 2");
            else if (!(mainPlayer.getPortals().size() < 2)) messages.getItems().add("Can't build more.");
            else if (!(mainPlayer.getBuildingSupplies() > 4)) messages.getItems().add("Not enough supplies.");
            Portal.setPlaceTimer(1);
        }
    }

    /**
     * pre:player has enough resources
     * post:probe created
     */
    public void createNewProbe() {
        if (ResourceProbe.getPlaceTimer() == 0) {
            if (probes.size() < 1 && mainPlayer.getBuildingSupplies() > 10 && level > 1) {
                Entity temp = Entities.builder()
                        .type(EntityType.PROBE)
                        .with(new CollidableComponent(true))
                        .at(0, 0)
                        .viewFromNodeWithBBox(new Rectangle(15, 15, Color.ORANGE))
                        .buildAndAttach(getGameWorld());
                probes.add(new ResourceProbe(temp.getX(), temp.getY(), temp));
                mainPlayer.addBuildingSupplies(-11);
            }
            else if (!(level > 1)) messages.getItems().add("Need level 2");
            else if (!(probes.size() < 1)) messages.getItems().add("Can't build more.");
            else if (!(mainPlayer.getBuildingSupplies() > 10)) messages.getItems().add("Not enough supplies.");
            ResourceProbe.setPlaceTimer(1);
        }
    }

    /**
     * pre:player has enough resources
     * post:satellite created
     */
    public void createNewSatellite() {
        if (Satellite.getPlaceTimer()==0) {
            if (mainPlayer.getBuildingSupplies() >= 5 && level > 2) {
                Entity temp = Entities.builder()
                        .at(player1OnScreen.getX(), player1OnScreen.getY())
                        .viewFromNodeWithBBox(new Rectangle(5, 5, Color.GREEN))
                        .with(new CollidableComponent(true))
                        .type(EntityType.SATELLITE)
                        .buildAndAttach(getGameWorld());
                satellites.add(new Satellite(player1OnScreen.getX(), player1OnScreen.getY(), temp));
                satellites.get(satellites.size() - 1).settarget(planets);
                satellites.get(satellites.size() - 1).setA(Math.sqrt(Math.pow(player1OnScreen.getX() - satellites.get(satellites.size() - 1).getTargetx(), 2) + Math.pow(player1OnScreen.getY() - satellites.get(satellites.size() - 1).getTargety(), 2)));
                satellites.get(satellites.size() - 1).setB(returnRandInt(40, (int) satellites.get(satellites.size() - 1).getA() + 20));
                satellites.get(satellites.size() - 1).setOrbit();
                if (satellites.get(satellites.size() - 1).getA() > 90) removeSatellite(temp);
                else {
                    planets.get(locateIndexPlanets((int) satellites.get(satellites.size() - 1).getTargetx(), (int) satellites.get(satellites.size() - 1).getTargety(), planets)).addSatellites(1);
                    mainPlayer.addBuildingSupplies(-5);
                }
            }
            else if (!(level > 2)) messages.getItems().add("Need level 3");
            else if (!(mainPlayer.getBuildingSupplies() >= 5)) messages.getItems().add("Not enough supplies.");
            Satellite.setPlaceTimer(1);
        }
    }

    /**
     * pre:player has enough resources
     * post:factory created
     */
    public void createNewFactory() {
        if (Factory.getPlaceTimer()==0) {
            int amount = 0;
            for (Factory p : mainPlayer.getFactories()) {
                if (p.isCanBeUsed()) amount++;
            }
            if (amount < 2 && mainPlayer.getBuildingSupplies() > 4 && level > 1) {
                mainPlayer.setFactories(new Factory((int) player1OnScreen.getPositionComponent().getX(), (int) player1OnScreen.getPositionComponent().getY(), 100, 250));
                mainPlayer.addBuildingSupplies(-5);
            }
            else if (!(level > 1)) messages.getItems().add("Need level 2");
            else if (!(mainPlayer.getFactories().size() < 2)) messages.getItems().add("Can't build more.");
            else if (!(mainPlayer.getBuildingSupplies() > 4)) messages.getItems().add("Not enough supplies.");
            Factory.setPlaceTimer(1);
        }
    }

    /**
     * pre: game has begun
     * post: lives are updated
     */
    public void changeLife(double amount){
        if(life+amount>=10) life=10;
        life+=amount;
        for (Entity temp:lives) {
            temp.removeFromWorld();
        }
        for (int i = 0; i < (int)life; i++) {
            getGameWorld().addEntity(lives.get(i));
        }
    }

    /**
     * pre:structures exist
     * post:updates if structures should disappear of if portals can reopen
     */
    public void checkStructureTimer(){
        //checks if the structure should disappear
        for (int i = mainPlayer.getCollectors().size() - 1; i >= 0; i--) {
            ResourceCollector r = mainPlayer.getCollectors().get(i);
            if (r.isBuilt()) {
                r.addTimer(1);
                if (r.getTimer() > 9) {
                    r.setCanBeUsed(false);
                }
            }
        }
        //checks if portal should reopen
        for (int i = mainPlayer.getPortals().size() - 1; i >= 0; i--) {
            Portal r = mainPlayer.getPortals().get(i);
            if (r.isBuilt()) {
                r.addTimer(1);
                if (r.getTimer() > 2) {
                    r.setJustTraveled(false);
                }
            }
        }
    }

    /**
     * pre: resource info given
     * post: resource created
     */
    public void createNewResource(Color c, int type, int x, int y) {
        Entity temp = Entities.builder()
                .at(x, y)
                .viewFromNodeWithBBox(new Rectangle(8, 8, c))
                .with(new CollidableComponent(true))
                .type(EntityType.RESOURCE)
                .buildAndAttach(getGameWorld());
        resources.add(new Resource(type, 12, temp.getX(), temp.getY(), 8, 8,temp));
        resourcesOnScreen.add(temp);

    }

    /**
     * pre: game has begun
     * post:displays food and fuel and supplies
     */
    public void displayResources(){
        showFood.setText("Food: " + mainPlayer.getFood());
        showFuel.setText("Fuel: " + mainPlayer.getFuel());
        showBuildingSupplies.setText("Supplies: " + mainPlayer.getBuildingSupplies());
    }

    /**
     * pre:resource collectors exist
     * post:removes resources found in resource collectors
     */
    public void detectInteractionBetweenResourceCollector(){
        for (ResourceCollector rc:mainPlayer.getCollectors()) {
            if (rc.isBuilt()&&rc.isCanBeUsed()) {
                for (int i = resources.size() - 1; i >= 0; i--) {
                    Resource r = resources.get(i);
                    if (Math.pow(r.getX() - rc.getCenterX(), 2) + Math.pow(r.getY() - rc.getCenterY(), 2) < Math.pow(rc.getRad() + 2, 2)) {//uses circle equation
                        addResourceBenefit(i);
                        removeResource(i);
                    }
                }
            }
        }
    }

    /**
     * pre:portals exist
     * post:decides to teleport person
     */
    public void detectInteractionBetweenPortal(){
        if (mainPlayer.getPortals().size()>=2) {
            for (int i = 0; i < mainPlayer.getPortals().size(); i++) {
                Portal portal = mainPlayer.getPortals().get(i);
                if (portal.isBuilt()&&!portal.isJustTraveled()&&portal.isCanBeUsed()) {
                    if (portal.getStartX() <= player1OnScreen.getX() && portal.getStartX() + portal.getSideLength() >= player1OnScreen.getX() && portal.getStartY() <= player1OnScreen.getY() && portal.getStartY() + portal.getSideLength() >= player1OnScreen.getY()) {
                        //^collision code
                        for (int j = 0; j <mainPlayer.getPortals().size() ; j++) {
                            Portal portal2=mainPlayer.getPortals().get(j);
                            if (i!=j&&portal2.isCanBeUsed()){
                                player1OnScreen.getPositionComponent().setX(portal2.getStartX()+20);
                                player1OnScreen.getPositionComponent().setY(portal2.getStartY()+20);
                                turret.setX(player1OnScreen.getX()+player1OnScreen.getWidth()/2.0-4);
                                turret.setY(player1OnScreen.getY()+player1OnScreen.getHeight()/2.0-4);
                                portal.setJustTraveled(true);
                                portal2.setJustTraveled(true);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * pre: factory placed
     * post:detects resources near factory
     */
    public void detectInteractionBetweenFactory(){
        for (Factory f:mainPlayer.getFactories()) {
            if (f.isBuilt()&&f.isCanBeUsed()){
                f.detectResources(resources,false);
                if (f.getResourcesAvailable()>0&&returnRandInt(1,500)==1) {
                    createNewResource(Color.BROWN,2,(int) returnRandDouble(f.getCenterX()-Math.sqrt(f.getCenterX()),f.getCenterX()+Math.sqrt(f.getCenterX())),f.getCenterY()+30);
                    f.setResourcesAvailable(f.getResourcesAvailable()-0.5);
                }
            }
        }
    }

    /**
     * pre: planet placed
     * post:detects player near factory
     */
    public void detectInteractionBetweenPlanet(){
        for (Planet p:planets) {
            if (p.detectPlayer(player1OnScreen,80)&&!p.isDetected()&&!p.isConquered()) {
                p.setDetected(true);
                Bullet entBul;
                if (p.getDefenseType() == 1) {
                    if (p.getShips().size() < 1) {
                        Entity asEnt = Entities.builder()
                                .at(p.getxCoord(), p.getyCoord())
                                .with(new CollidableComponent(true))
                                .type(EntityType.ALIENSHIP)
                                .renderLayer(RenderLayer.TOP)
                                .viewFromNodeWithBBox(new Rectangle(15, 15, Color.RED))
                                .buildAndAttach(getGameWorld());
                        AlienShip as = new AlienShip(p.getxCoord(), p.getyCoord(), asEnt, player1OnScreen.getX(), player1OnScreen.getY());
                        p.getShips().add(as);
                    }
                    if(p.getBulletTimer()==0) {
                        entBul = spawnBullet(p.getShips().get(p.getShips().size() - 1).getX(), p.getShips().get(p.getShips().size() - 1).getY(), player1OnScreen.getX(), player1OnScreen.getY(),0);
                        p.addBullet(entBul);
                        p.getBullets().get(p.getBullets().size() - 1).setEquation(entBul.getX(), entBul.getY(), entBul.getTargetX(), entBul.getTargetY());
                        p.setBulletTimer(1);
                    }

                }
                else{
                    entBul = spawnBullet(p.getxCoord(), p.getyCoord(), player1OnScreen.getX(), player1OnScreen.getY(),0);
                    p.addBullet(entBul);
                    p.getBullets().get(p.getBullets().size() - 1).setEquation(entBul.getX(), entBul.getY(), entBul.getTargetX(), entBul.getTargetY());
                    p.setBulletTimer(1);
                }
            }
            if (p.getBulletTimer()>30){
                p.setDetected(false);
                p.setBulletTimer(0);
            }
        }
    }

    /**
     * pre:gameboard initalized
     * post:resources are placed
     */
    public void generateResources(int amount){
        for (int i = 0; i < amount; i++) {
            //randomly generates resource
            int probType = returnRandInt(0, 5);
            if (probType < 3) createNewResource(Color.WHITE,0,returnRandInt(0, getSettings().getWidth() - 220), returnRandInt(0, getSettings().getHeight() - 20));
            else if (probType < 5) createNewResource(Color.ORANGE,1,returnRandInt(0, getSettings().getWidth() - 220), returnRandInt(0, getSettings().getHeight() - 20));
            else createNewResource(Color.BROWN,2,returnRandInt(0, getSettings().getWidth() - 220), returnRandInt(0, getSettings().getHeight() - 20));
        }
    }

    /**
     * pre:client has connected to server
     * post:input is taken in and player is moved
     */
    @Override protected void initInput() {
        Input input = getInput(); // get input service

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                if (mainPlayer.getFuel()>0) {
                    player1OnScreen.getPositionComponent().translateX(mainPlayer.getMoveSpeed()); // move right 5 pixels
                    if (player1OnScreen.getX()+15>1000){
                        player1OnScreen.setX(1000);
                    }
                    else {
                        turret.getPositionComponent().translateX(mainPlayer.getMoveSpeed()); // move right 5 pixels
                    }
                    moves++;
                }
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                if (mainPlayer.getFuel()>0) {
                    player1OnScreen.getPositionComponent().translateX(-1 * mainPlayer.getMoveSpeed()); // move left 5 pixels
                    moves++;
                    if (player1OnScreen.getX()<0)player1OnScreen.setX(0);
                    else turret.getPositionComponent().translateX(-1 * mainPlayer.getMoveSpeed()); // move  5 pixels

                }
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                if (mainPlayer.getFuel()>0) {
                    player1OnScreen.getPositionComponent().translateY(-1 * mainPlayer.getMoveSpeed()); // move up 5 pixels
                    moves++;
                    if (player1OnScreen.getY()<0)player1OnScreen.setY(0);
                    else turret.getPositionComponent().translateY(-1 * mainPlayer.getMoveSpeed()); // move up 5 pixels

                }
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                if (mainPlayer.getFuel()>0) {
                    player1OnScreen.getPositionComponent().translateY(mainPlayer.getMoveSpeed()); // move down 5 pixels
                    moves++;
                    if (player1OnScreen.getY()+15>getSettings().getHeight())player1OnScreen.setY(getSettings().getHeight()-15);
                    else turret.getPositionComponent().translateY(mainPlayer.getMoveSpeed()); // move down 5 pixels

                }
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Fire") {
            @Override
            protected void onAction() {
                if (mainPlayer.getBulletTimer()==0&&mainPlayer.getBuildingSupplies()>=1&&level==3) {
                    Entity entBul = Entities.builder()
                            .type(EntityType.BULLET)
                            .with(new CollidableComponent(true))
                            .at(player1OnScreen.getX(), player1OnScreen.getY())
                            .viewFromNodeWithBBox(new Rectangle(20, 10, Color.WHITE))
                            .buildAndAttach(getGameWorld());
                    double targetx=0;
                    double targety=0;
                    targetx=10000*Math.cos(turret.getRotation()*Math.PI/180)+entBul.getX();
                    targety=10000*Math.sin(turret.getRotation()*Math.PI/180)+entBul.getY();
                    entBul.rotateBy(turret.getRotation());
                    Bullet bullet = new Bullet(targetx,targety, player1OnScreen.getX(), player1OnScreen.getY(), entBul, 1);
                    mainPlayer.getBullets().add(bullet);
                    bullet.setEquation(player1OnScreen.getX(), player1OnScreen.getY(), targetx,targety);
                    bulletsOnScreen.add(bullet);
                    mainPlayer.setBulletTimer(1);
                    mainPlayer.addBuildingSupplies(-1);
                }
            }
        }, KeyCode.SLASH);

        input.addAction(new UserAction("Rotate right") {
            @Override
            protected void onAction() {
                turret.rotateBy(5);
            }
        }, KeyCode.PERIOD);

        input.addAction(new UserAction("Rotate left") {
            @Override
            protected void onAction() {
                turret.rotateBy(-5);
            }
        }, KeyCode.COMMA);

        input.addAction(new UserAction("Collector") {
            @Override
            protected void onAction() {
                createNewResourceCollector();
            }
        }, KeyCode.C);

        input.addAction(new UserAction("Portal") {
            @Override
            protected void onAction() {
                createNewPortal();
            }
        }, KeyCode.L);

        input.addAction(new UserAction("Satellite") {
            @Override
            protected void onAction() {
                createNewSatellite();
            }
        }, KeyCode.E);

        input.addAction(new UserAction("Factory") {
            @Override
            protected void onAction() {
                createNewFactory();
            }
        }, KeyCode.F);

        input.addAction(new UserAction("Probe") {
            @Override
            protected void onAction() {
                createNewProbe();
            }
        }, KeyCode.R);

        input.addAction(new UserAction("CHEATCODE food/fuel") {
            @Override
            protected void onAction() {
                mainPlayer.addFood(10);
                mainPlayer.addFuel(10);
            }
        }, KeyCode.DIGIT1);


        input.addAction(new UserAction("CHEATCODE RESOURCE") {
            @Override
            protected void onAction() {
                mainPlayer.addBuildingSupplies(mainPlayer.getBuildingSupplies()*-1);
                mainPlayer.addBuildingSupplies(20);
            }
        }, KeyCode.DIGIT2);

        input.addAction(new UserAction("CHEATCODE RESOURCE2") {
            @Override
            protected void onAction() {
                mainPlayer.addBuildingSupplies(mainPlayer.getBuildingSupplies()*-1);
                mainPlayer.addBuildingSupplies(30);
            }
        }, KeyCode.DIGIT3);

        input.addAction(new UserAction("CHEATCODE RESOURCE3") {
            @Override
            protected void onAction() {
                mainPlayer.addFood(mainPlayer.getFood()*-1);
                mainPlayer.addFuel(mainPlayer.getFuel()*-1);
            }
        }, KeyCode.DIGIT4);

        input.addAction(new UserAction("CHEATCODE FACTORY") {
            @Override
            protected void onAction() {
                mainPlayer.setFactories(new Factory((int)500, (int)300, 100, 250));
                mainPlayer.addBuildingSupplies(-5);
                int x=500;
                int y=0;
                int targetx=500;
                int targety=700;
                Entity temp=Entities.builder()
                        .at(x,y)
                        .with(new CollidableComponent(true))
                        .viewFromNodeWithBBox(new Rectangle(10,10, Color.BLACK))
                        .type(EntityType.BLACKHOLE)
                        .buildAndAttach(getGameWorld());
                blackHoles.add(new BlackHole(x,y,targetx,targety,50,temp));
                blackHoles.get(blackHoles.size()-1).setEquation(x,y,targetx,targety);
            }
        }, KeyCode.DIGIT6);

        input.addAction(new UserAction("CHEATCODE LIFE") {
            @Override
            protected void onAction() {
                life=10;
            }
        }, KeyCode.DIGIT5);

        input.addAction(new UserAction("BACKUP TURRET") {
            @Override
            protected void onAction() {
                turret.setX(player1OnScreen.getX()+player1OnScreen.getWidth()/2.0-4);
                turret.setY(player1OnScreen.getY()+player1OnScreen.getHeight()/2.0-4);
            }
        }, KeyCode.T);


    }

    /**
     * pre:server initialized
     * post:ui and fxml is set up
     */
    @Override protected void initGame() {
        Entities.builder()
                .at(0,0)
                .viewFromNodeWithBBox(new Rectangle(1000,600,Color.BLACK))
                .buildAndAttach(getGameWorld());
        Entities.builder()
                .at(1000,0)
                .viewFromNodeWithBBox(new Rectangle(200,600,Color.GRAY))
                .buildAndAttach(getGameWorld());

        //fxml stuff
        showFood.setTranslateX(1020);
        showFood.setTranslateY(100);
        showFuel.setTranslateX(1020);
        showFuel.setTranslateY(130);
        showBuildingSupplies.setTranslateX(1020);
        showBuildingSupplies.setTranslateY(160);
        getGameScene().addUINode(showFood);
        getGameScene().addUINode(showFuel);
        getGameScene().addUINode(showBuildingSupplies);

        //this is the resource collector button
            resourceCollect=new Button("Collector(2)(C)");
        resourceCollect.setTranslateX(1000);
        resourceCollect.setTranslateY(10);
        resourceCollect.setDisable(true);
        resourceCollect.setOnAction(event -> createNewResourceCollector());
        getGameScene().addUINode(resourceCollect);

        //this is the portal button
        portal=new Button("Portal(5)(L)");
        portal.setTranslateX(1100);
        portal.setTranslateY(40);
        portal.setDisable(true);
        portal.setOnAction(event -> createNewPortal());
        getGameScene().addUINode(portal);

        //this is the factory button
        factory=new Button("Factory(5)(F)");
        factory.setTranslateX(1100);
        factory.setTranslateY(10);
        factory.setDisable(true);
        factory.setOnAction(event -> createNewFactory());
        getGameScene().addUINode(factory);

        //satellite button
        satellite=new Button("Satellite(5)(E)");
        satellite.setTranslateX(1000);
        satellite.setTranslateY(40);
        satellite.setDisable(true);
        satellite.setOnAction(event -> createNewSatellite());
        getGameScene().addUINode(satellite);

        //this is the probe button
        probe=new Button("Probe(11)(R)");
        probe.setTranslateX(1000);
        probe.setTranslateY(70);
        probe.setDisable(true);
        probe.setOnAction(event -> createNewProbe());
        getGameScene().addUINode(probe);

        //start button
        start=new Button("Start");
        start.setTranslateX(1100);
        start.setTranslateY(70);
        start.setOnAction(event -> newGame());
        getGameScene().addUINode(start);

        //message label
        messages=new ListView();
        messages.setTranslateX(1000);
        messages.setTranslateY(250);
        messages.setMaxWidth(200);
        messages.setMaxHeight(350);
        getGameScene().addUINode(messages);

        lbl.setText("Welcome to the exploration game. Objective: conquer the universe. < > to move turret, / to fire. You need 20 supplies to be capable for level 2, and 40 for level 3. White=food, Yellow=fuel, Red=supplies.");
        lbl.setTranslateX(100);
        lbl.setTranslateY(200);
        lbl.setMaxWidth(900);
        lbl.setMaxHeight(50);
        lbl.setTextFill(Color.WHITE);
        lbl.setWrapText(true);
        getGameScene().addUINode(lbl);
    }

    /**
     * pre: game initialized with player on screen
     * post:detects collisions between entities
     */
    @Override protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.RESOURCE) {
            @Override protected void onCollisionBegin(Entity player, Entity resource) {
                addResourceBenefit(locateIndexEntity((int)resource.getX(),(int)resource.getY(),resourcesOnScreen));
                removeResource(locateIndexEntity((int)resource.getX(),(int)resource.getY(),resourcesOnScreen));
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PROBE, EntityType.RESOURCE) {
            @Override protected void onCollisionBegin(Entity probe, Entity resource) {
                int index=locateIndexEntity((int)resource.getX(),(int)resource.getY(),resourcesOnScreen);
                addResourceBenefit(index);
                removeResource(index);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.RESOURCE) {
            @Override protected void onCollisionBegin(Entity bullet, Entity resource) {
                int index=locateIndexEntity((int)resource.getX(),(int)resource.getY(),resourcesOnScreen);
                removeResource(index);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SATELLITE, EntityType.RESOURCE) {
            @Override protected void onCollisionBegin(Entity bullet, Entity resource) {
                int index=locateIndexEntity((int)resource.getX(),(int)resource.getY(),resourcesOnScreen);
                addResourceBenefit(index);
                removeResource(index);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.PLANETS) {
            @Override protected void onCollisionBegin(Entity bullet, Entity planet){
                int index=locateIndexBullet((int)bullet.getX(),(int)bullet.getY(),bulletsOnScreen);
                if (bulletsOnScreen.get(index).getType()==1){
                    bulletsOnScreen.remove(index);
                    bullet.removeFromWorld();
                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.BULLET) {
            @Override protected void onCollisionBegin(Entity bullet, Entity bullet2){
                int index=locateIndexBullet((int)bullet.getX(),(int)bullet.getY(),bulletsOnScreen);
                bulletsOnScreen.remove(index);
                int index2=locateIndexBullet((int)bullet2.getX(),(int)bullet2.getY(),bulletsOnScreen);
                bulletsOnScreen.remove(index2);
                bullet.removeFromWorld();
                bullet2.removeFromWorld();
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.SATELLITE) {
            @Override protected void onCollisionBegin(Entity bullet, Entity sat){
                removeSatellite(sat);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLANETS, EntityType.SATELLITE) {
            @Override protected void onCollisionBegin(Entity bullet, Entity sat){
                removeSatellite(sat);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SATELLITE, EntityType.SATELLITE) {
            @Override protected void onCollisionBegin(Entity satellite, Entity sat2){
                removeSatellite(satellite);
                removeSatellite(sat2);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.PLAYER) {
            @Override protected void onCollisionBegin(Entity bullet, Entity player){
                int index=locateIndexBullet((int)bullet.getX(),(int)bullet.getY(),bulletsOnScreen);
                if (bulletsOnScreen.get(index).getType()==0) {
                    bulletsOnScreen.remove(index);
                    bullet.removeFromWorld();
                    changeLife(-1);
                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ASTEROID) {
            @Override protected void onCollisionBegin(Entity bullet, Entity asteroid1){
                int index=locateIndexBullet((int)bullet.getX(),(int)bullet.getY(),bulletsOnScreen);
                bulletsOnScreen.remove(index);
                bullet.removeFromWorld();
                removeAsteroid(locateIndexAsteroid((int)asteroid1.getX(),(int)asteroid1.getY(),asteroids));
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ALIENSHIP) {
            @Override protected void onCollisionBegin(Entity bullet, Entity alienship){
                int index=locateIndexBullet((int)bullet.getX(),(int)bullet.getY(),bulletsOnScreen);
                if (bulletsOnScreen.get(index).getType()==1) {
                    bulletsOnScreen.remove(index);
                    bullet.removeFromWorld();
                    int[] index2=locateIndexAlienShip((int)alienship.getX(),(int)alienship.getY(),planets);
                    try {planets.get(index2[0]).getShips().get(index2[1]).setHealth(planets.get(index2[0]).getShips().get(index2[1]).getHealth()-1); }
                    catch (NullPointerException x){}
                }

            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.RESOURCE) {
            @Override protected void onCollisionBegin(Entity asteroid, Entity resource) {
                removeResource(locateIndexEntity((int)resource.getX(),(int)resource.getY(),resourcesOnScreen));
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.ASTEROID) {
            @Override protected void onCollisionBegin(Entity asteroid1, Entity asteroid2) {
                removeAsteroid(locateIndexAsteroid((int)asteroid1.getX(),(int)asteroid1.getY(),asteroids));
                removeAsteroid(locateIndexAsteroid((int)asteroid2.getX(),(int)asteroid2.getY(),asteroids));
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.SATELLITE) {
            @Override protected void onCollisionBegin(Entity bullet, Entity sat){
                removeSatellite(sat);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.RESOURCECOLLECT) {
            @Override protected void onCollisionBegin(Entity asteroid1, Entity rc) {
                try{
                    removeStructure(rc,0,mainPlayer.returnIndexOfStructure((int)rc.getX(),(int)rc.getY(),0));
                }
                catch (ArrayIndexOutOfBoundsException ignored){}
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.PORTAL) {
            @Override protected void onCollisionBegin(Entity asteroid1, Entity portal) {
                try{
                    removeStructure(portal,1,mainPlayer.returnIndexOfStructure((int)portal.getX(),(int)portal.getY(),1));
                }
                catch (ArrayIndexOutOfBoundsException ignored){}
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.FACTORY) {
            @Override protected void onCollisionBegin(Entity asteroid1, Entity factory) {
                try{
                    removeStructure(factory,2,mainPlayer.returnIndexOfStructure((int)factory.getX(),(int)factory.getY(),2));
                }
                catch (ArrayIndexOutOfBoundsException ignored){}
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.PLANETS) {
            @Override protected void onCollisionBegin(Entity asteroid1, Entity planet) {
                removeAsteroid(locateIndexAsteroid((int)asteroid1.getX(),(int)asteroid1.getY(),asteroids));
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.PLAYER) {
            @Override protected void onCollisionBegin(Entity asteroid1, Entity player) {
                changeLife(-0.5*asteroids.get(locateIndexAsteroid((int)asteroid1.getX(),(int)asteroid1.getY(),asteroids)).getSpeed());
                removeAsteroid(locateIndexAsteroid((int)asteroid1.getX(),(int)asteroid1.getY(),asteroids));
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ASTEROID, EntityType.PROBE) {
            @Override protected void onCollisionBegin(Entity asteroid1, Entity probe) {
                try{
                    probes.get(0).getEntityOnScreen().removeFromWorld();
                    probes.remove(0);
                }
                catch (ArrayIndexOutOfBoundsException ignored){}
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLACKHOLE, EntityType.RESOURCE) {
            @Override protected void onCollisionBegin(Entity bullet, Entity resource) {
                int index=locateIndexEntity((int)resource.getX(),(int)resource.getY(),resourcesOnScreen);
                removeResource(index);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLACKHOLE, EntityType.ASTEROID) {
            @Override protected void onCollisionBegin(Entity bullet, Entity asteroid) {
                int index=locateIndexAsteroid((int)asteroid.getX(),(int)asteroid.getY(),asteroids);
                removeAsteroid(index);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLACKHOLE, EntityType.FACTORY) {
            @Override protected void onCollisionBegin(Entity blackhole, Entity factory) {
                try {
                    removeStructure(factory, 2, mainPlayer.returnIndexOfStructure((int) factory.getX(), (int) factory.getY(), 2));
                }
                catch (ArrayIndexOutOfBoundsException ignored){

                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLACKHOLE, EntityType.RESOURCECOLLECT) {
            @Override protected void onCollisionBegin(Entity blackhole, Entity rc) {
                try {
                    removeStructure(rc, 0, mainPlayer.returnIndexOfStructure((int) rc.getX(), (int) rc.getY(), 0));
                }
                catch (ArrayIndexOutOfBoundsException ignored){

                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLACKHOLE, EntityType.PORTAL) {
            @Override protected void onCollisionBegin(Entity blackhole, Entity portal) {
                try {
                    removeStructure(portal, 1, mainPlayer.returnIndexOfStructure((int) portal.getX(), (int) portal.getY(), 1));
                }
                catch (ArrayIndexOutOfBoundsException ignored){

                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLACKHOLE, EntityType.PLAYER) {
            @Override protected void onCollision(Entity blackhole, Entity player) {
                try {
                    changeLife(-0.05);
                }
                catch (ArrayIndexOutOfBoundsException ignored){

                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLACKHOLE, EntityType.SATELLITE) {
            @Override protected void onCollision(Entity blackhole, Entity satellite) {
                try {
                    removeSatellite(satellite);
                }
                catch (ArrayIndexOutOfBoundsException ignored){

                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLACKHOLE, EntityType.PROBE) {
            @Override protected void onCollision(Entity blackhole, Entity probe) {
                try {
                    probes.get(0).getEntityOnScreen().removeFromWorld();
                    probes.remove(0);
                }
                catch (ArrayIndexOutOfBoundsException ignored){

                }
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.FACTORY) {
            @Override protected void onCollisionBegin(Entity blackhole, Entity factory) {
                try {
                    if(!mainPlayer.getFactories().get(mainPlayer.returnIndexOfStructure((int) factory.getX(), (int) factory.getY(), 2)).isCanBeUsed()) {
                        if (returnRandInt(0,20)==1)mainPlayer.addBuildingSupplies(1);
                        removeStructure(factory, 2, mainPlayer.returnIndexOfStructure((int) factory.getX(), (int) factory.getY(), 2));
                    }
                } catch (ArrayIndexOutOfBoundsException ignored){ }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.RESOURCECOLLECT) {
            @Override protected void onCollisionBegin(Entity player, Entity rc) {
                try {
                    if (!mainPlayer.getCollectors().get(mainPlayer.returnIndexOfStructure((int) rc.getX(), (int) rc.getY(), 0)).isCanBeUsed()) {
                        if (returnRandInt(0, 20) == 1) mainPlayer.addBuildingSupplies(1);
                        removeStructure(rc, 0, mainPlayer.returnIndexOfStructure((int) rc.getX(), (int) rc.getY(), 0));
                    }
                }
                catch (ArrayIndexOutOfBoundsException ignored){

                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.PORTAL) {
            @Override protected void onCollisionBegin(Entity blackhole, Entity p) {
                try {
                    if(!mainPlayer.getPortals().get(mainPlayer.returnIndexOfStructure((int) p.getX(), (int) p.getY(), 1)).isCanBeUsed()) {
                        if (returnRandInt(0,30)==1)mainPlayer.addBuildingSupplies(1);
                        removeStructure(p, 1, mainPlayer.returnIndexOfStructure((int) p.getX(), (int) p.getY(), 1));
                    }
                }
                catch (ArrayIndexOutOfBoundsException ignored){

                }
            }
        });
    }

    @Override protected void initUI() {

    }

    /**
     * pre:server initalized
     * post:scene is set up
     */
    @Override protected void initSettings(GameSettings settings) {
        settings.setWidth(1200);
        settings.setHeight(600);
        settings.setTitle("Server");
        settings.setVersion("");
        settings.setFullScreenAllowed(true);
        settings.setIntroEnabled(false); // turn off intro
        settings.setMenuEnabled(false);  // turn off menus
    }

    /**
     * pre:array is entity and not empty
     * post:finds an entity at x and y
     */
    public int locateIndexEntity(int x, int y, ArrayList<Entity> entities){
        for (int i = 0; i < entities.size(); i++) {
            if((int)(entities.get(i).getX())==x&&(int)(entities.get(i).getY())==y)return i;
        }
        return -1;
    }

    /**
     * pre: objects isnt empty
     * post: returns index of asteroid
     * @param x
     * @param y
     * @param objects
     * @return
     */
    public int locateIndexAsteroid(int x, int y, ArrayList<Asteroid> objects){
        for (int i = 0; i < objects.size(); i++) {
            if((int)(objects.get(i).getX())==x&&(int)(objects.get(i).getY())==y)return i;
        }
        return -1;
    }

    /**
     * pre: objects isnt empty
     * post: returns index of planets
     * @param x
     * @param y
     * @param objects
     * @return
     */
    public int locateIndexPlanets(int x, int y, ArrayList<Planet> objects){
        for (int i = 0; i < objects.size(); i++) {
            if((int)(objects.get(i).getxCoord())==x&&(int)(objects.get(i).getyCoord())==y)return i;
        }
        return -1;
    }

    /**
     * pre: objects isnt empty
     * post: returns index of bullet
     * @param x
     * @param y
     * @param objects
     * @return
     */
    public int locateIndexBullet(int x, int y, ArrayList<Bullet> objects){
        for (int i = 0; i < objects.size(); i++) {
            if((int)(objects.get(i).getEntityOnScreen().getX())==x&&(int)(objects.get(i).getEntityOnScreen().getY())==y)return i;
        }
        return -1;
    }

    /**
     * pre: objects isnt empty
     * post: returns index of satellite
     * @param x
     * @param y
     * @param objects
     * @return
     */
    public int locateIndexSatellite(int x, int y, ArrayList<Satellite> objects){
        for (int i = 0; i < objects.size(); i++) {
            if((int)(objects.get(i).getX())==x&&(int)(objects.get(i).getY())==y)return i;
        }
        return -1;
    }

    /**
     * pre: objects isnt empty
     * post: returns index of satellite
     * @param x
     * @param y
     * @param objects
     * @return
     */
    public int[] locateIndexAlienShip(int x, int y, ArrayList<Planet> objects){
        for (int i = 0; i < objects.size(); i++) {
            for (int j = 0; j <objects.get(i).getShips().size() ; j++) {
                if((int)(objects.get(i).getShips().get(j).getEntityOnScreen().getX())==x&&(int)(objects.get(i).getShips().get(j).getEntityOnScreen().getY())==y)return new int[]{i,j};
            }
        }
        return null;
    }

    /**
     * pre: resources exist
     * post: resources move
     */
    public void moveResources(){
        for (int i = resources.size() - 1; i >= 0; i--) {
            Resource r = resources.get(i);
            if (r.isBeingMoved()) {
                if (r.getTargetX()!=r.getX()){
                    double speed=200/Math.abs(r.getTargetX()-r.getX());
                    r.setY(r.returnYbasedOnX(r.getX()+((r.getTargetX() - r.getX())/Math.abs(r.getTargetX() - r.getX()))/speed*r.getSpeed()));
                    r.setX(r.getX()+((r.getTargetX() - r.getX())/Math.abs(r.getTargetX() - r.getX()))/speed*r.getSpeed());
                }
                updateLocationOfResource(i, r.getX(),r.getY());
            }
            if (r.isBeRemoved()){
                removeResource(i);
            }
        }
    }

    /**
     * pre: planets exist
     * post: moves bullets fired from planets
     */
    public void moveBullets(){
        for (Planet p:planets) {
            p.moveBullets(getSettings().getWidth(),getSettings().getHeight());
            for (int i = p.getBullets().size() - 1; i >= 0; i--) {
                if (p.getBullets().get(i).getX()<0||p.getBullets().get(i).getX()>getSettings().getWidth()||p.getBullets().get(i).getY()<0||p.getBullets().get(i).getY()>getSettings().getHeight()){
                    p.getBullets().get(i).getEntityOnScreen().removeFromWorld();
                    p.getBullets().remove(i);
                }
            }
        }
        for (int i = mainPlayer.getBullets().size() - 1; i >= 0; i--) {
            Bullet p = mainPlayer.getBullets().get(i);
            p.moveBullet(getSettings().getWidth(),getSettings().getHeight());
            if (p.getX() < 0 || p.getX() > getSettings().getWidth() || p.getY() < 0 || p.getY() > getSettings().getHeight()) {
                p.getEntityOnScreen().removeFromWorld();
                mainPlayer.getBullets().remove(i);
            }
        }
    }

    /**
     * pre: planets exist
     * post: alien ships moved
     */
    public void moveAlienShips(){
        for (Planet p: planets) {
            p.moveShips();
            for (AlienShip as:p.getShips()) {
                as.setTargetX(player1OnScreen.getX());
                as.setTargetY(player1OnScreen.getY());
                as.setEquation(as.getX(),as.getY(),as.getTargetX(),as.getTargetY());
                as.getEntityOnScreen().rotateBy(-1*as.getEntityOnScreen().getRotation());
                as.getEntityOnScreen().rotateBy(returnAngleBetweenCoordinates(as.getX(),as.getY(),as.getTargetX(),as.getTargetY()));
                if (p.getBulletTimer()==0&&(p.detectPlayer(player1OnScreen,120)||p.getDefenseType()==1)&&!p.isConquered()) {
                    Bullet entBul = spawnBullet(p.getShips().get(p.getShips().size() - 1).getX(), p.getShips().get(p.getShips().size() - 1).getY(), player1OnScreen.getX(), player1OnScreen.getY(),0);
                    p.addBullet(entBul);
                    p.getBullets().get(p.getBullets().size() - 1).setEquation(entBul.getX(), entBul.getY(), entBul.getTargetX(), entBul.getTargetY());
                    p.setBulletTimer(1);
                }
            }
        }
    }

    /**
     * pre: probes exist
     * post: probes are moved towards the target
     */
    public void moveProbes(){
        for (ResourceProbe p:probes) {
            p.detectResource(resources);
            p.moveProbe();
        }
    }

    /**
     * pre:satellites exist
     * post: position of satellites is updated
     */
    public void moveSatellites(){
        for (Satellite s: satellites) {
            s.move();
            s.getEntity().rotateBy(-1*s.getEntity().getRotation());
            s.getEntity().rotateBy(returnAngleBetweenCoordinates(s.getX(),s.getY(),s.getTargetx(),s.getTargety()));
        }
    }

    /**
     * pre:asteroids exist
     * post: position of asteroids is updated
     */
    public void moveAsteroids(){
        for (int i = asteroids.size() - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);
            asteroid.moveAsteroid();
            if (!asteroid.isWithinBoundary())
                asteroid.detectAsteroid(getSettings().getWidth(), getSettings().getHeight());
            if (asteroid.isWithinBoundary()) {
                asteroid.detectAsteroid(getSettings().getWidth(), getSettings().getHeight());
                if (!asteroid.isWithinBoundary()) {
                    removeAsteroid(i);
                }
            }
        }
    }

    /**
     * pre:blackholes exist
     * post: position of blackHoles is updated
     */
    public void moveBlackHoles(){
        for (int i = blackHoles.size() - 1; i >= 0; i--) {
            BlackHole blackHole = blackHoles.get(i);
            ArrayList<StructureBlock> structures=new ArrayList<>();
            for (Factory f:mainPlayer.getFactories()) {
                for (StructureBlock sb:f.getBlocks()) {
                    structures.add(sb);
                }
            }
            for (ResourceCollector f:mainPlayer.getCollectors()) {
                for (StructureBlock sb:f.getBlocks()) {
                    structures.add(sb);
                }
            }
            for (Portal f:mainPlayer.getPortals()) {
                for (StructureBlock sb:f.getBlocks()) {
                    structures.add(sb);
                }
            }
            blackHole.detectEntities(resources,asteroids,structures,player1OnScreen,turret,probes);
            blackHole.moveBlackHole();
            if (!blackHole.isWithinBoundary())
                blackHole.detectBlackHole(getSettings().getWidth(), getSettings().getHeight());
            if (blackHole.isWithinBoundary()) {
                blackHole.detectBlackHole(getSettings().getWidth(), getSettings().getHeight());
                if (!blackHole.isWithinBoundary()) {
                    removeBlackHole(i);
                }
            }
        }
    }

    /**
     * pre: user wants to start game
     * post: game begins
     */
    public void newGame(){
        getGameWorld().clear();
        resourcesOnScreen.clear();
        resources.clear();
        getMasterTimer().clear();
        planets.clear();
        satellites.clear();
        lives.clear();
        blackHoles.clear();
        probes.clear();
        messages.getItems().clear();
        bulletsOnScreen.clear();
        asteroids.clear();
        lbl.setText("");
        start.setText("Restart");
        life=10.0;
        for (int i = 0; i <10 ; i++) {
            Entity life=Entities.builder()
                    .at(1000+i*20,200)
                    .viewFromNodeWithBBox(new Rectangle(18,18,Color.RED))
                    .renderLayer(RenderLayer.TOP)
                    .buildAndAttach(getGameWorld());
            lives.add(life);
        }
        Entities.builder()
                .at(0,0)
                .viewFromNodeWithBBox(new Rectangle(1000,600,Color.BLACK))
                .buildAndAttach(getGameWorld());
        Entities.builder()
                .at(1000,0)
                .viewFromNodeWithBBox(new Rectangle(200,600,Color.GRAY))
                .renderLayer(RenderLayer.TOP)
                .buildAndAttach(getGameWorld());

        generateResources(100);
        level=1;
        moves=0;
        player1OnScreen = Entities.builder()
                .type(EntityType.PLAYER)
                .with(new CollidableComponent(true))
                .at(0, 0)
                .viewFromNodeWithBBox(new Rectangle(15, 15, Color.BLUE))
                .buildAndAttach(getGameWorld());
        turret= Entities.builder()
                .at(player1OnScreen.getX()+player1OnScreen.getWidth()/2.0-4, player1OnScreen.getY()+player1OnScreen.getHeight()/2.0-4)
                .viewFromNodeWithBBox(new Rectangle(8, 8, Color.RED))
                .buildAndAttach(getGameWorld());
        mainPlayer=new Player(.7,15,15);
        runLoopActions();
    }

    /**
     * pre:client server connected, player has structures
     * post: one block of structure placed
     */
    public void placeStructures() {
        //places resource collector block
        for (ResourceCollector r : mainPlayer.getCollectors()) {
            if (r.getPlacingIndex()< r.getxCoordsToBuild().size()&&!r.isBuilt()) {
                Entity temp = Entities.builder()
                        .at(r.getxCoordsToBuildAtIndex(r.getPlacingIndex()), r.getyCoordsToBuildAtIndex(r.getPlacingIndex()))
                        .viewFromNodeWithBBox(new Rectangle(6, 6, Color.GREEN))
                        .with(new CollidableComponent(true))
                        .type(EntityType.RESOURCECOLLECT)
                        .buildAndAttach(getGameWorld());
                r.getBlocks().add(new StructureBlock(temp.getX(),temp.getY(),temp));
                r.addPlacingIndex(1);
                r.getEntities().add(temp);
            }
            else r.setBuilt(true);
        }
        //places portal block
        for (Portal r : mainPlayer.getPortals()) {
            if (r.getPlacingIndex()< r.getxCoordsToBuild().size()&&!r.isBuilt()) {
                Entity temp = Entities.builder()
                        .at(r.getxCoordsToBuildAtIndex(r.getPlacingIndex()), r.getyCoordsToBuildAtIndex(r.getPlacingIndex()))
                        .viewFromNodeWithBBox(new Rectangle(6, 6, Color.PURPLE))
                        .with(new CollidableComponent(true))
                        .type(EntityType.PORTAL)
                        .buildAndAttach(getGameWorld());
                r.addPlacingIndex(1);
                r.getBlocks().add(new StructureBlock(temp.getX(),temp.getY(),temp));
                r.getEntities().add(temp);
            }
            else r.setBuilt(true);
        }
        //places factory block
        for (Factory f : mainPlayer.getFactories()) {
            if (f.getPlacingIndex()< f.getxCoordsToBuild().size()&&!f.isBuilt()) {
                Entity temp = Entities.builder()
                        .at(f.getxCoordsToBuildAtIndex(f.getPlacingIndex()), f.getyCoordsToBuildAtIndex(f.getPlacingIndex()))
                        .viewFromNodeWithBBox(new Rectangle(3, 3, Color.PURPLE))
                        .with(new CollidableComponent(true))
                        .type(EntityType.FACTORY)
                        .buildAndAttach(getGameWorld());
                f.addPlacingIndex(1);
                f.getEntities().add(temp);
                f.getBlocks().add(new StructureBlock(temp.getX(),temp.getY(),temp));
            }
            else{
                f.setBuilt(true);
            }
        }
    }

    /**
     * pre: game has been initalized
     * post:runs actions such as structure placement
     */
    public void runLoopActions(){
        getMasterTimer().runAtInterval(() -> {
            try {
                if (returnRandInt(0,6/level)==1)spawnAsteroids(returnRandInt(1,level+1));
                if (returnRandInt(0,40/level)==1)spawnBlackHole();
                checkStructureTimer();
                if (mainPlayer.getFood()>0&&mainPlayer.getFuel()>0) changeLife(0.5);
                for (Planet planet : planets) {
                    if (planet.getSatellites() >= 4) planet.addSatelliteTimer(1);
                    else planet.setConqueredTimer(0);
                    if (planet.getConqueredTimer() >= 2){
                        planet.setConquered(true);
                        planet.getEntity().setViewWithBBox(new Circle(planet.getxCoord()-10,planet.getyCoord()-10,20,Color.GREEN));
                        mainPlayer.addBuildingSupplies(30);
                        mainPlayer.addFuel(20);
                        mainPlayer.addFood(20);
                    }
                }
            }
            catch (Exception ignored) {}
        }, Duration.seconds(2));
        getMasterTimer().runAtInterval(() -> {
            mainPlayer.addFood(-1);
            if(mainPlayer.getFood()<0)mainPlayer.addFood(-1*mainPlayer.getFood());
        }, Duration.seconds(1.5));
        getMasterTimer().runAtInterval(() -> {
            int amount=0;
            for (Planet p:planets) {
                if (p.getBulletTimer()>0)p.setBulletTimer(p.getBulletTimer()+1);
                if(p.isConquered())amount++;
            }
            if (amount==planets.size()&&level==3)showEndScreen(true);
            if (life<=0) showEndScreen(false);
            if (mainPlayer.getBulletTimer()>0)mainPlayer.setBulletTimer(mainPlayer.getBulletTimer()+1);
            if (mainPlayer.getBulletTimer()>25)mainPlayer.setBulletTimer(0);
            if (returnRandInt(0,50)==1) generateResources(returnRandInt(1,level+1));//randomly generate resources

            if (Factory.getPlaceTimer()>=1) Factory.setPlaceTimer(Factory.getPlaceTimer()+1);
            if (Factory.getPlaceTimer()>10) Factory.setPlaceTimer(0);
            if (ResourceCollector.getPlaceTimer()>=1) ResourceCollector.setPlaceTimer(ResourceCollector.getPlaceTimer()+1);
            if (ResourceCollector.getPlaceTimer()>10) ResourceCollector.setPlaceTimer(0);
            if (Satellite.getPlaceTimer()>=1) Satellite.setPlaceTimer(Satellite.getPlaceTimer()+1);
            if (Satellite.getPlaceTimer()>10) Satellite.setPlaceTimer(0);
            if (ResourceProbe.getPlaceTimer()>=1) ResourceProbe.setPlaceTimer(ResourceProbe.getPlaceTimer()+1);
            if (ResourceProbe.getPlaceTimer()>10) ResourceProbe.setPlaceTimer(0);
            if (Portal.getPlaceTimer()>=1) Portal.setPlaceTimer(Portal.getPlaceTimer()+1);
            if (Portal.getPlaceTimer()>10) Portal.setPlaceTimer(0);

            displayResources();
            updateFuelAndFood();
            moveSatellites();
            if (level==1) unlockLevelTwo();
            if (level==2) unlockLevelThree();

            //structure action
            placeStructures();
            detectInteractionBetweenResourceCollector();
            detectInteractionBetweenPortal();
            detectInteractionBetweenFactory();
            detectInteractionBetweenPlanet();
            moveBlackHoles();
            moveResources();
            moveBullets();
            moveAsteroids();
            moveProbes();
            moveAlienShips();
            if (messages.getItems().size()>9){
                messages.getItems().remove(0);
            }
            for (Planet p:planets) {
                for (int i = p.getShips().size() - 1; i >= 0; i--) {
                    if(p.getShips().get(i).getHealth()<=0){
                        p.getShips().get(i).getEntityOnScreen().removeFromWorld();
                        p.getShips().remove(i);
                    }
                }
            }
        }, Duration.millis(1));
    }

    /**
     * pre: coordinates are double values
     * post: returns angle between them
     */
    public double returnAngleBetweenCoordinates(double x1, double y1, double x2, double y2){
        int rotate =(int)(180*(Math.acos(Math.abs(x2-x1)/(Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)))))/Math.PI);
        if (y2>=y1&&x2>=x1)return rotate;
        if (y2>=y1&&x2<=x1)return 180-rotate;
        if (y2<=y1&&x2>=x1)return -1*rotate;
        return 180+rotate;//(y2<=y1&&x2<=x1)
    }

    /**
     * pre:num1<num2
     * post:returns randint between them, inclusive
     */
    public int returnRandInt(int num1,int num2){
        return (int)(Math.random()*(num2-num1+1)+num1);
    }

    /**
     * pre:num1<num2
     * post:returns randdouble between them, inclusive
     */
    public double returnRandDouble(double num1,double num2){
        return (Math.random()*(num2-num1+1)+num1);
    }

    /**
     * pre: positive parameters
     * post returns the distance
     */
    public double returnDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    /**
     * PRE: double entered
     * post: returns rounded value
     */
    public double roundNum(double num, int places){
        num*=Math.pow(10,places);
        num=Math.round(num);
        return num/(Math.pow(10,places));
    }

    /**
     * pre:resources exist
     * post:removes a resource from the game
     */
    public void removeResource(int index){
        resourcesOnScreen.get(index).removeFromWorld();
        resourcesOnScreen.remove(index);
        resources.remove(index);
    }

    /**
     * pre: structure exists
     * post: structure removed from board entity by entity
     */
    public void removeStructure(Entity ent, int type, int index) {
        if (type == 0){
            mainPlayer.getCollectors().get(index).setCanBeUsed(false);
            int index1=locateIndexEntity((int)ent.getX(),(int)ent.getY(),mainPlayer.getCollectors().get(index).getEntities());
            ent.removeFromWorld();
            mainPlayer.getCollectors().get(index).getBlocks().remove(index1);
            if (mainPlayer.getCollectors().get(index).getBlocks().size()==0)mainPlayer.getCollectors().remove(index);
        }
        if (type == 1){
            mainPlayer.getPortals().get(index).setCanBeUsed(false);
            int index1=locateIndexEntity((int)ent.getX(),(int)ent.getY(),mainPlayer.getPortals().get(index).getEntities());
            ent.removeFromWorld();
            mainPlayer.getPortals().get(index).getBlocks().remove(index1);
            if (mainPlayer.getPortals().get(index).getBlocks().size()==0)mainPlayer.getPortals().remove(index);
        }
        if (type == 2) {
            mainPlayer.getFactories().get(index).detectResources(resources, true);
            mainPlayer.getFactories().get(index).setCanBeUsed(false);
            int index1 = locateIndexEntity((int) ent.getX(), (int) ent.getY(), mainPlayer.getFactories().get(index).getEntities());
            ent.removeFromWorld();
            mainPlayer.getFactories().get(index).getBlocks().remove(index1);
            if (mainPlayer.getFactories().get(index).getBlocks().size() == 0) mainPlayer.getFactories().remove(index);
        }
    }

    /**
     * pre:asteroids exist
     * post:removes an asterod from the game
     */
    public void removeAsteroid(int index){
        getGameWorld().removeEntity(asteroids.get(index).getEntityOnScreen());
        asteroids.remove(index);
    }

    /**
     * pre:blackHoles exist
     * post:removes a blackhole from the game
     */
    public void removeBlackHole(int index){
        getGameWorld().removeEntity(blackHoles.get(index).getEntityOnScreen());
        blackHoles.remove(index);
    }

    /**
     * pre: entity is active
     * post: removes satellite
     */
    public void removeSatellite(Entity satellite){
        int satIndex=locateIndexSatellite((int)satellite.getX(),(int)satellite.getY(),satellites);
        int planetIndex=locateIndexPlanets((int)satellites.get(satIndex).getTargetx(),(int)satellites.get(satIndex).getTargety(),planets);
        planets.get(planetIndex).addSatellites(-1);
        satellites.remove(satIndex);
        satellite.removeFromWorld();
    }

    /**
     *pre: user has reached level 3
     * post: planets appear
     */
    public void spawnPlanets(){
        for (int i = 0; i <1 ; i++) {
            int x=returnRandInt(50, 950);
            int y=returnRandInt(25, 575);
            Entity temp = Entities.builder()
                    .at(x,y)
                    .viewFromNodeWithBBox(new Circle(x,y,20, Color.RED))
                    .type(EntityType.PLANETS)
                    .renderLayer(RenderLayer.TOP)
                    .with(new CollidableComponent(true))
                    .buildAndAttach(getGameWorld());
            planets.add(new Planet(temp.getX(),temp.getY(),20,returnRandInt(0,1),temp));

        }
    }

    public void showEndScreen(boolean won){
        getGameWorld().clear();

        Entities.builder()
                .at(0,0)
                .viewFromNodeWithBBox(new Rectangle(1000,600,Color.BLACK))
                .buildAndAttach(getGameWorld());
        Entities.builder()
                .at(1000,0)
                .viewFromNodeWithBBox(new Rectangle(200,600,Color.GRAY))
                .buildAndAttach(getGameWorld());

        if (won)lbl.setText("You won! Click restart to play again");
        else lbl.setText("You lost :(. Click restart to try again");
        getMasterTimer().clear();
    }

    /**
     * pre: all coordinates are within game
     * post: creates a bullet with target and returns entity
     * @return
     */
    public Bullet spawnBullet(double x, double y, double givenTargetx, double givenTargety, int type){
        Entity entBul=Entities.builder()
                .type(EntityType.BULLET)
                .with(new CollidableComponent(true))
                .at(x,y)
                .viewFromNodeWithBBox( new Rectangle(20,10,Color.WHITE))
                .buildAndAttach(getGameWorld());
        double targetX=givenTargetx;
        double targetY=givenTargety;
        if(x!=targetX){
            double slope=(y-targetY)/(x-targetX);
            double b=targetY-slope*targetX;
            if(entBul.getX()<givenTargetx)targetX=1400;
            else targetX=-200;
            if(slope>0) targetY=targetX*slope+b;
            else if(slope<0) targetY=targetX*slope+b;
            else targetY=targetY;
        }
        else{
            targetX=givenTargetx;
            if(entBul.getY()<givenTargety)targetY=800;
            else targetY=-200;
        }
        entBul.rotateBy(returnAngleBetweenCoordinates(x,y,targetX,targetY));
        Bullet bToReturn=new Bullet(targetX,targetY,x,y,entBul,type);
        bulletsOnScreen.add(bToReturn);
        return bToReturn;
    }

    /**
     * pre: game has begun
     * post: asteroid spawned
     */
    public void spawnAsteroids(int amount) {
        for (int i = 0; i < amount; i++) {
            int prob = returnRandInt(1, 4);
            int x;
            int y;
            int targetx;
            int targety;
            if (prob == 1) {
                x = returnRandInt(0, getSettings().getWidth());
                y = returnRandInt(-200, -20);
                targetx = returnRandInt(0, getSettings().getWidth());
                targety = returnRandInt(0, getSettings().getHeight() + 200);
                while (0 < targetx && targetx < getSettings().getWidth() && 0 < targety && targety < getSettings().getHeight()) {
                    targetx = returnRandInt(-200, getSettings().getWidth() + 200);
                    targety = returnRandInt(0, getSettings().getHeight() + 200);
                }

            } else if (prob == 2) {
                x = returnRandInt(-200, -20);
                y = returnRandInt(0, getSettings().getHeight());
                targetx = returnRandInt(0, getSettings().getWidth() + 200);
                targety = returnRandInt(-200, getSettings().getHeight() + 200);
                while (0 < targetx && targetx < getSettings().getWidth() && 0 < targety && targety < getSettings().getHeight()) {
                    targetx = returnRandInt(0, getSettings().getWidth() + 200);
                    targety = returnRandInt(-200, getSettings().getHeight() + 200);
                }
            } else if (prob == 3) {
                x = returnRandInt(0, getSettings().getWidth());
                y = returnRandInt(getSettings().getHeight() + 20, getSettings().getHeight() + 200);
                targetx = returnRandInt(-200, getSettings().getWidth() + 200);
                targety = returnRandInt(-200, getSettings().getHeight());
                while (0 < targetx && targetx < getSettings().getWidth() && 0 < targety && targety < getSettings().getHeight()) {
                    targetx = returnRandInt(-200, getSettings().getWidth() + 200);
                    targety = returnRandInt(-200, getSettings().getHeight());
                }

            } else {
                x = returnRandInt(getSettings().getWidth() + 20, getSettings().getWidth() + 200);
                y = returnRandInt(0, getSettings().getHeight());
                targetx = returnRandInt(-200, getSettings().getWidth());
                targety = returnRandInt(-200, getSettings().getHeight() + 200);
                while (0 < targetx && targetx < getSettings().getWidth() && 0 < targety && targety < getSettings().getHeight()) {
                    targetx = returnRandInt(-200, getSettings().getWidth());
                    targety = returnRandInt(-200, getSettings().getHeight() + 200);
                }
            }
            Entity temp = Entities.builder()
                    .at(x, y)
                    .with(new CollidableComponent(true))
                    .viewFromNodeWithBBox(new Circle(x, y, 10, Color.GRAY))
                    .type(EntityType.ASTEROID)
                    .buildAndAttach(getGameWorld());
            asteroids.add(new Asteroid(x, y, 10, temp, targetx, targety));
            asteroids.get(asteroids.size() - 1).setEquation(targetx, targety, x, y);
        }
    }

    /**
     * pre: game has begun
     * post: blackHole spawned
     */
    public void spawnBlackHole(){
        int prob=returnRandInt(1,4);
        int x;
        int y;
        int targetx;
        int targety;
        if (prob==1){
            x=returnRandInt(0,getSettings().getWidth());
            y=returnRandInt(-200,-20);
            targetx=returnRandInt(0,getSettings().getWidth());
            targety=returnRandInt(0,getSettings().getHeight()+200);
            while (0<targetx&&targetx<getSettings().getWidth()&&0<targety&&targety<getSettings().getHeight()){
                targetx=returnRandInt(-200,getSettings().getWidth()+200);
                targety=returnRandInt(0,getSettings().getHeight()+200);
            }

        }
        else if (prob==2){
            x=returnRandInt(-200,-20);
            y=returnRandInt(0,getSettings().getHeight());
            targetx=returnRandInt(0,getSettings().getWidth()+200);
            targety=returnRandInt(-200,getSettings().getHeight()+200);
            while (0<targetx&&targetx<getSettings().getWidth()&&0<targety&&targety<getSettings().getHeight()){
                targetx=returnRandInt(0,getSettings().getWidth()+200);
                targety=returnRandInt(-200,getSettings().getHeight()+200);
            }
        }
        else if (prob==3){
            x=returnRandInt(0,getSettings().getWidth());
            y=returnRandInt(getSettings().getHeight()+20,getSettings().getHeight()+200);
            targetx=returnRandInt(-200,getSettings().getWidth()+200);
            targety=returnRandInt(-200,getSettings().getHeight());
            while (0<targetx&&targetx<getSettings().getWidth()&&0<targety&&targety<getSettings().getHeight()){
                targetx=returnRandInt(-200,getSettings().getWidth()+200);
                targety=returnRandInt(-200,getSettings().getHeight());
            }

        }
        else{
            x=returnRandInt(getSettings().getWidth()+20,getSettings().getWidth()+200);
            y=returnRandInt(0,getSettings().getHeight());
            targetx=returnRandInt(-200,getSettings().getWidth());
            targety=returnRandInt(-200,getSettings().getHeight()+200);
            while (0<targetx&&targetx<getSettings().getWidth()&&0<targety&&targety<getSettings().getHeight()){
                targetx=returnRandInt(-200,getSettings().getWidth());
                targety=returnRandInt(-200,getSettings().getHeight()+200);
            }
        }
        Entity temp=Entities.builder()
                .at(x,y)
                .with(new CollidableComponent(true))
                .viewFromNodeWithBBox(new Rectangle(10,10, Color.BLACK))
                .type(EntityType.BLACKHOLE)
                .buildAndAttach(getGameWorld());
        blackHoles.add(new BlackHole(x,y,targetx,targety,50,temp));
        blackHoles.get(blackHoles.size()-1).setEquation(x,y,targetx,targety);
    }

    /**
     * pre: resources exist
     * post: location of resource is updated
     */
    public void updateLocationOfResource(int index, double x, double y){
        resourcesOnScreen.get(index).getPositionComponent().setX(x);
        resourcesOnScreen.get(index).getPositionComponent().setY(y);
        resources.get(index).setX(x);
        resources.get(index).setY(y);
    }

    /**
     * pre: game has begun
     * post: game displays the updated food and fuel supplied
     */
    public void updateFuelAndFood(){
        if(player1OnScreen.getX()>50||player1OnScreen.getY()>50){
            if (moves>=200){
                mainPlayer.addFuel(-1);
                if(mainPlayer.getFuel()<0)mainPlayer.addFuel(-1*mainPlayer.getFuel());
                moves=0;
                displayResources();
            }
        }
        if (mainPlayer.getFood()<=0)changeLife(-0.010);
        if (mainPlayer.getFuel()<=0)changeLife(-0.010);
    }

    /**
     * pre: user has started level one
     * post: checks if user can move on to level two
     */
    public void unlockLevelTwo(){
        if (mainPlayer.getBuildingSupplies()>=20){
            factory.setDisable(false);
            portal.setDisable(false);
            resourceCollect.setDisable(false);
            probe.setDisable(false);
            level=2;
            messages.getItems().add("Level 2 unlocked!");
        }
    }

    /**
     * pre: user has started level two
     * post: checks if user can move on to level three
     */
    public void unlockLevelThree(){
        if (mainPlayer.getBuildingSupplies()>=30) {
            satellite.setDisable(false);
            for (int i = 0; i <2 ; i++) {
                spawnPlanets();
            }
            level=3;
            messages.getItems().add("Level 3 unlocked!");
        }
    }
}