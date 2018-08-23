/*
KATA BOT
BasicBot
fastMutalBot
ML_Q_Learning_Bot
Bomb_Drop
Squad_Sample
*/

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
		
		
		if(SM.myLurkerList.size() < 5 && SM.myHydraliskList.size()>18)
		{
			if(myPlayer.gas()>200)
			{
				SM.myHydraliskList.get(SM.myHydraliskList.size()-1).morph(UnitType.Zerg_Lurker);
			}
		}
		
		if(SM.myLurkerList.size() > 0)
		{
			for(Unit unit : SM.myLurkerList)
			{
				if(CombatState == StrategyManager.CombatState.attackStarted)// && myPlayer.getUpgradeLevel(UpgradeType.Ventral_Sacs)>0)// 공격나가는 시점		
				{
						if(unit.getTilePosition()!=null)
						{					
							if(bwta.BWTA.getRegion(unit.getTilePosition())!=null)
							{
								if(bwta.BWTA.getRegion(unit.getTilePosition()).getPolygon()!=null)
								{
									if(bwta.BWTA.getRegion(unit.getTilePosition()).getPolygon().isInside(StrategyManager.Instance().enemyMainBaseLocation.getPosition())==false)
									{
										if(unit.isBurrowed())
										{
											unit.unburrow();
										}
										
										if(unit.isLoaded()==false)
										{
											OverloadManager.Instance().addDropshipUnit(unit);
										}
									}
									else
									{
										lurkerAttackLogic(unit,SM.enemyMainBaseLocation.getPosition());
									}
									
									
								}
							}	
						}
						else 
						{
							lurkerAttackLogic(unit, UnitControl_COMMON.movePosition);
						}
				
				}
				else if(UnitControl_COMMON.defenseSite.isEmpty()==false)
				{
					Position toSearch = UnitControl_COMMON.getClosestDefenseSite(unit);
					Unit nextTarget = UnitControl_Zergling.Instance().getNextTargetOf(UnitType.Zerg_Lurker, toSearch);
					
					if(nextTarget!=null) 
					{
						lurkerAttackLogic(unit, nextTarget.getPosition());
					}
									
				}
				else
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
						lurkerAttackLogic(unit, SM.mySecondChokePoint.getCenter());						
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
	
	public void lurkerAttackLogic(Unit unit, Position targetPosition)
	{
		Unit tempEnemy = null;
		for(Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 5*Config.TILE_SIZE))
		{
			if(enemy.isFlying()==true)
			{
				continue;
			}
			
			if(enemy.isLifted() == true)
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
			
			commandUtil.move(unit, targetPosition);
		}
		
	}
	
	
	
}
