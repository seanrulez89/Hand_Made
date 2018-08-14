
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
	

	
	static int balanceIndex = 0;
	static int gatherIndex = UnitControl_COMMON.moveIndex - 1;




	public Unit getNextTargetOf(UnitType myUnitType, Position averagePosition) {
		Unit nextTarget = null;
		
		double targetHP = 10000;
		double tempHP = 0;		

		
		ArrayList <Unit> mustKillFirst = new ArrayList<Unit>();		 
		 																			
		for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(averagePosition, myUnitType.sightRange())) {

			if (enemy.getPlayer() == enemyPlayer) {
				
				if(enemy.isVisible() == false)
				{
					continue;
				}
				
				if(enemy.isFlying() == true)
				{
					continue;
				}
				
				if(enemy.isDefenseMatrixed()==true)
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
						|| enemy.getType().equals(UnitType.Terran_Bunker))
//						|| enemy.getType().equals(UnitType.Terran_Medic))
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
	
	
	

	
	
	public void update() {
		
		balanceIndex = 0;
		
		if(SM.myZerglingList.size() == 0)
		{
			return;
		}
		
		//setStartGatherEndPoint();
		
		if(UnitControl_COMMON.moveIndex == UnitControl_COMMON.BASIC_MOVE_INDEX)
		{
			gatherIndex=UnitControl_COMMON.moveIndex - 1;
		}
		
		if(UnitControl_COMMON.enoughGathered(UnitType.Zerg_Hydralisk, UnitControl_COMMON.movePosition, 5, 0.5) == true && gatherIndex<UnitControl_COMMON.moveIndex )
		{
			gatherIndex++;
			if(gatherIndex>(UnitControl_COMMON.positionList.size()-1))
			{
				gatherIndex=(UnitControl_COMMON.positionList.size()-1);
			}
		}
		
		//System.out.println("Zergling gatherIndex : " + gatherIndex);
		
		

		Position defenseSite = UnitControl_COMMON.defenseSite;
		Position movePosition = UnitControl_COMMON.movePosition;
		Unit nextTarget = null;
		int i = 0;


		List <Unit> goUp = MyBotModule.Broodwar.getUnitsInRadius(SM.enemyFirstChokePoint.getCenter(), 3*Config.TILE_SIZE);
		MyBotModule.Broodwar.drawCircleMap(SM.enemyFirstChokePoint.getCenter(), 3*Config.TILE_SIZE, Color.Orange);

		
		
		for(i=0 ; i<SM.myZerglingList.size() ; i++)
		{
			Unit Zergling = SM.myZerglingList.get(i);
			
			/*boolean shouldGoUp = false;
			for(Unit tempUnit : goUp)
			{
				if(Zergling.getID() == tempUnit.getID())
				{
					//System.out.println("길막이라 올라갑니다.");
					commandUtil.move(Zergling, enemyMainBaseLocation.getPosition());
					shouldGoUp = true;
					break;
				}
			}
			
			if(shouldGoUp==true)
			{
				continue;
			}*/
			/*
			if(Zergling.isIrradiated())
			{
				if(Zergling !=null)
				{
					commandUtil.move(Zergling, enemyMainBaseLocation.getPosition());
					continue;
				}
				
			}
			
			if(Zergling.isUnderStorm())
			{
				if(Zergling != null)
				{
					commandUtil.move(Zergling, myMainBaseLocation.getPosition());
					continue;
				}
				
			}
			*/
			nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());
			
			/*
			if(i % 10 == 0)
			{
				nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());			
			}
			*/
			
			if (nextTarget != null) 
			{
				commandUtil.attackUnit(Zergling, nextTarget);
				continue;
			}
			
			
			
			
			if (defenseSite != null && Zergling.isAttacking()==false)
			{
				commandUtil.attackMove(Zergling, defenseSite);				
			}
			else if(SM.combatState == StrategyManager.CombatState.attackStarted)
			{
				commandUtil.attackMove(Zergling, movePosition);				
			}
			else if (SM.myZerglingList.size() > 12) 
			{			
				if(Zergling!=null)
				{
					balanceIndex = UnitControl_COMMON.defenseBalance(Zergling, balanceIndex);
				}
			}		
			else
			{
				commandUtil.attackMove(Zergling, movePosition);
			}
		
		
		}
		
		
		
		
		
		
		
		
		
		
		/*
		
		if(SM.myZerglingList.size() == 0)
		{
			return;
		}
		
		setStartGatherEndPoint();

		
		boolean endGame = enoughGathered(UnitType.Zerg_Zergling, gatherPoint, 3, 0.3);
		Position invader = null;
		Unit nextTarget = null;
		
		int i = 0;


		//System.out.println("SM.myZerglingList.size() : " + SM.myZerglingList.size());
		
		for(i=0 ; i<SM.myZerglingList.size() ; i++)
		{
			
			Unit Zergling = SM.myZerglingList.get(i);
			
			if(i % (SM.myZerglingList.size()/6+1) == 0)
			{
				invader = UnitControl_COMMON.defenseSite;			
				
				if (invader != null && Zergling.isAttacking()==false)
				{	
					nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, invader);
				}
				else
				{
					nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());
				}
				
			}
			else
			{
				if(nextTarget!=null && Zergling.getDistance(nextTarget.getPosition()) > UnitType.Zerg_Zergling.sightRange())
				{
					invader = UnitControl_COMMON.defenseSite;			
					
					if (invader != null && Zergling.isAttacking()==false)
					{	
						nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, invader);
					}
					else
					{
						nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());
					}
				}
			}
			
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
			
	
			
			
			
			
			if (nextTarget != null) 
			{
				//System.out.println(1);
				commandUtil.attackUnit(Zergling, nextTarget);
				//Zergling.attack(nextTarget);
				continue; // 컨티뉴를 하면 무조건 이 타겟을 치고 안하면 근처를 친다 왜냐하면 어택땅이 기본 상태라서 근접유닛은 안하는게 낫겠다 자꾸 병목현상되니까
			}
			
			
			if(SM.enemyMainBaseLocation == null)
			{
				//System.out.println(3);
				commandUtil.attackMove(Zergling, startPoint);
			}
			else if(CombatState == StrategyManager.CombatState.attackStarted)// 공격나가는 시점		
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
				if(Zergling!=null)
				{
					defenseBalance(Zergling);					
				}
			}
			else
			{	
				//System.out.println(10);
				commandUtil.attackMove(Zergling, startPoint);
			}
		}*/
	}
	
	private static UnitControl_Zergling instance = new UnitControl_Zergling();

	// static singleton 객체를 리턴합니다
	public static UnitControl_Zergling Instance() {
		return instance;
	}
	
	
}
