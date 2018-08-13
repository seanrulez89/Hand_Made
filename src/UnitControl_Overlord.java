
import java.util.*;

import bwapi.*;
import bwta.*;




public class UnitControl_Overlord {
	
	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	
	Player myPlayer = SM.myPlayer;
	Player enemyPlayer = SM.enemyPlayer;
	
	public ArrayList<Unit> getOverLords()
	{
		ArrayList<Unit> overLords = new ArrayList<Unit>();
		
		for(Unit unit : myPlayer.getUnits())
		{
			if(unit==null || unit.exists() == false || unit.getHitPoints()<=0)
			{
				continue;
			}
			
			if(unit.getType() == UnitType.Zerg_Overlord && unit.isCompleted())
			{
				overLords.add(unit);
			}
		}
		
		
		sortUnitID sortOverLords = new sortUnitID();
		Collections.sort(overLords, sortOverLords);
		
		return overLords;
		
	}
	
	class sortUnitID implements Comparator <Unit>
	{
		@Override
		public int compare(Unit overLord_A, Unit overLord_B)
		{
			return ((Integer)(overLord_A.getID())).compareTo((Integer)(overLord_B.getID()));
		}
	}
	
	public Position getFollowSite(Unit overlord, UnitType unitType)
	{
		ArrayList<Position> explorationSite = new ArrayList<Position>();
		Position nextExplorationSite = null;
		double positionDistance = 100000000;
		double tempDistance = 0;
		ArrayList<Unit> myAttackUnitList = new ArrayList<Unit>();
		
		
		if(unitType == UnitType.Zerg_Hydralisk)
		{
			myAttackUnitList = SM.myHydraliskList;
		}
		else if(unitType == UnitType.Zerg_Mutalisk)
		{
			myAttackUnitList = SM.myMutaliskList;
		}
		
		
		// 유닛리스트 관리 알고리즘 수정 20180803 shsh0823.lee
		for(Unit unit : myAttackUnitList) {
			for(Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(unit.getPosition(), 6*Config.TILE_SIZE)) {
				if(enemy.getPlayer()==enemyPlayer) {
					explorationSite.add(unit.getPosition());
					if(explorationSite.size()==2) {
						break;
					}
				}
			}
		}
		
/*		Iterator<Unit> ITR = myAttackUnitList.iterator();
		
		if(myAttackUnitList.size()>0)
		{
			while(ITR.hasNext())
			{
				Unit myAttackUnit = ITR.next();
				Unit tempEnemy = null;
				
				for(Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(myAttackUnit.getPosition(), 6 * Config.TILE_SIZE))
				{
					if(enemy.getPlayer() == enemyPlayer)
					{
						tempEnemy = enemy;
						break;
					}
				}
				
				if(tempEnemy==null)
				{
					ITR.remove();
					break;
				}
			}
		}
		if(myAttackUnitList.size()>2)
		{
			explorationSite.add(myAttackUnitList.get(0).getPosition());
			explorationSite.add(myAttackUnitList.get(1).getPosition());
		}
 */		
		
		if(myAttackUnitList.size()==1)
		{
			explorationSite.add(myAttackUnitList.get(0).getPosition());
		}
		
		Iterator<Position> lir = explorationSite.iterator();
		
		while(lir.hasNext())
		{
			Position position = lir.next();
			
			for(Unit unit : MyBotModule.Broodwar.getUnitsInRadius(position, 1 * Config.TILE_SIZE))
			{
				if(unit.getPlayer() == myPlayer && unit.getType().equals(UnitType.Zerg_Overlord) && unit.isCompleted()
						&& unit != null && unit.getID() != overlord.getID())
				{
					lir.remove();
					break;
				}
			}
		}
		
		for(Position position : explorationSite)
		{
			tempDistance = position.getDistance(overlord.getPosition());
			if(positionDistance > tempDistance)
			{
				positionDistance = tempDistance;
				nextExplorationSite = position;
			}
		}

		if(nextExplorationSite==null)
		{
			return SM.mySecondChokePoint.getCenter();
		}
		
		return nextExplorationSite;
		
	}
	
	
	public Position getExplorationSite(Unit overlord)
	{
		ArrayList<Position> explorationSite = new ArrayList<Position>();
		Position nextExplorationSite = null;
		double positionDistance = 100000000;
		double tempDistance = 0;
		
		
		for(BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(myPlayer))
		{
			explorationSite.add(baseLocation.getPosition());
			
			Position chokePoint = bwta.BWTA.getNearestChokepoint(baseLocation.getPosition()).getCenter();
			explorationSite.add(chokePoint);
		}
		
		for(BaseLocation EXPLocation : BWTA.getBaseLocations())
		{
			if(MyBotModule.Broodwar.isExplored(EXPLocation.getTilePosition())==false)
			{
				explorationSite.add(EXPLocation.getPosition());
			}
		}
		
		

		
		
		
		

		
		explorationSite.add(SM.myFirstChokePoint.getPoint());
		explorationSite.add(SM.mySecondChokePoint.getPoint());		
		//explorationSite.add(new Position(63*32, 63*32));
		
		if(MyBotModule.Broodwar.mapFileName().equals("(4)CircuitBreaker.scx"))
		{
			/*
			 * 그 외 별도 지정좌표
			 */
			
		}
		else
		{
			/*
			 * 그 외 별도 지정좌표
			 */
		}
		
		Iterator <Position> lir = explorationSite.iterator();
		
		while(lir.hasNext())
		{
			Position position = lir.next();
			
			if(SM.enemyMainBaseLocation != null && SM.enemyFirstExpansionLocation !=null)
			{
				if(position.equals(SM.enemyMainBaseLocation.getPosition()) || position.equals(SM.enemyFirstExpansionLocation.getPosition()))
				{
					lir.remove();
					continue;
				}
			}
			
			
			
			
			for(Unit unit : MyBotModule.Broodwar.getUnitsInRadius(position, 1 * Config.TILE_SIZE))
			{
				if(unit.getPlayer()==myPlayer && unit.getType().equals(UnitType.Zerg_Overlord) 
						&& unit.isCompleted() && unit != null && unit.getID() != overlord.getID())
				{
					lir.remove();
					break;
				}
			}
		}
		
		
		
		
		for(Position position : explorationSite)
		{
			tempDistance = position.getDistance(overlord.getPosition());
			if(positionDistance > tempDistance)
			{
				positionDistance = tempDistance;
				nextExplorationSite = position;
			}
		}
		
		if(nextExplorationSite == null)
		{
			return SM.myMainBaseLocation.getPosition();
		}
		
		return nextExplorationSite;
	
	
	}
	
	
	public void update()
	{
		ArrayList<Unit> myOverLords = getOverLords();
		
		if(myOverLords.size()>2)
		{
			for(int i = 2 ; i < myOverLords.size() ; i++)
			{
				Unit overlord = myOverLords.get(i);
				
				if(overlord.isUnderAttack())
				{
					commandUtil.move(overlord, SM.myMainBaseLocation.getPosition());
					//overlord.move(SM.myMainBaseLocation.getPosition());
					continue;
				}
				
				if(i<4)
	            {
					commandUtil.move(overlord, getFollowSite(overlord, UnitType.Zerg_Hydralisk));
					//overlord.move(getFollowSite(overlord, UnitType.Zerg_Hydralisk));
	            }
	            else if(i<6)
	            {
	               if(SM.myMutaliskList.size()>0)
	               {
	            	   commandUtil.move(overlord, getFollowSite(overlord, UnitType.Zerg_Mutalisk));
	            	   //overlord.move(getFollowSite(overlord, UnitType.Zerg_Mutalisk));   
	               }
	               else
	               {
	            	   commandUtil.move(overlord, getExplorationSite(overlord));
	                   //overlord.move(getExplorationSite(overlord));
	               }
	               
	            }
	            else
	            {
	            	commandUtil.move(overlord, getExplorationSite(overlord));
	                //overlord.move(getExplorationSite(overlord));
	            }
					
					
				
				
			}
		}
	}

}