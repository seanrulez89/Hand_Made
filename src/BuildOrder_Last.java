
import java.util.*;
import java.io.*;

import bwapi.*;
import bwta.*;

public class BuildOrder_Last {
	
	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	
	
	Player myPlayer = SM.myPlayer;
	Race myRace = SM.myRace;

	Player enemyPlayer = SM.enemyPlayer;
	Race enemyRace = SM.enemyRace;
	
	private static BuildOrder_Last instance = new BuildOrder_Last();

	/// static singleton 객체를 리턴합니다
	public static BuildOrder_Last Instance() {
		return instance;
	}
	
	
	
	public void lastBuildOrder() {
		// myPlayer.completedUnitCount(UnitType.Zerg_Hatchery)<4
		if(myPlayer.allUnitCount(UnitType.Zerg_Hatchery) < 4 || SM.isInitialBuildOrderFinished==false )
		{
			return;
		}
		
		
		
		int spireNumber = myPlayer.allUnitCount(UnitType.Zerg_Spire)
				+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Spire)
				+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Spire, null);
		
		int chamberNumber = myPlayer.allUnitCount(UnitType.Zerg_Evolution_Chamber)
				+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Evolution_Chamber)
				+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Evolution_Chamber,	null);

		
		if(spireNumber == 0) // myPlayer.hasResearched(TechType.Lurker_Aspect)==true &&
		{
			BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Spire,
					BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
			
			if (chamberNumber <3) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Evolution_Chamber,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Evolution_Chamber,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Evolution_Chamber,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

			}
		}
		
		if (myPlayer.completedUnitCount(UnitType.Zerg_Evolution_Chamber) >= 1) 
		{
			
			Unit chamber = null;
			for (Unit unit : MyBotModule.Broodwar.self().getUnits()) 
			{
				if(unit.getType().equals(UnitType.Zerg_Evolution_Chamber) && unit.isUpgrading()==false && unit.isCompleted() && unit!=null)
				{
					chamber = unit;
					break;
				}
			}
			
			if(chamber != null)
			{
				// 지상유닛 방어력
				if (myPlayer.getUpgradeLevel(UpgradeType.Zerg_Carapace) < 2
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Carapace) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Zerg_Carapace) == 0) 
				{
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UpgradeType.Zerg_Carapace, true, chamber.getID());
					return;
				}
				else if(myPlayer.getUpgradeLevel(UpgradeType.Zerg_Carapace) == 2
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Carapace) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Zerg_Carapace) == 0
						&& myPlayer.completedUnitCount(UnitType.Zerg_Hive)>0)
				{
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UpgradeType.Zerg_Carapace, false, chamber.getID());
					return;
				}
				
				// 지상유닛 근접공격력
				if (myPlayer.getUpgradeLevel(UpgradeType.Zerg_Melee_Attacks) < 2
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Melee_Attacks) == false
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Carapace) == true
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Zerg_Melee_Attacks) == 0) 
				{
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Zerg_Melee_Attacks, true, chamber.getID());
					return;
				}
				else if(myPlayer.getUpgradeLevel(UpgradeType.Zerg_Melee_Attacks) == 2
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Melee_Attacks) == false
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Carapace) == true
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Zerg_Melee_Attacks) == 0
						&& myPlayer.completedUnitCount(UnitType.Zerg_Hive)>0)
				{
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Zerg_Melee_Attacks, false, chamber.getID());
					return;
				}

				// 지상유닛 원거리공격력
				if (myPlayer.getUpgradeLevel(UpgradeType.Zerg_Missile_Attacks) < 2
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Missile_Attacks) == false
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Melee_Attacks) == true
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Zerg_Missile_Attacks) == 0) 
				{
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Zerg_Missile_Attacks, true, chamber.getID());
					return;
				}
				else if(myPlayer.getUpgradeLevel(UpgradeType.Zerg_Missile_Attacks) == 2
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Missile_Attacks) == false
						&& myPlayer.isUpgrading(UpgradeType.Zerg_Melee_Attacks) == true
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Zerg_Missile_Attacks) == 0
						&& myPlayer.completedUnitCount(UnitType.Zerg_Hive)>0)
				{
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Zerg_Missile_Attacks, false, chamber.getID());
					return;
				}
				
			}
			
			
			
			
			
		}
		
		//if (myPlayer.getUpgradeLevel(UpgradeType.Zerg_Flyer_Carapace) >= 1)
		if(myPlayer.completedUnitCount(UnitType.Zerg_Spire) >= 1)
			
		{

			
			int queensNestNumber = myPlayer.allUnitCount(UnitType.Zerg_Queens_Nest)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Queens_Nest)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Queens_Nest, null);
			
			int hiveNumber = myPlayer.allUnitCount(UnitType.Zerg_Hive)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hive)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Hive, null);
			
			/*int greaterSpireNumber = myPlayer.allUnitCount(UnitType.Zerg_Greater_Spire)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Greater_Spire)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Greater_Spire, null);
			*/
			
			int ultraliskCavernNumber = myPlayer.allUnitCount(UnitType.Zerg_Ultralisk_Cavern)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Ultralisk_Cavern)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Ultralisk_Cavern, null);
			
			int defilerMoundNumber = myPlayer.allUnitCount(UnitType.Zerg_Defiler_Mound)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Defiler_Mound)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Defiler_Mound, null);
			


			
			
			
			
			if (queensNestNumber == 0 && BuildManager.Instance().getAvailableMinerals() > UnitType.Zerg_Queens_Nest.mineralPrice()) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Queens_Nest,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				
	//			BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Spire,
	//					BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);

			}
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Queens_Nest) == 1 && hiveNumber == 0)
			{
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hive,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);	
			}
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hive) == 1)
			{
				if (myPlayer.getUpgradeLevel(UpgradeType.Adrenal_Glands) == 0
						&& myPlayer.isUpgrading(UpgradeType.Adrenal_Glands) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Adrenal_Glands) == 0) {
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UpgradeType.Adrenal_Glands, true);
				}
				
				

			
			}	
			

			
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hive) == 1 && spireNumber == 1)
			{
			//	BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Creep_Colony, true);	
			//	BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Spire, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			//	BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Spore_Colony, false);
			}
			
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hive) == 1 && defilerMoundNumber == 0)
			{
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Defiler_Mound,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
			}
			
			
			
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hive) == 1 && ultraliskCavernNumber == 0)
			{
			
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Ultralisk_Cavern,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
				
			}
			
			if (myPlayer.completedUnitCount(UnitType.Zerg_Defiler_Mound) == 1)
			{
				if (myPlayer.hasResearched(TechType.Dark_Swarm)==false
						&& myPlayer.isResearching(TechType.Dark_Swarm) == false
						&& BuildManager.Instance().buildQueue.getItemCount(TechType.Dark_Swarm) == 0) {
					BuildManager.Instance().buildQueue.queueAsHighestPriority(TechType.Dark_Swarm, true);
				}
			}
				
			
			
				
			if (myPlayer.completedUnitCount(UnitType.Zerg_Ultralisk_Cavern) == 1)
			{
				if (myPlayer.getUpgradeLevel(UpgradeType.Chitinous_Plating) == 0
						&& myPlayer.isUpgrading(UpgradeType.Chitinous_Plating) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Chitinous_Plating) == 0) {
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UpgradeType.Chitinous_Plating, true);
				}
				
				
				if (myPlayer.getUpgradeLevel(UpgradeType.Anabolic_Synthesis) == 0
						&& myPlayer.getUpgradeLevel(UpgradeType.Chitinous_Plating) == 1
						&& myPlayer.isUpgrading(UpgradeType.Anabolic_Synthesis) == false
						&& BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Anabolic_Synthesis) == 0) {
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Anabolic_Synthesis, false);
				}
			
			}	
			
			
		}
		
		
		
		
		

	}	
	
	
	
	
	
	
	

}
