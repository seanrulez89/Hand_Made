
import java.util.*;

import bwapi.*;
import bwta.*;




public class UnitControl_COMMON {

	
	static Position defenseSite = null;
	static Position movePosition = null;
	
	final static int BASIC_MOVE_INDEX = 5;
	static int moveIndex = BASIC_MOVE_INDEX;
	static List<Position> positionList = null; // 몇분할인가
	
	
	public void getDefenseSite() {
	
		// 건물이나 드론이 얻어맞는 경우
		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) 
		{
			if ((unit.getType().isBuilding() && unit.isUnderAttack())
					|| (unit.getType().equals(UnitType.Zerg_Drone) && unit.isUnderAttack())
					|| (unit.getType().equals(UnitType.Zerg_Overlord) && unit.isUnderAttack())
					&& (unit.getType() != UnitType.Zerg_Sunken_Colony)) 
			{
				defenseSite = unit.getPosition();
				return;
			}
		}


		MyBotModule.Broodwar.drawCircleMap(StrategyManager.Instance().mySecondChokePoint.getCenter(), 13 * Config.TILE_SIZE, Color.Blue);
		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(StrategyManager.Instance().mySecondChokePoint.getCenter(), 13 * Config.TILE_SIZE)) 
		{
			if (unit.getPlayer().equals(InformationManager.Instance().enemyPlayer)) 
			{ 
				defenseSite = unit.getPosition();
				return; 
			}			
		}
		
		

		// 기지 주변에 악당이 등장하는 경우
		for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(MyBotModule.Broodwar.self())) 
		{
			for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(baseLocation.getPosition(), 13 * Config.TILE_SIZE)) 
			{
				if (unit.getPlayer().equals(InformationManager.Instance().enemyPlayer)) 
				{ 
					defenseSite = unit.getPosition();
					return; 
				}			
			}
		}
		
		defenseSite = null;
	}
	
	
	public void getMovePosition ()
	{
		if(positionList == null || positionList.isEmpty())
		{
			System.out.println("positionList == null || positionList.isEmpty()");
			positionList = InformationManager.getAssemblyPlaceList(10);
		}

		
		

		if(StrategyManager.Instance().enemyMainBaseLocation==null)
		{
			movePosition = StrategyManager.Instance().mySecondChokePoint.getPoint();
			moveIndex = BASIC_MOVE_INDEX;
		}
		else if(StrategyManager.Instance().combatState == StrategyManager.CombatState.attackStarted)
		{
			//List<Position> positionList = InformationManager.Instance().getAssemblyPlaceList(10);

			movePosition = positionList.get(moveIndex);

			//System.out.println("moveIndex : " + moveIndex);
			
			if(UnitControl_Hydralisk.gatherIndex == moveIndex && UnitControl_Zergling.gatherIndex == moveIndex)
			{
				moveIndex++;
				if(moveIndex>(positionList.size()-1))
				{
					moveIndex=(positionList.size()-1);
				}
			}			
		}
		else
		{
			movePosition = StrategyManager.Instance().mySecondChokePoint.getPoint();
			moveIndex = BASIC_MOVE_INDEX;			
		}
		
		
		
		
	}
	
	public static boolean enoughGathered(UnitType myUnitType, Position targetPosition, int radius, double input_ratio) {

		if (StrategyManager.Instance().enemyMainBaseLocation == null) {
			 // System.out.println("아직 기지 못 찾음");
			return false;
		}
		
		int numberOfGathered = 0;
		int numberOfTotal = MyBotModule.Broodwar.self().completedUnitCount(myUnitType);
		



		

		List<Unit> unitsAround = MyBotModule.Broodwar.getUnitsInRadius(targetPosition, radius * Config.TILE_SIZE);
		for (Unit unit : unitsAround) {

			if (unit.exists() && unit != null) {

				if (unit.getPlayer() == MyBotModule.Broodwar.self()) {

					if (unit.getType() == myUnitType) {
						numberOfGathered++;
					}
				}
			}
		}
		
		if (numberOfGathered > numberOfTotal * input_ratio) {
			return true;
		}
		else
		{
			return false;
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	



	
	
	
	
	private static UnitControl_COMMON instance = new UnitControl_COMMON();

	/// static singleton 객체를 리턴합니다
	public static UnitControl_COMMON Instance() {
		return instance;
	}
	
}
