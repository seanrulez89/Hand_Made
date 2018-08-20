
import java.util.*;
import java.io.*;

import bwapi.*;
import bwta.*;

public class BuildOrder_Initial {

	static Position hatcheryPosition = null;
	static Position creepColony_FIRST = null;
	static Position creepColony_SECOND = null;
	
	
	
	// 최초 빌드 : 9오버풀 2해처리 빠른 뮤탈리스크 6마리 5분 20초 생성
	public void setInitialBuildOrder() {

		
		int Mx = StrategyManager.Instance().myMainBaseLocation.getX();
		int Cx = StrategyManager.Instance().myFirstChokePoint.getX();
		
		int My = StrategyManager.Instance().myMainBaseLocation.getY();
		int Cy = StrategyManager.Instance().myFirstChokePoint.getY();
		
		Position between = new Position((Mx+Cx)/2, (My+Cy)/2);
		
		simCity();
		


		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hatchery, between.toTilePosition(), true); // hatcheryPosition
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Spawning_Pool,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		
		

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		/*		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hatchery, 
				new Position((StrategyManager.Instance().myMainBaseLocation.getX()+StrategyManager.Instance().myFirstChokePoint.getX())/2, 
						(StrategyManager.Instance().myMainBaseLocation.getY()+StrategyManager.Instance().myFirstChokePoint.getY())/2 ).toTilePosition(), true);
	*/
		
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Creep_Colony,
				BuildOrderItem.SeedPositionStrategy.FirstChokePoint, true); // creepColony_FIRST
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Creep_Colony,
				BuildOrderItem.SeedPositionStrategy.FirstChokePoint, true); // creepColony_SECOND
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Sunken_Colony, false);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Sunken_Colony, false);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Extractor,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true); 
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk_Den,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		//
		
		
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		
		
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		
		
		
		
		

		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Grooved_Spines, true);
		
		
		

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Lair,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Muscular_Augments, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		
		
		/*
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
*/
		
		
		
		/*
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		*/

		
		
/*/// 기존 빌드
 * 		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Spawning_Pool,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		

		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hatchery,
				BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, true);


		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Extractor,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hatchery,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		

		 
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		
		

		
		
		
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk_Den,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Grooved_Spines, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Zergling,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		


		
		

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);


		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Overlord,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);



		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		

		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Creep_Colony,
				BuildOrderItem.SeedPositionStrategy.SecondChokePoint);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Creep_Colony,
				BuildOrderItem.SeedPositionStrategy.SecondChokePoint);
		
		
		

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);




		

		


		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hydralisk,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);


		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Sunken_Colony,
				BuildOrderItem.SeedPositionStrategy.SecondChokePoint);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Sunken_Colony,
				BuildOrderItem.SeedPositionStrategy.SecondChokePoint);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.Instance().getWorkerType(),
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);

		BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Muscular_Augments, true);	
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Lair,
				BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		
		BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Extractor,
				BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, true);*/
		

		

		
		

		
		




		// 저글링 이동속도 업

		// BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Metabolic_Boost);

		// 오버로드 속도 증가

		// BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Pneumatized_Carapace);

		/*
		 * 
		 * // 드론, 일꾼
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.
		 * Instance().getWorkerType(),
		 * 
		 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		 * 
		 * 
		 * 
		 * // 저글링
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Zergling,
		 * 
		 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		 * 
		 * 
		 * 
		 * // 오버로드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.
		 * Instance().getBasicSupplyProviderUnitType(),
		 * 
		 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
		 * 
		 * 
		 * 
		 * // 해처리
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Hatchery,
		 * 
		 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation);
		 * 
		 * 
		 * 
		 * // 가스 익스트랙터
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Extractor,
		 * 
		 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation);
		 * 
		 * 
		 * 
		 * // 크립 콜로니
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Creep_Colony,
		 * 
		 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation);
		 * 
		 * 
		 * 
		 * // 성큰 콜로니 권순우 0617 크립 콜로니가 여럿이면 어느 것에서 지어질까???
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Sunken_Colony,
		 * 
		 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation);
		 * 
		 * 
		 * 
		 * // 스포어 코로니 권순우 0617 크립 콜로니가 여럿이면 어느 것에서 지어질까???
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Spore_Colony,
		 * 
		 * BuildOrderItem.SeedPositionStrategy.MainBaseLocation);
		 * 
		 * 
		 * 
		 * // 권순우 0617 이것도 가스 익스트랙터인데 위치를 지정하지 않는 경우 -> 어디에 건설될까???
		 * 
		 * BuildManager.Instance().buildQueue
		 * 
		 * .queueAsLowestPriority(InformationManager.Instance().getRefineryBuildingType(
		 * ));
		 * 
		 * 
		 * 
		 * // 저글링 이동속도 업
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Metabolic_Boost);
		 * 
		 * 
		 * 
		 * // 에볼루션 챔버
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Evolution_Chamber);
		 * 
		 * 
		 * 
		 * // 에볼루션 챔버 . 지상유닛 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Zerg_Melee_Attacks, false);
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Zerg_Missile_Attacks, false);
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Zerg_Carapace, false);
		 * 
		 * 
		 * 
		 * // 권순우 0617 이것도 드론인데 위치를 지정하지 않는 경우 -> 어느 해처리의 라바(애벌레)에서 생산될까???
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(InformationManager.
		 * Instance().getWorkerType());
		 * 
		 * 
		 * 
		 * // 히드라리스크 덴(건물)
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Hydralisk_Den);
		 * 
		 * 
		 * 
		 * // 히드라리스크
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Hydralisk);
		 * 
		 * 
		 * 
		 * // 히드라 이동속도 업
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Muscular_Augments, false);
		 * 
		 * 
		 * 
		 * // 히드라 공격 사정거리 업
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Grooved_Spines, false);
		 * 
		 * 
		 * 
		 * // 레어
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Lair);
		 * 
		 * 
		 * 
		 * // 오버로드 운반가능
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Ventral_Sacs);
		 * 
		 * 
		 * 
		 * // 오버로드 시야 증가
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Antennae
		 * );
		 * 
		 * 
		 * 
		 * // 오버로드 속도 증가
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Pneumatized_Carapace);
		 * 
		 * 
		 * 
		 * // 럴커 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
		 * Lurker_Aspect);
		 * 
		 * 
		 * 
		 * // 럴커 생산 권순우 0617 어느 히드라가 변하는거야?
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Lurker
		 * );
		 * 
		 * 
		 * 
		 * // 스파이어
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Spire,
		 * true);
		 * 
		 * 
		 * 
		 * // 스파이어 공중유닛 공격력 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Zerg_Flyer_Attacks, false);
		 * 
		 * 
		 * 
		 * // 스파이어 공중유닛 방어력 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Zerg_Flyer_Carapace, false);
		 * 
		 * 
		 * 
		 * // 뮤탈리스크
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Mutalisk, true);
		 * 
		 * 
		 * 
		 * // 스커지
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Scourge, true);
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * // 퀸스 네스트 (건물)
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Queens_Nest);
		 * 
		 * 
		 * 
		 * // 퀸
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Queen)
		 * ;
		 * 
		 * 
		 * 
		 * // 퀸 브루드링 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.
		 * Spawn_Broodlings, false);
		 * 
		 * 
		 * 
		 * // 퀸 인스네어 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Ensnare,
		 * false);
		 * 
		 * 
		 * 
		 * // 퀸 마나 업그레이드? 이런거 쓰긴 쓰나
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Gamete_Meiosis, false);
		 * 
		 * 
		 * 
		 * // 하이브
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hive);
		 * 
		 * 
		 * 
		 * // 저글링 공격 속도 업
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Adrenal_Glands, false);
		 * 
		 * 
		 * 
		 * // 그레이트 스파이어
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Greater_Spire, true);
		 * 
		 * 
		 * 
		 * // 가디언
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Guardian, true);
		 * 
		 * 
		 * 
		 * // 디바우러
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Devourer, true);
		 * 
		 * 
		 * 
		 * // 울트라리스크 동굴
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Ultralisk_Cavern);
		 * 
		 * 
		 * 
		 * // 울트라리스크
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Ultralisk);
		 * 
		 * 
		 * 
		 * // 울트라리스크 이동속도 업
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Anabolic_Synthesis, false);
		 * 
		 * 
		 * 
		 * // 울트라리스크 방어력 업
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Chitinous_Plating, false);
		 * 
		 * 
		 * 
		 * // 디파일러 마운드 (건물)
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Defiler_Mound);
		 * 
		 * 
		 * 
		 * // 디파일러
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Defiler);
		 * 
		 * 
		 * 
		 * // 디파일러 콘슘 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Consume,
		 * false);
		 * 
		 * 
		 * 
		 * // 디파일러 플라그 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(TechType.Plague,
		 * false);
		 * 
		 * 
		 * 
		 * // 디파일러 에너지 업그레이드
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.
		 * Metasynaptic_Node, false);
		 * 
		 * 
		 * 
		 * // 나이더스 캐널
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Nydus_Canal);
		 * 
		 * 
		 * 
		 * // 참고로, Zerg_Nydus_Canal 건물로부터 Nydus Canal Exit를 만드는 방법은 다음과 같습니다
		 * 
		 * //if
		 * (MyBotModule.Broodwar.self().completedUnitCount(UnitType.Zerg_Nydus_Canal) >
		 * 0) {
		 * 
		 * // for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
		 * 
		 * // if (unit.getType() == UnitType.Zerg_Nydus_Canal) {
		 * 
		 * // TilePosition targetTilePosition = new
		 * TilePosition(unit.getTilePosition().getX() + 6,
		 * unit.getTilePosition().getY()); // Creep 이 있는 곳이어야 한다
		 * 
		 * // unit.build(UnitType.Zerg_Nydus_Canal, targetTilePosition);
		 * 
		 * // }
		 * 
		 * // }
		 * 
		 * //}
		 * 
		 * 
		 * 
		 * // 퀸 - 인페스티드 테란 : 테란 Terran_Command_Center 건물의 HitPoint가 낮을 때, 퀸을 들여보내서
		 * Zerg_Infested_Command_Center 로 바꾸면, 그 건물에서 실행 됨
		 * 
		 * BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.
		 * Zerg_Infested_Terran);
		 * 
		 */

	}
	
	public void simCity ()
	{
		StrategyManager SM = StrategyManager.Instance();
		
		// 몇시 방향이냐가 아니고 4분면에서 몇사분면인가를 판단
		int enemyMainBasePosition = 0;
		
		if(SM.myMainBaseLocation.getX()/32 > 63 && SM.myMainBaseLocation.getY()/32 > 63)
		{
			enemyMainBasePosition = 4;
		}
		else if(SM.myMainBaseLocation.getX()/32 < 63 && SM.myMainBaseLocation.getY()/32 > 63)
		{
			enemyMainBasePosition = 3;
		}
		else if(SM.myMainBaseLocation.getX()/32 < 63 && SM.myMainBaseLocation.getY()/32 < 63)
		{
			enemyMainBasePosition = 2;
		}
		else
		{
			enemyMainBasePosition = 1;
		}
		
		
		
		if(MyBotModule.Broodwar.mapFileName().equals("(4)CircuitBreaker.scx"))
		{
			if(enemyMainBasePosition == 1)
			{
				hatcheryPosition = SM.myMainBaseLocation.getPosition();
				creepColony_FIRST = SM.myMainBaseLocation.getPosition();
				creepColony_SECOND = SM.myMainBaseLocation.getPosition();			
				//gatherPoint = new Position(42*32,3*32);
			}
			else if(enemyMainBasePosition == 2)
			{
				hatcheryPosition = SM.myMainBaseLocation.getPosition();
				creepColony_FIRST = SM.myMainBaseLocation.getPosition();
				creepColony_SECOND = SM.myMainBaseLocation.getPosition();			
				//gatherPoint = new Position(42*32,3*32);
			}
			else if(enemyMainBasePosition == 3)
			{
				hatcheryPosition = SM.myMainBaseLocation.getPosition();
				creepColony_FIRST = SM.myMainBaseLocation.getPosition();
				creepColony_SECOND = SM.myMainBaseLocation.getPosition();			
				//gatherPoint = new Position(42*32,3*32);
			}
			else
			{
				hatcheryPosition = SM.myMainBaseLocation.getPosition();
				creepColony_FIRST = SM.myMainBaseLocation.getPosition();
				creepColony_SECOND = SM.myMainBaseLocation.getPosition();			
				//gatherPoint = new Position(42*32,3*32);
			}	
		}
		else
		{
			if(enemyMainBasePosition == 1)
			{
				hatcheryPosition = SM.myMainBaseLocation.getPosition();
				creepColony_FIRST = SM.myMainBaseLocation.getPosition();
				creepColony_SECOND = SM.myMainBaseLocation.getPosition();			
				//gatherPoint = new Position(42*32,3*32);
			}
			else if(enemyMainBasePosition == 2)
			{
				hatcheryPosition = SM.myMainBaseLocation.getPosition();
				creepColony_FIRST = SM.myMainBaseLocation.getPosition();
				creepColony_SECOND = SM.myMainBaseLocation.getPosition();			
				//gatherPoint = new Position(42*32,3*32);
			}
			else if(enemyMainBasePosition == 3)
			{
				hatcheryPosition = SM.myMainBaseLocation.getPosition();
				creepColony_FIRST = SM.myMainBaseLocation.getPosition();
				creepColony_SECOND = SM.myMainBaseLocation.getPosition();			
				//gatherPoint = new Position(42*32,3*32);
			}
			else
			{
				hatcheryPosition = SM.myMainBaseLocation.getPosition();
				creepColony_FIRST = SM.myMainBaseLocation.getPosition();
				creepColony_SECOND = SM.myMainBaseLocation.getPosition();			
				//gatherPoint = new Position(42*32,3*32);
			}	
			
			
		}
		
		
		
		
	}
	

}
