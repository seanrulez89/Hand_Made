
import java.util.*;

import bwapi.*;
import bwta.*;




public class UnitControl_COMMON {

	
	static Position defenseSite = null;
	
	
	
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



		// 기지 주변에 악당이 등장하는 경우
		for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(MyBotModule.Broodwar.self())) 
		{
			for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(baseLocation.getPosition(), 13 * Config.TILE_SIZE)) 
			{
				if (unit.getPlayer().equals(InformationManager.Instance().enemyPlayer)) 
				{ 
					defenseSite = unit.getPosition();
					System.out.println("기지 주변 악당을 찾았다");
					return; 
				}			
			}
		}
		
		defenseSite = null;
	}
	
	



	
	
	
	
	private static UnitControl_COMMON instance = new UnitControl_COMMON();

	/// static singleton 객체를 리턴합니다
	public static UnitControl_COMMON Instance() {
		return instance;
	}
	
}
