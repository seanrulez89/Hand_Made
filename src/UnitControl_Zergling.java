
import java.util.*;

import bwapi.*;
import bwta.*;


public class UnitControl_Zergling {

	
	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	
	
	Player myPlayer = SM.myPlayer;
	Race myRace = SM.myRace;

	Player enemyPlayer = SM.enemyPlayer;
	Race enemyRace = SM.enemyRace;

	BaseLocation myMainBaseLocation = SM.myMainBaseLocation;
	BaseLocation enemyMainBaseLocation = SM.enemyMainBaseLocation;
	
	static Position startPoint = null;
	static Position gatherPoint = null;
	static Position endPoint = null;
	
	int balanceIndex = 0;



	StrategyManager.CombatState CombatStateNow = SM.combatState;
	ArrayList<Unit> Zerglings = SM.myZerglingList;

	public static boolean moveToEndPoint = false;
	public static boolean underAttack = true;

	public Unit getNextTargetOf(UnitType myUnitType, Position averagePosition) {
		Unit nextTarget = null;
		
		double targetHP = 10000;
		double tempHP = 0;		
		int inRange = 0;
		
		if(myUnitType == UnitType.Zerg_Zergling)
		{
			inRange = 5 * Config.TILE_SIZE;
		}
		else
		{
			inRange = 4 * Config.TILE_SIZE;
		}
		
		ArrayList <Unit> mustKillFirst = new ArrayList<Unit>();		 
		 																			
		for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(averagePosition, inRange)) {

			if (enemy.getPlayer() == enemyPlayer) {
				
				if(enemy.isVisible() == false)
				{
					continue;
				}
				
				if(enemy.isFlying() == true)
				{
					continue;
				}

				if(enemy.getType().equals(UnitType.Terran_Siege_Tank_Siege_Mode)
						|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Tank_Mode)
						|| enemy.getType().equals(UnitType.Zerg_Defiler)
						|| enemy.getType().equals(UnitType.Protoss_High_Templar)
						|| enemy.getType().equals(UnitType.Terran_Missile_Turret)
						|| enemy.getType().equals(UnitType.Zerg_Spore_Colony)
						|| enemy.getType().equals(UnitType.Protoss_Photon_Cannon)
						|| enemy.getType().equals(UnitType.Terran_Bunker)
						|| enemy.getType().equals(UnitType.Terran_Medic))
				{
					mustKillFirst.add(enemy);					
				}
				

			}

		}
		
		
		for(Unit enemy : mustKillFirst)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		
		

		return nextTarget;
	}
	
	
	public Position weAreUnderAttack(Position myPosition) {
		// 이것이 결국  SM의 isTimeToDefense를 대체하면서 별도의 클래스로 만들어지던가 해야한다
		Unit invader = null;

		double positionDistance = 100000000;
		double tempDistance = 0;
	
		// 건물이나 드론이 얻어맞는 경우
		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if ((unit.getType().isBuilding() && unit.isUnderAttack())
					|| (unit.getType().equals(UnitType.Zerg_Drone) && unit.isUnderAttack())
					&& (unit.getType() != UnitType.Zerg_Sunken_Colony)) {
				
				tempDistance = unit.getPosition().getDistance(myPosition);
				if (positionDistance > tempDistance) {
					positionDistance = tempDistance;
					invader = unit;
				}	
			}
		}

		if (invader != null) {
			underAttack = true;
			moveToEndPoint = false;
			return invader.getPosition();
		}

		// 기지 주변에 악당이 등장하는 경우. 꼭 맞고 있지는 않을 수도 있다
		for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(myPlayer)) {

			for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(baseLocation.getPosition(), 8 * Config.TILE_SIZE)) {

				if (unit.getPlayer() == enemyPlayer) {

					tempDistance = unit.getPosition().getDistance(myPosition);
					if (positionDistance > tempDistance) {
						positionDistance = tempDistance;
						invader = unit;
						  // System.out.println("기지 주변 악당을 찾았다");
					}
				}
			}
		}

		if (invader != null) {
			underAttack = true;
			moveToEndPoint = false;
			return invader.getPosition();
		}

		underAttack = false;
		return null;
	}
	
	

	public boolean enoughGathered(UnitType myUnitType, Position targetPosition, int radius, double input_ratio) {

		int numberOfGathered = 0;
		int numberOfTotal = 0;
		int numberOfMinimum = 0;

		if (SM.enemyMainBaseLocation == null) {
			 // System.out.println("아직 기지 못 찾음");
			return false;
		}

		
		if (moveToEndPoint == true) {
			return moveToEndPoint;
		}
		
		
		if (myUnitType == SM.myZergling) {
			numberOfTotal = SM.myZerglingList.size();
		} else if (myUnitType == SM.myMutalisk) {
			numberOfTotal = SM.myMutaliskList.size();
		} else if (myUnitType == SM.myZergling) {
			numberOfTotal = SM.myZerglingList.size();
		} else {

		}

		List<Unit> unitsAround = MyBotModule.Broodwar.getUnitsInRadius(targetPosition, radius * Config.TILE_SIZE);
		
		
		
		for (Unit unit : unitsAround) {

			if (unit.exists() && unit != null) {

				if (unit.getPlayer() == myPlayer) {

					if (unit.getType() == myUnitType) {
						numberOfGathered++;
					}
				}
			}
		}
		
		if (numberOfGathered > numberOfMinimum && numberOfGathered > numberOfTotal * input_ratio) {
			moveToEndPoint = true;
		}
		else
		{
			moveToEndPoint = false;
		}

		return moveToEndPoint;
	}
	
	
	
	public Position getAveragePosition(List <Unit> Units)
	{
		Position averagePosition = null;
		
		int x = 0;
		int y = 0;
		int includeCase = 0;
		
		for(Unit unit : Units)
		{
			Unit tempEnemy = null;
			
			for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 5 * Config.TILE_SIZE)) 
			{
				if (enemy.getPlayer() == enemyPlayer) {
					tempEnemy = enemy;
					includeCase++;
					break;
				}
				
			}
			if(tempEnemy!=null)
			{
				x += unit.getX();
				y += unit.getY();
			}

		}
		
		if(includeCase==0)
		{
			return SM.myFirstExpansionLocation.getPosition();
		}
		
		
		
		x = x/includeCase;
		y = y/includeCase;
		
		averagePosition = new Position(x,y);
		
		MyBotModule.Broodwar.drawCircleMap(averagePosition, 5 * Config.TILE_SIZE, Color.Red);
		
		return averagePosition;
	}
	
	
	
	
	
	public Position getNextPlaceToGo() {
		
		if(SM.enemyMainBaseLocation==null)
		{
			return SM.mySecondChokePoint.getPoint();
		}
		
		
		Position nextPlace = bwta.BWTA.getNearestChokepoint(SM.mySecondChokePoint.getCenter()).getCenter();
		

		if (endPoint != null && moveToEndPoint == true && underAttack == false
				&& SM.combatState == StrategyManager.CombatState.defenseMode) {
			nextPlace = endPoint;
		} else if (gatherPoint != null) {
			nextPlace = gatherPoint;
		} else {
			
		}

		return nextPlace;

	}

	
	public void setStartGatherEndPoint ()
	{
		

		if(SM.enemyMainBaseLocation==null)
		{
			startPoint = SM.myFirstChokePoint.getPoint();
			gatherPoint = SM.mySecondChokePoint.getPoint();
			endPoint = SM.mySecondChokePoint.getPoint();
		}
		else
		{
			startPoint = SM.mySecondChokePoint.getPoint();
			gatherPoint = SM.enemySecondChokePoint.getPoint();
			//gatherPoint = new Position(63*32, 63*32);
			endPoint = SM.enemyMainBaseLocation.getPoint();
		}
		
		
		
		
	}
	
	public void defenseBalance(Unit unit) {
		
		List<BaseLocation> myBaseLocations = InformationManager.Instance()
				.getOccupiedBaseLocations(InformationManager.Instance().selfPlayer);
		
		ArrayList<Position> defenseSite = new ArrayList<Position>();
		
		Iterator <BaseLocation> lir = myBaseLocations.iterator();		
		while(lir.hasNext())
		{

			
			Position position = lir.next().getPosition();
			
			defenseSite.add(position);
			
			Position chokePoint = bwta.BWTA.getNearestChokepoint(position).getCenter();
			defenseSite.add(chokePoint);
			
		}
		
		
		Iterator <Position> iter = defenseSite.iterator();	
		while(iter.hasNext())
		{
			Position position = iter.next();
			
			if(position.equals(SM.myFirstChokePoint.getPoint()))
			{
				iter.remove();
			}
			else if(position.equals(SM.myFirstExpansionLocation.getPosition()))
			{
				iter.remove();
			}
		}

		
		
		defenseSite.add(SM.mySecondChokePoint.getPoint());	
		
		
		
		int num = defenseSite.size();
		

		if(balanceIndex>=num)
		{
			balanceIndex=0;
		}
		commandUtil.attackMove(unit, defenseSite.get(balanceIndex));				
		balanceIndex++;
		
	
	}
	

	
	
	public void update() {
		
		if(SM.myZerglingList.size() == 0)
		{
			return;
		}
		
		setStartGatherEndPoint();

		
		//System.out.println("HYDRA : " + SM.myZerglingList.size());
		
		
		
		
		
		
		//Position averagePosition = getAveragePosition(SM.myZerglingList);
		boolean endGame = enoughGathered(UnitType.Zerg_Zergling, gatherPoint, 3, 0.5);
		//Position invader = weAreUnderAttack(averagePosition);
		//Position nextPlace = getNextPlaceToGo();
		//Unit nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, averagePosition);

		

		
		
		
		


		for (Unit Zergling : SM.myZerglingList) {
			
			//System.out.println(Zergling.getID());
			
			if(Zergling.isIrradiated())
			{
				Zergling.move(SM.enemyMainBaseLocation.getPosition());
				continue;
			}
			
			if(Zergling.isUnderStorm())
			{
				Zergling.move(SM.myMainBaseLocation.getPosition());
				continue;
			}
			
			//Position averagePosition = getAveragePosition(SM.myZerglingList);
			//boolean endGame = enoughGathered(UnitType.Zerg_Zergling, gatherPoint, 3, 0.5);
			Position invader = weAreUnderAttack(Zergling.getPosition());
			//Position nextPlace = getNextPlaceToGo();
			Unit nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());
			
			
			
			

			
			if (invader != null && Zergling.isAttacking()==false)
			{
				
				
				nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, invader);
				
			
			}
	
			
			
			
			
			if (nextTarget != null) 
			{
				//System.out.println(1);
					Zergling.attack(nextTarget);
					continue; // 컨티뉴를 하면 무조건 이 타겟을 치고 안하면 근처를 친다 왜냐하면 어택땅이 기본 상태라서 근접유닛은 안하는게 낫겠다 자꾸 병목현상되니까
			}
			
			
			if(SM.enemyMainBaseLocation == null)
			{
				//System.out.println(3);
				commandUtil.attackMove(Zergling, startPoint);
			}
			else if(SM.isTimeToStartAttack() == true)// 공격나가는 시점		
			{

				if (endGame == false) 
				{
					//System.out.println(7);
					commandUtil.attackMove(Zergling, gatherPoint);
					moveToEndPoint = false;
				}
				else 
				{
					//System.out.println(6);
					commandUtil.attackMove(Zergling, endPoint);
				}

			}
			else if (SM.myZerglingList.size() >12) 
			{
				//System.out.println(9);
				defenseBalance(Zergling);
			}
			else
			{	
				//System.out.println(10);
				commandUtil.attackMove(Zergling, startPoint);
			}
		}
	}	
}
