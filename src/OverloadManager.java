import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;

public class OverloadManager {
	private static class ScoutInfo{
		Position overloadPosition;
		int sumHitPoint;
		int avgAttackPoint;
		@Override
		public String toString() {
			return "ScoutInfo [overloadPosition=" + overloadPosition + ", sumHitPoint=" + sumHitPoint
					+ ", avgAttackPoint=" + avgAttackPoint + "]";
		}
	}

	private static class OverloadInfo {
		Unit overLoad;
		overloadStatus status;
		Position exploreArea;
		Unit followingAttackUnit;

		public OverloadInfo() {
			super();
		}
	}

	public enum overloadStatus {
		wait, /// < 대기
		scout, /// < 정찰
		withAttackUnit, /// < 공격유닛 따라다님
		dropshipWaitingForUnit, /// < 드랍십으로 이용
		dropshipConcentrating, /// < 드랍십으로 이용
		dropshipAttack, /// < 드랍십으로 이용
	};

	private static class WaitingLoadUnitAndOverload {
		Unit unit;
		OverloadInfo overloadInfo;
	}

	private OverloadManager() {

	}

	private static CommandUtil commandUtil = new CommandUtil();

	private static OverloadManager instance = new OverloadManager();

	public static OverloadManager Instance() {
		return instance;
	}

	private static List<OverloadInfo> allOverloadList = new ArrayList<OverloadInfo>();
	private static List<OverloadInfo> waitOverloadList = new ArrayList<OverloadInfo>();
	private static List<OverloadInfo> scoutOverloadList = new ArrayList<OverloadInfo>();
	private static List<OverloadInfo> withAttackUnitOverloadList = new ArrayList<OverloadInfo>();
	private static List<OverloadInfo> dropshipOverloadList = new ArrayList<OverloadInfo>();
	private static List<WaitingLoadUnitAndOverload> waitDropshipUnitList = new ArrayList<WaitingLoadUnitAndOverload>();
	private static BaseLocation baseLocation = InformationManager.Instance()
			.getMainBaseLocation(InformationManager.Instance().selfPlayer);
	// 정찰할 지역 리스트를 넣어둔다.
	private static List<Position> exploreAreaList = new ArrayList<Position>();
		
	// 모임장소
	private static Position gatherPosition = null;
	// 따라다닐 유닛 타입과 넘버
	private Map<UnitType, Integer> withAttackUnitMap = new HashMap<UnitType, Integer>();

	private static Comparator<Position> positionComparator = new Comparator<Position>() {

		@Override
		public int compare(Position o1, Position o2) {
			double distance1 = o1.getDistance(baseLocation.getX(), baseLocation.getY());
			double distance2 = o2.getDistance(baseLocation.getX(), baseLocation.getY());
			if (distance1 > distance2) {
				return -1;
			} else if (distance1 == distance2) {
				return 0;
			} else {
				return 1;
			}
		}
	};
	
	public List<ScoutInfo> getRushInfo(){
		List<ScoutInfo> retList = new ArrayList<ScoutInfo>();
		for(OverloadInfo overloadInfo : scoutOverloadList) {
			int sumHitPoint = 0;
			int avgAttackPoint = 0;
			int cnt = 0;
			for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(overloadInfo.overLoad.getPosition(), 6 * Config.TILE_SIZE)) {
				if (enemy.getPlayer() == StrategyManager.Instance().enemyPlayer) {
					sumHitPoint+=enemy.getHitPoints();
					sumHitPoint+=enemy.getShields();
					avgAttackPoint+=enemy.getType().groundWeapon().damageAmount();
					cnt++;
				}
			}
			ScoutInfo scoutInfo = new ScoutInfo();
			if(cnt!=0) {
				scoutInfo.avgAttackPoint = avgAttackPoint/cnt;
			}
			scoutInfo.sumHitPoint = sumHitPoint;
			scoutInfo.overloadPosition = new Position(overloadInfo.overLoad.getPosition().getX(), overloadInfo.overLoad.getPosition().getY());
			retList.add(scoutInfo);
		}
		Collections.sort(retList, new Comparator<ScoutInfo>() {

			@Override
			public int compare(ScoutInfo o1, ScoutInfo o2) {
				double distance1 = o1.overloadPosition.getDistance(baseLocation.getX(), baseLocation.getY());
				double distance2 = o2.overloadPosition.getDistance(baseLocation.getX(), baseLocation.getY());
				if (distance1 < distance2) {
					return -1;
				} else if (distance1 == distance2) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		//System.out.println(retList);
		//System.out.print("position : ");
		for(Position pos : exploreAreaList) {
			//System.out.print("(" + pos.getX() + "," + pos.getY()+"), ");
		}
		//System.out.println();
		return retList;
	}
	
	
	

	public void addDropshipUnit(Unit unit) {
		WaitingLoadUnitAndOverload waitingLoadUnitAndOverload = new WaitingLoadUnitAndOverload();
		for (OverloadInfo overloadInfo : dropshipOverloadList) {
			if (unit.canLoad(overloadInfo.overLoad)) {
				unit.load(overloadInfo.overLoad);
				waitingLoadUnitAndOverload.overloadInfo = overloadInfo;
				waitingLoadUnitAndOverload.unit = unit;
				waitDropshipUnitList.add(waitingLoadUnitAndOverload);
				return;
			}
		}
		PriorityQueue<OverloadInfo> overloadInfoQueue = new PriorityQueue<>(10, new Comparator<OverloadInfo>() {

			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				double distance1 = unit.getDistance(o1.overLoad.getX(), o1.overLoad.getY());
				double distance2 = unit.getDistance(o2.overLoad.getX(), o2.overLoad.getY());
				if (distance1 < distance2) {
					return -1;
				} else if (distance1 == distance2) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		overloadInfoQueue.addAll(waitOverloadList);
		overloadInfoQueue.addAll(scoutOverloadList);
		overloadInfoQueue.addAll(withAttackUnitOverloadList);

		while (overloadInfoQueue.isEmpty() == false) {
			OverloadInfo overloadInfo = overloadInfoQueue.poll();
			if (unit.canLoad(overloadInfo.overLoad)) {
				unit.load(overloadInfo.overLoad);
				waitingLoadUnitAndOverload.overloadInfo = overloadInfo;
				waitingLoadUnitAndOverload.unit = unit;
				waitDropshipUnitList.add(waitingLoadUnitAndOverload);

				if (overloadInfo.status == overloadStatus.wait) {
					waitOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.scout) {
					scoutOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.withAttackUnit) {
					withAttackUnitOverloadList.remove(overloadInfo);
				}

				overloadInfo.status = overloadStatus.dropshipWaitingForUnit;
				dropshipOverloadList.add(overloadInfo);
				break;
			}
		}
	}

	public boolean canDropshipsGoAttack() {
		if (dropshipOverloadList.size() == 0) {
			return false;
		}
		for (OverloadInfo overloadInfo : dropshipOverloadList) {
			if (overloadInfo.status != overloadStatus.dropshipConcentrating
					&& overloadInfo.status != overloadStatus.dropshipAttack) {
				return false;
			}

			if (overloadInfo.status == overloadStatus.dropshipConcentrating
					&& overloadInfo.overLoad.getDistance(gatherPosition) > 64) {
				return false;
			}
		}
		return true;
	}

	public void goDropships() {
		if (canDropshipsGoAttack() == false) {
			System.out.println("아직 공격 불가입니다.");
			return;
		}
		for (OverloadInfo overloadInfo : dropshipOverloadList) {
			overloadInfo.status = overloadStatus.dropshipAttack;
		}
	}

	public void updateWithCombatUnitNum(UnitType unitType, int num) {
		if (withAttackUnitMap.containsKey(unitType)) {
			if (withAttackUnitMap.get(unitType) != num) {
				withAttackUnitMap.put(unitType, num);
				onUpdate();
			}
		} else {
			withAttackUnitMap.put(unitType, num);
			onUpdate();
		}
	}

	public void addExploreArea(Position position) {
		for (Position explorePosition : exploreAreaList) {
			if (explorePosition.getX() == position.getX() && explorePosition.getY() == position.getY()) {
				return;
			}
		}
		exploreAreaList.add(position);
		Collections.sort(exploreAreaList, positionComparator);
		rebalance();
	}

	public void removeExploreArea(Position position) {
		for (Position explorePosition : exploreAreaList) {
			if (explorePosition.getX() == position.getX() && explorePosition.getY() == position.getY()) {
				exploreAreaList.remove(position);
				Collections.sort(exploreAreaList, positionComparator);
				rebalance();
				break;
			}
		}
	}

	public void addOverload(Unit overload) {
		OverloadInfo overloadInfo = new OverloadInfo();
		overloadInfo.overLoad = overload;
		overloadInfo.status = overloadStatus.wait;
		waitOverloadList.add(overloadInfo);
		allOverloadList.add(overloadInfo);
		rebalance();
	}

	public void removeOverload(Unit overload) {
		for (OverloadInfo overloadInfo : allOverloadList) {
			if (overloadInfo.overLoad.getID() == overload.getID()) {
				if (overloadInfo.status == overloadStatus.wait) {
					waitOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.withAttackUnit) {
					withAttackUnitOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.scout) {
					scoutOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
					dropshipOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.dropshipConcentrating) {
					dropshipOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.dropshipAttack) {
					dropshipOverloadList.remove(overloadInfo);
				} else {
					System.out.println("문제가있어요!!!removeOverload");
				}
				allOverloadList.remove(overloadInfo);
				rebalance();
				return;
			}
		}
		System.out.println("문제가있어요2!!removeOverload");
	}

	public void rebalance() {
		PriorityQueue<OverloadInfo> queue = priorityQueueInit();
		listInit();

		// 1. 뮤탈 따라다니기
		if (withAttackUnitMap.get(UnitType.Zerg_Mutalisk) != null) {
			List<Unit> mutaliskList = StrategyManager.Instance().myMutaliskList;
			if (mutaliskList != null && mutaliskList.size() != 0 && queue.size() != 0) {
				queue = rebalanceWithAttackUnitOverload(withAttackUnitMap.get(UnitType.Zerg_Mutalisk), mutaliskList,
						queue);
			}
		}

		// 2. 히드라 따라다니기
		if (withAttackUnitMap.get(UnitType.Zerg_Hydralisk) != null) {
			List<Unit> hydraliskList = StrategyManager.Instance().myHydraliskList;
			if (hydraliskList != null && hydraliskList.size() != 0 && queue.size() != 0) {
				queue = rebalanceWithAttackUnitOverload(2, hydraliskList, queue);
			}
		}

		// 3. 정찰시키기
		if (exploreAreaList != null && exploreAreaList.size() != 0 && queue.size() != 0) {
			for (Position position : exploreAreaList) {
				queue = rebalanceExploreOverload(position, queue);
				if (queue.size() == 0) {
					break;
				}
			}
		}
		// 4. 남는 오버로드는 대기
		queue = WaitAll(queue);
	}

	public void onUpdate() {

		
		if(gatherPosition==null && StrategyManager.Instance().enemyMainBaseLocation!=null)
		{
			gatherPosition = new Position((UnitControl_COMMON.positionList.get(3).getX()+63*32)/2, (UnitControl_COMMON.positionList.get(3).getY()+63*32)/2);
		}
		
		
		
		
		
		
		
		// 드랍십 관련 update
		for (int i = 0; i < waitDropshipUnitList.size(); i++) {
			WaitingLoadUnitAndOverload waitingLoadUnitAndOverload = waitDropshipUnitList.get(i);
			if (waitingLoadUnitAndOverload.unit.isLoaded()) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
				if (waitingLoadUnitAndOverload.overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
					waitingLoadUnitAndOverload.overloadInfo.status = overloadStatus.dropshipConcentrating;
				}
			} else if (waitingLoadUnitAndOverload.unit
					.canLoad(waitingLoadUnitAndOverload.overloadInfo.overLoad) == false) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
				if (waitingLoadUnitAndOverload.overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
					waitingLoadUnitAndOverload.overloadInfo.status = overloadStatus.dropshipConcentrating;
				}
				addDropshipUnit(waitingLoadUnitAndOverload.unit);
			}
		}

		if (waitDropshipUnitList.size() == 0) {
			for (OverloadInfo overloadInfo : dropshipOverloadList) {
				if (overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
					overloadInfo.status = overloadStatus.dropshipConcentrating;
				}
			}
		}

		// 공격유닛따라다니기 관련 update
		if (MyBotModule.Broodwar.getFrameCount() % 24 == 0 && withAttackUnitOverloadList.size() != 0) {
			Unit underAttackHydralisk = null;
			Unit underAttackMutallisk = null;
			a: for (Unit hydra : StrategyManager.Instance().myHydraliskList) {
				for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(hydra.getPosition(), 6 * Config.TILE_SIZE)) {
					if (enemy.getPlayer() == StrategyManager.Instance().enemyPlayer) {
						underAttackHydralisk = hydra;
						break a;
					}
				}
			}
			a: for (Unit mutal : StrategyManager.Instance().myMutaliskList) {
				for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(mutal.getPosition(), 6 * Config.TILE_SIZE)) {
					if (enemy.getPlayer() == StrategyManager.Instance().enemyPlayer) {
						underAttackMutallisk = mutal;
						break a;
					}
				}
			}

			for (OverloadInfo overloadInfo : withAttackUnitOverloadList) {
				if (overloadInfo.followingAttackUnit.getType() == UnitType.Zerg_Hydralisk) {
					if (underAttackHydralisk != null) {
						overloadInfo.followingAttackUnit = underAttackHydralisk;
					} else if (overloadInfo.followingAttackUnit == null
							|| overloadInfo.followingAttackUnit.exists() == false
							|| overloadInfo.followingAttackUnit.getHitPoints() <= 0) {
						if (StrategyManager.Instance().myHydraliskList.size() > 0) {
							overloadInfo.followingAttackUnit = StrategyManager.Instance().myHydraliskList.get(0);
						}
					}
				} else if (overloadInfo.followingAttackUnit.getType() == UnitType.Zerg_Mutalisk) {
					if (underAttackMutallisk != null) {
						overloadInfo.followingAttackUnit = underAttackMutallisk;
					} else if (overloadInfo.followingAttackUnit == null
							|| overloadInfo.followingAttackUnit.exists() == false
							|| overloadInfo.followingAttackUnit.getHitPoints() <= 0) {
						if (StrategyManager.Instance().myHydraliskList.size() > 0) {
							overloadInfo.followingAttackUnit = StrategyManager.Instance().myMutaliskList.get(0);
						}
					}
				}
			}
		}

		if (MyBotModule.Broodwar.getFrameCount() % 24 != 0) {
			return;
		}

		for (OverloadInfo overloadInfo : allOverloadList) {
			if (overloadInfo.status == overloadStatus.withAttackUnit) {
				if (overloadInfo.overLoad.isUnderAttack()) {
					commandUtil.move(overloadInfo.overLoad, baseLocation.getPosition());
				} else {
					commandUtil.move(overloadInfo.overLoad, overloadInfo.followingAttackUnit.getPosition());
				}
			} else if (overloadInfo.status == overloadStatus.scout) {
				if (overloadInfo.overLoad.isUnderAttack()) {
					commandUtil.move(overloadInfo.overLoad, baseLocation.getPosition());
				} else {
					commandUtil.move(overloadInfo.overLoad, overloadInfo.exploreArea);
				}
			} else if (overloadInfo.status == overloadStatus.wait) {
				commandUtil.move(overloadInfo.overLoad, baseLocation.getPosition());
			} else if (overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
				// 아직 미구현 --> 쓸 일 없음
			} else if (overloadInfo.status == overloadStatus.dropshipConcentrating) {
				commandUtil.move(overloadInfo.overLoad, gatherPosition);  //new Position(63 * 32, 63 * 32));
			} else if (overloadInfo.status == overloadStatus.dropshipAttack) {
				if (overloadInfo.overLoad.getLoadedUnits() == null
						|| overloadInfo.overLoad.getLoadedUnits().size() == 0) {
					dropshipOverloadList.remove(overloadInfo);
					overloadInfo.status = overloadStatus.wait;
					waitOverloadList.add(overloadInfo);
				}

				//System.out.println("overloadInfo.overLoad.getHitPoints() : " + overloadInfo.overLoad.getHitPoints());
				if (overloadInfo.overLoad.getHitPoints() < 50) {
					if (overloadInfo.overLoad.unloadAll(overloadInfo.overLoad.getPosition(), false)) {
						System.out.println("정상적으로 내렸다");
					} else {
						System.out.println("못내림..");
					}
					continue;
				}

				TilePosition enemyBasePosition = StrategyManager.Instance().enemyMainBaseLocation.getTilePosition();
				TilePosition enemyBaseGeysersPosition = StrategyManager.Instance().enemyMainBaseLocation.getGeysers()
						.get(0).getTilePosition();
				TilePosition targetPosition = new TilePosition(
						(enemyBasePosition.getX() + enemyBaseGeysersPosition.getX()) / 2,
						(enemyBasePosition.getY() + enemyBaseGeysersPosition.getY()) / 2);

				if (enemyBaseGeysersPosition.isValid() == false) {
					commandUtil.move(overloadInfo.overLoad, enemyBasePosition.toPosition());
				} else {
					if (overloadInfo.overLoad.getDistance(targetPosition.toPosition()) < 32) {
						if (overloadInfo.overLoad.canUnload()) {
							if (overloadInfo.overLoad.unloadAll(overloadInfo.overLoad.getPosition(), false)) {
								System.out.println("정상적으로 내렸다");
							} else {
								System.out.println("못내림..");
							}
						}
					} else {
						commandUtil.move(overloadInfo.overLoad, targetPosition.toPosition());
					}
				}
			}
		}
	}

	private static PriorityQueue<OverloadInfo> priorityQueueInit() {
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				return o1.overLoad.getID() - o2.overLoad.getID();
			}
		});
		queue.addAll(waitOverloadList);
		queue.addAll(scoutOverloadList);
		queue.addAll(withAttackUnitOverloadList);
		return queue;
	}

	private static PriorityQueue<OverloadInfo> WaitAll(PriorityQueue<OverloadInfo> waitOverloadQueue) {
		while (waitOverloadQueue.isEmpty() == false) {
			OverloadInfo target = waitOverloadQueue.poll();
			target.status = overloadStatus.wait;
			target.exploreArea = baseLocation.getPosition();
			target.followingAttackUnit = null;
			waitOverloadList.add(target);
		}
		return waitOverloadQueue;
	}

	private static PriorityQueue<OverloadInfo> rebalanceExploreOverload(Position position,
			PriorityQueue<OverloadInfo> waitOverloadQueue) {
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				double distance1 = position.getDistance(o1.overLoad.getX(), o1.overLoad.getY());
				double distance2 = position.getDistance(o2.overLoad.getX(), o2.overLoad.getY());
				if (distance1 < distance2) {
					return -1;
				} else if (distance1 == distance2) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		queue.addAll(waitOverloadQueue);
		OverloadInfo overload = queue.poll();
		overload.status = overloadStatus.scout;
		overload.exploreArea = position;
		overload.followingAttackUnit = null;

		scoutOverloadList.add(overload);

		return queue;
	}

	private static void listInit() {
		waitOverloadList.clear();
		scoutOverloadList.clear();
		withAttackUnitOverloadList.clear();
	}

	private static PriorityQueue<OverloadInfo> rebalanceWithAttackUnitOverload(int number, List<Unit> targetList,
			PriorityQueue<OverloadInfo> waitOverloadQueue) {
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				double distance1 = targetList.get(0).getDistance(o1.overLoad.getX(), o1.overLoad.getY());
				double distance2 = targetList.get(0).getDistance(o2.overLoad.getX(), o2.overLoad.getY());
				if (distance1 < distance2) {
					return -1;
				} else if (distance1 == distance2) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		queue.addAll(waitOverloadQueue);
		int cnt = 0;
		while (queue.isEmpty() == false) {
			OverloadInfo overload = queue.poll();
			overload.status = overloadStatus.withAttackUnit;
			overload.exploreArea = null;
			overload.followingAttackUnit = targetList.get(Math.min(cnt, targetList.size() - 1));
			cnt++;

			withAttackUnitOverloadList.add(overload);
			number--;
			if (number == 0) {
				break;
			}
		}
		return queue;
	}

	private static String listTostring(List<OverloadInfo> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("size : " + list.size() + " units : ");
		for (OverloadInfo overloadInfo : list) {
			sb.append(overloadInfo.overLoad.getID() + " ");
		}
		return sb.toString();
	}

	private static String listTostring2(List<WaitingLoadUnitAndOverload> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("size : " + list.size() + " units : ");	
		for (WaitingLoadUnitAndOverload waitingLoadUnitAndOverload : list) {
			sb.append(waitingLoadUnitAndOverload.overloadInfo.overLoad.getID() + "&"
					+ waitingLoadUnitAndOverload.unit.getID() + " ");
		}
		return sb.toString();
	}
}
