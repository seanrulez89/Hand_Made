
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
	

	
	static int balanceIndex = 0;
	static int gatherIndex = UnitControl_COMMON.moveIndex - 1;
	static boolean goOut = false;




	public Unit getNextTargetOf(UnitType myUnitType, Position averagePosition) {
		Unit nextTarget = null;
		
		double targetHP = 10000;
		double tempHP = 0;		
		

		
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
		 
		 																			
		for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(averagePosition, myUnitType.groundWeapon().maxRange())) { // 저글링 같은 근접공격유닛은 10배 해봐야 무의미한가?

			if (enemy.getPlayer() == enemyPlayer) {
				
				if(enemy.isVisible() == false)
				{
					continue;
				}
				
				if(enemy.isDefenseMatrixed()==true)
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
						|| enemy.getType().equals(UnitType.Protoss_Zealot)
						|| enemy.getType().equals(UnitType.Terran_Medic))

				{
					closeUnit.add(enemy);					
				}
				else if(enemy.getType().equals(UnitType.Terran_Science_Vessel)
						|| enemy.getType().equals(UnitType.Zerg_Queen)
						|| enemy.getType().equals(UnitType.Zerg_Defiler)
						|| enemy.getType().equals(UnitType.Protoss_High_Templar)
						|| enemy.getType().equals(UnitType.Terran_Vulture_Spider_Mine)
						)
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
	
	
	public static Position setFleePoint (Position averagePosition)
	{
		Position position = StrategyManager.Instance().myMainBaseLocation.getPosition();
		
		int currentEnemy = 0;
		int minEnemy = 100000;

		
		for(int i = -1 ; i < 2 ; i++)
		{
			for(int j = -1 ; j < 2 ; j++)
			{
				Position tempPosition = new Position(averagePosition.getX()+4*Config.TILE_SIZE*i+16*i, averagePosition.getY()+4*Config.TILE_SIZE*j+16*j);
				
				MyBotModule.Broodwar.drawCircleMap(tempPosition, 3 * Config.TILE_SIZE, Color.Red);

				currentEnemy = 0;
				for(Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(tempPosition, 3 * Config.TILE_SIZE))				
				{
					if(enemy.getPlayer() == StrategyManager.Instance().enemyPlayer) // 벙커와 터렛과 기타등등 모두 포함해야함 canattack이 좀 이상한거 같아
					{
						if(isAttackUnit(enemy))
						{
							//System.out.println(enemy.getType().toString());
							
							currentEnemy = currentEnemy + enemy.getHitPoints() + enemy.getShields();
							
							//currentEnemy++;
						}
					}
				}
				
				//System.out.println("currentEnemy : " + currentEnemy);
				
				int x = tempPosition.getX()/32;
				int y = tempPosition.getY()/32;
				
				
				if(currentEnemy<minEnemy && x > 0 && x < 128 && y > 0  && y < 128)
				{
					minEnemy = currentEnemy;
					position = tempPosition;
				}			
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		return position;
	}
	

	public static boolean isAttackUnit(Unit enemy)
	{
		boolean isAttackUnit = false;
		
		if(enemy.getType().equals(UnitType.Zerg_Zergling)
				|| enemy.getType().equals(UnitType.Zerg_Hydralisk)
				|| enemy.getType().equals(UnitType.Zerg_Ultralisk)
				|| enemy.getType().equals(UnitType.Zerg_Mutalisk)
				|| enemy.getType().equals(UnitType.Zerg_Guardian)
				|| enemy.getType().equals(UnitType.Zerg_Broodling)
				|| enemy.getType().equals(UnitType.Zerg_Infested_Terran)
				|| enemy.getType().equals(UnitType.Zerg_Sunken_Colony)
				|| enemy.getType().equals(UnitType.Protoss_Zealot)
				|| enemy.getType().equals(UnitType.Protoss_Dragoon)
				|| enemy.getType().equals(UnitType.Protoss_Dark_Templar)
				|| enemy.getType().equals(UnitType.Protoss_Archon)
				|| enemy.getType().equals(UnitType.Protoss_Reaver)
				|| enemy.getType().equals(UnitType.Protoss_Scarab)
				|| enemy.getType().equals(UnitType.Protoss_Arbiter)
				|| enemy.getType().equals(UnitType.Protoss_Scout)
				|| enemy.getType().equals(UnitType.Protoss_Carrier)
				|| enemy.getType().equals(UnitType.Protoss_Interceptor)
				|| enemy.getType().equals(UnitType.Protoss_Photon_Cannon)
				|| enemy.getType().equals(UnitType.Terran_Marine)
				|| enemy.getType().equals(UnitType.Terran_Firebat)
				|| enemy.getType().equals(UnitType.Terran_Ghost)
				|| enemy.getType().equals(UnitType.Terran_Bunker)
				|| enemy.getType().equals(UnitType.Terran_Goliath)
				|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Siege_Mode)
				|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Tank_Mode)
				|| enemy.getType().equals(UnitType.Terran_Vulture)
				|| enemy.getType().equals(UnitType.Terran_Vulture_Spider_Mine)
				|| enemy.getType().equals(UnitType.Terran_Wraith)
				|| enemy.getType().equals(UnitType.Terran_Battlecruiser)
				
				
				
				)
		{
			isAttackUnit = true;
			return isAttackUnit;
		}
		
		
		
		
		
		return isAttackUnit;
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
			
			for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), unit.getType().sightRange())) 
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
			return SM.myMainBaseLocation.getPosition();
		}
		
		
		
		x = x/includeCase;
		y = y/includeCase;
		
		averagePosition = new Position(x,y);
		
		//MyBotModule.Broodwar.drawCircleMap(averagePosition, 7 * Config.TILE_SIZE, Color.Red);
		
		return averagePosition;
	}
	
	
	
	
	

	
	
	public void update() {
		
		balanceIndex = 0;
		
		if(SM.myHydraliskList.size() == 0)
		{
			return;
		}
		
		Position averagePosition = getAveragePosition(SM.myHydraliskList);
		
		if(SM.isInitialBuildOrderFinished==true)// && myPlayer.minerals()>350)
		{
			goOut = true;
		}
		
		if(MyBotModule.Broodwar.getFrameCount() <= 24*60*6.0)
		{
			/*if(SM.enemyMainBaseLocation==null 
					|| SM.isInitialBuildOrderFinished==false 
					|| (SM.isInitialBuildOrderFinished==true && myPlayer.minerals()<350))
			{
			*/	ArrayList <Position> defenseSite = UnitControl_COMMON.defenseSite;
				for(Unit unit : SM.myHydraliskList)
				{				
					Unit nextTarget = getNextTargetOf(UnitType.Zerg_Hydralisk, unit.getPosition());	
					if (nextTarget != null) 
					{
						if (unit.getGroundWeaponCooldown() == 0) 
						{
							commandUtil.attackUnit(unit, nextTarget);
							continue;
						}				
						else
						{
							commandUtil.move(unit, SM.myMainBaseLocation.getPosition());
							continue;
						}
						
						
						
						/*
						else if(Hydralisk.isUnderAttack())
						{
							//System.out.println("공격받아서 집으로");
							commandUtil.move(Hydralisk, SM.myMainBaseLocation.getPosition());
							continue;
						}
						*/					
					}
					
					if (defenseSite.isEmpty()==false && unit.isAttacking()==false)
					{
						commandUtil.attackMove(unit, UnitControl_COMMON.getClosestDefenseSite(unit));
					}
					else
					{
						commandUtil.attackMove(unit, SM.myFirstChokePoint.getCenter());
					}
	
				}
				
				return;
			//}
		}
		
		/*
		if (MyBotModule.Broodwar.getFrameCount() > 24*60*6.0 && MyBotModule.Broodwar.getFrameCount() < 24*60*6.1)
		{
			for(Unit Hydralisk : SM.myHydraliskList)
			{
				commandUtil.attackMove(Hydralisk, SM.enemyMainBaseLocation.getPosition());
			}
			return;
		}
		*/
		
		
		
		
		
		//setStartGatherEndPoint();
		
		if(UnitControl_COMMON.moveIndex == UnitControl_COMMON.BASIC_MOVE_INDEX)
		{
			gatherIndex=UnitControl_COMMON.moveIndex - 1;
		}
		
		if(UnitControl_COMMON.enoughGathered(UnitType.Zerg_Hydralisk, UnitControl_COMMON.movePosition, 6, 0.4) == true && gatherIndex<UnitControl_COMMON.moveIndex )
		{
			gatherIndex++;
			if(gatherIndex>(UnitControl_COMMON.positionList.size()-1))
			{
				gatherIndex=(UnitControl_COMMON.positionList.size()-1);
			}
		}
		
		//System.out.println("Hydralisk gatherIndex : " + gatherIndex);
		//System.out.println("UnitControl_COMMON.moveIndex : " + UnitControl_COMMON.moveIndex);

		ArrayList <Position> defenseSite = UnitControl_COMMON.defenseSite;
		Position movePosition = UnitControl_COMMON.movePosition;
		Unit nextTarget = null;
		int i = 0;


		//List <Unit> goUp = MyBotModule.Broodwar.getUnitsInRadius(SM.enemyFirstChokePoint.getCenter(), 3*Config.TILE_SIZE);
		//MyBotModule.Broodwar.drawCircleMap(SM.enemyFirstChokePoint.getCenter(), 3*Config.TILE_SIZE, Color.Orange);

		
		
		for(i=0 ; i<SM.myHydraliskList.size() ; i++)
		{
			Unit Hydralisk = SM.myHydraliskList.get(i);
			
			/*boolean shouldGoUp = false;
			for(Unit tempUnit : goUp)
			{
				if(Hydralisk.getID() == tempUnit.getID())
				{
					//System.out.println("길막이라 올라갑니다.");
					commandUtil.move(Hydralisk, enemyMainBaseLocation.getPosition());
					shouldGoUp = true;
					break;
				}
			}
			
			if(shouldGoUp==true)
			{
				continue;
			}*/
			/*
			if(Hydralisk.isIrradiated())
			{
				if(Hydralisk != null)
				{
					commandUtil.move(Hydralisk, enemyMainBaseLocation.getPosition());
					continue;
				}
				
			}
			
			if(Hydralisk.isUnderStorm())
			{
				if(Hydralisk != null)
				{
					commandUtil.move(Hydralisk, myMainBaseLocation.getPosition());
					continue;				
				}
			}
			*/

			
			
			nextTarget = getNextTargetOf(UnitType.Zerg_Hydralisk, Hydralisk.getPosition());
		

			
			if (nextTarget != null) 
			{
				/*
				catchAndAttackUnit(Hydralisk, nextTarget);
				continue;
				*/
				
				
				/*
				commandUtil.attackUnit(Hydralisk, nextTarget);
				continue;
				*/
				
				if (Hydralisk.getGroundWeaponCooldown() == 0) 
				{
					//System.out.println("사정거리 이내 / 쿨타임 0");
					commandUtil.attackUnit(Hydralisk, nextTarget);
					continue;
				}
				else
				{
					if(SM.combatState == StrategyManager.CombatState.attackStarted)
					{
						commandUtil.move(Hydralisk, setFleePoint(averagePosition));
						continue;
					}
					else
					{
						commandUtil.move(Hydralisk, SM.myMainBaseLocation.getPosition());
						continue;
					}
					
				}
				
				
				
				/*
				else if(Hydralisk.isUnderAttack())
				{
					//System.out.println("공격받아서 집으로");
					commandUtil.move(Hydralisk, SM.myMainBaseLocation.getPosition());
					continue;
				}
				*/
				
				/*
				else if(PredictMovement(nextTarget,24).getDistance(Hydralisk) < Hydralisk.getType().groundWeapon().maxRange())
				{
					//System.out.println("가까워서 멀어져라");
					commandUtil.move(Hydralisk, PredictMovement(nextTarget,72));
					continue;
				}
				*/
				
				/*
				else if(Hydralisk.isUnderAttack())//(nextTarget.getDistance(Hydralisk.getPosition()) < Hydralisk.getType().groundWeapon().maxRange())
				{
					//System.out.println("사정거리 이내 / 쿨타임 0 아님 / 공격받는중");
					commandUtil.move(Hydralisk, SM.myMainBaseLocation.getPosition());
					continue;
				}
				*/
				
				
				/*
				if (Hydralisk.getGroundWeaponCooldown() == 0) 
				{
					//System.out.println("사정거리 이내 / 쿨타임 0");
					commandUtil.attackUnit(Hydralisk, nextTarget);
					continue;
				} 
				else if(Hydralisk.isUnderAttack())//(nextTarget.getDistance(Hydralisk.getPosition()) < Hydralisk.getType().groundWeapon().maxRange())
				{
					//System.out.println("사정거리 이내 / 쿨타임 0 아님 / 공격받는중");
					commandUtil.move(Hydralisk, SM.myMainBaseLocation.getPosition());
					continue;
				}				
				*/
			}
			/*
			else if (nextTarget != null) // 적은 있는데  멀다
			{
				if (Hydralisk.getGroundWeaponCooldown() == 0) 
				{
					//System.out.println("목표적은 멀지만 일단 쿨이 돌아와서 공격한다 어택땅");
					commandUtil.attackMove(Hydralisk, nextTarget.getPosition());
					continue;
				} 
				else if(Hydralisk.isUnderAttack())
				{
					commandUtil.move(Hydralisk, SM.myMainBaseLocation.getPosition());
					continue;
				}
				
				
			}
			*/
			
			
			
			
			
			if (defenseSite.isEmpty()==false && Hydralisk.isAttacking()==false)
			{
				commandUtil.attackMove(Hydralisk, UnitControl_COMMON.getClosestDefenseSite(Hydralisk));
			}
			else if(SM.combatState == StrategyManager.CombatState.attackStarted)
			{
				//commandUtil.attackMove(Hydralisk, movePosition);
				if(i>5)
				{
					commandUtil.attackMove(Hydralisk, movePosition);
					continue;
				}
				else
				{
					if(Hydralisk.getTilePosition()!=null)
					{					
						if(bwta.BWTA.getRegion(Hydralisk.getTilePosition())!=null)
						{
							if(bwta.BWTA.getRegion(Hydralisk.getTilePosition()).getPolygon()!=null)
							{
								if(bwta.BWTA.getRegion(Hydralisk.getTilePosition()).getPolygon().isInside(StrategyManager.Instance().enemyMainBaseLocation.getPosition())==false)
								{
									if(Hydralisk.isLoaded()==false)
									{
										OverloadManager.Instance().addDropshipUnit(Hydralisk);
										continue;
									}
								}
								else
								{
									commandUtil.attackMove(Hydralisk, SM.enemyMainBaseLocation.getPosition());
									continue;
								}
								
								
							}
						}	
					}
					else
					{						
						commandUtil.attackMove(Hydralisk, movePosition);
						continue;
					}
				}
				
				commandUtil.attackMove(Hydralisk, movePosition);
				continue;

			}
			else if (SM.myHydraliskList.size() > 12) 
			{			
				if(Hydralisk!=null)
				{
					balanceIndex = UnitControl_COMMON.defenseBalance(Hydralisk, balanceIndex);
				}				
			}		
			else
			{
				commandUtil.attackMove(Hydralisk, movePosition);
			}

		}
	}



	public boolean catchAndAttackUnit(Unit attacker, Unit target) {
		if (target == null || !target.exists()) {
			return false;
		}
		if (!target.isMoving() || !attacker.canMove() || attacker.isInWeaponRange(target))
		{
			commandUtil.attackUnit(attacker, target);
		}
		else
		{
			Position destination = PredictMovement(target, 8);	// the number is how many frames to look ahead
			//BWAPI::Broodwar->drawLineMap(attacker->getPosition(), destination, BWAPI::Colors::Blue);
			MyBotModule.Broodwar.drawLineMap(attacker.getPosition(), destination, Color.Yellow);
			commandUtil.move(attacker, destination);
			System.out.println("추격");
		}
		return true;
	}

	private Position PredictMovement(Unit target, int frames) {
		Position pos =  new Position(
				target.getPosition().getX() + (int)(frames * target.getVelocityX()),
				target.getPosition().getY() + (int)(frames * target.getVelocityY())
			);
		//ClipToMap(pos);
		return pos;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private static UnitControl_Hydralisk instance = new UnitControl_Hydralisk();

	// static singleton 객체를 리턴합니다
	public static UnitControl_Hydralisk Instance() {
		return instance;
	}
	
	
}

