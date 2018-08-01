
import java.util.*;

import bwapi.*;
import bwta.*;


public class UnitControl_Mutalisk {
	
	

	
	
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



	StrategyManager.CombatState CombatStateNow = SM.combatState;
	ArrayList<Unit> mutalisks = SM.myMutaliskList;

	public static boolean moveToEndPoint = false;
	public static boolean underAttack = true;

	public Unit getNextTargetOf(UnitType myUnitType, Position averagePosition) {
		Unit nextTarget = null;

		enemyPlayer = SM.enemyPlayer;

		
		double targetHP = 10000;
		double tempHP = 0;
		
		int inRange = 0;
		
		if(myUnitType == UnitType.Zerg_Mutalisk)
		{
			inRange = 8 * Config.TILE_SIZE;
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
		 
		 																			
		for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(averagePosition, inRange)) { // 저글링 같은 근접공격유닛은 10배 해봐야 무의미한가?

			if (enemy.getPlayer() == enemyPlayer) {
				
				if(enemy.isVisible() == false)
				{
					continue;
				}
				

				if(enemy.getType().equals(UnitType.Terran_Valkyrie)
						|| enemy.getType().equals(UnitType.Terran_Wraith)
						|| enemy.getType().equals(UnitType.Terran_Battlecruiser)
						|| enemy.getType().equals(UnitType.Zerg_Mutalisk)
						|| enemy.getType().equals(UnitType.Zerg_Devourer)
						|| enemy.getType().equals(UnitType.Zerg_Scourge)
						|| enemy.getType().equals(UnitType.Protoss_Scout)
						|| enemy.getType().equals(UnitType.Protoss_Corsair)
						|| enemy.getType().equals(UnitType.Protoss_Carrier)
						|| enemy.getType().equals(UnitType.Protoss_Interceptor))
				{
					airToAir.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Terran_Ghost) // 공중방어 건물도 지대공으로 넣어봤다 180718
						|| enemy.getType().equals(UnitType.Terran_Goliath)
						|| enemy.getType().equals(UnitType.Terran_Marine)
						|| enemy.getType().equals(UnitType.Zerg_Hydralisk)
						|| enemy.getType().equals(UnitType.Protoss_Dragoon)
						|| enemy.getType().equals(UnitType.Protoss_Archon)
						|| enemy.getType().equals(UnitType.Terran_Missile_Turret)
						|| enemy.getType().equals(UnitType.Zerg_Spore_Colony)
						|| enemy.getType().equals(UnitType.Protoss_Photon_Cannon))
				{
					groundToAir.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Zerg_Guardian)
						|| enemy.getType().equals(UnitType.Protoss_Arbiter))
				{
					airToGround.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Terran_Firebat)
						|| enemy.getType().equals(UnitType.Terran_Vulture)
						|| enemy.getType().equals(UnitType.Terran_Vulture_Spider_Mine)
						|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Siege_Mode)
						|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Tank_Mode)
						|| enemy.getType().equals(UnitType.Zerg_Zergling)
						|| enemy.getType().equals(UnitType.Zerg_Lurker)
						|| enemy.getType().equals(UnitType.Zerg_Ultralisk)
						|| enemy.getType().equals(UnitType.Protoss_Zealot)
						|| enemy.getType().equals(UnitType.Protoss_Reaver))
				{
					groundToGround.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Terran_Science_Vessel)
						|| enemy.getType().equals(UnitType.Zerg_Queen)
						|| enemy.getType().equals(UnitType.Zerg_Defiler)
						|| enemy.getType().equals(UnitType.Protoss_High_Templar))
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
						|| enemy.getType().equals(UnitType.Protoss_Photon_Cannon))
//						|| enemy.getType().equals(UnitType.Terran_Bunker))
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

		
		/*
		for(Unit enemy : enemyWorker)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{
			 if(nextTarget.isRepairing())
			 {return nextTarget;}
		}
		*/	
		
		
		

		
		for(Unit enemy : airToAir)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		
		for(Unit enemy : groundToAir)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		
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
		
		
		for(Unit enemy : airToGround)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		
		for(Unit enemy : groundToGround)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		
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
		

		return nextTarget;
	}
	
	
	public Position weAreUnderAttack(Position myPosition) {
		// 이것이 결국  SM의 isTimeToDefense를 대체하면서 별도의 클래스로 만들어지던가 해야한다
		Unit invader = null;
		enemyPlayer = SM.enemyPlayer;

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

		/*
		if (invader != null) {
			underAttack = true;
			moveToEndPoint = false;
			return invader.getPosition();
		}
		*/

		// 기지 주변에 악당이 등장하는 경우
		for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(myPlayer)) {

			for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(baseLocation.getPosition(), 13 * Config.TILE_SIZE)) {

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
		int numberOfMinimum = 9;

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
			
			for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 7 * Config.TILE_SIZE)) 
			{
				
				
				if (enemy.getPlayer() == enemyPlayer) {
					tempEnemy = enemy;
					//System.out.println("include");
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
		
		MyBotModule.Broodwar.drawCircleMap(averagePosition, 7 * Config.TILE_SIZE, Color.Red);
		
		return averagePosition;
	}
	
	
	
	
	
	public Position getNextPlaceToGo() {
		
		if(SM.enemyMainBaseLocation==null)
		{
			return SM.mySecondChokePoint.getPoint();
		}
		
		
		Position nextPlace = bwta.BWTA.getNearestChokepoint(SM.mySecondChokePoint.getCenter()).getCenter();

		if (enemyMainBaseLocation != null) {
			int x, y;

			x = enemyMainBaseLocation.getX();
			y = enemyMainBaseLocation.getY();

			if (x < 63 * 32) {
				x = 0;
			} else {
				x = 127 * 32;
			}

			if (y < 63 * 32) {
				y = 0;
			} else {
				y = 127 * 32;
			}

			endPoint = new Position(x, y);

			int a = 63 * 32;
			int b = 63 * 32;


			a = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX()) / 2;
			b = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY()) / 2;

			if (50 * 32 < a && a < 70 * 32) {

				if (enemyMainBaseLocation.getX() < myMainBaseLocation.getX()) {
					a = myMainBaseLocation.getX() / 2;
				} else {
					a = enemyMainBaseLocation.getX() / 2;
				}
			}

			if (a / 32 == 59)//  대각이거나 가로로 나란하거나
			{
				if (enemyMainBaseLocation.getX() == myMainBaseLocation.getX()) {
					;
				} else {
					b = enemyMainBaseLocation.getY();
				}
			} else // 세로로 나란히
			{
				;
			}
			
			gatherPoint = new Position(a, b);
		}

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
			return;
		}
		
		
		
		startPoint = SM.mySecondChokePoint.getPoint();
		endPoint = SM.enemyMainBaseLocation.getPoint();		
		// 몇시 방향이냐가 아니고 4분면에서 몇사분면인가를 판단
		int enemyMainBasePosition = 0;
		
		if(SM.enemyMainBaseLocation.getX()/32 > 63 && SM.enemyMainBaseLocation.getY()/32 > 63)
		{
			enemyMainBasePosition = 4;
		}
		else if(SM.enemyMainBaseLocation.getX()/32 < 63 && SM.enemyMainBaseLocation.getY()/32 > 63)
		{
			enemyMainBasePosition = 3;
		}
		else if(SM.enemyMainBaseLocation.getX()/32 < 63 && SM.enemyMainBaseLocation.getY()/32 < 63)
		{
			enemyMainBasePosition = 2;
		}
		else
		{
			enemyMainBasePosition = 1;
		}
		
		
		
		if(MyBotModule.Broodwar.mapFileName().equals("(4)CircuitBreaker.scx"))
		{
			if(enemyMainBasePosition == 1)
			{
				gatherPoint = new Position(42*32,3*32);
			}
			else if(enemyMainBasePosition == 2)
			{
				gatherPoint = new Position(85*32,3*32);
			}
			else if(enemyMainBasePosition == 3)
			{
				gatherPoint = new Position(85*32,123*32);
			}
			else
			{
				gatherPoint = new Position(42*32,123*32);
			}	
		}
		else
		{
			if(enemyMainBasePosition == 1)
			{
				gatherPoint = new Position(123*32,73*32);
			}
			else if(enemyMainBasePosition == 2)
			{
				gatherPoint = new Position(73*32,3*32);
			}
			else if(enemyMainBasePosition == 3)
			{
				gatherPoint = new Position(3*32,53*32);
			}
			else
			{
				gatherPoint = new Position(55*32,123*32);
			}	
			
			
		}
		
		
		
		
	}

	
	
	
	
	public void update() {
		
		if(SM.myMutaliskList.size() == 0)
		{
			return;
		}
		
		 setStartGatherEndPoint();
		
		
		
		
		
		
		Position averagePosition = getAveragePosition(SM.myMutaliskList);
		boolean endGame = enoughGathered(UnitType.Zerg_Mutalisk, gatherPoint, 5, 0.5);
		Position invader = weAreUnderAttack(averagePosition);
		//Position nextPlace = getNextPlaceToGo();
		Unit nextTarget = getNextTargetOf(UnitType.Zerg_Mutalisk, averagePosition);

		

	

		for (Unit Mutalisk : SM.myMutaliskList) 
		{
			

			
			
			if(Mutalisk.isIrradiated())
			{
				Mutalisk.move(SM.enemyMainBaseLocation.getPosition());
				continue;
			}
			
			if(Mutalisk.isUnderStorm())
			{
				Mutalisk.move(SM.myMainBaseLocation.getPosition());
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
					mutal.move(SM.myMainBaseLocation.getPosition());
			//		System.out.println("STP01");
					continue;
				}
				

			}
			
			
			if(invader != null && getNextTargetOf(UnitType.Zerg_Mutalisk, invader).isStimmed())
			{
				mutal.move(SM.myMainBaseLocation.getPosition());
				System.out.println("STP02");
				continue;				
			}
			*/
			
			


			if (invader != null && Mutalisk.isAttacking()==false) 
			{	
				nextTarget = getNextTargetOf(UnitType.Zerg_Mutalisk, invader);
			}
	
			
		
			
			
			
			if (nextTarget != null) 
			{
				if (Mutalisk.getAirWeaponCooldown() == 0) 
				{
					System.out.println(1);
					Mutalisk.attack(nextTarget);
					continue; //컨티뉴를 하면 무조건 이 타겟을 치고 안하면 근처를 친다 왜냐하면 어택땅이 기본 상태라서 근접유닛은 안하는게 낫겠다 자꾸 병목현상되니까
				} 
				else if(Mutalisk.getAirWeaponCooldown() > 10)
				{
					System.out.println(2);
					Mutalisk.move(SM.myMainBaseLocation.getPosition());
					continue;
				}

			}
			
			if(SM.enemyMainBaseLocation == null)
			{
				System.out.println(3);
				commandUtil.attackMove(Mutalisk, startPoint);
			}
			else if (SM.myMutaliskList.size() <= 6) 
			{
				moveToEndPoint = false;
				
				if (MyBotModule.Broodwar.getUnitsInRadius(startPoint, 4 * Config.TILE_SIZE)
						.contains(Mutalisk) == false) {
					//Mutalisk.move(startPoint);
					commandUtil.attackMove(Mutalisk, startPoint);
					System.out.println(4);
					continue;
				}
				else
				{
					System.out.println(5);
					commandUtil.attackMove(Mutalisk, startPoint);
				}
				
			}
			else 
			{

				if (endGame == false) 
				{
					//System.out.println(7);
					commandUtil.attackMove(Mutalisk, gatherPoint);
					moveToEndPoint = false;
				}
				else 
				{
					//System.out.println(6);
					commandUtil.attackMove(Mutalisk, endPoint);
				}

			}
		}
	

		

	}

	
	
	
	
	
	

	
	/*
	private static Mutalisk instance = new Mutalisk();

	/ static singleton 객체를 리턴합니다
	public static Mutalisk Instance() {
		return instance;
	}
	*/
	

}
