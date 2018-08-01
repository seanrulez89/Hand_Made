
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
	
	public void update()
	{
		if(SM.myHydraliskList.size() == 0 || myPlayer.hasResearched(TechType.Lurker_Aspect) == false)
		{
			return;
		}
		
		if(SM.myLurkerList.size() < 6)
		{
			if(myPlayer.gas()>200)
			{
				SM.myHydraliskList.get(0).morph(UnitType.Zerg_Lurker);
			}
		}
		
		if(SM.myLurkerList.size() > 0)
		{
			//if(myPlayer.completedUnitCount(UnitType.Zerg_Mutalisk) > 10 || myPlayer.completedUnitCount(UnitType.Zerg_Lurker)>4) // 공격나가는 시점
			if(SM.isTimeToStartAttack() == true) // 공격나가는 시점
			{
				for(Unit unit : SM.myLurkerList)
				{
					Unit tempEnemy = null;
					
					for(Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 6*Config.TILE_SIZE))
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
						
						unit.move(SM.enemyMainBaseLocation.getPosition());
					}
					
					
					
				}
			}
			else
			{
				for(Unit unit : SM.myLurkerList)
				{
					if(MyBotModule.Broodwar.getUnitsInRadius(SM.mySecondChokePoint.getCenter(), 3*Config.TILE_SIZE).contains(unit)==false)
					{
						if(unit.isBurrowed()==true)
						{
							unit.unburrow();
						}
						
						unit.move(SM.mySecondChokePoint.getCenter());
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
