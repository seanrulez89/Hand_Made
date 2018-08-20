
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bwapi.*;
import bwta.*;

/// 일꾼 유닛들의 상태를 관리하고 컨트롤하는 class
public class WorkerManager {
	
	static int defenceFlagforEarlyAttack=0;
	static int FlagforReconstructedSpawningPool=0;

	/// 각 Worker 에 대한 WorkerJob 상황을 저장하는 자료구조 객체
	private WorkerData workerData = new WorkerData();

	private CommandUtil commandUtil = new CommandUtil();

	/// 일꾼 중 한명을 Repair Worker 로 정해서, 전체 수리 대상을 하나씩 순서대로 수리합니다
	private Unit currentRepairWorker = null;
	
	private static int deadDrone = 0;

	private static WorkerManager instance = new WorkerManager();

	/// static singleton 객체를 리턴합니다
	public static WorkerManager Instance() {
		return instance;
	}

	/// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트하고, 일꾼 유닛들이 자원 채취 등 임무 수행을 하도록 합니다
	public void update() {
		long time = System.currentTimeMillis();
		// 1초에 1번만 실행한다
		if (MyBotModule.Broodwar.getFrameCount() % 24 != 0)
			return;

		int mineralWorkerCount = 0;
		for (Unit worker : workerData.getWorkers()) {
			if (worker.isCompleted() && worker != null
					&& workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Minerals) {
				mineralWorkerCount++;
			}
		}

		if (mineralWorkerCount == 0)// 이렇게 하면 다음 업데이트에서는 할당된 미네랄 일꾼이 있다고 생각해서 결국 채취를 못하고 재분배 하는 문제가 발생함
		{
			for (Unit worker : workerData.getWorkers()) {
				setMineralWorker(worker);
			}
			return;
		}

		updateWorkerStatus();
		handleGasWorkers();
		handleIdleWorkers();
		handleMoveWorkers();
		handleCombatWorkers();			
		handleRepairWorkers();
		// rebalanceWorkers(); //0628 이걸 하는건 좋은데 이걸 하니까 우왕좌왕 미네랄 못캐는 애들이 생기는듯 계속 새로 할당되서
	}
	

	
	public Unit getCloestMineral(Unit depot)
	{
		List<Unit> mineralsNearDepot = new ArrayList<Unit>();

	    int radius = 320;

	    for (Unit unit : MyBotModule.Broodwar.getAllUnits())
		{
			if ((unit.getType() == UnitType.Resource_Mineral_Field) && unit.getDistance(depot) < radius)
			{
	            mineralsNearDepot.add(unit);
			}
		}
	    return mineralsNearDepot.get(1);
	}//현재 미사용. 추후 확인 후 삭제 


	public void updateWorkerStatus() {
		// Drone 은 건설을 위해 isConstructing = true 상태로 건설장소까지 이동한 후,
		// 잠깐 getBuildType() == none 가 되었다가, isConstructing = true, isMorphing = true 가
		// 된 후, 건설을 시작한다

		// for each of our Workers
		for (Unit worker : workerData.getWorkers()) {
			if (!worker.isCompleted() || worker == null) {
				continue;
			}
			
			if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Build)
			{
				continue;
			}
			
			
			
			
			
			// 게임상에서 worker가 isIdle 상태가 되었으면 (새로 탄생했거나, 그전 임무가 끝난 경우), WorkerData 도 Idle 로
			// 맞춘 후, handleGasWorkers, handleIdleWorkers 등에서 새 임무를 지정한다
			if (worker.isIdle()) {
				// workerData 에서 Build / Move / Scout 로 임무지정한 경우, worker 는 즉 임무 수행 도중 (임무 완료 전)
				// 에 일시적으로 isIdle 상태가 될 수 있다
				if ((workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Build)
						&& (workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Move)
						&& (workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Scout)) {
					workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
				}
			}

			// if its job is gas
			if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Gas) {
				Unit refinery = workerData.getWorkerResource(worker);

				// if the refinery doesn't exist anymore (파괴되었을 경우)
				if (refinery == null || !refinery.exists() || refinery.getHitPoints() <= 0) {
					workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
				}
			}

			// if its job is repair
			if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Repair) {
				Unit repairTargetUnit = workerData.getWorkerRepairUnit(worker);

				// 대상이 파괴되었거나, 수리가 다 끝난 경우
				if (repairTargetUnit == null || !repairTargetUnit.exists() || repairTargetUnit.getHitPoints() <= 0
						|| repairTargetUnit.getHitPoints() == repairTargetUnit.getType().maxHitPoints()) {
					workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
				}
			}
			
			
			
			if (MyBotModule.Broodwar.getFrameCount() > 240) // MyBotModule.Broodwar.getFrameCount() < 7680
			{
				
				Unit target = getWeakestEnemyUnitFromWorker(worker); //32*8 내에 있는 적을 반환한다
				// 권순우 5는 적어서 8로 늘림 
				
				
				// 타겟이 있냐 없냐로 일단 구분하고
				if(target!=null)
				{
					 // 정찰 일꾼은 무시한다

					if(worker.isUnderAttack()==true)
					{
						if(worker.getDistance(getClosestResourceDepotFromWorker(worker).getPosition()) < 32 * 8 )
						{
							if(target.getType() == UnitType.Protoss_Probe
									|| target.getType() == UnitType.Zerg_Drone
									|| target.getType() == UnitType.Terran_SCV)
									
							{
								//setCombatWorker(worker);
								commandUtil.attackUnit(worker, target);
								//System.out.println("나를 공격한 정찰 일꾼 / 가까움 / 전투");
								//나를 공격한 일꾼은 때림
							}
						}
					}
					else if (target.getType() == UnitType.Protoss_Probe
								|| target.getType() == UnitType.Zerg_Drone
								|| target.getType() == UnitType.Terran_SCV)							
					{
						continue;
					}
							 
					 
					
					else if(worker.getDistance(getClosestResourceDepotFromWorker(worker).getPosition()) < 32 * 8)
					{
						if(target.getType() != UnitType.Protoss_Probe
								|| target.getType() != UnitType.Zerg_Drone
								|| target.getType() != UnitType.Terran_SCV)
						{
							//setCombatWorker(worker);
							commandUtil.attackUnit(worker, target);
							//System.out.println("적군 / 가까움 / 전투");
							//가까운 적군은 떄림
						}
					}
					else if (worker.isUnderAttack()==true)
					{
						if(target.getType() != UnitType.Protoss_Probe
								&& target.getType() != UnitType.Zerg_Drone
								&& target.getType() != UnitType.Terran_SCV)
						{
							setMineralWorker(worker);
							//System.out.println("적군에게 맞으니까 미네랄로 도망쳐");
						}
					}
					else
					{
						setMineralWorker(worker);
						//System.out.println("멀어졌어 싸우지말고 미네랄 캐");
					}
				}
				else
				{
					if(workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat)
					{
						setMineralWorker(worker);
						//System.out.println("적군 없어 하던 일 해");
					}
				}
				
				
				/*
				if (target != null && worker.getDistance(getClosestResourceDepotFromWorker(worker).getPosition()) < 32 * 6) {
					setCombatWorker(worker);
					commandUtil.attackUnit(worker, target);
					System.out.println("공격 전환");
				}
				//타겟이 있고, 드론이 기지와 가까이 있으면 적을 공격한다.
				
				if (worker.isUnderAttack() && workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat) {
//					setMineralWorker(worker);
						commandUtil.rightClick(worker, getCloestMineral(worker));	
						System.out.println("ㅌㅌㅌ");
				}//드론이 공격을 당하면 미네랄을 캔다!
				
				if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat) {
					Unit depot = getClosestResourceDepotFromWorker(worker);
					if (worker.getDistance(depot.getPosition()) > 32 * 6) {
						setMineralWorker(worker);
						System.out.println("공격취소1");

					}
				}//멀리가면 공격취소!
				*/
			}
			//초반에만 적용		

			
			
			if (MyBotModule.Broodwar.getFrameCount() > 7680 && workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat) {
				setMineralWorker(worker);				
				System.out.println("공격해제");
			}
			
			///////스포닝 풀 강제 재생성
			if (MyBotModule.Broodwar.getFrameCount() >= 24*60*7)
			{
				if(MyBotModule.Broodwar.getFrameCount()%1440==0)
				//FlagforReconstructedSpawningPool=0;
				{
					if(MyBotModule.Broodwar.self().completedUnitCount(UnitType.Zerg_Spawning_Pool)==0
					&& workerData.getWorkers().size() >= 5
					&& FlagforReconstructedSpawningPool==0)
					{
						BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Spawning_Pool,
								BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
						FlagforReconstructedSpawningPool=1;
					//	System.out.println("스포닝 풀 : "+MyBotModule.Broodwar.self().completedUnitCount(UnitType.Zerg_Spawning_Pool)+
					//						"드론 : "+ workerData.getWorkers().size()+
					//						"Flag : "+FlagforReconstructedSpawningPool);
					//	System.out.println("스포닝 풀 강제 생성");
						
					}	

				}

			} //게임 시간 7분 이후, 1분에 한번씩 스포닝풀 체크
			  //스포닝풀이 없고 드론이 5마리 이상이면 스포닝 풀을 만든다. (현재 1회만 동작)
			
		}
		
		
		
		

		///////스포닝 풀 강제 재생성
		if (MyBotModule.Broodwar.getFrameCount() >= 24*60*6.5)
		{
			if(MyBotModule.Broodwar.getFrameCount()%14400==0)
			{
				if(MyBotModule.Broodwar.self().completedUnitCount(UnitType.Zerg_Spawning_Pool)==0
				&& MyBotModule.Broodwar.self().completedUnitCount(UnitType.Zerg_Drone)>=5)
				{
					BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Spawning_Pool,
							BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
					
					
				}			
			}
			
			//System.out.println("스포닝 풀 강제 생성");
		} //게임 시간 8분 이후, 1분에 한번씩 스포닝풀 체크
		  //스포닝풀이 없고 드론이 5마리 이상이면 스포닝 풀을 만든다.

		
		

		//////// 초반 긴급 방어 구축

		if (MyBotModule.Broodwar.getFrameCount() < 24*60*6.5 && MyBotModule.Broodwar.getFrameCount() > 240) {
			
			List<BaseLocation> myBaseLocations = InformationManager.Instance()
					.getOccupiedBaseLocations(InformationManager.Instance().selfPlayer);

			// 변수 선언 위치 조정
			int numberOfUnitType_Zerg_Creep_Colony = 0;
			int numberOfUnitType_Zerg_Sunken_Colony = 0;
			int numberOfEarlyAttackUnit = 0;
			Player myPlayer = MyBotModule.Broodwar.self();
			
			for (BaseLocation myBase : myBaseLocations) 
			{

				for (Unit unit : MyBotModule.Broodwar.getUnitsInRadius(myBase.getPosition(), 18 * Config.TILE_SIZE)) 
				{
			
					if (unit.getPlayer() == StrategyManager.Instance().enemyPlayer
							&& unit.getType() != UnitType.Terran_SCV && unit.getType() != UnitType.Protoss_Probe
							&& unit.getType() != UnitType.Zerg_Drone && unit.getType() != UnitType.Zerg_Overlord) 
					{
						// 권순우 위에 4종류만 아니면 무조건 센다, 어차피 질럿 아니면 저글링일테니까
						numberOfEarlyAttackUnit++;
					}
				}
			}	
						/*
						numberOfEarlyAttackUnit += StrategyManager.Instance().enemyPlayer.allUnitCount(UnitType.Zerg_Zergling);
						numberOfEarlyAttackUnit += StrategyManager.Instance().enemyPlayer.allUnitCount(UnitType.Terran_Marine);

						*/
			if(MyBotModule.Broodwar.getFrameCount()%720==0)
			{
				defenceFlagforEarlyAttack = 0;
				//System.out.println(MyBotModule.Broodwar.getFrameCount()/24+"초/ 플래그 초기화");
			}
			
			if(numberOfEarlyAttackUnit >= 1 && defenceFlagforEarlyAttack == 0 && myPlayer.completedUnitCount(UnitType.Zerg_Spawning_Pool)>0)
			{
				
				
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Zergling,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Zergling,
						BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
				
				System.out.println("저글링 생산!");
				defenceFlagforEarlyAttack = 1;

			}//30초에 한번 실행 :  기지 근처에 적이 있으면 저글링 4마리 생산 (경기 시간 10분까지만)

			
			
			if(numberOfEarlyAttackUnit == 0)
			{
				for(int i = 0 ; i < deadDrone ; i++)
				{
					System.out.println("드론 죽은 만큼 추가");
					BuildManager.Instance().buildQueue.queueAsHighestPriority(InformationManager.Instance().getWorkerType(),
							BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
					deadDrone--;
				}
			}
			
			
													
			
			// 권순우 일단 뭐든 적이 있으면
			if(numberOfEarlyAttackUnit>0)
			{
				
				
				// 저그의 경우 크립 콜로니 갯수를 셀 때 성큰 콜로니 갯수까지 포함해서 세어야, 크립 콜로니를 지정한 숫자까지만 만든다
				numberOfUnitType_Zerg_Creep_Colony += myPlayer.allUnitCount(UnitType.Zerg_Creep_Colony);
				numberOfUnitType_Zerg_Creep_Colony += BuildManager.Instance().buildQueue
						.getItemCount(UnitType.Zerg_Creep_Colony);
				numberOfUnitType_Zerg_Creep_Colony += ConstructionManager.Instance()
						.getConstructionQueueItemCount(UnitType.Zerg_Creep_Colony, null);
				numberOfUnitType_Zerg_Creep_Colony += myPlayer.allUnitCount(UnitType.Zerg_Sunken_Colony);
				numberOfUnitType_Zerg_Creep_Colony += BuildManager.Instance().buildQueue
						.getItemCount(UnitType.Zerg_Sunken_Colony);
				numberOfUnitType_Zerg_Sunken_Colony += myPlayer.allUnitCount(UnitType.Zerg_Sunken_Colony);
				numberOfUnitType_Zerg_Sunken_Colony += BuildManager.Instance().buildQueue
						.getItemCount(UnitType.Zerg_Sunken_Colony);
/*
				System.out.println("numberOfUnitType_Zerg_Creep_Colony : " +
				numberOfUnitType_Zerg_Creep_Colony);
				System.out.println("+++++");
				System.out.println("numberOfUnitType_Zerg_Sunken_Colony : " +
				numberOfUnitType_Zerg_Sunken_Colony);

*/				
				for (Unit building : MyBotModule.Broodwar.self().getUnits()) 
				{
					if (building.getType().equals(UnitType.Zerg_Hatchery) && building.canCancelMorph()) 
					{					
						if(building.getHitPoints()<100) // 체력이 버티는 순간까지 최대한 버티다가 취소한다
						{
							building.cancelMorph();// 이걸로 크립, 성큰콜로니 지을 미네랄을 확보한다
							BuildManager.Instance().buildQueue.queueAsLowestPriority(UnitType.Zerg_Hatchery,
									BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation, true);
							System.out.println("해처리 취소");
						}
					}
				}
				
				
				//미구현 : 오버로드1이 적을 발견 바로 크립을 깔고, 오버로드2가 적을 발견하면 성큰으로 간다. (성큰 최대 2개)
				//현재는 기지 주위에서 적을 발견하면 까는 방식
				
				/*
				* 권순우
				* 여기가 까다롭다
				* 이미 이니셜 빌드오더 후반에 크립고 성큰이 있으므로 
				* 그들의 숫자를 감안해서 조건을 걸어야 한다
				* 이미 정해진 빌드 뒤에 존재한다면
				* 현재 순간으로 땡겨오는 식으로 구현하면 좋겠지만
				* 그건 너무 어려워 ㅎㅎ
				*/				
				if (numberOfUnitType_Zerg_Creep_Colony < 7) {

					if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Creep_Colony) < 6) {

						/*
						 * BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Creep_Colony,
								BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
								*/
						

					}
				}//Mainbase는 너무 안쪽이고 1st choke point는 적절한데 가끔 언덕 아래로 배치되면 망테크;

				if (myPlayer.completedUnitCount(UnitType.Zerg_Creep_Colony) > 0
						&& numberOfUnitType_Zerg_Sunken_Colony < 4) {

					if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Zerg_Sunken_Colony) < 4) {
						/*
						BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Zerg_Sunken_Colony, true);
						*/
					}
				}
			}

		}

	}

	
	
	
	
	
	
	
	
	
	
	
	public void handleGasWorkers() {
		// for each unit we have
		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			// refinery 가 건설 completed 되었으면,
			if (unit.getType().isRefinery() && unit.isCompleted()) {
				// get the number of workers currently assigned to it
				int numAssigned = workerData.getNumAssignedWorkers(unit);

				// if it's less than we want it to be, fill 'er up
				// 단점 : 미네랄 일꾼은 적은데 가스 일꾼은 무조건 3~4명인 경우 발생.
				for (int i = 0; i < (Config.WorkersPerRefinery - numAssigned); ++i) {
					Unit gasWorker = chooseGasWorkerFromMineralWorkers(unit);
					if (gasWorker != null) {
						if (!gasWorker.isCarryingMinerals()) // 0626 미네랄을 들지 않은 일꾼만 가스를 캐라!
						{

							if(workerData.getWorkers().size() >= 5) {    //0819 드론이 5마리 이상일때만
								workerData.setWorkerJob(gasWorker, WorkerData.WorkerJob.Gas, unit); 
								// System.out.println("New gasworker set");								
							}

						}
					}
				}

			}
		}
	}

	/// Idle 일꾼을 Mineral 일꾼으로 만듭니다
	public void handleIdleWorkers() {
		// for each of our workers
		for (Unit worker : workerData.getWorkers()) {
			if (worker == null)
				continue;

			// if worker's job is idle
			if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Idle
					|| workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Default) {
				// send it to the nearest mineral patch
				setMineralWorker(worker);
			}
		}
	}

	public void handleMoveWorkers() {
		// for each of our workers
		for (Unit worker : workerData.getWorkers()) {
			if (worker == null)
				continue;

			// if it is a move worker
			if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Move) {
				WorkerMoveData data = workerData.getWorkerMoveData(worker);

				// 목적지에 도착한 경우 이동 명령을 해제한다
				if (worker.getPosition().getDistance(data.getPosition()) < 4) {
					setIdleWorker(worker);
				} else {
					commandUtil.move(worker, data.getPosition());
				}
			}
		}
	}

	// bad micro for combat workers
	public void handleCombatWorkers() {
		for (Unit worker : workerData.getWorkers()) {
			if (worker == null)
				continue;

			if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat) {
				MyBotModule.Broodwar.drawCircleMap(worker.getPosition().getX(), worker.getPosition().getY(), 4,
						Color.Yellow, true);
				Unit target = getWeakestEnemyUnitFromWorker(worker);

				if (target != null) {
					commandUtil.attackUnit(worker, target);
				}
			}
		}
	}

	public void handleRepairWorkers() {
		if (MyBotModule.Broodwar.self().getRace() != Race.Terran) {
			return;
		}

		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			// 건물의 경우 아무리 멀어도 무조건 수리. 일꾼 한명이 순서대로 수리
			if (unit.getType().isBuilding() && unit.isCompleted() == true
					&& unit.getHitPoints() < unit.getType().maxHitPoints()) {
				Unit repairWorker = chooseRepairWorkerClosestTo(unit.getPosition(), 0);
				setRepairWorker(repairWorker, unit);
				break;
			}
			// 메카닉 유닛 (SCV, 시즈탱크, 레이쓰 등)의 경우 근처에 SCV가 있는 경우 수리. 일꾼 한명이 순서대로 수리
			else if (unit.getType().isMechanical() && unit.isCompleted() == true
					&& unit.getHitPoints() < unit.getType().maxHitPoints()) {
				// SCV 는 수리 대상에서 제외. 전투 유닛만 수리하도록 한다
				if (unit.getType() != UnitType.Terran_SCV) {
					Unit repairWorker = chooseRepairWorkerClosestTo(unit.getPosition(), 10 * Config.TILE_SIZE);
					setRepairWorker(repairWorker, unit);
					break;
				}
			}

		}
	}

	/// position 에서 가장 가까운 Mineral 혹은 Idle 혹은 Move 일꾼 유닛들 중에서 Repair 임무를 수행할 일꾼 유닛을
	/// 정해서 리턴합니다
	public Unit chooseRepairWorkerClosestTo(Position p, int maxRange) {
		if (!p.isValid())
			return null;

		Unit closestWorker = null;

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 변수 기본값 수정

		double closestDist = 1000000000;

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		if (currentRepairWorker != null && currentRepairWorker.exists() && currentRepairWorker.getHitPoints() > 0) {
			return currentRepairWorker;
		}

		// for each of our workers
		for (Unit worker : workerData.getWorkers()) {
			if (worker == null) {
				continue;
			}

			if (worker.isCompleted() && (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Minerals
					|| workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Idle
					|| workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Move)) {
				double dist = worker.getDistance(p);

				if (closestWorker == null || (dist < closestDist && worker.isCarryingMinerals() == false
						&& worker.isCarryingGas() == false)) {
					closestWorker = worker;
					dist = closestDist;
				}
			}
		}

		if (currentRepairWorker == null || currentRepairWorker.exists() == false
				|| currentRepairWorker.getHitPoints() <= 0) {
			currentRepairWorker = closestWorker;
		}

		return closestWorker;
	}

	/// 해당 일꾼 유닛 unit 의 WorkerJob 값를 Mineral 로 변경합니다
	public void setMineralWorker(Unit unit) {
		if (unit == null)
			return;

		// check if there is a mineral available to send the worker to
		Unit depot = getClosestResourceDepotFromWorker(unit);

		// if there is a valid ResourceDepot (Command Center, Nexus, Hatchery)
		if (depot != null) {
			// update workerData with the new job
			workerData.setWorkerJob(unit, WorkerData.WorkerJob.Minerals, depot);
		}
	}
	
	

	/// target 으로부터 가장 가까운 Mineral 일꾼 유닛을 리턴합니다
	public Unit getClosestMineralWorkerTo(Position target) {
		Unit closestUnit = null;

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 변수 기본값 수정

		double closestDist = 1000000000;

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if (unit.isCompleted() && unit.getHitPoints() > 0 && unit.exists() && unit.getType().isWorker()
					&& WorkerManager.Instance().isMineralWorker(unit)) {
				double dist = unit.getDistance(target);
				if (closestUnit == null || dist < closestDist) {
					closestUnit = unit;
					closestDist = dist;
				}
			}
		}

		return closestUnit;
	}

	/// 해당 일꾼 유닛 unit 으로부터 가장 가까운 ResourceDepot 건물을 리턴합니다
	public Unit getClosestResourceDepotFromWorker(Unit worker) {
		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 멀티 기지간 일꾼 숫자 리밸런싱이 잘 일어나도록 버그 수정

		if (worker == null)
			return null;

		Unit closestDepot = null;
		double closestDistance = 1000000000;

		// 완성된, 공중에 떠있지 않고 땅에 정착해있는, ResourceDepot 혹은 Lair 나 Hive로 변형중인 Hatchery 중에서
		// 첫째로 미네랄 일꾼수가 꽉 차지않은 곳
		// 둘째로 가까운 곳을 찾는다
//		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
		for (Unit unit : workerData.getDepots()) {
			if (unit == null)
				continue;

			if (unit.getType().isResourceDepot() && (unit.isCompleted() || unit.getType() == UnitType.Zerg_Lair
					|| unit.getType() == UnitType.Zerg_Hive) && unit.isLifted() == false) {
				if (workerData.depotHasEnoughMineralWorkers(unit) == false) {
					double distance = unit.getDistance(worker);
					if (closestDistance > distance) {
						closestDepot = unit;
						closestDistance = distance;
					}
				}
			}
		}

		// 모든 ResourceDepot 이 다 일꾼수가 꽉 차있거나, 완성된 ResourceDepot 이 하나도 없고 건설중이라면,
		// ResourceDepot 주위에 미네랄이 남아있는 곳 중에서 가까운 곳이 선택되도록 한다
		if (closestDepot == null) {
			for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
				if (unit == null)
					continue;

				if (unit.getType().isResourceDepot()) {
					if (workerData.getMineralsNearDepot(unit) > 0) {
						double distance = unit.getDistance(worker);
						if (closestDistance > distance) {
							closestDepot = unit;
							closestDistance = distance;
						}
					}
				}
			}

		}

		// 모든 ResourceDepot 주위에 미네랄이 하나도 없다면, 일꾼에게 가장 가까운 곳을 선택한다
		if (closestDepot == null) {
			for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
				if (unit == null)
					continue;

				if (unit.getType().isResourceDepot()) {
					double distance = unit.getDistance(worker);
					if (closestDistance > distance) {
						closestDepot = unit;
						closestDistance = distance;
					}
				}
			}
		}

		return closestDepot;

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////
	}

	/// 해당 일꾼 유닛 unit 의 WorkerJob 값를 Idle 로 변경합니다
	public void setIdleWorker(Unit unit) {
		if (unit == null)
			return;

		workerData.setWorkerJob(unit, WorkerData.WorkerJob.Idle, (Unit) null);
	}

	/// Mineral 일꾼 유닛들 중에서 Gas 임무를 수행할 일꾼 유닛을 정해서 리턴합니다<br>
	/// Idle 일꾼은 Build, Repair, Scout 등 다른 임무에 먼저 투입되어야 하기 때문에 Mineral 일꾼 중에서만 정합니다
	public Unit chooseGasWorkerFromMineralWorkers(Unit refinery) {
		if (refinery == null)
			return null;

		Unit closestWorker = null;

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 변수 기본값 수정

		double closestDistance = 1000000000;

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		for (Unit unit : workerData.getWorkers()) {
			if (unit == null)
				continue;

			if (unit.isCompleted() && workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Minerals) {
				double distance = unit.getDistance(refinery);
				if (closestWorker == null || (distance < closestDistance && unit.isCarryingMinerals() == false
						&& unit.isCarryingGas() == false)) {
					closestWorker = unit;
					closestDistance = distance;
				}
			}
		}

		return closestWorker;
	}

	public void setConstructionWorker(Unit worker, UnitType buildingType) {
		if (worker == null)
			return;

		workerData.setWorkerJob(worker, WorkerData.WorkerJob.Build, buildingType);
	}

	/// buildingPosition 에서 가장 가까운 Move 혹은 Idle 혹은 Mineral 일꾼 유닛들 중에서 Construction
	/// 임무를 수행할 일꾼 유닛을 정해서 리턴합니다<br>
	/// Move / Idle Worker 중에서 먼저 선정하고, 없으면 Mineral Worker 중에서 선정합니다<br>
	/// 일꾼 유닛이 2개 이상이면, avoidWorkerID 에 해당하는 worker 는 선정하지 않도록 합니다<br>
	/// if setJobAsConstructionWorker is true (default), it will be flagged as a
	/// builder unit<br>
	/// if setJobAsConstructionWorker is false, we just want to see which worker
	/// will build a building
	public Unit chooseConstuctionWorkerClosestTo(UnitType buildingType, TilePosition buildingPosition,
			boolean setJobAsConstructionWorker, int avoidWorkerID) {
		// variables to hold the closest worker of each type to the building
		Unit closestMovingWorker = null;
		Unit closestMiningWorker = null;

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 변수 기본값 수정

		double closestMovingWorkerDistance = 1000000000;
		double closestMiningWorkerDistance = 1000000000;

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		// look through each worker that had moved there first
		for (Unit unit : workerData.getWorkers()) {
			if (unit == null)
				continue;

			// worker 가 2개 이상이면, avoidWorkerID 는 피한다
			if (workerData.getWorkers().size() >= 2 && avoidWorkerID != 0 && unit.getID() == avoidWorkerID)
				continue;

			// Move / Idle Worker
			if (unit.isCompleted() && (workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Move
					|| workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Idle)) {
				// if it is a new closest distance, set the pointer
				/*
				 * double distance = unit.getDistance(buildingPosition.toPosition()); if
				 * (closestMovingWorker == null || (distance < closestMovingWorkerDistance &&
				 * unit.isCarryingMinerals() == false && unit.isCarryingGas() == false )) { if
				 * (BWTA.isConnected(unit.getTilePosition(), buildingPosition)) {
				 * closestMovingWorker = unit; closestMovingWorkerDistance = distance; } }
				 */
				// 건물짓기최적일꾼선택 알고리즘 수정 20180729 shsh0823.lee
				double distance = unit.getDistance(buildingPosition.toPosition());

				// 자원 옮기고 있으면 패스
				if (unit.isCarryingMinerals() || unit.isCarryingGas()) {
					continue;
				}

				// 기존 선택된 일꾼이 있을 때 선택된 일꾼의 거리보다 현재 일꾼의 거리가 크거나 같으면 패스
				if (closestMovingWorker != null && closestMovingWorkerDistance <= distance) {
					continue;
				}

				// 위에 걸러지지 않은 케이스에 대해 최적일꾼 변경
				if (BWTA.isConnected(unit.getTilePosition(), buildingPosition)) {
					closestMovingWorker = unit;
					closestMovingWorkerDistance = distance;
				}
			}

			// Move / Idle Worker 가 없을때, 다른 Worker 중에서 차출한다
			if (unit.isCompleted() && (workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Move
					&& workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Idle
					&& workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Build)) {
				// if it is a new closest distance, set the pointer
				/*
				 * double distance = unit.getDistance(buildingPosition.toPosition()); if
				 * (closestMiningWorker == null || (distance < closestMiningWorkerDistance &&
				 * unit.isCarryingMinerals() == false && unit.isCarryingGas() == false )) { if
				 * (BWTA.isConnected(unit.getTilePosition(), buildingPosition)) {
				 * closestMiningWorker = unit; closestMiningWorkerDistance = distance; } }
				 */

				// 건물짓기최적일꾼선택 알고리즘 수정 20180729 shsh0823.lee
				double distance = unit.getDistance(buildingPosition.toPosition());

				// 자원 옮기고 있으면 패스
				if (unit.isCarryingMinerals() || unit.isCarryingGas()) {
					continue;
				}

				// 기존 선택된 일꾼이 있을 때 선택된 일꾼의 거리보다 현재 일꾼의 거리가 크거나 같으면 패스
				if (closestMiningWorker != null && closestMiningWorkerDistance <= distance) {
					continue;
				}

				// 위에 걸러지지 않은 케이스에 대해 최적일꾼 변경
				if (BWTA.isConnected(unit.getTilePosition(), buildingPosition)) {
					closestMiningWorker = unit;
					closestMiningWorkerDistance = distance;
				}
			}
		}

		Unit chosenWorker = closestMovingWorker != null ? closestMovingWorker : closestMiningWorker;

		// if the worker exists (one may not have been found in rare cases)
		if (chosenWorker != null && setJobAsConstructionWorker) {
			workerData.setWorkerJob(chosenWorker, WorkerData.WorkerJob.Build, buildingType);
		}

		return chosenWorker;
	}

	/// Mineral 혹은 Idle 일꾼 유닛들 중에서 Scout 임무를 수행할 일꾼 유닛을 정해서 리턴합니다
	public Unit getScoutWorker() {
		// for each of our workers
		for (Unit worker : workerData.getWorkers()) {
			if (worker == null) {
				continue;
			}
			// if it is a scout worker
			if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Scout) {
				return worker;
			}
		}

		return null;
	}

	// sets a worker as a scout
	public void setScoutWorker(Unit worker) {
		if (worker == null)
			return;

		workerData.setWorkerJob(worker, WorkerData.WorkerJob.Scout, (Unit) null);
	}

	// get a worker which will move to a current location
	public Unit chooseMoveWorkerClosestTo(Position p) {
		// set up the pointer
		Unit closestWorker = null;

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 변수 기본값 수정

		double closestDistance = 1000000000;

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		// for each worker we currently have
		for (Unit unit : workerData.getWorkers()) {
			if (unit == null)
				continue;

			// only consider it if it's a mineral worker
			if (unit.isCompleted() && workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Minerals) {
				// if it is a new closest distance, set the pointer
				double distance = unit.getDistance(p);
				if (closestWorker == null || (distance < closestDistance && unit.isCarryingMinerals() == false
						&& unit.isCarryingGas() == false)) {
					closestWorker = unit;
					closestDistance = distance;
				}
			}
		}

		// return the worker
		return closestWorker;
	}

	/// position 에서 가장 가까운 Mineral 혹은 Idle 일꾼 유닛들 중에서 Move 임무를 수행할 일꾼 유닛을 정해서 리턴합니다
	public void setMoveWorker(Unit worker, int mineralsNeeded, int gasNeeded, Position p) {
		// set up the pointer
		Unit closestWorker = null;

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 변수 기본값 수정

		double closestDistance = 1000000000;

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////

		// for each worker we currently have
		for (Unit unit : workerData.getWorkers()) {
			if (unit == null)
				continue;

			// only consider it if it's a mineral worker or idle worker
			if (unit.isCompleted() && (workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Minerals
					|| workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Idle)) {
				// if it is a new closest distance, set the pointer
				double distance = unit.getDistance(p);
				if (closestWorker == null || distance < closestDistance) {
					closestWorker = unit;
					closestDistance = distance;
				}
			}
		}

		if (closestWorker != null) {
			workerData.setWorkerJob(closestWorker, WorkerData.WorkerJob.Move,
					new WorkerMoveData(mineralsNeeded, gasNeeded, p));
		} else {
			// MyBotModule.Broodwar.printf("Error, no worker found");
		}
	}

	/// 해당 일꾼 유닛으로부터 가장 가까운 적군 유닛을 리턴합니다
	public Unit getClosestEnemyUnitFromWorker(Unit worker) {
		if (worker == null)
			return null;

		Unit closestUnit = null;
		double closestDist = 10000;

		for (Unit unit : MyBotModule.Broodwar.enemy().getUnits()) {
			double dist = unit.getDistance(worker);

			if ((dist < 32 * 8) && (closestUnit == null || (dist < closestDist))) {
				closestUnit = unit;
				closestDist = dist;
			}
		}

		return closestUnit;
	}
	
	public Unit getWeakestEnemyUnitFromWorker(Unit worker) {
		if (worker == null)
			return null;

		Unit closestUnit = null;
		double closestDist = 10000;
		int lowestHP = 1000;
		

		for (Unit unit : MyBotModule.Broodwar.enemy().getUnits()) {
			double dist = unit.getDistance(worker);
			int hp = unit.getHitPoints();

			if ((hp < lowestHP) && (dist < 32 * 8) && (closestUnit == null || (dist < closestDist))) {
				closestUnit = unit;
				closestDist = dist;
			}
		}

		return closestUnit;
	}

	/// 해당 일꾼 유닛에게 Combat 임무를 부여합니다
	public void setCombatWorker(Unit worker) {
		if (worker == null)
			return;

		workerData.setWorkerJob(worker, WorkerData.WorkerJob.Combat, (Unit) null);
	}

	/// 모든 Combat 일꾼 유닛에 대해 임무를 해제합니다
	public void stopCombat() {
		for (Unit worker : workerData.getWorkers()) {
			if (worker == null)
				continue;

			if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat) {
				setMineralWorker(worker);
			}
		}

		// 0703 일단 싹 미네랄로 넣었다가 바꾸자
	}

	public void setRepairWorker(Unit worker, Unit unitToRepair) {
		workerData.setWorkerJob(worker, WorkerData.WorkerJob.Repair, unitToRepair);
	}

	public void stopRepairing(Unit worker) {
		workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
	}

	/// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트합니다
	public void onUnitMorph(Unit unit) {
		if (unit == null)
			return;

		// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
		// 일꾼 탄생/파괴 등에 대한 업데이트 로직 버그 수정

		// onUnitComplete 에서 처리하도록 수정
		// if something morphs into a worker, add it
		// if (unit.getType().isWorker() && unit.getPlayer() ==
		// MyBotModule.Broodwar.self() && unit.getHitPoints() >= 0)
		// {
		// workerData.addWorker(unit);
		// }

		// if something morphs into a building, it was a worker (Zerg Drone)
		if (unit.getType().isBuilding() && unit.getPlayer() == MyBotModule.Broodwar.self()
				&& unit.getPlayer().getRace() == Race.Zerg) {
			// 해당 worker 를 workerData 에서 삭제한다
			workerData.workerDestroyed(unit);
			rebalanceWorkers();
		}

		// BasicBot 1.1 Patch End //////////////////////////////////////////////////
	}

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// 일꾼 탄생/파괴 등에 대한 업데이트 로직 버그 수정 : onUnitShow 가 아니라 onUnitComplete 에서 처리하도록 수정

	// onUnitShow 메소드 제거
	/*
	 * // 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트합니다 public void onUnitShow(Unit unit)
	 * { if (unit == null) return;
	 * 
	 * // add the depot if it exists if (unit.getType().isResourceDepot() &&
	 * unit.getPlayer() == MyBotModule.Broodwar.self()) { workerData.addDepot(unit);
	 * }
	 * 
	 * // add the worker if (unit.getType().isWorker() && unit.getPlayer() ==
	 * MyBotModule.Broodwar.self() && unit.getHitPoints() >= 0) {
	 * workerData.addWorker(unit); }
	 * 
	 * if (unit.getType().isResourceDepot() && unit.getPlayer() ==
	 * MyBotModule.Broodwar.self()) { rebalanceWorkers(); }
	 * 
	 * }
	 */

	// onUnitComplete 메소드 추가

	/// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트합니다
	/// Terran_SCV, Protoss_Probe 유닛 훈련이 끝나서 탄생할 경우,
	/// Zerg_Drone 유닛이 탄생하는 경우,
	/// Zerg_Drone 유닛이 건물로 Morph 가 끝나서 건물이 완성되는 경우,
	/// Zerg_Drone 유닛의 Zerg_Extractor 건물로의 Morph 를 취소시켜서 Zerg_Drone 유닛이 새롭게 탄생하는 경우
	/// 호출됩니다
	public void onUnitComplete(Unit unit) {
		if (unit == null)
			return;

		// ResourceDepot 건물이 신규 생성되면, 자료구조 추가 처리를 한 후, rebalanceWorkers 를 한다
		if (unit.getType().isResourceDepot() && unit.getPlayer() == MyBotModule.Broodwar.self()) {
			List<Unit> depotList = workerData.getDepots();
			
			
			// 단순 유닛생산용 해처리들은 depot에 할당하지 못하도록 수정 shsh0823.lee 2018.08.11
			List<BaseLocation> baseLocationList = InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().selfPlayer);
			int baseLocationSize = baseLocationList.size();
			
			// 각 점령지에서 가장 가까운 해처리의 거리 계산
			double[] minDistances = new double[baseLocationSize]; 
			Unit[] minDistanceUnits = new Unit[baseLocationSize]; 
			Arrays.fill(minDistances, 987654321);
			for(int i = 0; i< baseLocationSize; i++) {
				for(int j = 0; j<depotList.size(); j++) {
					double distance = baseLocationList.get(i).getPosition().getDistance(depotList.get(j).getPosition());
					if(distance<minDistances[i]) {
						minDistances[i] = distance;
						minDistanceUnits[i] = depotList.get(j);
					}
				}
			}
			
			// 현재 지어진 해처리가 기존 점령지의 최소 거리보다 더 가깝다면, 현재 해처리로 인해 점령된 지역이므로 추가
			for(int i = 0; i<baseLocationSize; i++) {
				double distance = baseLocationList.get(i).getPosition().getDistance(unit.getPosition());
				if(distance<minDistances[i]) {
					if(minDistanceUnits[i]!=null) {
						workerData.removeDepot(minDistanceUnits[i]);
					}
					workerData.addDepot(unit);
					rebalanceWorkers();
					break;
				}
			}
			//System.out.println("depotList : " + depotList);
		}

		// 일꾼이 신규 생성되면, 자료구조 추가 처리를 한다.
		if (unit.getType().isWorker() && unit.getPlayer() == MyBotModule.Broodwar.self() && unit.getHitPoints() >= 0) {
			workerData.addWorker(unit);
			rebalanceWorkers();
		}
	}

	// BasicBot 1.1 Patch End //////////////////////////////////////////////////

	// 일하고있는 resource depot 에 충분한 수의 mineral worker 들이 지정되어 있다면, idle 상태로 만든다
	// idle worker 에게 mineral job 을 부여할 때, mineral worker 가 부족한 resource depot 으로
	// 이동하게 된다
	public void rebalanceWorkers() {
		for (Unit worker : workerData.getWorkers()) {
			if (workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Minerals) {
				continue;
			}

			Unit depot = workerData.getWorkerDepot(worker);
			
			if (depot != null && workerData.depotHasEnoughMineralWorkers(depot)) {
				workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
			} else if (depot == null) {
				workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
			}
		}
	}

	// BasicBot 1.1 Patch Start ////////////////////////////////////////////////
	// 일꾼 탄생/파괴 등에 대한 업데이트 로직 버그 수정 및 멀티 기지간 일꾼 숫자 리밸런싱이 잘 일어나도록 수정

	/// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트합니다
	public void onUnitDestroy(Unit unit) {
		if (unit == null)
			return;

		
		
		// ResourceDepot 건물이 파괴되면, 자료구조 삭제 처리를 한 후, 일꾼들을 Idle 상태로 만들어 rebalanceWorkers 한
		// 효과가 나게 한다
		if (unit.getType().isResourceDepot() && unit.getPlayer() == MyBotModule.Broodwar.self()) {
			workerData.removeDepot(unit);
		}

		// 일꾼이 죽으면, 자료구조 삭제 처리를 한 후, rebalanceWorkers 를 한다
		if (unit.getType().isWorker() && unit.getPlayer() == MyBotModule.Broodwar.self()) {
			deadDrone++;
			workerData.workerDestroyed(unit);
			rebalanceWorkers();
		}

		// 미네랄을 다 채취하면 rebalanceWorkers를 한다
		if (unit.getType() == UnitType.Resource_Mineral_Field) {
			rebalanceWorkers();
		}
	}

	// BasicBot 1.1 Patch End //////////////////////////////////////////////////

	public boolean isMineralWorker(Unit worker) {
		if (worker == null)
			return false;

		return workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Minerals
				|| workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Idle;
	}

	public boolean isScoutWorker(Unit worker) {
		if (worker == null)
			return false;

		return (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Scout);
	}

	public boolean isConstructionWorker(Unit worker) {
		if (worker == null)
			return false;

		return (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Build);
	}

	public int getNumMineralWorkers() {
		return workerData.getNumMineralWorkers();
	}

	/// idle 상태인 일꾼 유닛 unit 의 숫자를 리턴합니다
	public int getNumIdleWorkers() {
		return workerData.getNumIdleWorkers();
	}

	public int getNumGasWorkers() {
		return workerData.getNumGasWorkers();
	}

	/// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 리턴합니다
	public WorkerData getWorkerData() {
		return workerData;
	}
}