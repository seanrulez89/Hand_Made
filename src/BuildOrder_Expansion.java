
import java.util.*;
import java.io.*;

import bwapi.*;
import bwta.*;

public class BuildOrder_Expansion {

	

	public BaseLocation expansion() {
		
		if(StrategyManager.Instance().enemyMainBaseLocation==null)
		{
			return null;
		}
		
		
		BaseLocation nextEXP = null;

		List<BaseLocation> myBaseLocations = InformationManager.Instance()
				.getOccupiedBaseLocations(InformationManager.Instance().selfPlayer);

		List<BaseLocation> enemyBaseLocations = InformationManager.Instance()
				.getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer);

		List<BaseLocation> EXPLocations = BWTA.getBaseLocations();

		double minDistance = 1000000000;
		double distanceFromMyLocation = 0;
		double distanceFromEnemyLocation = 0;
		
		int numberOfMyCombatUnitTrainingBuilding = 0;
		numberOfMyCombatUnitTrainingBuilding += MyBotModule.Broodwar.self().allUnitCount(UnitType.Zerg_Hatchery);
		numberOfMyCombatUnitTrainingBuilding += MyBotModule.Broodwar.self().allUnitCount(UnitType.Zerg_Lair);
		numberOfMyCombatUnitTrainingBuilding += MyBotModule.Broodwar.self().allUnitCount(UnitType.Zerg_Hive);

		numberOfMyCombatUnitTrainingBuilding += BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery);
		numberOfMyCombatUnitTrainingBuilding += BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Lair);
		numberOfMyCombatUnitTrainingBuilding += BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hive);

		numberOfMyCombatUnitTrainingBuilding += ConstructionManager.Instance()
				.getConstructionQueueItemCount(UnitType.Zerg_Hatchery, null);
		numberOfMyCombatUnitTrainingBuilding += ConstructionManager.Instance()
				.getConstructionQueueItemCount(UnitType.Zerg_Lair, null);
		numberOfMyCombatUnitTrainingBuilding += ConstructionManager.Instance()
				.getConstructionQueueItemCount(UnitType.Zerg_Hive, null);
		

		for (BaseLocation myBaseLocation : myBaseLocations) {
			if (EXPLocations.contains(myBaseLocation)) {
				EXPLocations.remove(myBaseLocation);
			}

		}

		for (BaseLocation enemyBaseLocation : enemyBaseLocations) {
			if (EXPLocations.contains(enemyBaseLocation)) {
				EXPLocations.remove(enemyBaseLocation);
			}
		}

		for (BaseLocation EXPLocation : EXPLocations) {
			
		/*
			if(numberOfMyCombatUnitTrainingBuilding<=2)
			{
				if(EXPLocation.getGeysers().size()==1)
				{
					continue;
				}
			}
			else if(numberOfMyCombatUnitTrainingBuilding==3)
			{
				return StrategyManager.Instance().myMainBaseLocation;
			}
			else if(EXPLocation.getGeysers().size()==0)
			{
				continue;
			}
			*/

			for (BaseLocation myBaseLocation : myBaseLocations) {
				distanceFromMyLocation = EXPLocation.getGroundDistance(myBaseLocation);

				for (BaseLocation enemyBaseLocation : enemyBaseLocations) {
					distanceFromEnemyLocation = EXPLocation.getGroundDistance(enemyBaseLocation);

					if (minDistance > distanceFromMyLocation && distanceFromMyLocation < distanceFromEnemyLocation) 
					{
							minDistance = distanceFromMyLocation;
							nextEXP = EXPLocation;
					
					}
				}
			}
		}
		
		return nextEXP;
	}

	private static BuildOrder_Expansion instance = new BuildOrder_Expansion();

	/// static singleton 객체를 리턴합니다
	public static BuildOrder_Expansion Instance() {
		return instance;
	}

}
