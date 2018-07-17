
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
			inRange =myUnitType.seekRange() * 5;
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
			 if(nextTarget.isRepairing()||nextTarget.isConstructing())
			 {return nextTarget;}
		}
				
		
		
		

		
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
					|| (unit.getType().equals(UnitType.Zerg_Drone) && unit.isUnderAttack())) {
				Unit tempEnemy = getNextTargetOf(unit.getType(), unit.getPosition());//  반경 4개 타일안의 적을 찾을 것이다.

				if (tempEnemy != null) {
					tempDistance = tempEnemy.getPosition().getDistance(myPosition);
					if (positionDistance > tempDistance) {
						positionDistance = tempDistance;
						invader = tempEnemy;
					}
				}
			}
		}

		if (invader != null) {
			underAttack = true;
			moveToEndPoint = false;
			return invader.getPosition();
		}

		// 기지 주변에 악당이 등장하는 경우
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
/*
		// 길목 주변에 악당이 등장하는 경우
		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(SM.mySecondChokePoint.getPoint(), 8 * Config.TILE_SIZE)) {
			if (unit.getPlayer() == enemyPlayer) {

				tempDistance = unit.getPosition().getDistance(myPosition);
				if (positionDistance > tempDistance) {
					positionDistance = tempDistance;
					invader = unit;
					  // System.out.println("길목2 주변 악당을 찾았다");
				}
			}
		}

		if (invader != null) {
			underAttack = true;
			moveToEndPoint = false;
			return invader;
		}
		
		// 길목 주변에 악당이 등장하는 경우
		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(SM.myFirstChokePoint.getPoint(), 8 * Config.TILE_SIZE)) {
			if (unit.getPlayer() == enemyPlayer) {

				tempDistance = unit.getPosition().getDistance(myPosition);
				if (positionDistance > tempDistance) {
					positionDistance = tempDistance;
					invader = unit;
					  // System.out.println("길목1 주변 악당을 찾았다");
				}
			}
		}

		if (invader != null) {
			underAttack = true;
			moveToEndPoint = false;
			return invader;
		}
*/		
		underAttack = false;
		return null;
	}
	
	

	public boolean enoughGathered(UnitType myUnitType, Position targetPosition, int radius, double input_ratio) {

		int numberOfGathered = 0;
		int numberOfTotal = 0;
		int numberOfMinimum = 5;

		if (targetPosition == null) {
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
		} else {

		}

		List<Unit> unitsAround = MyBotModule.Broodwar.getUnitsInRadius(targetPosition, radius * Config.TILE_SIZE);

		 // System.out.println("unitsAround : " + unitsAround.size());
		 // System.out.println(targetPosition.getX());
		 // System.out.println(targetPosition.getY());
		
		
		
		for (Unit unit : unitsAround) {
			 // System.out.println("aaaaa");

			if (unit.exists() && unit != null) {
 // System.out.println("bbbbbbbb");
				if (unit.getPlayer() == myPlayer) {
					 // System.out.println("ccccc");
					if (unit.getType() == myUnitType) {
						 // System.out.println("dddd");
						numberOfGathered++;
					}
				}
			}
		}

		
		 // System.out.println("numberOfGathered : " + numberOfGathered);
		 // System.out.println("numberOfTotal * input_ratio : " + numberOfTotal * input_ratio);
		
		if (numberOfGathered > numberOfMinimum && numberOfGathered > numberOfTotal * input_ratio) {
			moveToEndPoint = true;
			 // System.out.println("-----------------");
		}

		return moveToEndPoint;
	}
	
	
	
	public Position getAveragePosition(List <Unit> Units)
	{
		Position averagePosition = null;
		
		int x = 0;
		int y = 0;
		
		for(Unit unit : Units)
		{
			x += unit.getX();
			y += unit.getY();
		}
		
		x = x/Units.size();
		y = y/Units.size();
		
		averagePosition = new Position(x,y);
		
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

	
	
	
	
	public void update() {
		
		if(SM.myMutaliskList.size() == 0)
		{
			return;
		}
		
		

		
		
		
		
		
		
		Position averagePosition = getAveragePosition(SM.myMutaliskList);
		enoughGathered(UnitType.Zerg_Mutalisk, gatherPoint, 3, 0.5);
		Position invader = weAreUnderAttack(averagePosition);
		Position nextPlace = getNextPlaceToGo();
		Unit nextTarget = getNextTargetOf(UnitType.Zerg_Mutalisk, averagePosition);
		int timer = 0;
		

		
		
		
		
		ArrayList<Unit> mutalsAroundNextPlace = new ArrayList<Unit>();
		if (SM.enemyMainBaseLocation != null) {
			List<Unit> unitsAroundNextPlace = MyBotModule.Broodwar
					.getUnitsInRadius(SM.enemyMainBaseLocation.getPosition(), 8 * Config.TILE_SIZE);

			for (Unit unit : unitsAroundNextPlace) {
				if (unit.getPlayer() == SM.myPlayer && unit.getType() == UnitType.Zerg_Mutalisk) {
					mutalsAroundNextPlace.add(unit);
				}
			}
		}

		for (Unit mutal : SM.myMutaliskList) {
			

			if(mutal.isIrradiated())
			{
				mutal.move(SM.enemyMainBaseLocation.getPosition());
				continue;
			}
			
			if(mutal.isUnderStorm())
			{
				mutal.move(SM.myMainBaseLocation.getPosition());
			}
			
			
			
			
			timer = (int) (mutal.getAirWeaponCooldown() / 1.5);

			
			 // System.out.println("underAttack : " + underAttack);
			 // System.out.println("moveToEndPoint : " + moveToEndPoint);
			

			if (invader != null) {
				
				nextTarget = getNextTargetOf(UnitType.Zerg_Mutalisk, invader);
				
				
				if (timer == 0) {
					mutal.attack(nextTarget);
				} else {
					mutal.move(SM.myMainBaseLocation.getPosition());
				}
				
				
				  // System.out.println("11111");
				  // System.out.println("집지키러 가즈아");
			} else if (SM.enemyMainBaseLocation == null) {
				commandUtil.attackMove(mutal, SM.mySecondChokePoint.getPoint());
				moveToEndPoint = false;
				  // System.out.println("22222");
				  // System.out.println("아직 기지 못찾아서 앞마당으로 가라");

			} else if (SM.combatState == StrategyManager.CombatState.attackStarted) {
				if (nextTarget != null) {
					if (timer == 0) {
						mutal.attack(nextTarget);
					} else {
						mutal.move(SM.myMainBaseLocation.getPosition());
					}

				} else {
					commandUtil.attackMove(mutal, SM.enemyMainBaseLocation.getPosition());
				}
				  // System.out.println("33333");
			}

			else if (nextTarget != null && invader == null && mutalsAroundNextPlace.size() > 3) {

				if (timer == 0) {
					mutal.attack(nextTarget);
				} else {
					mutal.move(gatherPoint);
				}

				  // System.out.println("44444");
				  // System.out.println("잡아먹기");

			} else if (mutalsAroundNextPlace.size() < 3 && SM.myMutaliskList.size() < 5) {

				mutal.move(SM.myFirstExpansionLocation.getPoint());
				moveToEndPoint = false;
				  // System.out.println("55555");
			}

			else if (SM.myMutaliskList.size() < 4 + SM.myZerglingList.size() / 4) // 6에서 점점 늘려보자는 취지
			{
				if (nextTarget != null) {
					if (timer == 0) {
						mutal.attack(nextTarget);
					} else {
						mutal.move(SM.myMainBaseLocation.getPosition());

					}

				} else {
					commandUtil.attackMove(mutal, SM.mySecondChokePoint.getPoint());
				}

				moveToEndPoint = false;
				  // System.out.println("66666");

			}

			else {
				if (nextTarget != null) {
					if (timer == 0) {
						mutal.attack(nextTarget);
					} else {
						mutal.move(SM.myMainBaseLocation.getPosition());
					}

				} else {
					commandUtil.attackMove(mutal, nextPlace);
				}

				  // System.out.println("어택땅으로 이동");
				  // System.out.println("7777");
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
