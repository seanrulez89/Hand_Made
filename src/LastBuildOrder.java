import java.util.*;
import java.io.*;

import bwapi.*;
import bwta.*;

public class LastBuildOrder {
	
	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	
	
	Player myPlayer = SM.myPlayer;
	Race myRace = SM.myRace;

	Player enemyPlayer = SM.enemyPlayer;
	Race enemyRace = SM.enemyRace;
	
	private static LastBuildOrder instance = new LastBuildOrder();

	/// static singleton 객체를 리턴합니다
	public static LastBuildOrder Instance() {
		return instance;
	}
	
	
	
	public void lastBuildOrder() {

		if (myPlayer.getUpgradeLevel(UpgradeType.Metabolic_Boost) == 1) {

			int chamberNumber = myPlayer.allUnitCount(UnitType.Zerg_Evolution_Chamber)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Evolution_Chamber)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Evolution_Chamber,	null);

			if (chamberNumber == 0) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Evolution_Chamber,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			}

			if (myPlayer.completedUnitCount(UnitType.Zerg_Evolution_Chamber) == 1) {
				if (myPlayer.getUpgradeLevel(UpgradeType.Zerg_Carapace) < 4
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Carapace) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Zerg_Carapace) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Zerg_Carapace, false);
				}
			}

		}
		
		if (myPlayer.getUpgradeLevel(UpgradeType.Zerg_Flyer_Attacks) >= 1)
		{
			int queensNestNumber = myPlayer.allUnitCount(UnitType.Zerg_Queens_Nest)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Queens_Nest)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Queens_Nest, null);
			
			int hiveNumber = myPlayer.allUnitCount(UnitType.Zerg_Hive)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hive)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Hive, null);
			
			int greaterSpireNumber = myPlayer.allUnitCount(UnitType.Zerg_Greater_Spire)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Greater_Spire)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Greater_Spire, null);
			
			int ultraliskCavernNumber = myPlayer.allUnitCount(UnitType.Zerg_Ultralisk_Cavern)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Ultralisk_Cavern)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Ultralisk_Cavern, null);
			
			if (queensNestNumber == 0 && BuildManager.Instance().getAvailableMinerals() > 500) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Queens_Nest,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Spire,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			}
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Queens_Nest) == 1 && hiveNumber == 0 && BuildManager.Instance().getAvailableMinerals() > 500)
			{
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hive,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);	
			}
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hive) == 1)
			{
				if (myPlayer.getUpgradeLevel(UpgradeType.Adrenal_Glands) == 0
						&& myPlayer.isUpgrading(UpgradeType.Adrenal_Glands) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Adrenal_Glands) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Adrenal_Glands, false);
				}
				
				

			
			}	
			

			
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hive) == 1 && greaterSpireNumber < 2)
			{
			//	BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Greater_Spire,
			//			BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
				
			}
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hive) == 1 && ultraliskCavernNumber == 0)
			{
			
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Ultralisk_Cavern,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				
			}
				
			if (myPlayer.completedUnitCount(UnitType.Zerg_Ultralisk_Cavern) == 1)
			{
				if (myPlayer.getUpgradeLevel(UpgradeType.Chitinous_Plating) == 0
						&& myPlayer.isUpgrading(UpgradeType.Chitinous_Plating) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Chitinous_Plating) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Chitinous_Plating, false);
				}
				
				
				if (myPlayer.getUpgradeLevel(UpgradeType.Anabolic_Synthesis) == 0
						&& myPlayer.isUpgrading(UpgradeType.Anabolic_Synthesis) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Anabolic_Synthesis) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Anabolic_Synthesis, false);
				}
			
			}	
			
			
		}
		
		
		
		
		

	}	
	
	
	
	
	
	
	

}
