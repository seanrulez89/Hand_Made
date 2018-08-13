
import java.util.*;

import bwapi.*;
import bwta.*;




public class UnitControl_Defiler {

	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	Player myPlayer = SM.myPlayer;
	Race myRace = SM.myRace;
	Player enemyPlayer = SM.enemyPlayer;
	Race enemyRace = SM.enemyRace;
	StrategyManager.CombatState CombatState = SM.combatState;
	
	
	public void update() {
		
		if(SM.myDefilerList.size() == 0)
		{
			return;
		}
		
		
		int idx=0;
		for(idx=0 ; idx<SM.myDefilerList.size() ; idx++)
		{
			Unit Defiler = SM.myDefilerList.get(idx);
			
			
			Position position = null;
			
			int currentEnemy = 0;
			int maxEnemy = 0;
			
			int tempUnderAttack = 0;
			int maxUnderAttack = 0;
			
			
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					Position tempPosition = new Position(Defiler.getX() + 8 * Config.TILE_SIZE * i,
							Defiler.getY() + 8 * Config.TILE_SIZE * j);

					//MyBotModule.Broodwar.drawCircleMap(tempPosition, 4 * Config.TILE_SIZE, Color.Red);
					
					/*
					currentEnemy = 0;
					for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(tempPosition, 4 * Config.TILE_SIZE)) {
						if (enemy.getPlayer() == enemyPlayer) // 건물은 제외하고
						{
							if(enemy.isUnderDarkSwarm()==true)
							{
								currentEnemy = 0;
								break;
							}
							else
							{
								currentEnemy++;
							}							
						}
					}
					
					//System.out.println("currentEnemy : " + currentEnemy);

					if (currentEnemy > maxEnemy && currentEnemy > 4) {
						maxEnemy = currentEnemy;
						position = tempPosition;
					}
					*/
					
					tempUnderAttack = 0;
					for (Unit myUnit : MyBotModule.Broodwar.getUnitsInRadius(tempPosition, 4 * Config.TILE_SIZE)) {
						if (myUnit.getPlayer() == myPlayer) // 건물은 제외하고
						{
							if(myUnit.isUnderDarkSwarm()==true)
							{
								tempUnderAttack = 0;
								break;
							}
							else if(myUnit.isUnderAttack()==true)
							{
								tempUnderAttack++;
							}							
						}
					}
					
					//System.out.println("currentEnemy : " + currentEnemy);

					if (tempUnderAttack > maxUnderAttack) {
						maxUnderAttack = tempUnderAttack;
						position = tempPosition;
					}
					
					
					

				}
			}
			
			if(myPlayer.hasResearched(TechType.Dark_Swarm))// && CombatState == StrategyManager.CombatState.attackStarted)
			{
				if(position!=null)
				{
					if(Defiler.getEnergy() > TechType.Dark_Swarm.energyCost())
					{
						//System.out.println("1");
						Defiler.useTech(TechType.Dark_Swarm, position);
					}
					else if(myPlayer.hasResearched(TechType.Consume))
					{
						for(Unit unit : MyBotModule.Broodwar.getUnitsInRadius(Defiler.getPosition(), 4*Config.TILE_SIZE))
						{
							if(unit.getPlayer()==myPlayer)
							{
								if(unit.getType().equals(UnitType.Zerg_Zergling))
								{
									//System.out.println("2");
									Defiler.useTech(TechType.Consume, unit);
									break;
								}
								
								
							}
							
						}
					}
					else
					{
						//System.out.println("3");
						commandUtil.move(Defiler, SM.mySecondChokePoint.getCenter());
					}
					
				}
				else
				{
					if(CombatState == StrategyManager.CombatState.attackStarted)
					{
						//System.out.println("4-1");
						commandUtil.move(Defiler, UnitControl_COMMON.movePosition);
					}
					else
					{
						//System.out.println("4-2");						
						commandUtil.move(Defiler, SM.mySecondChokePoint.getCenter());
					}
		
				}
			}
			else
			{
				//System.out.println("5");
				commandUtil.move(Defiler, SM.mySecondChokePoint.getCenter());
			}
			
			
			
			
			
			
		
		}
		
		
		
		
		
	}
}
