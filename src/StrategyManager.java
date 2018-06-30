import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

//import StrategyManager.CombatState;

import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

public class StrategyManager {

	boolean isEXP = true;
	BaseLocation nextEXP;
	Expansion myExpansion;
	Position attackTargetPosition;

	
	Mutalisk mutalisk;
	boolean mutalisk_start;

	boolean Mutal_flag;

	// 아군
	Player myPlayer;
	Race myRace;

	// 적군
	Player enemyPlayer;
	Race enemyRace;

	int[] buildOrderArrayOfMyCombatUnitType; /// 아군 공격 유닛 생산 순서
	int nextTargetIndexOfBuildOrderArray; /// buildOrderArrayMyCombatUnitType 에서 다음 생산대상 아군 공격 유닛

	// 아군 공격 유닛 종류(타입)
	UnitType myCombatUnitType1; /// 저글링
	UnitType myCombatUnitType2; /// 뮤탈리스크
	UnitType myCombatUnitType3; /// #미정
	UnitType myCombatUnitType4; /// #미정
	UnitType myCombatUnitType5; /// #미정

	// 아군 특수 유닛 첫번째, 두번째 타입
	UnitType mySpecialUnitType1; /// 옵저버 사이언스베쓸 오버로드
	UnitType mySpecialUnitType2; /// 하이템플러 배틀크루저 디파일러

	// 아군의 공격유닛 최소 필요 숫자
	int necessaryNumberOfCombatUnitType1; /// 공격을 시작하기위해 필요한 최소한의 유닛 숫자
	int necessaryNumberOfCombatUnitType2; /// 공격을 시작하기위해 필요한 최소한의 유닛 숫자
	int necessaryNumberOfCombatUnitType3; /// 공격을 시작하기위해 필요한 최소한의 유닛 숫자
	int necessaryNumberOfCombatUnitType4; /// 공격을 시작하기위해 필요한 최소한의 유닛 숫자
	int necessaryNumberOfCombatUnitType5;

	int myKilledCombatUnitCount1; /// 첫번째 유닛 타입의 사망자 숫자 누적값
	int myKilledCombatUnitCount2; /// 두번째 유닛 타입의 사망자 숫자 누적값
	int myKilledCombatUnitCount3;
	int myKilledCombatUnitCount4;
	int myKilledCombatUnitCount5;

	int numberOfCompletedCombatUnitType1; /// 첫번째 유닛 타입의 현재 유닛 숫자
	int numberOfCompletedCombatUnitType2; /// 두번째 유닛 타입의 현재 유닛 숫자
	int numberOfCompletedCombatUnitType3; /// 세번째 유닛 타입의 현재 유닛 숫자
	int numberOfCompletedCombatUnitType4; /// 네번째 유닛 타입의 현재 유닛 숫자
	int numberOfCompletedCombatUnitType5;

	int MaxNumberOfCombatUnitType4; /// 생산 숫자 제한

	int myKilledUnitCount1; /// 첫번째 유닛 타입의 사망자 숫자 누적값
	int myKilledUnitCount2; /// 두번째 유닛 타입의 사망자 숫자 누적값
	int myKilledUnitCount3;
	int myKilledUnitCount4;

	// 아군 공격 유닛 목록
	ArrayList<Unit> myAllCombatUnitList = new ArrayList<Unit>();
	ArrayList<Unit> myCombatUnitType1List = new ArrayList<Unit>(); // 저글링
	ArrayList<Unit> myCombatUnitType2List = new ArrayList<Unit>(); // 뮤탈리스크
	ArrayList<Unit> myCombatUnitType3List = new ArrayList<Unit>(); // #미정
	ArrayList<Unit> myCombatUnitType4List = new ArrayList<Unit>(); // #미정
	ArrayList<Unit> myCombatUnitType5List = new ArrayList<Unit>(); // #미정

	// 아군 방어 건물 첫번째 타입
	UnitType myDefenseBuildingType1; /// 파일런 벙커 크립콜로니

	// 아군 방어 건물 두번째 타입
	UnitType myDefenseBuildingType2; /// 포톤 터렛 성큰콜로니

	// 아군 방어 건물 건설 숫자
	int necessaryNumberOfDefenseBuilding1; /// 방어 건물 건설 갯수
	int necessaryNumberOfDefenseBuilding2; /// 방어 건물 건설 갯수

	// 아군 방어 건물 건설 위치
	BuildOrderItem.SeedPositionStrategy seedPositionStrategyOfMyDefenseBuildingType; // 권순우 0617 현상태의 전략 거점이 어딘가를
																						// 판단하는것으로 공격 방어와 연계되어야 함
	BuildOrderItem.SeedPositionStrategy seedPositionStrategyOfMyCombatUnitTrainingBuildingType;
	BuildOrderItem.SeedPositionStrategy buildAtFirstChokePoint;

	// 아군 방어 건물 목록
	ArrayList<Unit> myDefenseBuildingType1List = new ArrayList<Unit>(); // 파일런 벙커 크립
	ArrayList<Unit> myDefenseBuildingType2List = new ArrayList<Unit>(); // 캐논 터렛 성큰

	// 업그레이드 / 리서치 할 것
	UpgradeType necessaryUpgradeType1; ///
	UpgradeType necessaryUpgradeType2; ///
	UpgradeType necessaryUpgradeType3;
	UpgradeType necessaryUpgradeType4;

	TechType necessaryTechType1; ///
	TechType necessaryTechType2; ///
	TechType necessaryTechType3; ///

	// 적군 공격 유닛 숫자
	int numberOfCompletedEnemyCombatUnit;
	int numberOfCompletedEnemyWorkerUnit;

	// 적군 공격 유닛 사망자 수
	int enemyKilledCombatUnitCount; /// 적군 공격유닛 사망자 숫자 누적값
	int enemyKilledWorkerUnitCount; /// 적군 일꾼유닛 사망자 숫자 누적값

	// 아군 / 적군의 본진, 첫번째 길목, 두번째 길목
	BaseLocation myMainBaseLocation;
	BaseLocation myFirstExpansionLocation;

	Chokepoint myFirstChokePoint;
	Chokepoint mySecondChokePoint;

	BaseLocation enemyMainBaseLocation;
	BaseLocation enemyFirstExpansionLocation;

	Chokepoint enemyFirstChokePoint;
	Chokepoint enemySecondChokePoint;

	boolean isInitialBuildOrderFinished; /// setInitialBuildOrder 에서 입력한 빌드오더가 다 끝나서 빌드오더큐가 empty 되었는지 여부

	enum CombatState {
		defenseMode, // 아군 진지 방어
		attackStarted, // 적 공격 시작
		attackMySecondChokepoint, // 아군 두번째 길목까지 공격
		attackEnemySecondChokepoint, // 적진 두번째 길목까지 공격
		attackEnemyFirstExpansionLocation, // 적진 앞마당까지 공격
		attackEnemyFirstChokepoint, attackEnemyMainBaseLocation, // 적진 본진까지 공격
		eliminateEnemy // 적 Eliminate
	};

	CombatState combatState; /// 전투 상황

	boolean moveToFirstChokePoint = false;
	boolean moveToEnemyBaseLocation = false;
	boolean buildKey;

	public StrategyManager() {

	}

	/// 경기가 시작될 때 일회적으로 전략 초기 세팅 관련 로직을 실행합니다

	public void onStart() {

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////

		// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가
		// 과거 게임 기록을 로딩합니다
		// loadGameRecordList();

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		/// 변수 초기값을 설정합니다
		setVariables();
		/// 게임 초기에 사용할 빌드오더를 세팅합니다

		InitialBuildOrder initialBuildOrder = new InitialBuildOrder();
		initialBuildOrder.setInitialBuildOrder();

		// System.out.println(enemyRace);

		// expansion ();

	}

	/// 변수 초기값을 설정합니다
	void setVariables() {
		// 참가자께서 자유롭게 초기값을 수정하셔도 됩니다

		isEXP = true;
		Mutal_flag = false;
		
		mutalisk = new Mutalisk();
		mutalisk_start = false;
		attackTargetPosition = null;
		

		myPlayer = MyBotModule.Broodwar.self();
		myRace = MyBotModule.Broodwar.self().getRace();
		enemyPlayer = InformationManager.Instance().enemyPlayer;

		myKilledCombatUnitCount1 = 0; // 죽은 마린 숫자
		myKilledCombatUnitCount2 = 0;
		myKilledCombatUnitCount3 = 0;
		myKilledCombatUnitCount4 = 0;
		myKilledCombatUnitCount5 = 0;

		numberOfCompletedEnemyCombatUnit = 0;
		numberOfCompletedEnemyWorkerUnit = 0;

		enemyKilledCombatUnitCount = 0;
		enemyKilledWorkerUnitCount = 0;

		isInitialBuildOrderFinished = false;
		combatState = CombatState.defenseMode;

		if (myRace == Race.Zerg) {

			// 공격 유닛 종류 설정
			myCombatUnitType1 = UnitType.Zerg_Zergling;
			myCombatUnitType2 = UnitType.Zerg_Mutalisk;
			// myCombatUnitType3 = UnitType.Terran_Siege_Tank_Tank_Mode;
			// myCombatUnitType4 = UnitType.Terran_Science_Vessel;
			// myCombatUnitType5 = UnitType.Terran_Wraith;

			// 특수 유닛 종류 설정
			mySpecialUnitType1 = UnitType.Zerg_Overlord;
			mySpecialUnitType2 = UnitType.Zerg_Defiler;

			// 공격 모드로 전환하기 위해 필요한 최소한의 유닛 숫자 설정
			necessaryNumberOfCombatUnitType1 = 12; // 공격을 시작하기위해 필요한 최소한의 마린 유닛 숫자
			necessaryNumberOfCombatUnitType2 = 8; // 공격을 시작하기위해 필요한 최소한의 메딕 유닛 숫자
			// necessaryNumberOfCombatUnitType3 = 2; // 공격을 시작하기위해 필요한 최소한의 메딕 유닛 숫자
			// necessaryNumberOfCombatUnitType4 = 1; // 공격을 시작하기위해 필요한 최소한의 메딕 유닛 숫자
			// necessaryNumberOfCombatUnitType5 = 2;

			// MaxNumberOfCombatUnitType4 = 4 ;

			// 공격 유닛 생산 순서 설정
			buildOrderArrayOfMyCombatUnitType = new int[] { 2, 2, 1 }; // 마린 마린 마린 메딕 시즈 베슬
			nextTargetIndexOfBuildOrderArray = 0; // 다음 생산 순서 index

			// 방어 건물 종류 및 건설 갯수 설정
			myDefenseBuildingType1 = UnitType.Zerg_Creep_Colony;
			necessaryNumberOfDefenseBuilding1 = 3;
			myDefenseBuildingType2 = UnitType.Zerg_Sunken_Colony;
			necessaryNumberOfDefenseBuilding2 = 3;

			// 방어 건물 건설 위치 설정
			seedPositionStrategyOfMyDefenseBuildingType = BuildOrderItem.SeedPositionStrategy.SecondChokePoint; // 앞마당
			seedPositionStrategyOfMyCombatUnitTrainingBuildingType = BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation; // 앞마당
			buildAtFirstChokePoint = BuildOrderItem.SeedPositionStrategy.FirstChokePoint;

			// 업그레이드 및 리서치 대상 설정
			necessaryUpgradeType1 = UpgradeType.Metabolic_Boost;
			necessaryUpgradeType2 = UpgradeType.Pneumatized_Carapace;
			necessaryUpgradeType3 = UpgradeType.Zerg_Flyer_Attacks; // 공중공업
			necessaryUpgradeType4 = UpgradeType.Zerg_Flyer_Carapace; // 공중방업

			necessaryTechType1 = TechType.Lurker_Aspect;
			necessaryTechType2 = TechType.Consume;
			necessaryTechType3 = TechType.Plague;
		}
	}

	/// 경기 진행 중 매 프레임마다 경기 전략 관련 로직을 실행합니다
	public void update() {

		/// 변수 값을 업데이트 합니다
		updateVariables();

		/// 일꾼을 계속 추가 생산합니다
		executeWorkerTraining();

		/// Supply DeadLock 예방 및 SupplyProvider 가 부족해질 상황 에 대한 선제적 대응으로서 SupplyProvider를
		/// 추가 건설/생산합니다
		executeSupplyManagement();

		/// 방어건물 및 공격유닛 생산 건물을 건설합니다
		executeBuildingConstruction();

		/// 업그레이드 및 테크 리서치를 실행합니다
		executeUpgradeAndTechResearch();

		/// 공격유닛을 계속 추가 생산합니다
		executeCombatUnitTraining();

		/// 전반적인 전투 로직 을 갖고 전투를 수행합니다
		executeCombat();

		/// StrategyManager 의 수행상황을 표시합니다
		/// drawStrategyManagerStatus();

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////

		// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가
		// 이번 게임의 로그를 남깁니다
		// saveGameLog();

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		// wraith();

		if (MyBotModule.Broodwar.getFrameCount() % 24 == 0)
		{
			myExpansion = Expansion.Instance(); // 0627 지속적인 정찰에 따라 업데이트 되는 내용을 반영하기 위해서 인스턴스를 계속 호출한다. 하지마 싱글톤이므로 결국 같은 인스턴스를 새로고침하는 셈이다.
			myExpansion.expansion();
			nextEXP = myExpansion.nextEXP;
		}
		
		
		if(enemyMainBaseLocation!=null && mutalisk_start == false)
		{
			attackTargetPosition = enemyFirstExpansionLocation.getPosition();
			mutalisk = new Mutalisk();
			mutalisk_start = true;
		}
		
/*		
		if (MyBotModule.Broodwar.getFrameCount() % 24 == 0 && mutalisk_start == true) { // 0627 일단 이렇게 했는데 공격시점에서는 조건을 어떻게 해줄까?
			
			//mutalisk = new Mutalisk();
			mutalisk.myMutal();
			//System.out.println(mutalisk.moveToEndPoint);
		}
*/	
		
		mutalisk.myMutal();
		
/*		if (MyBotModule.Broodwar.getFrameCount() % 24 == 0) { // 0627 일단 이렇게 했는데 공격시점에서는 조건을 어떻게 해줄까?
			
			//mutalisk = new Mutalisk();
			mutalisk.myMutal();
			//System.out.println(mutalisk.moveToEndPoint);
		}
*/		
		
		
		
		
		// enemyProtossTurret();

	}

	/*
	 * 권순우 0617 다음의 코드는 내가 테란, 상대가 프로토스일때, 다크템플러에 털릴까봐 긴급히 터렛을 추가하는 코드이다. 5드론 저글링을
	 * 상대하기 위해 변형해서 사용할 수 있을 것으로 보임
	 * 
	 * int engBaycnt = 0; public void enemyProtossTurret() { if (enemyPlayer == null
	 * || enemyRace == Race.Unknown) { return; }
	 * 
	 * if (enemyPlayer != null && enemyRace == Race.Protoss && engBaycnt ==0) {
	 * BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.
	 * Terran_Engineering_Bay, false); engBaycnt = 1; }
	 * 
	 * if (myPlayer.completedUnitCount(UnitType.Terran_Engineering_Bay) == 1 &&
	 * engBaycnt == 1) {
	 * BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.
	 * Terran_Missile_Turret, BuildOrderItem.SeedPositionStrategy.SecondChokePoint,
	 * false); }
	 * 
	 * return; }
	 */

	/*
	 * 권순우 0617 테란 레이스 은신 개발 관련코드이다. 저그라서 필요없는데 혹시 어디 써먹을까 싶어서 남겨둔다.
	 * 
	 * public void wraith () { if(myPlayer.minerals() > 150 && myPlayer.gas() > 150)
	 * { if (myPlayer.isResearching(TechType.Cloaking_Field) == false &&
	 * myPlayer.hasResearched(TechType.Cloaking_Field) == false &&
	 * BuildManager.Instance().buildQueue.getItemCount(TechType.Cloaking_Field) ==
	 * 0) { if (myPlayer.completedUnitCount(UnitType.Terran_Control_Tower) > 0 &&
	 * myPlayer.completedUnitCount(UnitType.Terran_Wraith) > 0) {
	 * BuildManager.Instance().buildQueue.queueAsHighestPriority(TechType.
	 * Cloaking_Field, true); } } } }
	 */

	/// 전반적인 전투 로직 을 갖고 전투를 수행합니다
	/*
	 * 권순우 0617 방어 / 공격 / 앨리(elimination) 3 단계로 구분하였으며 방어모드에서는 공격여부를 판단하고 공격모드에서는
	 * 방어여부나 앨리여부를 판단한다. 현재 상태에서 어떤 상태로 넘어갈지를 판단한다.
	 */
	public void executeCombat() {

		int y = 250;

		// 공격을 시작할만한 상황이 되기 전까지는 방어를 합니다
		if (combatState == CombatState.defenseMode) {

			/// 아군 공격유닛 들에게 방어를 지시합니다
			MyBotModule.Broodwar.drawTextScreen(100, y, "Defense Mode");

			commandMyCombatUnitToDefense();

			// commandMyCombatUnitToDefense();
			// MUTAL();

			/// 공격 모드로 전환할 때인지 여부를 판단합니다
			if (isTimeToStartAttack()) {
				MyBotModule.Broodwar.drawTextScreen(100, y, "Attack Mode" + attack_cnt);
				combatState = CombatState.attackStarted;
			}
		}

		// 공격을 시작한 후에는 공격을 계속 실행하다가, 거의 적군 기지를 파괴하면 Eliminate 시키기를 합니다
		else if (combatState == CombatState.attackStarted) {

			/// 아군 공격유닛 들에게 공격을 지시합니다
			MyBotModule.Broodwar.drawTextScreen(100, y, "Attack Mode" + attack_cnt);
			commandMyCombatUnitToAttack();

			/// 방어 모드로 전환할 때인지 여부를 판단합니다
			if (isTimeToStartDefense()) {
				MyBotModule.Broodwar.drawTextScreen(100, y, "Defense Mode");
				combatState = CombatState.defenseMode;

			}

			/// 적군을 Eliminate 시키는 모드로 전환할지 여부를 판단합니다
			if (isTimeToStartElimination()) {
				MyBotModule.Broodwar.drawTextScreen(100, y, "Eliminate Mode");
				combatState = CombatState.eliminateEnemy;
			}
		} else if (combatState == CombatState.eliminateEnemy) {

			/// 적군을 Eliminate 시키도록 아군 공격 유닛들에게 지시합니다
			MyBotModule.Broodwar.drawTextScreen(100, y, "Eliminate Mode");
			commandMyCombatUnitToEliminate();
		}
	}

	///////

	/// 공격 모드로 전환할 때인지 여부를 리턴합니다
	int attack_cnt = 0;

	boolean isTimeToStartAttack() {

		MyBotModule.Broodwar.drawTextScreen(100, 240, "Wave Count : " + attack_cnt);
		// 유닛 종류별로 최소 숫자 이상 있으면

		if (attack_cnt == 0) {
			if (myCombatUnitType1List.size() >= necessaryNumberOfCombatUnitType1
					&& myCombatUnitType2List.size() >= necessaryNumberOfCombatUnitType2) {
				attack_cnt = attack_cnt + 1;
				return true; // first wave
			}
		} else if (myCombatUnitType1List.size() >= necessaryNumberOfCombatUnitType1
				&& myCombatUnitType2List.size() >= necessaryNumberOfCombatUnitType2)
		// && myCombatUnitType3List.size() >= necessaryNumberOfCombatUnitType3)
		{
			// 공격 유닛이 30 이상 있으면
			if (myCombatUnitType1List.size() + myCombatUnitType2List.size() > 30) {
				attack_cnt = attack_cnt + 1;
				if (attack_cnt >= 2 && isInitialBuildOrderFinished == true) {
					// 커맨드 짓자
					List<BaseLocation> startLocation = BWTA.getStartLocations();
					int size = startLocation.size();

					
					/*
					for (int i = 1; i < size; i++) // 0이면 우리 기지에 짓더라 이게 무슨 순서가 있는건지... 0623 이거 좀 손봐라 기지만 계속 지어
					{
						if (MyBotModule.Broodwar.isExplored(startLocation.get(i).getTilePosition())) {
							InformationManager instance = new InformationManager();
							if (instance.hasBuildingAroundBaseLocation(startLocation.get(i), myPlayer, 200) == false) {
								if (!startLocation.get(i).equals(enemyMainBaseLocation)
										&& !startLocation.get(i).equals(myMainBaseLocation)) {
									BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hatchery,
											startLocation.get(i).getTilePosition(), false);
									BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Extractor,
											startLocation.get(i).getTilePosition(), false);
									// System.out.println(size);
									break;
								}
							}
						}

					}*/
				}
				return true;
			}
		}
		return false;
	}

	/// 방어 모드로 전환할 때인지 여부를 리턴합니다
	boolean isTimeToStartDefense() {

		// 공격 유닛 숫자가 10 미만으로 떨어지면 후퇴
		// if (myCombatUnitType1List.size() + myCombatUnitType2List.size() +
		// myCombatUnitType3List.size() < 10)

		if (myCombatUnitType1List.size() < 2) {
			Mutal_flag = false;
			return true;
		}

		int enemyUnitCountAroundMyMainBaseLocation = 0;

		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(myMainBaseLocation.getPosition(),
				8 * Config.TILE_SIZE)) {
			if (unit.getPlayer() == enemyPlayer) {
				enemyUnitCountAroundMyMainBaseLocation++;
			}
		}

		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(myFirstExpansionLocation.getPosition(),
				8 * Config.TILE_SIZE)) {
			if (unit.getPlayer() == enemyPlayer) {
				enemyUnitCountAroundMyMainBaseLocation++;
			}
		}

		if (enemyUnitCountAroundMyMainBaseLocation > 3) {
			Mutal_flag = false;
			return true;
		}

		return false;
	}

	/// 적군을 Eliminate 시키는 모드로 전환할지 여부를 리턴합니다
	boolean isTimeToStartElimination() {

		// 적군 유닛을 많이 죽였고, 아군 서플라이가 120 을 넘었으면
		if (enemyKilledCombatUnitCount >= 15 && enemyKilledWorkerUnitCount >= 5 && myPlayer.supplyUsed() > 60 * 2) {

			// 적군 본진에 아군 유닛이 30 이상 도착했으면 거의 게임 끝난 것
			int myUnitCountAroundEnemyMainBaseLocation = 0;
			for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(),
					10 * Config.TILE_SIZE)) {
				if (unit.getPlayer() == myPlayer) {
					myUnitCountAroundEnemyMainBaseLocation++;
				}
			}
			if (myUnitCountAroundEnemyMainBaseLocation > 20) {
				System.out.println("eli case 1");
				return true;
			}
		}

		if (MyBotModule.Broodwar.getFrameCount() > 40000 && myPlayer.supplyUsed() > 80 * 2) {
			System.out.println("eli case 2");
			return true;
		}

		/*
		 * InformationManager instance = new InformationManager();
		 * 
		 * if (myInstance.hasBuildingAroundBaseLocation(enemyMainBaseLocation,
		 * enemyPlayer, 200) == false && enemyKilledWorkerUnitCount >= 10) { return
		 * true; }
		 */
		return false;
	}

	/// 권순우 0617 방어를 하다가 적군에 끌어 당겨지지 않게
	/// 일정 범위 이상 나가면 다시 원래 자리로 돌아오는 코드가 필요함
	/// 아군 공격유닛 들에게 방어를 지시합니다
	void commandMyCombatUnitToDefense() {

		// 아군 방어 건물이 세워져있는 위치
		Position myDefenseBuildingPosition = null;
		Position myAttackedBuildingPosition = null; // 공격받는 건물의 위치가 저장될 변수 - 노승호 170807

		switch (seedPositionStrategyOfMyDefenseBuildingType) {
		case MainBaseLocation:
			myDefenseBuildingPosition = myMainBaseLocation.getPosition();
			break;
		case FirstChokePoint:
			myDefenseBuildingPosition = myFirstChokePoint.getCenter();
			break;
		case FirstExpansionLocation:
			myDefenseBuildingPosition = myFirstExpansionLocation.getPosition();
			break;
		case SecondChokePoint:
			myDefenseBuildingPosition = mySecondChokePoint.getCenter();
			break;
		default:
			myDefenseBuildingPosition = myMainBaseLocation.getPosition();
			break;
		}

		// 아군 공격유닛을 방어 건물이 세워져있는 위치로 배치시킵니다
		// 아군 공격유닛을 아군 방어 건물 뒤쪽에 배치시켰다가 적들이 방어 건물을 공격하기 시작했을 때 다함께 싸우게하면 더 좋을 것입니다
		for (Unit unit : myAllCombatUnitList) {
			if (unit == null || unit.exists() == false)
				continue;

			if(unit.getType() == UnitType.Zerg_Mutalisk) // 0627 뮤탈코드 테스트를 위한 건너뛰기 조치
			{
				continue;
			}
			
			boolean hasCommanded = false;

			for (Unit building : MyBotModule.Broodwar.self().getUnits()) {
				if ((building.getType().isBuilding() && building.isUnderAttack())
						|| (building.getType() == UnitType.Zerg_Drone && building.isUnderAttack())) // buliding.getHitPoints()
																									// <
																									// buliding.getType().maxHitPoints())
				{
					myAttackedBuildingPosition = building.getPosition();
					myAttackedBuildingPosition = setTargetPositionOne(myAttackedBuildingPosition, 80);

					List<Unit> nearbyunits = building.getUnitsInRadius(1000);

					ArrayList<Unit> nearbyenemy = new ArrayList<Unit>(); // 0622 여기서 초기화 안하면 에러남

					for (Unit unit222 : nearbyunits) {
						if (unit222.getPlayer() == enemyPlayer && unit222 != null && unit222.exists()) {
							nearbyenemy.add(unit222);
						}
					}

					double minDistance = 1000000000;
					double tempDistance = 0;
					Unit myAttackTarget = null;
					for (Unit targetENEMY : nearbyenemy) {

						if (targetENEMY == null || targetENEMY.exists() == false) {
							continue;
						}

						tempDistance = unit.getDistance(targetENEMY.getPosition());
						if (minDistance > tempDistance) {
							minDistance = tempDistance;
							myAttackTarget = targetENEMY;
						}

					}

					if (unit.canAttack() && myAttackTarget != null) {
						// commandUtil.attackMove(unit, myAttackedBuildingPosition);
						// commandUtil.attackUnit(unit, building);
						unit.attack(myAttackTarget);
					} else {
						commandUtil.attackMove(unit, myAttackedBuildingPosition);
					}

					// System.out.println("건물님 지키러 갑니다");
					hasCommanded = true;
				}
			} // 건물이 데미지를 입었으면 그쪽으로 이동한다 - 노승호 170807

			if (unit.getType() == UnitType.Zerg_Lurker) {
				hasCommanded = controlLurkerUnitType(unit);
			}
			if (unit.getType() == mySpecialUnitType1) {
				hasCommanded = controlSpecialUnitType1(unit);
			}
			if (unit.getType() == mySpecialUnitType2) {
				hasCommanded = controlSpecialUnitType2(unit);
			}

			// 따로 명령 내린 적이 없으면, 방어 건물 주위로 이동시킨다
			if (hasCommanded == false) {

				if (unit.isIdle()) {

					if (unit.canAttack()) {
						commandUtil.attackMove(unit, myDefenseBuildingPosition);
					} else {
						commandUtil.move(unit, myDefenseBuildingPosition);
					}
				}
			}
		}

		if (!isTimeToStartDefense()) {
			// Mutal_flag=false;
//			Mutalisk mu = new Mutalisk();
//			mu.myMutal();
		}

	}

	/// 러커 유닛에 대해 컨트롤 명령을 내립니다
	boolean controlLurkerUnitType(Unit unit) {

		boolean hasCommanded = false;

		// defenseMode 일 경우
		if (combatState == CombatState.defenseMode) {

			// 아군 방어 건물이 세워져있는 위치 주위에 버로우시켜놓는다
			Position myDefenseBuildingPosition = null;
			switch (seedPositionStrategyOfMyDefenseBuildingType) {
			case MainBaseLocation:
				myDefenseBuildingPosition = myMainBaseLocation.getPosition();
				break;
			case FirstChokePoint:
				myDefenseBuildingPosition = myFirstChokePoint.getCenter();
				break;
			case FirstExpansionLocation:
				myDefenseBuildingPosition = myFirstExpansionLocation.getPosition();
				break;
			case SecondChokePoint:
				myDefenseBuildingPosition = mySecondChokePoint.getCenter();
				break;
			default:
				myDefenseBuildingPosition = myMainBaseLocation.getPosition();
				break;
			}

			if (myDefenseBuildingPosition != null) {
				if (unit.isBurrowed() == false) {
					if (unit.getDistance(myDefenseBuildingPosition) < 5 * Config.TILE_SIZE) {
						unit.burrow();
						hasCommanded = true;
					}
				}
			}
		}
		// defenseMode 가 아닐 경우
		else {

			// 근처에 적 유닛이 있으면 버로우 시키고, 없으면 언버로우 시킨다
			Position nearEnemyUnitPosition = null;
			double tempDistance = 0;
			for (Unit enemyUnit : MyBotModule.Broodwar.enemy().getUnits()) {

				if (enemyUnit.isFlying())
					continue;

				tempDistance = unit.getDistance(enemyUnit.getPosition());
				if (tempDistance < 6 * Config.TILE_SIZE) {
					nearEnemyUnitPosition = enemyUnit.getPosition();
				}
			}

			if (unit.isBurrowed() == false) {

				if (nearEnemyUnitPosition != null) {
					unit.burrow();
					hasCommanded = true;
				}
			} else {
				if (nearEnemyUnitPosition == null) {
					unit.unburrow();
					hasCommanded = true;
				}
			}
		}

		return hasCommanded;
	}

	/// 첫번째 특수 유닛 타입의 유닛에 대해 컨트롤 명령을 입력합니다
	boolean controlSpecialUnitType1(Unit unit) {

		///////////////////////////////////////////////////////////////////
		///////////////////////// 아래의 코드를 수정해보세요 ///////////////////////
		//
		// TODO 1. 아군 옵저버/사이언스베슬/오버로드를 공격 유닛들과 함께 이동하게 하는 로직 (예상 개발시간 10분)
		//
		// 목표 : 첫번째 특수유닛 타입은 적군 투명 유닛 탐지 능력이 있고 시야가 넓은 옵저버/사이언스베슬/오버로드 입니다.
		//
		// 현재는 아군 옵저버/사이언스베슬/오버로드에게 따로 컨트롤 명령을 입력하지 않으면
		// 다른 공격유닛들과 동일하게 적군 본진을 향해 이동하도록 되어있습니다.
		//
		// 그러나 이렇게하면 적군 유닛들이 있는데도 무시하고 계속 이동하다가 사망하게 됩니다
		//
		// 아군 공격 유닛들의 목록 myCombatUnitType1List, myCombatUnitType2List,
		/////////////////////////////////////////////////////////////////// myCombatUnitType3List
		// 을 사용해서, 다른 아군 공격 유닛들과 함께 다니도록 해보세요
		//
		// return false = 유닛에게 따로 컨트롤 명령을 입력하지 않음 -> 다른 공격유닛과 동일하게 이동하도록 합니다
		// return true = 유닛에게 따로 컨트롤 명령을 입력했음
		//
		// Hint : myCombatUnitType1List 에서 랜덤하게 한 유닛을 선택해서 그 유닛을 따라다니게 하면 어떨까요?
		//
		///////////////////////////////////////////////////////////////////

		boolean hasCommanded = false;
		if (unit.getType() == UnitType.Protoss_Observer) {

			Position targetPosition = null;

			// targetPosition 을 적절히 정해서 이동시켜보세요

		} else if (unit.getType() == UnitType.Terran_Science_Vessel) {

			Position targetPosition = null;

			// targetPosition 을 적절히 정해서 이동시켜보세요

			if (unit.getEnergy() >= TechType.Defensive_Matrix.energyCost()) {

				Unit targetMyUnit = null;

				// targetMyUnit 을 적절히 정해보세요

				if (targetMyUnit != null) {
					unit.useTech(TechType.Defensive_Matrix, targetMyUnit);
					hasCommanded = true;
				}
			}

			if (unit.getEnergy() >= TechType.Irradiate.energyCost() && myPlayer.hasResearched(TechType.Irradiate)) {

				Unit targetEnemyUnit = null;

				// targetEnemyUnit 을 적절히 정해보세요

				if (targetEnemyUnit != null) {
					unit.useTech(TechType.Irradiate, targetEnemyUnit);
					hasCommanded = true;
				}
			}

		} else if (unit.getType() == UnitType.Zerg_Overlord) {

			Position targetPosition = null;

			// targetPosition 을 적절히 정해서 이동시켜보세요
		}

		return hasCommanded;
	}

	/// 두번째 특수 유닛 타입의 유닛에 대해 컨트롤 명령을 내립니다
	boolean controlSpecialUnitType2(Unit unit) {

		///////////////////////////////////////////////////////////////////
		///////////////////////// 아래의 코드를 수정해보세요 ///////////////////////
		//
		// TODO 2. 아군 하이템플러/배틀크루저/디파일러가 특수 기술을 사용하게 하는 로직 (예상 개발시간 20분)
		//
		// 목표 : 두번째 특수유닛 타입은 특수 기술을 갖고있는 하이템플러/배틀크루저/디파일러 입니다.
		//
		// 현재는 특수기술 사용 대상을 정하는 로직이 구현 안되어있습니다.
		//
		// 적군 유닛들의 목록 MyBotModule.Broodwar.enemy().getUnits() 을 사용하여
		// 특수 기술 사용 대상을 적절히 정하도록 해보세요
		//
		// return false = 유닛에게 따로 컨트롤 명령을 입력하지 않음 -> 다른 공격유닛과 동일하게 이동하도록 합니다
		// return true = 유닛에게 따로 컨트롤 명령을 입력했음
		//
		// 추가 : 테란 종족 첫번째 특수유닛 타입 사이언스베슬에 대해서도 특수 기술을 사용하게 하려면
		// controlSpecialUnitType1 함수를 수정하시면 됩니다
		//
		///////////////////////////////////////////////////////////////////

		boolean hasCommanded = false;

		// 프로토스 종족 하이템플러의 경우
		if (unit.getType() == UnitType.Protoss_High_Templar) {

			if (unit.getEnergy() >= TechType.Psionic_Storm.energyCost()
					&& myPlayer.hasResearched(TechType.Psionic_Storm)) {

				Position targetPosition = null;

				// targetPosition 을 적절히 정해보세요

				if (targetPosition != null) {
					unit.useTech(TechType.Psionic_Storm, targetPosition);
					hasCommanded = true;
				}
			}
		} else if (unit.getType() == UnitType.Terran_Battlecruiser) {

			if (unit.getEnergy() >= TechType.Yamato_Gun.energyCost() && myPlayer.hasResearched(TechType.Yamato_Gun)) {

				Unit targetEnemyUnit = null;

				// targetEnemyUnit 을 적절히 정해보세요

				if (targetEnemyUnit != null) {
					unit.useTech(TechType.Yamato_Gun, targetEnemyUnit);
					hasCommanded = true;
				}
			}
		} else if (unit.getType() == UnitType.Zerg_Defiler) {

			if (unit.getEnergy() < 200 && myPlayer.hasResearched(TechType.Consume)) {

				Unit targetMyUnit = null;

				// 가장 가까운 저글링을 컨슘 한다
				double minDistance = 1000000000;
				double tempDistance = 0;
				for (Unit zerglingUnit : myCombatUnitType1List) {
					tempDistance = unit.getDistance(zerglingUnit.getPosition());
					if (minDistance > tempDistance) {
						minDistance = tempDistance;
						targetMyUnit = zerglingUnit;
					}
				}

				if (targetMyUnit != null) {
					unit.useTech(TechType.Consume, targetMyUnit);
					hasCommanded = true;
				}
			}

			if (unit.getEnergy() >= TechType.Plague.energyCost() && myPlayer.hasResearched(TechType.Plague)) {

				Unit targetEnemyUnit = null;

				// targetEnemyUnit 을 적절히 정해보세요

				if (targetEnemyUnit != null) {
					unit.useTech(TechType.Plague, targetEnemyUnit);
					hasCommanded = true;
				}
			} else if (unit.getEnergy() >= TechType.Dark_Swarm.energyCost()) {

				Position targetPosition = null;

				// targetPosition 을 적절히 정해보세요

				if (targetPosition != null) {
					unit.useTech(TechType.Dark_Swarm, targetPosition);
					hasCommanded = true;
				}
			}
		}

		return hasCommanded;
	}

	Position setTargetPositionOne(Position A, int C) {
		Random random = new Random();

		int x;
		int y;

		Position targetPosition;

		x = random.nextInt(C);
		x = x + random.nextInt(C * 2) * (-1);

		y = random.nextInt(C);
		y = y + random.nextInt(C * 2) * (-1);

		/*
		 * while(true) { x = random.nextInt(C); x = x + random.nextInt(C*2)*(-1);
		 * 
		 * y = random.nextInt(C); y = y + random.nextInt(C*2)*(-1);
		 * 
		 * break; }
		 */

		targetPosition = new Position(A.getX() + x, A.getY() + y);
		// System.out.println(7);
		return targetPosition;
	}

	Position setTargetPositionTwo(Position A, Position B, int C) {
		Random random = new Random();

		int x;
		int y;

		Position targetPosition;

		x = random.nextInt(C);
		x = x + random.nextInt(C * 2) * (-1);

		y = random.nextInt(C);
		y = y + random.nextInt(C * 2) * (-1);

		/*
		 * while(true) { x = random.nextInt(C); x = x + random.nextInt(C*2)*(-1);
		 * 
		 * y = random.nextInt(C); y = y + random.nextInt(C*2)*(-1);
		 * 
		 * break; }
		 */

		targetPosition = new Position((A.getX() + B.getX()) / 2 + x, (A.getY() + B.getY()) / 2 + y);
		// System.out.println(6);
		return targetPosition;
	}

	int countEnemyAround(Position A, int B) {
		int enemyNum = 0;

		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(A.getPoint(), B * Config.TILE_SIZE)) {
			if (unit.getPlayer() == enemyPlayer) {
				enemyNum = enemyNum + 1;
			}
		}

		// System.out.println("enemyNum :"+ enemyNum);

		return enemyNum;
	}

	int countMyAttackUnitAround(Position A, int B) {
		int myNum = 0;

		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(A.getPoint(), B * Config.TILE_SIZE)) {
			if (unit.getPlayer() == myPlayer && unit.canAttack()) {
				myNum = myNum + 1;
			}
		}

		// System.out.println("enemyNum :"+ enemyNum);
		return myNum;
	}

	int countMyAttackUnitAll() {
		int myNum = 0;

		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if (unit.canAttack()) {
				if (unit.getType().isBuilding() == false && unit.getType() != UnitType.Zerg_Drone
						&& unit.getType() != UnitType.Zerg_Larva && unit.getType() != UnitType.Zerg_Overlord) {
					myNum = myNum + 1;
				}
			}
		}

		return myNum;
	}

	/// 아군 공격 유닛들에게 공격을 지시합니다
	public void commandMyCombatUnitToAttack() {

		// 최종 타겟은 적군의 Main BaseLocation

		/////////////// 20170806 권순우 공격지점 설정

		BaseLocation targetEnemyBaseLocation = enemyMainBaseLocation;
		Chokepoint targetPoint = enemySecondChokePoint;
		

		// int myDeadUnits = myKilledCombatUnitCount1 + myKilledCombatUnitCount2 +
		// myKilledCombatUnitCount3;
		// int enemyDeadUnits = enemyKilledCombatUnitCount;

		if (targetPoint != null) {

			////////////////////////////////////////////////

			/*
			 * // 테란 종족의 경우, 벙커 안에 있는 유닛은 밖으로 빼낸다 if (myRace == Race.Terran) { for(Unit
			 * bunker : myDefenseBuildingType1List) { if (bunker.getLoadedUnits().size() >
			 * 0) { boolean isThereSomeEnemyUnit = false; for(Unit someUnit :
			 * MyBotModule.Broodwar.getUnitsInRadius(bunker.getPosition(), 6 *
			 * Config.TILE_SIZE)) { if (someUnit.getPlayer() == enemyPlayer) {
			 * isThereSomeEnemyUnit = true; break; } } if (isThereSomeEnemyUnit == false) {
			 * bunker.unloadAll(); } } } }
			 */

			// targetPosition 을 설정한다
			/*
			 * 
			 * if(moveToFirstChokePoint==true) { targetPoint = enemyFirstChokePoint;
			 * targetPosition = targetPoint.getPoint();
			 * 
			 * int enemyUnitCountAround = 0;
			 * 
			 * for(Unit unit :
			 * MyBotModule.Broodwar.getUnitsInRadius(enemyFirstChokePoint.getPoint(), 30 *
			 * Config.TILE_SIZE)) { if (unit.getPlayer() == enemyPlayer) {
			 * enemyUnitCountAround ++; } }
			 * 
			 * if (enemyUnitCountAround == 0) { moveToFirstChokePoint = false;
			 * moveToEnemyBaseLocation = true; } } else if(moveToEnemyBaseLocation==true) {
			 * targetPosition = enemyMainBaseLocation.getPoint();
			 * 
			 * if(1.2*(myKilledCombatUnitCount1 + myKilledCombatUnitCount2 +
			 * myKilledCombatUnitCount3) < enemyKilledCombatUnitCount ) { targetPoint =
			 * enemyFirstChokePoint; targetPosition = targetPoint.getPoint();
			 * 
			 * moveToFirstChokePoint = false; moveToEnemyBaseLocation = false; } } else
			 */

			/*
			 * if((myDeadUnits * 1.15) < enemyDeadUnits) {
			 * 
			 * } else if((myDeadUnits * 1.05) < enemyDeadUnits) { ; } else { ; }
			 */

			// int goMore = 0;
			// int myAttackUnitAround;

			// 권순우 0617 두 지점사이의 위치를 구해서 이동목표로 삼는 것인데 그 지점을 여러개 설정하는 끔찍한 모습이다.
			Position position01 = setTargetPositionTwo(mySecondChokePoint.getPoint(), enemySecondChokePoint.getPoint(),
					1);
			position01 = setTargetPositionTwo(position01, enemySecondChokePoint.getPoint(), 1);
			position01 = setTargetPositionTwo(position01, enemySecondChokePoint.getPoint(), 30);

			Position position02 = setTargetPositionTwo(position01, enemySecondChokePoint.getPoint(), 30);
			Position position03 = setTargetPositionOne(enemySecondChokePoint.getPoint(), 35);
			Position position04 = setTargetPositionTwo(position03, enemyFirstExpansionLocation.getPoint(), 45);
			Position position05 = setTargetPositionTwo(position04, enemyMainBaseLocation.getPoint(), 45);
			Position position06 = setTargetPositionOne(enemyMainBaseLocation.getPoint().getPoint(), 100);

			// Position position07 = setTargetPositionTwo(mySecondChokePoint.getPoint(),
			// enemySecondChokePoint.getPoint(), 30);

			// for (Unit unit : myAllCombatUnitList)

			attackTargetPosition = position01;
			if (myAllCombatUnitList.size() > 0.5 * myAllCombatUnitList.size()) {
				attackTargetPosition = position02;
				if (myAllCombatUnitList.size() > 0.5 * myAllCombatUnitList.size()) {
					attackTargetPosition = position03;
					if (myAllCombatUnitList.size() > 0.5 * myAllCombatUnitList.size()) {
						attackTargetPosition = position04;
						if (myAllCombatUnitList.size() > 0.5 * myAllCombatUnitList.size()) {
							attackTargetPosition = position05;
							if (myAllCombatUnitList.size() > 0.5 * myAllCombatUnitList.size()) {
								attackTargetPosition = position06;
							}
						}
					}
				}
			}

			/*
			 * if(countMyAttackUnitAround(position05,6) > 0.1 * countMyAttackUnitAll ()) {
			 * targetPosition = position06; } else if(countMyAttackUnitAround(position04, 5)
			 * > 0.2 * countMyAttackUnitAll ()) { targetPosition = position05; } else
			 * if(countMyAttackUnitAround(position03, 4) > 0.3 * countMyAttackUnitAll ()) {
			 * targetPosition = position04; } else if(countMyAttackUnitAround(position02, 7)
			 * > 0.4 * countMyAttackUnitAll ()) { targetPosition = position03; } else
			 * if(countMyAttackUnitAround(position01, 10) > 0.4 * countMyAttackUnitAll ()) {
			 * targetPosition = position03; } else { targetPosition = position03; }
			 */

			/*
			 * if(countEnemyAround(enemySecondChokePoint.getPoint(), 30) < 5) // 두번째 길목이
			 * 비었으면 { //System.out.println(2); // 앞마당으로 가라 targetPosition =
			 * setTargetPositionTwo(enemySecondChokePoint.getPoint(),
			 * enemyFirstExpansionLocation.getPoint(), 30); goMore = 1;
			 * 
			 * if(countEnemyAround(enemyFirstExpansionLocation.getPosition(), 45) < 5)//
			 * 앞마당이 비었으면 { //System.out.println(3); // 첫번째 길목을 통과해라 targetPosition =
			 * setTargetPositionTwo(enemyFirstChokePoint.getPoint(),
			 * enemyMainBaseLocation.getPoint(), 45); goMore = 2;
			 * 
			 * if(countEnemyAround(setTargetPositionTwo(enemyFirstChokePoint.getPoint(),
			 * enemyMainBaseLocation.getPoint(), 1), 30) < 5) // 그래도 뭐가 없으면 {
			 * //System.out.println(4); // 본진을 쳐라 targetPosition =
			 * setTargetPositionOne(enemyMainBaseLocation.getPoint().getPoint(), 60); goMore
			 * = 3; } } }
			 * 
			 * if((myDeadUnits * 1.15) < enemyDeadUnits) { if(goMore != 3) { targetPosition
			 * = setTargetPositionOne(enemyMainBaseLocation.getPoint().getPoint(), 60); }
			 * 
			 * // 위에서 판단에 따르면 적이 있어서 본진 칠 상황이 아니지만 그래도 승세가 있으니 본진쳐도 되는 경우 } else
			 * if((myDeadUnits * 1.05) < enemyDeadUnits) { // 위에서 판단에 따르면 적이 있어서 아직 갈 상황이
			 * 아니라고 했으나 승세가 있으니 첫길목을 지나쳐도 된다고 판단되는 경우 if(goMore != 2) { targetPosition =
			 * setTargetPositionTwo(enemyFirstChokePoint.getPoint(),
			 * enemyMainBaseLocation.getPoint(), 45); } } else { // 아직 딱히 싸워보지 않은 경우라고 볼 수
			 * 있다 targetPosition = setTargetPositionTwo(enemySecondChokePoint.getPoint(),
			 * enemyFirstExpansionLocation.getPoint(), 30); goMore = 1; }
			 */

			/*
			 * if(attack_cnt==1 && goMore == 1 &&
			 * countEnemyAround(setTargetPositionTwo(enemySecondChokePoint.getPoint(),
			 * enemyFirstExpansionLocation.getPoint(),1), 30) == 0) { // 스캔을 뿌려보고 판단해야 할 것
			 * 같은데 스캔이 좀 아깝지 않을까? // 이게 아니면 일정 시간 머물다 올라갈까 이동시간 및 현지 대기시간등을 프레임과 연계해서
			 * 0.001단위정도씩 += 하는 식으로 탐색의 범위를 넓혀볼까 // 안되면 그냥 기다리자...
			 * //System.out.println("첫 공격이라서 왔더니 텅 비어서 올라감");
			 * 
			 * targetPosition = setTargetPositionTwo(enemyFirstChokePoint.getPoint(),
			 * enemyMainBaseLocation.getPoint(), 15); }
			 */

			/*
			 * if(1.15*(myKilledCombatUnitCount1 + myKilledCombatUnitCount2 +
			 * myKilledCombatUnitCount3) < enemyKilledCombatUnitCount ) { //targetPoint =
			 * enemyFirstChokePoint; targetPosition = enemyMainBaseLocation.getPoint(); }
			 * else if(1.05*(myKilledCombatUnitCount1 + myKilledCombatUnitCount2 +
			 * myKilledCombatUnitCount3) < enemyKilledCombatUnitCount ) { targetPoint =
			 * enemyFirstChokePoint; targetPosition = targetPoint.getPoint();
			 * 
			 * int enemyUnitCountAround = 0;
			 * 
			 * for(Unit unit :
			 * MyBotModule.Broodwar.getUnitsInRadius(enemyFirstChokePoint.getPoint(), 8 *
			 * Config.TILE_SIZE)) { if (unit.getPlayer() == enemyPlayer) {
			 * enemyUnitCountAround ++; } }
			 * 
			 * if (enemyUnitCountAround == 0) { moveToFirstChokePoint = false;
			 * moveToEnemyBaseLocation = true; }
			 * 
			 * } else { targetPoint = enemySecondChokePoint; targetPosition =
			 * targetPoint.getPoint();
			 * 
			 * int enemyUnitCountAround = 0;
			 * 
			 * for(Unit unit :
			 * MyBotModule.Broodwar.getUnitsInRadius(enemySecondChokePoint.getPoint(), 8 *
			 * Config.TILE_SIZE)) { if (unit.getPlayer() == enemyPlayer) {
			 * enemyUnitCountAround ++; } }
			 * 
			 * if (enemyUnitCountAround == 0) { moveToFirstChokePoint = true; } }
			 */

			/////////////// 20170806 권순우 공격지점 설정

			/*
			 * 권순우 0617 노승호의 역작이다. 올해는 아래 코드를 변형해서 오버로드를 은신캐 근처로 이동시키는 코드를 짜면 되겠다.
			 * 
			 * 제일 좋은 것은 은신캐 근처에서 가장 가까운 오버로드를 이동시키는 것인데 적군 유닛의 위치를 중심으로 가장 가까운 아군 유닛중에서
			 * 오버로드면 와라!
			 * 
			 * 
			 * 
			 * for (Unit scanner : MyBotModule.Broodwar.self().getUnits()) {
			 * 
			 * if (scanner.getType() == UnitType.Terran_Comsat_Station) { for(Unit
			 * isInvisible : MyBotModule.Broodwar.getUnitsInRadius(scanner.getPoint(), 256 *
			 * Config.TILE_SIZE)) { if(isInvisible.isBurrowed() == true ||
			 * isInvisible.isCloaked() == true) { if(scanner.getEnergy() >=
			 * TechType.Scanner_Sweep.energyCost()) {
			 * scanner.useTech(TechType.Scanner_Sweep, isInvisible);
			 * System.out.println("스캔!"); } } } } }//20170808 스캐너 구현 완료.
			 */

			// 모든 아군 공격유닛들로 하여금 targetPosition 을 향해 공격하게 한다
			for (Unit unit : myAllCombatUnitList) {

				boolean hasCommanded = false;

				/*
				 * 권순우 0617 지금 이 부분에 구현되는 코드가 실제 전투시에 각 유닛이 수행할 행동을 의미한다
				 * 
				 * 작년에도 나쁘지는 않았는데 올해는 개별 유닛말고 부대단위 유닛으로 조정하면 좀 더 좋은 효과가 있겠다. 가령 일점사
				 * 
				 * 
				 * 
				 * 
				 * if (unit.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || unit.getType()
				 * == UnitType.Terran_Siege_Tank_Siege_Mode) { hasCommanded =
				 * controlSiegeTankUnitType(unit); }
				 * 
				 * if (unit.getType() == UnitType.Terran_Science_Vessel) { for(Unit isInvisible
				 * : MyBotModule.Broodwar.getUnitsInRadius(unit.getPoint(), 30 *
				 * Config.TILE_SIZE)) { if(isInvisible.getType() == UnitType.Zerg_Lurker &&
				 * isInvisible.isIrradiated() == false) { unit.useTech(TechType.Irradiate,
				 * isInvisible); } } }
				 * 
				 * if (unit.getType() == UnitType.Terran_Marine) {
				 * if(myPlayer.hasResearched(necessaryTechType1) == true) { if(!unit.isStimmed()
				 * && unit.isAttacking()) { unit.useTech(TechType.Stim_Packs); } } }
				 * 
				 * if (unit.getType() == UnitType.Terran_Wraith) {
				 * if(myPlayer.hasResearched(TechType.Cloaking_Field) == true) {
				 * if(!unit.isCloaked() && unit.isAttacking()) // 은신이 필요한 다양한 경우의 수 고려해야 {
				 * unit.useTech(TechType.Cloaking_Field); } } }
				 * 
				 */

				// 따로 명령 내린 적이 없으면, targetPosition 을 향해 공격 이동시킵니다
				if (hasCommanded == false) {

					if (unit.isIdle()) {

						if (unit.canAttack()) {
							// 권순우 0617 공격을 할 수는 있지만
							// 전략이나 업그레이드 등 전략적인 사유로 공격을 나가서는 안된다면
							// 이 부분에서 그런것을 체크해야 한다
							// 가령 아래 레이스는 은신이 개발 안되면 모두 공격나가도 공격 안따라가는 것이다.
							if (unit.getType() == UnitType.Zerg_Mutalisk)// && myCombatUnitType5List.size()<6 &&
																			// !myPlayer.hasResearched(TechType.Cloaking_Field))
							{
								// unit.patrol(myMainBaseLocation.getPosition(),true);
								// unit.move(enemyMainBaseLocation.getPosition());
								// 0623 commandUtil.attackMove(unit, targetPosition); // 0622 일단은 이렇게 하는데 뮤탈이 먼저
								// 들어가서 일꾼 잡고 있었으면 상황이 살짝 달라진다 굳이 나올 필요 없는데 나오는 형국
								// 0623 MUTAL_MOVEMENT(unit, null, targetPosition);

								int x, y;

								x = enemyMainBaseLocation.getX();
								y = enemyMainBaseLocation.getY();

								if (x < 63 * 32) {
									x = 0;
								} else {
									x = 127 * 32;
								}

								if (y < 63 * 32) {
									y = 0;
								} else {
									y = 127 * 32;
								}

								
								//mu.MUTAL_MOVEMENT(unit, null, new Position(x, y));
								//mutalisk = new Mutalisk();
								
								/* 0628 이유는 모르지만 이 부분이 프레임 아웃을 유발하는듯 하다
								if(enemyMainBaseLocation!=null && mutalisk_start == true)
								{
								mutalisk.myMutal();
								}
								*/ 
								
								
								hasCommanded = true;
							} else {
								commandUtil.attackMove(unit, attackTargetPosition);
								hasCommanded = true;
							}
						}

						else {

							/*
							 * 권순우 0617 가장 까다로운 부분이다. 메딕, 사이언스 베슬 등 특수 유닛은 일단 전투지역으로 이동을 시키고 뭔가를 해야되는데 이 부분을
							 * 해결하는 것이 매우 까다롭다.
							 * 
							 * 올해는 각 유닛별로 고유번호가 있다는 것을 알았으니 단순히 가까운 유닛을 찾는 것이 아니라 전담 마크 개념을 구현해 볼 수 있을 것이다.
							 * 과연?
							 */

							// canAttack 기능이 없는 유닛타입 중 메딕은 마린 유닛에 대해 Heal 하러 가게 하고, 마린 유닛이 없으면 아군 지역으로 돌아오게
							// 합니다
							if (unit.getType() == UnitType.Terran_Medic) {

								Position targetMyUnitPosition = null;
								Random random = new Random();
								Unit randomMarine;

								int breakCondition = 0;
								int size = myCombatUnitType1List.size();
								int isNear = 100;

								while (true) {
									randomMarine = myCombatUnitType1List.get(random.nextInt(size));
									if (randomMarine == null || randomMarine.exists() == false
											|| randomMarine.getHitPoints() < 0) {
										// 일단 마린이 null이거나, 죽었거나, 풀피면 다른 마린을 찾는다
										continue;
									}
									if (randomMarine.getHitPoints() < randomMarine.getInitialHitPoints()) {
										// 맞긴 했는데 아직 살았다면 힐줘라
										unit.useTech(TechType.Healing, randomMarine);
										// hasCommanded = true;
										break;
									} else if (breakCondition == size) {
										// 가까운 마린이면 그냥 따라가고
										// 멀면 새로운 마린을 찾아서 따라가고
										// 루프 탈출

										if (unit.getDistance(randomMarine) < isNear) {
											unit.follow(randomMarine);
											// hasCommanded = true;
											break;
										} else {
											isNear = isNear + 10;
										}
									} else {
										breakCondition = breakCondition + 1;
									}
								}

								for (Unit myUnit : myCombatUnitType1List) {
									if (myUnit == null || myUnit.exists() == false || myUnit.getHitPoints() < 0) {
										continue;
									}
									if (myUnit.getHitPoints() < myUnit.getInitialHitPoints()
											|| random.nextInt() % 2 == 0) {
										targetMyUnitPosition = myUnit.getPosition();
										break;
									}
								}
								if (targetMyUnitPosition != null) {
									unit.useTech(TechType.Healing, targetMyUnitPosition);
									hasCommanded = true;
								} else {
									unit.useTech(TechType.Healing, mySecondChokePoint.getCenter());
									hasCommanded = true;
								}

							}

							// canAttack 기능이 없는 유닛타입 중 러커는 일반 공격유닛처럼 targetPosition 을 향해 이동시킵니다
							else if (unit.getType() == UnitType.Zerg_Lurker) {
								commandUtil.move(unit, attackTargetPosition);
								hasCommanded = true;
							} else if (unit.getType() == UnitType.Terran_Science_Vessel) {

								// Position targetMyUnitPosition = null;

								Random random = new Random();

								Unit randomMarine;

								int breakCondition = 0;
								int size = myCombatUnitType1List.size();
								int isNear = 100;

								while (true) {
									randomMarine = myCombatUnitType1List.get(random.nextInt(size));
									if (randomMarine.isStartingAttack() || randomMarine.isAttacking()
											|| randomMarine.isUnderAttack()) {
										unit.follow(randomMarine);
										hasCommanded = true;
										break;
									} else if (breakCondition == size) {
										// 가까운 마린이면 그냥 따라가고
										// 멀면 새로운 마린을 찾아서 따라가고
										// 루프 탈출
										if (unit.getDistance(randomMarine) < isNear) {
											unit.follow(randomMarine);
											hasCommanded = true;
											break;
										} else {
											isNear = isNear + 10;
										}
									} else {
										breakCondition = breakCondition + 1;
									}
								}

								for (Unit myUnit : myCombatUnitType1List) {
									unit.follow(myUnit);
									hasCommanded = true;

								}

							}

							// canAttack 기능이 없는 다른 유닛타입 (하이템플러, 옵저버, 사이언스베슬, 오버로드) 는
							// 따로 명령을 내린 적이 없으면 다른 공격유닛들과 동일하게 이동하도록 되어있습니다.
							else {
								commandUtil.move(unit, attackTargetPosition);
								hasCommanded = true;
							}
						}
					}
				}
			}
		}
	}

	/// 적군을 Eliminate 시키도록 아군 공격 유닛들에게 지시합니다
	void commandMyCombatUnitToEliminate() {

		if (enemyPlayer == null || enemyRace == Race.Unknown) {
			return;
		}

		Random random = new Random();
		int mapHeight = MyBotModule.Broodwar.mapHeight(); // 128
		int mapWidth = MyBotModule.Broodwar.mapWidth(); // 128

		// 아군 공격 유닛들로 하여금 적군의 남은 건물을 알고 있으면 그것을 공격하게 하고, 그렇지 않으면 맵 전체를 랜덤하게 돌아다니도록 합니다

		Unit targetEnemyBuilding = null;

		for (Unit enemyUnit : enemyPlayer.getUnits()) {

			if (enemyUnit == null || enemyUnit.exists() == false || enemyUnit.getHitPoints() < 0)
				continue;

			if (enemyUnit.getType().isBuilding()) {
				targetEnemyBuilding = enemyUnit;
				break;
			}
		}

		for (Unit unit : myAllCombatUnitList) {

			boolean hasCommanded = false;

			if (unit.getType() == UnitType.Terran_Siege_Tank_Tank_Mode
					|| unit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
				hasCommanded = controlSiegeTankUnitType(unit);
			}

			// 따로 명령 내린 적이 없으면, 적군의 남은 건물 혹은 랜덤 위치로 이동시킨다
			if (hasCommanded == false) {

				if (unit.isIdle()) {

					Position targetPosition = null;
					if (targetEnemyBuilding != null) {
						targetPosition = targetEnemyBuilding.getPosition();
					} else {
						targetPosition = new Position(random.nextInt(mapWidth * Config.TILE_SIZE),
								random.nextInt(mapHeight * Config.TILE_SIZE));
					}

					if (unit.canAttack()) {
						commandUtil.attackMove(unit, targetPosition);
						hasCommanded = true;
					} else {

						// canAttack 기능이 없는 유닛타입 중 메딕은 마린 유닛에 대해 Heal 하러 가게 하고, 마린 유닛이 없으면 아군 지역으로 돌아오게
						// 합니다
						if (unit.getType() == UnitType.Terran_Medic) {
							Position targetMyUnitPosition = null;

							for (Unit myUnit : myCombatUnitType1List) {

								if (myUnit == null || myUnit.exists() == false || myUnit.getHitPoints() < 0) {
									continue;
								}

								if (myUnit.getHitPoints() < myUnit.getInitialHitPoints() || random.nextInt() % 2 == 0) {
									targetMyUnitPosition = myUnit.getPosition();
									break;
								}
							}

							if (targetMyUnitPosition != null) {

								unit.useTech(TechType.Healing, targetMyUnitPosition);
								hasCommanded = true;
							} else {
								unit.useTech(TechType.Healing, mySecondChokePoint.getCenter());
								hasCommanded = true;
							}
						}

						// canAttack 기능이 없는 유닛타입 중 러커는 일반 공격유닛처럼 targetPosition 을 향해 이동시킵니다
						else if (unit.getType() == UnitType.Zerg_Lurker) {
							commandUtil.move(unit, targetPosition);
							hasCommanded = true;
						}

						// canAttack 기능이 없는 다른 유닛타입 (하이템플러, 옵저버, 사이언스베슬, 오버로드) 는
						// 따로 명령을 내린 적이 없으면 다른 공격유닛들과 동일하게 이동하도록 되어있습니다.

						else {
							commandUtil.move(unit, targetPosition);
							hasCommanded = true;
						}
					}
				}
			}
		}
	}

	/// 시즈탱크 유닛에 대해 컨트롤 명령을 내립니다
	boolean controlSiegeTankUnitType(Unit unit) {

		boolean hasCommanded = false;
		int MaxNumberofSiegedUnit = myCombatUnitType3List.size(); // 노승호

		// defenseMode 일 경우
		if (combatState == CombatState.defenseMode) {

			// 아군 방어 건물이 세워져있는 위치 주위에 시즈모드 시켜놓는다
			Position myDefenseBuildingPosition = null;

			switch (seedPositionStrategyOfMyDefenseBuildingType) {
			case MainBaseLocation:
				myDefenseBuildingPosition = myMainBaseLocation.getPosition();
				break;
			case FirstChokePoint:
				myDefenseBuildingPosition = myFirstChokePoint.getCenter();
				break;
			case FirstExpansionLocation:
				myDefenseBuildingPosition = myFirstExpansionLocation.getPosition();
				break;
			case SecondChokePoint:
				myDefenseBuildingPosition = mySecondChokePoint.getCenter();
				break;
			default:
				myDefenseBuildingPosition = myMainBaseLocation.getPosition();
				break;
			}

			if (myDefenseBuildingPosition != null) {
				if (unit.isSieged() == false) {
					if (unit.getDistance(myDefenseBuildingPosition) < 5 * Config.TILE_SIZE) {
						unit.siege();
						hasCommanded = true;
					}
				}
			}
		} else {

			// 적이 근처에 있으면 시즈모드 시키고, 없으면 시즈모드를 해제한다
			Position nearEnemyUnitPosition = null;
			double tempDistance = 0;

			for (Unit enemyUnit : MyBotModule.Broodwar.enemy().getUnits()) {

				if (enemyUnit.isFlying() || enemyUnit.exists() == false)
					continue;

				tempDistance = unit.getDistance(enemyUnit.getPosition());

				if (tempDistance < 12 * Config.TILE_SIZE) {
					nearEnemyUnitPosition = enemyUnit.getPosition();
				}
			}
			// 시즈탱크 전체의 절반만 시즈모드를 한다 - 노승호 - 작동안해..

			if (unit.isSieged() == false && MaxNumberofSiegedUnit >= myCombatUnitType3List.size() / 2) {
				if (nearEnemyUnitPosition != null) {
					unit.siege();
					MaxNumberofSiegedUnit--;
					hasCommanded = true;
				}
			} else {
				if (nearEnemyUnitPosition == null) {
					unit.unsiege();
					hasCommanded = true;
				}
			}
		}
		return hasCommanded;
	}

	// 일꾼 계속 추가 생산
	public void executeWorkerTraining() {

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (isInitialBuildOrderFinished == false) {
			return;
		}

		if (MyBotModule.Broodwar.self().minerals() >= 50) {
			// workerCount = 현재 일꾼 수 + 생산중인 일꾼 수
			int workerCount = MyBotModule.Broodwar.self().allUnitCount(InformationManager.Instance().getWorkerType());

			if (MyBotModule.Broodwar.self().getRace() == Race.Zerg) {
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
					if (unit.getType() == UnitType.Zerg_Egg) {
						// Zerg_Egg 에게 morph 명령을 내리면 isMorphing = true,
						// isBeingConstructed = true, isConstructing = true 가 된다
						// Zerg_Egg 가 다른 유닛으로 바뀌면서 새로 만들어진 유닛은 잠시
						// isBeingConstructed = true, isConstructing = true 가
						// 되었다가,
						if (unit.isMorphing() && unit.getBuildType() == UnitType.Zerg_Drone) {
							workerCount++;
						}
					}
				}
			} else {
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
					if (unit.getType().isResourceDepot()) // 권순우 0617 기지건물 : 넥서스, 커맨드센터, 해처리, 레어, 하이브
					{
						if (unit.isTraining()) {
							workerCount += unit.getTrainingQueue().size();
						}
					}
				}
			}

			// 0630 최대치 설정
			if(workerCount > 40) // 0630 해처리가 일정 숫자 이상이 되면 라바소모하느라 자원이 더 안모이는데 그때 맥스를 올려주자 
			{
				return;
			}
			
			
			int optimalWorkerCount = 0;
			for (BaseLocation baseLocation : InformationManager.Instance()
					.getOccupiedBaseLocations(InformationManager.Instance().selfPlayer)) {
				optimalWorkerCount += baseLocation.getMinerals().size() * 2.0;
				optimalWorkerCount += baseLocation.getGeysers().size() * 3;
			}

			if (workerCount < optimalWorkerCount) {
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
					if (unit.getType().isResourceDepot()) {
						if (unit.isTraining() == false || unit.getLarva().size() > 0) {
							// 빌드큐에 일꾼 생산이 1개는 있도록 한다
							if (BuildManager.Instance().buildQueue
									.getItemCount(InformationManager.Instance().getWorkerType(), null) == 0) {
								// std.cout + "worker enqueue" + std.endl;

								// 권순우 0617 다음의 코드를 highest로 하면 긴급으로 유닛 생산을 명령하는데 추가하는데 쓰일 수 있을 듯 하다. 가령 최초 빌드가
								// 끝나기도 전에 일꾼이 죽어버리는 등의 상황에서
								BuildManager.Instance().buildQueue.queueAsLowestPriority(
										new MetaType(InformationManager.Instance().getWorkerType()), false);
							}
						}
					}
				}
			}
		}
	}

	/// Supply DeadLock 예방 및 SupplyProvider 가 부족해질 상황 에 대한 선제적 대응으로서
	/// SupplyProvider를 추가 건설/생산합니다
	public void executeSupplyManagement() {

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 가이드 추가 및 콘솔 출력 명령 주석 처리
		// InitialBuildOrder 진행중 혹은 그후라도 서플라이 건물이 파괴되어 데드락이 발생할 수 있는데, 이 상황에 대한 해결은
		// 참가자께서 해주셔야 합니다.
		// 오버로드가 학살당하거나, 서플라이 건물이 집중 파괴되는 상황에 대해 무조건적으로 서플라이 빌드 추가를 실행하기 보다 먼저 전략적 대책
		// 판단이 필요할 것입니다
		// BWAPI::Broodwar->self()->supplyUsed() >
		// BWAPI::Broodwar->self()->supplyTotal() 인 상황이거나
		// BWAPI::Broodwar->self()->supplyUsed() + 빌드매니저 최상단 훈련 대상 유닛의
		// unit->getType().supplyRequired() > BWAPI::Broodwar->self()->supplyTotal() 인
		// 경우
		// 서플라이 추가를 하지 않으면 더이상 유닛 훈련이 안되기 때문에 deadlock 상황이라고 볼 수도 있습니다.
		// 저그 종족의 경우 일꾼을 건물로 Morph 시킬 수 있기 때문에 고의적으로 이런 상황을 만들기도 하고,
		// 전투에 의해 유닛이 많이 죽을 것으로 예상되는 상황에서는 고의적으로 서플라이 추가를 하지 않을수도 있기 때문에
		// 참가자께서 잘 판단하셔서 개발하시기 바랍니다.

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		// InitialBuildOrder 진행중이라도 supplyUsed 가 supplyTotal 보다 커져버리면 실행하도록 합니다
		if (isInitialBuildOrderFinished == false
				&& MyBotModule.Broodwar.self().supplyUsed() <= MyBotModule.Broodwar.self().supplyTotal()) {
			return;
		}

		// 1초에 한번만 실행
		if (MyBotModule.Broodwar.getFrameCount() % 24 != 0) {
			return;
		}

		// 게임에서는 서플라이 값이 200까지 있지만, BWAPI 에서는 서플라이 값이 400까지 있다
		// 저글링 1마리가 게임에서는 서플라이를 0.5 차지하지만, BWAPI 에서는 서플라이를 1 차지한다
		if (MyBotModule.Broodwar.self().supplyTotal() <= 400) {
			// 서플라이가 다 꽉찼을때 새 서플라이를 지으면 지연이 많이 일어나므로, supplyMargin (게임에서의 서플라이 마진 값의 2배)만큼
			// 부족해지면 새 서플라이를 짓도록 한다
			// 이렇게 값을 정해놓으면, 게임 초반부에는 서플라이를 너무 일찍 짓고, 게임 후반부에는 서플라이를 너무 늦게 짓게 된다
			int supplyMargin = 24;

			// currentSupplyShortage 를 계산한다
			int currentSupplyShortage = MyBotModule.Broodwar.self().supplyUsed() + supplyMargin
					- MyBotModule.Broodwar.self().supplyTotal();

			if (currentSupplyShortage > 0) {
				// 생산/건설 중인 Supply를 센다
				int onBuildingSupplyCount = 0;

				// 저그 종족인 경우, 생산중인 Zerg_Overlord (Zerg_Egg) 를 센다. Hatchery 등 건물은 세지 않는다
				if (MyBotModule.Broodwar.self().getRace() == Race.Zerg) {

					for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {

						if (unit.getType() == UnitType.Zerg_Egg && unit.getBuildType() == UnitType.Zerg_Overlord) {

							onBuildingSupplyCount += UnitType.Zerg_Overlord.supplyProvided();
						}

						// 갓태어난 Overlord 는 아직 SupplyTotal 에 반영안되어서, 추가 카운트를 해줘야함
						if (unit.getType() == UnitType.Zerg_Overlord && unit.isConstructing()) {

							onBuildingSupplyCount += UnitType.Zerg_Overlord.supplyProvided();
						}
					}
				}

				// 저그 종족이 아닌 경우, 건설중인 Protoss_Pylon, Terran_Supply_Depot 를 센다. Nexus, Command
				// Center 등 건물은 세지 않는다
				else {
					onBuildingSupplyCount += ConstructionManager.Instance().getConstructionQueueItemCount(
							InformationManager.Instance().getBasicSupplyProviderUnitType(), null)
							* InformationManager.Instance().getBasicSupplyProviderUnitType().supplyProvided();
				}

				// 주석처리
				// System.out.println("currentSupplyShortage : " + currentSupplyShortage + "
				// onBuildingSupplyCount : " + onBuildingSupplyCount);

				if (currentSupplyShortage > onBuildingSupplyCount) {
					// BuildQueue 최상단에 SupplyProvider 가 있지 않으면 enqueue 한다
					boolean isToEnqueue = true;

					if (!BuildManager.Instance().buildQueue.isEmpty()) {
						BuildOrderItem currentItem = BuildManager.Instance().buildQueue.getHighestPriorityItem();
						if (currentItem.metaType.isUnit() && currentItem.metaType.getUnitType() == InformationManager
								.Instance().getBasicSupplyProviderUnitType()) {
							isToEnqueue = false;
						}
					}

					if (isToEnqueue) {
						// 주석처리
						// System.out.println("enqueue supply provider "
						// + InformationManager.Instance().getBasicSupplyProviderUnitType());
						BuildManager.Instance().buildQueue.queueAsHighestPriority(
								new MetaType(InformationManager.Instance().getBasicSupplyProviderUnitType()), true);
					}

				}

			}

		}
		// BasicBot 1.1 Patch End ////////////////////////////////////////////////
	}

	/// 방어건물 및 공격유닛 생산 건물을 건설합니다

	void executeBuildingConstruction() {

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (isInitialBuildOrderFinished == false) {
			return;
		}

		// 1초에 한번만 실행
		if (MyBotModule.Broodwar.getFrameCount() % 24 != 0) {
			return;
		}

		boolean isPossibleToConstructDefenseBuildingType1 = false;
		boolean isPossibleToConstructDefenseBuildingType2 = false;
		boolean isPossibleToConstructCombatUnitTrainingBuildingType = false;

		// 권순우 0617 방어 건물을 짓는데 흥미로운 것은
		// 이미 존재하거나, 짓고 있거나, 지을 예정인 것도 모두 "존재"한다고 해야만
		// 목표치 이상을 초과 건설하는 문제가 발생하지 않는다.
		// 방어 건물 증설을 우선적으로 실시한다
		// 현재 방어 건물 갯수

		int numberOfMyDefenseBuildingType1 = 0;
		int numberOfMyDefenseBuildingType2 = 0;

		if (myRace == Race.Protoss) {
		} else if (myRace == Race.Terran) {
		} else if (myRace == Race.Zerg) {
			// 저그의 경우 크립 콜로니 갯수를 셀 때 성큰 콜로니 갯수까지 포함해서 세어야, 크립 콜로니를 지정한 숫자까지만 만든다
			numberOfMyDefenseBuildingType1 += myPlayer.allUnitCount(myDefenseBuildingType1);
			numberOfMyDefenseBuildingType1 += BuildManager.Instance().buildQueue.getItemCount(myDefenseBuildingType1);
			numberOfMyDefenseBuildingType1 += ConstructionManager.Instance()
					.getConstructionQueueItemCount(myDefenseBuildingType1, null);
			numberOfMyDefenseBuildingType1 += myPlayer.allUnitCount(myDefenseBuildingType2);
			numberOfMyDefenseBuildingType1 += BuildManager.Instance().buildQueue.getItemCount(myDefenseBuildingType2);
			numberOfMyDefenseBuildingType2 += myPlayer.allUnitCount(myDefenseBuildingType2);
			numberOfMyDefenseBuildingType2 += BuildManager.Instance().buildQueue.getItemCount(myDefenseBuildingType2);

			if (myPlayer.completedUnitCount(UnitType.Zerg_Spawning_Pool) > 0) {
				isPossibleToConstructDefenseBuildingType1 = true;
			}

			if (myPlayer.completedUnitCount(UnitType.Zerg_Creep_Colony) > 0) {
				isPossibleToConstructDefenseBuildingType2 = true;
			}

			isPossibleToConstructCombatUnitTrainingBuildingType = true;
		}

		if (isPossibleToConstructDefenseBuildingType1 == true
				&& numberOfMyDefenseBuildingType1 < necessaryNumberOfDefenseBuilding1) {

			if (BuildManager.Instance().buildQueue.getItemCount(myDefenseBuildingType1) == 0) {

				if (BuildManager.Instance().getAvailableMinerals() >= myDefenseBuildingType1.mineralPrice()) {

					BuildManager.Instance().buildQueue.queueAsHighestPriority(myDefenseBuildingType1,
							BuildOrderItem.SeedPositionStrategy.SecondChokePoint, false);
				}
			}
		}

		if (isPossibleToConstructDefenseBuildingType2 == true
				&& numberOfMyDefenseBuildingType2 < necessaryNumberOfDefenseBuilding2) {

			if (BuildManager.Instance().buildQueue.getItemCount(myDefenseBuildingType2) == 0) {

				if (BuildManager.Instance().getAvailableMinerals() >= myDefenseBuildingType2.mineralPrice()) {

					BuildManager.Instance().buildQueue.queueAsHighestPriority(myDefenseBuildingType2,
							BuildOrderItem.SeedPositionStrategy.SecondChokePoint, false);
				}
			}
		}

		// 현재 공격 유닛 생산 건물 갯수

		int numberOfMyCombatUnitTrainingBuilding = 0;
		numberOfMyCombatUnitTrainingBuilding += myPlayer.allUnitCount(UnitType.Zerg_Hatchery);
		numberOfMyCombatUnitTrainingBuilding += myPlayer.allUnitCount(UnitType.Zerg_Lair);
		numberOfMyCombatUnitTrainingBuilding += myPlayer.allUnitCount(UnitType.Zerg_Hive);

		numberOfMyCombatUnitTrainingBuilding += BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery);
		numberOfMyCombatUnitTrainingBuilding += BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Lair);
		numberOfMyCombatUnitTrainingBuilding += BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hive);

		numberOfMyCombatUnitTrainingBuilding += ConstructionManager.Instance()
				.getConstructionQueueItemCount(UnitType.Zerg_Hatchery, null);
		numberOfMyCombatUnitTrainingBuilding += ConstructionManager.Instance()
				.getConstructionQueueItemCount(UnitType.Zerg_Lair, null);
		numberOfMyCombatUnitTrainingBuilding += ConstructionManager.Instance()
				.getConstructionQueueItemCount(UnitType.Zerg_Hive, null);

		// 공격 유닛 생산 건물 증설 : 돈이 남아돌면 실시. 최대 6개 까지만
		if (isPossibleToConstructCombatUnitTrainingBuildingType == true
				&& BuildManager.Instance().getAvailableMinerals() > 400 && numberOfMyCombatUnitTrainingBuilding == 2) {

			// 게이트웨이 / 배럭 / 해처리 증설
			if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery) == 0) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hatchery,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true); /// 해처리 추가 확장 0622
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Extractor,
						BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, true); /// 해처리 추가 확장 0622
			
			System.out.println("AA");
			
			}
		} else if (isPossibleToConstructCombatUnitTrainingBuildingType == true
				&& BuildManager.Instance().getAvailableMinerals() > 400 && numberOfMyCombatUnitTrainingBuilding == 3) {
			if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery) == 0) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hatchery,
						BuildOrderItem.SeedPositionStrategy.FirstChokePoint, true); /// 해처리 추가 확장 0622
				
				System.out.println("BB");
			}
		} else if (isPossibleToConstructCombatUnitTrainingBuildingType == true
				&& BuildManager.Instance().getAvailableMinerals() > 500 && numberOfMyCombatUnitTrainingBuilding < 13) {
			if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery) == 0) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hatchery,
						nextEXP.getTilePosition(), true); /// 해처리 추가 확장 0622
				
				System.out.println("CC");

				if (nextEXP.gas() > 3500) {
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Extractor,
							nextEXP.getTilePosition(), false); /// 해처리 추가 확장 0622
					
					System.out.println("DD");
				}

				// 이거 되면 내 베이스 포문 돌면서 근처에 해처리 갯수보고 근처에 더 짓기 일단 몇 해처리 이상 먹었다 치고

			}

		} else {

		}

	}

	/// 업그레이드 및 테크 리서치를 실행합니다
	void executeUpgradeAndTechResearch() {

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (isInitialBuildOrderFinished == false) {
			return;
		}

		// 업그레이드 및 리서치 대상 설정
		/*
		 * necessaryUpgradeType1 = UpgradeType.U_238_Shells; necessaryUpgradeType2 =
		 * UpgradeType.Terran_Infantry_Weapons;
		 * 
		 * necessaryTechType1 = TechType.Stim_Packs; necessaryTechType2 =
		 * TechType.Tank_Siege_Mode; necessaryTechType3 = TechType.Irradiate;
		 */

		// 1초에 한번만 실행
		if (MyBotModule.Broodwar.getFrameCount() % 24 != 0) {
			return;
		}

		boolean isTimeToStartUpgradeType1 = false; /// 업그레이드할 타이밍인가
		boolean isTimeToStartUpgradeType2 = false; /// 업그레이드할 타이밍인가
		boolean isTimeToStartUpgradeType3 = false; /// 업그레이드할 타이밍인가
		boolean isTimeToStartUpgradeType4 = false; /// 업그레이드할 타이밍인가

		boolean isTimeToStartResearchTech1 = false; /// 리서치할 타이밍인가
		boolean isTimeToStartResearchTech2 = false; /// 리서치할 타이밍인가
		boolean isTimeToStartResearchTech3 = false; /// 리서치할 타이밍인가

		// 업그레이드 / 리서치할 타이밍인지 판단
		if (myRace == Race.Zerg) {
			// 업그레이드 / 리서치를 너무 성급하게 하다가 위험에 빠질 수 있으므로, 최소 저글링 6기 생산 후 업그레이드한다
			if (myPlayer.completedUnitCount(UnitType.Zerg_Spawning_Pool) > 0
					&& myPlayer.completedUnitCount(UnitType.Zerg_Zergling) >= 6) {
				isTimeToStartUpgradeType1 = true;
			}
			// 레어 있으면 오버로드 이속 업
			if (myPlayer.completedUnitCount(UnitType.Zerg_Lair) > 0) {
				isTimeToStartUpgradeType2 = true;
			}
			// 가스 좀 남으면 하라고 넣은건데 저기서 가스가 현재량인지 채굴총량인지???????
			if (myPlayer.completedUnitCount(UnitType.Zerg_Spire) > 0 && myPlayer.gas() > 300 && myPlayer.completedUnitCount(UnitType.Zerg_Mutalisk) >= 5) {
				isTimeToStartUpgradeType3 = true;
			}
			if (myPlayer.completedUnitCount(UnitType.Zerg_Spire) > 0 && myPlayer.gas() > 300 && myPlayer.completedUnitCount(UnitType.Zerg_Mutalisk) >= 5){
				isTimeToStartUpgradeType4 = true;
			}
			// 러커는 최우선으로 리서치한다
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hydralisk_Den) > 0
					&& myPlayer.completedUnitCount(UnitType.Zerg_Lair) > 0) {
				isTimeToStartResearchTech1 = true;
			}
			// 컨슘은 최우선으로 리서치한다
			if (myPlayer.completedUnitCount(UnitType.Zerg_Defiler_Mound) > 0) {
				isTimeToStartResearchTech2 = true;
			}
			// 업그레이드 / 리서치를 너무 성급하게 하다가 위험에 빠질 수 있으므로, 최소 컨슘 리서치 후 리서치한다
			if (myPlayer.completedUnitCount(UnitType.Zerg_Defiler_Mound) > 0
					&& myPlayer.hasResearched(necessaryTechType2) == true) {
				isTimeToStartResearchTech3 = true;
			}
		}

		// 테크 리서치는 높은 우선순위로 우선적으로 실행
		if (isTimeToStartResearchTech1) {
			if (myPlayer.isResearching(necessaryTechType1) == false
					&& myPlayer.hasResearched(necessaryTechType1) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryTechType1) == 0) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(necessaryTechType1, true);
			}
		}

		if (isTimeToStartResearchTech2) {
			if (myPlayer.isResearching(necessaryTechType2) == false
					&& myPlayer.hasResearched(necessaryTechType2) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryTechType2) == 0) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(necessaryTechType2, true);
			}
		}

		if (isTimeToStartResearchTech3) {
			if (myPlayer.isResearching(necessaryTechType3) == false
					&& myPlayer.hasResearched(necessaryTechType3) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryTechType3) == 0) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(necessaryTechType3, true);
			}
		}

		// 업그레이드는 낮은 우선순위로 실행
		if (isTimeToStartUpgradeType1) {
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType1) == 0
					&& myPlayer.isUpgrading(necessaryUpgradeType1) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryUpgradeType1) == 0) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(necessaryUpgradeType1, false);
			}
		}

		if (isTimeToStartUpgradeType2) {
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType2) == 0
					&& myPlayer.isUpgrading(necessaryUpgradeType2) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryUpgradeType2) == 0) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(necessaryUpgradeType2, false);
			}
		}

		if (isTimeToStartUpgradeType3) {
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType3) == 0
					&& myPlayer.isUpgrading(necessaryUpgradeType3) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryUpgradeType3) == 0) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(necessaryUpgradeType3, false);
			}
		}

		if (isTimeToStartUpgradeType4) {
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType4) == 0
					&& myPlayer.isUpgrading(necessaryUpgradeType4) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryUpgradeType4) == 0) {
				BuildManager.Instance().buildQueue.queueAsLowestPriority(necessaryUpgradeType4, false);
			}
		}

		// BWAPI 4.1.2 의 버그때문에, 오버로드 업그레이드를 위해서는 반드시 Zerg_Lair 가 있어야함
		if (myRace == Race.Zerg) {
			if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Pneumatized_Carapace) > 0) {
				if (myPlayer.allUnitCount(UnitType.Zerg_Lair) == 0
						&& BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Lair) == 0) {
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Lair, false);
				}
			}
		}
	}

	/// 공격유닛을 계속 추가 생산합니다
	public void executeCombatUnitTraining() {

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (isInitialBuildOrderFinished == false) {
			return;
		}

		// 1초에 4번만 실행
		if (MyBotModule.Broodwar.getFrameCount() % 6 != 0) {
			return;
		}

		if (myPlayer.supplyUsed() <= 390) {

			for (Unit unit : myPlayer.getUnits()) {

				if (unit.getType() == InformationManager.Instance().getBasicCombatBuildingType()) {

					if (unit.isTraining() == false || unit.getLarva().size() > 0) {

						UnitType nextUnitTypeToTrain = getNextCombatUnitTypeToTrain();

						if (BuildManager.Instance().buildQueue.getItemCount(nextUnitTypeToTrain) == 0) {

							BuildManager.Instance().buildQueue.queueAsLowestPriority(nextUnitTypeToTrain, false);
							nextTargetIndexOfBuildOrderArray++;

							if (nextTargetIndexOfBuildOrderArray >= buildOrderArrayOfMyCombatUnitType.length) {
								nextTargetIndexOfBuildOrderArray = 0;
							}
						}
					}
				}
			}
		}
	}

	/// 다음에 생산할 공격유닛 UnitType 을 리턴합니다
	public UnitType getNextCombatUnitTypeToTrain() {

		UnitType nextUnitTypeToTrain = null;

		if (buildOrderArrayOfMyCombatUnitType[nextTargetIndexOfBuildOrderArray] == 1) {
			if (myCombatUnitType1List.size() < 2.0 * myCombatUnitType2List.size()) {
				nextUnitTypeToTrain = myCombatUnitType1;
			} else {
				nextUnitTypeToTrain = myCombatUnitType2;
			}
		}

		else if (buildOrderArrayOfMyCombatUnitType[nextTargetIndexOfBuildOrderArray] == 2) {

			if (myCombatUnitType1List.size() < 2.0 * myCombatUnitType2List.size()) {
				nextUnitTypeToTrain = myCombatUnitType1;
			} else {
				nextUnitTypeToTrain = myCombatUnitType2;
			}
		} else if (buildOrderArrayOfMyCombatUnitType[nextTargetIndexOfBuildOrderArray] == 3) {

			if (myCombatUnitType1List.size() < 2.0 * myCombatUnitType2List.size()) {
				nextUnitTypeToTrain = myCombatUnitType1;
			} else {
				nextUnitTypeToTrain = myCombatUnitType2;
			}
		} else if (buildOrderArrayOfMyCombatUnitType[nextTargetIndexOfBuildOrderArray] == 4) // myCombatUnitType4List.size()
																								// <=
																								// MaxNumberOfCombatUnitType4
																								// )
		{
			/*
			 * int cnt = 0;
			 * 
			 * for (Unit unit : myAllCombatUnitList) { if(unit.getType() ==
			 * UnitType.Terran_Science_Vessel) { cnt = cnt +1; } }
			 */
			if (myCombatUnitType1List.size() < 2.0 * myCombatUnitType2List.size()) {
				nextUnitTypeToTrain = myCombatUnitType1;
			} else {
				nextUnitTypeToTrain = myCombatUnitType2;
			}

			// maxnumber 수정. 노승호 170808
		}

		else if (buildOrderArrayOfMyCombatUnitType[nextTargetIndexOfBuildOrderArray] == 5) {

			if (myCombatUnitType1List.size() < 2.0 * myCombatUnitType2List.size()) {
				nextUnitTypeToTrain = myCombatUnitType1;
			} else {
				nextUnitTypeToTrain = myCombatUnitType2;
			}
		} else {
			;
		}

		return nextUnitTypeToTrain;
	}

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가
	/// 과거 전체 게임 기록을 로딩합니다

	void loadGameRecordList() {

		// 과거의 게임에서 bwapi-data\write 폴더에 기록했던 파일은 대회 서버가 bwapi-data\read 폴더로 옮겨놓습니다
		// 따라서, 파일 로딩은 bwapi-data\read 폴더로부터 하시면 됩니다

		// TODO : 파일명은 각자 봇 명에 맞게 수정하시기 바랍니다

		String gameRecordFileName = "bwapi-data\\read\\BullBug_GameRecord.dat";

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(gameRecordFileName));

			System.out.println("loadGameRecord from file: " + gameRecordFileName);
			String currentLine;
			StringTokenizer st;
			GameRecord tempGameRecord;

			while ((currentLine = br.readLine()) != null) {

				st = new StringTokenizer(currentLine, " ");
				tempGameRecord = new GameRecord();
				if (st.hasMoreTokens()) {
					tempGameRecord.mapName = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.myName = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.myRace = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.myWinCount = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.myLoseCount = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.enemyName = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.enemyRace = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.enemyRealRace = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					tempGameRecord.gameFrameCount = Integer.parseInt(st.nextToken());
				}

				gameRecordList.add(tempGameRecord);
			}
		} catch (FileNotFoundException e) {
			System.out.println("loadGameRecord failed. Could not open file :" + gameRecordFileName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/// 과거 전체 게임 기록 + 이번 게임 기록을 저장합니다
	void saveGameRecordList(boolean isWinner) {

		// 이번 게임의 파일 저장은 bwapi-data\write 폴더에 하시면 됩니다.
		// bwapi-data\write 폴더에 저장된 파일은 대회 서버가 다음 경기 때 bwapi-data\read 폴더로 옮겨놓습니다
		// TODO : 파일명은 각자 봇 명에 맞게 수정하시기 바랍니다

		String gameRecordFileName = "bwapi-data\\write\\BullBug_GameRecord.dat";

		System.out.println("saveGameRecord to file: " + gameRecordFileName);

		String mapName = MyBotModule.Broodwar.mapFileName();
		mapName = mapName.replace(' ', '_');
		String enemyName = MyBotModule.Broodwar.enemy().getName();
		enemyName = enemyName.replace(' ', '_');
		String myName = MyBotModule.Broodwar.self().getName();
		myName = myName.replace(' ', '_');

		/// 이번 게임에 대한 기록
		GameRecord thisGameRecord = new GameRecord();
		thisGameRecord.mapName = mapName;
		thisGameRecord.myName = myName;
		thisGameRecord.myRace = MyBotModule.Broodwar.self().getRace().toString();
		thisGameRecord.enemyName = enemyName;
		thisGameRecord.enemyRace = MyBotModule.Broodwar.enemy().getRace().toString();
		thisGameRecord.enemyRealRace = InformationManager.Instance().enemyRace.toString();
		thisGameRecord.gameFrameCount = MyBotModule.Broodwar.getFrameCount();

		if (isWinner) {
			thisGameRecord.myWinCount = 1;
			thisGameRecord.myLoseCount = 0;
		}

		else {
			thisGameRecord.myWinCount = 0;
			thisGameRecord.myLoseCount = 1;
		}

		// 이번 게임 기록을 전체 게임 기록에 추가

		gameRecordList.add(thisGameRecord);

		// 전체 게임 기록 write
		StringBuilder ss = new StringBuilder();

		for (GameRecord gameRecord : gameRecordList) {

			ss.append(gameRecord.mapName + " ");
			ss.append(gameRecord.myName + " ");
			ss.append(gameRecord.myRace + " ");
			ss.append(gameRecord.myWinCount + " ");
			ss.append(gameRecord.myLoseCount + " ");
			ss.append(gameRecord.enemyName + " ");
			ss.append(gameRecord.enemyRace + " ");
			ss.append(gameRecord.enemyRealRace + " ");
			ss.append(gameRecord.gameFrameCount + "\n");
		}

		Common.overwriteToFile(gameRecordFileName, ss.toString());
	}

	/// 이번 게임 중간에 상시적으로 로그를 저장합니다
	void saveGameLog() {

		// 100 프레임 (5초) 마다 1번씩 로그를 기록합니다
		// 참가팀 당 용량 제한이 있고, 타임아웃도 있기 때문에 자주 하지 않는 것이 좋습니다
		// 로그는 봇 개발 시 디버깅 용도로 사용하시는 것이 좋습니다

		if (MyBotModule.Broodwar.getFrameCount() % 100 != 0) {
			return;
		}

		// TODO : 파일명은 각자 봇 명에 맞게 수정하시기 바랍니다
		String gameLogFileName = "bwapi-data\\write\\Bullbug_LastGameLog.dat";
		String mapName = MyBotModule.Broodwar.mapFileName();
		mapName = mapName.replace(' ', '_');
		String enemyName = MyBotModule.Broodwar.enemy().getName();
		enemyName = enemyName.replace(' ', '_');
		String myName = MyBotModule.Broodwar.self().getName();
		myName = myName.replace(' ', '_');
		StringBuilder ss = new StringBuilder();
		ss.append(mapName + " ");
		ss.append(myName + " ");
		ss.append(MyBotModule.Broodwar.self().getRace().toString() + " ");
		ss.append(enemyName + " ");
		ss.append(InformationManager.Instance().enemyRace.toString() + " ");
		ss.append(MyBotModule.Broodwar.getFrameCount() + " ");
		ss.append(MyBotModule.Broodwar.self().supplyUsed() + " ");
		ss.append(MyBotModule.Broodwar.self().supplyTotal() + "\n");

		Common.appendTextToFile(gameLogFileName, ss.toString());
	}

	// BasicBot 1.1 Patch End //////////////////////////////////////////////////

	private void drawStrategyManagerStatus() {

		int y = 250;

		// 아군 공격유닛 숫자 및 적군 공격유닛 숫자
		MyBotModule.Broodwar.drawTextScreen(200, y, "My " + myCombatUnitType1.toString());
		MyBotModule.Broodwar.drawTextScreen(350, y, "alive " + myCombatUnitType1List.size());
		MyBotModule.Broodwar.drawTextScreen(400, y, "killed " + myKilledCombatUnitCount1);
		y += 10;
		MyBotModule.Broodwar.drawTextScreen(200, y, "My " + myCombatUnitType2.toString());
		MyBotModule.Broodwar.drawTextScreen(350, y, "alive " + myCombatUnitType2List.size());
		MyBotModule.Broodwar.drawTextScreen(400, y, "killed " + myKilledCombatUnitCount2);
		y += 10;
		MyBotModule.Broodwar.drawTextScreen(200, y, "My " + myCombatUnitType3.toString());
		MyBotModule.Broodwar.drawTextScreen(350, y, "alive " + myCombatUnitType3List.size());
		MyBotModule.Broodwar.drawTextScreen(400, y, "killed " + myKilledCombatUnitCount3);
		y += 10;
		MyBotModule.Broodwar.drawTextScreen(200, y, "Enemy CombatUnit");
		MyBotModule.Broodwar.drawTextScreen(350, y, "alive " + numberOfCompletedEnemyCombatUnit);
		MyBotModule.Broodwar.drawTextScreen(400, y, "killed " + enemyKilledCombatUnitCount);
		y += 10;
		MyBotModule.Broodwar.drawTextScreen(200, y, "Enemy WorkerUnit");
		MyBotModule.Broodwar.drawTextScreen(350, y, "alive " + numberOfCompletedEnemyWorkerUnit);
		MyBotModule.Broodwar.drawTextScreen(400, y, "killed " + enemyKilledWorkerUnitCount);
		y += 20;

		// setInitialBuildOrder 에서 입력한 빌드오더가 다 끝나서 빌드오더큐가 empty 되었는지 여부
		MyBotModule.Broodwar.drawTextScreen(200, y, "isInitialBuildOrderFinished " + isInitialBuildOrderFinished);
		y += 10;
		// 전투 상황
		MyBotModule.Broodwar.drawTextScreen(200, y, "combatState " + combatState.ordinal());
	}

	private static StrategyManager instance = new StrategyManager();

	/// static singleton 객체를 리턴합니다
	public static StrategyManager Instance() {
		return instance;
	}

	private CommandUtil commandUtil = new CommandUtil();

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가를 위한 변수 및 메소드 선언
	/// 한 게임에 대한 기록을 저장하는 자료구조

	private class GameRecord {
		String mapName;
		String enemyName;
		String enemyRace;
		String enemyRealRace;
		String myName;
		String myRace;
		int gameFrameCount = 0;
		int myWinCount = 0;
		int myLoseCount = 0;
	}

	/// 과거 전체 게임들의 기록을 저장하는 자료구조
	ArrayList<GameRecord> gameRecordList = new ArrayList<GameRecord>();
	// BasicBot 1.1 Patch End //////////////////////////////////////////////////

	/// 경기가 종료될 때 일회적으로 전략 결과 정리 관련 로직을 실행합니다
	public void onEnd(boolean isWinner) {

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 경기 결과 파일 Save / Load 및 로그파일 Save 예제 추가
		// 과거 게임 기록 + 이번 게임 기록을 저장합니다
		saveGameRecordList(isWinner);
		// BasicBot 1.1 Patch End //////////////////////////////////////////////////
	}

	/// 변수 값을 업데이트 합니다
	void updateVariables() {

		enemyRace = InformationManager.Instance().enemyRace;

		if (BuildManager.Instance().buildQueue.isEmpty()) {
			isInitialBuildOrderFinished = true;
		}

		// 적군의 공격유닛 숫자
		numberOfCompletedEnemyCombatUnit = 0;
		numberOfCompletedEnemyWorkerUnit = 0;

		for (Map.Entry<Integer, UnitInfo> unitInfoEntry : InformationManager.Instance()
				.getUnitAndUnitInfoMap(enemyPlayer).entrySet()) {

			UnitInfo enemyUnitInfo = unitInfoEntry.getValue();

			if (enemyUnitInfo.getType().isWorker() == false && enemyUnitInfo.getType().canAttack()
					&& enemyUnitInfo.getLastHealth() > 0) {

				numberOfCompletedEnemyCombatUnit++;
			}

			if (enemyUnitInfo.getType().isWorker() == true) {

				numberOfCompletedEnemyWorkerUnit++;
			}
		}

		// 아군 / 적군의 본진, 첫번째 길목, 두번째 길목

		myMainBaseLocation = InformationManager.Instance().getMainBaseLocation(myPlayer);
		myFirstExpansionLocation = InformationManager.Instance().getFirstExpansionLocation(myPlayer);
		myFirstChokePoint = InformationManager.Instance().getFirstChokePoint(myPlayer);
		mySecondChokePoint = InformationManager.Instance().getSecondChokePoint(myPlayer);
		enemyMainBaseLocation = InformationManager.Instance().getMainBaseLocation(enemyPlayer);
		enemyFirstExpansionLocation = InformationManager.Instance().getFirstExpansionLocation(enemyPlayer);
		enemyFirstChokePoint = InformationManager.Instance().getFirstChokePoint(enemyPlayer);
		enemySecondChokePoint = InformationManager.Instance().getSecondChokePoint(enemyPlayer);

		// 아군 방어 건물 목록, 공격 유닛 목록
		myDefenseBuildingType1List.clear();
		myDefenseBuildingType2List.clear();

		myAllCombatUnitList.clear();
		myCombatUnitType1List.clear();

		myCombatUnitType2List.clear();
		myCombatUnitType3List.clear();

		for (Unit unit : myPlayer.getUnits()) {
			if (unit == null || unit.exists() == false || unit.getHitPoints() <= 0)
				continue;

			if (unit.getType() == myCombatUnitType1) {
				myCombatUnitType1List.add(unit);
				myAllCombatUnitList.add(unit);
			} else if (unit.getType() == myCombatUnitType2) {
				myCombatUnitType2List.add(unit);
				myAllCombatUnitList.add(unit);
			} else if (unit.getType() == myCombatUnitType3 || unit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
				myCombatUnitType3List.add(unit);
				myAllCombatUnitList.add(unit);
			} else if (unit.getType() == myCombatUnitType4 || unit.getType() == UnitType.Terran_Science_Vessel) {
				myCombatUnitType4List.add(unit);
				myAllCombatUnitList.add(unit);
			} //////////////////////////// 20170806 권순우 베슬을 리스트에 추가
			else if (unit.getType() == myCombatUnitType5 || unit.getType() == UnitType.Terran_Wraith) {
				myCombatUnitType5List.add(unit);
				myAllCombatUnitList.add(unit);
			} else if (unit.getType() == myDefenseBuildingType1) {
				myDefenseBuildingType1List.add(unit);
			} else if (unit.getType() == myDefenseBuildingType2) {
				myDefenseBuildingType2List.add(unit);
			}
		}
	}

	/// 권순우 0617 죽은 유닛을 세는 단순한 로직인데
	/// 시즈탱크가 탱크상태로 죽는것과 시즈상태로 죽은 것이 서로 다르게 분류되서
	/// 이런 것을 보정해줄 필요가 있다
	/// 저그에도 이런 것이 있으려나
	/// 아군 / 적군 공격 유닛 사망 유닛 숫자 누적값을 업데이트 합니다
	public void onUnitDestroy(Unit unit) {
		if (unit.getType().isNeutral()) {
			return;
		}

		if (unit.getPlayer() == myPlayer) {
			if (unit.getType() == myCombatUnitType1) {
				myKilledCombatUnitCount1++;
			} else if (unit.getType() == myCombatUnitType2) {
				myKilledCombatUnitCount2++;
			} else if (unit.getType() == myCombatUnitType3) {
				myKilledCombatUnitCount3++;
			} else if (myCombatUnitType3 == UnitType.Terran_Siege_Tank_Tank_Mode
					&& unit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
				myKilledCombatUnitCount3++;
			}
		} else if (unit.getPlayer() == enemyPlayer) {

			/// 적군 공격 유닛타입의 사망 유닛 숫자 누적값
			if (unit.getType().isWorker() == false && unit.getType().isBuilding() == false) {
				enemyKilledCombatUnitCount++;
			}
			/// 적군 일꾼 유닛타입의 사망 유닛 숫자 누적값
			if (unit.getType().isWorker() == true) {
				enemyKilledWorkerUnitCount++;
			}
		}
	}

}