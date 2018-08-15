
import java.util.*;

import bwapi.*;
import bwta.*;




public class UnitControl_COMMON {

	private static CommandUtil commandUtil = new CommandUtil();
	
	static ArrayList <Position> defenseSite = new ArrayList<Position>();
	static Position movePosition = null;
	
	final static int ASSEMBLY_NUMBER = 6;
	final static int BASIC_MOVE_INDEX = ASSEMBLY_NUMBER/2;
	static int moveIndex = BASIC_MOVE_INDEX;
	static List<Position> positionList = null; // 몇분할인가
	static ArrayList <Position> localDefense = null;
	
	
	public void getDefenseSite() {
	
		if(defenseSite.isEmpty()==false)
		{
			defenseSite.clear();
		}
		
		
		// 건물이나 드론이 얻어맞는 경우
		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) 
		{
			if ((unit.getType().isBuilding() && unit.isUnderAttack())
					|| (unit.getType().equals(UnitType.Zerg_Drone) && unit.isUnderAttack())
					
					&& (unit.getType() != UnitType.Zerg_Sunken_Colony))
//				|| (unit.getType().equals(UnitType.Zerg_Overlord) && unit.isUnderAttack())
			{

				defenseSite.add(unit.getPosition());
				
			}
		}

		
		MyBotModule.Broodwar.drawCircleMap(StrategyManager.Instance().mySecondChokePoint.getCenter(), 12 * Config.TILE_SIZE, Color.Blue);
		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(StrategyManager.Instance().mySecondChokePoint.getCenter(), 12 * Config.TILE_SIZE)) 
		{
			if (unit.getPlayer().equals(InformationManager.Instance().enemyPlayer)) 
			{ 
				defenseSite.add(unit.getPosition()); 
			}			
		}
		
		

		// 기지 주변에 악당이 등장하는 경우
		for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(MyBotModule.Broodwar.self())) 
		{
			for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(baseLocation.getPosition(), 12 * Config.TILE_SIZE)) 
			{
				if (unit.getPlayer().equals(InformationManager.Instance().enemyPlayer)) 
				{ 
					defenseSite.add(unit.getPosition());
				}			
			}
		}
		
		
	}
	
	
	public void getMovePosition ()
	{
		if(positionList == null || positionList.isEmpty())
		{
			//System.out.println("positionList == null || positionList.isEmpty()");
			positionList = InformationManager.getAssemblyPlaceList(ASSEMBLY_NUMBER);
		}

		
		

		if(StrategyManager.Instance().enemyMainBaseLocation==null)
		{
			movePosition = StrategyManager.Instance().mySecondChokePoint.getCenter();
			moveIndex = BASIC_MOVE_INDEX;
		}
		else if(StrategyManager.Instance().combatState == StrategyManager.CombatState.attackStarted)
		{
			//List<Position> positionList = InformationManager.Instance().getAssemblyPlaceList(10);

			if(moveIndex>=5)
			{
				movePosition = StrategyManager.Instance().enemyMainBaseLocation.getPosition();
			}
			else
			{
				movePosition = positionList.get(moveIndex);
			}
			
			
			

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
			movePosition = StrategyManager.Instance().mySecondChokePoint.getCenter();
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

	
	public void getLocalDefense()
	{
		List<BaseLocation> myBaseLocations = InformationManager.Instance()
				.getOccupiedBaseLocations(InformationManager.Instance().selfPlayer);
		
		ArrayList<Position> defenseSite = new ArrayList<Position>();
		
		Iterator <BaseLocation> lir = myBaseLocations.iterator();		
		while(lir.hasNext())
		{

			
			Position position = lir.next().getPosition();
			
			defenseSite.add(position);
			
			Position chokePoint = bwta.BWTA.getNearestChokepoint(position).getCenter();
			defenseSite.add(chokePoint);
			
		}
		
		
		Iterator <Position> iter = defenseSite.iterator();	
		while(iter.hasNext())
		{
			Position position = iter.next();
			
			if(position.equals(StrategyManager.Instance().myFirstChokePoint.getPoint()))
			{
				iter.remove();
			}
			else if(position.equals(StrategyManager.Instance().mySecondChokePoint.getPoint()))
			{
				iter.remove();
			}
		}

		
		
		defenseSite.add(StrategyManager.Instance().mySecondChokePoint.getPoint());
		
		
		BaseLocation expansionLocation = BuildOrder_Expansion.expansion();
		if(expansionLocation !=null)
		{
			Chokepoint tempChokePoint = bwta.BWTA.getNearestChokepoint(expansionLocation.getPosition());
			Position middlePosition = new Position ((expansionLocation.getX()+tempChokePoint.getX())/2,
					(expansionLocation.getY()+tempChokePoint.getY())/2);
		
			defenseSite.add(middlePosition);
			
			
			//defenseSite.add(bwta.BWTA.getNearestChokepoint(expansionLocation.getPosition()).getCenter());
		}
		
		localDefense = defenseSite;
		
	}
	
	
	public static int defenseBalance(Unit unit, int balanceIndex) {
		

		
		
		
		int num = localDefense.size();
		

		if(balanceIndex>=num)
		{
			balanceIndex=0;
		}
		commandUtil.attackMove(unit, localDefense.get(balanceIndex));				
		balanceIndex++;
		
		return balanceIndex;
	
	}
	
	
	public static Position getClosestDefenseSite(Unit unit) {
		
		double minDistance = 100000000;
		double tempDistance = 0;
		Position unitPosition = unit.getPosition();
		Position closestDefenseSite = StrategyManager.Instance().myMainBaseLocation.getPosition();
		
		for(Position tempPosition : defenseSite)
		{
			tempDistance = tempPosition.getDistance(unitPosition);
			if(tempDistance < minDistance)
			{
				minDistance = tempDistance;
				closestDefenseSite = tempPosition;
			}
			
		}
		
		
		return closestDefenseSite;
	
	}
	
	
	
	
	
	
	



	
	
	
	
	private static UnitControl_COMMON instance = new UnitControl_COMMON();

	/// static singleton 객체를 리턴합니다
	public static UnitControl_COMMON Instance() {
		return instance;
	}
	
}
