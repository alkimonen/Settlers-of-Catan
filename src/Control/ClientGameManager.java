package Control;

import Model.*;
import Model.DevelopmentCardTypes.KnightCard;
import Model.DevelopmentCardTypes.ProgressCardTypes.MonopolyCard;
import Model.DevelopmentCardTypes.ProgressCardTypes.RoadCard;
import Model.DevelopmentCardTypes.ProgressCardTypes.YearOfPlentyCard;
import Model.DevelopmentCardTypes.VictoryPointCard;
import View.Main;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

public class ClientGameManager extends ClientManager{
	private final String[] PLAYER_COLORS = {"#FFA500" , "#FF6347" , "#98FB98" , "#87CEFA"};
    private int[][] DICE_TO_ADJACENT_TOWNS_WOOL = {{} , {} , {} , {40,44,45,48,49,52} , {31,36,37,41,42,46} , {17,22,23,28,29,34} , {20,25,26,31,32,37} , {} , {} , {} , {} , {} , {} };
    private int[][] DICE_TO_ADJACENT_TOWNS_LUMBER = {{} , {} , {} , {} , {} , {19,24,25,30,31,36} , {} , {} , {16,21,22,27,28,33} , {} , {7,11,12,16,17,22} , {29,34,35,39,40,44} , {} };
    private int[][] DICE_TO_ADJACENT_TOWNS_GRAIN = {{} , {} , {} , {28,33,34,38,39,43} , {9,13,14,18,19,24} , {} , {} , {} , {} , {41,45,46,49,50,53} , {} , {} , {} };
    private int[][] DICE_TO_ADJACENT_TOWNS_BRICK = {{} , {} , {} , {} , {} , {} , {} , {} , {30,35,36,40,41,45} , {} , {} , {0,3,4,7,8,12} , {10,14,15,19,20,25} };
    private int[][] DICE_TO_ADJACENT_TOWNS_ORE = {{} , {} , {2,5,6,9,10,14} , {} , {} , {} , {39,43,44,47,48,51} , {} , {} , {18,23,24,29,30,35} , {1,4,5,8,9,13} , {} , {} };
    private PlayerManager playerManager;
    private BuildingManager buildingManager;
    private Map map;
    int playerNo;
    int lastAdded;
    int playerJoined;
    private int turnNo;
    private boolean firstTurn;
    private boolean secondTurn;
    private ArrayList<Card> initialDevelopmentCardStack = new ArrayList<Card>();
    //private ServerManager serverManager;

    private AnchorPane robber;
    private AnchorPane hex;
    private AnchorPane buildings;
    private AnchorPane roads;
    private int dice;

    public ClientGameManager(int port, String playerName, AnchorPane robberImage, AnchorPane hex, AnchorPane buildings, AnchorPane roads) throws URISyntaxException, IOException {
        super(port);
        this.robber = robber;
        this.hex = hex;
        this.buildings = buildings;
        this.roads = roads;
        this.playerManager = new PlayerManager("player1", " player2", "player3", "player4");
        playerNo = 0;
        lastAdded = 1;
        turnNo = 1;
        firstTurn = false;
        secondTurn = false;
        if ( playerName == null)
            playerName = "Player";
        sendNameToServer(playerName);
        initialDevelopmentCardStack = createInitialCardStack();
        buildingManager = new BuildingManager();
        map = new Map();
    }

    public void sendNameToServer(String playerName){
    	super.sendMessage("XCD" + playerName + "###");
    }

    public String returnPlayerColor(){
        return PLAYER_COLORS[playerNo];
    }

    public void createMap( Land[] toCopy) throws URISyntaxException {
        map = new Map( toCopy);
        synchronizeMap();
    }

    private void synchronizeMap() {
        DICE_TO_ADJACENT_TOWNS_BRICK = map.getDiceAdjacency( "Brick");
        DICE_TO_ADJACENT_TOWNS_GRAIN = map.getDiceAdjacency( "Grain");
        DICE_TO_ADJACENT_TOWNS_LUMBER = map.getDiceAdjacency( "Lumber");
        DICE_TO_ADJACENT_TOWNS_ORE = map.getDiceAdjacency( "Ore");
        DICE_TO_ADJACENT_TOWNS_WOOL = map.getDiceAdjacency( "Wool");
    }


    public void visualizeMap() throws URISyntaxException {
        map.visualizeMap();
    }

    public boolean changeRobberLocation( int newLoc)
    {
        return map.moveRobber( newLoc);
    }

    public int getRobbersLocation()
    {
        return map.getRobbersLocation();
    }

    private ArrayList<Card> createInitialCardStack() {
        ArrayList<Card> stack = new ArrayList<Card>();
        for(int i = 0 ; i < 14 ; i++){
            stack.add( new KnightCard());
        }
        for(int i = 0 ; i < 5 ; i++){
            stack.add( new VictoryPointCard());
        }
        for(int i = 0 ; i < 2 ; i++){
            stack.add( new RoadCard());
        }
        for(int i = 0 ; i < 2 ; i++){
            stack.add( new MonopolyCard());
        }
        for(int i = 0 ; i < 2 ; i++){
            stack.add( new YearOfPlentyCard());
        }
        Collections.shuffle(stack);
        return stack;
    }



    public boolean distributeResources( int dice)
    {
        if ( dice == 7 )
        {
            playerManager.robberSteals();
            // will change robbers location to selected pos
        }
        else if( dice <= 12 && dice > 1)
        {
            for(int i = 0 ; i < DICE_TO_ADJACENT_TOWNS_WOOL[dice].length ; i++){
                playerManager.distributeResources(DICE_TO_ADJACENT_TOWNS_WOOL[dice][i] , "Wool");
            }
            for(int i = 0 ; i < DICE_TO_ADJACENT_TOWNS_BRICK[dice].length ; i++){
                playerManager.distributeResources(DICE_TO_ADJACENT_TOWNS_BRICK[dice][i] , "Brick");
            }
            for(int i = 0 ; i < DICE_TO_ADJACENT_TOWNS_LUMBER[dice].length ; i++){
                playerManager.distributeResources(DICE_TO_ADJACENT_TOWNS_LUMBER[dice][i] , "Lumber");
            }
            for(int i = 0 ; i < DICE_TO_ADJACENT_TOWNS_GRAIN[dice].length ; i++){
                playerManager.distributeResources(DICE_TO_ADJACENT_TOWNS_GRAIN[dice][i] , "Grain");
            }
            for(int i = 0 ; i < DICE_TO_ADJACENT_TOWNS_ORE[dice].length ; i++){
                playerManager.distributeResources(DICE_TO_ADJACENT_TOWNS_ORE[dice][i] , "Ore");
            }
            return true;
        }
        return false;
    }

    private boolean moveRobber( int newLoc)
    {
        return map.moveRobber( newLoc);
    }

    public String gameOver()
    {
        return playerManager.getPlayers()[playerNo].getName();
    }

    public boolean tradeResource( String given , String wanted){
        return this.playerManager.tradeResource(playerNo , given , wanted);
    }

    public ArrayList<Card> getInitialDevelopmentCardStack() {
        return initialDevelopmentCardStack;
    }
    public boolean buyDevelopmentCard(){
        if(!initialDevelopmentCardStack.isEmpty()) {
            if (this.playerManager.buyDevelopmentCard(playerNo)) {
                this.playerManager.addProgressCard(playerNo , initialDevelopmentCardStack.get(0));
                initialDevelopmentCardStack.remove(0);
                return true;
            }
        }
        return false;
    }
    public void playMonopoly(String resourceType){
        this.playerManager.playMonopoly(resourceType , playerNo);
    }
    public void playYearOfPlenty(String resourceType){
        this.playerManager.playYearOfPlenty(resourceType , playerNo);
    }
    public void playRoadBuilding(){
        this.playerManager.playRoadBuilding(playerNo);
    }

    public boolean knightCardPlayable()
    {
        return playerManager.getPlayers()[playerNo].hasCard( "Knight", 1);
    }

    public void playKnightCard( int newLoc)
    {
        playerManager.playKnightCard( playerNo, buildingManager.getBuildingOwnersAt( map.getLandCornerLocation( newLoc)));
    }

    public boolean addRoad(int location) throws URISyntaxException {
        return true;
    }

    public boolean addSettlement(int location) throws URISyntaxException {
        if(buildingManager.buildSettlement(playerManager.getPlayers()[playerNo] , location))
        {
            buildingManager.setBuildingImage(returnPlayerColor(),location);
            return true;
        }
        else
            return false;
    }

    public boolean addCity(int location) throws URISyntaxException {
        if(buildingManager.buildCity(playerManager.getPlayers()[playerNo] , location))
        {
            buildingManager.setBuildingImage(returnPlayerColor(),location);
            return true;
        }
        else
            return false;
    }

    public boolean addCapital(int location) throws URISyntaxException {
        if(buildingManager.buildCapital(playerManager.getPlayers()[playerNo] , location))
        {
            buildingManager.setBuildingImage(returnPlayerColor(),location);
            return true;
        }
        else
            return false;
    }

    public String nextTurn(int turnDice){
        playerNo++;
        if(playerNo == 4) {
            playerNo = 0;
            turnNo++;
        }
        System.out.println("SIradakinin adi: " + playerManager.getName(playerNo));
        dice = turnDice;
        this.distributeResources(turnDice);
        if(Main.myName.equals(playerManager.getName(playerNo))) Main.myTurn = true;
        else    Main.myTurn = false;
        //Main.refreshClient();
        return playerManager.getPlayers()[playerNo].getName();
    }

    private void updateHappiness()
    {
        playerManager.fishing();
    }

    public void makeOffer( int receiverNo, String offeredItem,
                           String demandedItem, int offerNum, int demandNum)
    {
        playerManager.makeOffer( playerNo, receiverNo, offeredItem, demandedItem, offerNum, demandNum);
    }

    public ArrayList<Offer> listOffer()
    {
        return playerManager.listOffers( playerNo);
    }

    public boolean acceptOffer( Offer offer)
    {
        return playerManager.acceptOffer( offer);
    }

    public boolean declineOffer( Offer offer)
    {
        return playerManager.declineOffer( offer);
    }


    public int[] getResources(){
        return playerManager.getPlayers()[playerNo].getResources();
    }
    public int[] getDevelopmentCards(){
        return playerManager.getPlayers()[playerNo].getDevelopmentCards();
    }

    public int[] getScoreBoard(){
        return playerManager.scoreBoard(playerNo);
    }
    public int largestArmy(){
        return playerManager.largestArmy();
    }
    public int longestRoad(){
        return playerManager.whoHasLongestRoad();
    }

    public String getPlayerName() {
        return playerManager.getName(playerNo);
    }

    public int getTurnNo()
    {
        return turnNo;
    }

    @Override
    public void received(String message) throws URISyntaxException {
        System.out.println(message);
        String id = message.substring(0 , 2);
        if(id.equals("AA")){
            System.out.println("Burada");
            this.nextTurn(Integer.parseInt(message.substring(2)));
        }
        else if(id.equals("AB")){
            this.addRoad(Integer.parseInt(message.substring(2)));

        }
        else if(id.equals("AC")){
        	this.addSettlement(Integer.parseInt(message.substring(2)));
        }
        else if(id.equals("AD")){
        	this.addCity(Integer.parseInt(message.substring(2)));
        }
        else if(id.equals("AE")){

        }
        else if(id.equals("BA")){
        	this.playMonopoly(message.substring(2)); //String message for resourceType
        }
        else if(id.equals("BB")){
        	this.playYearOfPlenty(message.substring(2));  //String message for resourceType
        }
        else if(id.equals("BC")){
        	this.playRoadBuilding();
        }
        else if(id.equals("BD")){
        	this.playKnightCard(Integer.parseInt(message.substring(2)));
        }
        else if(id.equals("BE")){
        	this.distributeResources(Integer.parseInt(message.substring(2))); //Integer for diceNumber
        }
        else if(id.equals("CA")){
        	this.changeRobberLocation(Integer.parseInt(message.substring(2))); //Int for robberLocation
        }
        else if(id.equals("CB")){
        	this.buyDevelopmentCard();
        }
        else if(id.equals("CC")){
        	//this.chatMessage(message.substring(2));
        }
        else if(id.equals("CF")){
            int index = Integer.parseInt(message.substring(2,3));
            while(playerManager == null){

            }
            playerManager.changeIndexPlayerName(message.substring(3), index);
        }
      	else if(id.equals("CE")){
      	    while ( map == null)
            {

            }
            createMap( map.decodeMap( message.substring(2)));
      	    visualizeMap();
      	}
    }


    public int getTurnDice() {
        return dice;
    }

    public Player[] getPlayerArray() {
        return this.playerManager.getPlayers();
    }
}
