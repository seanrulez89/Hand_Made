
import java.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;


//import StrategyManager.CombatState;
import bwapi.*;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.Unitset;
import bwapi.UpgradeType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

public class StrategyManager {


	
	static int attack_cnt;
	public Position attackTargetPosition;

	UnitControl_MASTER UnitControl_MASTER;


	// 아군
	public Player myPlayer;
	public Race myRace;

	// 적군
	public Player enemyPlayer;
	public Race enemyRace;

	public int[] buildOrderArrayOfMyCombatUnitType; /// 아군 공격 유닛 생산 순서
	public int nextTargetIndexOfBuildOrderArray; /// buildOrderArrayMyCombatUnitType 에서 다음 생산대상 아군 공격 유닛

	// 아군 공격 유닛 종류(타입)
	public UnitType myZergling; /// 저글링
	public UnitType myMutalisk; /// 뮤탈리스크
	public UnitType myUltralisk; /// #미정
	public UnitType myHydralisk; /// #미정
	public UnitType myLurker; /// #미정
	public UnitType myDefiler;

	// 아군 특수 유닛 첫번째, 두번째 타입
	public UnitType mySpecialUnitType1; /// 옵저버 사이언스베쓸 오버로드
	public UnitType mySpecialUnitType2; /// 하이템플러 배틀크루저 디파일러

	// 아군의 공격유닛 최소 필요 숫자
	public int necessaryNumberOfZergling; /// 공격을 시작하기위해 필요한 최소한의 유닛 숫자
	public int necessaryNumberOfMutalisk; /// 공격을 시작하기위해 필요한 최소한의 유닛 숫자
	public int necessaryNumberOfUltralisk; /// 공격을 시작하기위해 필요한 최소한의 유닛 숫자
	public int necessaryNumberOfHydralisk; /// 공격을 시작하기위해 필요한 최소한의 유닛 숫자
	public int necessaryNumberOfLurker;

	public int myKilledZerglings; /// 첫번째 유닛 타입의 사망자 숫자 누적값
	public int myKilledMutalisks; /// 두번째 유닛 타입의 사망자 숫자 누적값
	public int myKilledUltralisks;
	public int myKilledHydralisks;
	public int myKilledLurkers;
	public int myKilledDefilers;

	public int numberOfCompletedZerglings; /// 첫번째 유닛 타입의 현재 유닛 숫자
	public int numberOfCompletedMutalisks; /// 두번째 유닛 타입의 현재 유닛 숫자
	public int numberOfCompletedUltralisks; /// 세번째 유닛 타입의 현재 유닛 숫자
	public int numberOfCompletedHydralisks; /// 네번째 유닛 타입의 현재 유닛 숫자
	public int numberOfCompletedLurkers;
	public int numberOfCompletedDefilers;


	// 아군 공격 유닛 목록
	public ArrayList<Unit> myAllCombatUnitList = new ArrayList<Unit>();
	public ArrayList<Unit> myZerglingList = new ArrayList<Unit>(); // 저글링
	public ArrayList<Unit> myMutaliskList = new ArrayList<Unit>(); // 뮤탈리스크
	public ArrayList<Unit> myUltraliskList = new ArrayList<Unit>(); // 울트라리스크
	public ArrayList<Unit> myHydraliskList = new ArrayList<Unit>(); // #미정
	public ArrayList<Unit> myLurkerList = new ArrayList<Unit>(); // #미정
	public ArrayList<Unit> myDefilerList = new ArrayList<Unit>(); // #미정
	

	// 아군 방어 건물 첫번째 타입
	public UnitType myCreepColony; /// 파일런 벙커 크립콜로니

	// 아군 방어 건물 두번째 타입
	public UnitType mySunkenColony; /// 포톤 터렛 성큰콜로니

	// 아군 방어 건물 건설 숫자
	public int necessaryNumberOfCreepColony; /// 방어 건물 건설 갯수
	public int necessaryNumberOfSunkenColony; /// 방어 건물 건설 갯수

	// 아군 방어 건물 건설 위치
	public BuildOrderItem.SeedPositionStrategy seedPositionStrategyOfMyDefenseBuildingType; // 권순우 0617 현상태의 전략 거점이 어딘가를 판단하는것으로 공격 방어와 연계되어야 함																						
	public BuildOrderItem.SeedPositionStrategy seedPositionStrategyOfMyCombatUnitTrainingBuildingType;
	public BuildOrderItem.SeedPositionStrategy buildAtFirstChokePoint;
	public BuildOrderItem.SeedPositionStrategy SeedPositionSpecified;

	// 아군 방어 건물 목록
	public 	ArrayList<Unit> myCreepColonyList = new ArrayList<Unit>(); // 파일런 벙커 크립
	public ArrayList<Unit> mySunkenColonyList = new ArrayList<Unit>(); // 캐논 터렛 성큰

	// 업그레이드 / 리서치 할 것
	public UpgradeType necessaryUpgradeType1; ///
	public UpgradeType necessaryUpgradeType2; ///
	public UpgradeType necessaryUpgradeType3;
	public UpgradeType necessaryUpgradeType4;

	public TechType necessaryTechType1; ///
	public TechType necessaryTechType2; ///
	public TechType necessaryTechType3; ///

	// 적군 공격 유닛 숫자
	public int numberOfCompletedEnemyCombatUnit;
	public int numberOfCompletedEnemyWorkerUnit;

	// 적군 공격 유닛 사망자 수
	public int enemyKilledCombatUnitCount; /// 적군 공격유닛 사망자 숫자 누적값
	public int enemyKilledWorkerUnitCount; /// 적군 일꾼유닛 사망자 숫자 누적값

	// 아군 / 적군의 본진, 첫번째 길목, 두번째 길목
	public BaseLocation myMainBaseLocation;
	public BaseLocation myFirstExpansionLocation;

	public Chokepoint myFirstChokePoint;
	public Chokepoint mySecondChokePoint;

	public BaseLocation enemyMainBaseLocation;
	public BaseLocation enemyFirstExpansionLocation;

	public Chokepoint enemyFirstChokePoint;
	public Chokepoint enemySecondChokePoint;

	public boolean isInitialBuildOrderFinished; /// setInitialBuildOrder 에서 입력한 빌드오더가 다 끝나서 빌드오더큐가 empty 되었는지 여부

	public enum CombatState {
		defenseMode, // 아군 진지 방어
		attackStarted, // 적 공격 시작
		attackMySecondChokepoint, // 아군 두번째 길목까지 공격
		attackEnemySecondChokepoint, // 적진 두번째 길목까지 공격
		attackEnemyFirstExpansionLocation, // 적진 앞마당까지 공격
		attackEnemyFirstChokepoint, attackEnemyMainBaseLocation, // 적진 본진까지 공격
		eliminateEnemy // 적 Eliminate
	};
	public CombatState combatState; /// 전투 상황


	public StrategyManager() {

	}

	/// 경기가 시작될 때 일회적으로 전략 초기 세팅 관련 로직을 실행합니다

	public void onStart() {

		// 과거 게임 기록을 로딩합니다
		// loadGameRecordList(); 자꾸 에러가 나서 주석처리함

		/// 변수 초기값을 설정합니다
		setVariables();
		
		UnitControl_MASTER = new UnitControl_MASTER();
		

		BuildOrder_Initial initialBuildOrder = new BuildOrder_Initial();
		initialBuildOrder.setInitialBuildOrder();
		
		

		
		
		


	}

	/// 변수 초기값을 설정합니다
	void setVariables() {
		
		attack_cnt = 0;
		attackTargetPosition = null;
		

		myPlayer = MyBotModule.Broodwar.self();
		myRace = MyBotModule.Broodwar.self().getRace();
		enemyPlayer = InformationManager.Instance().enemyPlayer;

		myKilledZerglings = 0; // 죽은 마린 숫자
		myKilledMutalisks = 0;
		myKilledUltralisks = 0;
		myKilledHydralisks = 0;
		myKilledLurkers = 0;

		numberOfCompletedEnemyCombatUnit = 0;
		numberOfCompletedEnemyWorkerUnit = 0;

		enemyKilledCombatUnitCount = 0;
		enemyKilledWorkerUnitCount = 0;

		isInitialBuildOrderFinished = false;
		combatState = CombatState.defenseMode;

		if (myRace == Race.Zerg) {

			// 공격 유닛 종류 설정
			myZergling = UnitType.Zerg_Zergling;
			myMutalisk = UnitType.Zerg_Mutalisk;
			myUltralisk = UnitType.Zerg_Ultralisk;
			myHydralisk = UnitType.Zerg_Hydralisk;
			myLurker = UnitType.Zerg_Lurker;
			myDefiler = UnitType.Zerg_Defiler;

			// 특수 유닛 종류 설정
			mySpecialUnitType1 = UnitType.Zerg_Overlord;
			mySpecialUnitType2 = UnitType.Zerg_Defiler;

			// 공격 모드로 전환하기 위해 필요한 최소한의 유닛 숫자 설정
			necessaryNumberOfZergling = 18; // 공격을 시작하기위해 필요한 최소한의 마린 유닛 숫자
			necessaryNumberOfMutalisk = 5; // 공격을 시작하기위해 필요한 최소한의 메딕 유닛 숫자
			necessaryNumberOfUltralisk = 4; // 공격을 시작하기위해 필요한 최소한의 메딕 유닛 숫자
			// necessaryNumberOfCombatUnitType4 = 1; // 공격을 시작하기위해 필요한 최소한의 메딕 유닛 숫자
			// necessaryNumberOfLurker = 2;

			// MaxNumberOfCombatUnitType4 = 4 ;

			// 공격 유닛 생산 순서 설정
			buildOrderArrayOfMyCombatUnitType = new int[] {1,2,3,4}; // 마린 마린 마린 메딕 시즈 베슬
			nextTargetIndexOfBuildOrderArray = 0; // 다음 생산 순서 index

			// 방어 건물 종류 및 건설 갯수 설정
			myCreepColony = UnitType.Zerg_Creep_Colony;
			necessaryNumberOfCreepColony = 2;
			mySunkenColony = UnitType.Zerg_Sunken_Colony;
			necessaryNumberOfSunkenColony = 2;

			// 방어 건물 건설 위치 설정
			seedPositionStrategyOfMyDefenseBuildingType = BuildOrderItem.SeedPositionStrategy.FirstChokePoint; // 앞마당
			seedPositionStrategyOfMyCombatUnitTrainingBuildingType = BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation; // 앞마당
			buildAtFirstChokePoint = BuildOrderItem.SeedPositionStrategy.FirstChokePoint;
			SeedPositionSpecified = BuildOrderItem.SeedPositionStrategy.SeedPositionSpecified;

			// 업그레이드 및 리서치 대상 설정
			necessaryUpgradeType1 = UpgradeType.Metabolic_Boost;
			necessaryUpgradeType2 = UpgradeType.Ventral_Sacs; //UpgradeType.Pneumatized_Carapace;
			necessaryUpgradeType3 = UpgradeType.Zerg_Flyer_Attacks; // 공중공업
			necessaryUpgradeType4 = UpgradeType.Zerg_Flyer_Carapace; // 공중방업

			necessaryTechType1 = TechType.Lurker_Aspect;
			necessaryTechType2 = TechType.Consume;
			necessaryTechType3 = TechType.Plague;
		}
	}

	/// 경기 진행 중 매 프레임마다 경기 전략 관련 로직을 실행합니다
	public void update() {
		
		//drawStrategyManagerStatus(); 2017년 버전의 화면 정보 나타내기. 화면이 중첩되서 주석처리함

		//saveGameLog(); 자꾸 에러가 나서 주석처리함

		/// 변수 값을 업데이트 합니다
		updateVariables();
	
		/// 일꾼을 계속 추가 생산합니다
		executeWorkerTraining();
		
		/// Supply DeadLock 예방 및 SupplyProvider 가 부족해질 상황 에 대한 선제적 대응으로서 SupplyProvider를 추가 건설/생산합니다
		executeSupplyManagement();

		/// 방어건물 및 공격유닛 생산 건물을 건설합니다
		executeBuildingConstruction();
					
		/// 업그레이드 및 테크 리서치를 실행합니다
		executeUpgradeAndTechResearch();

		/// 공격유닛을 계속 추가 생산합니다
		executeCombatUnitTraining();

		/// 전반적인 전투 로직 을 갖고 전투를 수행합니다
		executeCombat();

		
		BuildOrder_Last.Instance().lastBuildOrder();
		

		/*
		if(MyBotModule.Broodwar.getFrameCount() % (24*10) == 0)
		{		
	   		MyBotModule.Broodwar.sendText("APM : " + MyBotModule.Broodwar.getAPM());
		}
		*/
		 


		UnitControl_MASTER.update();
		



	}

	
	
	// 권순우 0617 방어 <-> 공격 <-> 앨리(elimination) 3 단계로 구분
	public void executeCombat() {

		int y = 250;

		// 공격을 시작할만한 상황이 되기 전까지는 방어를 합니다
		if (combatState == CombatState.defenseMode) {

			/// 아군 공격유닛 들에게 방어를 지시합니다
			MyBotModule.Broodwar.drawTextScreen(100, y, "Defense Mode");

			commandMyCombatUnitToDefense();


			/// 공격 모드로 전환할 때인지 여부를 판단합니다
			if (isTimeToStartAttack()) {
				MyBotModule.Broodwar.drawTextScreen(100, y, "Attack Mode" + attack_cnt);
				combatState = CombatState.attackStarted;
			}
			
			
			/// 적군을 Eliminate 시키는 모드로 전환할지 여부를 판단합니다
			if (isTimeToStartElimination()) {
				MyBotModule.Broodwar.drawTextScreen(100, y, "Eliminate Mode");
				combatState = CombatState.eliminateEnemy;
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
	

	boolean isTimeToStartAttack() {

		MyBotModule.Broodwar.drawTextScreen(100, 240, "Wave Count : " + attack_cnt);
			
		if (myPlayer.completedUnitCount(UnitType.Zerg_Mutalisk) > 10
				|| myPlayer.completedUnitCount(UnitType.Zerg_Lurker) > 4) {

			if (myHydraliskList.size() > 18) {
				attack_cnt = attack_cnt + 1;
				return true;
			}

		}
		
		if (myPlayer.completedUnitCount(UnitType.Zerg_Zergling) > 30) {
			if (myHydraliskList.size() > 18) {
				attack_cnt = attack_cnt + 1;
				return true;
			}
		}
		
		
		
		return false;
	}

	// 방어 모드로 전환할 때인지 여부를 리턴합니다
	boolean isTimeToStartDefense() {

		

		/*
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
			return true;
		}
		*/
		
		if(UnitControl_COMMON.defenseSite!=null)
		{
			return true;
		}
		
		
		
		if (myHydraliskList.size() < 12) {
			return true;
		}
		
		
		
		
		

		return false;
	}

	
	
	
	
	

	/// 적군을 Eliminate 시키는 모드로 전환할지 여부를 리턴합니다
	boolean isTimeToStartElimination() {
		
		if(enemyMainBaseLocation==null)
		{
			return false;
		}
		
		
		
		// 적군 본진에 아군 유닛이 30 이상 도착했으면 거의 게임 끝난 것
		int myUnitCountAroundEnemyMainBaseLocation = 0;
		for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 8 * Config.TILE_SIZE)) 
		{
			if (unit.getPlayer() == myPlayer) {
				myUnitCountAroundEnemyMainBaseLocation++;
			}
		}
		
		if (myUnitCountAroundEnemyMainBaseLocation > 24) {
			System.out.println("eli case 1");
			return true;
		}

		// 30분이 경과했고 내 유닛이 80 이상이라면 
		if (MyBotModule.Broodwar.getFrameCount() > 24*60*30 && myPlayer.supplyUsed() > 100 * 2) {
			System.out.println("eli case 2");
			return true;
		}

		return false;
	}

	/// 권순우 0617 방어를 하다가 적군에 끌어 당겨지지 않게
	/// 일정 범위 이상 나가면 다시 원래 자리로 돌아오는 코드가 필요함
	/// 아군 공격유닛 들에게 방어를 지시합니다
	void commandMyCombatUnitToDefense() {

		// 아군 방어 건물이 세워져있는 위치
		Position myDefenseBuildingPosition = null;
		
		// seedPositionStrategyOfMyDefenseBuildingType 이것이 정하는 위치가 곧 집결지가 된다
		// 아래 사전 정의된 경우를 제외하고서는 specified를 쓰면 된다
		// 활용이 어렵다. 가령 공격을 동시에 여러 지점에서 받는다면 어디로 갈것인가?
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

		case SeedPositionSpecified: // 샘플 코드이다. 이렇게 하면 다음 확장기지가 건설될 위치로 집결할 것이다.
			myDefenseBuildingPosition = bwta.BWTA.getNearestChokepoint(BuildOrder_Expansion.expansion().getPosition()).getCenter();
			break;
						
		default:
			myDefenseBuildingPosition = myMainBaseLocation.getPosition();
			break;
		}
		

		for (Unit unit : myAllCombatUnitList) {
			if (unit == null || unit.exists() == false)
				continue;

			if(unit.getType().equals(UnitType.Zerg_Ultralisk)) // 0627 뮤탈코드 테스트를 위한 건너뛰기 조치
			{
				commandUtil.attackMove(unit, myDefenseBuildingPosition);
			}
			
			
		}

	}



	/// 아군 공격 유닛들에게 공격을 지시합니다
	public void commandMyCombatUnitToAttack() {

		for (Unit unit : myAllCombatUnitList) 
		{
			if(unit.getType().equals(UnitType.Zerg_Ultralisk))
			{
				commandUtil.attackMove(unit, enemyMainBaseLocation.getPosition());
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

			

			// 따로 명령 내린 적이 없으면, 적군의 남은 건물 혹은 랜덤 위치로 이동시킨다
			

				if (unit.isIdle()) 
				{

					Position targetPosition = null;
					if (targetEnemyBuilding != null) 
					{
						targetPosition = targetEnemyBuilding.getPosition();
					} 
					else 
					{
						targetPosition = new Position(random.nextInt(mapWidth * Config.TILE_SIZE),
								random.nextInt(mapHeight * Config.TILE_SIZE));
					}

					if (unit.canAttack()) 
					{
						commandUtil.attackMove(unit, targetPosition);
						
					} 
					else 
					{
						// canAttack 기능이 없는 유닛타입 예를 들면 럴커
						if (unit.getType() == UnitType.Zerg_Lurker) 
						{
							if(unit.getDistance(targetPosition) < 6*Config.TILE_SIZE)
							{
								if(unit.isBurrowed()==false)
								{
									unit.burrow();
								}
							}
							else
							{
								if(unit.isBurrowed()==true)
								{
									unit.unburrow();
								}
								
								commandUtil.move(unit, targetPosition);
							}	
						}
						else // 오버로드
						{
							commandUtil.move(unit, targetPosition);	
						}
					}
				}
			
		}
	}


	// 일꾼 계속 추가 생산
	public void executeWorkerTraining() {

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (isInitialBuildOrderFinished == false) {
			return;
		}

		if (MyBotModule.Broodwar.self().minerals() >= 50) {
			// workerCount = 현재 일꾼 수 + 생산중인 일꾼 수
			//int workerCount = MyBotModule.Broodwar.self().allUnitCount(UnitType.Zerg_Drone);

			int workerCount = 0;
			
			
			for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
				
				if (unit.getType() == UnitType.Zerg_Drone)
				{
					workerCount++;
				}
				else if (unit.getType() == UnitType.Zerg_Egg) {
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
			
			
			
			if(workerCount>60)
			{
				return;
			}
			
			/* 0630 해처리 숫자 기반 최대치 설정
			if(numberOfMyCombatUnitTrainingBuilding>=8 && workerCount > 60)
			{
					return;
			}
			else if(numberOfMyCombatUnitTrainingBuilding>=6 && workerCount > 50)
			{
					return;
			}
			else if(numberOfMyCombatUnitTrainingBuilding>=4 && workerCount > 40)
			{
				return;
			}
			*/
			
			
			
			int optimalWorkerCount = 0;
			for (BaseLocation baseLocation : InformationManager.Instance()
					.getOccupiedBaseLocations(InformationManager.Instance().selfPlayer)) {
				optimalWorkerCount += baseLocation.getMinerals().size() * 1.5;
				optimalWorkerCount += baseLocation.getGeysers().size() * 3;
			}

			//System.out.println("현재 : " + workerCount + " / 최적 : " + optimalWorkerCount);
			
			
			
			if (workerCount < optimalWorkerCount) {
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
					if (unit.getType().equals(UnitType.Zerg_Hatchery) 
							|| unit.getType().equals(UnitType.Zerg_Lair) 
							|| unit.getType().equals(UnitType.Zerg_Hive)) 
					{
					//	if (unit.isTraining() == false || unit.getLarva().size() > 0) {
						if (unit.getTrainingQueue().contains(UnitType.Zerg_Drone) == false || unit.getLarva().size() > 0) {
							// 빌드큐에 일꾼 생산이 1개는 있도록 한다
							if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Drone, null) == 0) {

								BuildManager.Instance().buildQueue.queueAsHighestPriority(
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
		if (MyBotModule.Broodwar.self().supplyTotal() < 400) {
			// 서플라이가 다 꽉찼을때 새 서플라이를 지으면 지연이 많이 일어나므로, supplyMargin (게임에서의 서플라이 마진 값의 2배)만큼
			// 부족해지면 새 서플라이를 짓도록 한다
			// 이렇게 값을 정해놓으면, 게임 초반부에는 서플라이를 너무 일찍 짓고, 게임 후반부에는 서플라이를 너무 늦게 짓게 된다
			int supplyMargin = 12;
			
			
			if(myPlayer.completedUnitCount(UnitType.Zerg_Hatchery)>2)
			{
				supplyMargin = 36;
			}
			
			

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

	public void executeBuildingConstruction() {

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (isInitialBuildOrderFinished == false) {
			return;
		}
		

		boolean isPossibleToConstructDefenseBuildingType1 = false;
		boolean isPossibleToConstructDefenseBuildingType2 = false;

		// 권순우 0617 방어 건물을 짓는데 흥미로운 것은
		// 이미 존재하거나, 짓고 있거나, 지을 예정인 것도 모두 "존재"한다고 해야만
		// 목표치 이상을 초과 건설하는 문제가 발생하지 않는다.
		// 방어 건물 증설을 우선적으로 실시한다
		// 현재 방어 건물 갯수

		int numberOfMyDefenseBuildingType1 = 0;
		int numberOfMyDefenseBuildingType2 = 0;

		// 저그의 경우 크립 콜로니 갯수를 셀 때 성큰 콜로니 갯수까지 포함해서 세어야, 크립 콜로니를 지정한 숫자까지만 만든다
		numberOfMyDefenseBuildingType1 += myPlayer.allUnitCount(myCreepColony);
		numberOfMyDefenseBuildingType1 += BuildManager.Instance().buildQueue.getItemCount(myCreepColony);
		numberOfMyDefenseBuildingType1 += ConstructionManager.Instance()
				.getConstructionQueueItemCount(myCreepColony, null);
		numberOfMyDefenseBuildingType1 += myPlayer.allUnitCount(mySunkenColony);
		numberOfMyDefenseBuildingType1 += BuildManager.Instance().buildQueue.getItemCount(mySunkenColony);
		
		////////////////////////////////////////////////////////////////////////////////
		
		numberOfMyDefenseBuildingType2 += myPlayer.allUnitCount(mySunkenColony);
		numberOfMyDefenseBuildingType2 += BuildManager.Instance().buildQueue.getItemCount(mySunkenColony);

		if (myPlayer.completedUnitCount(UnitType.Zerg_Spawning_Pool) > 0) {
			isPossibleToConstructDefenseBuildingType1 = true;
		}

		if (myPlayer.completedUnitCount(UnitType.Zerg_Creep_Colony) > 0) {
			isPossibleToConstructDefenseBuildingType2 = true;
		}

		if (isPossibleToConstructDefenseBuildingType1 == true
				&& numberOfMyDefenseBuildingType1 < necessaryNumberOfCreepColony) {

			if (BuildManager.Instance().buildQueue.getItemCount(myCreepColony) == 0) {

				if (BuildManager.Instance().getAvailableMinerals() >= myCreepColony.mineralPrice()) {

					BuildManager.Instance().buildQueue.queueAsLowestPriority(myCreepColony,
							BuildOrderItem.SeedPositionStrategy.SecondChokePoint, false);
				}
			}
		}

		if (isPossibleToConstructDefenseBuildingType2 == true
				&& numberOfMyDefenseBuildingType2 < necessaryNumberOfSunkenColony) {

			if (BuildManager.Instance().buildQueue.getItemCount(mySunkenColony) == 0) {

				if (BuildManager.Instance().getAvailableMinerals() >= mySunkenColony.mineralPrice()) {

					BuildManager.Instance().buildQueue.queueAsLowestPriority(mySunkenColony,
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
		
		BaseLocation nextExpansion = null;
		nextExpansion = BuildOrder_Expansion.expansion();
		
		
		if (myPlayer.completedUnitCount(UnitType.Zerg_Drone) > 19 && numberOfMyCombatUnitTrainingBuilding == 2 && nextExpansion!=null) 
		{
			if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery) == 0 
					&& ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Hatchery, null) == 0) 
			{
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hatchery,
						nextExpansion.getTilePosition(), true); /// 해처리 추가 확장 0622

				System.out.println("1");

				if (nextExpansion.getGeysers().size()>0) 
				{
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Extractor,
							nextExpansion.getTilePosition(), false); /// 해처리 추가 확장 0622

					System.out.println("2");
				}
			}
		}
		/*
		else if (BuildManager.Instance().getAvailableMinerals() > 350 && numberOfMyCombatUnitTrainingBuilding == 3 && nextExpansion!=null) 
		{
			if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery) ==0 
					&& ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Hatchery, null) ==0) 
			{
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hatchery,
						nextExpansion.getTilePosition(), true); /// 해처리 추가 확장 0622

				System.out.println("3-1");

				if (nextExpansion.getGeysers().size()>0) 
				{
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Extractor,
							nextExpansion.getTilePosition(), false); /// 해처리 추가 확장 0622

					System.out.println("3-2");
				}

			}
		}
		*/
		/*
		else if (BuildManager.Instance().getAvailableMinerals() > 300 && numberOfMyCombatUnitTrainingBuilding == 5) 
		{
			if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery) <2 
					&& ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Hatchery, null) <2) 
			{
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hatchery,
						BuildOrderItem.SeedPositionStrategy.FirstChokePoint, true); /// 해처리 추가 확장 0622

				System.out.println("4");

			}
		}
		*/
		/*
		else if (BuildManager.Instance().getAvailableMinerals() > 350 && numberOfMyCombatUnitTrainingBuilding >= 5 && nextExpansion!=null) 
		{
			if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery) == 0 
					&& ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Hatchery, null) == 0) 
			{
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hatchery,
						nextExpansion.getTilePosition(), true); /// 해처리 추가 확장 0622

				System.out.println("5");

				if (nextExpansion.getGeysers().size()>0) 
				{
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Extractor,
							nextExpansion.getTilePosition(), true); /// 해처리 추가 확장 0622

					System.out.println("6");
				}
			}

		}
		*/
		else if (BuildManager.Instance().getAvailableMinerals() > 650 && numberOfMyCombatUnitTrainingBuilding < 13 && nextExpansion!=null) 
		{
			if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Hatchery) ==0 
					&& ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Hatchery, null) ==0) 
			{
				BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hatchery,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				
				
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Hatchery,
						nextExpansion.getTilePosition(), true); /// 해처리 추가 확장 0622

				System.out.println("7");

				if (nextExpansion.getGeysers().size()>0) 
				{
					BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Extractor,
							nextExpansion.getTilePosition(), true); /// 해처리 추가 확장 0622

					System.out.println("8");
				}
			}

		} 
		else 
		{

		}

	}

	/// 업그레이드 및 테크 리서치를 실행합니다
	void executeUpgradeAndTechResearch() {

		// InitialBuildOrder 진행중에는 아무것도 하지 않습니다
		if (isInitialBuildOrderFinished == false) {
			return;
		}

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
			// 하이브 있으면 오버로드 이속 업
			if (myPlayer.completedUnitCount(UnitType.Zerg_Lair) > 0) {
				isTimeToStartUpgradeType2 = true;
			}
			// 가스 좀 남으면 하라고 넣음
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType4) == 3 && myPlayer.gas() > 100){
				isTimeToStartUpgradeType3 = true;
			}
			if (myPlayer.completedUnitCount(UnitType.Zerg_Spire) > 0 && myPlayer.gas() > 100){
				isTimeToStartUpgradeType4 = true;
			}
			// 러커는 최우선으로 리서치한다
			if (myPlayer.completedUnitCount(UnitType.Zerg_Hydralisk_Den) > 0
					&& myPlayer.completedUnitCount(UnitType.Zerg_Lair) > 0) {
				isTimeToStartResearchTech1 = true;
			}
			// 컨슘은 최우선으로 리서치한다
			if (myPlayer.completedUnitCount(UnitType.Zerg_Defiler_Mound) > 0
					&& myPlayer.hasResearched(TechType.Dark_Swarm) == true) {
				isTimeToStartResearchTech2 = true;
			}
			// 업그레이드 / 리서치를 너무 성급하게 하다가 위험에 빠질 수 있으므로, 최소 컨슘 리서치 후 리서치한다
			if (myPlayer.completedUnitCount(UnitType.Zerg_Defiler_Mound) > 0
					&& myPlayer.hasResearched(necessaryTechType2) == true) {
				isTimeToStartResearchTech3 = true;
			}
			
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType3)==2 && myPlayer.completedUnitCount(UnitType.Zerg_Hive)==0)
			{
				isTimeToStartUpgradeType3 = false;
			}
			
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType4)==2 && myPlayer.completedUnitCount(UnitType.Zerg_Hive)==0)
			{
				isTimeToStartUpgradeType3 = false;
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
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryTechType3) == 0) 
			{
			//	BuildManager.Instance().buildQueue.queueAsHighestPriority(necessaryTechType3, true);
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

		// <3 의 의미는 3레벨 업그레이드까지 계속 하라는 뜻이다. 그레이트 스파이어는 2단계 끝나고 지어주긴 해야함
		if (isTimeToStartUpgradeType3) {
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType3) < 2
					&& myPlayer.isUpgrading(necessaryUpgradeType3) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryUpgradeType3) == 0) {
				
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) 
				{
					if(unit.getType().equals(UnitType.Zerg_Spire) && unit.isUpgrading()==false && unit.isCompleted() && unit!=null)
					{
						BuildManager.Instance().buildQueue.queueAsHighestPriority(necessaryUpgradeType3, true, unit.getID());
						return;
					}
				}
				
			}
		}
		
		if (isTimeToStartUpgradeType3) {
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType3) == 2
					&& myPlayer.isUpgrading(necessaryUpgradeType3) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryUpgradeType3) == 0
					&& myPlayer.completedUnitCount(UnitType.Zerg_Hive)>0) {
				
				for (Unit unit : MyBotModule.Broodwar.self().getUnits()) 
				{
					if(unit.getType().equals(UnitType.Zerg_Spire) && unit.isUpgrading()==false && unit.isCompleted() && unit!=null)
					{
						BuildManager.Instance().buildQueue.queueAsHighestPriority(necessaryUpgradeType3, true, unit.getID());
						return;
					}
				}
				
			}
		}
		

		if (isTimeToStartUpgradeType4) {
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType4) < 2
					&& myPlayer.isUpgrading(necessaryUpgradeType4) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryUpgradeType4) == 0) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(necessaryUpgradeType4, true);
			}
		}
		
		if (isTimeToStartUpgradeType4) {
			if (myPlayer.getUpgradeLevel(necessaryUpgradeType4) == 2
					&& myPlayer.isUpgrading(necessaryUpgradeType4) == false
					&& BuildManager.Instance().buildQueue.getItemCount(necessaryUpgradeType4) == 0
					&& myPlayer.completedUnitCount(UnitType.Zerg_Hive)>0) {
				BuildManager.Instance().buildQueue.queueAsHighestPriority(necessaryUpgradeType4, true);
			}
		}
		
		
		
		
		
		
		
		

		// BWAPI 4.1.2 의 버그때문에, 오버로드 업그레이드를 위해서는 반드시 Zerg_Lair 가 있어야함
		// 이말인즉 해처리 잔뜩 + 하이브 1개 있으면 업그레이드 못하고 꼭 레어를 필요로 한다는 의미이다.
		if (myRace == Race.Zerg) {
			
			int numberOfLair = 0;
			numberOfLair += myPlayer.allUnitCount(UnitType.Zerg_Lair);
			numberOfLair += BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Lair);
			numberOfLair += ConstructionManager.Instance()
					.getConstructionQueueItemCount(UnitType.Zerg_Lair, null);
			
			
			
			//UpgradeType.Pneumatized_Carapace
			if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Ventral_Sacs) > 0) {
				if (numberOfLair == 0) 
				{
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Lair, true);
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
		if (MyBotModule.Broodwar.getFrameCount() % 24 != 0) {
			return;
		}
		
		if (myPlayer.supplyUsed() <= 390) {

			for (Unit unit : myPlayer.getUnits()) {

				if (unit.getType() == UnitType.Zerg_Hatchery
						||unit.getType() == UnitType.Zerg_Lair
						||unit.getType() == UnitType.Zerg_Hive) {

					if (unit.isTraining() == false || unit.getLarva().size() > 0) {

						UnitType nextUnitTypeToTrain = getNextCombatUnitTypeToTrain();

						if (BuildManager.Instance().buildQueue.getItemCount(nextUnitTypeToTrain) < myPlayer.completedUnitCount(UnitType.Zerg_Hatchery)*2) {

							if(nextUnitTypeToTrain == UnitType.Zerg_Mutalisk)
							{
								BuildManager.Instance().buildQueue.queueAsHighestPriority(nextUnitTypeToTrain, false);
							}
							else
							{
								BuildManager.Instance().buildQueue.queueAsLowestPriority(nextUnitTypeToTrain, false);
							}
							
							

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

			int defilerNumber = myPlayer.allUnitCount(UnitType.Zerg_Defiler)
					+ BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Defiler)
					+ ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Zerg_Defiler, null);


			
			if(myPlayer.completedUnitCount(UnitType.Zerg_Defiler_Mound)>0 &&  defilerNumber<3) {
				nextUnitTypeToTrain = myDefiler;
			}
			else if (myZerglingList.size() < myHydraliskList.size()) 
			{
				nextUnitTypeToTrain = myZergling;
			} 
			else 
			{
				nextUnitTypeToTrain = myHydralisk;
			}
			
				

		}

		else if (buildOrderArrayOfMyCombatUnitType[nextTargetIndexOfBuildOrderArray] == 2) {

			if(myPlayer.completedUnitCount(UnitType.Zerg_Spire)>0 && myPlayer.completedUnitCount(UnitType.Zerg_Mutalisk)<15)
			{
				nextUnitTypeToTrain = myMutalisk;				
			}
			else if(myPlayer.minerals() > 1000)
			{
				if(myZerglingList.size()<45)
				{
					nextUnitTypeToTrain = myZergling;
					
				}
				else
				{
					nextUnitTypeToTrain = myHydralisk;
				}
			}	
			else if(myZerglingList.size()<15)
			{
				nextUnitTypeToTrain = myZergling;
			}
			else
			{
				nextUnitTypeToTrain = myHydralisk;
			}
				
			
		} else if (buildOrderArrayOfMyCombatUnitType[nextTargetIndexOfBuildOrderArray] == 3) {

			if(myPlayer.completedUnitCount(UnitType.Zerg_Ultralisk_Cavern)>0 && myPlayer.completedUnitCount(UnitType.Zerg_Ultralisk)<4)
			{
				nextUnitTypeToTrain = myUltralisk;

			}
			else if(myPlayer.minerals() > 1000)  
			{
				if(myZerglingList.size()<45)
				{
					nextUnitTypeToTrain = myZergling;
					
				}
				else
				{
					nextUnitTypeToTrain = myHydralisk;
				}

			}
			else if(myPlayer.completedUnitCount(UnitType.Zerg_Hydralisk)<15)
			{
				nextUnitTypeToTrain = myHydralisk;
			}
			
			else if (myZerglingList.size() < myHydraliskList.size()) 
			{
				nextUnitTypeToTrain = myZergling;
			} 
			else 
			{
				nextUnitTypeToTrain = myHydralisk;
			}
			
			
			
		} 
		else if (buildOrderArrayOfMyCombatUnitType[nextTargetIndexOfBuildOrderArray] == 4) {


			if(myPlayer.minerals() > 1000)
			{
				if(myZerglingList.size()<45)
				{
					nextUnitTypeToTrain = myZergling;
					
				}
				else
				{
					nextUnitTypeToTrain = myHydralisk;
				}
			}
			else
			{
				nextUnitTypeToTrain = myHydralisk;
			}
	
		}
		else 
		{
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

		String gameRecordFileName = "bwapi-data\\read\\NoNameBot_GameRecord.dat";

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

		String gameRecordFileName = "bwapi-data\\write\\NoNameBot_GameRecord.dat";

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
		String gameLogFileName = "bwapi-data\\write\\NoNameBot_LastGameLog.dat";
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
		MyBotModule.Broodwar.drawTextScreen(200, y, "My " + myZergling.toString());
		MyBotModule.Broodwar.drawTextScreen(350, y, "alive " + myZerglingList.size());
		MyBotModule.Broodwar.drawTextScreen(400, y, "killed " + myKilledZerglings);
		y += 10;
		MyBotModule.Broodwar.drawTextScreen(200, y, "My " + myMutalisk.toString());
		MyBotModule.Broodwar.drawTextScreen(350, y, "alive " + myMutaliskList.size());
		MyBotModule.Broodwar.drawTextScreen(400, y, "killed " + myKilledMutalisks);
		y += 10;
		MyBotModule.Broodwar.drawTextScreen(200, y, "My " + myUltralisk.toString());
		MyBotModule.Broodwar.drawTextScreen(350, y, "alive " + myUltraliskList.size());
		MyBotModule.Broodwar.drawTextScreen(400, y, "killed " + myKilledUltralisks);
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
		//saveGameRecordList(isWinner);
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

//		// 아군 방어 건물 목록, 공격 유닛 목록
//		myCreepColonyList.clear();
//		mySunkenColonyList.clear();
//
//		myAllCombatUnitList.clear();
//		myZerglingList.clear();
//		myMutaliskList.clear();
//		myUltraliskList.clear();
//		myHydraliskList.clear();
//		myLurkerList.clear();
//
//		for (Unit unit : myPlayer.getUnits()) {
//			if (unit == null || unit.exists() == false || unit.getHitPoints() <= 0)
//				continue;
//
//			if (unit.getType() == myZergling) {
//				myZerglingList.add(unit);
//				myAllCombatUnitList.add(unit);
//			} else if (unit.getType() == myMutalisk) {
//				myMutaliskList.add(unit);
//				myAllCombatUnitList.add(unit);
//			} else if (unit.getType() == myUltralisk) {
//				myUltraliskList.add(unit);
//				myAllCombatUnitList.add(unit);
//			} else if (unit.getType() == myHydralisk) {
//				myHydraliskList.add(unit);
//				myAllCombatUnitList.add(unit);
//			}
//			else if (unit.getType() == myLurker || unit.getType() == UnitType.Zerg_Lurker_Egg) {
//				myLurkerList.add(unit);
//				myAllCombatUnitList.add(unit);
//			}
//			else if (unit.getType() == UnitType.Zerg_Overlord) {
//				myAllCombatUnitList.add(unit);
//			}
//			else if (unit.getType() == myCreepColony) {
//				myCreepColonyList.add(unit);
//			} else if (unit.getType() == mySunkenColony) {
//				mySunkenColonyList.add(unit);
//			}
//		}
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
		// 죽은 유닛 삭제
		ArrayList<Unit> unitList = getUnitList(unit);
		if (unitList != null) {
			boolean check = deleteUnitInList(unit, unitList);
			if(!check) {
			}
		}
		if(isCombatUnit(unit)) {
			myAllCombatUnitList.remove(unit);
		}
		
		if (unit.getPlayer() == myPlayer) {
			if (unit.getType() == myZergling) {
				myKilledZerglings++;
			} else if (unit.getType() == myMutalisk) {
				myKilledMutalisks++;
			} else if (unit.getType() == myUltralisk) {
				myKilledUltralisks++;
			} else if (unit.getType() == myLurker) {
				myKilledLurkers++;
			} else if (unit.getType() == myHydralisk) {
				myKilledHydralisks++;
			} else if (unit.getType() == myDefiler) {
				myKilledDefilers++;
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

	public void onUnitCreate(Unit unit) {
		if (unit.getType().isNeutral()) {
			return;
		}
		if(unit.getType()==UnitType.Zerg_Zergling) {
			myZerglingList.add(unit);
		}
	}
	
	public void onUnitMorph(Unit unit) {
		if (unit.getType().isNeutral()) {
			return;
		}
		ArrayList<Unit> unitList = getUnitList(unit);
		
		
		if (unitList != null) {
			unitList.add(unit);
			if(isCombatUnit(unit)) {
				myAllCombatUnitList.add(unit);
			}
		}
		
		/*
		if (unitList != null) {
			unitList.add(unit);
			if(isCombatUnit(unit)) {
				myAllCombatUnitList.add(unit);
			}
		}
		*/

		ArrayList<Unit> bfUnitList = getBfUnitList(unit);
		if(bfUnitList!=null) {
			boolean check = deleteUnitInList(unit, bfUnitList);
			if(!check) {
			}
		}
		
	}
	
	public ArrayList<Unit> getBfUnitList(Unit unit){
		
		/*
		if(unit.getType()==mySunkenColony) {
			return myCreepColonyList;
		}else if(unit.getType()==myLurker || unit.getType()==UnitType.Zerg_Lurker_Egg) {
			return myHydraliskList;
		}else {
			return null;
		}
		*/
		
		
		if(unit.getType()==mySunkenColony) {
			return myCreepColonyList;
		}else if(unit.getType()==UnitType.Zerg_Lurker_Egg) {
			return myHydraliskList;
		}else if(unit.getType()==myLurker) { 
				return myLurkerList;
		}
		else {
			return null;
		}
		
		
		
		
		
	}

	public ArrayList<Unit> getUnitList(Unit unit) {
		if (unit.getType() == myZergling) {
			return myZerglingList;
		} else if (unit.getType() == myMutalisk) {
			return myMutaliskList;
		} else if (unit.getType() == myUltralisk) {
			return myUltraliskList;
		} else if (unit.getType() == myHydralisk) {
			return myHydraliskList;
		} else if (unit.getType() == myLurker || unit.getType() == UnitType.Zerg_Lurker_Egg) {
			return myLurkerList;
		}		else if (unit.getType() == myDefiler) {
			return myDefilerList;
		}else if (unit.getType() == UnitType.Zerg_Overlord) {
			return null;
		} else if (unit.getType() == myCreepColony) {
			return myCreepColonyList;
		} else if (unit.getType() == mySunkenColony) {
			return mySunkenColonyList;
		} else {
			return null;
		}
	}

	public boolean isCombatUnit(Unit unit) {
		if (unit.getType() == myZergling) {
			return true;
		} else if (unit.getType() == myMutalisk) {
			return true;
		} else if (unit.getType() == myUltralisk) {
			return true;
		} else if (unit.getType() == myHydralisk) {
			return true;
		} else if (unit.getType() == myLurker || unit.getType() == UnitType.Zerg_Lurker_Egg) {
			return true;
		} else if (unit.getType() == myDefiler) {
			return true;
		}else if (unit.getType() == UnitType.Zerg_Overlord) {
			return true;
		} else if (unit.getType() == myCreepColony) {
			return false;
		} else if (unit.getType() == mySunkenColony) {
			return false;
		} else {
			return false;
		}
	}
	private boolean deleteUnitInList(Unit unit, ArrayList<Unit> list) {
		for(int i = 0; i<list.size(); i++) {
			if(list.get(i).getID()==unit.getID()) {
				list.remove(i);
				return true;
			}
		}
		return false;
		
	}
	
}