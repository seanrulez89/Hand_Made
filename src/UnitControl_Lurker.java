
import java.util.*;

import bwapi.*;
import bwta.*;


public class UnitControl_Lurker {

	
	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	
	Player myPlayer = SM.myPlayer;
	Player enemyPlayer = SM.enemyPlayer;
	
	Race myRace = SM.myRace;
	Race enemyRace = SM.enemyRace;
	
	StrategyManager.CombatState CombatState = SM.combatState;
	
	public void update()
	{
		if(SM.myHydraliskList.size() == 0 || myPlayer.hasResearched(TechType.Lurker_Aspect) == false)
		{
			return;
		}
		
		//System.out.println("LURKER : " + SM.myLurkerList.size());
		
		
		if(SM.myLurkerList.size() < 5)
		{
			if(myPlayer.gas()>200)
			{
				SM.myHydraliskList.get(SM.myHydraliskList.size()-1).morph(UnitType.Zerg_Lurker);
			}
		}
		
		if(SM.myLurkerList.size() > 0)
		{
			if(CombatState == StrategyManager.CombatState.attackStarted)// && myPlayer.getUpgradeLevel(UpgradeType.Ventral_Sacs)>0)// 공격나가는 시점		
			{
				for(Unit unit : SM.myLurkerList)
				{
					
					/*
					UnitControl_Overlord a = new UnitControl_Overlord();
					ArrayList<Unit> myOverLords = a.getOverLords();
					
					if(unit.isBurrowed())
					{
						unit.unburrow();
					}
					
					if(unit.isLoaded()== false && myOverLords.size()>5)
					{
						
							unit.load(myOverLords.get(6), true);					
						
					}
					else if(unit.isLoaded())
					{
						myOverLords.get(6).unloadAll(new Position(63*32, 63*32));
					}
					*/
					
					
					
					
					
					Unit tempEnemy = null;
					
					for(Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 5*Config.TILE_SIZE))
					{
						if(enemy.isFlying()==true)
						{
							continue;
						}
						
						if(enemy.getPlayer() == enemyPlayer)
						{
							tempEnemy = enemy;
							break;
						}
					}
					
					if(tempEnemy!=null)
					{
						if(unit.isBurrowed()==false)
						{
							unit.burrow();
						}
					}
					else
					{
						if(unit.isBurrowed() == true)
						{
							unit.unburrow();
						}
						
						commandUtil.move(unit, SM.enemyMainBaseLocation.getPosition());
						//unit.move(SM.enemyMainBaseLocation.getPosition());
					}
					
					
					
				}
			}
			else
			{
				for(Unit unit : SM.myLurkerList)
				{
					
					boolean isNear = false;
					for(Unit myUnit : MyBotModule.Broodwar.getUnitsInRadius(SM.mySecondChokePoint.getCenter(), 3*Config.TILE_SIZE))
					{
						if(myUnit.getID() == unit.getID())
						{
							isNear=true;
							break;
						}
					}
					
					if(isNear==false)
					{
						if(unit.isBurrowed()==true)
						{
							unit.unburrow();	
						}
						commandUtil.move(unit, SM.mySecondChokePoint.getCenter());
						//unit.move(SM.mySecondChokePoint.getCenter());
					}
					else
					{
						if(unit.isBurrowed()==false)
						{
							unit.burrow();
						}
					}
				}
			}
			
		}
		
	}
	
}
