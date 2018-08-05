
import java.util.*;

import bwapi.*;
import bwta.*;


public class UnitControl_Hydralisk {
	
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
	ArrayList<Unit> Hydralisks = SM.myHydraliskList;

	public static boolean moveToEndPoint = false;
	public static boolean underAttack = true;

	public Unit getNextTargetOf(UnitType myUnitType, Position averagePosition) {
		Unit nextTarget = null;
		
		double targetHP = 10000;
		double tempHP = 0;		
		int inRange = 0;
		
		if(myUnitType == UnitType.Zerg_Hydralisk)
		{
			inRange = 5 * Config.TILE_SIZE;
		}
		else
		{
			inRange = 4 * Config.TILE_SIZE;
		}
		
		ArrayList <Unit> airToAir = new ArrayList<Unit>();
		ArrayList <Unit> groundToAir = new ArrayList<Unit>();
		ArrayList <Unit> airToGround = new ArrayList<Unit>();
		ArrayList <Unit> groundToGround = new ArrayList<Unit>();
		ArrayList <Unit> specialUnit = new ArrayList<Unit>();		
		ArrayList <Unit> enemyWorker = new ArrayList<Unit>();
		ArrayList <Unit> supply = new ArrayList<Unit>();
		ArrayList <Unit> defenseBuilding = new ArrayList<Unit>();
		ArrayList <Unit> normalBuilding = new ArrayList<Unit>();
		ArrayList <Unit> elseUnit = new ArrayList<Unit>();
		
		ArrayList <Unit> closeUnit = new ArrayList<Unit>();
		ArrayList <Unit> distantUnit = new ArrayList<Unit>();
		 
		 																			
		for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(averagePosition, inRange)) { // 저글링 같은 근접공격유닛은 10배 해봐야 무의미한가?

			if (enemy.getPlayer() == enemyPlayer) {
				
				if(enemy.isVisible() == false)
				{
					continue;
				}
				

				if(enemy.getType().equals(UnitType.Terran_Valkyrie)
						|| enemy.getType().equals(UnitType.Terran_Wraith)
						|| enemy.getType().equals(UnitType.Terran_Battlecruiser)
						|| enemy.getType().equals(UnitType.Zerg_Hydralisk)
						|| enemy.getType().equals(UnitType.Zerg_Devourer)
						|| enemy.getType().equals(UnitType.Zerg_Scourge)
						|| enemy.getType().equals(UnitType.Protoss_Scout)
						|| enemy.getType().equals(UnitType.Protoss_Corsair)
						|| enemy.getType().equals(UnitType.Protoss_Carrier)
						|| enemy.getType().equals(UnitType.Protoss_Interceptor)
						|| enemy.getType().equals(UnitType.Terran_Goliath)
						|| enemy.getType().equals(UnitType.Terran_Marine)
						|| enemy.getType().equals(UnitType.Zerg_Hydralisk)
						|| enemy.getType().equals(UnitType.Protoss_Dragoon)
						|| enemy.getType().equals(UnitType.Terran_Ghost)
						|| enemy.getType().equals(UnitType.Zerg_Guardian)
						|| enemy.getType().equals(UnitType.Protoss_Arbiter)
						|| enemy.getType().equals(UnitType.Terran_Vulture)
						|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Siege_Mode)
						|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Tank_Mode)
						|| enemy.getType().equals(UnitType.Zerg_Lurker)
						|| enemy.getType().equals(UnitType.Protoss_Reaver))
				{
					distantUnit.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Protoss_Archon)
						|| enemy.getType().equals(UnitType.Terran_Firebat)
						|| enemy.getType().equals(UnitType.Zerg_Zergling)
						|| enemy.getType().equals(UnitType.Zerg_Ultralisk)
						|| enemy.getType().equals(UnitType.Protoss_Zealot))

				{
					closeUnit.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Terran_Science_Vessel)
						|| enemy.getType().equals(UnitType.Zerg_Queen)
						|| enemy.getType().equals(UnitType.Zerg_Defiler)
						|| enemy.getType().equals(UnitType.Protoss_High_Templar)
						|| enemy.getType().equals(UnitType.Terran_Vulture_Spider_Mine))
				{
					specialUnit.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Terran_SCV)
						|| enemy.getType().equals(UnitType.Zerg_Drone)
						|| enemy.getType().equals(UnitType.Protoss_Probe))
				{
					enemyWorker.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Terran_Supply_Depot)
						|| enemy.getType().equals(UnitType.Terran_Command_Center)
						|| enemy.getType().equals(UnitType.Zerg_Overlord)
						|| enemy.getType().equals(UnitType.Zerg_Hatchery)
						|| enemy.getType().equals(UnitType.Zerg_Lair)
						|| enemy.getType().equals(UnitType.Zerg_Hive)
						|| enemy.getType().equals(UnitType.Protoss_Nexus)
						|| enemy.getType().equals(UnitType.Protoss_Pylon))
				{
					supply.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Terran_Missile_Turret)
						|| enemy.getType().equals(UnitType.Zerg_Spore_Colony)
						|| enemy.getType().equals(UnitType.Protoss_Photon_Cannon)
						|| enemy.getType().equals(UnitType.Terran_Bunker))
				{
					defenseBuilding.add(enemy);
					
				}			
				else if(enemy.getType().isBuilding() || enemy.getType().isAddon())
				{
					normalBuilding.add(enemy);				
				}
				else
				{
				//	 메딕, 드랍쉽 등등
					elseUnit.add(enemy);
				}

			}

		}
		
		
		for(Unit enemy : specialUnit)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		//////////////////////////////////

		
		for(Unit enemy : closeUnit)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		//////////////////////////////////
		
		for(Unit enemy : distantUnit)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		//////////////////////////////////
		
		for(Unit enemy : defenseBuilding)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		//////////////////////////////////
		
		
		for(Unit enemy : enemyWorker)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		//////////////////////////////////
		
		for(Unit enemy : supply)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		//////////////////////////////////

		

		
		
		for(Unit enemy : normalBuilding)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		//////////////////////////////////
		
		for(Unit enemy : elseUnit)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		//////////////////////////////////
		

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
		int numberOfMinimum = 12;

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
		} else if (myUnitType == SM.myHydralisk) {
			numberOfTotal = SM.myHydraliskList.size();
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
		
		if(SM.myHydraliskList.size() == 0)
		{
			return;
		}
		
		setStartGatherEndPoint();

		
		//System.out.println("HYDRA : " + SM.myHydraliskList.size());
		
		
		
		
		
		
		Position averagePosition = getAveragePosition(SM.myHydraliskList);
		boolean endGame = enoughGathered(UnitType.Zerg_Hydralisk, gatherPoint, 3, 0.5);
		Position invader = weAreUnderAttack(averagePosition);
		//Position nextPlace = getNextPlaceToGo();
		Unit nextTarget = getNextTargetOf(UnitType.Zerg_Hydralisk, averagePosition);

		

		
		
		
		


		for (Unit Hydralisk : SM.myHydraliskList) {
			
			if(Hydralisk.isIrradiated())
			{
				Hydralisk.move(SM.enemyMainBaseLocation.getPosition());
				continue;
			}
			
			if(Hydralisk.isUnderStorm())
			{
				Hydralisk.move(SM.myMainBaseLocation.getPosition());
				continue;
			}
			
			
			/*
			if(nextTarget != null && nextTarget.isStimmed())
			{
				int medic = 0;
				for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(nextTarget.getPosition(), 32*2))
				{
					if(unit.getType().equals(UnitType.Terran_Medic))
					{
						medic++;
					}
				}
				
				if(medic!=0)
				{
					Hydralisk.move(SM.myMainBaseLocation.getPosition());
			//		System.out.println("STP01");
					continue;
				}
				

			}
			*/
			
			/*
			if(invader != null && getNextTargetOf(UnitType.Zerg_Hydralisk, invader).isStimmed())
			{
				Hydral.move(SM.myMainBaseLocation.getPosition());
				System.out.println("STP02");
				continue;				
			}
			*/
			
			

			
			

			/*
			if (SM.myHydraliskList.size() <= 12) 
			{
				if (MyBotModule.Broodwar.getUnitsInRadius(startPoint, 4 * Config.TILE_SIZE)
						.contains(Hydralisk) == false) {
					Hydralisk.move(startPoint);
					System.out.println(0);
					continue;
				}
			}
			*/
			
			
			

			
			if (invader != null && Hydralisk.isAttacking()==false)
			{	
				nextTarget = getNextTargetOf(UnitType.Zerg_Hydralisk, invader);
			}
	
			
			
			
			
			if (nextTarget != null) 
			{
				if (Hydralisk.getGroundWeaponCooldown() == 0) 
				{
					//System.out.println(1);
					Hydralisk.attack(nextTarget);
					//continue; 컨티뉴를 하면 무조건 이 타겟을 치고 안하면 근처를 친다 왜냐하면 어택땅이 기본 상태라서 근접유닛은 안하는게 낫겠다 자꾸 병목현상되니까
				} 
				else if(Hydralisk.isUnderAttack())
				{
					//System.out.println(2);
					Hydralisk.move(SM.myMainBaseLocation.getPosition());
					continue;
				}

			}
			
			if(SM.enemyMainBaseLocation == null)
			{
				//System.out.println(3);
				commandUtil.attackMove(Hydralisk, startPoint);
			}
			
			
			
			
			
			

			
			//else if(myPlayer.completedUnitCount(UnitType.Zerg_Mutalisk) > 10 || myPlayer.completedUnitCount(UnitType.Zerg_Lurker)>4)// 공격나가는 시점
			else if(SM.isTimeToStartAttack() == true)// 공격나가는 시점		
			{

				if (endGame == false) 
				{
					//System.out.println(7);
					commandUtil.attackMove(Hydralisk, gatherPoint);
					moveToEndPoint = false;
				}
				else 
				{
					//System.out.println(6);
					commandUtil.attackMove(Hydralisk, endPoint);
				}

			}
			else if (SM.myHydraliskList.size() >18) 
			{
				
				
				defenseBalance(Hydralisk);
				
				
				
			}
			
			
			
			
			else
			{
				
				commandUtil.attackMove(Hydralisk, startPoint);
			}
		}
	}
	
	/*
	private static Hydralisk instance = new Hydralisk();

	/ static singleton 객체를 리턴합니다
	public static Hydralisk Instance() {
		return instance;
	}
	*/
	
	
	

	/*
	public Position rPoint(Position enemyUnit, Position myUnit)
	{
		Position rPoint = null;
		
		int enemyUnitX = enemyUnit.getX()/32;
		int enemyUnitY = enemyUnit.getY()/32;
		int myUnitX = myUnit.getX()/32;
		int myUnitY = myUnit.getY()/32;
		

		int a = 0;
		
		a = (enemyUnit.getY() - myUnit.getY() + 1) / (enemyUnit.getX() -  myUnit.getX() + 1);
		
		int dimension = 0;
		
		int deltaX = myUnit.getX() - enemyUnit.getX();
		int deltaY = myUnit.getY() - enemyUnit.getY();
		
		if(deltaX > 0 && deltaY > 0)
		{
			dimension = 1;
		}
		else if(deltaX < 0 && deltaY > 0)
		{
			dimension = 2;
		}
		else if (deltaX < 0 && deltaY < 0)
		{
			dimension = 3;
		}
		else
		{
			dimension = 4;
		}
		
		if(dimension == 1 || dimension == 4)
		{
			rPoint = new Position(myUnitX+3, a*(myUnitX+3));
		}
		else
		{
			rPoint = new Position(myUnitX-3, a*(myUnitX-3));
		}

		System.out.println(a);
		System.out.println(myUnitX);
		System.out.println(myUnitX+3 + " + " + (int)a*(myUnitX+3));
		
		return rPoint;
	}
	*/
	
}

